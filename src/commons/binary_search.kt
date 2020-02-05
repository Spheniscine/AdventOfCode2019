package commons

inline fun bsFirst(first: Int, last: Int, predicate: (Int) -> Boolean): Int {
    var low = first
    var high = last

    while (low <= high) {
        val mid = low.and(high) + low.xor(high).shr(1)
        if(predicate(mid)) high = mid - 1
        else low = mid + 1
    }
    return low
}
inline fun IntRange.bsFirst(predicate: (Int) -> Boolean) = bsFirst(first, last, predicate)

inline fun bsLast(first: Int, last: Int, predicate: (Int) -> Boolean) = bsFirst(first, last) { !predicate(it) } - 1
inline fun IntRange.bsLast(predicate: (Int) -> Boolean) = bsLast(first, last, predicate)

inline fun <T> List<T>.bsIndexOfFirst(predicate: (T) -> Boolean) = bsFirst(0, lastIndex) { predicate(get(it)) }
inline fun <T> List<T>.bsFirst(predicate: (T) -> Boolean) = get(bsIndexOfFirst(predicate))
inline fun <T> List<T>.bsFirstOrNull(predicate: (T) -> Boolean) = getOrNull(bsIndexOfFirst(predicate))
inline fun <T> List<T>.bsIndexOfLast(predicate: (T) -> Boolean) = bsLast(0, lastIndex) { predicate(get(it)) }
inline fun <T> List<T>.bsLast(predicate: (T) -> Boolean) = get(bsIndexOfLast(predicate))
inline fun <T> List<T>.bsLastOrNull(predicate: (T) -> Boolean) = getOrNull(bsIndexOfLast(predicate))

fun <T: Comparable<T>> List<T>.bsHigher(v: T) = bsFirstOrNull { it > v }
fun <T: Comparable<T>> List<T>.bsLower(v: T) = bsLastOrNull { it < v }
fun <T: Comparable<T>> List<T>.bsCeiling(v: T) = bsFirstOrNull { it >= v }
fun <T: Comparable<T>> List<T>.bsFloor(v: T) = bsLastOrNull { it <= v }

inline fun LongArray.bsIndexOfFirst(predicate: (Long) -> Boolean) = bsFirst(0, lastIndex) { predicate(get(it)) }
inline fun LongArray.bsFirst(predicate: (Long) -> Boolean) = get(bsIndexOfFirst(predicate))
inline fun LongArray.bsFirstOrNull(predicate: (Long) -> Boolean) = getOrNull(bsIndexOfFirst(predicate))
inline fun LongArray.bsIndexOfLast(predicate: (Long) -> Boolean) = bsLast(0, lastIndex) { predicate(get(it)) }
inline fun LongArray.bsLast(predicate: (Long) -> Boolean) = get(bsIndexOfLast(predicate))
inline fun LongArray.bsLastOrNull(predicate: (Long) -> Boolean) = getOrNull(bsIndexOfLast(predicate))

fun LongArray.bsHigher(v: Long) = bsFirstOrNull { it > v }
fun LongArray.bsLower(v: Long) = bsLastOrNull { it < v }
fun LongArray.bsCeiling(v: Long) = bsFirstOrNull { it >= v }
fun LongArray.bsFloor(v: Long) = bsLastOrNull { it <= v }

inline fun bsFirst(first: Long, last: Long, predicate: (Long) -> Boolean): Long {
    var low = first
    var high = last

    while (low <= high) {
        val mid = low.and(high) + low.xor(high).shr(1)
        if(predicate(mid)) high = mid - 1
        else low = mid + 1
    }
    return low
}
inline fun LongRange.bsFirst(predicate: (Long) -> Boolean) = bsFirst(first, last, predicate)

inline fun bsLast(first: Long, last: Long, predicate: (Long) -> Boolean) = bsFirst(first, last) { !predicate(it) } - 1
inline fun LongRange.bsLast(predicate: (Long) -> Boolean) = bsLast(first, last, predicate)