/*
 * Lincheck
 *
 * Copyright (C) 2019 - 2026 JetBrains s.r.o.
 *
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.jetbrains.lincheck_test.evaluation.common

import java.io.File

class EvalLogger(outputFile: File) {
    private val writer = outputFile.printWriter()

    init {
        writer.println(
            "benchmark,mode,run_kind,repetition,status,runtime_ms," +
                    "explored_schedules,failure_found," +
                    "switch_thread_count,stuck_count,idle_count," +
                    "await_count,cas_count,zne_count,unknown_count," +
                    "switch_by_bounded_period,switch_by_await,switch_by_cas,switch_by_zne,switch_by_unknown,switch_by_no_observations," +
                    "stuck_by_loop_bound,stuck_by_recursion_bound,stuck_by_adaptive_iteration_bound,stuck_by_abstract_state,stuck_by_no_switchable_thread," +
                    "loop_enter_count,loop_exit_count,loop_iteration_count,irreducible_loop_iteration_count,await_back_edge_hit_count,max_loop_iterations," +
                    "method_enter_count,method_exit_count,max_method_depth,recursion_bound_hit_count," +
                    "shared_read_count,shared_write_count,cas_success_count,cas_failure_count,relevant_external_write_count," +
                    "wait_set_size_sum,max_wait_set_size,abstract_state_visit_count,max_abstract_state_visits,signature_cycle_count,repeat_signature_count," +
                    "switch_request_count,successful_switch_count,failed_switch_count,enabled_threads_sum,max_enabled_threads," +
                    "incorrect_results_failure_count,livelock_failure_count,deadlock_failure_count,obstruction_freedom_failure_count," +
                    "runner_timeout_failure_count,internal_failure_count,exception_failure_count,trace_collection_count," +
                    "error_type,error_message,"
        )
        writer.flush()
    }

    fun append(result: EvalResult) {
        writer.println(
            listOf(
                result.benchmark,
                result.modeLabel,
                result.runKind,
                result.repetition,
                result.status,
                result.runtimeMs,

                result.exploredSchedules,
                result.failureFound,

                result.switchThreadCount,
                result.stuckCount,
                result.idleCount,

                result.awaitCount,
                result.casCount,
                result.zneCount,
                result.unknownCount,

                result.switchByBoundedPeriod,
                result.switchByAwait,
                result.switchByCas,
                result.switchByZne,
                result.switchByUnknown,
                result.switchByNoObservations,

                result.stuckByLoopBound,
                result.stuckByRecursionBound,
                result.stuckByAdaptiveIterationBound,
                result.stuckByAbstractState,
                result.stuckByNoSwitchableThread,

                result.loopEnterCount,
                result.loopExitCount,
                result.loopIterationCount,
                result.irreducibleLoopIterationCount,
                result.awaitBackEdgeHitCount,
                result.maxLoopIterations,

                result.methodEnterCount,
                result.methodExitCount,
                result.maxMethodDepth,
                result.recursionBoundHitCount,

                result.sharedReadCount,
                result.sharedWriteCount,
                result.casSuccessCount,
                result.casFailureCount,
                result.relevantExternalWriteCount,
                result.waitSetSizeSum,
                result.maxWaitSetSize,
                result.abstractStateVisitCount,
                result.maxAbstractStateVisits,
                result.signatureCycleCount,
                result.repeatSignatureCount,

                result.switchRequestCount,
                result.successfulSwitchCount,
                result.failedSwitchCount,
                result.enabledThreadsSum,
                result.maxEnabledThreads,

                result.incorrectResultsFailureCount,
                result.livelockFailureCount,
                result.deadlockFailureCount,
                result.obstructionFreedomFailureCount,
                result.runnerTimeoutFailureCount,
                result.internalFailureCount,
                result.exceptionFailureCount,
                result.traceCollectionCount,
                sanitize(result.errorType),
                sanitize(result.errorMessage),
            ).joinToString(",")
        )
        writer.flush()
    }

    fun close() {
        writer.close()
    }

    private fun sanitize(s: String): String =
        s.replace(",", ";")
            .replace("\n", " ")
            .replace("\r", " ")
}
