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

inline fun printLine(len: Int, func: (Int) -> Char) {
    val ln = CharArray(len) { func(it) }
    println(String(ln))
}

inline fun displayLine(len: Int, predicate: (Int) -> Boolean) {
    printLine(len) { if(predicate(it)) '#' else '.' }
}

inline fun printLine(range: IntRange, func: (Int) -> Char) {
    val s = range.first
    printLine(range.last - s + 1) { x -> func(x + s) }
}

inline fun displayLine(range: IntRange, predicate: (Int) -> Boolean) {
    printLine(range) { if(predicate(it)) '#' else '.' }
}

inline fun printGrid(w: Int, h: Int, func: (x: Int, y: Int) -> Char) {
    for(y in 0 until h) {
        printLine(w) { x -> func(x, y) }
    }
}

inline fun displayGrid(w: Int, h: Int, predicate: (x: Int, y: Int) -> Boolean) {
    printGrid(w, h) { x, y -> if(predicate(x, y)) '#' else '.' }
}

inline fun printGrid(xRange: IntRange, yRange: IntRange, func: (x: Int, y: Int) -> Char) {
    for(y in yRange) {
        printLine(xRange) { x -> func(x, y) }
    }
}

inline fun displayGrid(xRange: IntRange, yRange: IntRange, predicate: (x: Int, y: Int) -> Boolean) {
    printGrid(xRange, yRange) { x, y -> if(predicate(x, y)) '#' else '.' }
}