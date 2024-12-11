import kotlin.text.flatMapIndexed

fun main() {
    fun part1(input: List<String>): Long {
        val disk = parseInputDay09(input)
        return disk.compactFragmented().checksum()
    }

    fun part2(input: List<String>): Long {
        val disk = parseInputDay09(input)
        return disk.blockify().compactContiguous().checksum()
    }

    val testInput = readInput("Day09_test")
    check(part1(testInput) == 1928L)
    check(part2(testInput) == 2858L)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}

fun parseInputDay09(input: List<String>): DiscretizedDisk {
    var id = 0
    return input[0].flatMapIndexed { index, char ->
        val size = "$char".toInt()
        if (index % 2 == 0) List(size) { DiskBlockElement(id) }
        else (List(size) { DiskBlockElement(-1) }).also { id++ }
    }
}

data class DiskBlockElement(val id: Int)  // -1 id is empty
data class DiskBlock(val id: Int, val indexRange: IntRange)

typealias Disk = List<DiskBlock>
typealias DiscretizedDisk = List<DiskBlockElement>
fun DiscretizedDisk.compactFragmented(): DiscretizedDisk {
    val freeSpace = count { it.id < 0 }
    val onlyFiles = reversed().filter { it.id >= 0 }
    var indexIntoOnlyFiles = 0
    val discretizedCompacted = map { if (it.id < 0) onlyFiles[indexIntoOnlyFiles++] else it }.dropLast(freeSpace) + List(freeSpace) { DiskBlockElement(-1) }
    return discretizedCompacted
}

fun Disk.compactContiguous(): DiscretizedDisk {
    var (onlyFiles, spaaaaace) = (this.partition { it.id >= 0 })
    onlyFiles = onlyFiles.reversed()

    val emptySpacesCreatedIndexes = mutableListOf<List<Int>>()
    var spaceRemaining = spaaaaace.first().indexRange.size()
    val emptyBlockReplacements = spaaaaace.map { emptyBlock ->
        buildList {
            spaceRemaining = emptyBlock.indexRange.size()
            while (spaceRemaining > 0) {
                val fittingBlockIndex = onlyFiles.indexOfFirst { it.indexRange.size() <= spaceRemaining }
                if (fittingBlockIndex == -1) {
                    add(DiskBlock(-1, (emptyBlock.indexRange.last() - spaceRemaining + 1)..emptyBlock.indexRange.last()))
                    break
                }

                val fittingBlock = onlyFiles[fittingBlockIndex]
                val newDiscretizedIndex = with(emptyBlock.indexRange.last() - spaceRemaining + 1) { this until (this + fittingBlock.indexRange.size()) }
                add(fittingBlock.copy(indexRange = newDiscretizedIndex))
                spaceRemaining -= fittingBlock.indexRange.size()
                onlyFiles = onlyFiles.withoutElementAt(fittingBlockIndex)
                emptySpacesCreatedIndexes.add(fittingBlock.indexRange.toList())
            }
        }
    }

    TODO("Probably an issue with reassembling the stuff.")

    val replaceWithEmpty = emptySpacesCreatedIndexes.flatten()
    var replacementIndex = 0
    return flatMap {
        if (it.id >= 0) listOf(it) else emptyBlockReplacements[replacementIndex++]
    }.discretize().mapIndexed { index, blockElement -> if (index in replaceWithEmpty) DiskBlockElement(-1) else blockElement }
}

fun DiscretizedDisk.blockify(): Disk = buildList {
    var blockID = this@blockify.first().id
    var blockStartIndex = 0
    this@blockify.forEachIndexed { index, blockElement ->
        if (blockElement.id != blockID) {
            add(DiskBlock(blockID, blockStartIndex until index))
            blockStartIndex = index
            blockID = blockElement.id
        } else if (index == this@blockify.lastIndex) add(DiskBlock(blockElement.id, blockStartIndex.. this@blockify.lastIndex))
    }
}

fun Disk.discretize(): DiscretizedDisk = flatMap { block -> List(block.indexRange.size()) { DiskBlockElement(block.id) } }

fun DiscretizedDisk.checksum(): Long = foldIndexed(0) { index, acc, blockElement ->
    if (blockElement.id >= 0) acc + index * blockElement.id else acc
}
