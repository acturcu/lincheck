package org.jetbrains.lincheck_test.evaluation.benchmarks

import org.jctools.maps.ConcurrentAutoTable
import org.jetbrains.lincheck.datastructures.IntGen
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.Param
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions

object ConcurrentAutoTableEvaluationTest : BenchmarkCase {
    override val name: String = "ConcurrentAutoTable"

    @Param(name = "delta", gen = IntGen::class, conf = "1:3")
    class Scenario {
        private val counter = ConcurrentAutoTable()

        @Operation
        fun add(@Param(name = "delta") delta: Int) = counter.add(delta.toLong())

        @Operation
        fun get(): Long = counter.get()
    }

    override fun runModelChecking() {
        EvalOptions.applyCommonOptions(EvalOptions.modelChecking()).check(Scenario::class)
    }
}
