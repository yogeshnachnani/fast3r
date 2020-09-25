package io.btc.lsp.protocol

import io.btc.lsp.capability.ClientCapabilities
import io.btc.lsp.capability.CodeActionKind
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Various Params for [RequestMessage]
 * */

@Serializable
sealed class RequestMessageParams

@Serializable
data class InitializeParams (
    val workDoneToken: ProgressToken,
    val processId: Long?,
    val clientInfo: ClientInfo,
    val rootPath: String?,
    val rootUri: DocumentUri?,
    val initializationOptions: String?,
    val capabilities: ClientCapabilities,
    /**
     * 'off' | 'messages' | 'verbose'
     */
    val trace: String? = "off",
    val workspaceFolders: List<WorkspaceFolder> = emptyList()
): RequestMessageParams()

@Serializable
data class WorkspaceFolder (
    /**
     * The associated URI for this workspace folder.
     */
    val uri: String,

    /**
     * The name of the workspace folder. Used to refer to this
     * workspace folder in the user interface.
     */
    val name: String
)

@Serializable
data class WorkspaceSymbolParams (
    /**
     * A query string to filter symbols by. Clients may send an empty
     * string here to request all symbols.
     */
    val query: String,
    override val workDoneToken: ProgressToken?,
    override val partialResultToken: ProgressToken?
): RequestMessageParams(), WorkDoneProgressParams, PartialResultParams

@Serializable
data class ApplyWorkspaceEditParams (
    /**
     * An optional label of the workspace edit. This label is
     * presented in the user interface for example on an undo
     * stack to undo the workspace edit.
     */
    val label: String? = null,

    /**
     * The edits to apply.
     */
    val edit: WorkspaceEdit
): RequestMessageParams()

interface PartialResultParams {
    /**
     * An optional token that a server can use to report partial results (e.g. streaming) to
     * the client.
     */
    val partialResultToken: ProgressToken?
}

interface TextDocumentPositionParams {
    /**
     * The text document.
     */
    val textDocument: TextDocumentIdentifier

    /**
     * The position inside the text document.
     */
    val position: Position
}
@Serializable
data class HoverParams(
    override val workDoneToken: ProgressToken,
    override val textDocument: TextDocumentIdentifier,
    override val position: Position
): TextDocumentPositionParams, WorkDoneProgressParams, RequestMessageParams()

/**
 * How a signature help was triggered.
 *
 * @since 3.15.0
 */
@Serializable(with = SignatureHelpTriggerKindSerializer::class)
enum class SignatureHelpTriggerKind {
    UnusedEnumAddedForParity,
    /**
     * Signature help was invoked manually by the user or by a command.
     */
    Invoked,
    /**
     * Signature help was triggered by a trigger character.
     */
    TriggerCharacter,
    /**
     * Signature help was triggered by the cursor moving or by the document content changing.
     */
    ContentChange;
}

class SignatureHelpTriggerKindSerializer: KSerializer<SignatureHelpTriggerKind> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("SignatureHelpTriggerKind", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): SignatureHelpTriggerKind {
        val ordinalValue = decoder.decodeInt()
        return SignatureHelpTriggerKind.values()[ordinalValue]
    }

    override fun serialize(encoder: Encoder, value: SignatureHelpTriggerKind) {
        encoder.encodeInt(value.ordinal)
    }

}
/**
 * Additional information about the context in which a signature help request was triggered.
 *
 * @since 3.15.0
 */
@Serializable
data class SignatureHelpContext (
    /**
     * Action that caused signature help to be triggered.
     */
    val triggerKind: SignatureHelpTriggerKind,

    /**
     * Character that caused signature help to be triggered.
     *
     * This is undefined when `triggerKind !== SignatureHelpTriggerKind.TriggerCharacter`
     */
    val triggerCharacter: String? = null,

    /**
     * `true` if signature help was already showing when it was triggered.
     *
     * Retriggers occur when the signature help is already active and can be caused by actions such as
     * typing a trigger character, a cursor move, or document content changes.
     */
    val isRetrigger: Boolean,

    /**
     * The currently active `SignatureHelp`.
     *
     * The `activeSignatureHelp` has its `SignatureHelp.activeSignature` field updated based on
     * the user navigating through available signatures.
     */
    val activeSignatureHelp: SignatureHelp? = null
)

