package red.razvan.mindthegap.ui.quiz

import red.razvan.mindthegap.Constants
import red.razvan.mindthegap.data.FilesRepository
import red.razvan.mindthegap.model.Assignment
import red.razvan.mindthegap.model.File
import red.razvan.mindthegap.ui.AppIcon
import red.razvan.mindthegap.ui.JBar
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Font
import java.awt.GridLayout
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

interface QuizFrame {
    companion object

    fun init()

    fun refreshQuestionFieldText(text: String)

    fun refreshAnswerFieldState(state: AnswerFieldState)

    fun refreshNextButton(state: NextButtonState)

    fun refreshErrorsLabel(state: ErrorsLabelState)

    fun refreshMissingLabel(state: MissingLabelState)

    fun refreshValidateButton(state: ValidateButtonState)
}

fun QuizFrame.Companion.withFileIds(
    fileIds: List<File.Id>,
    shuffled: Boolean = false,
    builder: JFrame.() -> Unit = {},
): QuizFrame =
    DefaultQuizFrame(
        assignments = fileIds
            .map { id ->
                FilesRepository
                    .getFiles()
                    .getValue(id)
                    .assignments
            }
            .flatten()
            .let { assignments ->
                if (shuffled) {
                    assignments.shuffled()
                } else {
                    assignments
                }
            }
    )
        .also(builder)

private class DefaultQuizFrame(
    assignments: List<Assignment>,
) : JFrame("Quiz in corso - " + Constants.APP_NAME), QuizFrame {

    private val nextButton = JButton().apply {
        isEnabled = false
        addActionListener { _ ->
            controller.next()
        }
    }

    private val font = Font("Arial", Font.PLAIN, 10)

    private val errorsLabel = JLabel().apply {
        horizontalAlignment = JLabel.LEFT
        font = this@DefaultQuizFrame.font
    }
    private val missingLabel = JLabel().apply {
        horizontalAlignment = JLabel.RIGHT
        font = this@DefaultQuizFrame.font
    }

    fun createWordTextField(): JTextField =
        JTextField(20)

    private val questionTextField = createWordTextField().apply {
        isEditable = false
        background = Color.WHITE
        isFocusable = false
    }
    private val answerTextField = createWordTextField().apply {
        document.addDocumentListener(
            object : DocumentListener {
                override fun changedUpdate(e: DocumentEvent?) {
                    // empty implementation
                }

                override fun insertUpdate(e: DocumentEvent?) {
                    controller.onAnswerFieldTextChanged(text = this@apply.text)
                }

                override fun removeUpdate(e: DocumentEvent?) {
                    controller.onAnswerFieldTextChanged(text = this@apply.text)
                }
            }
        )
    }

    private val validateButton = JButton("Valida").apply {
        isEnabled = false
        addActionListener { _ ->
            controller.validate(answerFieldText = answerTextField.text)
        }
    }

    private val controller = QuizFrameController(
        frame = this,
        assignments = assignments,
    )

    override fun init() {
        fun createTextFieldPanel(imageIcon: ImageIcon, textField: JTextField) =
            JPanel().apply {
                layout = BorderLayout()
                add(JLabel(imageIcon, JLabel.CENTER), "North")
                add(textField, "Center")
            }


        val skipButton = JButton("Salta").apply {
            isEnabled = false
            addActionListener { _ ->
                controller.skip()
            }
        }

        val questionPanel = createTextFieldPanel(
            imageIcon = ImageIcon("/en.png"),
            textField = questionTextField
        )

        val answerPanel = createTextFieldPanel(
            imageIcon = ImageIcon("/it.png"),
            textField = answerTextField
        )

        val panel2 = JPanel().apply {
            layout = BorderLayout()
            add(questionPanel, "West")
            add(answerPanel, "Center")
        }

        val panel3 = JPanel().apply {
            layout = BorderLayout()
            add(validateButton, "Center")
            add(skipButton, "West")
            add(nextButton, "East")
        }

        val panel4 = JPanel().apply {
            layout = GridLayout(2, 1)
            add(panel2)
            add(panel3)
        }

        val panelS0 = JPanel().apply {
            layout = BorderLayout()
            add(errorsLabel, "West")
            add(missingLabel, "East")
        }

        val panelS1 = JPanel().apply {
            layout = BorderLayout()
            add(panel4, "Center")
            add(panelS0, "South")
        }

        contentPane.add(panelS1)
        iconImage = AppIcon

        jMenuBar = JBar(1, this)

        pack()
        isResizable = false
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)
    }

    override fun refreshNextButton(state: NextButtonState) {
        with (nextButton) {
            text = when (state.label) {
                NextButtonState.Label.Finish -> "Fine"
                NextButtonState.Label.Next -> "Prossimo"
            }
            isEnabled = state.isEnabled
        }
    }

    override fun refreshMissingLabel(state: MissingLabelState) {
        val count = state.missing
        missingLabel.text = if (count == 1U) {
            "Manca un quesito"
        } else {
            "Mancano $count quesiti"
        }
    }

    override fun refreshErrorsLabel(state: ErrorsLabelState) {
        val count = state.errors
        errorsLabel.text = if (count == 1U) {
            "Un errore"
        } else {
            "$count errori"
        }
    }

    override fun refreshAnswerFieldState(state: AnswerFieldState) {
        with (answerTextField) {
            when (state) {
                is AnswerFieldState.Answered -> {
                    background = when (state.isCorrect) {
                        true -> Color.GREEN
                        false -> Color.RED
                    }
                    isEditable = false
                }
                AnswerFieldState.WaitForAnswer -> {
                    background = Color.WHITE
                    text = ""
                    isEditable = true
                    requestFocus()
                }
            }
        }
    }

    override fun refreshQuestionFieldText(text: String) {
        questionTextField.text = text
    }

    override fun refreshValidateButton(state: ValidateButtonState) {
        validateButton.isEnabled = state.isEnabled
    }

    companion object
}