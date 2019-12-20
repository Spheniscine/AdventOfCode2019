package d19

import commons.*
import d9.IntCodeVM
import java.io.File

private val input by lazy { File("src/d19/input/gmail.in").readText() }

fun main() {
    println("--- Day 19: Tractor Beam ---")

    markTime()
    val prog = input.split(',').map { it.toLong() }

    fun grid(x: Int, y: Int): Boolean {
        val vm = IntCodeVM(prog)
        vm.input(x)
        vm.input(y)
        vm.execute()
        return vm.output[0] == 1L
    }

    fun grid(pos: Vec2) = grid(pos.x, pos.y)

    var lastTrue = Vec2.ORIGIN

    val ans1 = Rect(0 until 50, 0 until 50).count { pos -> grid(pos).also { if(it) lastTrue = pos } }
    println("Part 1: $ans1")
    printTime()

    //displayGrid(50, 50) { x, y -> grid(x, y) }

    markTime()
    var (x, y) = lastTrue
    while(grid(x + 1, y)) x++
    while(true) {
        if(x >= 99 && grid(x - 99, y + 99)) {
            x -= 99
            while(grid(x - 1, y + 99)) x--
            break
        }
        y++
        while(grid(x + 1, y)) x++
    }

    val ans2 = x * 10000 + y
    println("Part 2: $ans2")
    printTime()
}
