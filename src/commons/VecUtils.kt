package commons

/**
 * Displays a set of points as an ASCII image; '#' represents a point in the set, and '.' represents a point not in the set.
 * The bounds of the image are calculated automatically.
 *
 * Avoid using on sets with a high range of values.
 */
fun Set<Vec2>.display() {
    if(isEmpty()) return
    val xMin = minBy { it.x }!!.x
    val xMax = maxBy { it.x }!!.x
    val yMin = minBy { it.y }!!.y
    val yMax = maxBy { it.y }!!.y

    for(y in yMin..yMax) {
        val ln = CharArray(xMax - xMin + 1)
        for(x in xMin..xMax) {
            val i = x - xMin
            if(Vec2(x, y) in this) ln[i] = '#'
            else ln[i] = '.'
        }
        println(String(ln))
    }
}