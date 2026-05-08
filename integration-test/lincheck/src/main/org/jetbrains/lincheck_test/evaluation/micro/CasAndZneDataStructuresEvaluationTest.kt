package org.jetbrains.lincheck_test.evaluation.micro

import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.Options
import org.jetbrains.lincheck.datastructures.scenario
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KFunction

object CasAlwaysFailLoopEvaluationTest : BenchmarkCase {
    override val name: String = "CasAlwaysFailLoop"

    class Scenario {
        private val value = AtomicInteger(0)

        @Operation
        fun boundedFailedCas(): Int {
            var failures = 0
            while (failures < LOOP_ITERATIONS) {
                if (!value.compareAndSet(1, 2)) failures++
            }
            return failures
        }
    }

    override fun runModelChecking() {
        singleActorOptions(EvalOptions.modelChecking(), Scenario::boundedFailedCas).check(Scenario::class)
    }
}

object CasEventuallySuccessfulLoopEvaluationTest : BenchmarkCase {
    override val name: String = "CasEventuallySuccessfulLoop"

    class Scenario {
        private val value = AtomicInteger(0)

        @Operation
        fun boundedCasThenSuccess(): Int {
            var attempts = 0
            while (attempts < LOOP_ITERATIONS) {
                attempts++
                if (value.compareAndSet(1, 2)) return attempts
            }
            value.set(1)
            while (true) {
                attempts++
                if (value.compareAndSet(1, 2)) return attempts
            }
        }
    }

    override fun runModelChecking() {
        singleActorOptions(EvalOptions.modelChecking(), Scenario::boundedCasThenSuccess).check(Scenario::class)
    }
}

object ZneBooleanToggleLoopEvaluationTest : BenchmarkCase {
    override val name: String = "ZneBooleanToggleLoop"

    class Scenario {
        private var flag = false

        @Operation
        fun boundedToggle(): Boolean {
            var iterations = 0
            while (iterations < LOOP_ITERATIONS) {
                flag = true
                flag = false
                iterations++
            }
            return flag
        }
    }

    override fun runModelChecking() {
        singleActorOptions(EvalOptions.modelChecking(), Scenario::boundedToggle).check(Scenario::class)
    }
}

object ZneIntegerRestoreLoopEvaluationTest : BenchmarkCase {
    override val name: String = "ZneIntegerRestoreLoop"

    class Scenario {
        private var value = 0

        @Operation
        fun boundedRestore(): Int {
            var iterations = 0
            while (iterations < LOOP_ITERATIONS) {
                value = 1
                value = 0
                iterations++
            }
            return value
        }
    }

    override fun runModelChecking() {
        singleActorOptions(EvalOptions.modelChecking(), Scenario::boundedRestore).check(Scenario::class)
    }
}

private const val LOOP_ITERATIONS = 8

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
