package commons

data class Rect(val xRange: IntRange, val yRange: IntRange) {
    companion object {
        val EMPTY = Rect(IntRange.EMPTY, IntRange.EMPTY)
    }

    fun isEmpty() = xRange.isEmpty() || yRange.isEmpty()

    override fun equals(other: Any?): Boolean {
        if(other !is Rect) return false
        if(isEmpty() && other.isEmpty()) return true
        return xRange == other.xRange && yRange == other.yRange
    }

    override fun hashCode(): Int = sipHasher.doHash {
        if(!isEmpty()) {
            acc(xRange.first)
            acc(xRange.last)
            acc(yRange.first)
            acc(yRange.last)
        }
    }
}

fun Collection<Vec2>.bounds(): Rect {
    if(isEmpty()) return Rect.EMPTY

    val xMin = minBy { it.x }!!.x
    val xMax = maxBy { it.x }!!.x
    val yMin = minBy { it.y }!!.y
    val yMax = maxBy { it.y }!!.y

    return Rect(xMin..xMax, yMin..yMax)
}