package com.example.telegramapp

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide

class ChatAdapter(private val messages : List<ChatMessage>) : RecyclerView.Adapter<ViewHolder>() {
    override fun getItemViewType(position: Int): Int {
        return when (val message = messages[position]) {
            is ChatMessage.TextMessage -> if (message.senderType == SenderType.ME) 0 else 1
            is ChatMessage.ImageMessage -> if (message.senderType == SenderType.ME) 2 else 3
            is ChatMessage.PollMessage -> if (message.senderType == SenderType.ME) 4 else 5
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> TextMessageViewHolder(inflater.inflate(R.layout.item_text_message_me, parent, false))
            1 -> TextMessageViewHolder(inflater.inflate(R.layout.item_text_message_other, parent, false))
            2 -> ImageMessageViewHolder(inflater.inflate(R.layout.item_image_message_me, parent, false))
            3 -> ImageMessageViewHolder(inflater.inflate(R.layout.item_image_message_other, parent, false))
            4 -> PollMessageViewHolder(inflater.inflate(R.layout.item_poll, parent, false))
            else -> PollMessageViewHolder(inflater.inflate(R.layout.item_poll, parent, false))
        }

    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (val message = messages[position]) {
            is ChatMessage.TextMessage -> (holder as TextMessageViewHolder).bind(message)
            is ChatMessage.ImageMessage -> (holder as ImageMessageViewHolder).bind(message)
            is ChatMessage.PollMessage -> (holder as PollMessageViewHolder).bind(message)
        }
    }
}

sealed class ChatMessage {
    data class TextMessage(val text: String, val reactions: Map<String, Int>, val senderType: SenderType) : ChatMessage()
    data class ImageMessage(val text: String?, val imageUrl: String, val reactions: Map<String, Int>, val senderType: SenderType) : ChatMessage()
    data class PollMessage(val question: String, val options: List<String>, var votes: MutableMap<String, Int>, val senderType: SenderType) : ChatMessage()
}

enum class SenderType {
    ME, OTHER
}

class TextMessageViewHolder(private val binding: View) : ViewHolder(binding) {
    fun bind(message: ChatMessage.TextMessage) {
        val messageText = binding.findViewById<TextView>(R.id.messageText)
        val reactionsText = binding.findViewById<TextView>(R.id.reactionsText)

        messageText.text = message.text
        reactionsText.text = message.reactions.entries.joinToString(" ") { "${it.key} ${it.value}" }
    }
}

class ImageMessageViewHolder(private val binding: View) : ViewHolder(binding) {
    fun bind(message: ChatMessage.ImageMessage) {
        val messageText = binding.findViewById<TextView>(R.id.messageText)
        val messageImage = binding.findViewById<ImageView>(R.id.messageImage)
        val reactionsText = binding.findViewById<TextView>(R.id.reactionsText)

        messageText.text = message.text ?: ""
        Glide.with(binding.context).load(message.imageUrl).into(messageImage)
        Log.e("URL", message.imageUrl)
        reactionsText.text = message.reactions.entries.joinToString(" ") { "${it.key} ${it.value}" }
    }
}

class PollMessageViewHolder(private val binding: View) : ViewHolder(binding) {

    private var hasVoted = false
    private var selectedOption: String? = null

    fun bind(message: ChatMessage.PollMessage) {
        val pollQuestion = binding.findViewById<TextView>(R.id.pollQuestion)
        val pollOptionsLayout = binding.findViewById<RadioGroup>(R.id.pollOptionsGroup)
        val voteCountTextView = binding.findViewById<TextView>(R.id.voteCount)

        pollQuestion.text = message.question

        voteCountTextView.text = "${message.votes.values.sum()} votes"

        pollOptionsLayout.removeAllViews()

        for (option in message.options) {
            // Create a container for each option (RadioButton + Percentage + ProgressBar)
            val optionContainer = LinearLayout(binding.context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also {
                    it.setMargins(0, 8, 0, 8)
                }
            }

            val radioButton = RadioButton(binding.context).apply {
                text = option
                setTextColor(Color.BLACK)

                setOnClickListener {
                    if (!hasVoted) {
                        message.votes[option] = (message.votes[option] ?: 0) + 1
                        selectedOption = option
                        hasVoted = true

                        voteCountTextView.text = "${message.votes.values.sum()} votes"

                        showResults(message)
                    }
                }
            }
            optionContainer.addView(radioButton)

            val percentageTextView = TextView(binding.context).apply {
                text = "0%"
                visibility = View.GONE
                setPadding(8, 4, 8, 4)
                setTextColor(Color.BLACK)
            }
            optionContainer.addView(percentageTextView)

            val progressBar = ProgressBar(binding.context, null, android.R.attr.progressBarStyleHorizontal).apply {
                visibility = View.GONE // Hidden until the user votes
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also {
                    it.setMargins(8, 4, 8, 8)
                }
                progress = 0
                max = 100
                progressDrawable = binding.context.getDrawable(android.R.drawable.progress_horizontal)
            }
            optionContainer.addView(progressBar)

            pollOptionsLayout.addView(optionContainer)
        }
    }

    private fun showResults(message: ChatMessage.PollMessage) {
        val pollOptionsLayout = binding.findViewById<RadioGroup>(R.id.pollOptionsGroup)
        val totalVotes = message.votes.values.sum().takeIf { it > 0 } ?: 1

        for (i in 0 until pollOptionsLayout.childCount) {
            val optionContainer = pollOptionsLayout.getChildAt(i) as LinearLayout
            val radioButton = optionContainer.getChildAt(0) as RadioButton
            val percentageTextView = optionContainer.getChildAt(1) as TextView
            val progressBar = optionContainer.getChildAt(2) as ProgressBar

            val option = radioButton.text.toString().substringBefore(" (")
            val count = message.votes[option] ?: 0
            val percentage = (count * 100) / totalVotes

            radioButton.text = "$option ($count votes)"

            percentageTextView.text = "$percentage%"
            percentageTextView.visibility = View.VISIBLE

            progressBar.progress = percentage
            progressBar.visibility = View.VISIBLE
        }

        selectedOption?.let { option ->
            for (i in 0 until pollOptionsLayout.childCount) {
                val optionContainer = pollOptionsLayout.getChildAt(i) as LinearLayout
                val radioButton = optionContainer.getChildAt(0) as RadioButton
                if (radioButton.text.toString().startsWith(option)) {
                    radioButton.isChecked = true
                    break
                }
            }
        }
    }
}
