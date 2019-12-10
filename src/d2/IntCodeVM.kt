package d2

import commons.coerceToInt

class IntCodeVM(program: List<Long>) {
    val mem = program.toLongArray()

    enum class Status { Continue, Halt, Error }

    var ip = 0

    fun address(i: Int) = mem[mem[i].coerceToInt()]

    fun setAddress(i: Int, v: Long) { mem[mem[i].coerceToInt()] = v }

    fun step(): Status {
        val code = mem[ip]
        when(code) {
            1L -> {
                setAddress(ip+3, address(ip+1) + address(ip+2))
            }
            2L -> {
                setAddress(ip+3, address(ip+1) * address(ip+2))
            }
            99L -> return Status.Halt
            else -> return Status.Error
        }

        ip += 4
        return Status.Continue
    }

    fun execute() { while(step() == Status.Continue) {} }
}