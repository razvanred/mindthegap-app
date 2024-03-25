package red.razvan.mindthegap.data

import kotlinx.serialization.Serializable

@Serializable
data class FileDto(
    val id: Id,
    val name: String,
    val description: String,
    val assignments: List<AssignmentDto>,
) {
    @Serializable
    @JvmInline
    value class Id private constructor(val value: String) {
        companion object {
            fun String.toId(): Id =
                Id(value = this)
        }
    }
}