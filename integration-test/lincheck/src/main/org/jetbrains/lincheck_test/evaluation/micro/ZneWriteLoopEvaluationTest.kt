/*
 * Lincheck
 *
 * Copyright (C) 2019 - 2026 JetBrains s.r.o.
 *
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.jetbrains.lincheck_test.evaluation.micro

import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions
import java.util.concurrent.atomic.AtomicBoolean

object ZneWriteLoopEvaluationTest : BenchmarkCase {
    override val name: String = "ZneWriteLoop"

    class Scenario {
        private val done = AtomicBoolean(false)
//        @Volatile
        private var sink = 0

        @Operation
        fun spinWrite(): Int {

            while (!done.get()) {
                sink = sink + 1
                sink = 0
            }
            return sink
        }

        @Operation
        fun release() {
            done.set(true)
        }
    }

    override fun runModelChecking() {
        EvalOptions.applyCommonOptions(EvalOptions.modelChecking()).check(Scenario::class)
    }
}
