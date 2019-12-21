package d21

import commons.*
import d9.IntCodeVM
import java.io.File

private val input by lazy { File("src/d21/input/gmail.in").readText() }
private val script1 by lazy { File("src/d21/res/part1.spr").readText() }
private val script2 by lazy { File("src/d21/res/part2.spr").readText() }

fun main() {
    println("--- Day 21: Springdroid Adventure ---")

    markTime()
    val prog = input.split(',').map { it.toLong() }

    val vm1 = IntCodeVM(prog)
    vm1.inputAscii(script1)
    vm1.execute()

    val ans1 = vm1.output.last()
    if(ans1 < 128) println(vm1.outputToAscii())
    else println("Part 1: $ans1")
    printTime()

    markTime()
    val vm2 = IntCodeVM(prog)
    vm2.inputAscii(script2)
    vm2.execute()

    val ans2 = vm2.output.last()
    if(ans2 < 128) println(vm2.outputToAscii())
    else println("Part 2: $ans2")
    printTime()
}