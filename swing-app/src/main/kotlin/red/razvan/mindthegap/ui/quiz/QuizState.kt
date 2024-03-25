package red.razvan.mindthegap.ui.quiz

import red.razvan.mindthegap.model.Assignment

data class QuizState(
    val assignmentStates: List<AssignmentState>,
) {
    companion object

    fun evaluate(at: UInt, answer: String) {
        val a = answer.trim()
        val assignmentState = assignmentStates[at.toInt()]

        val isCorrect = assignmentState
            .assignment
            .en
            .values
            .any { solution ->
                solution.equals(a.trim(), ignoreCase = true)
            }

        assignmentState.evaluation = Evaluation.Answered(
            isCorrect = isCorrect,
            userAnswer = a,
        )
    }

    fun skip(at: UInt) {
        assignmentStates[at.toInt()].evaluation = Evaluation.Skipped
    }
}

fun QuizState.Companion.fromAssignments(assignments: List<Assignment>): QuizState =
    QuizState(
        assignmentStates = assignments
            .map {
                AssignmentState(
                    assignment = it
                )
            }
    )

val QuizState.errorsCount: UInt
    get() = assignmentStates
        .count { assignment ->
            assignment.evaluation
                .let { evaluation ->
                    evaluation != null && (evaluation as? Evaluation.Answered)?.isCorrect != true
                }
        }
        .toUInt()

val QuizState.skippedCount: UInt
    get() = assignmentStates
        .count { assignment ->
            assignment.evaluation == Evaluation.Skipped
        }
        .toUInt()

val QuizState.missingCount: UInt
    get() = assignmentStates
        .count { it.evaluation == null }
        .toUInt()

data class AssignmentState(
    val assignment: Assignment,
    var evaluation: Evaluation? = null,
)

sealed interface Evaluation {
    data object Skipped : Evaluation

    data class Answered(
        val userAnswer: String,
        val isCorrect: Boolean
    ) : Evaluation
}