package io.btc.lsp.protocol

import io.btc.lsp.JSON_RPC_VERSION
import io.btc.lsp.capability.TextDocumentSyncKind
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object MessageSerializer: JsonContentPolymorphicSerializer<Message>(Message::class) {

    val idToSerializerMap: MutableMap<String, KSerializer<out Any>> = mutableMapOf()

    operator fun set(id: String, serializer: KSerializer<out Any>) {
        idToSerializerMap[id] = serializer
    }

    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out Message> {
        return when {
            "result" in element.jsonObject -> {
                val serializerToUse= try {
                    val idValue = element.jsonObject["id"]!!.jsonPrimitive.content
                    idToSerializerMap[idValue]!!
                        .also {
                            idToSerializerMap.remove(idValue)
                        }
                } catch (exception: NullPointerException) {
                    String.serializer()
                }
                ResponseMessage.serializer(serializerToUse)
            }
            "error" in element.jsonObject -> {
                ResponseMessage.serializer(String.serializer())
            }
            "method" in element.jsonObject -> {
                if ("id" in element.jsonObject) {
                    RequestMessage.serializer()
                } else {
                    when (element.jsonObject["method"]!!.jsonPrimitive.content) {
                        "$/progress" -> NotificationMessage.serializer(ProgressParams.serializer(WorkDoneProgress.serializer()))
                        else -> NotificationMessage.serializer(LogMessageParams.serializer())
                    }
                }
            }
            else -> Message.serializer()
        }
    }
}

@Serializable
sealed class Message(
    open val jsonrpc: String = JSON_RPC_VERSION
)

@Serializable
data class RequestMessage(
    val id: String,
    val method: String,
    val params: RequestMessageParams?
): Message()

@Serializable
data class ResponseMessage<T: Any>(
    val id: String? = null,
    val result: T? = null,
    val error: ResponseError? = null
): Message()

@Serializable
data class ResponseError(
    val code: Long,
    val message: String,
    val data: String?
)

const val ParseError: Long = -32700;
const val InvalidRequest: Long = -32600;
const val MethodNotFound: Long = -32601;
const val InvalidParams: Long = -32602;
const val InternalError: Long = -32603;
const val serverErrorStart: Long = -32099;
const val serverErrorEnd: Long = -32000;
const val ServerNotInitialized: Long = -32002;
const val UnknownErrorCode: Long = -32001;
const val RequestCancelled: Long = -32800;
const val ContentModified: Long = -32801;

@Serializable
data class NotificationMessage<T: NotificationMessageParams>(
    val method: String,
    val params: T
): Message()


@Serializable
data class DocumentFilter (
	/**
	 * A language id, like `typescript`. See [LanguageId]
	 */
	val language: String,

	/**
	 * A Uri [scheme](#Uri.scheme), like `file` or `untitled`.
	 */
	val scheme: String? = null,
    /** A glob pattern, like `*.{ts,js}`.
     *
     * Glob patterns can have the following syntax:
     * - `*` to match one or more characters in a path segment
     * - `?` to match on one character in a path segment
     * - `**` to match any number of path segments, including none
     * - `{}` to group conditions (e.g. `**​.{ts,js}` matches all TypeScript and JavaScript files)
     * - `[]` to declare a range of characters to match in a path segment (e.g., `example.[0-9]` to match on `example.0`, `example.1`, …)
     * - `[!...]` to negate a range of characters to match in a path segment (e.g., `example.[!0-9]` to match on `example.a`, `example.b`, but not `example.0`)
     */
	val pattern: String? = null
)
typealias DocumentSelector = Array<DocumentFilter>

/**
 * General text document registration options.
 */
interface TextDocumentRegistrationOptions {
	/**
	 * A document selector to identify the scope of the registration. If set to null
	 * the document selector provided on the client side will be used.
	 */
	val documentSelector: DocumentSelector?
}


/**
 * The document change notification is sent from the client to the server to signal changes to a text document.
 * Before a client can change a text document it must claim ownership of its content using the textDocument/didOpen notification.
 * In 2.0 the shape of the params has changed to include proper version numbers and language ids.
 *
 * Describe options to be used when registering for text document change events.
 */
@Serializable
data class TextDocumentChangeRegistrationOptions (
    /**
	 * How documents are synced to the server. See TextDocumentSyncKind.Full
	 * and TextDocumentSyncKind.Incremental.
	 */
	val syncKind: TextDocumentSyncKind,
    override val documentSelector: DocumentSelector? = null
): TextDocumentRegistrationOptions

@Serializable
data class ClientInfo(
    val name: String,
    val version: String?
)

