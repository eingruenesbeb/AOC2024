fun main() {
    fun part1(input: List<String>): Int =
        Regex("""mul\(\d+,\d+\)""").findAll(input.joinToString()).sumOf {
            with(Regex("""\d+""").findAll(it.value)) { this.first().value.toInt() * this.last().value.toInt() }
        }

    fun part2(input: List<String>): Int {
        var isMultiplyInstructionEnabled = true  // by default
        return Regex("""mul\(\d+,\d+\)|do\(\)|don't\(\)""").findAll(input.joinToString()).fold(0) { acc, instruction ->
            when (instruction.value) {
                "do()" -> acc.also { isMultiplyInstructionEnabled = true }
                "don't()" -> acc.also { isMultiplyInstructionEnabled = false }
                else -> {
                    if (isMultiplyInstructionEnabled) {
                        acc + with(Regex("""\d+""").findAll(instruction.value)) { this.first().value.toInt() * this.last().value.toInt() }
                    } else acc
                }
            }
        }
    }

    val testInputPart1 = readInput("Day03_test_part1")
    val testInputPart2 = readInput("Day03_test_part2")
    check(part1(testInputPart1) == 161)
    check(part2(testInputPart2) == 48)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
