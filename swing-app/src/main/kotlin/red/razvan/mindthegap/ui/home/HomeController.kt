package red.razvan.mindthegap.ui.home

import red.razvan.mindthegap.data.FilesRepository
import red.razvan.mindthegap.model.File

class HomeController(
    private val frame: HomeFrame,
) {
    private val files = FilesRepository.getFiles().values

    init {
        frame.init(files = files.toList())
    }

    fun start(selectedFileIds: List<File.Id>) {
        with (frame) {
            if (selectedFileIds.isEmpty()) {
                displayNoFileSelectedWarning()
            } else {
                navigateToQuizFrame(fileIds = selectedFileIds, shuffled = false)
            }
        }
    }
}