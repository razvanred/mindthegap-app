package red.razvan.mindthegap.data

import kotlinx.serialization.Serializable

@Serializable
data class AssignmentDto(
    val id: Id,
    val en: WordDto,
    val it: WordDto,
) {
    @JvmInline
    @Serializable
    value class Id private constructor(val value: String) {
        companion object {
            fun String.toId(): Id = Id(value = this)
        }
    }
}
