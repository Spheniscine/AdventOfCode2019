package commons

fun Set<Vec2>.displayGrid() {
    displayGrid(bounds()) { x, y -> Vec2(x, y) in this }
}

inline fun printLine(len: Int, func: (Int) -> Char) {
    val ln = CharArray(len) { func(it) }
    println(String(ln))
}

inline fun displayLine(len: Int, predicate: (Int) -> Boolean) {
    printLine(len) { if(predicate(it)) WHITE_CHAR else BLACK_CHAR }
}

inline fun printLine(range: IntRange, func: (Int) -> Char) {
    val s = range.first
    printLine(range.last - s + 1) { x -> func(x + s) }
}

inline fun displayLine(range: IntRange, predicate: (Int) -> Boolean) {
    printLine(range) { if(predicate(it)) WHITE_CHAR else BLACK_CHAR }
}

inline fun printGrid(w: Int, h: Int, func: (x: Int, y: Int) -> Char) {
    for(y in 0 until h) {
        printLine(w) { x -> func(x, y) }
    }
}

inline fun displayGrid(w: Int, h: Int, predicate: (x: Int, y: Int) -> Boolean) {
    printGrid(w, h) { x, y -> if(predicate(x, y)) WHITE_CHAR else BLACK_CHAR }
}

inline fun printGrid(xRange: IntRange, yRange: IntRange, func: (x: Int, y: Int) -> Char) {
    for(y in yRange) {
        printLine(xRange) { x -> func(x, y) }
    }
}

inline fun printGrid(rect: Rect, func: (x: Int, y: Int) -> Char) = printGrid(rect.xRange, rect.yRange, func)

inline fun displayGrid(xRange: IntRange, yRange: IntRange, predicate: (x: Int, y: Int) -> Boolean) {
    printGrid(xRange, yRange) { x, y -> if(predicate(x, y)) WHITE_CHAR else BLACK_CHAR }
}

inline fun displayGrid(rect: Rect, predicate: (x: Int, y: Int) -> Boolean) = displayGrid(rect.xRange, rect.yRange, predicate)

const val WHITE_CHAR = '▓'
const val BLACK_CHAR = '·'