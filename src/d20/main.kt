@file:Suppress("NOTHING_TO_INLINE")

package d20

import commons.*
import java.io.File
import java.util.ArrayDeque
import java.util.PriorityQueue

private val input by lazy { File("src/d20/input/gmail.in").readText() }

fun main() {
    println("--- Day 20: Donut Maze ---")

    markTime()
    val maze = input.lines()
    val h = maze.size
    val w = maze.maxBy { it.length }!!.length

    val portalPreRegex = Regex("""[A-Z]{2}\.""")
    val portalPostRegex = Regex("""\.[A-Z]{2}""")

    val gates = StringHashMap<Vec2>()
    val warps = HashMap<Vec2, Vec2>()

    fun connectGate(label: String, pos: Vec2) {
        gates[label]?.let {
            warps[pos] = it
            warps[it] = pos
            gates.remove(label)
            true
        } ?: run {
            gates[label] = pos
        }
    }

    for(y in 0 until h) {
        val row = maze[y]

        for(match in portalPreRegex.findAll(row)) {
            val label = match.value.substring(0, 2)
            val pos = Vec2(match.range.last, y)
            connectGate(label, pos)
        }

        for(match in portalPostRegex.findAll(row)) {
            val label = match.value.substring(1, 3)
            val pos = Vec2(match.range.first, y)
            connectGate(label, pos)
        }
    }

    for(x in 0 until w) {
        val col = String(CharArray(h) { maze[x, it] })

        for(match in portalPreRegex.findAll(col)) {
            val label = match.value.substring(0, 2)
            val pos = Vec2(x, match.range.last)
            connectGate(label, pos)
        }

        for(match in portalPostRegex.findAll(col)) {
            val label = match.value.substring(1, 3)
            val pos = Vec2(x, match.range.first)
            connectGate(label, pos)
        }
    }

    val start = gates["AA"]!!
    val end = gates["ZZ"]!!

    val ans1 = run {
        val closed = hashSetOf(start)
        val open = ArrayDeque<BFSEntry>()
        open.add(BFSEntry(start, 0))

        while(true) {
            val (pos, cost) = open.poll() ?: break
            if(pos == end) return@run cost

            val neighbors = mutableListOf<Vec2>()
            for(dir in Dir2.values) {
                val npos = pos + dir
                if(maze[npos] == '.') neighbors.add(npos)
            }
            warps[pos]?.let { neighbors.add(it) }

            for(npos in neighbors) {
                if(closed.add(npos)) {
                    open.add(BFSEntry(npos, cost + 1))
                }
            }
        }

        -1
    }
    println("Part 1: $ans1")
    printTime()

    val distMap: Map<Vec2, Map<Vec2, Int>> = run {
        val ans = HashMap<Vec2, Map<Vec2, Int>>()

        for(src in sequenceOf(start) + warps.keys) {
            val map = HashMap<Vec2, Int>()
            val closed = hashSetOf(src)
            val open = ArrayDeque<BFSEntry>()
            open.add(BFSEntry(src, 0))

            while(true) {
                val (pos, cost) = open.poll() ?: break

                // Pretend a key is also its own door, blocking other keys behind it.
                // This significantly prunes the search tree, as it prevents walking past keys without activating them.
                for(dir in Dir2.values) {
                    val npos = pos + dir
                    val tile = maze[npos]
                    if(tile != '.' || closed.add(npos).not()) continue
                    if(npos != src) when {
                        npos == end -> map[npos] = cost + 1
                        warps.containsKey(npos) -> map[warps[npos]!!] = cost + 2
                    }
                    open.add(BFSEntry(npos, cost + 1))
                }
            }
            ans[src] = map
        }

        ans
    }

    markTime()
    val ans2 = run {
        val closed = HashMap<Vec3, Int>()
        val open = PriorityQueue<Dijk<Vec3>>(11, compareBy { it.cost })
        open.add(Dijk(start.z(0), 0))
        val goal = end.z(0)

        while(true) {
            val (state, cost) = open.poll() ?: break
            if(state == goal) return@run cost
            if(closed[state].let { it != null && it < cost }) continue
            for((dest, dist) in distMap[state.x, state.y]!!) {
                if(dest == end && state.z != 0) continue

                // if the new position is on the outer edge, we just warped inward, otherwise, we warped outward
                val inward = (dest.x == 2 || dest.x == w-3) || (dest.y == 2 || dest.y == h-3)
                val nz = if(dest == end) 0 else state.z + (if(inward) 1 else -1)
                if(nz < 0) continue

                val nstate = dest.z(nz)
                val ncost = cost + dist

                if(closed[nstate].let { it != null && it <= ncost }) continue
                closed[nstate] = ncost
                open.add(Dijk(nstate, ncost))
            }
        }

        -1
    }
    println("Part 2: $ans2")
    printTime()
}

operator fun List<String>.get(x: Int, y: Int) = if(y in indices && x in this[y].indices) this[y][x] else ' '
operator fun List<String>.get(pos: Vec2) = this[pos.x, pos.y]

data class BFSEntry(val pos: Vec2, val cost: Int)
data class Dijk<T>(val state: T, val cost: Int)

fun Vec2.z(z: Int) = Vec3(x, y, z)

