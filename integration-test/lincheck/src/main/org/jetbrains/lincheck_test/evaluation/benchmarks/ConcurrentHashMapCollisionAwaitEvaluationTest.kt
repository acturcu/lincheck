package org.jetbrains.lincheck_test.evaluation.benchmarks

import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions
import java.util.concurrent.ConcurrentHashMap

/**
 * Exercises the linked-bin traversal loop in the JDK's ConcurrentHashMap.
 *
 * Six colliding entries keep the bin below the treeification threshold.
 * Looking up an absent colliding key traverses the existing map's internal
 * read-only retry path long enough to classify it as a relaxed await.
 */
object ConcurrentHashMapCollisionAwaitEvaluationTest : BenchmarkCase {
    override val name: String = "ConcurrentHashMapCollisionAwait"

    class Scenario {
        private val map = ConcurrentHashMap<CollidingKey, Int>().apply {
            repeat(6) { put(CollidingKey(it), it) }
        }

        @Operation
        fun getMissing(): Int? = map[CollidingKey(100)]

        @Operation
        fun containsMissing(): Boolean = map.containsKey(CollidingKey(101))
    }

    private data class CollidingKey(val value: Int) {
        override fun hashCode(): Int = 0
    }

    private fun modelCheckingOptions() =
        EvalOptions.applyCommonOptions(EvalOptions.modelChecking())
            .iterations(1)
            .invocationsPerIteration(1)
            .threads(2)
            .actorsBefore(0)
            .actorsAfter(0)
            .actorsPerThread(1)
            .analyzeStdLib(true)


    override fun runModelChecking() {
        modelCheckingOptions().check(Scenario::class)
    }
}
