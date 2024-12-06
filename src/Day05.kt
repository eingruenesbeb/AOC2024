import kotlin.text.Regex

fun main() {
    fun part1(input: List<String>): Int = parseInput(input).sumOf { if (it.isCorrectlyOrdered()) it[it.size / 2].pageNumber else 0 }

    fun part2(input: List<String>): Int = parseInput(input).sumOf { if (!it.isCorrectlyOrdered()) it.sorted()[it.size / 2].pageNumber else 0 }

    val testInput = readInput("Day05_test")
    check(part1(testInput) == 143)
    check(part2(testInput) == 123)

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}

fun parseInput(input: List<String>): List<List<Page>> {
    val (orderRulesStrings, pageSequencesStrings) = input.filter { it.isNotEmpty() }.partition { Regex("""\d+\|\d+""").matches(it) }

    val orderRules = orderRulesStrings.map { with(it.split('|')) { this[0].toInt() to this[1].toInt() } }
    val orderRulesX = orderRules.map { it.first }.toSet()
    val pages = orderRulesX.map { pageNumber ->
        val orderClasses = orderRules.filter { it.first == pageNumber }.map { it.second }
        Page(pageNumber, orderClasses)
    }.associateBy { it.pageNumber }

    val pageSequences = pageSequencesStrings.map { sequenceString ->
        sequenceString.split(',').map { pages[it.toInt()] ?: Page(it.toInt(), emptyList()) }
    }

    return pageSequences
}

/*
 * An order class is an equivalence class for every page with the same page to be printed before.
 */
data class Page(val pageNumber: Int, val orderClasses: List<Int>): Comparable<Page> {
    override fun compareTo(other: Page): Int =
        if (other.pageNumber in orderClasses) -1
        else if (pageNumber in other.orderClasses) 1
        else 0
}

fun List<Page>.isCorrectlyOrdered(): Boolean = this == this.sorted()
