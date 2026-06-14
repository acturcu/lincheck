package org.jetbrains.lincheck_test.evaluation.benchmarks

import org.jctools.queues.MpscUnboundedXaddArrayQueue
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.scenario
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions

object MpscUnboundedXaddArrayQueueAwaitEvaluationTest : BenchmarkCase {
    override val name: String = "MpscUnboundedXaddArrayQueueAwait"

    class Scenario {
        private val queue = MpscUnboundedXaddArrayQueue<Int>(2).apply {
            offer(0)
            offer(1)
            check(poll() == 0)
            check(poll() == 1)
        }

        @Operation
        fun offer(): Boolean = queue.offer(2)

        @Operation
        fun poll(): Int? = queue.poll()
    }

    override fun runModelChecking() {
        EvalOptions.modelChecking()
            .iterations(1)
            .invocationsPerIteration(20)
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
