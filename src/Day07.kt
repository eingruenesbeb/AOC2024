import kotlin.collections.any
import kotlin.math.pow

fun main() {
    fun part1(input: List<String>): Long {
        val operations = setOf(CalibrationOperation.Plus, CalibrationOperation.Multiply)
        return generalizedSolution(input, operations)
    }

    fun part2(input: List<String>): Long {
        val operations = setOf(CalibrationOperation.Plus, CalibrationOperation.Multiply, CalibrationOperation.Concat)
        return generalizedSolution(input, operations)
    }

    val testInput = readInput("Day07_test")
    check(part1(testInput) == 3749L)
    check(part2(testInput) == 11387L)

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}

fun parseInputDay7(input: List<String>) = input.map {
    val calibrationResultAndInput = it.split(':')
    calibrationResultAndInput[0].toLong() to calibrationResultAndInput[1].split(' ').filter { it != "" }.map { it.toLong() }
}

fun generalizedSolution(input: List<String>, operations: Set<CalibrationOperation>): Long {
    val parsedInput = parseInputDay7(input)
    val operationsPermutations = CalibrationOperation.operationPermutationSequence(*operations.toTypedArray()).take(calculatePermutationsNeeded(parsedInput, operations)).toList()
    return sumOfPossibleCalibrationEquations(parsedInput, operationsPermutations)
}

fun calculatePermutationsNeeded(parsedInput: List<Pair<Long, List<Long>>>, operations: Set<CalibrationOperation>): Int {
    val highestNumberOfOperations = parsedInput.maxOf { it.second.size - 1 }
    return (1..highestNumberOfOperations).sumOf { operations.size.toDouble().pow(it).toInt() }
}

fun sumOfPossibleCalibrationEquations(parsedInput: List<Pair<Long, List<Long>>>, operationPermutationCollection: Collection<OperationPermutation>): Long {
    val permutationsGrouped = operationPermutationCollection.groupBy { it.size }
    return parsedInput.sumOf { (equationResult, equationInput) ->
        if (permutationsGrouped[equationInput.size - 1]!!.any { operations ->
                equationResult == equationInput.drop(1)
                    .foldIndexed(equationInput[0]) { index, acc, lng -> operations[index](acc, lng) }
            }) equationResult else 0
    }
}

typealias OperationPermutation = List<CalibrationOperation>

sealed class CalibrationOperation(val operation: (Long, Long) -> Long) {
    operator fun invoke(a: Long, b: Long) = operation(a, b)
    object Plus : CalibrationOperation({ a: Long, b: Long -> a + b })
    object Multiply : CalibrationOperation({ a: Long, b: Long -> a * b })
    object Concat : CalibrationOperation({ a: Long, b: Long -> "$a$b".toLong() })

    companion object {
        fun operationPermutationSequence(vararg operations: CalibrationOperation) = sequence<OperationPermutation> {
            val cache = mutableListOf<OperationPermutation>()
            val calculateCacheRange = { currentLength: Int ->
                val sectionSize = operations.size.toDouble().pow(currentLength - 1).toInt()
                val sectionStart = (1 until currentLength - 1).sumOf { operations.size.toDouble().pow(it).toInt() }
                sectionStart..(sectionStart + sectionSize - 1)
            }

            // Populate the cache with initial values for permutation length 1.
            operations.forEach { operation -> yield(listOf(operation).also { cache.add(it) }) }

            var currentLength = 2
            var offset = 0
            var cacheRange = calculateCacheRange(currentLength)
            var rotatingOperations = operations.toList()
            yieldAll(
                generateSequence {
                    if (cacheRange.count() == offset) {
                        rotatingOperations = rotatingOperations.rotated(1)
                        if (rotatingOperations.first() == operations.first()) {
                            currentLength++
                        }

                        offset = 0
                        cacheRange = calculateCacheRange(currentLength)
                    }

                    val cacheSlice = cache.slice(cacheRange)

                    return@generateSequence (cacheSlice[offset] + rotatingOperations.first()).also {
                        cache += it
                        offset++
                    }
                }
            )
        }
    }
}
