package com.funsol.funmob_sdk.utils

import java.util.*

class NumberConverter {
    private val suffixes: NavigableMap<Long, String> = TreeMap()

    init {
        suffixes[1_000L] = "k+"
        suffixes[1_000_000L] = "M+"
        suffixes[1_000_000_000L] = "B+"
        suffixes[1_000_000_000_000L] = "T+"
    }

    public fun format(value: Long): String {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1)
        if (value < 0) return "-" + format(-value)
        if (value < 1000) return java.lang.Long.toString(value) //deal with easy case
        val (divideBy, suffix) = suffixes.floorEntry(value)
        val truncated = value / (divideBy / 10) //the number part of the output times 10
        val hasDecimal = truncated < 100 && truncated / 10.0 != (truncated / 10).toDouble()
        return if (hasDecimal) (truncated / 10.0).toString() + suffix else (truncated / 10).toString() + suffix
    }
}