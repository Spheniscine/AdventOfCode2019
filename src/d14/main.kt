package d14

import commons.*
import java.io.File

private val input by lazy { File("src/d14/input/gmail.in").readText() }

fun main() {
    println("--- Day 14: Space Stoichiometry ---")

    markTime()
    val reactions = StringHashMap<Reaction>()

    for(ln in input.lineSequence()) {
        val (ps, rs) = ln.split(" => ")
        val reaction = Reaction (
            precursors = ps.split(", ").map { it.parseQuantity() },
            result = rs.parseQuantity()
        )
        reactions[reaction.result.material] = reaction
    }

    fun makeFuel(amount: Long): Long {
        val rem = StringHashMap<Long>().default(0)
        var oreReq = 0L

        fun requisition(amount: Long, material: String) {
            if(material == "ORE") {
                oreReq += amount
                return
            }
            val req = amount - rem[material]
            if(req > 0) {
                val reaction = reactions[material]!!
                val m = req divCeil reaction.result.amount.toLong()
                for(precursor in reaction.precursors) {
                    requisition(precursor.amount * m, precursor.material)
                }
                rem[material] += m * reaction.result.amount
            }

            rem[material] -= amount
        }

        requisition(amount, "FUEL")
        return oreReq
    }

    val ans1 = makeFuel(1)
    println("Part 1: $ans1")
    printTime()

    markTime()
    val ans2 = (1..TRILLION).bsLast { makeFuel(it) <= TRILLION }
    println("Part 2: $ans2")
    printTime()
}

const val TRILLION = 1000000000000

data class Quantity(val amount: Int, val material: String)
data class Reaction(val precursors: List<Quantity>, val result: Quantity)
private fun String.parseQuantity() = split(' ').let { (q, m) -> Quantity(q.toInt(), m) }