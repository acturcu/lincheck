package org.jetbrains.lincheck_test.evaluation.benchmarks.recursive

import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions
import java.util.concurrent.atomic.AtomicInteger

object RecursiveThroughObjectsNoProgressEvaluationTest : BenchmarkCase {
    override val name: String = "RecursiveThroughObjectsNoProgress"

    class Scenario {
        private val reads = AtomicInteger(0)
        private val first = Node(reads)
        private val second = Node(reads)
        private val third = Node(reads)

        init {
            first.next = second
            second.next = third
            third.next = first
        }

        @Operation
        fun enterFirst(): Int = first.enter(0)

        @Operation
        fun enterSecond(): Int = second.enter(0)

        @Operation
        fun update(): Int = reads.incrementAndGet()
    }

    private class Node(private val reads: AtomicInteger) {
        lateinit var next: Node

        fun enter(depth: Int): Int {
            reads.get()
            return next.enter(depth + 1)
        }
    }

    override fun runModelChecking() {
        actorOptions(
            EvalOptions.modelChecking(),
            Scenario::enterFirst,
            Scenario::enterSecond,
            Scenario::update
        ).check(Scenario::class)
    }
}
