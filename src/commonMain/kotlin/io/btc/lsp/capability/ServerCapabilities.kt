package io.btc.lsp.capability

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


@Serializable
data class ServerInfo (
	/**
	 * The name of the server as defined by the server.
	 */
	val name: String,

	/**
	 * The server's version as defined by the server.
	 */
	val version: String?
)


class TextDocumentKindSerializer: KSerializer<TextDocumentSyncKind> {
	override val descriptor: SerialDescriptor
		get() = PrimitiveSerialDescriptor("TextDocumentKindSerializer", PrimitiveKind.INT)

	override fun deserialize(decoder: Decoder): TextDocumentSyncKind {
		val ordinalValue = decoder.decodeInt()
		return TextDocumentSyncKind.values()[ordinalValue]
	}

	override fun serialize(encoder: Encoder, value: TextDocumentSyncKind) {
		encoder.encodeInt(value.ordinal)
	}

}

@Serializable
data class TextDocumentSyncOptions (
	/**
	 * Open and close notifications are sent to the server. If omitted open close notification should not
	 * be sent.
	 */
	val openClose: Boolean? = null,

	/**
	 * Change notifications are sent to the server. See TextDocumentSyncKind.None, TextDocumentSyncKind.Full
	 * and TextDocumentSyncKind.Incremental. If omitted it defaults to TextDocumentSyncKind.None.
	 */
	val change: TextDocumentSyncKind? = null
)

/**
 * Defines how the host (editor) should sync document changes to the language server.
 */
@Serializable(with = TextDocumentKindSerializer::class)
enum class TextDocumentSyncKind {
	/**
	 * Documents should not be synced at all.
	 */
	None,

	/**
	 * Documents are synced by always sending the full content
	 * of the document.
	 */
	Full,

	/**
	 * Documents are synced by sending the full content on open.
	 * After that only incremental updates to the document are
	 * send.
	 */
	Incremental
}

@Serializable
data class CompletionOptions (
	/**
	 * Most tools trigger completion request automatically without explicitly requesting
	 * it using a keyboard shortcut (e.g. Ctrl+Space). Typically they do so when the user
	 * starts to type an identifier. For example if the user types `c` in a JavaScript file
	 * code complete will automatically pop up present `console` besides others as a
	 * completion item. Characters that make up identifiers don't need to be listed here.
	 *
	 * If code complete should automatically be trigger on characters not being valid inside
	 * an identifier (for example `.` in JavaScript) list them in `triggerCharacters`.
	 */
	val triggerCharacters: List<String> = emptyList(),

	/**
	 * The list of all possible characters that commit a completion. This field can be used
	 * if clients don't support individual commit characters per completion item. See
	 * `ClientCapabilities.textDocument.completion.completionItem.commitCharactersSupport`.
	 *
	 * If a server provides both `allCommitCharacters` and commit characters on an individual
	 * completion item the ones on the completion item win.
	 *
	 * @since 3.2.0
	 */
	val allCommitCharacters: List<String> = emptyList(),

	/**
	 * The server provides support to resolve additional
	 * information for a completion item.
	 */
	val resolveProvider: Boolean,

	val workDoneProgress: Boolean? = false
)

@Serializable
data class SignatureHelpOptions (
	/**
	 * The characters that trigger signature help
	 * automatically.
	 */
	val triggerCharacters: List<String> = emptyList(),

	/**
	 * List of characters that re-trigger signature help.
	 *
	 * These trigger characters are only active when signature help is already showing. All trigger characters
	 * are also counted as re-trigger characters.
	 *
	 * @since 3.15.0
	 */
	val retriggerCharacters: List<String> = emptyList()
)

@Serializable
data class CodeLensOptions (
	/**
	 * Code lens has a resolve provider as well.
	 */
	val resolveProvider: Boolean? = false,

	val workDoneProgress: Boolean? = false
)

@Serializable
data class WorkspaceFoldersServerCapabilities (
	/**
	 * The server has support for workspace folders
	 */
	val supported: Boolean? = false,

	/**
	 * Whether the server wants to receive workspace folder
	 * change notifications.
	 *
	 * If a string is provided, the string is treated as an ID
	 * under which the notification is registered on the client
	 * side. The ID can be used to unregister for these events
	 * using the `client/unregisterCapability` request.
	 */
	val changeNotifications: String? = null
)

@Serializable
data class WorkspaceSpecificServerCapabilities (
	/**
	 * The server supports workspace folder.
	 *
	 * @since 3.6.0
	 */
	val workspaceFolders: WorkspaceFoldersServerCapabilities
)

@Serializable
data class ExecuteCommandOptions (
	/**
	 * The commands to be executed on the server
	 */
	val commands: List<String> = emptyList(),
	val workDoneProgress: Boolean? = false
)

