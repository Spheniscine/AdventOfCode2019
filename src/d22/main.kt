package d22

import commons.*
import java.io.File
import kotlin.math.*

private val input by lazy { File("src/d22/input/gmail.in").readText() }
private val instructions by lazy { input.lines() }

fun main() {
    println("--- Day 22: Slam Shuffle ---")

    markTime()
    val ans1 = solve(2019, 10007, 1)
    println("Part 1: $ans1")
    printTime()

    markTime()
    val ans2 = solve(2020, 119315717514047, -101741582076661)
    println("Part 2: $ans2")
    printTime()
}

/**
 * x - input number
 * n - number of cards
 * k - iterations, positive = get position of card x, negative = get card in position x
 */
fun solve(x: Long, m: Long, k: Long): Long {
    // compose basis function
    // f(x) = ax + b
    var a = 1L
    var b = 0L

    for(ln in instructions) {
        when {
            ln == "deal into new stack" -> {
                // x → -x - 1; ax + b → -ax - b - 1
                a = -a umod m
                b = b.inv() umod m // b.not() = -b - 1
            }
            "cut" in ln -> {
                // x → x - i; ax + b → ax + b - i
                val i = ln.split(' ').last().toInt()
                b = b - i umod m
            }
            "deal with increment" in ln -> {
                // x → x · i; ax + b → aix + bi
                val i = ln.split(' ').last().toLong()
                a = a.mulMod(i, m)
                b = b.mulMod(i, m)
            }
            else -> error("Unrecognized instruction: $ln")
        }
    }

    // invert basis function. f^-1(x) = (a^-1)(x - b)
    if(k < 0) {
        a = a.powMod(m-2, m)
        b = a.mulMod(-b, m)
    }

    // start exponentiation for function, f^k(x) = cx + d
    var c = 1L
    var d = 0L
    var e = abs(k)

    // exponentiation by squaring. Equivalent to computing
    // ⌈ a 0 ⌉ k
    // ⌊ b 1 ⌋
    while(e > 0) {
        if(e and 1 == 1L) {
            // a(cx + d) + b = acx + (ad + b)
            c = a.mulMod(c, m)
            d = (a.mulMod(d, m) + b) % m
        }
        e = e shr 1
        b = (a.mulMod(b, m) + b) % m
        a = a.mulMod(a, m)
    }

    return (x.mulMod(c, m) + d) % m
}

fun Long.mulMod(other: Long, m: Long): Long {
    val a = this umod m
    val b = other umod m
    var hi = Math.multiplyHigh(a, b) shl 1
    var lo = a * b
    if(lo < 0) {
        hi = hi or 1
        lo = lo xor Long.MIN_VALUE
    }

    val res = hi.shl63Mod(m) + lo % m - m
    return (res shr Long.SIZE_BITS - 1 and m) + res
}

inline val Long.numLeadingZeroes get() = java.lang.Long.numberOfLeadingZeros(this)
private fun Long.shl63Mod(m: Long): Long {
    var a = this
    var remShift = 63
    if(m > Long.MIN_VALUE ushr 1) {
        do {
            val shift = min(remShift, a.numLeadingZeroes)
            a = a.shl(shift)
            while(a !in 0 until m) a -= m
            remShift -= shift
        } while (remShift > 0)
    } else {
        do {
            val shift = min(remShift, a.numLeadingZeroes - 1)
            a = a.shl(shift) % m
            remShift -= shift
        } while (remShift > 0)
    }
    return a
}

fun Long.powMod(exponent: Long, mod: Long): Long {
    if(exponent < 0) error("Inverse not implemented")
    var res = 1L
    var e = exponent
    var b = umod(mod)

    while(e > 0) {
        if(e and 1 == 1L) {
            res = res.mulMod(b, mod)
        }
        e = e shr 1
        b = b.mulMod(b, mod)
    }
    return res
}

/**
 * Naive shuffler
 */
fun doShuffle(n: Int, k: Int): IntArray {
    val deck = IntArray(n) { it }
    repeat(k) {
        for(ln in instructions) {
            when {
                ln == "deal into new stack" -> deck.reverse()
                "cut" in ln -> {
                    val arg = ln.split(' ').last().toInt()
                    val new = IntArray(n) { i ->
                        deck[i + arg umod n]
                    }
                    new.copyInto(deck)
                }
                "deal with increment" in ln -> {
                    val arg = ln.split(' ').last().toInt()
                    val new = IntArray(n)
                    var i = 0
                    repeat(n) { j ->
                        new[i] = deck[j]
                        i += arg
                        i %= n
                    }
                    new.copyInto(deck)
                }

                else -> error("Unrecognized instruction: $ln")
            }
        }
    }
    return deck
}