/**
 * MarkedString can be used to render human readable text. It is either a markdown string
 * or a code-block that provides a language and a code snippet. The language identifier
 * is semantically equal to the optional language identifier in fenced code blocks in GitHub
 * issues. See https://help.github.com/articles/creating-and-highlighting-code-blocks/#syntax-highlighting
 *
 * The pair of a language and a value is an equivalent to markdown:
 * ```${language}
 * ${value}
 * ```
 *
 * Note that markdown strings will be sanitized - that means html will be escaped.
* @deprecated use MarkupContent instead.
*/


interface CancelParams {
    val id: String
}

typealias DocumentUri = String

val EOL = arrayOf("\n", "\r\n", "\r")

@Serializable
data class Position (
    val line: Long,
    val character: Long
)

@Serializable
data class Range (
	val start: Position,
	val end: Position
)

@Serializable
data class LocationLink(
	val originSelectionRange: Range?,
	val targetUri: DocumentUri,
	val targetRange: Range,
	val targetSelectionRange: Range
)

@Serializable
data class Diagnostic (
	val range: Range,
	val severity: DiagnosticSeverity?,
	val code: String?,
	val source: String?,
	val message: String,
	val tags: List<DiagnosticTag> = emptyList(),
	val relatedInformation: List<DiagnosticRelatedInformation> = emptyList()
)

typealias URI = String

@Serializable
enum class DiagnosticSeverity{
    UnusedEnumAddedForParity,
    Error,
    Warning,
    Information,
    Hint
}


enum class DiagnosticTag{
    UnusedEnumAddedForParity,
    Unnecessary,
    Deprecated
}

val allDiagnosticTags = DiagnosticTag.values().toList().minus(DiagnosticTag.UnusedEnumAddedForParity).map { it.ordinal }

@Serializable
data class DiagnosticRelatedInformation (
	val location: Location,
	val message: String
)

@Serializable
data class Location (
	val uri: DocumentUri,
	val range: Range
)

@Serializable
data class WorkspaceEdit (
    /**
     * Holds changes to existing resources.
     */
    val changes: Map<DocumentUri, TextEdit> = emptyMap(),

    /**
     * Depending on the client capability `workspace.workspaceEdit.resourceOperations` document changes
     * are either an array of `TextDocumentEdit`s to express changes to n different text documents
     * where each text document edit addresses a specific version of a text document. Or it can contain
     * above `TextDocumentEdit`s mixed with create, rename and delete file / folder operations.
     *
     * Whether a client supports versioned document edits is expressed via
     * `workspace.workspaceEdit.documentChanges` client capability.
     *
     * If a client neither supports `documentChanges` nor `workspace.workspaceEdit.resourceOperations` then
     * only plain `TextEdit`s using the `changes` property are supported.
     *
     * Should be : (TextDocumentEdit[] | (TextDocumentEdit | CreateFile | RenameFile | DeleteFile)[])
     */
    val documentChanges: List<TextDocumentEdit> = emptyList()
)

@Serializable
data class TextDocumentEdit (
    /**
     * The text document to change.
     */
    val textDocument: VersionedTextDocumentIdentifier,

    /**
     * The edits to be applied.
     */
    val edits: List<TextEdit> = emptyList()
)

@Serializable
data class TextDocumentIdentifier (
    val uri: DocumentUri
)

@Serializable
data class VersionedTextDocumentIdentifier(
    /**
     * The version number of this document. If a versioned text document identifier
     * is sent from the server to the client and the file is not open in the editor
     * (the server has not received an open notification before) the server can send
     * `null` to indicate that the version is known and the content on disk is the
     * master (as speced with document content ownership).
     *
     * The version number of a document will increase after each change, including
     * undo/redo. The number doesn't need to be consecutive.
     */
    val version: Long? = null,
    val uri: DocumentUri
)


/** git-commit and git-rebase, objective-c, objective-cpp not included. TODO */
enum class LanguageId {
    abap,
    bat,
    bibtex,
    clojure,
    coffeescript,
    c,
    cpp,
    csharp,
    css,
    diff,
    dart,
    dockerfile,
    elixir,
    erlang,
    fsharp,
    go,
    groovy,
    handlebars,
    html,
    ini,
    java,
    javascript,
    javascriptreact,
    json,
    latex,
    less,
    lua,
    makefile,
    markdown,
    perl,
    perl6,
    php,
    powershell,
    jade,
    python,
    r,
    razor,
    ruby,
    rust,
    scss,
    sass,
    scala,
    shaderlab,
    shellscript,
    sql,
    swift,
    typescript,
    typescriptreact,
    tex,
    vb,
    xml,
    xsl,
    yaml
}