@Serializable
data class SignatureHelpParams (
    /**
     * The signature help context. This is only available if the client specifies
     * to send this using the client capability  `textDocument.signatureHelp.contextSupport === true`
     *
     * @since 3.15.0
     */
    val context: SignatureHelpContext? = null,
    override val textDocument: TextDocumentIdentifier,
    override val position: Position,
    override val workDoneToken: ProgressToken?
):  TextDocumentPositionParams, WorkDoneProgressParams, RequestMessageParams()

@Serializable
data class DeclarationParams(
    override val textDocument: TextDocumentIdentifier,
    override val position: Position,
    override val workDoneToken: ProgressToken?
): TextDocumentPositionParams, WorkDoneProgressParams, RequestMessageParams()

@Serializable
data class DefinitionParams(
    override val textDocument: TextDocumentIdentifier,
    override val position: Position,
    override val workDoneToken: ProgressToken?,
    override val partialResultToken: ProgressToken?
): TextDocumentPositionParams, WorkDoneProgressParams, PartialResultParams, RequestMessageParams()

@Serializable
data class TypeDefinitionParams(
    override val textDocument: TextDocumentIdentifier,
    override val position: Position,
    override val workDoneToken: ProgressToken?,
    override val partialResultToken: ProgressToken?
): TextDocumentPositionParams, WorkDoneProgressParams, PartialResultParams, RequestMessageParams()

@Serializable
data class ImplementationParams(
    override val textDocument: TextDocumentIdentifier,
    override val position: Position,
    override val workDoneToken: ProgressToken?,
    override val partialResultToken: ProgressToken?
): TextDocumentPositionParams, WorkDoneProgressParams, PartialResultParams, RequestMessageParams()

@Serializable
data class ReferenceParams (
    val context: ReferenceContext,
    override val textDocument: TextDocumentIdentifier,
    override val position: Position,
    override val workDoneToken: ProgressToken?,
    override val partialResultToken: ProgressToken?
): TextDocumentPositionParams, WorkDoneProgressParams, PartialResultParams, RequestMessageParams()

@Serializable
data class ReferenceContext (
    /**
     * Include the declaration of the current symbol.
     */
    val includeDeclaration: Boolean
)

@Serializable
data class DocumentHighlightParams(
    override val textDocument: TextDocumentIdentifier,
    override val position: Position,
    override val workDoneToken: ProgressToken?,
    override val partialResultToken: ProgressToken?
): TextDocumentPositionParams, WorkDoneProgressParams, PartialResultParams, RequestMessageParams()

@Serializable
data class DocumentSymbolParams (
    /**
     * The text document.
     */
    val textDocument: TextDocumentIdentifier,
    override val workDoneToken: ProgressToken?,
    override val partialResultToken: ProgressToken?
): WorkDoneProgressParams, PartialResultParams, RequestMessageParams()


/**
 * Params for the CodeActionRequest
 */
@Serializable
data class CodeActionParams (
    /**
     * The document in which the command was invoked.
     */
    val textDocument: TextDocumentIdentifier,

    /**
     * The range for which the command was invoked.
     */
    val range: Range,

    /**
     * Context carrying additional information.
     */
    val context: CodeActionContext,
    override val workDoneToken: ProgressToken?,
    override val partialResultToken: ProgressToken?

): WorkDoneProgressParams, PartialResultParams, RequestMessageParams()

/**
 * Contains additional diagnostic information about the context in which
 * a code action is run.
 */
@Serializable
data class CodeActionContext (
    /**
     * An array of diagnostics known on the client side overlapping the range provided to the
     * `textDocument/codeAction` request. They are provided so that the server knows which
     * errors are currently presented to the user for the given range. There is no guarantee
     * that these accurately reflect the error state of the resource. The primary parameter
     * to compute code actions is the provided range.
     */
    val diagnostics: List<Diagnostic>,

    /**
     * Requested kind of actions to return.
     *
     * Actions not of this kind are filtered out by the client before being shown. So servers
     * can omit computing them.
     */
    val only: List<CodeActionKind>? = emptyList()
)

@Serializable
data class CodeLensParams(
    /**
     * The document to request code lens for.
     */
    val textDocument: TextDocumentIdentifier,
    override val workDoneToken: ProgressToken?,
    override val partialResultToken: ProgressToken?
): WorkDoneProgressParams, PartialResultParams, RequestMessageParams()

