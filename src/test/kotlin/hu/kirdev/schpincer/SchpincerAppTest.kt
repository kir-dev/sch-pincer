package hu.kirdev.schpincer

inline fun <reified T : Throwable> getException(runnable: Runnable) : T {
    try {
        runnable.run()
        throw IllegalStateException("No exception was thrown but expected: ${T::class.simpleName}")
    } catch(e: Throwable) {
        return if (e is T) e else throw IllegalStateException("Unexpected exception type: ${e.javaClass.simpleName} expected: ${T::class.simpleName}", e)
    }
}