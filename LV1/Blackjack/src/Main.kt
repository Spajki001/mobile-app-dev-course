fun main() {
    val deck = createDeck()
    val playerHand = mutableListOf<Card>()
    val dealerHand = mutableListOf<Card>()

    // Deal the first cards
    dealCard(dealerHand, deck)
    dealCard(playerHand, deck)
    dealCard(playerHand, deck)

    // Check for player blackjack
    val playerBlackjack = checkBlackjack(playerHand)

    // Start the game
    if (playerBlackjack) {
        println("Blackjack! You might win.")
        dealCard(dealerHand, deck)
        println("Dealer hand: ${dealerHand.joinToString(", ")} >> Score: ${calculateHandValue(dealerHand)}")
        println("Your hand: ${playerHand.joinToString(", ")} >> Score: ${calculateHandValue(playerHand)}")
    } else {
        playerTurn(playerHand, deck, dealerHand)
    }

    // Check for dealer blackjack after the first hit
    val dealerBlackjack = checkBlackjack(dealerHand)

    // Determine the winner based on Blackjack
    if (dealerBlackjack && playerBlackjack) {
        println("Push! (both sides have Blackjack)")
    } else if (dealerBlackjack) {
        dealCard(dealerHand, deck)
        println("\nDealer hand: ${dealerHand.joinToString(", ")} >> Score: ${calculateHandValue(dealerHand)}")
        println("Your hand: ${playerHand.joinToString(", ")} >> Score: ${calculateHandValue(playerHand)}")
        println("You lose! (dealer Blackjack)")
    } else if (playerBlackjack) {
        println("You win! (Blackjack)")
    } else {
        if (calculateHandValue(playerHand) > 21) {
            return // Player's turn ends
        } else {
        dealerTurn(dealerHand, deck)
        determineWinner(playerHand, dealerHand)
        }
    }
}

data class Card(val suit: String, val value: String) {
    override fun toString(): String {
        return "$suit $value"
    }

    fun getScore(): Int {
        if (value == "A") {
            return 11
        } else if (value in listOf("J", "Q", "K")) {
            return 10
        } else {
            return value.toInt()
        }
    }
}

fun checkBlackjack(hand: MutableList<Card>): Boolean {
    return calculateHandValue(hand) == 21 && hand.size == 2
}

fun createDeck(): MutableList<Card> {
    val suits = listOf("Hearts", "Diamonds", "Spades", "Clubs")
    val values = listOf("2", "3", "4", "5", "6", "7", "8", "9", "J", "Q", "K", "A")
    val deck = mutableListOf<Card>()

    for (suit in suits) {
        for (value in values) {
            deck.add(Card(suit, value))
        }
    }

    deck.shuffle()
    return deck
}

fun dealCard(hand: MutableList<Card>, deck: MutableList<Card>) {
    val card = deck.removeFirst()
    hand.add(card)
}

fun calculateHandValue(hand: MutableList<Card>): Int {
    var totalScore = hand.sumOf { it.getScore() }

    // Ace handling for values over 21
    if (totalScore > 21 && hand.any { it.value == "A" }) {
        totalScore -= 10
    }

    return totalScore
}

fun playerTurn(playerHand: MutableList<Card>, deck: MutableList<Card>, dealerHand: MutableList<Card>) {
    println("Dealer hand: ${dealerHand.joinToString(", ")} >> Score: ${calculateHandValue(dealerHand)}")
    println("Your hand: ${playerHand.joinToString(", ")} >> Score: ${calculateHandValue(playerHand)}")

    while (true) {
        print("Hit or Stand? (h/s): ")
        val choice = readLine()?.lowercase() ?: ""

        if (choice == "h") {
            dealCard(playerHand, deck)
            println("You drew a card. Your new hand: ${playerHand.joinToString(", ")} >> Score: ${calculateHandValue(playerHand)}")
            if (calculateHandValue(playerHand) > 21) {
                println("You lose! (bust)")
                return // Player's turn ends
            }
        } else if (choice == "s") {
            break // Player stands
        } else {
            println("Invalid input. Please enter 'h' or 's'!")
        }
    }
}

fun dealerTurn(dealerHand: MutableList<Card>, deck: MutableList<Card>) {
    //checkBlackjackDealer(dealerHand)
    while (calculateHandValue(dealerHand) < 17) {
        dealCard(dealerHand, deck)
        println("Dealer draws a card.")
    }
    println("Dealer's final hand: ${dealerHand.joinToString(", ")} >> Score: ${calculateHandValue(dealerHand)}")
}

fun determineWinner(playerHand: MutableList<Card>, dealerHand: MutableList<Card>) {
    val playerScore = calculateHandValue(playerHand)
    val dealerScore = calculateHandValue(dealerHand)

    if (dealerScore > 21) {
        println("You win! (dealer bust)")
    } else if (playerScore > dealerScore) {
        println("You win!")
    } else if (playerScore < dealerScore) {
        println("You lose!")
    } else {
        println("Push!")
    }
}