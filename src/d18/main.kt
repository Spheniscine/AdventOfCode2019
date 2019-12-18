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
            val tile = grid[y][x]
            when(tile) {
                in 'a'..'z' -> posMap[tile] = Vec2(x, y)
                '@' -> posMap['0'] = Vec2(x, y)
            }
        }
    }

    val numKeys = posMap.size - 1
    val allMask = (1 shl numKeys) - 1

    fun distMap(): Map<Char, Map<Char, DistResult>> {
        val ans = HashMap<Char, Map<Char, DistResult>>()

        for((key, init) in posMap) {
            val map = HashMap<Char, DistResult>()
            val closed = hashSetOf(init)
            val open = ArrayDeque<BFSEntry>()
            open.add(BFSEntry(init, 0, 0))

            while(true) {
                val (pos, mask, cost) = open.poll() ?: break
                for(dir in Dir2.values) {
                    val npos = pos + dir
                    val tile = grid[npos]
                    if(tile == '#' || closed.add(npos).not()) continue
                    val nmask = if(tile in 'A'..'Z') mask.setBit(tile - 'A') else mask
                    if(key != tile && tile in 'a'..'z') map[tile] = DistResult(nmask, cost + 1)
                    open.add(BFSEntry(npos, nmask, cost + 1))
                }
            }
            ans[key] = map
        }

        return ans
    }

    fun solve(numBots: Int): Int {
        val distMap = distMap()

        val closed = HashSet<ClosedEntry>()
        val open = PriorityQueue(11, compareBy<OpenEntry> { it.cost })
        open.add(OpenEntry((0 until numBots).joinToString(""), 0, 0))

        while(true) {
            val (state, mask, cost) = open.poll() ?: break
            if(mask == allMask) return cost
            if(closed.add(ClosedEntry(state, mask)).not()) continue
            for(bot in 0 until numBots) {
                for((dest, distResult) in distMap[state[bot]]!!) {
                    val (doorMask, dist) = distResult
                    if(mask.getBit(dest - 'a') || doorMask and mask != doorMask) continue
                    val nstate = buildString {
                        append(state)
                        set(bot, dest)
                    }
                    val nmask = mask.setBit(dest - 'a')
                    if(ClosedEntry(nstate, nmask) in closed) continue
                    val ncost = cost + dist
                    open.add(OpenEntry(nstate, nmask, ncost))
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
fun Int.getBit(i: Int) = shr(i) and 1 == 1
fun Int.setBit(i: Int) = or(1 shl i)

data class BFSEntry(val pos: Vec2, val mask: Int, val cost: Int)
data class DistResult(val mask: Int, val cost: Int)
data class OpenEntry(val state: String, val mask: Int, val cost: Int)
data class ClosedEntry(val state: String, val mask: Int) {
    override fun hashCode(): Int = sipHasher.doHash {
        acc(state)
        acc(mask)
    }
}