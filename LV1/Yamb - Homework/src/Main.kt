data class Dice(val values: Array<Int>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Dice

        return values.contentEquals(other.values)
    }

    override fun hashCode(): Int {
        return values.contentHashCode()
    }
}

data class ScoreSheet(
    var ones: Int? = null,
    var twos: Int? = null,
    var threes: Int? = null,
    var fours: Int? = null,
    var fives: Int? = null,
    var sixes: Int? = null,
    var threeOfAKind: Int? = null,
    var fourOfAKind: Int? = null,
    var fullHouse: Int? = null,
    var smallStraight: Int? = null,
    var largeStraight: Int? = null,
    var yamb: Int? = null
)

fun rollDice(): Dice {
    // Generate six random dice values (1-6) and store in a list
    return Dice(Array(6) { (1..6).random() })
}

fun reRollDice(dice: Dice, indicesToReroll: List<Int>): Dice {
    // Modify the specified indices of the dice with new random values
    val newValues = dice.values.toMutableList()
    for (index in indicesToReroll) {
        newValues[index] = (1..6).random()
    }
    return Dice(newValues.toTypedArray())
}

fun calculateScore(dice: Dice, category: String): Int {
    return when (category) {
        "ones" -> dice.values.count { it == 1 }
        "twos" -> dice.values.count { it == 2 } * 2
        "threes" -> dice.values.count { it == 3 } * 3
        "fours" -> dice.values.count { it == 4 } * 4
        "fives" -> dice.values.count { it == 5 } * 5
        "sixes" -> dice.values.count { it == 6 } * 6
        "threeOfAKind", "fourOfAKind" -> if (dice.values.groupingBy { it }.eachCount().any { it.value >= category.length }) dice.values.sum() else 0
        "fullHouse" -> if (dice.values.groupingBy { it }.eachCount().filter { it.value >= 2 }.size == 2) 25 else 0
        "smallStraight" -> if (dice.values.distinct().size >= 4 && (1..4).any { it in dice.values }) 30 else 0
        "largeStraight" -> if (dice.values.distinct().size >= 5 && (1..5).any { it in dice.values }) 40 else 0
        "yamb" -> if (dice.values.distinct().size == 1) 50 else 0
        else -> throw IllegalArgumentException("Invalid category")
    }
}

fun scoreboard(scoreSheet: ScoreSheet) {
    println("Score Sheet:")
    println("Ones: ${scoreSheet.ones}")
    println("Twos: ${scoreSheet.twos}")
    println("Threes: ${scoreSheet.threes}")
    println("Fours: ${scoreSheet.fours}")
    println("Fives: ${scoreSheet.fives}")
    println("Sixes: ${scoreSheet.sixes}")
    println("Three of a Kind: ${scoreSheet.threeOfAKind}")
    println("Four of a Kind: ${scoreSheet.fourOfAKind}")
    println("Full House: ${scoreSheet.fullHouse}")
    println("Small Straight: ${scoreSheet.smallStraight}")
    println("Large Straight: ${scoreSheet.largeStraight}")
    println("Yamb: ${scoreSheet.yamb}")
}

fun chooseCategory(scoreSheet: ScoreSheet, category: String, dice: Dice) {
    val field = scoreSheet.javaClass.getDeclaredField(category)
    field.isAccessible = true // Make the field accessible

    if (field.get(scoreSheet) != null) {
        throw IllegalArgumentException("Category already filled")
    }
    field.set(scoreSheet, calculateScore(dice, category))
}

data class Player(val name: String, val scoreSheet: ScoreSheet = ScoreSheet())

fun computerTurn(computer: Player,dice: Dice) {
    println("${computer.name}'s turn")

    var currentDice = dice
    for (i in 0..1) {  // Allow computer two re-rolls
        currentDice = reRollDice(currentDice, currentDice.values.indices.filter { (1..3).random() > 1 }) // Randomly re-roll about half the dice
    }

    var chosenCategory = findBestAvailableCategory(computer.scoreSheet, currentDice)
    while (chosenCategory == null) {
        // If no good category, pick a random one
        chosenCategory = computer.scoreSheet.javaClass.declaredFields.filter { it.get(computer.scoreSheet) == null }.random().name
    }

    chooseCategory(computer.scoreSheet, chosenCategory, dice)
    println("Computer chose: $chosenCategory")
}

