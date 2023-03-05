import kotlin.random.Random

fun main(){
  println(generatePredictablePassword(42))
}

fun generatePredictablePassword(seed: Int): String {
    val friendshipRandom = Random(seed)

    var randomPassword = ""
    // write your code here
    repeat(10){
        randomPassword = "$randomPassword${friendshipRandom.nextInt(33,127).toChar()}"
    }
    return randomPassword
}