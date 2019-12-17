package d16

import commons.*
import java.io.File
import kotlin.math.*

private val input by lazy { File("src/d16/input/gmail.in").readText() }

fun main() {
    println("--- Day 16: Flawed Frequency Transmission ---")

    markTime()
    val n = input.length
    val signal = FFTMatrix(1, n) { _, j -> input[j] - '0' }

    val pattern = List(n) { IntArray(n) }.apply {
        for(j in 0 until n) {
            val seq = patternSequence(j).iterator()
            for(i in 0 until n) {
                this[i][j] = seq.next()
            }
        }
    }.let { FFTMatrix(it) }

    val res1 = signal.iterate(100) { (it * pattern).abs() }.rows[0]
    //for(row in pattern.rows) { println(row.contentToString()) }

    val ans1 = res1.take(8).joinToString("")
    println("Part 1: $ans1")
    printTime()

    markTime()
    // latter half of transformation matrix is a suffix sum
    val offset = (0 until 7).fold(0) { acc, i -> acc * 10 + signal[0,i] }
    require(offset >= n * 5000) { "Simplification failed" }
    val m = n * 10000 - offset
    val signal2 = IntArray(m) { signal[0, (it + offset) % n]}

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

inline class FFTMatrix(val rows: List<IntArray>) {
    val height get() = rows.size
    val width get() = rows[0].size

    operator fun get(r: Int, c: Int) = rows[r][c]

    operator fun times(that: FFTMatrix): FFTMatrix {
        if(width != that.height) throw ArithmeticException("Second matrix's height must equal first matrix's width")

        return FFTMatrix(height, that.width) { r, c ->
            (0 until width).sumBy { k ->
                this[r, k] * that[k, c]
            } % 10
        }
    }

    fun abs() = FFTMatrix(height, width) { i, j ->
        abs(this[i, j])
    }
}

inline fun FFTMatrix(height: Int, width: Int, generator: (r: Int, c: Int) -> Int) =
    FFTMatrix(
        List(height) { r ->
            IntArray(width) { c -> generator(r, c) }
        }
    )