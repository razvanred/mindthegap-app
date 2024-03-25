package red.razvan.mindthegap.model

data class Assignment(
    val id: Id,
    val fileId: File.Id,
    val en: Word,
    val it: Word,
) {
    @JvmInline
    value class Id private constructor(val value: String) {
        companion object {
            fun String.toId(): Id = Id(value = this)
        }
    }
}
