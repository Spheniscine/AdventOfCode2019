package commons

inline fun <T> T.iterate(times: Int, func: (T) -> T): T {
    var res = this
    repeat(times) { res = func(res) }
    return res
}

fun <T> MutableList<T>.pop() = removeAt(lastIndex)
fun <T> MutableList<T>.poll() = if(isEmpty()) null else pop()