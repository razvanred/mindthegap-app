package red.razvan.mindthegap.ui.home

import red.razvan.mindthegap.Constants
import red.razvan.mindthegap.data.FilesRepository.getFiles
import red.razvan.mindthegap.model.File
import red.razvan.mindthegap.ui.AppIcon
import red.razvan.mindthegap.ui.JBar
import red.razvan.mindthegap.ui.quiz.QuizFrame
import red.razvan.mindthegap.ui.quiz.withFileIds
import java.awt.BorderLayout
import java.awt.GridLayout
import javax.swing.*

class HomeFrame : JFrame("Scegli file - " + Constants.APP_NAME) {

    private val files = getFiles().values

    init {
        jMenuBar = JBar(0, this)

        val pnl = JPanel()
        pnl.layout = BorderLayout()
        pnl.add(JLabel("Scegli almeno un file:"), "North")

        val pnlCbx = JPanel()
        pnlCbx.layout = GridLayout(2, 3)

        val filesCheckBoxes = files
            .map { file ->
                JCheckBox(file.name)
                    .apply { putClientProperty("file_id", file.id) }
            }

        for (checkBox in filesCheckBoxes) {
            pnlCbx.add(checkBox)
        }

        pnl.add(pnlCbx, "Center")
        val btnNext = JButton("Prosegui")
        pnl.add(btnNext, "South")
        add(pnl)

        btnNext.addActionListener { _ ->
            val selectedFileIds = filesCheckBoxes
                .map {
                    it.getClientProperty("file_id") as File.Id
                }

            if (selectedFileIds.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Devi selezionare almeno un file",
                    "Attenzione",
                    JOptionPane.WARNING_MESSAGE
                )
            } else {
                QuizFrame.withFileIds(fileIds = selectedFileIds) {
                    isVisible = true
                }
                isVisible = false
                dispose()
            }
        }

        contentPane.add(pnl)
        pack()
        setLocationRelativeTo(null)
        isResizable = false
        iconImage = AppIcon
        defaultCloseOperation = EXIT_ON_CLOSE
    }
}