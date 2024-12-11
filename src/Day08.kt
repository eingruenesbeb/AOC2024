import kotlin.math.abs
import kotlin.math.pow

fun main() {
    fun part1(input: List<String>): Int {
        val inputMap = Day08Map(input)
        return inputMap.isoFrequencyNodeVectorsByLocations
            .flatMap { (location, vectors) ->
                vectors.map { (2.0 scaleVec it) + location }
            }
            .toSet()
            .count { inputMap.isInGrid(it) }
    }

    fun part2(input: List<String>): Int {
        val inputMap = Day08Map(input)
        return buildSet {
            inputMap.isoFrequencyNodeVectorsByLocations.forEach { (location, vectors) ->
                vectors.forEach { vector ->
                    var i = 0.0
                    val scaledDownVector = smallestIntegerVectorInSameDirection2D(vector)
                    while (inputMap.isInGrid(location + (i scaleVec scaledDownVector))) {
                        add(location + (i scaleVec scaledDownVector))
                        i++
                    }
                }
            }
        }.count()
    }

    val testInput = readInput("Day08_test")
    check(part1(testInput) == 14)
    check(part2(testInput) == 34)

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}

tailrec fun gcdEuclid(a: Int, b: Int): Int =
    if (b == 0) a
    else if (a == 0) b
    else if (a > b) gcdEuclid(a - b, b)
    else gcdEuclid(a, b - a)

fun smallestIntegerVectorInSameDirection2D(vec: VecNReal): VecNReal {
    assert(vec.dimension == 2)  // Only works in two dimensions.
    assert(vec == vec.roundComponents())  // Only works on integer vectors.

    return (gcdEuclid(abs(vec[0].toInt()), abs(vec[1].toInt())).toDouble().pow(-1) scaleVec vec).roundComponents()
}

class Day08Map(input: List<String>): Grid2D<Char>(input.reversed().map { it.toList() }) {
    init {
        transpose()
    }

    val isoFrequencyNodesLocations = asIterable().toSet().filter { it != '.' }.map { frequency -> asIterable().indicesWhere { frequency == it } }
    val isoFrequencyNodeVectorsByLocations = buildMap {
        isoFrequencyNodesLocations.forEach { isoFrequencyLocationList ->
            isoFrequencyLocationList.mapIndexed { index, nodeLocation ->
                this[VecNReal(nodeLocation)] = isoFrequencyLocationList
                    .slice((0 until index) + ((index + 1)..isoFrequencyLocationList.lastIndex))
                    .map { VecNReal(it) - VecNReal(nodeLocation) }
            }
        }
    }
}
