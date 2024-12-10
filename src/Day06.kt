fun main() {
    fun part1(input: List<String>): Int {
        val puzzleMap = PuzzleMap.fromPuzzleInput(input)
        puzzleMap.simulateGuardPath()
        return puzzleMap.asIterable().indicesWhere { it is MapObject.Visited }.count()
    }

    fun part2(input: List<String>): Int {
        val puzzleMap = PuzzleMap.fromPuzzleInput(input)
        puzzleMap.simulateGuardPath()

        return puzzleMap.asIterable().indicesWhere { it is MapObject.Visited }.count {
            val alteredPuzzleMap = PuzzleMap.fromPuzzleInput(input)
            alteredPuzzleMap[VecNReal(it)] = MapObject.Obstacle()
            alteredPuzzleMap.simulateGuardPath()
        }
    }

    val testInput = readInput("Day06_test")
    check(part1(testInput) == 41)
    check(part2(testInput) == 6)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}

enum class Orientation {
    NORTH, SOUTH, WEST, EAST;

    fun rotateClockwise(): Orientation {
        return when (this) {
            NORTH -> EAST
            EAST -> SOUTH
            SOUTH -> WEST
            WEST -> NORTH
        }
    }
    
    fun asVector(): VecNReal {
        return when (this) {
            NORTH -> VecNReal(listOf(0.0, 1.0))
            SOUTH -> VecNReal(listOf(0.0, -1.0))
            WEST -> VecNReal(listOf(-1.0, 0.0))
            EAST -> VecNReal(listOf(1.0, 0.0))
        }
    }
}

class PuzzleMap(objectElements: List<List<MapObject>>): Grid2D<MapObject>(objectElements) {
    private val guard = Grid2D(objectElements).asIterable().first { it is MapObject.Guard } as MapObject.Guard

    companion object {
        fun fromPuzzleInput(input: List<String>): PuzzleMap = PuzzleMap(
            input.reversed().mapIndexed { y, row -> row.mapIndexed { x, cell ->  MapObject.fromCharAndIndex(cell, x to y) } }
        ).also { it.transpose() }
    }

    fun guardStep() {
        if (guardScout() is MapObject.Obstacle) guard.orientation = guard.orientation.rotateClockwise()
        else {
            guard.position += guard.orientation.asVector()
        }
    }

    fun simulateGuardPath(): Boolean {
        while (true) {
            markVisited()
            val scouted = guardScout()
            if (scouted is MapObject.Visited && guard.orientation in scouted.inOrientation) return true
            else if (scouted is MapObject.OutOfBounds) return false
            guardStep()
        }
    }

    fun guardScout(): MapObject = runCatching { this[guard.position + guard.orientation.asVector()] }.getOrElse { MapObject.OutOfBounds }

    fun markVisited() {
        val previousMapObject = this[guard.position]
        if (previousMapObject is MapObject.Visited) this[guard.position] = previousMapObject.copy(previousMapObject.inOrientation.plus(guard.orientation))
        else this[guard.position] = MapObject.Visited(listOf(guard.orientation))
    }
}

sealed class MapObject {
    class Empty: MapObject()
    class Obstacle: MapObject()
    object OutOfBounds: MapObject()

    data class Visited(val inOrientation: List<Orientation>): MapObject()
    data class Guard(var position: VecNReal, var orientation: Orientation = Orientation.NORTH): MapObject()

    companion object {
        fun fromCharAndIndex(c: Char, index: Pair<Int, Int>): MapObject {
            return when (c) {
                '.' -> Empty()
                '#' -> Obstacle()
                '^' -> Guard(VecNReal(index))
                else -> throw IllegalArgumentException("Unknown map object $c")
            }
        }
    }
}
