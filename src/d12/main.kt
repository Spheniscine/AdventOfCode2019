package d12

import commons.*
import java.io.File

private val input by lazy { File("src/d12/input/gmail.in").readText() }

fun main() {
    markTime()
    val vecs = input.lines().map { it.parseVec3() }

    val sim1 = Simulator(vecs)
    sim1.step(1000)

    val ans1 = sim1.totalEnergy
    println("Part 1: $ans1")
    printTime()

    markTime()
    val sim2 = Simulator(vecs)
    val ans2 = sim2.findCycle()
    println("Part 2: $ans2")
    printTime()
}

val REGEX by lazy { Regex("""<x=(.+), y=(.+), z=(.+)>""")}
fun String.parseVec3() = REGEX.capture(this)!!.let { (x, y, z) ->
    Vec3(x.toInt(), y.toInt(), z.toInt())
}

class Moon(var pos: Vec3, var vel: Vec3 = Vec3.ZERO) {
    val energy get() = pos.manDist() * vel.manDist()
}

class Simulator(val vecs: List<Vec3>) {
    val moons = vecs.map { Moon(it) }
    var t = 0

    fun step() {
        for((a, b) in moons.combinations(2)) {
            val (x, y, z) = IntArray(3) { i ->
                a.pos[i].compareTo(b.pos[i])
            }
            val ch = Vec3(x, y, z)
            a.vel -= ch
            b.vel += ch
        }

        for(m in moons) {
            m.pos += m.vel
        }
        t++
    }

    fun step(n: Int) { repeat(n) { step() } }

    fun findCycle(): Long {
        val cycs = IntArray(3)
        var ok: Boolean
        do {
            step()
            ok = true
            for(i in 0 until 3) {
                if(cycs[i] != 0) continue
                ok = false
                if(moons.indices.all { j ->
                        val m = moons[j]
                        m.pos[i] == vecs[j][i] && m.vel[i] == 0
                    }) { cycs[i] = t }
            }
        } while(!ok)

        return cycs.fold(1L) { acc, i ->
            acc / gcd(acc, i.toLong()) * i
        }
    }

    val totalEnergy get() = moons.sumBy { it.energy }
}