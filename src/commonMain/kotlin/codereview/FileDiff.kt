package codereview

import kotlinx.serialization.Serializable

@Serializable
enum class FileTShirtSize {
    XS,
    S,
    M,
    L,
    XL
}


@Serializable
enum class DiffTShirtSize {
    XS,
    S,
    M,
    L,
    XL
}

@Serializable
sealed class FileLineItem() {
    @Serializable
    data class Comment(
        val body: String,
        val createdAt: String,
        val updatedAt: String,
        val userId: String
    ): FileLineItem()

}


@Serializable
data class FileLine(
    val lineText: String,
    /** Position in the actual content - as per file system (and git). Starts with 0 index */
    val filePosition: Int?,

    val lineItems: List<FileLineItem>
)

@Serializable
data class FileData(
    val objectId: String,
    val fileLines: List<FileLine>,
    val path: String
)
/** Retrieves line items as Triples of (viewPosition, filePosition, lineItem) */
fun FileData.retrieveAllLineItems(): List<Triple<Int, Int?, List<FileLineItem>>> {
    return this.fileLines
        .mapIndexedNotNull { index, fileLine ->
            if (fileLine.lineItems.isNotEmpty()) {
                Triple(index, fileLine.filePosition, fileLine.lineItems)
            } else {
                null
            }
        }
}

fun FileData.retrieveViewPositionToCommentMap(): Map<Int, List<FileLineItem.Comment>> {
    return this.fileLines
        .mapIndexedNotNull { index, fileLine ->
            val comments = fileLine.lineItems.filterIsInstance<FileLineItem.Comment>()
            if (comments.isNotEmpty()) {
                Pair(index, comments)
            } else {
                null
            }
        }
        .associateBy(keySelector = {it.first}, valueTransform = {it.second})
}

@Serializable
enum class FilePatchType {
    BINARY,
    TEXT
}

@Serializable
data class FileDiffV2(
    val oldFile: FileData? = null,
    val newFile: FileData? = null,
    val diffChangeType: DiffChangeType,
    val tShirtSize: FileTShirtSize,
    val editList: List<Edit>,
    val patchType: FilePatchType = FilePatchType.TEXT
)

fun FileDiffV2.getOldFileText(): String {
    return oldFile?.getText(patchType) ?: ""
}

fun FileDiffV2.getNewFileText(): String {
    return newFile?.getText(patchType) ?: ""
}

private fun FileData.getText(patchType: FilePatchType): String {
    return when(patchType) {
        FilePatchType.BINARY -> "Binary File"
        FilePatchType.TEXT -> fileLines.joinToString(separator = "\n") { it.lineText }
    }
}

fun FileDiffV2.getUniqueIdentifier(): String {
    return ( newFile?.objectId ?: oldFile?.objectId!! ).substring(0..5)
}

fun FileDiffV2.hasOldFile(): Boolean {
    return oldFile != null && oldFile.fileLines.isNotEmpty()
}

fun FileDiffV2.hasNewFile(): Boolean {
    return newFile != null && newFile.fileLines.isNotEmpty()
}

@Serializable
data class FileDiffListV2(
    val fileDiffs: List<FileDiffV2>
) {
    val diffTShirtSize: DiffTShirtSize
    get() {
        val allFileEditLength = this.fileDiffs.fold(0L) { acc, fileDiff ->
            acc + fileDiff.editList.totalEditLength()
        }
        return when {
            allFileEditLength < 20L -> DiffTShirtSize.XS
            allFileEditLength < 100L -> DiffTShirtSize.S
            allFileEditLength < 200L -> DiffTShirtSize.M
            allFileEditLength < 400L -> DiffTShirtSize.L
            else -> DiffTShirtSize.XL
        }
    }
}

@Serializable
enum class DiffEditType {
    /** Sequence B has inserted the region. */
    INSERT,

    /** Sequence B has removed the region. */
    DELETE,

    /** Sequence B has replaced the region with different content. */
    REPLACE,

    /** Sequence A and B have zero length, describing nothing. */
    EMPTY;
}

@Serializable
data class Edit (
    val beginA: Long,
    val beginB: Long,
    val endA: Long,
    val endB: Long
) {
    val editType: DiffEditType
    get() = when {
        (beginA < endA) -> {
            if (beginB < endB) {
                DiffEditType.REPLACE;
            } else {
                DiffEditType.DELETE;
            }
        }
        (beginB < endB) -> DiffEditType.INSERT
        else -> DiffEditType.EMPTY
    }
    val isEmpty: Boolean
        get() = beginA == endA && beginB == endB
    val lengthA: Long
        get() = endA - beginA
    val lengthB: Long
        get() = endB - beginB

    override fun toString(): String {
        return "$editType($beginA-$endA,$beginB-$endB)"
    }
}

fun List<Edit>.totalEditLength(): Long {
    return this.fold(0L) { acc, edit ->
        acc + edit.lengthB
    }
}

@Serializable
enum class DiffChangeType {
    /** Add a new file to the project */
    ADD,

    /** Modify an existing file in the project (content and/or mode) */
    MODIFY,

    /** Delete an existing file from the project */
    DELETE,

    /** Rename an existing file to a new location */
    RENAME,

    /** Copy an existing file to a new location, keeping the original */
    COPY;
}

@Serializable
data class SimpleFileDiff(
    val oldFileText: String?,
    val newFileText: String?,
    val editList: List<Edit>
)
