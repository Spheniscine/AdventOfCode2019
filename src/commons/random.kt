package commons

import java.security.SecureRandom
import kotlin.random.Random

val secureRandom = SecureRandom()
val _seed1 = secureRandom.generateSeed(8).toLong()
val _seed2 = secureRandom.generateSeed(8).toLong()

val random: Random = PCGRandom(_seed1, _seed2)

// http://www.pcg-random.org/
class PCGRandom(seed: Long, seq: Long): Random() {
    private var state = 0L
    private val inc = seq shl 1 or 1

    override fun nextBits(bitCount: Int): Int = nextInt().ushr(32 - bitCount) and (-bitCount).shr(31)

    override fun nextInt(): Int {
        val old = state
        state = old * 6364136223846793005 + inc
        val shift = old.ushr(18).xor(old).ushr(27).toInt()
        val rot = old.ushr(59).toInt()
        return shift.ushr(rot) or shift.shl(-rot)
    }

    init {
        nextInt()
        state += seed
        nextInt()
    }
}