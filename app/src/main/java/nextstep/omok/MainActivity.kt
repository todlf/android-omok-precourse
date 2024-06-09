package nextstep.omok

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children

class MainActivity : AppCompatActivity() {

    private var turn = true
    private var boardSize: Int = 15
    private var boardStoneColor = Array(boardSize) { Array<String?>(boardSize) { null } }
    private lateinit var printWin: TextView
    private lateinit var restartButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        printWin = findViewById(R.id.printWin)
        restartButton = findViewById(R.id.restartButton)

        val board = findViewById<TableLayout>(R.id.board)
        board
            .children
            .filterIsInstance<TableRow>()
            .flatMap { it.children }
            .filterIsInstance<ImageView>()
            .forEach { view ->
                view.setOnClickListener {
                    val row = (view.parent as? TableRow)?.indexOfChild(view) ?: -1
                    val column =
                        (view.parent.parent as? TableLayout)?.indexOfChild(view.parent as? TableRow)
                            ?: -1
                    if (stoneColor(row, column) == null) {
                        turn = changeTurn(view)
                        boardStoneColor[row][column] = if (turn) "white" else "black"
                        Log.d("testt", "Stone placed at: ($row, $column)")
                        if (checkWin(row, column, boardStoneColor[row][column]!!)) {
                            Log.d(
                                "testt",
                                "Win detected for ${boardStoneColor[row][column]!!} stones."
                            )
                            revealWin(boardStoneColor[row][column]!!)
                        }
                    }
                }

            }
        restartButton.setOnClickListener {
            restartGame()
        }
    }

    private fun changeTurn(view: ImageView): Boolean {
        if (turn)
            view.setImageResource(R.drawable.black_stone)
        else
            view.setImageResource(R.drawable.white_stone)
        return !turn
    }

    private fun stoneColor(row: Int, column: Int): String? {
        return boardStoneColor[row][column]
    }

    private fun checkWin(row: Int, column: Int, color: String): Boolean {
        val color = boardStoneColor[row][column] ?: return false
        val directions = arrayOf(
            intArrayOf(0, 1), intArrayOf(1, 0), intArrayOf(1, 1), intArrayOf(-1, 1)
        )

        for (now in directions) {
            var count = 1
            val (nowX, nowY) = now
            var nextX = row + nowX
            var nextY = column + nowY

            while (nextX in 0 until boardSize && nextY in 0 until boardSize && boardStoneColor[nextX][nextY] == color) {
                count++
                Log.d("testt", "Counting stone at: ($nextX, $nextY), count: $count")
                nextX += nowX
                nextY += nowY
            }

            nextX = row - nowX
            nextY = column - nowY
            while (nextX in 0 until boardSize && nextY in 0 until boardSize && boardStoneColor[nextX][nextY] == color) {
                count++
                Log.d("testt", "Counting stone at: ($nextX, $nextY), count: $count")
                nextX -= nowX
                nextY -= nowY
            }

            if (count >= 5) return true
        }
        return false
    }

    private fun revealWin(winner: String) {
        val winMessage = if (winner == "black") "흑돌 승리!" else "백돌 승리!"
        printWin.text = winMessage
        printWin.visibility = TextView.VISIBLE
        restartButton.visibility = Button.VISIBLE
    }

    private fun restartGame(){
        for (i in 0 until boardSize) {
            for (j in 0 until boardSize) {
                boardStoneColor[i][j] = null
            }
        }
        val board = findViewById<TableLayout>(R.id.board)
        board
            .children
            .filterIsInstance<TableRow>()
            .flatMap { it.children }
            .filterIsInstance<ImageView>()
            .forEach { view ->
                view.setImageResource(0)
            }
        printWin.visibility = TextView.INVISIBLE
        restartButton.visibility = Button.INVISIBLE

        turn = true
        }
}
