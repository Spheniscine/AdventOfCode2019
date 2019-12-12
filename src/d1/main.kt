package d1

import commons.*
import java.io.File

private val input by lazy { File("src/d1/input/gmail.in").readText() }

fun main() {
    markTime()
    val A = input.lines().map { it.toInt() }
    val ans1 = A.sumBy { it / 3 - 2 }
    println("Part 1: $ans1")
    printTime()

    markTime()
    val ans2 = A.sumBy {
        var r = 0
        var k = it
        while(true) {
            k = k / 3 - 2
            if(k <= 0) break
            r += k
        }
        r
    }
    println("Part 2: $ans2")
    printTime()
}