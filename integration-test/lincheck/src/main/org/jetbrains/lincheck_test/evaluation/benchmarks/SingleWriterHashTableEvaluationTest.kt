package org.jetbrains.lincheck_test.evaluation.benchmarks

import org.jetbrains.lincheck.datastructures.IntGen
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.Param
import org.jetbrains.lincheck_test.datastructures.SingleWriterHashTable
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import org.jetbrains.lincheck_test.evaluation.common.EvalOptions

object SingleWriterHashTableEvaluationTest : BenchmarkCase {
    override val name: String = "SingleWriterHashTable"

    @Param(name = "key", gen = IntGen::class, conf = "1:3")
    @Param(name = "value", gen = IntGen::class, conf = "1:3")
    class Scenario {
        private val table = SingleWriterHashTable<Int, Int>(2)

        @Operation(nonParallelGroup = "writer")
        fun put(@Param(name = "key") key: Int, @Param(name = "value") value: Int): Int? =
            table.put(key, value)

        @Operation
        fun get(@Param(name = "key") key: Int): Int? = table.get(key)

        @Operation(nonParallelGroup = "writer")
        fun remove(@Param(name = "key") key: Int): Int? = table.remove(key)
    }

    override fun runModelChecking() {
        EvalOptions.applyCommonOptions(EvalOptions.modelChecking()).check(Scenario::class)
    }
}
