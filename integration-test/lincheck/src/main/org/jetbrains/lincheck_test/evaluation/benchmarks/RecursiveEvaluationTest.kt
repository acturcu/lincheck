package org.jetbrains.lincheck_test.evaluation.benchmarks

import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.Options
import org.jetbrains.lincheck.datastructures.scenario
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KFunction

object DirectRecursiveNoProgressEvaluationTest : BenchmarkCase {
    override val name: String = "DirectRecursiveNoProgress"

    class Scenario {
        @Operation
        fun recurse(): Int = recurse0(0)

        private fun recurse0(depth: Int): Int = recurse0(depth + 1)
    }

    override fun runModelChecking() {
        singleActorOptions(EvalOptions.modelChecking(), Scenario::recurse).check(Scenario::class)
    }
}

object MutualRecursiveNoProgressEvaluationTest : BenchmarkCase {
    override val name: String = "MutualRecursiveNoProgress"

    class Scenario {
        @Operation
        fun recurse(): Int = recurseA(0)

        private fun recurseA(depth: Int): Int = recurseB(depth + 1)

        private fun recurseB(depth: Int): Int = recurseA(depth + 1)
    }

    override fun runModelChecking() {
        singleActorOptions(EvalOptions.modelChecking(), Scenario::recurse).check(Scenario::class)
    }
}

object ConcurrentDirectRecursiveNoProgressEvaluationTest : BenchmarkCase {
    override val name: String = "ConcurrentDirectRecursiveNoProgress"

    class Scenario {
        @Operation
        fun recurseLeft(): Int = recurseLeft0(0)

        @Operation
        fun recurseRight(): Int = recurseRight0(0)

        private fun recurseLeft0(depth: Int): Int = recurseLeft0(depth + 1)

        private fun recurseRight0(depth: Int): Int = recurseRight0(depth + 1)
    }

    override fun runModelChecking() {
        twoActorOptions(
            EvalOptions.modelChecking(),
            Scenario::recurseLeft,
            Scenario::recurseRight
        ).check(Scenario::class)
    }
}

object ConcurrentRecursiveWithSharedReadEvaluationTest : BenchmarkCase {
    override val name: String = "ConcurrentRecursiveWithSharedRead"

    class Scenario {
        private val value = AtomicInteger(0)

        @Operation
        fun recurseReader(): Int = recurseReader0(0)

        @Operation
        fun update(): Int = value.incrementAndGet()

        private fun recurseReader0(depth: Int): Int {
            value.get()
            return recurseReader0(depth + 1)
        }
    }

    override fun runModelChecking() {
        twoActorOptions(
            EvalOptions.modelChecking(),
            Scenario::recurseReader,
            Scenario::update
        ).check(Scenario::class)
    }
}

object ThreeThreadRecursiveNoProgressEvaluationTest : BenchmarkCase {
    override val name: String = "ThreeThreadRecursiveNoProgress"

    class Scenario {
        private val counter = AtomicInteger(0)

        @Operation
        fun recurseA(): Int = recurseA0(0)

        @Operation
        fun recurseB(): Int = recurseB0(0)

        @Operation
        fun recurseC(): Int = recurseC0(0)

        private fun recurseA0(depth: Int): Int {
            counter.get()
            return recurseA0(depth + 1)
        }

        private fun recurseB0(depth: Int): Int {
            counter.get()
            return recurseB0(depth + 1)
        }

        private fun recurseC0(depth: Int): Int {
            counter.get()
            return recurseC0(depth + 1)
        }
    }

    override fun runModelChecking() {
        manyActorOptions(
            EvalOptions.modelChecking(),
            Scenario::recurseA,
            Scenario::recurseB,
            Scenario::recurseC
        ).check(Scenario::class)
    }
}

object FourThreadMutualRecursiveNoProgressEvaluationTest : BenchmarkCase {
    override val name: String = "FourThreadMutualRecursiveNoProgress"

    class Scenario {
        private val state = AtomicInteger(0)

        @Operation
        fun startA(): Int = recurseA(0)

        @Operation
        fun startB(): Int = recurseB(0)

        @Operation
        fun startC(): Int = recurseC(0)

        @Operation
        fun update(): Int = state.incrementAndGet()

        private fun recurseA(depth: Int): Int {
            state.get()
            return recurseB(depth + 1)
        }

        private fun recurseB(depth: Int): Int {
            state.get()
            return recurseC(depth + 1)
        }

        private fun recurseC(depth: Int): Int {
            state.get()
            return recurseA(depth + 1)
        }
    }

    override fun runModelChecking() {
        manyActorOptions(
            EvalOptions.modelChecking(),
            Scenario::startA,
            Scenario::startB,
            Scenario::startC,
            Scenario::update
        ).check(Scenario::class)
    }
}

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
        manyActorOptions(
            EvalOptions.modelChecking(),
            Scenario::enterFirst,
            Scenario::enterSecond,
            Scenario::update
        ).check(Scenario::class)
    }
}

object BranchingRecursiveNoProgressEvaluationTest : BenchmarkCase {
    override val name: String = "BranchingRecursiveNoProgress"

    class Scenario {
        private val selector = AtomicInteger(0)

        @Operation
        fun recurseBranching(): Int = root(0)

        @Operation
        fun flipSelector(): Int = selector.incrementAndGet()

        private fun root(depth: Int): Int {
            return if ((selector.get() and 1) == 0) {
                left(depth + 1)
            } else {
                right(depth + 1)
            }
        }

        private fun left(depth: Int): Int = root(depth + 1)

        private fun right(depth: Int): Int = root(depth + 1)
    }

    override fun runModelChecking() {
        twoActorOptions(
            EvalOptions.modelChecking(),
            Scenario::recurseBranching,
            Scenario::flipSelector
        ).check(Scenario::class)
    }
}

private fun <O : Options<O, *>> singleActorOptions(options: O, operation: KFunction<*>): O =
    EvalOptions.applyCommonOptions(options)
        .actorsBefore(0)
        .actorsAfter(0)
        .threads(1)
        .actorsPerThread(1)
        .addCustomScenario(
            scenario {
                parallel {
                    thread { actor(operation) }
                }
            }
        )

private fun <O : Options<O, *>> manyActorOptions(options: O, vararg operations: KFunction<*>): O =
    EvalOptions.applyCommonOptions(options)
        .actorsBefore(0)
        .actorsAfter(0)
        .threads(operations.size)
        .actorsPerThread(1)
        .addCustomScenario(
            scenario {
                parallel {
                    operations.forEach { operation ->
                        thread { actor(operation) }
                    }
                }
            }
        )

private fun <O : Options<O, *>> twoActorOptions(
    options: O,
    firstOperation: KFunction<*>,
    secondOperation: KFunction<*>
): O =
    EvalOptions.applyCommonOptions(options)
        .actorsBefore(0)
        .actorsAfter(0)
        .threads(2)
        .actorsPerThread(1)
        .addCustomScenario(
            scenario {
                parallel {
                    thread { actor(firstOperation) }
                    thread { actor(secondOperation) }
                }
            }
        )
