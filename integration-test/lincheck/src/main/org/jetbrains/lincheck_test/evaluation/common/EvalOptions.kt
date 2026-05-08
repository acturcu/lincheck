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

import org.jetbrains.lincheck.datastructures.ModelCheckingOptions
import org.jetbrains.lincheck.datastructures.Options

object EvalOptions {
    fun modelChecking(): ModelCheckingOptions {
        return ModelCheckingOptions()
        // Keep this shared across BASELINE / BOUNDED / ADAPTIVE runs.
        // Detector mode should be selected manually in ManagedStrategy.
    }

    @Suppress("UNCHECKED_CAST")
    fun <O : Options<O, *>> applyCommonOptions(options: O): O {
        return options
            .actorsBefore(2)
            .actorsAfter(2)
            .threads(2)
            .actorsPerThread(3)
        // Add shared settings here later if you want:
        // .iterations(...)
        // .threads(...)
        // .actorsPerThread(...)
        // .invocationsPerIteration(...)
    }
}