package org.jetbrains.lincheck_test.evaluation.benchmarks

import org.jctools.queues.SpmcArrayQueue
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.scenario
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions

object SpmcArrayQueueAwaitEvaluationTest : BenchmarkCase {
    override val name: String = "SpmcArrayQueueAwait"

    class Scenario {
        private val queue = SpmcArrayQueue<Int>(4).apply {
            repeat(4) { offer(it) }
        }

        @Operation
        fun offer(): Boolean = queue.offer(4)

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
