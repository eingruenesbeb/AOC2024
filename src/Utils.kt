import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.math.sqrt

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readText().trim().lines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun <T> List<T>.withoutElementAt(index: Int): List<T> = filterIndexed { i, _ -> i != index }

/**
 * A data class holding a 2D-grid of values. Internally data is stored as List (x-index) of columns (y-index).
 *
 * **The input must be square!**
 */
open class Grid2D<T>(private val elements: List<List<T>>) {
    val xDimension = elements.size
    val yDimension = elements.first().size

    operator fun get(x: Int, y: Int): T = elements[x][y]

    operator fun get(vec: VecNReal): T {
        assert(vec.dimension == 2) { "Index vector for 2D-grid has more than 2 dimensions." }
        return elements[vec[0].toInt()][vec[1].toInt()]
    }

    /**
     * Swaps indices. Rows become columns and vice versa.
     * E.g.:
     * `[[a, b], [c, d]]` becomes `[[a, c], [b, d]]`.
     */
    fun transposed() = Grid2D((0 until yDimension).map { x -> (0 until xDimension).map { y -> this[x, y] } })

    fun asIterable(gridIterator: GridIterator<T> = XYAscendGridIterator<T>(this)) = Grid2DIterable(elements, gridIterator)

    sealed class GridIterator<T>(protected val grid: Grid2D<T>): Iterator<T> {
        abstract fun currentX(): Int
        abstract fun currentY(): Int
        abstract fun newInstance() : GridIterator<T>
    }

    /**
     * Iterates through rows x fist, then increases y, whilst wrapping around to 0 in the x-index.
     *
     * @throws NoSuchElementException If all elements have been reached at least once
     */
    class XYAscendGridIterator<T>(grid: Grid2D<T>) : GridIterator<T>(grid) {
        private var x = -1
        private var y = 0

        override fun currentX(): Int = x

        override fun currentY(): Int = y
        override fun newInstance(): XYAscendGridIterator<T> = XYAscendGridIterator(grid)

        override fun hasNext(): Boolean = !(y + 1 >= grid.yDimension && x + 1 >= grid.xDimension)

        override fun next(): T {
            if (x >= grid.xDimension - 1) {
                x = 0
                y++
            } else x++

            return grid[x, y]
        }
    }

    class Grid2DIterable<T>(elements: List<List<T>>, private val gridIterator: GridIterator<T>): Iterable<T>, Grid2D<T>(elements) {
        override fun iterator(): GridIterator<T> = gridIterator.newInstance()

        /**
         * Returns all indices, where a grid-element matches a given predicate.
         */
        fun indicesWhere(predicate: (T) -> Boolean) = buildList {
            val searchIterator = this@Grid2DIterable.iterator()
            searchIterator.forEachRemaining {
                if (predicate(it)) add(searchIterator.currentX() to searchIterator.currentY())
            }
        }
    }
}

open class VecNReal(val entries: List<Double>) {
    val dimension = entries.size

    operator fun plus(other: VecNReal): VecNReal {
        assert(dimension == other.dimension) { "Dimensions mismatch!" }
        return VecNReal(entries.zip(other.entries).map { it.first + it.second })
    }

    operator fun minus(other: VecNReal): VecNReal {
        assert(dimension == other.dimension) { "Dimensions mismatch!" }
        return VecNReal(entries.zip(other.entries).map { it.first - it.second })
    }

    operator fun times(other: VecNReal): Double {
        assert(dimension == other.dimension) { "Dimensions mismatch!" }

        return entries.zip(other.entries).map { it.first * it.second }.reduce { a, b -> a + b }
    }

    operator fun get(index: Int): Double = entries[index]

    fun norm(): Double = sqrt(this * this)
}

infix fun Double.scaleVec(vec: VecNReal) = VecNReal(vec.entries.map { this * it })
