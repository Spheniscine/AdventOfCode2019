package d13

import commons.*
import d9.IntCodeVM
import java.io.File

private val input by lazy { File("src/d13/input/gmail.in").readText() }

fun main() {
    println("--- Day 13: Care Package ---")
    markTime()
    val prog = input.split(',').map { it.toLong() }

    val game1 = Game(prog)
    game1.start1()
    val ans1 = game1.grid.values.count { it == Game.Tile.Block }

    println("Part 1: $ans1")
    printTime()

    markTime()
    val game = Game(prog)
//     game.startManual()
    game.startAuto()
    val ans2 = game.score
    println("Part 2: $ans2")
    printTime()
}

class Game(prog: List<Long>) {
    val vm = IntCodeVM(prog)

    enum class Tile {
        Empty, Wall, Block, Paddle, Ball;
        companion object {
            val values = values().asList()
        }
    }

    val grid = HashMap<Vec2, Tile>()
    var score = 0L

    var ballPos = Vec2.ORIGIN
    var paddlePos = Vec2.ORIGIN

    fun readGrid() {
        grid.clear()
        for((x, y, id) in vm.output.chunked(3)) {
            if(x == -1L && y == 0L) score = id
            else {
                val pos = Vec2(x.toInt(), y.toInt())
                grid[pos] = Tile.values[id.toInt()]
            }
        }
        vm.output.clear()
    }

    fun start1() {
        vm.execute()
        readGrid()
    }

    fun startManual() {
        vm.mem[0] = 2
        while(true) {
            vm.execute()
            readGrid()
            display()
            if(vm.isWaiting) {
                while(true) {
                    print("Input: ")
                    val ln = readLine()!!
                    if(ln.isBlank()) continue
                    val c = ln[0]
                    val i = controls.indexOf(c) - 1
                    if(i < -1) continue
                    vm.input(i)
                    break
                }
            } else break
        }
    }

    fun readGridAuto() {
        for((x, y, id) in vm.output.chunked(3)) {
            if(x == -1L && y == 0L) score = id
            else when(id.toInt()) {
                Tile.Ball.ordinal -> ballPos = Vec2(x.toInt(), y.toInt())
                Tile.Paddle.ordinal -> paddlePos = Vec2(x.toInt(), y.toInt())
            }
        }
        vm.output.clear()
    }

    fun startAuto() {
        vm.mem[0] = 2
        while(true) {
            vm.execute()
            readGridAuto()
            if(vm.isWaiting) {
                val i = ballPos.x.compareTo(paddlePos.x)
                vm.input(i)
            } else break
        }
        // display()
    }

    fun display() {
        if(grid.isEmpty()) return
        val xMin = grid.keys.minBy { it.x }!!.x
        val xMax = grid.keys.maxBy { it.x }!!.x
        val yMin = grid.keys.minBy { it.y }!!.y
        val yMax = grid.keys.maxBy { it.y }!!.y

        printGrid(xMin..xMax, yMin..yMax) { x, y ->
            when(grid[Vec2(x, y)]) {
                Tile.Empty -> ' '
                Tile.Wall -> '▓'
                Tile.Block -> 'O'
                Tile.Paddle -> '—'
                Tile.Ball -> '•'
                null -> ' '
            }
        }
        println("Score: $score")
    }
}

const val controls = "oeu"