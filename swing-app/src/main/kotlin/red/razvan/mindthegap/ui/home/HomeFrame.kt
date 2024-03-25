package red.razvan.mindthegap.ui.home

import red.razvan.mindthegap.Constants
import red.razvan.mindthegap.model.File
import red.razvan.mindthegap.ui.AppIcon
import red.razvan.mindthegap.ui.JBar
import red.razvan.mindthegap.ui.quiz.QuizFrame
import red.razvan.mindthegap.ui.quiz.withFileIds
import java.awt.BorderLayout
import java.awt.GridLayout
import javax.swing.*

interface HomeFrame {
    companion object

    fun init(files: List<File>)

    fun displayNoFileSelectedWarning()

    fun navigateToQuizFrame(fileIds: List<File.Id>, shuffled: Boolean)
}

fun HomeFrame.Companion.create(builder: JFrame.() -> Unit = {}): HomeFrame =
    DefaultHomeFrame()
        .also(builder)

private class DefaultHomeFrame : JFrame("Scegli file - " + Constants.APP_NAME), HomeFrame {

    private object ClientProperties {
        const val FILE_ID = "file_id"
    }

    private companion object {
        fun JComponent.putFileIdClientProperty(value: File.Id) {
            putClientProperty(ClientProperties.FILE_ID, value)
        }

        fun JComponent.getFileIdClientProperty(): File.Id =
            getClientProperty(ClientProperties.FILE_ID) as File.Id
    }

    private val controller = HomeController(frame = this)

    override fun init(files: List<File>) {
        jMenuBar = JBar(0, this)

        val contentPanel = JPanel().apply {
            layout = BorderLayout()
            add(JLabel("Scegli almeno un file"), "North")
        }

        val filesCheckBoxesPanel = JPanel().apply {
            layout = GridLayout(2, 3)
        }

        val filesCheckBoxes = files
            .map { file ->
                JCheckBox(file.name)
                    .apply { putFileIdClientProperty(file.id) }
            }

        for (checkBox in filesCheckBoxes) {
            filesCheckBoxesPanel.add(checkBox)
        }

        contentPanel.add(filesCheckBoxesPanel, "Center")
        val startButton = JButton("Iniziamo")
        contentPanel.add(startButton, "South")

        startButton.addActionListener { _ ->
            val selectedFileIds = filesCheckBoxes
                .filter {
                    it.isSelected
                }
                .map {
                    it.getFileIdClientProperty()
                }
            controller.start(selectedFileIds = selectedFileIds)
        }

        contentPane.add(contentPanel)
        pack()
        setLocationRelativeTo(null)
        isResizable = false
        iconImage = AppIcon
        defaultCloseOperation = EXIT_ON_CLOSE
    }

    override fun navigateToQuizFrame(fileIds: List<File.Id>, shuffled: Boolean) {
        QuizFrame
            .withFileIds(fileIds = fileIds, shuffled = shuffled) {
                isVisible = true
            }
        isVisible = false
        dispose()
    }

    override fun displayNoFileSelectedWarning() {
        JOptionPane.showMessageDialog(
            this,
            "Devi selezionare almeno un file",
            "Attenzione",
            JOptionPane.WARNING_MESSAGE
        )
    }
}