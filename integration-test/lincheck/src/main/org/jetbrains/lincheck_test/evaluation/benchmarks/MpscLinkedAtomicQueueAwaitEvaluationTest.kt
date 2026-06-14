package org.jetbrains.lincheck_test.evaluation.benchmarks

import org.jctools.queues.atomic.MpscLinkedAtomicQueue
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.scenario
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions

object MpscLinkedAtomicQueueAwaitEvaluationTest : BenchmarkCase {
    override val name: String = "MpscLinkedAtomicQueueAwait"

    class Scenario {
        private val queue = MpscLinkedAtomicQueue<Int>()

        @Operation
        fun offer(): Boolean = queue.offer(1)

        @Operation
        fun poll(): Int? = queue.poll()
    }

    override fun runModelChecking() {
        EvalOptions.modelChecking()
            .iterations(1)
            .invocationsPerIteration(200)
            .actorsBefore(0)
            .actorsAfter(0)
            .threads(2)
            .actorsPerThread(1)
            .addCustomScenario(
                scenario {
                    parallel {
                        thread { actor(Scenario::offer) }
                        thread { actor(Scenario::poll) }
                    }
                }
            )
            .check(Scenario::class)
    }
}
