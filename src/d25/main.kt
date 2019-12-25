package d25

import commons.*
import d9.IntCodeVM
import java.io.File
import java.util.EnumMap

private val input by lazy { File("src/d25/input/gmail.in").readText() }

fun main() {
    println("--- Day 25: Cryostasis ---")

    markTime()
    val prog = input.split(',').map { it.toLong() }
    val vm = IntCodeVM(prog)

    val ai = SaintBernardAI(vm)
    ai.go()

    val ans1 = ai.ans
    println("Part 1: $ans1")
    printTime()
}

private val dirLabels by lazy {
    EnumMap<Dir2, String>(Dir2::class.java).apply {
        put(Dir2.North, "north")
        put(Dir2.South, "south")
        put(Dir2.West, "west")
        put(Dir2.East, "east")
    }
}

private val Dir2.label: String get() = dirLabels.getValue(this)
private const val SECURITY_ROOM_NAME = "Security Checkpoint"
private const val CHECK_ROOM_NAME = "Pressure-Sensitive Floor"
private val dangerousItems by lazy {
    File("src/d25/res/dangerous.txt").readLines().toCollection(StringHashSet())
}
private val passwordRegex by lazy { Regex("""\d+""")}

class SaintBernardAI(val vm: IntCodeVM) {
    val rooms = StringHashMap<Room>()
    val items = mutableListOf<String>()
    var ans = ""

    fun addRoom(room: Room) {
        rooms[room.name] = room
    }

    fun dfs(pickup: Boolean, prev: Pair<Dir2, Room>?): Room {
        vm.execute()
        val out = vm.outputToAscii().lines().filter { it.isNotBlank() }
        vm.output.clear()

        val name = out[0].removeSurrounding("== ", " ==")
        rooms[name]?.let { room ->
            prev?.let {(dir, neighbor) ->
                room.doors[dir] = neighbor
                room.path = neighbor.path + (-dir)
            }
            return room
        }
        val desc = out[1]
        val room = Room(name, desc)
        addRoom(room)

        prev?.let {(dir, neighbor) ->
            room.doors[dir] = neighbor
            room.path = neighbor.path + (-dir)
        }

        var i = 3
        loop@ while(i < out.size) {
            val ln = out[i++]
            when {
                ln[0] == '-' -> {
                    val dir = Dir2.fromChar(ln[2])
                    if(dir == prev?.first) continue@loop
                    val neighbor =
                        if(name == SECURITY_ROOM_NAME) Room(CHECK_ROOM_NAME, "Analyzing...").also { addRoom(it) }
                        else {
                            vm.inputAscii(dir.label)
                            dfs(pickup, -dir to room)
                        }

                    room.doors[dir] = neighbor
                }
                ln.startsWith("Items") -> {
                    while(i < out.size) {
                        @Suppress("NAME_SHADOWING")
                        val ln = out[i++]
                        if(ln[0] == '-') {
                            val item = ln.removePrefix("- ")
                            if(pickup) {
                                if(item !in dangerousItems) {
                                    vm.inputAscii("take $item")
                                    vm.execute()
                                    vm.output.clear()
                                    items.add(item)
                                }
                            } else items.add(item)
                        }
                        else break
                    }
                    break@loop
                }
            }
        }

        prev?.let {(dir, _) ->
            vm.inputAscii(dir.label)
            vm.execute()
            vm.output.clear()
        }

        return room
    }

    fun go() {
        dfs(true, null)

        // go to security checkpoint
        val securityRoom = rooms[SECURITY_ROOM_NAME]!!
        for (dir in securityRoom.path.toList()) {
            vm.inputAscii(dir.label)
        }
        vm.execute()
        vm.output.clear()

        // find direction to pressure sensitive floor
        val (dir: Dir2) = securityRoom.doors.entries.first { it.value.name == CHECK_ROOM_NAME }

        // bruteforce all 2^8 item combinations
        var currItems: List<String> = items
        for(comb in items.allCombinations()) {
            for(item in currItems) vm.inputAscii("drop $item")
            for(item in comb) vm.inputAscii("take $item")
            vm.execute()
            vm.output.clear()
            vm.inputAscii(dir.label)
            vm.execute()
            val out = vm.outputToAscii()
            currItems = comb
            if("heavier" in out || "lighter" in out) continue
            else {
                // found it! Parse Santa's answer
                ans = passwordRegex.find(out)!!.value
                break
            }
        }
    }
}

class Room(val name: String, val desc: String) {
    val doors = EnumMap<Dir2, Room>(Dir2::class.java)
    var path: PathNode<Dir2>? = null

    override fun toString(): String = name
}