package io.btc.lsp.protocol

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Various Params for [NotificationMessage]
 * */
@Serializable
sealed class NotificationMessageParams

@Serializable
data class ProgressParams<T: WorkDoneProgress> (
    val token: ProgressToken,
    val value: T
): NotificationMessageParams()

@Serializable
data class LogMessageParams(
    val id: Long? = null,
    val message: String
): NotificationMessageParams()

/**
 * The document open notification is sent from the client to the server to signal newly opened text documents.
 * The document’s content is now managed by the client and the server must not try to read the document’s content using the document’s Uri.
 * Open in this sense means it is managed by the client.
 * It doesn’t necessarily mean that its content is presented in an editor.
 * An open notification must not be sent more than once without a corresponding close notification send before.
 * This means open and close notification must be balanced and the max open count for a particular textDocument is one.
 * Note that a server’s ability to fulfill requests is independent of whether a text document is open or closed.
 *
 * The DidOpenTextDocumentParams contain the language id the document is associated with.
 * If the language Id of a document changes, the client needs to send a textDocument/didClose to the server
 * followed by a textDocument/didOpen with the new language id if the server handles the new language id as well.
 */
@Serializable
data class DidOpenTextDocumentParams (
    /**
     * The document that was opened.
     */
    val textDocument: TextDocumentItem
): NotificationMessageParams()

@Serializable
data class TextDocumentItem (
    /**
     * The text document's URI.
     */
    val uri: DocumentUri,

    /**
     * The text document's language identifier. see [LanguageId]
     */
    val languageId: String,

    /**
     * The version number of this document (it will increase after each
     * change, including undo/redo).
     */
    val version: Long,

    /**
     * The content of the opened text document.
     */
    val text: String,
)
typealias ProgressToken = String

interface WorkDoneProgressParams{
    val workDoneToken: ProgressToken?
}

class WorkDoneProgressSerializer: JsonContentPolymorphicSerializer<WorkDoneProgress>(WorkDoneProgress::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out WorkDoneProgress> {
        return when(element.jsonObject["kind"]!!.jsonPrimitive.content) {
            "begin" -> WorkDoneProgressBegin.serializer()
            "report" -> WorkDoneProgressReport.serializer()
            "end" -> WorkDoneProgressReport.serializer()
            else -> throw RuntimeException("No appropriate deserializer for an apparent WorkDoneProgress object")
        }
    }

}

@Serializable(with = WorkDoneProgressSerializer::class)
sealed class WorkDoneProgress

@Serializable
data class WorkDoneProgressBegin (

    val kind: String = "begin",

    /**
     * Mandatory title of the progress operation. Used to briefly inform about
     * the kind of operation being performed.
     *
     * Examples: "Indexing" or "Linking dependencies".
     */
    val title:String,

    /**
     * Controls if a cancel button should show to allow the user to cancel the
     * long running operation. Clients that don't support cancellation are allowed
     * to ignore the setting.
     */
    val cancellable:Boolean?,

    /**
     * Optional, more detailed associated progress message. Contains
     * complementary information to the `title`.
     *
    val  * Examples:"3/25 files", "project/src/module2", "node_modules/some_dep".
     * If unset, the previous progress message (if any) is still valid.
     */
    val message:String?,

    /**
     * Optional progress percentage to display (value 100 is considered 100%).
     * If not provided infinite progress is assumed and clients are allowed
     * to ignore the `percentage` value in subsequent in report notifications.
     *
     * The value should be steadily rising. Clients are free to ignore values
     * that are not following this rule.
     */
    val percentage:Double?
): WorkDoneProgress()

@Serializable
data class WorkDoneProgressReport (

    val kind: String = "report",

    /**
     * Controls enablement state of a cancel button. This property is only valid if a cancel
     * button got requested in the `WorkDoneProgressStart` payload.
     *
     * Clients that don't support cancellation or don't support control the button's
     * enablement state are allowed to ignore the setting.
     */
    val cancellable: Boolean?,

    /**
     * Optional, more detailed associated progress message. Contains
     * complementary information to the `title`.
     *
    val  * Examples: "3/25 files", "project/src/module2", "node_modules/some_dep".
     * If unset, the previous progress message (if any) is still valid.
     */
    val message: String?,

    /**
     * Optional progress percentage to display (value 100 is considered 100%).
     * If not provided infinite progress is assumed and clients are allowed
     * to ignore the `percentage` value in subsequent in report notifications.
     *
     * The value should be steadily rising. Clients are free to ignore values
     * that are not following this rule.
     */
    val percentage: Double?
): WorkDoneProgress()

@Serializable
data class WorkDoneProgressEnd (

    val kind: String = "end",

    /**
     * Optional, a final message indicating to for example indicate the outcome
     * of the operation.
     */
    val message: String?
): WorkDoneProgress()

