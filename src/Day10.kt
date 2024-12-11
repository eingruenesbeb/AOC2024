fun main() {
    /**
     * The idea is simple: Just simulate the pathing and sum all the end points
     */
    fun part1(input: List<String>): Int {
        val topologicalMap = Day10Map(input)
        val startingPoints = topologicalMap.asIterable().indicesWhere { it == 0 }
        val directions = Orientation.entries.map { it.asVector() }
        return startingPoints.sumOf { startingPoint ->
            var wayPoints = setOf(VecNReal(startingPoint))
            val endPoints = mutableSetOf<VecNReal>()
            while (wayPoints.isNotEmpty()) {
                wayPoints = wayPoints.flatMap { wayPoint ->
                    directions.map { direction ->
                        val checkoutLocation = wayPoint + direction
                        checkoutLocation to runCatching { topologicalMap[checkoutLocation] }.getOrElse { -1 }
                    }.filter { nextLocation ->
                        val endPointHeight = topologicalMap[wayPoint]
                        if (nextLocation.second - 1 == endPointHeight && nextLocation.second == 9) false.also { endPoints.add(nextLocation.first) }
                        else if (nextLocation.second - 1 == endPointHeight) true
                        else false
                    }.map { it.first }
                }.toSet()
            }

            endPoints.count()
        }
    }

    /**
     * A bit more complicated, but not by much.
     * Main difference is, that node accumulates all the possible paths, thus adding all the possibilities of
     * its parent node.
     */
    fun part2(input: List<String>): Int {
        val topologicalMap = Day10Map(input)
        val startingPoints = topologicalMap.asIterable().indicesWhere { it == 0 }
        val directions = Orientation.entries.map { it.asVector() }

        return startingPoints.sumOf { startingPoint ->
            var pathNodes = setOf<Node>(Node(VecNReal(startingPoint), topologicalMap[VecNReal(startingPoint)], 1))
            val endNodes = mutableSetOf<Node>()
            while (pathNodes.isNotEmpty()) {
                pathNodes = pathNodes.flatMap { pathNode ->
                    directions.map { direction ->
                        val nextNodeLocation = pathNode.position + direction
                        val nextNodeHeight = runCatching { topologicalMap[nextNodeLocation] }.getOrElse { -1 }
                        Node(nextNodeLocation, nextNodeHeight, pathNode.weight)
                    }.filter { nextNode ->
                        nextNode.height == pathNode.height + 1
                    }
                }.groupBy { it.position }.map { (position, nodesUnadjusted) ->
                    val adjustedWeight = nodesUnadjusted.sumOf { node -> node.weight }
                    Node(position, nodesUnadjusted.first().height, adjustedWeight)
                }.filter { node ->
                    if (node.height == 9) false.also { endNodes.add(node) } else true
                }.toSet()
            }

            endNodes.sumOf { endNode -> endNode.weight }
        }
    }

    val testInput = readInput("Day10_test")
    check(part1(testInput) == 36)
    check(part2(testInput) == 81)

    val input = readInput("Day10")
    part1(input).println()
    part2(input).println()
}

class Day10Map(input: List<String>): Grid2D<Int>(input.map { row -> row.map { "$it".toInt() } }) {
    init { transpose() }
}

data class Node(val position: VecNReal, val height: Int, val weight: Int = 1)
