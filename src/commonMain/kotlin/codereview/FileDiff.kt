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
data class FileDiffList(
    val fileDiffs: List<FileDiff>
)

@Serializable
data class FileDiff(
    val rawTextOld: String?,
    val rawTextNew: String?,
    val diffChangeType: DiffChangeType,
    val fileHeader: FileHeader
)
@Serializable
data class FileHeader(
    val identifier: String,
    val fileNewPath: String,
    val fileOldPath: String,
    val description: String,
    val editList: List<Edit>
) {
    val tShirtSize: FileTShirtSize
        get() = editList.fold(0L) {acc, edit ->
            acc + edit.lengthB
        }.let { totalEditLength ->
            when {
                totalEditLength < 2L -> FileTShirtSize.XS
                totalEditLength < 60L -> FileTShirtSize.S
                totalEditLength < 100L -> FileTShirtSize.M
                totalEditLength < 160L -> FileTShirtSize.L
                else -> FileTShirtSize.XL
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
