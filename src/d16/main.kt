package d16

import commons.*
import java.io.File
import kotlin.math.*

private val input by lazy { File("src/d16/input/gmail.in").readText() }

fun main() {
    println("--- Day 16: Flawed Frequency Transmission ---")

    markTime()
    val n = input.length
    val signal = IntArray(n) { j -> input[j] - '0' }

    val pattern = List(n) { IntArray(n) }.apply {
        for(j in 0 until n) {
            val seq = patternSequence(j).iterator()
            for(i in 0 until n) {
                this[i][j] = seq.next()
            }
        }
    }

    val res1 = signal.iterate(100) {
        IntArray(it.size) { j ->
            abs(it.indices.sumBy { k ->
                it[k] * pattern[k][j]
            } % 10)
        }
    }
    //for(row in pattern.rows) { println(row.contentToString()) }

    val ans1 = String(CharArray(8) { '0' + res1[it] })
    println("Part 1: $ans1")
    printTime()

    markTime()
    // latter half of transformation is a suffix sum
    val offset = (0 until 7).fold(0) { acc, i -> acc * 10 + signal[i] }
    require(offset >= n * 5000) { "Part 2 heuristic failed." }
    val signal2 = IntArray(n * 10000 - offset) { signal[(it + offset) % n] }

    val res2 = signal2.iterate(100) {
        val new = IntArray(it.size)
        new[it.lastIndex] = it.last()
        for(j in it.lastIndex - 1 downTo 0) {
            new[j] = (new[j+1] + it[j]) % 10
        }
        new
    }

    val ans2 = res2.take(8).joinToString("")
    println("Part 2: $ans2")
    printTime()
}

fun patternSequence(j: Int) = sequence {
    while(true) {
        repeat(j+1) { yield(0) }
        repeat(j+1) { yield(1) }
        repeat(j+1) { yield(0) }
        repeat(j+1) { yield(-1) }
    }
}.drop(1)