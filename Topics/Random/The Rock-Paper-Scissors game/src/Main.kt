import kotlin.random.Random

fun makeDecision(): String {
    return when (Random.nextInt(3) + 1) {
        1 -> "Rock"
        2 -> "Paper"
        else -> "Scissors"
    }
}
