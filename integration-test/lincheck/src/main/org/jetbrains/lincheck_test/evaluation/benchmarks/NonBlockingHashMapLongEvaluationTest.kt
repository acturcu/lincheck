package org.jetbrains.lincheck_test.evaluation.benchmarks

import org.jctools.maps.NonBlockingHashMapLong
import org.jetbrains.lincheck.datastructures.IntGen
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.Param
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions

object NonBlockingHashMapLongEvaluationTest : BenchmarkCase {
    override val name: String = "NonBlockingHashMapLong"

    @Param(name = "key", gen = IntGen::class, conf = "1:3")
    @Param(name = "value", gen = IntGen::class, conf = "1:3")
    class Scenario {
        private val map = NonBlockingHashMapLong<Int>()

        @Operation
        fun put(@Param(name = "key") key: Int, @Param(name = "value") value: Int): Int? =
            map.put(key.toLong(), value)

        @Operation
        fun get(@Param(name = "key") key: Int): Int? = map.get(key.toLong())

        @Operation
        fun remove(@Param(name = "key") key: Int): Int? = map.remove(key.toLong())
    }

    override fun runModelChecking() {
        EvalOptions.applyCommonOptions(EvalOptions.modelChecking()).check(Scenario::class)
    }
}
