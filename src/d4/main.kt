package d4

import commons.*
import java.io.File

private val input by lazy { File("src/d4/input/gmail.in").readText() }

fun main() {
    println("--- Day 4: Secure Container ---")
    markTime()
    val range = input.split('-').let { (a, b) -> a.toInt()..b.toInt() }

    fun Int.check() = run {
        val s = toString()
        (0 until 5).any { s[it] == s[it+1] } &&
        (0 until 5).all { s[it] <= s[it+1] }
    }

    val A = range.filter(Int::check)
    val ans1 = A.size
    println("Part 1: $ans1")
    printTime()

    markTime()
    fun Int.check2() = run {
        val s = toString()
        s.runs().any { it.num == 2 }
    }

    val ans2 = A.count(Int::check2)
    println("Part 2: $ans2")
    printTime()
}