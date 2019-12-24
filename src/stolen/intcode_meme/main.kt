package stolen.intcode_meme

import d9.IntCodeVM
import java.io.File

private val input by lazy { File("src/stolen/intcode_meme/intcode_meme.in").readText() }

// https://www.reddit.com/r/adventofcode/comments/edgzk4/
// author: reddit user RudolphPlays

fun main() {
    val prog = input.split(',').map { it.toLong() }

    val vm = IntCodeVM(prog)
    vm.execute()
    println(vm.outputToAscii())
}