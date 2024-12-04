import kotlin.math.abs
import kotlin.math.sign

data class Report(val levels: List<Int>) {
    fun isSafe(withProblemDampener: Boolean): Boolean {
        var orderSign = 0.0f  // - 1 is descending; +1 is ascending

        levels.zipWithNext().forEachIndexed { index, level ->
            val difference = (level.second - level.first).toFloat()
            if (orderSign == 0.0f) orderSign = sign(difference)
            if (sign(difference) != orderSign || abs(difference) !in 1.0..3.0) {
                // With problem dampener: Drop either element in the pair or the first element from the original list and check if the result is now safe.
                return if (withProblemDampener) {
                    Report(levels.drop(1)).isSafe(false) || Report(levels.withoutElementAt(index)).isSafe(false) || Report(levels.withoutElementAt(index + 1)).isSafe(false)
                }  else false
            }
        }

        return true
    }
}

fun main() {
    fun part1(input: List<String>): Int = input.map { Report(it.split(" ").map { it.toInt() }).isSafe(false) }.count { it }

    fun part2(input: List<String>): Int = input.map { Report(it.split(" ").map { it.toInt() }).isSafe(true) }.count { it }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
