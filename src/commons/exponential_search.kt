package commons

fun xsFirst(start: Int, predicate: (Int) -> Boolean): Int? {
    var inc = 2
    var prev = start
    var curr = start

    while(curr >= start) {
        if(predicate(curr)) return (prev..curr).bsFirst(predicate)
        prev = curr + 1
        curr += inc
        inc *= 2
    }

    return null
}

inline fun xsLast(start: Int, crossinline predicate: (Int) -> Boolean): Int? =
    xsFirst(start) { !predicate(it) }?.minus(1)