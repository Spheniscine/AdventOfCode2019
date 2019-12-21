package d6

import commons.*
import java.io.File

private val input by lazy { File("src/d6/input/gmail.in").readText() }

fun main() {
    println("--- Day 6: Universal Orbit Map ---")
    markTime()
    val bodies = StringHashMap<Body>().memoize { Body(it) }
    for(ln in input.lineSequence()) {
        val (parent, child) = ln.split(')').map { bodies[it] }
        child.parent = parent
        parent.children.add(child)
    }

    val ans1 = run {
        var ans = 0
        fun dfs(body: Body, depth: Int) {
            ans += depth
            for(c in body.children) {
                dfs(c, depth+1)
            }
        }
        dfs(bodies["COM"], 0)
        ans
    }
    println("Part 1: $ans1")
    printTime()

    markTime()
    val you = bodies["YOU"]
    val san = bodies["SAN"]

    val youAnc = HashMap<Body, Int>().also {
        you.ancestors().forEachIndexed { index, body ->
            it[body] = index
        }
    }

    val ans2 = san.ancestors().withIndex().first { youAnc.containsKey(it.value) }
        .let { it.index + youAnc.getValue(it.value) }
    println("Part 2: $ans2")
    printTime()
}

class Body(val name: String) {
    var parent: Body? = null
    val children = mutableListOf<Body>()

    fun ancestors() = generateSequence(parent) { it.parent }
}
