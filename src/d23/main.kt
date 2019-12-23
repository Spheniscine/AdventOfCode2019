package d23

import commons.*
import d9.IntCodeVM
import java.io.File

private val input by lazy { File("src/d23/input/gmail.in").readText() }

fun main() {
    println("--- Day 23: Category Six ---")

    markTime()
    val prog = input.split(',').map { it.toLong() }

    var ans1 = 0L
    val ans2 = run {
        val nics = List(50) { i ->
            IntCodeVM(prog).also { it.input(i) }
        }

        var natx = 0L
        var naty = 0L
        var natActive = false
        val natHist = LongHashSet()

        while(true) {
            for (nic in nics) nic.execute()
            val hasInput = BooleanArray(nics.size)
            for (nic in nics) {
                for((il, x, y) in nic.output.chunked(3)) {
                    val i = il.toInt()
                    if(i == 255) {
                        if(!natActive) ans1 = y
                        natx = x
                        naty = y
                        natActive = true
                    } else {
                        nics[i].input(x)
                        nics[i].input(y)
                        hasInput[i] = true
                    }
                }
                nic.output.clear()
            }
            for (i in nics.indices) {
                if(!hasInput[i]) nics[i].input(-1)
            }
            if(natActive && hasInput.none { it }) {
                if(natHist.add(naty).not()) return@run naty
                nics[0].input(natx)
                nics[0].input(naty)
            }
        }
        @Suppress("UNREACHABLE_CODE") error("Unreachable")
    }
    println("Part 1: $ans1")
    println("Part 2: $ans2")
    printTime()
}