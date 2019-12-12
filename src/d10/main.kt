package d10

import commons.*
import java.io.File
import java.util.ArrayDeque
import kotlin.math.*

private val input by lazy { File("src/d10/input/gmail.in").readText() }

fun main() {
    println("--- Day 10: Monitoring Station ---")
    markTime()
    val A = mutableListOf<Vec2>()
    input.lineSequence().forEachIndexed { y, ln ->
        ln.forEachIndexed { x, c ->
            if(c == '#') A.add(Vec2(x, y))
        }
    }

    val (best, ans1) = A.map { a ->
        val B = HashSet<Vec2>()
        for(b in A) {
            if(a != b) B.add((b-a).normalize())
        }
        a to B.size
    }.maxBy { it.second }!!

    println("Part 1: $ans1")
    printTime()

    markTime()
    val vmap = HashMap<Vec2, MutableList<Vec2>>().memoize { mutableListOf() }

    for(a in A) {
        if(a == best) continue
        vmap[(a - best).normalize()].add(a)
    }

    for(l in vmap.values) {
        l.sortBy { best.manDist(it) }
    }

//    val V0 = vmap.entries.sortedBy {
//        val (x, y) = it.key
//        -atan2(x.toDouble(), y.toDouble())
//    }

    val V = vmap.entries.sortedWith(Comparator { a, b -> compareAngle(a.key, b.key) })

//    test(V)
//    require(V0 == V)

    val seq = sequence {
        val Vi = ArrayDeque<ListIterator<Vec2>>()
        for((_, l) in V) {
            Vi.add(l.listIterator())
        }

        while(Vi.isNotEmpty()) {
            val li = Vi.remove()
            yield(li.next())
            if(li.hasNext()) Vi.add(li)
        }
    }.toList()

    val ans2 = seq[199].let { (x, y) -> x * 100 + y }
    println("Part 2: $ans2")
    printTime()
}

fun Vec2.normalize() = gcd(x, y).let { Vec2(x/it, y/it) }

// discrete angle comparator: -1 = "a < b", 0 = "a = b", 1 = "a > b"
fun compareAngle(a: Vec2, b: Vec2): Int {
    // note: behavior undefined if either vector is (0, 0)
    (a.x < 0).compareTo(b.x < 0).let { if(it != 0) return it } // separate to left and right half, if different halves, right half < left half
    (b cross a).sign.let { if(it != 0) return it } // use cross product to find direction of smallest angle
    return a.y.sign.compareTo(b.y.sign) // if cross is 0, angles are either incident, or edge case (0, -a) vs (0, b) where a, b > 0
}

private fun test(V: List<Map.Entry<Vec2, List<Vec2>>>) {
    for(i in 0 until V.lastIndex) {
        require(compareAngle(V[i].key, V[i].key) == 0)
        for(j in i+1..V.lastIndex) {
            require(compareAngle(V[i].key, V[j].key) == -1)
            require(compareAngle(V[j].key, V[i].key) == 1)
        }
    }

    println("Test passed.")
}