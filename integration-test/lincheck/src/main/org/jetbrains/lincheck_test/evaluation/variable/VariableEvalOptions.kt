package org.jetbrains.lincheck_test.evaluation.variable

import org.jetbrains.lincheck.datastructures.ModelCheckingOptions
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions

internal object VariableEvalOptions {
    fun modelChecking(
        threads: Int = 3,
        invocationsPerIteration: Int = 10_000,
        analyzeStdLib: Boolean = false
    ): ModelCheckingOptions =
        EvalOptions.applyCommonOptions(EvalOptions.modelChecking())
            .threads(threads)
            .actorsBefore(2)
            .actorsAfter(2)
            .actorsPerThread(3)
            .invocationsPerIteration(invocationsPerIteration)
            .also { if (analyzeStdLib) it.analyzeStdLib(true) }
}
