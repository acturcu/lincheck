package org.jetbrains.lincheck_test.evaluation.variable

import org.jetbrains.lincheck.datastructures.IntGen
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.Param
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import java.util.concurrent.ConcurrentLinkedDeque

object ConcurrentLinkedDequeChurnEvaluationTest : BenchmarkCase {
    override val name: String = "VariableConcurrentLinkedDequeChurn"

    @Param(name = "value", gen = IntGen::class, conf = "1:4")
    class Scenario {
        private val deque = ConcurrentLinkedDeque<Int>()

        @Operation
        fun addFirst(@Param(name = "value") value: Int) {
            deque.addFirst(value)
        }

        @Operation
        fun addLast(@Param(name = "value") value: Int) {
            deque.addLast(value)
        }

        @Operation
        fun pollFirst(): Int? = deque.pollFirst()

        @Operation
        fun pollLast(): Int? = deque.pollLast()

        @Operation
        fun peekFirst(): Int? = deque.peekFirst()

        @Operation
        fun peekLast(): Int? = deque.peekLast()

        @Operation
        fun removeFirstOccurrence(@Param(name = "value") value: Int): Boolean =
            deque.removeFirstOccurrence(value)
    }

    override fun runModelChecking() {
        VariableEvalOptions.modelChecking(invocationsPerIteration = 5_000, analyzeStdLib = true).check(Scenario::class)
    }
}
