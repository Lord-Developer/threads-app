package nad1r.techie

fun Boolean.runIfTrue(func: () -> Unit) {
    if (this) func()
}