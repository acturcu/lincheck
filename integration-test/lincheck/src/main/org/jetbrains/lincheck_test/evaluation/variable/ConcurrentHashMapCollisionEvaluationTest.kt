package org.jetbrains.lincheck_test.evaluation.variable

import org.jetbrains.lincheck.datastructures.IntGen
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.Param
import org.jetbrains.lincheck_test.evaluation.common.BenchmarkCase
import java.util.concurrent.ConcurrentHashMap

object ConcurrentHashMapCollisionEvaluationTest : BenchmarkCase {
    override val name: String = "VariableConcurrentHashMapCollision"

    @Param(name = "key", gen = IntGen::class, conf = "1:8")
    @Param(name = "value", gen = IntGen::class, conf = "1:4")
    class Scenario {
        private val map = ConcurrentHashMap<BadHashKey, Int>()

        @Operation
        fun put(@Param(name = "key") key: Int, @Param(name = "value") value: Int): Int? =
            map.put(BadHashKey(key), value)

        @Operation
        fun putIfAbsent(@Param(name = "key") key: Int, @Param(name = "value") value: Int): Int? =
            map.putIfAbsent(BadHashKey(key), value)

        @Operation
        fun merge(@Param(name = "key") key: Int, @Param(name = "value") value: Int): Int? =
            map.merge(BadHashKey(key), value, Int::plus)

        @Operation
        fun remove(@Param(name = "key") key: Int): Int? =
            map.remove(BadHashKey(key))

        @Operation
        fun get(@Param(name = "key") key: Int): Int? =
            map[BadHashKey(key)]

        @Operation
        fun containsKey(@Param(name = "key") key: Int): Boolean =
            map.containsKey(BadHashKey(key))
    }

    private data class BadHashKey(val value: Int) {
        override fun hashCode(): Int = 0
    }

    override fun runModelChecking() {
        VariableEvalOptions.modelChecking(invocationsPerIteration = 1_000, analyzeStdLib = true).check(Scenario::class)
    }
}