@Serializable
data class DocumentLinkParams(
    /**
     * The document to request code lens for.
     */
    val textDocument: TextDocumentIdentifier,
    override val workDoneToken: ProgressToken?,
    override val partialResultToken: ProgressToken?
): WorkDoneProgressParams, PartialResultParams, RequestMessageParams()


@Serializable
data class DocumentColorParams(
    /**
     * The document to request code lens for.
     */
    val textDocument: TextDocumentIdentifier,
    override val workDoneToken: ProgressToken?,
    override val partialResultToken: ProgressToken?
): WorkDoneProgressParams, PartialResultParams, RequestMessageParams()

@Serializable
data class ColorPresentationParams (
    /**
     * The text document.
     */
    val textDocument: TextDocumentIdentifier,

    /**
     * The color information to request presentations for.
     */
    val color: Color,

    /**
     * The range where the color would be inserted. Serves as a context.
     */
    val range: Range,
    override val workDoneToken: ProgressToken?,
    override val partialResultToken: ProgressToken?

): WorkDoneProgressParams, PartialResultParams, RequestMessageParams()

@Serializable
data class DocumentFormattingParams (
    /**
     * The document to format.
     */
    val textDocument: TextDocumentIdentifier,

    /**
     * The format options.
     */
    val options: FormattingOptions,
    override val workDoneToken: ProgressToken?
): WorkDoneProgressParams, RequestMessageParams()

/**
 * Value-object describing what options formatting should use.
 */
@Serializable
data class FormattingOptions (
    /**
     * Size of a tab in spaces.
     */
    val tabSize: Int,

    /**
     * Prefer spaces over tabs.
     */
    val insertSpaces: Boolean,

    /**
     * Trim trailing whitespace on a line.
     *
     * @since 3.15.0
     */
    val trimTrailingWhitespace: Boolean = false,

    /**
     * Insert a newline character at the end of the file if one does not exist.
     *
     * @since 3.15.0
     */
    val insertFinalNewline: Boolean? = false,

    /**
     * Trim all newlines after the final newline at the end of the file.
     *
     * @since 3.15.0
     */
    val trimFinalNewlines: Boolean? = null,

    /**
     * Signature for further properties.
     */
    val key: Map<String, String>
)

@Serializable
data class DocumentRangeFormattingParams (
    /**
     * The document to format.
     */
    val textDocument: TextDocumentIdentifier,

    /**
     * The range to format
     */
    val range: Range,

    /**
     * The format options
     */
    val options: FormattingOptions,
    override val workDoneToken: ProgressToken?,
): WorkDoneProgressParams, RequestMessageParams()

@Serializable
data class DocumentOnTypeFormattingParams (
    /**
     * The character that has been typed.
     */
    val ch: String,

    /**
     * The format options.
     */
    val options: FormattingOptions,
    override val textDocument: TextDocumentIdentifier,
    override val position: Position
): TextDocumentPositionParams, RequestMessageParams()

@Serializable
data class RenameParams (
    /**
     * The new name of the symbol. If the given name is not valid the
     * request must return a [ResponseError](#ResponseError) with an
     * appropriate message set.
     */
    val newName: String,
    override val textDocument: TextDocumentIdentifier,
    override val position: Position,
    override val workDoneToken: ProgressToken?
): TextDocumentPositionParams, WorkDoneProgressParams, RequestMessageParams()

@Serializable
data class PrepareRenameParams (
    override val textDocument: TextDocumentIdentifier,
    override val position: Position
): TextDocumentPositionParams, RequestMessageParams()

@Serializable
data class FoldingRangeParams (
    /**
     * The text document.
     */
    val textDocument: TextDocumentIdentifier,
    override val workDoneToken: ProgressToken?,
    override val partialResultToken: ProgressToken?
): WorkDoneProgressParams, PartialResultParams, RequestMessageParams()

@Serializable
data class SelectionRangeParams (
    /**
     * The text document.
     */
    val textDocument: TextDocumentIdentifier,

    /**
     * The positions inside the text document.
     */
    val positions: List<Position>,
    override val workDoneToken: ProgressToken?,
    override val partialResultToken: ProgressToken?
): WorkDoneProgressParams, PartialResultParams, RequestMessageParams()
