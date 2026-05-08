/*
 * Lincheck
 *
 * Copyright (C) 2019 - 2026 JetBrains s.r.o.
 *
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.jetbrains.lincheck_test.evaluation.benchmarks

import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck_test.datastructures.Counter
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions

object CounterEvaluationTest : BenchmarkCase {
    override val name: String = "Counter"

    class Scenario {
        private val counter = Counter()

        @Operation
        fun add(): Int = counter.inc()

        @Operation
        fun get(): Int = counter.get()
    }

    override fun runModelChecking() {
        EvalOptions.applyCommonOptions(EvalOptions.modelChecking()).check(Scenario::class)
    }
}
