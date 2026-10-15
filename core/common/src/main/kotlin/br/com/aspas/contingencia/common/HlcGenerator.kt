package br.com.aspas.contingencia.common

import java.util.concurrent.atomic.AtomicReference

class HlcGenerator(private val deviceSuffix: String, private val physicalClock: () -> Long = System::currentTimeMillis) {
    private val state = AtomicReference(Hlc(0L, 0, deviceSuffix))
    fun next(): Hlc {
        val now = physicalClock(); val prev = state.get()
        val next = if (now > prev.physical) Hlc(now, 0, deviceSuffix) else Hlc(prev.physical, prev.counter + 1, deviceSuffix)
        state.set(next); return next
    }
    fun observe(remote: Hlc): Hlc {
        val now = physicalClock(); val prev = state.get(); val maxPhysical = maxOf(now, prev.physical, remote.physical)
        val counter = when {
            maxPhysical == prev.physical && maxPhysical == remote.physical -> maxOf(prev.counter, remote.counter) + 1
            maxPhysical == prev.physical -> prev.counter + 1
            maxPhysical == remote.physical -> remote.counter + 1
            else -> 0
        }
        return Hlc(maxPhysical, counter, deviceSuffix).also(state::set)
    }
}
