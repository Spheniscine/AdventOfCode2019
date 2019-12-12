package d2

import commons.*
import java.io.File

private val input by lazy { File("src/d2/input/gmail.in").readText() }

fun main() {
//    input.split(",").chunked(4).forEachIndexed {index, list ->
//        println("${index * 4} $list")
//    }
    markTime()
    val inp = input.split(',').map { it.toLong() }

    var vm = IntCodeVM(inp)
    vm.mem[1] = 12
    vm.mem[2] = 2
    vm.execute()
    val ans1 = vm.mem[0]
    println("Part 1: $ans1")
    printTime()

    markTime()
    val ans2 = run {
        for(i in 0..99) {
            for(j in 0..99) {
                vm = IntCodeVM(inp)
                vm.mem[1] = i.toLong()
                vm.mem[2] = j.toLong()
                vm.execute()
                if(vm.mem[0] == 19690720L) {
                    return@run i * 100 + j
                }
            }
        }
        null
    }
    println("Part 2: $ans2")
    printTime()
}



