package d17

import commons.*
import d9.IntCodeVM
import java.io.File

private val input by lazy { File("src/d17/input/gmail.in").readText() }

fun main() {
    println("--- Day 17: Set and Forget ---")

    markTime()
    val prog = input.split(',').map { it.toLong() }

    val vm = IntCodeVM(prog)
    vm.mem[0] = 2
    vm.execute()

    val map = vm.output.let { o -> String(CharArray(o.size) { o[it].toChar() }) }
        .lineSequence().filter { it.isNotEmpty() }.toList()

    fun ok(pos: Vec2) = pos.y in map.indices && pos.x in map[pos.y].indices && map[pos.y][pos.x] != '.'

    val intersections = Rect(1 until map[0].length - 1, 1 until map.size - 1).filter { pos ->
        ok(pos) && Dir2.values.all { ok(pos + it) }
    }

    val ans1 = intersections.sumBy { (x, y) -> x * y }
    println("Part 1: $ans1")
    printTime()

    markTime()
    val strat = buildString {
        var pos = Rect(map[0].indices, map.indices).first { (x, y) -> map[y][x] in ">v<^" }
        var dir = Dir2.fromChar(map[pos.y][pos.x])

        while(true) {
            while(ok(pos + dir)) {
                pos += dir
                append('1')
            }
            when {
                ok(pos + dir.left()) -> {
                    append('L')
                    dir--
                }
                ok(pos + dir.right()) -> {
                    append('R')
                    dir++
                }
                else -> return@buildString
            }
        }
    }

    fun compress(strat: String, label: Char = 'A'): CompressResult? {
        val l = strat.indices.find { strat[it] !in "ABC" }
            ?: return if(label == 'D' && strat.length <= 10)
                CompressResult(strat.asIterable().joinToString(","), emptyList())
                else compress(strat, label + 1)?.let{ CompressResult(it.main, listOf("") + it.macros) }

        if(label == 'D') return null

        val r = (l..strat.lastIndex).find {
            strat[it] in "ABC" || stratToMacro(strat.substring(l..it)).length > 20
        } ?: strat.length

        for(i in r-1 downTo l) {
            val sub = strat.substring(l..i)
            val newStrat = strat.replace(sub, label.toString())
            val res = compress(newStrat, label + 1) ?: continue
            return CompressResult(res.main, listOf(stratToMacro(sub)) + res.macros)
        }

        return null
    }

    val (main, macros) = compress(strat) ?: error("Part 2 heuristic failed.")

    vm.input(main)
    for(macro in macros) vm.input(macro)
    vm.input("n")

    vm.execute()

    val ans2 = vm.output.pop()
    println("Part 2: $ans2")
    printTime()

    // println(vm.output.joinToString("") { it.toChar().toString() })
}

data class CompressResult(val main: String, val macros: List<String>)

private fun IntCodeVM.input(string: String) {
    for(char in string) input(char.toLong())
    input('\n'.toLong())
}

private fun stratToMacro(strat: String) = strat.runs().map {
    if(it.item == '1') it.num else it.item
}.joinToString(",")