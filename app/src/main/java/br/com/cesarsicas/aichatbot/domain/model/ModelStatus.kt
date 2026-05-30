package br.com.cesarsicas.aichatbot.domain.model

sealed interface ModelStatus {
    data object Absent : ModelStatus
    data class Transferring(val progress: Float, val label: String) : ModelStatus
    data object Initializing : ModelStatus
    data object Ready : ModelStatus
    data class Failure(val message: String) : ModelStatus
}
