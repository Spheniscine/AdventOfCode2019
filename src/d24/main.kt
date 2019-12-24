package d24

import commons.*
import commons.Dir2.*
import java.io.File

private val input by lazy { File("src/d24/input/gmail.in").readText() }
private val initGrid by lazy {
    input.lines().map { ln ->
        BooleanArray(ln.length) { ln[it] == '#' }
    }
}

fun main() {
    println("--- Day 24 ---")

    markTime()
    val ans1 = Simulator().ans1()
    println("Part 1: $ans1")
    printTime()

    markTime()
    val ans2 = Simulator2().ans2(200)
    println("Part 2: $ans2")
    printTime()
}

operator fun List<BooleanArray>.get(x: Int, y: Int) = y in indices && x in this[y].indices && this[y][x]

operator fun List<BooleanArray>.get(pos: Vec2) = get(pos.x, pos.y)

class Simulator {
    fun next(grid: List<BooleanArray>) = List(grid.size) { y ->
        BooleanArray(grid[0].size) { x ->
            val pos = Vec2(x, y)
            val neighbors = Dir2.values.count { grid[pos + it] }
            if(grid[x, y]) (neighbors == 1) else (neighbors in 1..2)
        }
    }

    fun score(grid: List<BooleanArray>): Int {
        var ans = 0
        var bit = 1

        for(y in 0 until grid.size) {
            for(x in 0 until grid[y].size) {
                if(grid[x, y]) ans += bit
                bit *= 2
            }
        }

        return ans
    }

    fun ans1(): Int {
        var grid = initGrid
        val seen = IntHashSet().also { it.add(score(grid)) }

        while(true) {
            grid = next(grid)
            val score = score(grid)
            if(seen.add(score).not()) return score
        }
    }
}

class Simulator2 {
    val center = Vec2(2, 2)

    fun neighbors(pos: Vec3) : Sequence<Vec3> = sequence {
        val xy = Vec2(pos.x, pos.y)
        val z = pos.z

        for(dir in Dir2.values) {
            val (x, y) = xy + dir
            when {
                x !in 0..4 || y !in 0..4 -> {
                    yield((center + dir).z(z-1))
                }
                x == 2 && y == 2 -> {
                    when(dir) {
                        Right -> for(j in 0..4) yield(Vec3(0, j, z+1))
                        Down -> for(i in 0..4) yield(Vec3(i, 0, z+1))
                        Left -> for(j in 0..4) yield(Vec3(4, j, z+1))
                        Up -> for(i in 0..4) yield(Vec3(i, 4, z+1))
                    }
                }
                else -> yield(Vec3(x, y, z))
            }
        }
    }

    fun ans2(iters: Int): Int {
        var state = HashSet<Vec3>()

        for(y in 0..4) {
            for(x in 0..4) {
                if(initGrid[x, y]) state.add(Vec3(x, y, 0))
            }
        }

        repeat(iters) {
            val cnt = HashMap<Vec3, Int>().default(0)
            for(pos in state) {
                for(neighbor in neighbors(pos)) {
                    cnt[neighbor]++
                }
            }
            val next = HashSet<Vec3>()
            for((pos, num) in cnt) {
                val live = if(pos in state) (num == 1) else (num in 1..2)
                if(live) next.add(pos)
            }
            state = next
        }

        return state.size
    }
}

fun Vec2.z(z: Int) = Vec3(x, y, z)