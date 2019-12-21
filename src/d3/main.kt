package d3

import commons.*
import java.io.File

private val input by lazy { File("src/d3/input/gmail.in").readText() }

fun main() {
    println("--- Day 3: Crossed Wires ---")
    markTime()
    val (A, B) = input.lines().map(::wireMap)
    val I = A.keys intersect B.keys

    val ans1 = I.map { it.manDist() }.min()!!

    println("Part 1: $ans1")
    printTime()

    markTime()
    val ans2 = I.map { A.getValue(it) + B.getValue(it) }.min()!!
    println("Part 2: $ans2")
    printTime()
}

fun wire(string: String) = sequence {
    var pos = Vec2.ORIGIN
    for (ins in string.splitToSequence(",")) {
        val dir = Dir2.fromChar(ins[0])
        val reps = ins.drop(1).toInt()
        repeat(reps) {
            pos += dir
            yield(pos)
        }
    }
}

fun wireMap(string: String): Map<Vec2, Int> {
    val res = HashMap<Vec2, Int>()

    wire(string).forEachIndexed { index, pos ->
        res.putIfAbsent(pos, index + 1)
    }

    return res
}