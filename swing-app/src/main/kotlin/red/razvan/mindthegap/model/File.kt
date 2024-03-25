package red.razvan.mindthegap.model

data class File(
    val id: Id,
    val name: String,
    val description: String,
    val assignments: List<Assignment>,
) {
    @JvmInline
    value class Id private constructor(val value: String) {
        companion object {
            fun String.toId(): Id =
                Id(value = this)
        }
    }
}