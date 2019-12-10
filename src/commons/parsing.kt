package commons

fun Regex.capture(string: String) = find(string)?.destructured

inline fun <R> regexWhen(string: String, block: RegexWhen.() -> R) = RegexWhen(string).block()

inline class RegexWhen(val regexWhenArg: String) {
    /** Use `return@regexWhen null` to return null to the expression */
    inline fun <R: Any> Regex.then(block: (MatchResult.Destructured) -> R) = capture(regexWhenArg)?.let(block)
    fun error(): Nothing = error("Unparsed string: $regexWhenArg")
}