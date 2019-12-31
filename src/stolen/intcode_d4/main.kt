package stolen.intcode_d4

import commons.*
import d5.IntCodeVM_d5
import d9.IntCodeVM
import java.io.File

private val input by lazy { File("src/d4/input/gmail.in").readText() }

// https://www.reddit.com/r/adventofcode/comments/e6k648
// author: reddit user 1vader
private val prog by lazy { File("src/stolen/intcode_d4/day4.int").readText().split(',').map { it.toLong() } }

fun main() {
    println("--- Day 4: Secure Container ---")
    markTime()

    val (a, b) = input.split('-').map { it.toInt() }

    val vm = IntCodeVM(prog)
    vm.input(a)
    vm.input(b)
    vm.execute()

    val (ans2, ans1) = vm.output.asReversed()
    println("Part 1: $ans1")
    println("Part 2: $ans2")
    printTime()
}