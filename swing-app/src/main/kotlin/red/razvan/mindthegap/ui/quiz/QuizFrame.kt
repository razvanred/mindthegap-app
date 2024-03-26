package red.razvan.mindthegap.ui.quiz

import red.razvan.mindthegap.Constants
import red.razvan.mindthegap.data.FilesRepository
import red.razvan.mindthegap.model.Assignment
import red.razvan.mindthegap.model.File
import red.razvan.mindthegap.ui.AppIcon
import red.razvan.mindthegap.ui.JBar
import red.razvan.mindthegap.ui.result.ResultFrame
import red.razvan.mindthegap.ui.result.withQuizState
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Font
import java.awt.GridLayout
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.border.EmptyBorder
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

    fun refreshBackButton(state: BackButtonState)

    fun navigateToResultFrame(state: QuizState)
}

fun QuizFrame.Companion.withFileIds(
    fileIds: List<File.Id>,
    shuffle: Boolean = false,
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
                if (shuffle) {
                    assignments.shuffled()
                } else {
                    assignments
                }
            }
    )
        .also(builder)

fun QuizFrame.Companion.withPreviousState(
    state: QuizState,
    shuffle: Boolean = false,
    builder: JFrame.() -> Unit = {},
) : QuizFrame =
    DefaultQuizFrame(
        assignments = state
            .assignmentStates
            .map { assignmentState ->
                assignmentState.assignment
            }
            .let { assignments ->
                if (shuffle) {
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
        JTextField(20).apply {
            horizontalAlignment = SwingConstants.CENTER
        }

    private val questionTextField = createWordTextField().apply {
        isEditable = false
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

    private val skipButton = JButton("Salta").apply {
        addActionListener { _ ->
            val result = JOptionPane.showConfirmDialog(
                this,
                "Il quesito saltato verrà conteggiato tra gli errori",
                "Salta quesito",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
            )
            if (result == JOptionPane.OK_OPTION) {
                controller.skip()
            }
        }
    }

    private val backButton = JButton("Indietro").apply {
        addActionListener { _ ->
            controller.back()
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
                add(JLabel(imageIcon, JLabel.CENTER).apply {
                    border = EmptyBorder(0, 0, 10, 0)
                }, "North")
                add(textField, "Center")
            }

        val questionPanel = createTextFieldPanel(
            imageIcon = ImageIcon(ImageIO.read(this::class.java.getResourceAsStream("/it.png"))),
            textField = questionTextField
        ).apply {
            border = EmptyBorder(5, 0, 10, 5)
        }

        val answerPanel = createTextFieldPanel(
            imageIcon = ImageIcon(ImageIO.read(this::class.java.getResourceAsStream("/uk.png"))),
            textField = answerTextField
        ).apply {
            border = EmptyBorder(5, 5, 10, 0)
        }

        val assignmentPanel = JPanel().apply {
            layout = BorderLayout()
            add(questionPanel, "West")
            add(answerPanel, "East")
        }

        val controlButtonsPanel = JPanel().apply {
            layout = BorderLayout()
            add(validateButton, "Center")
            add(JPanel().apply {
                layout = GridLayout(2, 1)
                add(skipButton)
                add(backButton)
            }, "West")
            add(nextButton, "East")
            border = EmptyBorder(5, 0, 5, 0)
        }

        val panel4 = JPanel().apply {
            layout = GridLayout(2, 1)
            add(assignmentPanel)
            add(controlButtonsPanel)
        }

        val labelsPanel = JPanel().apply {
            layout = BorderLayout()
            add(errorsLabel, "West")
            add(missingLabel, "East")
            border = EmptyBorder(5, 0, 0, 0)
        }

        val contentPanel = JPanel().apply {
            layout = BorderLayout()
            add(panel4, "Center")
            add(labelsPanel, "South")
            border = EmptyBorder(10, 10, 10, 10)
        }

        contentPane.add(contentPanel)
        iconImage = AppIcon

        jMenuBar = JBar(1, this)

        pack()
        isResizable = false
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)
    }

    override fun refreshNextButton(state: NextButtonState) {
        with(nextButton) {
            text = when (state.label) {
                NextButtonState.Label.Finish -> "Fine"
                NextButtonState.Label.Next -> "Avanti"
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
        val (errors, skipped) = state
        val errorsText = when (errors) {
            0U -> "Nessun errore"
            1U -> "Un errore"
            else -> "$errors errori"
        }
        val skippedText = if (skipped == 1U) {
            "uno saltato"
        } else {
            "$skipped saltati"
        }
        errorsLabel.text = errorsText + if (skipped > 0U) " (di cui $skippedText)" else ""
    }

    override fun refreshAnswerFieldState(state: AnswerFieldState) {
        with(answerTextField) {
            when (state) {
                is AnswerFieldState.Answered, AnswerFieldState.Skipped -> {
                    skipButton.isEnabled = false
                    background = if (state is AnswerFieldState.Answered) {
                        when (state.isCorrect) {
                            true -> Color.GREEN
                            false -> Color.RED
                        }
                    } else {
                        Color.ORANGE
                    }
                    text = if (state is AnswerFieldState.Answered) {
                        state.userAnswer
                    } else {
                        "<Saltata>"
                    }
                    isEditable = false
                    isFocusable = false
                    rootPane.defaultButton = nextButton
                }

                AnswerFieldState.WaitForAnswer -> {
                    skipButton.isEnabled = true
                    isFocusable = true
                    background = UIManager.getColor("TextField.inactiveBackground")
                    text = ""
                    isEditable = true
                    requestFocus()
                    rootPane.defaultButton = validateButton
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

    override fun refreshBackButton(state: BackButtonState) {
        backButton.isEnabled = state.isEnabled
    }

    override fun navigateToResultFrame(state: QuizState) {
        ResultFrame.withQuizState(state = state) { isVisible = true }
        isVisible = false
        dispose()
    }

    companion object
}