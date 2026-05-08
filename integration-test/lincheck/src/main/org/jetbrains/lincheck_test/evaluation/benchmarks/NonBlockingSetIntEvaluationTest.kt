package org.jetbrains.lincheck_test.evaluation.benchmarks

import org.jctools.maps.NonBlockingSetInt
import org.jetbrains.lincheck.datastructures.IntGen
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.Param
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions

object NonBlockingSetIntEvaluationTest : BenchmarkCase {
    override val name: String = "NonBlockingSetInt"

    @Param(name = "value", gen = IntGen::class, conf = "1:3")
    class Scenario {
        private val set = NonBlockingSetInt()

        @Operation
        fun add(@Param(name = "value") value: Int): Boolean = set.add(value)

        @Operation
        fun contains(@Param(name = "value") value: Int): Boolean = set.contains(value)

        @Operation
        fun remove(@Param(name = "value") value: Int): Boolean = set.remove(value)
    }

    private fun modelCheckingOptions() =
        EvalOptions.applyCommonOptions(EvalOptions.modelChecking())
            .threads(3)


    override fun runModelChecking() {
        modelCheckingOptions().check(Scenario::class)
    }
}
