package d10

import commons.*
import java.io.File
import java.util.ArrayDeque
import kotlin.math.*

private val input by lazy { File("src/d10/input/gmail.in").readText() }

fun main() {
    val A = HashSet<Pos2>()
    input.lineSequence().forEachIndexed { y, ln ->
        ln.forEachIndexed { x, c ->
            if(c == '#') A.add(Pos2(x, y))
        }
    }

    val (best, ans1) = A.map { a ->
        val B = HashSet<Pos2>()
        for(b in A) {
            if(a != b) B.add((b-a).normalize())
        }
        a to B.size
    }.maxBy { it.second }!!

    println("Part 1: $ans1")

    val vmap = HashMap<Pos2, MutableList<Pos2>>().memoize { mutableListOf() }

    for(a in A) {
        if(a == best) continue
        vmap[(a - best).normalize()].add(a)
    }

    for(l in vmap.values) {
        l.sortBy { best.manDist(it) }
    }

    val V = vmap.entries.sortedBy {
        val (x, y) = it.key
        -atan2(x.toDouble(), y.toDouble())
    }

    val seq = sequence {
        val Vi = ArrayDeque<ListIterator<Pos2>>()
        for((_, l) in V) {
            Vi.add(l.listIterator())
        }

        while(Vi.isNotEmpty()) {
            val li = Vi.remove()
            yield(li.next())
            if(li.hasNext()) Vi.add(li)
        }
    }

    val ans2 = seq.elementAt(199).let { (x, y) -> x * 100 + y }
    println("Part 2: $ans2")
}

fun Pos2.normalize() = gcd(x, y).let { Pos2(x/it, y/it) }