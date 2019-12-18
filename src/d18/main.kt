package d18

import commons.*
import java.io.File
import java.util.PriorityQueue

private val input by lazy { File("src/d18/input/gmail.in").readText() }

fun main() {
    println("--- Day 18: Many-Worlds Interpretation ---")

    markTime()
    val grid = input.lines().map { it.toCharArray() }
    val h = grid.size
    val w = grid[0].size

    val rect = Rect(0 until w, 0 until h)
    val entrance = rect.first { grid[it] == '@' }
    val numKeys = rect.count { grid[it] in 'a'..'z' }
    val allMask = (1 shl numKeys) - 1

    val ans1 = run {
        val closed = HashSet<ClosedEntry>()
        val open = PriorityQueue(11, compareBy<OpenEntry> { it.cost })
        open.add(OpenEntry(0, entrance, 0))

        while(true) {
            val (cost, pos, mask) = open.poll() ?: break
            if(closed.add(ClosedEntry(grid[pos], mask)).not()) continue

            if(mask == allMask)
                return@run cost

            // bfs for nearest possible keys
            val bclosed = HashSet<Vec2>().also { it.add(pos) }
            var bopen = listOf(pos)
            var bcost = cost

            while(bopen.isNotEmpty()) {
                val new = mutableListOf<Vec2>()
                bcost++

                for(bpos in bopen) {
                    for (dir in Dir2.values) {
                        val npos = bpos + dir
                        if(bclosed.add(npos).not()) continue
                        val tile = grid[npos]
                        var passable = tile != '#' && (tile !in 'A'..'Z' || mask.getBit(tile - 'A'))
                        if(tile in 'a'..'z' && mask.getBit(tile - 'a').not()) {
                            passable = false
                            val nmask = mask or (1 shl tile - 'a')
                            if(ClosedEntry(tile, nmask) in closed) continue
                            open.add(OpenEntry(bcost, npos, nmask))
                        }

                        if(passable) { new.add(npos) }
                    }
                }

                bopen = new
            }
        }

        -1
    }

    println("Part 1: $ans1")
    printTime()

//    val ans2 = 2
//    println("Part 2: $ans2")
}

operator fun List<CharArray>.get(pos: Vec2) = this[pos.y][pos.x]
fun Int.getBit(i: Int) = shr(i) and 1 == 1
val Int.bitCount get() = Integer.bitCount(this)

data class OpenEntry(val cost: Int, val pos: Vec2, val mask: Int)
data class ClosedEntry(val last: Char, val mask: Int) {
    override fun hashCode(): Int = last.toInt().bitConcat(mask).hash().toInt()
}