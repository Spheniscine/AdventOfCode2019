package d15.version.save_state

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

    val ans1 = droid.search()
    println("Part 1: $ans1")
    printTime()

    markTime()
    val ans2 = droid.fill()
    println("Part 2: $ans2")
    printTime()
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

    private fun IntCodeVM.input(dir: Dir2) = input(moves.getValue(dir))
    data class SearchEntry(val pos: Vec2, val state: IntCodeVM, val cost: Int)

    fun search(): Int {
        val open = ArrayDeque<SearchEntry>()
        open.add(SearchEntry(Vec2.ORIGIN, IntCodeVM(prog), 0))

        var ans = -1

        while(open.isNotEmpty()) {
            val (pos, state, cost) = open.remove()

            for(dir in Dir2.values) {
                val npos = pos + dir
                if(map.containsKey(npos).not()) {
                    val vm = state.clone()
                    vm.input(dir)
                    vm.execute()
                    val status = vm.output.pop()
                    when(status) {
                        0L -> {
                            map[npos] = false
                        }
                        1L -> {
                            map[npos] = true
                            open.add(SearchEntry(npos, vm, cost + 1))
                        }
                        2L -> {
                            map[npos] = true
                            oxygen = npos
                            ans = cost + 1
                            open.add(SearchEntry(npos, vm, ans))
                        }
                    }
                }
            }
        }
        return ans
    }

    data class FillEntry(val pos: Vec2, val cost: Int)

    fun fill(): Int {
        val oxygen = this.oxygen ?: return -1

        val open = ArrayDeque<FillEntry>()
        open.add(FillEntry(oxygen, 0))
        val closed = HashSet<Vec2>()
        closed.add(oxygen)

        var ans = 0

        while(open.isNotEmpty()) {
            val (pos, cost) = open.remove()
            for(dir in Dir2.values) {
                val npos = pos + dir
                if(map[npos] == true && closed.add(npos)) {
                    ans = cost + 1
                    open.add(FillEntry(npos, ans))
                }
            }
        }

        return ans
    }
}