@Serializable
data class DocumentLinkOptions (
	/**
	 * Document links have a resolve provider as well.
	 */
	val resolveProvider: Boolean? = false,
	val workDoneProgress: Boolean? = false
)

@Serializable
data class ServerCapabilities (
	/**
	 * Defines how text documents are synced. Is either a detailed structure defining each notification or
	 * for backwards compatibility the TextDocumentSyncKind number. If omitted it defaults to `TextDocumentSyncKind.None`.
	 */
	val textDocumentSync: TextDocumentSyncKind,

	/**
	 * The server provides completion support.
	 */
	val completionProvider: CompletionOptions? = null,

	/**
	 * The server provides hover support.
	 * boolean | HoverOptions
	 */
	val hoverProvider: Boolean? = false,

	/**
	 * The server provides signature help support.
	 */
	val signatureHelpProvider: SignatureHelpOptions? = null,

	/**
	 * The server provides go to declaration support.
	 * boolean | DeclarationOptions | DeclarationRegistrationOptions
	 * @since 3.14.0
	 */
	val declarationProvider: Boolean? = false,

	/**
	 * The server provides goto definition support.
	 * : boolean | DefinitionOptions
	 */
	val definitionProvider: Boolean? = false,

	/**
	 * The server provides goto type definition support.
	 * boolean | TypeDefinitionOptions | TypeDefinitionRegistrationOptions
	 * @since 3.6.0
	 */
	val typeDefinitionProvider: Boolean? = false ,

	/**
	 * The server provides goto implementation support.
	 * boolean | ImplementationOptions | ImplementationRegistrationOptions
	 * @since 3.6.0
	 */
	val implementationProvider: Boolean? = false,

	/**
	 * The server provides find references support.
	 *  boolean | ReferenceOptions
	 */
	val referencesProvider: Boolean? = false,

	/**
	 * The server provides document highlight support.
	 * ?: boolean | DocumentHighlightOptions
	 */
	val documentHighlightProvider: Boolean? = false,

	/**
	 * The server provides document symbol support.
	 * ?: boolean | DocumentSymbolOptions
	 */
	val documentSymbolProvider: Boolean? = false,

	/**
	 * The server provides code actions. The `CodeActionOptions` return type is only
	 * valid if the client signals code action literal support via the property
	 * `textDocument.codeAction.codeActionLiteralSupport`.
	 * ?: boolean | CodeActionOptions
	 */
	val codeActionProvider: Boolean? = false,

	/**
	 * The server provides code lens.
	 */
	val codeLensProvider: CodeLensOptions? = null,

	/**
	 * The server provides document link support.
	 */
	val documentLinkProvider: DocumentLinkOptions? = null,

	/**
	 * The server provides color provider support.
	 *?: boolean | DocumentColorOptions | DocumentColorRegistrationOptions,
	 * @since 3.6.0
	 */
	val colorProvider: Boolean? = false,

	/**
	 * The server provides document formatting.
	 * ?: boolean | DocumentFormattingOptions
	 */
	val documentFormattingProvider: Boolean? = false,

	/**
	 * The server provides document range formatting.
	 * ?: boolean | DocumentRangeFormattingOptions
	 */
	val documentRangeFormattingProvider: Boolean? = false,

	/**
	 * The server provides document formatting on typing.
	 * ?: DocumentOnTypeFormattingOptions
	 */
	val documentOnTypeFormattingProvider: Boolean? = false,

	/**
	 * The server provides rename support. RenameOptions may only be
	 * specified if the client states that it supports
	 * `prepareSupport` in its initial `initialize` request.
	 * ?: boolean | RenameOptions
	 */
	val renameProvider: Boolean? = false,

	/**
	 * The server provides folding provider support.
	 * ?: boolean | FoldingRangeOptions | FoldingRangeRegistrationOptions
	 * @since 3.10.0
	 */
	val foldingRangeProvider: Boolean? = false,

	/**
	 * The server provides execute command support.
	 */
	val executeCommandProvider: ExecuteCommandOptions? = null,

	/**
	 * The server provides selection range support.
	 * ?: boolean | SelectionRangeOptions | SelectionRangeRegistrationOptions
	 * @since 3.15.0
	 */
	val selectionRangeProvider: Boolean? = false,

	/**
	 * The server provides workspace symbol support.
	 */
	val workspaceSymbolProvider: Boolean? = false,

	/**
	 * Workspace specific server capabilities
	 */
	val workspace: WorkspaceSpecificServerCapabilities,

	/**
	 * Experimental server capabilities.
	 */
	val experimental: String? = null
)