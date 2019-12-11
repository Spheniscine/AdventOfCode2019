package d5

import java.io.File

private val input by lazy { File("src/d5/input/gmail.in").readText() }

fun main() {
    val prog = input.split(',').map { it.toLong() }

    val vm1 = IntCodeVM(prog)
    vm1.input(1)
    vm1.execute()
    val ans1 = vm1.output.last()
    println("Part 1: $ans1")

    val vm2 = IntCodeVM(prog)
    vm2.input(5)
    vm2.execute()
    val ans2 = vm2.output.single()
    println("Part 2: $ans2")
}