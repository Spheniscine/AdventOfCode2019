package commons

@Suppress("EqualsOrHashCode")
data class CharRun(val item: Char, val num: Int) {
    override fun hashCode(): Int = item.toInt().bitConcat(num).hash().toInt()
}

fun String.runs() = sequence {
    val i = iterator()

    if(i.hasNext().not()) return@sequence
    var last = i.nextChar()
    var num = 1

    for(e in i) {
        if(last == e) num++
        else {
            yield(CharRun(last, num))
            last = e
            num = 1
        }
    }
    yield(CharRun(last, num))
}

fun String.count(char: Char) = count { it == char }