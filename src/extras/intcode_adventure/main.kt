package extras.intcode_adventure

import d9.IntCodeVM
import java.io.File

private val input by lazy { File("src/extras/intcode_adventure/intcode_adventure.in").readText() }

// https://www.reddit.com/r/adventofcode/comments/edl79n/
// author: reddit user sbguest

fun main() {
    val prog = input.split(',').map { it.toLong() }

    val vm = IntCodeVM(prog)
    vm.runAsConsole()
}