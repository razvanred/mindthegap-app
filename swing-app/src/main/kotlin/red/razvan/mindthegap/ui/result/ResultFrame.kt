package red.razvan.mindthegap.ui.result

import red.razvan.mindthegap.Constants
import red.razvan.mindthegap.ui.AppIcon
import red.razvan.mindthegap.ui.JBar
import red.razvan.mindthegap.ui.home.HomeFrame
import red.razvan.mindthegap.ui.home.create
import red.razvan.mindthegap.ui.quiz.QuizFrame
import red.razvan.mindthegap.ui.quiz.QuizState
import red.razvan.mindthegap.ui.quiz.withPreviousState
import java.awt.Color
import java.awt.Font
import java.awt.GridLayout
import javax.swing.*
import javax.swing.border.EmptyBorder

interface ResultFrame {
    companion object

    fun init(score: Score)

    fun navigateToHomeFrame()

    fun navigateToQuizFrame(state: QuizState)

    fun displayErrorsFrame()
}

fun ResultFrame.Companion.withQuizState(
    state: QuizState,
    builder: JFrame.() -> Unit,
): ResultFrame =
    DefaultResultFrame(quizState = state)
        .also(builder)

private class DefaultResultFrame(
    quizState: QuizState
) : ResultFrame, JFrame("Risultato - ${Constants.APP_NAME}") {

    private val controller = ResultFrameController(
        frame = this,
        quizState = quizState,
    )

    override fun init(score: Score) {
        val retryButton = JButton("Riprova").apply {
            addActionListener { _ ->
                controller.retry()
            }
        }
        val displayErrorsButton = JButton("Vedi errori").apply {
            addActionListener { _ ->
                controller.displayErrors()
            }
        }
        val finishButton = JButton("Fine").apply {
            addActionListener { _ ->
                controller.finish()
            }
        }
        val buttonsPanel = JPanel().apply {
            layout = GridLayout(1, 3)
            add(retryButton)
            add(displayErrorsButton)
            add(finishButton)
        }

        val (description, foregroundColor) = score.grade.toUiAttributes()

        val descriptionLabel = JLabel(description, JLabel.CENTER).apply {
            font = Font("Arial", Font.BOLD, 50)
            foreground = foregroundColor
        }
        val scoreLabel = JLabel("Il tuo punteggio: ${score.correctAnswersCount} / ${score.assignmentsCount} (${score.percentage} %)", JLabel.CENTER).apply {
            font = Font("Arial", Font.BOLD, 20)
        }
        val labelsPanel = JPanel().apply {
            layout = GridLayout(2, 1)
            add(descriptionLabel)
            add(scoreLabel)
            border = EmptyBorder(50, 150, 50, 150)
        }

        val contentPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(labelsPanel)
            add(buttonsPanel)
            border = EmptyBorder(10, 20, 10, 20)
        }

        jMenuBar = JBar(1, this)
        contentPane = contentPanel
        pack()
        setLocationRelativeTo(null)
        isResizable = false
        iconImage = AppIcon
        defaultCloseOperation = EXIT_ON_CLOSE
    }

    override fun navigateToHomeFrame() {
        HomeFrame.create { isVisible = true }
        isVisible = false
        dispose()
    }

    override fun navigateToQuizFrame(state: QuizState) {
        QuizFrame.withPreviousState(state = state) { isVisible = true }
        isVisible = false
        dispose()
    }

    override fun displayErrorsFrame() {
        TODO("display errors")
    }

    private enum class ScoreUiAttributes(
        val description: String,
        val foregroundColor: Color,
    ) {
        G(
            description = "The Gap 🚇",
            foregroundColor = Color.BLACK,
        ),
        F(
            description = "Studia 📚",
            foregroundColor = Color(102, 0, 1),
        ),
        E(
            description = "Riprova 👟",
            foregroundColor = Color(255, 0, 1),
        ),
        D(
            description = "Per poco 🤏",
            foregroundColor = Color(255, 84, 0)
        ),
        C(
            description = "Niente male 🥉",
            foregroundColor = Color(205, 127, 50),
        ),
        B(
            description = "Impressionante 🥈",
            foregroundColor = Color(192, 192, 192),
        ),
        A(
            description = "Perfetto 🥇",
            foregroundColor = Color(255, 191, 0),
        );

        operator fun component1() = description
        operator fun component2() = foregroundColor
    }

    private fun Grade.toUiAttributes(): ScoreUiAttributes =
        when (this) {
            Grade.G -> ScoreUiAttributes.G
            Grade.F -> ScoreUiAttributes.F
            Grade.E -> ScoreUiAttributes.E
            Grade.D -> ScoreUiAttributes.D
            Grade.C -> ScoreUiAttributes.C
            Grade.B -> ScoreUiAttributes.B
            Grade.A -> ScoreUiAttributes.A
        }
}