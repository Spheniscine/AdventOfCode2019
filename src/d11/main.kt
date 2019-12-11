package d11

import commons.*
import java.io.File

private val input by lazy { File("src/d11/input/gmail.in").readText() }

fun main() {
    val prog = input.split(',').map { it.toLong() }

    val robot1 = Robot(prog)
    robot1.deploy()

    val ans1 = robot1.allPainted.size
    println("Part 1: $ans1")
    // robot1.display()

    val robot2 = Robot(prog)
    robot2.paint(true)
    robot2.deploy()

    println("Part 2:")
    robot2.display()
}

class Robot(prog: List<Long>) {
    val vm = IntCodeVM(prog)
    var dir = Dir2.Up
    var pos = Vec2.ORIGIN
    val allPainted = HashSet<Vec2>()
    val isWhite = HashSet<Vec2>()

    fun paint(color: Boolean) {
        if(color) isWhite.add(pos) else isWhite.remove(pos)
        allPainted.add(pos)
    }

    fun deploy() {
        do {
            vm.input(pos in isWhite)
            vm.execute()

            paint(vm.output[0] == 1L)
            if(vm.output[1] == 1L) dir++ else dir--
            pos += dir
            vm.output.clear()
        } while (vm.isWaiting)
    }

    fun display() {
        isWhite.display()
    }
}

typealias IntCodeVM = d9.IntCodeVM
fun IntCodeVM.input(boolean: Boolean) = input(boolean.toLong())