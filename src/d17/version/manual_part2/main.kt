package d17.version.manual_part2

import commons.*
import d9.IntCodeVM
import java.io.File

private val input by lazy { File("src/d17/input/gmail.in").readText() }

fun main() {
    println("--- Day 17: Set and Forget ---")

    val prog = input.split(',').map { it.toLong() }

    val vm = IntCodeVM(prog)
    vm.mem[0] = 2
    vm.execute()

    val map = vm.output.let { o -> String(CharArray(o.size) { o[it].toChar() }) }.lines().filter { it.isNotEmpty() }

    fun ok(pos: Vec2) = pos.y in map.indices && pos.x in map[pos.y].indices && map[pos.y][pos.x] != '.'

    val intersections = Rect(1 until map[0].length - 1, 1 until map.size - 1).filter { pos ->
        ok(pos) && Dir2.values.all { ok(pos + it) }
    }

    val ans1 = intersections.sumBy { (x, y) -> x * y }
    println("Part 1: $ans1")

    // buildStrat(map)

    // manually solved and hardcoded
    val A = "L,12,L,10,R,8,L,12"
    val B = "R,8,R,10,R,12"
    val C = "L,10,R,12,R,8"
    val main = "A,B,A,B,C,C,B,A,B,C"

    vm.input(main)
    vm.input(A)
    vm.input(B)
    vm.input(C)
    vm.input("n")

    vm.execute()

    val ans2 = vm.output.pop()
    println("Part 2: $ans2")

    // println(vm.output.joinToString("") { it.toChar().toString() })
}

private fun IntCodeVM.input(string: String) {
    for(char in string) input(char.toLong())
    input('\n'.toLong())
}

fun buildStrat(map: List<String>) {
    fun ok(pos: Vec2) = pos.y in map.indices && pos.x in map[pos.y].indices && map[pos.y][pos.x] != '.'

    var pos = Rect(map[0].indices, map.indices).first { (x, y) -> map[y][x] in ">v<^" }
    var dir = Dir2.fromChar(map[pos.y][pos.x])

    val strat = mutableListOf<String>()

    buildStrat@ while(true) {
        var cnt = 0
        while(ok(pos + dir)) {
            pos += dir
            cnt++
        }
        if(cnt != 0) strat.add(cnt.toString())
        when {
            ok(pos + dir.left()) -> {
                strat.add("L")
                dir--
            }
            ok(pos + dir.right()) -> {
                strat.add("R")
                dir++
            }
            else -> break@buildStrat
        }
    }

    println(strat.joinToString(","))
}