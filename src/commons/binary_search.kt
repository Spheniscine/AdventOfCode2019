package commons

/**
 * Binary searches the given integer range based on the given lambda.
 * [goldilocks] should be a function that returns a positive value to indicate "too much"
 * , a negative value to indicate "too little", and 0 to indicate "just right".
 *
 * If no "just right" is found, the result will have exactFound = false and
 * the lowest possible "too much" value, which may be outside the range if the
 * entire range returns "too little"
 */
fun IntRange.binarySearchBy(goldilocks: (Int) -> Int): IntBinarySearchResult {
    var low = start
    var high = endInclusive

    while (low <= high) {
        val mid = low.and(high) + low.xor(high).shr(1)
        val cmp = goldilocks(mid)

        when {
            cmp < 0 -> low = mid + 1
            cmp > 0 -> high = mid - 1
            else -> return IntBinarySearchResult(mid, true)
        }
    }
    return IntBinarySearchResult(low, false)
}

data class IntBinarySearchResult(val value: Int, val exactFound: Boolean) {
    val floor get() = if(exactFound) value else value - 1
    inline val ceil get() = value
}

fun IntRange.bsFirst(predicate: (Int) -> Boolean): Int {
    var low = start
    var high = endInclusive

    while (low <= high) {
        val mid = low.and(high) + low.xor(high).shr(1)
        if(predicate(mid)) high = mid - 1
        else low = mid + 1
    }
    return low
}

inline fun IntRange.bsLast(crossinline predicate: (Int) -> Boolean) = bsFirst { !predicate(it) } - 1

inline fun <T> List<T>.bsIndexOfFirst(crossinline predicate: (T) -> Boolean) = indices.bsFirst { predicate(get(it)) }
inline fun <T> List<T>.bsFirst(crossinline predicate: (T) -> Boolean) = get(bsIndexOfFirst(predicate))
inline fun <T> List<T>.bsFirstOrNull(crossinline predicate: (T) -> Boolean) = getOrNull(bsIndexOfFirst(predicate))
inline fun <T> List<T>.bsIndexOfLast(crossinline predicate: (T) -> Boolean) = indices.bsLast { predicate(get(it)) }
inline fun <T> List<T>.bsLast(crossinline predicate: (T) -> Boolean) = get(bsIndexOfLast(predicate))
inline fun <T> List<T>.bsLastOrNull(crossinline predicate: (T) -> Boolean) = getOrNull(bsIndexOfLast(predicate))

fun <T: Comparable<T>> List<T>.bsHigher(v: T) = bsFirstOrNull { it > v }
fun <T: Comparable<T>> List<T>.bsLower(v: T) = bsLastOrNull { it < v }

fun LongRange.binarySearchBy(goldilocks: (Long) -> Int): LongBinarySearchResult {
    var low = start
    var high = endInclusive

    while (low <= high) {
        val mid = low.and(high) + low.xor(high).shr(1)
        val cmp = goldilocks(mid)

        when {
            cmp < 0 -> low = mid + 1
            cmp > 0 -> high = mid - 1
            else -> return LongBinarySearchResult(mid, true)
        }
    }
    return LongBinarySearchResult(low, false)
}

data class LongBinarySearchResult(val value: Long, val exactFound: Boolean) {
    val floor get() = if(exactFound) value else value - 1
    inline val ceil get() = value
}

fun LongRange.bsFirst(predicate: (Long) -> Boolean): Long {
    var low = start
    var high = endInclusive

    while (low <= high) {
        val mid = low.and(high) + low.xor(high).shr(1)
        if(predicate(mid)) high = mid - 1
        else low = mid + 1
    }
    return low
}

inline fun LongRange.bsLast(crossinline predicate: (Long) -> Boolean) = 
    bsFirst { !predicate(it) } - 1