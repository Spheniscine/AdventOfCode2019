package d15.version.bugged.a_star_backtrack

import commons.*
import d9.IntCodeVM
import java.io.File
import java.util.ArrayDeque
import java.util.EnumMap
import java.util.PriorityQueue

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

    var pos = Vec2.ORIGIN
    val vm = IntCodeVM(prog)

    data class SearchEntry(val pos: Vec2, val cost: Int)
    data class PathEntry(val pos: Vec2, val fScore: Int, val path: PathNode<Dir2>?)

    fun pathTo(dest: Vec2): Boolean {
        val closed = HashSet<Vec2>()
        val gScore = HashMap<Vec2, Int>()
        gScore[pos] = 0
        val open = PriorityQueue<PathEntry>(11, compareBy { it.fScore })
        open.add(PathEntry(pos, pos.manDist(dest), null))
        while(open.isNotEmpty()) {
            val entry = open.remove()!!
            val curr = entry.pos
            if(!closed.add(curr)) continue
            if(curr == dest) {
                val path = entry.path.toList()
                for(dir in path) {
                    vm.input(dir)
                }
                vm.execute()
                vm.output.clear()
                pos = dest
                return true
            }
            for(dir in Dir2.values) {
                val neighbor = pos + dir
                if(neighbor in closed || map[neighbor] != true) continue
                val tentativeGScore = gScore[curr]!! + 1
                if(gScore[neighbor].let { it != null &&
                            tentativeGScore >= it }) continue
                gScore[neighbor] = tentativeGScore
                open.add(
                    PathEntry(
                        neighbor,
                        tentativeGScore + neighbor.manDist(dest),
                        entry.path + dir
                    )
                )
            }
        }
        return false
    }

    fun search(): Int {
        val open = ArrayDeque<SearchEntry>()
        open.add(SearchEntry(Vec2.ORIGIN, 0))

        var ans = -1

        while(open.isNotEmpty()) {
            val (pos, cost) = open.remove()

            for(dir in Dir2.values) {
                val npos = pos + dir
                if(map.containsKey(npos).not()) {
                    pathTo(pos).also {
                        if(it == false)
                            0
                    }
                    vm.input(dir)
                    vm.execute()
                    val status = vm.output.pop()
                    when(status) {
                        0L -> {
                            map[npos] = false
                        }
                        1L -> {
                            map[npos] = true
                            open.add(SearchEntry(npos, cost + 1))
                            this.pos = npos
                        }
                        2L -> {
                            map[npos] = true
                            oxygen = npos
                            ans = cost + 1
                            open.add(SearchEntry(npos, ans))
                            this.pos = npos
                        }
                    }
                }
            }
        }
        return ans
    }



    fun fill(): Int {
        val oxygen = this.oxygen ?: return -1

        val open = ArrayDeque<SearchEntry>()
        open.add(SearchEntry(oxygen, 0))
        val closed = HashSet<Vec2>()
        closed.add(oxygen)

        var ans = 0

        while(open.isNotEmpty()) {
            val (pos, cost) = open.remove()
            for(dir in Dir2.values) {
                val npos = pos + dir
                if(map[npos] == true && closed.add(npos)) {
                    ans = cost + 1
                    open.add(SearchEntry(npos, ans))
                }
            }
        }

        return ans
    }
}