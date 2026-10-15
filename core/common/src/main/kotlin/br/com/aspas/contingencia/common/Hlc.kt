package br.com.aspas.contingencia.common

data class Hlc(val physical: Long, val counter: Int, val deviceSuffix: String) : Comparable<Hlc> {
    fun encode(): String = "$physical-$counter-$deviceSuffix"
    override fun compareTo(other: Hlc): Int {
        val p = physical.compareTo(other.physical); if (p != 0) return p
        val c = counter.compareTo(other.counter); if (c != 0) return c
        return deviceSuffix.compareTo(other.deviceSuffix)
    }
    companion object {
        fun decode(s: String): Hlc {
            val parts = s.split("-", limit = 3)
            require(parts.size == 3) { "Invalid HLC: $s" }
            return Hlc(parts[0].toLong(), parts[1].toInt(), parts[2])
        }
    }
}
