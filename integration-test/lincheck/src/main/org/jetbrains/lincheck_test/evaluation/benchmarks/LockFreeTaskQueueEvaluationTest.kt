package org.jetbrains.lincheck_test.evaluation.benchmarks

import org.jetbrains.lincheck.datastructures.IntGen
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.Param
import org.jetbrains.lincheck.datastructures.verifier.QuiescentConsistent
import org.jetbrains.lincheck.datastructures.verifier.QuiescentConsistencyVerifier
import org.jetbrains.lincheck_test.datastructures.LockFreeTaskQueue
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions

object LockFreeTaskQueueEvaluationTest : BenchmarkCase {
    override val name: String = "LockFreeTaskQueue"

    @Param(name = "value", gen = IntGen::class, conf = "1:3")
    class Scenario {
        private val q = LockFreeTaskQueue<Int>(true)

        @Operation
        fun addLast(@Param(name = "value") value: Int) = q.addLast(value)

        @QuiescentConsistent
        @Operation(nonParallelGroup = "consumer")
        fun removeFirstOrNull(): Int? = q.removeFirstOrNull()

        @Operation
        fun close() = q.close()
    }

    private fun modelCheckingOptions() =
        EvalOptions.applyCommonOptions(EvalOptions.modelChecking())
            .verifier(QuiescentConsistencyVerifier::class.java)


    override fun runModelChecking() {
        modelCheckingOptions().check(Scenario::class)
    }
}
