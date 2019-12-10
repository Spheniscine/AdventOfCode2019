package d8

import commons.*
import java.io.File

private val input by lazy { File("src/d8/input/gmail.in").readText() }

const val W = 25
const val H = 6

fun main() {
    val layers = input.chunked(W * H)

    val ans1 = layers.minBy { it.count('0') }!!.let { it.count('1') * it.count('2') }
    println("Part 1: $ans1")

    println("Part 2:")
    for (r in 0 until H) {
        val line = CharArray(W) { c ->
            val i = r * W + c
            when(layers.find { it[i] != '2' }?.get(i) ?: '2') {
                '0' -> '.'
                '1' -> '#'
                else -> '?'
            }
        }
        println(String(line))
    }
}