package commons

/**
 * Trie-like node that maintains a pointer to its parent (or null if it's a root node)
 * Best used for path-searching algorithms that need to link several possibilities to a tail node (O(1)),
 * but only needs to retrieve the entire path once at completion (O(n))
 */

class PathNode<T>(val data: T, val parent: PathNode<T>? = null) {
    operator fun plus(childData: T) = PathNode(childData, this)

    fun toList() = sequence {
        var node = this@PathNode
        while(true) {
            yield(node.data)
            node = node.parent ?: break
        }
    }.toList().asReversed()
}

operator fun <T> PathNode<T>?.plus(childData: T) = PathNode(childData, this)
fun <T> PathNode<T>?.toList() = this?.toList().orEmpty()