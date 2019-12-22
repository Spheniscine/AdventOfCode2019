package d22

import commons.*
import java.io.File
import java.math.BigInteger
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
 * k - iterations, positive = get position of card, negative = get card in position
 */
fun solve(x: Long, n: Long, k: Long): Long {
    var a: BigInteger = BigInteger.ONE
    var b: BigInteger = BigInteger.ZERO

    val m = n.toBigInteger()

    for(ln in instructions) {
        when {
            ln == "deal into new stack" -> {
                a = (-a).mod(m)
                b = b.not().mod(m) // b.not() = -(b + 1)
            }
            "cut" in ln -> {
                val arg = ln.split(' ').last().toBigInteger()
                b = (b - arg).mod(m)
            }
            "deal with increment" in ln -> {
                val arg = ln.split(' ').last().toBigInteger()
                a = a * arg % m
                b = b * arg % m
            }
            else -> error("Unrecognized instruction: $ln")
        }
    }

    if(k < 0) {
        a = a.modInverse(m)
        b = (-b * a).mod(m)
    }

    var c: BigInteger = BigInteger.ONE
    var d: BigInteger = BigInteger.ZERO
    var e = abs(k)

    while(e > 0) {
        if(e and 1 == 1L) {
            c = a * c % m
            d = (a * d + b) % m
        }
        e = e shr 1
        b = (a * b + b) % m
        a = a * a % m
    }

    return (x.toBigInteger() * c + d).mod(m).toLong()
}

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