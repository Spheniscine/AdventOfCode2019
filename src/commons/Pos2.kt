package commons

import kotlin.math.*

// y- is upward, y+ is downward, unlike traditional math, to accommodate 2D arrays
@Suppress("EqualsOrHashCode")
data class Pos2(val x: Int, val y: Int) {
    companion object {
        val ORIGIN = Pos2(0, 0)
    }

    // Manhattan distance
    fun manDist(other: Pos2) = abs(x - other.x) + abs(y - other.y)
    fun manDist() = abs(x) + abs(y)

    operator fun plus(dir: Dir2) = when(dir) {
        Dir2.Right -> Pos2(x+1, y)
        Dir2.Down -> Pos2(x, y+1)
        Dir2.Left -> Pos2(x-1, y)
        Dir2.Up -> Pos2(x, y-1)
    }

    operator fun plus(other: Pos2) = Pos2(x + other.x, y + other.y)
    operator fun minus(other: Pos2) = Pos2(x - other.x, y - other.y)
    operator fun times(scale: Int) = Pos2(x * scale, y * scale)

    override fun hashCode(): Int = x.bitConcat(y).hash().toInt()
}

enum class Dir2 { Right, Down, Left, Up;
    companion object {
        inline val East get() = Right
        inline val South get() = Down
        inline val West get() = Left
        inline val North get() = Up

        fun fromChar(char: Char) = when(char) {
            in "RrEe" -> Right
            in "DdSs" -> Down
            in "LlWw" -> Left
            in "UuNn" -> Up
            else -> error("Unrecognized direction")
        }
    }
}