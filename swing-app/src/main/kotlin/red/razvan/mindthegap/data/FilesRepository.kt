package red.razvan.mindthegap.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import red.razvan.mindthegap.model.File

object FilesRepository {

    private var cache: Map<File.Id, File>? = null

    @OptIn(ExperimentalSerializationApi::class)
    fun getFiles(): Map<File.Id, File> =
        cache
            ?: run {
                val json = Json {
                    encodeDefaults = true
                }

                (this::class.java
                    .getResourceAsStream("/files.json") ?: throw IllegalStateException("Error while reading resources"))
                    .let {
                        json.decodeFromStream<List<FileDto>>(it)
                    }
                    .associate { file ->
                        val model = file.toModel()
                        model.id to model
                    }
            }
                .also { cache = it }
}