package org.jetbrains.lincheck_test.evaluation.micro

import org.jetbrains.lincheck.datastructures.Options
import org.jetbrains.lincheck.datastructures.scenario
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions
import kotlin.reflect.KFunction

internal const val LOOP_ITERATIONS = 8

internal fun <O : Options<O, *>> singleActorOptions(options: O, operation: KFunction<*>): O =
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
