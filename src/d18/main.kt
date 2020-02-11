@file:Suppress("NOTHING_TO_INLINE")

package d18

import commons.*
import java.io.File
import java.util.ArrayDeque
import java.util.PriorityQueue

private val input by lazy { File("src/d18/input/gmail.in").readText() }

fun main() {
    println("--- Day 18: Many-Worlds Interpretation ---")

    markTime()
    val grid = input.lines().map { it.toCharArray() }
    val h = grid.size
    val w = grid[0].size

    val posMap = HashMap<Char, Vec2>()
    for(y in 0 until h) {
        for(x in 0 until w) {
            when(val tile = grid[y][x]) {
                in 'a'..'z' -> posMap[tile] = Vec2(x, y)
                '@' -> posMap['0'] = Vec2(x, y)
            }
        }
    }

    val numKeys = posMap.size - 1
    val allMask = (1 shl numKeys) - 1

    fun distMap(): Map<Char, List<SuccEntry>> {
        val ans = HashMap<Char, List<SuccEntry>>()

        for((src, initPos) in posMap) {
            val successors = mutableListOf<SuccEntry>()
            val closed = hashSetOf(initPos)
            val open = ArrayDeque<BFSEntry>()
            open.add(BFSEntry(initPos, 0))

            var cost = 0
            while(open.isNotEmpty()) {
                repeat(open.size) {
                    val (pos, _mask) = open.remove()

                    // Pretend a key is also its own door, blocking other keys behind it.
                    // This significantly prunes the search tree, as it prevents walking past keys without activating them.
                    val mask = grid[pos].let { if (it in 'a'..'z') _mask.setBit(it - 'a') else _mask }

                    for (dir in Dir2.values) {
                        val npos = pos + dir
                        val tile = grid[npos]
                        if (tile == '#' || closed.add(npos).not()) continue
                        val nmask = if (tile in 'A'..'Z') mask.setBit(tile - 'A') else mask
                        if (src != tile && tile in 'a'..'z') successors.add(SuccEntry(tile, nmask, cost + 1))
                        open.add(BFSEntry(npos, nmask))
                    }
                }
                cost++
            }
            ans[src] = successors
        }

        return ans
    }

    fun solve(numBots: Int): Int {
        val distMap = distMap()

        val closed = HashMap<State, Int>()
        val open = PriorityQueue<Cost<State>>(11, compareBy { it.cost })
        open.add(Cost(State(String(CharArray(numBots) { '0' + it }), 0), 0))

        while(true) {
            val (state, cost) = open.poll() ?: break
            val (pos, mask) = state
            if(mask == allMask) return cost
            if(closed[state].let { it != null && it < cost }) continue
            for(bot in 0 until numBots) {
                for((dest, doorMask, dist) in distMap[pos[bot]]!!) {
                    if(mask.getBit(dest - 'a') || doorMask and mask != doorMask) continue
                    val npos = buildString {
                        append(pos)
                        set(bot, dest)
                    }
                    val nmask = mask.setBit(dest - 'a')
                    val ncost = cost + dist
                    val nstate = State(npos, nmask)
                    if(closed[nstate].let { it != null && it <= ncost }) continue
                    closed[nstate] = ncost
                    open.add(Cost(State(npos, nmask), ncost))
                }
            }
        }

        return -1
    }

    val ans1 = solve(1)

    println("Part 1: $ans1")
    printTime()

    markTime()
    val oldEntrance = posMap['0']!!

    for(dir in Dir2.values) grid[oldEntrance + dir] = '#'

    posMap['0'] = oldEntrance + Vec2(-1, -1)
    posMap['1'] = oldEntrance + Vec2(1, -1)
    posMap['2'] = oldEntrance + Vec2(-1, 1)
    posMap['3'] = oldEntrance + Vec2(1, 1)

    val ans2 = solve(4)
    println("Part 2: $ans2")
    printTime()
}

operator fun List<CharArray>.get(pos: Vec2) = this[pos.y][pos.x]
operator fun List<CharArray>.set(pos: Vec2, v: Char) { this[pos.y][pos.x] = v }
inline fun Int.getBit(i: Int) = shr(i) and 1 == 1
inline fun Int.setBit(i: Int) = or(1 shl i)

data class BFSEntry(val pos: Vec2, val mask: Int)
data class SuccEntry(val dest: Char, val mask: Int, val cost: Int)
data class Cost<T>(val state: T, val cost: Int)
@Suppress("EqualsOrHashCode")
data class State(val pos: String, val mask: Int) {
    override fun hashCode(): Int = sipHasher.doHash {
        acc(pos)
        acc(mask)
    }
}