package com.is0git.multicategorylayout.utils

infix fun Int.hasEnabled(flag: Int): Boolean {
    return (this and flag) > 0
}

infix fun Int.enable(flag: Int): Int {
    return this or flag
}

infix fun Int.disable(flag: Int): Int {
    return flag.inv().and(this)
}
