package org.jetbrains.lincheck_test.evaluation.benchmarks

import org.jetbrains.lincheck.datastructures.IntGen
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.Param
import org.jetbrains.lincheck.datastructures.verifier.QuiescentConsistent
import org.jetbrains.lincheck.datastructures.verifier.QuiescentConsistencyVerifier
import org.jetbrains.lincheck_test.datastructures.LockFreeTaskQueue
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase

object LockFreeTaskQueueMultiConsumerEvaluationTest : BenchmarkCase {
    override val name: String = "VariableLockFreeTaskQueueMultiConsumer"

    @Param(name = "value", gen = IntGen::class, conf = "1:5")
    class Scenario {
        private val queue = LockFreeTaskQueue<Int>(singleConsumer = false)

        @Operation
        fun addLast(@Param(name = "value") value: Int): Boolean =
            queue.addLast(value)

        @QuiescentConsistent
        @Operation
        fun removeFirstOrNull(): Int? =
            queue.removeFirstOrNull()

        @Operation
        fun close() {
            queue.close()
        }
    }

    override fun runModelChecking() {
        VariableEvalOptions.modelChecking()
            .verifier(QuiescentConsistencyVerifier::class.java)
            .check(Scenario::class)
    }
}
