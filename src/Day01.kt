import kotlin.math.abs

fun main() {

    fun getLists(input: List<String>): Pair<List<Int>, List<Int>> {
        val unsortedPairs = input.map {
            it.split("   ").map { it.toInt() }
        }

        val listASorted = unsortedPairs.map { it.first() }
        val listBSorted = unsortedPairs.map { it.last() }
        return Pair(listASorted, listBSorted)
    }

    fun part1(input: List<String>): Int {
        val (listA, listB) = getLists(input)

        return listA.sorted().zip(listB.sorted()).sumOf { abs(it.first - it.second) }
    }

    fun part2(input: List<String>): Int {
        val (listA, listB) = getLists(input)

        return listA.sumOf { number ->
            number * listB.count { it == number }
        }
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 11)
    check(part2(testInput) == 31)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