fun findBestAvailableCategory(scoreSheet: ScoreSheet, dice: Dice): String? {
    // Prioritize categories not yet scored
    val availableCategories = scoreSheet.javaClass.declaredFields.filter {
        it.isAccessible = true
        it.get(scoreSheet) == null
    }.map { it.name }

    // Potential scores for each category (can be refined)
    val categoryScores = mapOf(
        "ones" to dice.values.count { it == 1 },
        "twos" to dice.values.count { it == 2 } * 2,
        "threes" to dice.values.count { it == 3 } * 3,
        "fours" to dice.values.count { it == 4 } * 4,
        "fives" to dice.values.count { it == 5 } * 5,
        "sixes" to dice.values.count { it == 6 } * 6,
        "threeOfAKind" to if (dice.values.groupingBy { it }.eachCount().any { it.value >= 3 }) dice.values.sum() else 0,
        "fourOfAKind" to if (dice.values.groupingBy { it }.eachCount().any { it.value >= 4 }) dice.values.sum() else 0,
        "fullHouse" to if (dice.values.groupingBy { it }.eachCount().filter { it.value >= 2 }.size == 2) 25 else 0,
        "smallStraight" to if (dice.values.distinct().size >= 4 && (1..4).any { it in dice.values }) 30 else 0,
        "largeStraight" to if (dice.values.distinct().size >= 5 && (1..5).any { it in dice.values }) 40 else 0,
        "yamb" to if (dice.values.distinct().size == 1) 50 else 0
    )

    // Return the available category with the highest potential score
    return availableCategories.maxByOrNull { categoryScores[it] ?: 0 }
}

fun calculateTotalScore(scoreSheet: ScoreSheet): Int {
    return scoreSheet.javaClass.declaredFields.sumOf { it.getInt(scoreSheet) }
}

fun main() {
    println("Enter your name: ")
    val playerName = readlnOrNull() ?: "Player 1"

    val player = Player(playerName)
    val computer = Player("Computer")

    val maxRerolls = 3  // Maximum re-rolls per turn
    val totalTurns = 13 // Number of turns matches the number of scoring categories
    val scoreSheet = ScoreSheet()

    for (turn in 1..totalTurns) {
        var gameDice = rollDice()
        println("Your turn")
        println("Turn $turn")
        var rerollsRemaining = maxRerolls

        while (rerollsRemaining > 0) {
            println("Dice: ")
            gameDice.values.forEach { print("$it ") }
            println()

            val keepOrReroll = readlnOrNull()?.lowercase() ?: "k"
            if (keepOrReroll == "k") {
                break
            } else if (keepOrReroll == "r") {
                println("Enter the indices of the dice to reroll (1-6), separated by spaces:")
                val rerollIndices: List<Int> = readlnOrNull()?.split(" ")?.mapNotNull { it.toIntOrNull()?.minus(1) } ?: emptyList()

                if (rerollIndices.any { it !in 0..5 }) {
                    println("Invalid dice indices. Please enter numbers between 1 and 6")
                } else {
                    gameDice = reRollDice(gameDice, rerollIndices)
                    rerollsRemaining -= 1
                }
            } else if (keepOrReroll == "s") {
                scoreboard(scoreSheet)
            } else {
                println("Invalid input. Please enter 'k' to keep the dice, 'r' to reroll or 's' for a current scoreboard.")
            }
        }
        println("Dice: ")
        gameDice.values.forEach { print("$it ") }
        println()

        // Choose Category
        var validCategory = false
        while (!validCategory) {
            println("Available categories:")
            scoreSheet.javaClass.declaredFields.forEach { field ->
                field.isAccessible = true
                if (field.get(scoreSheet) == null) {
                    println(field.name) // Print the category name if it's not yet filled
                }
            }
            println("Enter the category name to score:")
            val category = readlnOrNull()?.lowercase() ?: ""
            try {
                chooseCategory(scoreSheet, category, gameDice)
                validCategory = true
            } catch (e: IllegalArgumentException) {
                println(e.message)
            }
        }

        computerTurn(computer, gameDice) // Reset dice for the next turn
    }

    // Game Over - Determine Winner
    val playerScore = calculateTotalScore(player.scoreSheet)
    val computerScore = calculateTotalScore(computer.scoreSheet)

    println("Final Results:")
    println("$playerName Score: $playerScore")
    println("${computer.name} Score: $computerScore")

    if (playerScore > computerScore) {
        println("You Win!")
    } else if (computerScore > playerScore) {
        println("Computer Wins!")
    } else {
        println("It's a Tie!")
    }
}

