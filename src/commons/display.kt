package commons

/**
 * Displays a set of points as an ASCII image; '#' represents a point in the set, and '.' represents a point not in the set.
 * The bounds of the image are calculated automatically.
 *
 * Avoid using on sets with a high range of values.
 */
fun Set<Vec2>.displayGrid() {
    if(isEmpty()) return
    val xMin = minBy { it.x }!!.x
    val xMax = maxBy { it.x }!!.x
    val yMin = minBy { it.y }!!.y
    val yMax = maxBy { it.y }!!.y

    displayGrid(xMin..xMax, yMin..yMax) { x, y -> Vec2(x, y) in this }
}

inline fun displayLine(len: Int, predicate: (Int) -> Boolean) {
    val ln = CharArray(len) { if(predicate(it)) '#' else '.' }
    println(String(ln))
}

inline fun displayLine(range: IntRange, predicate: (Int) -> Boolean) {
    val s = range.first
    displayLine(range.last - s + 1) { x -> predicate(x + s) }
}

inline fun displayGrid(w: Int, h: Int, predicate: (x: Int, y: Int) -> Boolean) {
    for(y in 0 until h) {
        displayLine(w) { x -> predicate(x, y) }
    }
}

inline fun displayGrid(xRange: IntRange, yRange: IntRange, predicate: (x: Int, y: Int) -> Boolean) {
    for(y in yRange) {
        displayLine(xRange) { x -> predicate(x, y) }
    }
}