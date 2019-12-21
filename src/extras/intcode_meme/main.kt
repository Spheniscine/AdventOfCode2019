package extras.intcode_meme

import d9.IntCodeVM
import java.io.File

private val input by lazy { File("src/extras/intcode_meme/intcode_meme.in").readText() }

// https://www.reddit.com/r/adventofcode/comments/edgzk4/

fun main() {
    val prog = input.split(',').map { it.toLong() }

    val vm = IntCodeVM(prog)
    vm.execute()
    println(vm.output.let { o -> String(CharArray(o.size) { o[it].toChar() }) })
}