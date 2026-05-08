package org.jetbrains.lincheck_test.evaluation.benchmarks

import org.jetbrains.lincheck.datastructures.IntGen
import org.jetbrains.lincheck.datastructures.ModelCheckingOptions
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.Param
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions
import java.util.concurrent.ConcurrentHashMap

object ConcurrentHashMapEvaluationTest : BenchmarkCase {
    override val name: String = "ConcurrentHashMap"

    @Param(name = "key", gen = IntGen::class, conf = "1:3")
    @Param(name = "value", gen = IntGen::class, conf = "1:3")
    class Scenario {
        private val map = ConcurrentHashMap<Int, Int>()

        @Operation
        fun put(@Param(name = "key") key: Int, @Param(name = "value") value: Int): Int? =
            map.put(key, value)

        @Operation
        fun get(@Param(name = "key") key: Int): Int? = map[key]

        @Operation
        fun remove(@Param(name = "key") key: Int): Int? = map.remove(key)

        @Operation
        fun containsKey(@Param(name = "key") key: Int): Boolean = map.containsKey(key)
    }

    private fun modelCheckingOptions() =
        EvalOptions.applyCommonOptions(EvalOptions.modelChecking())
            .threads(3)
            .also { (it as ModelCheckingOptions).analyzeStdLib(true) }


    override fun runModelChecking() {
        modelCheckingOptions().check(Scenario::class)
    }
}
