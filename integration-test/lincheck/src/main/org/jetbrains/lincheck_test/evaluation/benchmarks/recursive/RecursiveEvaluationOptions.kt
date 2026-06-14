package org.jetbrains.lincheck_test.evaluation.benchmarks.recursive

import org.jetbrains.lincheck.datastructures.Options
import org.jetbrains.lincheck.datastructures.scenario
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions
import kotlin.reflect.KFunction

internal fun <O : Options<O, *>> singleActorOptions(options: O, operation: KFunction<*>): O =
    actorOptions(options, operation)

internal fun <O : Options<O, *>> actorOptions(options: O, vararg operations: KFunction<*>): O =
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
