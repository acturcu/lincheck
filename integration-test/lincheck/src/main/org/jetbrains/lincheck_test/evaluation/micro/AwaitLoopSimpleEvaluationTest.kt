package org.jetbrains.lincheck_test.evaluation.micro

import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.scenario
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions
import java.util.concurrent.atomic.AtomicBoolean

object AwaitLoopSimpleEvaluationTest : BenchmarkCase {
    override val name: String = "AwaitLoopSimple"

    class Scenario {
        private val flag = AtomicBoolean(false)

        @Operation
        fun waitUntilReady(): Boolean {
            val a = "a"
            while (!flag.get()) {
                a.toString()
                Thread.onSpinWait()
            }
            return true
        }

//        @Operation (runOnce = true)

        @Operation
        fun release() {
            flag.set(true)
        }
    }

    private fun modelCheckingOptions() =
        EvalOptions.applyCommonOptions(EvalOptions.modelChecking())
            .actorsBefore(0)
            .actorsAfter(0)
            .threads(3)
            .actorsPerThread(1)
            .addCustomScenario(
                scenario {
                    parallel {
                        thread { actor(Scenario::waitUntilReady) }
                        thread { actor(Scenario::waitUntilReady) }
                        thread { actor(Scenario::release) }
                    }
                }
            )


    override fun runModelChecking() {
        EvalOptions.applyCommonOptions(EvalOptions.modelChecking()).check(Scenario::class)
//        modelCheckingOptions().check(Scenario::class)
    }
}
