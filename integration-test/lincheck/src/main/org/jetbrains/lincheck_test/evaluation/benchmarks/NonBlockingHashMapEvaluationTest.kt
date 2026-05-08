package org.jetbrains.lincheck_test.evaluation.benchmarks

import org.jctools.maps.NonBlockingHashMap
import org.jetbrains.lincheck.datastructures.IntGen
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.Param
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions

object NonBlockingHashMapEvaluationTest : BenchmarkCase {
    override val name: String = "NonBlockingHashMap"

    @Param(name = "key", gen = IntGen::class, conf = "1:3")
    @Param(name = "value", gen = IntGen::class, conf = "1:3")
    class Scenario {
        private val map = NonBlockingHashMap<Int, Int>()

        @Operation
        fun put(@Param(name = "key") key: Int, @Param(name = "value") value: Int): Int? =
            map.put(key, value)

        @Operation
        fun get(@Param(name = "key") key: Int): Int? = map[key]

        @Operation
        fun remove(@Param(name = "key") key: Int): Int? = map.remove(key)

        @Operation
        fun putIfAbsent(@Param(name = "key") key: Int, @Param(name = "value") value: Int): Int? =
            map.putIfAbsent(key, value)
    }

    private fun modelCheckingOptions() =
        EvalOptions.applyCommonOptions(EvalOptions.modelChecking())
            .threads(3)


    override fun runModelChecking() {
        modelCheckingOptions().check(Scenario::class)
    }
}
