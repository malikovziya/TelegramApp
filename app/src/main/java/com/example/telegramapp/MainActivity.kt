package com.example.telegramapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.telegramapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val messages = listOf(
            ChatMessage.TextMessage("Hello!", mapOf("👍" to 5, "❤️" to 3), SenderType.ME),
            ChatMessage.ImageMessage("I am the cutiest cat in the world.", "https://images.unsplash.com/photo-1529778873920-4da4926a72c2?fm=jpg&q=60&w=3000&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8Y3V0ZSUyMGNhdHxlbnwwfHwwfHx8MA%3D%3D", mapOf("😍" to 15, "❤️" to 12, "😊" to 8, "🔥" to 2, "😂" to 1), SenderType.OTHER),
            ChatMessage.ImageMessage("Check this out!", "https://images.pexels.com/photos/1591047/pexels-photo-1591047.jpeg", mapOf("🔥" to 10), SenderType.ME),
            ChatMessage.TextMessage("How are you?", mapOf("😊" to 4), SenderType.OTHER),
            ChatMessage.PollMessage("Which framework do you prefer?", listOf("Flutter", "React Native", "SwiftUI"), mutableMapOf("Flutter" to 250, "React Native" to 145, "SwiftUI" to 109), SenderType.OTHER),
            ChatMessage.TextMessage("Good morning!", mapOf("😊" to 6, "☀️" to 3), SenderType.ME),
            ChatMessage.ImageMessage("Sunset view from my window!", "https://images.pexels.com/photos/1591047/pexels-photo-1591047.jpeg", mapOf("😍" to 5, "🌅" to 2), SenderType.OTHER),
            ChatMessage.TextMessage("Any plans for the weekend?", mapOf("🤔" to 4, "🎉" to 3), SenderType.ME),
            ChatMessage.ImageMessage("Just finished my painting!", "https://images.unsplash.com/photo-1738447429433-69e3ecd0bdd0?q=80&w=2670&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", mapOf("🎨" to 7, "👏" to 3), SenderType.OTHER),
            ChatMessage.TextMessage("Let's catch up soon!", mapOf("😊" to 5, "🙌" to 2), SenderType.ME),
        )

        adapter = ChatAdapter(messages)
        binding.recyclerView.layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = true  // Latest messages appear at the bottom
        }
        binding.recyclerView.adapter = adapter
    }
}