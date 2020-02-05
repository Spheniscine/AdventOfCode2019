package commons

fun Regex.capture(string: String) = find(string)?.destructured

inline fun <R> get(block: GetScope<R>.() -> Nothing): R = GetScope<R>().invoke(block)

open class GetScope<in R> {
    class Result @Deprecated("Use yield function") constructor(val scope: GetScope<*>, val data: Any?):
        Throwable(null, null, true, false)
    @Suppress("DEPRECATION")
    fun yield(result: R): Nothing { throw Result(this, result) }
}

inline fun GetScope<Unit>.yield(): Nothing = yield(Unit)

inline operator fun <R, G: GetScope<R>> G.invoke(block: G.() -> R): R =
    try { block() }
    catch (r: GetScope.Result) {
        if(r.scope !== this) throw r
        @Suppress("UNCHECKED_CAST")
        r.data as R
    }


inline fun <R> regexWhen(string: String, block: RegexWhen<R>.() -> Nothing): R = RegexWhen<R>(string).invoke(block)

class RegexWhen<in R>(val regexWhenArg: String): GetScope<R>() {
    inline infix fun Regex.then(block: (MatchResult.Destructured) -> R) { capture(regexWhenArg)?.let { yield(block(it)) } }
    fun error(): Nothing = error("Unparsed string: $regexWhenArg")
}