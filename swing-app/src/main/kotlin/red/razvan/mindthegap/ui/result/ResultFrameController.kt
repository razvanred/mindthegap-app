package red.razvan.mindthegap.ui.result

import red.razvan.mindthegap.ui.quiz.QuizState
import red.razvan.mindthegap.ui.quiz.correctCount
import java.math.RoundingMode

class ResultFrameController(
    private val frame: ResultFrame,
    private val quizState: QuizState,
) {
    init {
        val correctAnswersCount = quizState.correctCount
        val assignmentsCount = quizState
            .assignmentStates
            .size
            .toUInt()
        val percentage = (correctAnswersCount.toLong().toBigDecimal().divide(assignmentsCount.toLong().toBigDecimal(), 2, RoundingMode.FLOOR) * 100.toBigDecimal())
            .toInt()
            .toUInt()

        val grade = if (percentage < 25U) {
            Grade.G
        } else if (percentage < 50U) {
            Grade.F
        } else if (percentage < 60U) {
            Grade.E
        } else if (percentage < 70U)  {
            Grade.D
        } else if (percentage < 85U) {
            Grade.C
        } else if (percentage < 100U) {
            Grade.B
        } else if (percentage == 100U) {
            Grade.A
        } else {
            throw IllegalStateException("Illegal percentage: $percentage % ($correctAnswersCount / $assignmentsCount)")
        }

        val score = Score(
            correctAnswersCount = correctAnswersCount,
            assignmentsCount = assignmentsCount,
            percentage = percentage,
            grade = grade,
        )

        frame.init(score)
    }

    fun retry() {
        frame.navigateToQuizFrame(state = quizState)
    }

    fun displayErrors() {
        frame.displayErrorsFrame()
    }

    fun finish() {
        frame.navigateToHomeFrame()
    }
}

data class Score(
    val correctAnswersCount: UInt,
    val assignmentsCount: UInt,
    val grade: Grade,
    val percentage: UInt,
) {
    init {
        require(percentage <= 100U)
        require(correctAnswersCount <= assignmentsCount)
    }
}

enum class Grade {
    G,
    F,
    E,
    D,
    C,
    B,
    A
}