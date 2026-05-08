package org.jetbrains.lincheck_test.evaluation.common

import org.jetbrains.kotlinx.lincheck_test.AbstractLincheckTest
import org.jetbrains.lincheck.datastructures.Options

abstract class AbstractEvaluationTest : AbstractLincheckTest() {
    override fun <O : Options<O, *>> O.customize() {
        actorsBefore(2)
        actorsAfter(2)
        threads(2)
        actorsPerThread(3)
    }
}
