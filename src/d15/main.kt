package d15

import commons.*
import d9.IntCodeVM
import java.io.File
import java.util.ArrayDeque
import java.util.EnumMap

private val input by lazy { File("src/d15/input/gmail.in").readText() }

fun main() {
    println("--- Day 15: Oxygen System ---")

    markTime()
    val prog = input.split(',').map { it.toLong() }

    val droid = Droid(prog)

    val (ans1, ans2) = droid.solve()
    println("Part 1: $ans1")
    println("Part 2: $ans2")
    printTime()
    // droid.display()
}

class Droid(val prog: List<Long>) {
    val moves: Map<Dir2, Int> = EnumMap<Dir2, Int>(Dir2::class.java).apply {
        put(Dir2.North, 1)
        put(Dir2.South, 2)
        put(Dir2.West, 3)
        put(Dir2.East, 4)
    }

    val map = HashMap<Vec2, Boolean>().apply {
        put(Vec2.ORIGIN, true)
    }

    var oxygen: Vec2? = null

    val vm = IntCodeVM(prog)
    var pos = Vec2.ORIGIN

    fun move(dir: Dir2): Int {
        vm.input(moves.getValue(dir))
        vm.execute()
        val status = vm.output.pop()
        if(status != 0L) pos += dir
        return status.toInt()
    }

    fun explore() {
        for (dir in Dir2.values) {
            val npos = pos + dir
            if(map.containsKey(npos).not()) {
                val status = move(dir)
                map[npos] = status != 0
                if(status == 2) oxygen = npos
                if(status != 0) {
                    explore()
                    move(-dir)
                }
            }
        }
    }

    data class BFSEntry(val pos: Vec2, val cost: Int)
    data class Answer(val ans1: Int, val ans2: Int)

    fun solve(): Answer {
        explore()
        val oxygen = this.oxygen ?: error("Oxygen system not found.")

        val open = ArrayDeque<BFSEntry>()
        open.add(BFSEntry(oxygen, 0))
        val closed = HashSet<Vec2>()
        closed.add(oxygen)

        var ans1 = 0
        var ans2 = 0

        while(open.isNotEmpty()) {
            val (pos, cost) = open.remove()
            for(dir in Dir2.values) {
                val npos = pos + dir
                if(npos == Vec2.ORIGIN) ans1 = cost + 1
                if(map[npos] == true && closed.add(npos)) {
                    ans2 = cost + 1
                    open.add(BFSEntry(npos, ans2))
                }
            }
        }

        return Answer(ans1, ans2)
    }

    fun display() {
        printGrid(map.keys.bounds()) { x, y ->
            when (Vec2(x, y)) {
                pos -> '@'
                oxygen -> '$'
                else -> when(map[x, y]) {
                    true -> '.'
                    false -> '#'
                    null -> ' '
                }
            }
        }
    }
}