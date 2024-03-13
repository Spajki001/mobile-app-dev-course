package com.example.inspired
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Make sure this matches your layout file
        val imageTuring = findViewById<ImageView>(R.id.imageView7)
        val imageHamilton = findViewById<ImageView>(R.id.imageView6)
        val imageTorvalds = findViewById<ImageView>(R.id.imageTorvalds)
        imageTuring.setOnClickListener {
            Toast.makeText(
                this@MainActivity,
                "Machines take me by surprise with great frequency.",
                Toast.LENGTH_SHORT
            ).show()
        }
        imageHamilton.setOnClickListener {
            Toast.makeText(
                this@MainActivity,
                "When something went wrong, we just got on with it.",
                Toast.LENGTH_SHORT
            ).show()
        }
        imageTorvalds.setOnClickListener {
            Toast.makeText(
                this@MainActivity,
                "Talk is cheap. Show me the code.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}