package red.razvan.mindthegap.data

import red.razvan.mindthegap.model.Assignment
import red.razvan.mindthegap.model.File
import red.razvan.mindthegap.model.Word
import red.razvan.mindthegap.model.File.Id.Companion.toId as toModelFileId
import red.razvan.mindthegap.model.Assignment.Id.Companion.toId as toModelAssignmentId

fun FileDto.toModel(): File =
    File(
        id = id.toModel(),
        name = name,
        description = description,
        assignments = assignments.map { assignment ->
            assignment.toModel(fileId = id)
        }
    )

fun FileDto.Id.toModel(): File.Id =
    value.toModelFileId()

fun AssignmentDto.toModel(fileId: FileDto.Id): Assignment =
    Assignment(
        id = id.value.toModelAssignmentId(),
        fileId = fileId.toModel(),
        en = en.toModel(),
        it = it.toModel(),
    )

fun WordDto.toModel(): Word =
    Word(
        values = values,
        tip = tip,
    )