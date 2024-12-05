fun main() {
    fun part1(input: List<String>): Int = WordSearch(input.map { it.toCharArray().toList() }).findXMAS()

    fun part2(input: List<String>): Int = WordSearch(input.map { it.toCharArray().toList() }).findCrossMAS()

    val testInput = readInput("Day04_test")
    check(part1(testInput) == 18)
    check(part2(testInput) == 9)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}

class WordSearch(elements: List<List<Char>>): Grid2D<Char>(elements) {
    /**
     * Find potential starts of the word "XMAS".
     */
    private fun findXMASStart() = asIterable().indicesWhere { it == 'X' }

    /**
     * Find potential starts of the cross-pattern or any of its permutations:
     * ```
     * M-M
     * -A-
     * S-S
     * ```
     * Set the middle as the "seed"-point.
     */
    private fun findCrossMASStart() = asIterable().indicesWhere { it == 'A' }

    fun findXMAS(): Int {
        var xmasFound = 0
        var seeds = findXMASStart()

        seeds.forEach { seed ->
            val seedVec = VecNReal(listOf(seed.first.toDouble(), seed.second.toDouble()))
            StepDirection.directions.forEach { direction ->
                // Stepping outside the grid throws an exception. Ignore it.
                runCatching {
                    val isMSecond = this[seedVec + direction] == 'M'
                    val isAThird = this[seedVec + (2.0 scaleVec direction)] == 'A'
                    val isSFourth = this[seedVec + (3.0 scaleVec direction)] == 'S'

                    if (isMSecond && isAThird && isSFourth) xmasFound++
                }
            }
        }

        return xmasFound
    }

    fun findCrossMAS(): Int {
        var crossMasFound = 0
        var seeds = findCrossMASStart()

        /* Check the corners for these patterns. There are 6 unique ones, if you respect orientation:
         * 4 "symmetrical" ones and 2 "anti-symmetrical" ones, **which are to be disregarded**.
         */
        val validCornerPatterns = listOf("MMSS", "SMMS", "MSSM", "SSMM", "SSMM")
        seeds.forEach { seed ->
            val seedVec = VecNReal(listOf(seed.first.toDouble(), seed.second.toDouble()))
            runCatching {
                val cornerPattern = listOf(
                    this[seedVec + StepDirection.MinusXPlusY],
                    this[seedVec + StepDirection.PlusXPlusY],
                    this[seedVec + StepDirection.PlusXMinusY],
                    this[seedVec + StepDirection.MinusXMinusY]
                ).joinToString("")

                if (cornerPattern in validCornerPatterns) crossMasFound++
            }
        }

        return crossMasFound
    }

    private sealed class StepDirection(vec: Pair<Int, Int>): VecNReal(listOf(vec.first.toDouble(), vec.second.toDouble())) {
        object PlusX : StepDirection(1 to 0)
        object PlusXPlusY: StepDirection(1 to 1)
        object PlusY: StepDirection(0 to 1)
        object MinusXPlusY: StepDirection(-1 to 1)
        object MinusX : StepDirection(-1 to 0)
        object MinusXMinusY : StepDirection(-1 to -1)
        object MinusY : StepDirection(0 to -1)
        object PlusXMinusY : StepDirection(1 to -1)

        companion object {
            val directions = setOf(PlusX, PlusXPlusY, PlusY, MinusXPlusY, MinusX, MinusXMinusY, MinusY, PlusXMinusY)
        }
    }
}
