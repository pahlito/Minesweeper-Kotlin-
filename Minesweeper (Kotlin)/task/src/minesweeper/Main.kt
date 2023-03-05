package minesweeper

import kotlin.random.Random

const val MINE_CHAR = "X"
const val SAFE_CHAR = "."
const val MARK_CHAR = "*"
const val FREE_CHAR = "/"

const val DIVISOR = "—│—————————│"
const val HEADER = "\n │123456789│\n$DIVISOR"
const val PLAY_INPUT_SIZE = 3

const val WIDTH = 9
const val HEIGHT = 9

fun main() {
    val mineNumber = getInputMinesNumber()
    val minePositions = randomMinePositions(WIDTH, HEIGHT, mineNumber)
    val mineField = Field(WIDTH, HEIGHT, minePositions)

    startGame(mineField)
    printGoodByeMessage(mineField)
}


private fun getInputMinesNumber(): Int {
    print("How many mines do you want on the field? ")
    return readln().toInt()
}

private fun randomMinePositions(width: Int, height: Int, mineNumber: Int): Set<Int> {
    val set = mutableSetOf<Int>()
    val until = width * height
    while (set.size < mineNumber) {
        set.add(Random.nextInt(until))
    }
    return set
}

private fun startGame(mineField: Field) {
    var playing = true
    paintField(mineField)
    while (playing) {
        playing = play(mineField)
    }
}

private fun paintField(field: Field, solution: Boolean = false) {
    println(HEADER)
    for (y in 0 until field.height) {
        print("${y+1}|")
        for (x in 0 until field.width) {
            print(field.toChar(x, y, solution))
        }
        println("|")
    }
    println(DIVISOR)
}

private fun play(mineField: Field): Boolean {
    val (inputX, inputY, action) = inputActionAndCoordinates()
    val x = inputX.toInt() - 1
    val y = inputY.toInt() - 1
    when (action) {
        "mine" -> markPosition(mineField, x, y)
        "free" -> if(!exploreMine(mineField, x, y)) return false
    }
    return !mineField.isCompleted();
}

private fun inputActionAndCoordinates(): List<String> {
    var input = listOf<String>()
    while(input.size != PLAY_INPUT_SIZE){
        print("Set/unset mines marks or claim a cell as free: ")
        input = readln().split(" ")
        if(input.size != 3) {
            println("Wrong input, try something like 4 2 free.")
        }
    }
    return input
}

private fun markPosition(mineField: Field, x: Int, y: Int) {
    if (!mineField.isExplored(x, y)) {
        mineField.toggleMark(x, y)
        paintField(mineField)
    }
}

private fun exploreMine(mineField: Field, x: Int, y: Int):Boolean {
    return if (mineField.isMine(x, y)){
        paintField(mineField, true)
        false
    } else {
        mineField.setExplored(x, y)
        paintField(mineField)
        true
    }
}

private fun printGoodByeMessage(mineField: Field) {
    if (mineField.isCompleted()) {
        println("Congratulations! You found all the mines!")
    } else {
        println("You stepped on a mine and failed!")
    }
}

data class Field(
    val width: Int,
    val height: Int,
    val minePositions: Set<Int>,
    val markPositions: MutableSet<Int> = mutableSetOf(),
    val exploredPositions: MutableSet<Int> =  mutableSetOf()
){
    val nearRange = 0..7
    val nearXList = listOf(-1,  0,  1, -1, 1, -1, 0, 1)
    val nearYList = listOf(-1, -1, -1,  0, 0,  1, 1, 1)
}

fun Field.getPosition(x: Int, y: Int) = x + y * this.width

fun Field.isMine(x: Int, y: Int) = x >= 0 && y >= 0 && x < this.width && y < this.height
        && this.minePositions.contains(this.getPosition(x, y))

fun Field.countAround(x: Int, y: Int): Int {
    var cont = 0
    for (i in nearRange) {
        cont += if (this.isMine(x + nearXList[i], y + nearYList[i])) 1 else 0
    }
    return cont
}

fun Field.toggleMark(x: Int, y: Int) {
    val i = getPosition(x, y)
    if (this.markPositions.contains(i)) {
        this.markPositions.remove(i)
    } else {
        this.markPositions.add(i)
    }
}

fun Field.isMarked(x: Int, y: Int) = this.markPositions.contains(this.getPosition(x, y))

fun Field.setExplored(x: Int, y: Int) {
    val i = getPosition(x, y)
    this.markPositions.remove(i)
    this.exploredPositions.add(i)
    for (i in nearRange) exploreNearRange(x + nearXList[i], y + nearYList[i])

}

fun Field.exploreNearRange(x: Int, y: Int) {
    if(x < 0 || y < 0 || x >= this.width || y >= this.height){
        return
    }
    if (!this.isMine(x, y)) {
        if (!this.isExplored(x, y) && this.countAround(x, y) == 0) {
            this.setExplored(x, y)
        } else {
            val i = getPosition(x, y)
            this.markPositions.remove(i)
            this.exploredPositions.add(i)
        }
    }
}

fun Field.isExplored(x: Int, y: Int) = this.exploredPositions.contains(getPosition(x, y))

fun Field.toChar(x: Int, y: Int, solution: Boolean): String = when {
    this.isMarked(x, y) -> if(solution && this.isMine(x, y)) MINE_CHAR else MARK_CHAR
    this.isMine(x, y) -> if(solution) MINE_CHAR else SAFE_CHAR
    this.isExplored(x, y) -> {
        val minesAround = this.countAround(x, y)
        if (minesAround == 0) {
            FREE_CHAR
        } else "$minesAround"
    }
    else -> {
        SAFE_CHAR
    }
}

fun Field.isCompleted(): Boolean =
    this.minePositions.size + this.exploredPositions.size == this.width * this.height



