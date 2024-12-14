import kotlin.math.floor
import kotlin.math.log
import kotlin.math.pow
import kotlin.time.DurationUnit

fun main() {
    fun part1(input: List<String>): Long = Day11Solver(input).solve(25)

    fun part2(input: List<String>): Long = Day11Solver(input).solve(75)

    val testInput = readInput("Day11_test")
    check(part1(testInput) == 55312L)
    //check(part2(testInput) == 0L)  No test output available.

    val input = readInput("Day11")
    part1(input).println()
    part2(input).println()

    timeTrials("Part 1", unit = DurationUnit.MICROSECONDS) { part1(input) }
    timeTrials("Part 2", repetitions = 1000) { part2(input) }
}

class Day11Solver(input: List<String>) {
    private val parsedInput = input[0].split(' ').map { it.toLong() }

    /*
     * i ∈ ℕ₀ ∪ {-1}, φᵢ: ℕ₀ → ℕ shall be the function mapping the amount of stones generated to the amount of steps
     * taken with a stone of starting number i.
     *
     * Furthermore, ѱ: ℕ₀ → ℕ₀ ⨯ (ℕ₀ ∪ {-1}) shall be the function mapping an index to two new indices after a step.
     *
     *         ⎧ (1, -1)       if i = 0
     * ѱ(i) := ⎨ (a, b)        if ⌊lg(i)⌋ + 1 ∈ 2 ℕ    with a := i/(10^((⌊lg(i)⌋ + 1) / 2)), b := i - 10^((⌊lg(i)⌋ + 1) / 2) * a
     *         ⎩ (2024 i, -1)  otherwise
     *
     *          ⎧ 0                      if i = -1
     * φᵢ(n) := ⎨ 1                      if n = 0
     *          ⎩ φₖ(n - 1) + φₗ(n - 1)  otherwise    with (k, l) := ѱ(i)
     *
     * With that φᵢ(n) is a sum with n up to 2ⁿ summands, that are either 0 or 1.
     */
    private val cacheIndices = mutableMapOf<Long, Pair<Long, Long>>()  // Cache the next indices for going from φᵢ(n) to φₖ(n - 1) + φₗ(n - 1).
    private val cacheValues = mutableMapOf<Pair<Long, Int>, Long>()  // Also cache already calculated φᵢ(n)

    fun calculatePsi(i: Long): Pair<Long, Long> = cacheIndices.getOrPut(i) {
        if(i == -1L) throw IllegalArgumentException("Advancement made: How did we get here?")
        else if (i == 0L) 1L to -1L
        else {
            val amountOfDigits = (floor(log(i.toDouble(), 10.0)) + 1)

            if (amountOfDigits.toLong() % 2 == 0L) {
                // Split digits at the midpoint.
                val a = floor(i / 10.0.pow(amountOfDigits / 2))
                val b = i - a * 10.0.pow(amountOfDigits / 2)
                a.toLong() to b.toLong()
            } else {
                2024 * i to -1L
            }
        }
    }

    fun calculatePhi(i: Long, n: Int): Long = cacheValues.getOrPut(i to n) {
        if (i == -1L) 0L
        else if (n == 0) 1L
        else {
            val (k, l) = calculatePsi(i)
            calculatePhi(k, n - 1) + calculatePhi(l, n - 1)
        }
    }

    fun solve(steps: Int): Long = parsedInput.sumOf {
        val debug = calculatePhi(it, steps)
        debug
    }
}
