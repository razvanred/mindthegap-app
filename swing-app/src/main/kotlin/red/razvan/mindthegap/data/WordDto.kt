package red.razvan.mindthegap.data

import kotlinx.serialization.Serializable

@Serializable
data class WordDto(
    val values: List<String>,
    val tip: String? = null,
)
