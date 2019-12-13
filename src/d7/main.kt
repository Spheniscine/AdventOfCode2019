package d7

import commons.*
import d5.IntCodeVM_d5
import java.io.File

private val input by lazy { File("src/d7/input/gmail.in").readText() }

fun main() {
    println("--- Day 7: Amplification Circuit ---")
    markTime()
    val prog = input.split(',').map { it.toLong() }

    fun IntRange.solve() = permutations().map { P ->
        val amps = P.map { p ->
            IntCodeVM_d5(prog).also { it.input(p) }
        }

        var sig = 0L
        do {
            for (amp in amps) {
                amp.input(sig)
                amp.execute()
                sig = amp.output.pop()
            }
        } while (amps[0].isWaiting)

        sig
    }.max()!!

    val ans1 = (0..4).solve()
    println("Part 1: $ans1")
    printTime()

    markTime()
    val ans2 = (5..9).solve()
    println("Part 2: $ans2")
    printTime()
}