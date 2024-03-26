package red.razvan.mindthegap.ui.quiz

import red.razvan.mindthegap.model.Assignment

class QuizFrameController(
    private val frame: QuizFrame,
    assignments: List<Assignment>,
) {
    private var at: UInt = 0U
    private val state = QuizState.fromAssignments(assignments = assignments)

    init {
        with (frame) {
            init()
            refreshAll()
        }
    }

    fun onAnswerFieldTextChanged(text: String) {
        frame.refreshValidateButton(ValidateButtonState(isEnabled = text.isNotBlank() && state.assignmentStates[at.toInt()].evaluation == null))
    }

    fun validate(answerFieldText: String) {
        frame.refreshValidateButton(ValidateButtonState(isEnabled = false))
        val userAnswer = answerFieldText.trim()
        state.evaluate(at = at, answer = userAnswer)
        refreshLabels()

        val evaluation = requireNotNull(state.assignmentStates[at.toInt()].evaluation)
        require(evaluation is Evaluation.Answered) {
            "Just validated the answer, so it cannot be Skipped or null"
        }

        frame.refreshAnswerFieldState(evaluation.toAnswerFieldState())
        refreshNextButton()
        refreshLabels()
    }

    fun skip() {
        state.skip(at = at)
        refreshLabels()
        next()
    }

    fun next() {
        at++
        if (at == state.assignmentStates.size.toUInt()) {
            frame.navigateToResultFrame(state = state)
        } else {
            refreshNavigator()
        }
    }

    fun back() {
        at--
        refreshNavigator()
    }

    private fun refreshAll() {
        refreshNavigator()
        refreshLabels()
    }

    private fun refreshNavigator() {
        val assignmentState = state.assignmentStates[at.toInt()]
        with(frame) {
            refreshQuestionFieldText(assignmentState.assignment.it.values.first())
            refreshAnswerFieldState(assignmentState.evaluation.toAnswerFieldState())
            refreshBackButton(BackButtonState(isEnabled = at > 0U))
        }
        refreshNextButton()
    }

    private fun refreshLabels() {
        with (frame) {
            refreshErrorsLabel(ErrorsLabelState(errors = state.errorsCount, skipped = state.skippedCount))
            refreshMissingLabel(MissingLabelState(missing = state.missingCount))
        }
    }

    private fun refreshNextButton() {
        frame.refreshNextButton(
            NextButtonState(
                label = if (at < state.assignmentStates.lastIndex.toUInt()) {
                    NextButtonState.Label.Next
                } else {
                    NextButtonState.Label.Finish
                },
                isEnabled = state.assignmentStates[at.toInt()].evaluation != null
            )
        )
    }
}

@JvmInline
value class ValidateButtonState(val isEnabled: Boolean)

data class ErrorsLabelState(val errors: UInt, val skipped: UInt) {
    init {
        require(skipped <= errors)
    }
}

@JvmInline
value class MissingLabelState(val missing: UInt)

@JvmInline
value class BackButtonState(val isEnabled: Boolean)

data class NextButtonState(
    val isEnabled: Boolean,
    val label: Label,
) {
    enum class Label {
        Next,
        Finish
    }
}

sealed interface AnswerFieldState {
    data object WaitForAnswer : AnswerFieldState
    data class Answered(val userAnswer: String, val isCorrect: Boolean): AnswerFieldState
    data object Skipped : AnswerFieldState
}

private fun Evaluation?.toAnswerFieldState(): AnswerFieldState =
    when (this) {
        null -> AnswerFieldState.WaitForAnswer
        is Evaluation.Answered -> {
            AnswerFieldState.Answered(
                isCorrect = isCorrect,
                userAnswer = userAnswer
            )
        }
        Evaluation.Skipped -> AnswerFieldState.Skipped
    }