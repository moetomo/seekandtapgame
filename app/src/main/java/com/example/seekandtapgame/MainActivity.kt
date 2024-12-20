package com.example.seekandtapgame

import android.graphics.Color
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.SystemClock
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var gridLayout: GridLayout
    private lateinit var startButton: TextView
    private lateinit var scoreTextView: TextView
    private lateinit var targetTitleTextView: TextView
    private lateinit var targetItemsTextView: TextView

    private lateinit var soundPool: SoundPool
    private var matchSoundId: Int = 0
    private var wrongSoundId: Int = 0
    private var startSoundId: Int = 0

    private val itemImages = mutableListOf<Int>()
    private val targetItems = mutableListOf<Int>()
    private val targetItemStates = mutableMapOf<Int, Boolean>()

    private var correctTaps = 0
    private var startTime: Long = 0
    private var gameStartFlag = false

    private val itemNames = mapOf(
        R.drawable.apple to "リンゴ",
        R.drawable.banana to "バナナ",
        R.drawable.grape to "ブドウ",
        R.drawable.orange to "オレンジ",
        R.drawable.lemon to "レモン",
        R.drawable.pear to "洋ナシ",
        R.drawable.strawberry to "イチゴ",
        R.drawable.watermelon to "スイカ",
        R.drawable.cherry to "サクランボ",
        R.drawable.pineapple to "パイナップル"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gridLayout = findViewById(R.id.gridLayout)
        startButton = findViewById(R.id.startButton)
        scoreTextView = findViewById(R.id.scoreTextView)
        targetTitleTextView = findViewById(R.id.targetTitleTextView)
        targetItemsTextView = findViewById(R.id.targetItemsTextView)

        initializeSoundPool()
        loadItemImages()

        startButton.setOnClickListener {
            startGame()
        }
    }

    private fun initializeSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        matchSoundId = soundPool.load(this, R.raw.match_sound, 1)
        wrongSoundId = soundPool.load(this, R.raw.wrong_sound, 1)
        startSoundId = soundPool.load(this, R.raw.start_message, 1)
    }

    private fun loadItemImages() {
        itemImages.addAll(
            listOf(
                R.drawable.apple,
                R.drawable.banana,
                R.drawable.grape,
                R.drawable.orange,
                R.drawable.lemon,
                R.drawable.pear,
                R.drawable.strawberry,
                R.drawable.watermelon,
                R.drawable.cherry,
                R.drawable.pineapple
            )
        )
    }

    private fun startGame() {
        gridLayout.removeAllViews()
        targetItems.clear()
        targetItemStates.clear()
        correctTaps = 0
        startTime = SystemClock.elapsedRealtime()

        val targetCount = (3..4).random()
        targetItems.addAll(itemImages.shuffled().take(targetCount))
        targetItems.forEach { targetItemStates[it] = false }

        updateTargetTitle()
        updateTargetText()

        val shuffledItems = ArrayList(itemImages).shuffled()

        gridLayout.columnCount = 5
        gridLayout.rowCount = 2

        shuffledItems.forEach { imageResource ->
            val tile = HighlightableTile(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 0
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(1, 1, 1, 1)
                }
                setImageResource(imageResource)
                setOnClickListener { handleTileClick(this, imageResource) }
            }
            gridLayout.addView(tile)
        }
        soundPool.play(startSoundId, 1f, 1f, 0, 0, 1f)
        startButton.isEnabled = false
        gameStartFlag = true

        scoreTextView.text = "クリアタイム: 0 秒"
        scoreTextView.setTextColor(Color.BLACK)
    }

    private fun handleTileClick(tile: HighlightableTile, imageResource: Int) {
        if (!gameStartFlag){return}
        if (imageResource in targetItems && !targetItemStates[imageResource]!!) {
            soundPool.play(matchSoundId, 1f, 1f, 0, 0, 1f)
            tile.setHighlighted(true)
            targetItemStates[imageResource] = true
            correctTaps++
        } else {
            soundPool.play(wrongSoundId, 1f, 1f, 0, 0, 1f)
        }

        updateTargetText()

        if (correctTaps == targetItems.size) {
            endGame()
        }
    }

    private fun updateTargetTitle() {
        targetTitleTextView.text = "探し物は ${targetItems.size} 個"
    }

    private fun updateTargetText() {
        val updatedText = targetItems.joinToString("  ") { item ->
            val name = itemNames[item] ?: "不明"
            if (targetItemStates[item] == true) {
                "<font color='blue'>【$name】</font>"
            } else {
                "<font color='red'>【$name】</font>"
            }
        }
        targetItemsTextView.text = android.text.Html.fromHtml(updatedText, android.text.Html.FROM_HTML_MODE_LEGACY)
    }

    private fun endGame() {
        val elapsedTime = SystemClock.elapsedRealtime() - startTime
        scoreTextView.text = "クリアタイム: %.2f 秒".format(elapsedTime / 1000.0)
        scoreTextView.setTextColor(Color.RED)
        startButton.isEnabled = true
        gameStartFlag = false
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }
}
