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
        frame.refreshValidateButton(ValidateButtonState(isEnabled = text.isNotBlank()))
    }

    fun validate(answerFieldText: String) {
        frame.refreshValidateButton(ValidateButtonState(isEnabled = false))
        val userAnswer = answerFieldText.trim()
        state.evaluate(at = at, answer = userAnswer)
        refreshLabels()

        val evaluation = requireNotNull(state.assignmentStates[at.toInt()].evaluation)
        evaluation !is Evaluation.Skipped

        frame.refreshAnswerFieldState(
            AnswerFieldState.Answered(
                isCorrect = evaluation is Evaluation.Correct
            )
        )
        refreshNextButton()
    }

    fun skip() {
        state.skip(at = at)
        refreshAll()
    }

    fun next() {
        at++
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
            refreshAnswerFieldState(AnswerFieldState.WaitForAnswer)
        }
        refreshNextButton()
    }

    private fun refreshLabels() {
        with (frame) {
            refreshErrorsLabel(ErrorsLabelState(errors = state.errorsCount))
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

@JvmInline
value class ErrorsLabelState(val errors: UInt)

@JvmInline
value class MissingLabelState(val missing: UInt)

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
    data class Answered(val isCorrect: Boolean) : AnswerFieldState
}