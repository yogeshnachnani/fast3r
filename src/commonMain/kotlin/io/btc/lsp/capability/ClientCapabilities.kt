package io.btc.lsp.capability

import io.btc.lsp.protocol.allDiagnosticTags
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class TextDocumentClientCapabilities (

	val synchronization: TextDocumentSyncClientCapabilities?,

	/**
	 * Capabilities specific to the `textDocument/completion` request.
	 */
	val completion: CompletionClientCapabilities?,

	/**
	 * Capabilities specific to the `textDocument/hover` request.
	 */
	val hover: HoverClientCapabilities?,

	/**
	 * Capabilities specific to the `textDocument/signatureHelp` request.
	 */
	val signatureHelp: SignatureHelpClientCapabilities?,

	/**
	 * Capabilities specific to the `textDocument/declaration` request.
	 *
	 * @since 3.14.0
	 */
	val declaration: DeclarationClientCapabilities?,

	/**
	 * Capabilities specific to the `textDocument/definition` request.
	 */
	val definition: DefinitionClientCapabilities?,

	/**
	 * Capabilities specific to the `textDocument/typeDefinition` request.
	 *
	 * @since 3.6.0
	 */
	val typeDefinition: TypeDefinitionClientCapabilities?,

	/**
	 * Capabilities specific to the `textDocument/implementation` request.
	 *
	 * @since 3.6.0
	 */
	val implementation: ImplementationClientCapabilities?,

	/**
	 * Capabilities specific to the `textDocument/references` request.
	 */
	val references: ReferenceClientCapabilities?,

	/**
	 * Capabilities specific to the `textDocument/documentHighlight` request.
	 */
	val documentHighlight: DocumentHighlightClientCapabilities?,

	/**
	 * Capabilities specific to the `textDocument/documentSymbol` request.
	 */
	val documentSymbol: DocumentSymbolClientCapabilities?,

	/**
	 * Capabilities specific to the `textDocument/codeAction` request.
	 */
	val codeAction: CodeActionClientCapabilities?,

	/**
	 * Capabilities specific to the `textDocument/codeLens` request.
	 */
	val codeLens: CodeLensClientCapabilities?,

	/**
	 * Capabilities specific to the `textDocument/documentLink` request.
	 */
	val documentLink: DocumentLinkClientCapabilities?,

	/**
	 * Capabilities specific to the `textDocument/documentColor` and the
	 * `textDocument/colorPresentation` request.
	 *
	 * @since 3.6.0
	 */
	val colorProvider: DocumentColorClientCapabilities?,

	/**
	 * Capabilities specific to the `textDocument/formatting` request.
	 */
	val formatting:  DocumentFormattingClientCapabilities?,

	/**
	 * Capabilities specific to the `textDocument/rangeFormatting` request.
	 */
	val rangeFormatting: DocumentRangeFormattingClientCapabilities?,

	/** request.
	 * Capabilities specific to the `textDocument/onTypeFormatting` request.
	 */
	val onTypeFormatting: DocumentOnTypeFormattingClientCapabilities?,

	/**
	 * Capabilities specific to the `textDocument/rename` request.
	 */
	val rename: RenameClientCapabilities?,

	/**
	 * Capabilities specific to the `textDocument/publishDiagnostics` notification.
	 */
	val publishDiagnostics: PublishDiagnosticsClientCapabilities?,

	/**
	 * Capabilities specific to the `textDocument/foldingRange` request.
	 *
	 * @since 3.10.0
	 */
	val foldingRange: FoldingRangeClientCapabilities?,

	/**
	 * Capabilities specific to the `textDocument/selectionRange` request.
	 *
	 * @since 3.15.0
	 */
	val selectionRange: SelectionRangeClientCapabilities?
)

@Serializable
data class WorkspaceCapabilities (
	/**
	 * The client supports applying batch edits
	 * to the workspace by supporting the request
	 * 'workspace/applyEdit'
	 */
	val applyEdit: Boolean?,

	/**
	 * Capabilities specific to `WorkspaceEdit`s
	 */
	val workspaceEdit: WorkspaceEditClientCapabilities?,

	/**
	 * Capabilities specific to the `workspace/didChangeConfiguration` notification.
	 */
	val didChangeConfiguration: DidChangeConfigurationClientCapabilities?,

	/**
	 * Capabilities specific to the `workspace/didChangeWatchedFiles` notification.
	 */
	val didChangeWatchedFiles: DidChangeWatchedFilesClientCapabilities?,

	/**
	 * Capabilities specific to the `workspace/symbol` request.
	 */
	val symbol: WorkspaceSymbolClientCapabilities?,

	/**
	 * Capabilities specific to the `workspace/executeCommand` request.
	 */
	val executeCommand: ExecuteCommandClientCapabilities?,

	/**
	 * The client has support for workspace folders.
	 *
	 * Since 3.6.0
	 */
	val workspaceFolders: Boolean?,

	/**
	 * The client supports `workspace/configuration` requests.
	 *
	 * Since 3.6.0
	 */
	val configuration: Boolean?
)

@Serializable
data class DidChangeWatchedFilesClientCapabilities (
	/**
	 * Did change watched files notification supports dynamic registration. Please note
	 * that the current protocol doesn't support static configuration for file changes
	 * from the server side.
	 */
	val dynamicRegistration: Boolean?
)
@Serializable
data class ClientCapabilities (
	/**
     * Workspace specific client capabilities.
     */
    val workspace: WorkspaceCapabilities?,

	/**
     * Text document specific client capabilities.
     */
    val textDocument: TextDocumentClientCapabilities?,

	/**
     * Window specific client capabilities.
     *
	 * Whether client supports handling progress notifications. If set servers are allowed to
	 * report in `workDoneProgress` property in the request specific server capabilities.
	 *
	 * Since 3.15.0
     */
    val window: WorkDoneProgressOptions?,

	/**
     * Experimental client capabilities.
     */
    val experimental: String?
)

@Serializable
data class WorkspaceEditClientCapabilities (
	/**
	 * The client supports versioned document changes in `WorkspaceEdit`s
	 */
	val documentChanges: Boolean?,

	/**
	 * The resource operations the client supports. Clients should at least
	 * support 'create', 'rename' and 'delete' files and folders.
	 *
	 * @since 3.13.0
	 */
	val resourceOperations: List<ResourceOperationKind>?,

	/**
	 * The failure handling strategy of a client if applying the workspace edit
	 * fails.
	 *
	 * @since 3.13.0
	 */
	val failureHandling: FailureHandlingKind?
)

enum class ResourceOperationKind {
	create,
	rename,
	delete
}

enum class FailureHandlingKind {
	/**
	 * Applying the workspace change is simply aborted if one of the changes provided
	 * fails. All operations executed before the failing operation stay executed.
	 */
	abort,
	/**
	 * All operations are executed transactional. That means they either all
	 * succeed or no changes at all are applied to the workspace.
	 */
	transactional,
	/**
	 * If the workspace edit contains only textual file changes they are executed transactional.
	 * If resource changes (create, rename or delete file) are part of the change the failure
	 * handling strategy is abort.
	 */
	textOnlyTransactional,
	/**
	 * The client tries to undo the operations already executed. But there is no
	 * guarantee that this is succeeding.
	 */
	undo
}

@Serializable
data class DidChangeConfigurationClientCapabilities (
	/**
	 * Did change configuration notification supports dynamic registration.
	 */
	val dynamicRegistration: Boolean?
)

@Serializable
data class SymbolKindValueSet(
	/**
	 * The symbol kind values the client supports. When this
	 * property exists the client also guarantees that it will
	 * handle values outside its set gracefully and falls back
	 * to a default value when unknown.
	 *
	 * If this property is not present the client only supports
	 * the symbol kinds from `File` to `Array` as defined in
	 * the initial version of the protocol.
	 */
	val valueSet: List<Int>?
)

enum class SymbolTag {
	UnusedEnumAddedForParity,
	Deprecated
}

val allSymbolTags = SymbolTag.values().toList().minus(SymbolTag.UnusedEnumAddedForParity).map { it.ordinal }

@Serializable
data class SymbolTagValueSet(
	val valueSet: List<Int>?
)

@Serializable
data class WorkspaceSymbolClientCapabilities (
	/**
	 * Symbol request supports dynamic registration.
	 */
	val dynamicRegistration: Boolean?,

	/**
	 * Specific capabilities for the `SymbolKind` in the `workspace/symbol` request.
	 */
	val symbolKind: SymbolKindValueSet?,

	/**
	 * The client supports tags on `SymbolInformation`.
	 * Clients supporting tags have to handle unknown tags gracefully.
	 *
	 * @since 3.16.0 - Proposed state
	 */
	val tagSupport: SymbolTagValueSet
)

class SymbolKindSerializer: KSerializer<SymbolKind> {
	override val descriptor: SerialDescriptor
		get() = PrimitiveSerialDescriptor("SymbolKindSerializer", PrimitiveKind.INT)

	override fun deserialize(decoder: Decoder): SymbolKind {
		val ordinalValue = decoder.decodeInt()
		return SymbolKind.values()[ordinalValue]
	}

	override fun serialize(encoder: Encoder, value: SymbolKind) {
		encoder.encodeInt(value.ordinal)
	}

}

@Serializable(with = SymbolKindSerializer::class)
enum class SymbolKind {
    UnusedEnumAddedForParity,
	File,
	Module,
	Namespace,
	Package,
	Class,
	Method,
	Property,
	Field,
	Constructor,
	Enum,
	Interface,
	Function,
	Variable,
	Constant,
	String,
	Number,
	Boolean,
	Array,
	Object,
	Key,
	Null,
	EnumMember,
	Struct,
	Event,
	Operator,
	TypeParameter,
}

val allSymbolKind = SymbolKind.values().toList().minus(SymbolKind.UnusedEnumAddedForParity).map { it.ordinal }


@Serializable
data class ExecuteCommandClientCapabilities (
	/**
	 * Execute command supports dynamic registration.
	 */
	val dynamicRegistration: Boolean?
)
@Serializable
data class TextDocumentSyncClientCapabilities (
	/**
	 * Whether text document synchronization supports dynamic registration.
	 */
	val dynamicRegistration: Boolean?,

	/**
	 * The client supports sending will save notifications.
	 */
	val willSave: Boolean?,

	/**
	 * The client supports sending a will save request and
	 * waits for a response providing text edits which will
	 * be applied to the document before it is saved.
	 */
	val willSaveWaitUntil: Boolean?,

	/**
	 * The client supports did save notifications.
	 */
	val didSave: Boolean?
)
enum class CompletionItemTag {
	UnusedEnumAddedForParity,
	Deprecated
}

enum class MarkupKind {
	plaintext,
	markdown
}

/**
 * Client supports the tag property on a completion item. Clients supporting
 * tags have to handle unknown tags gracefully. Clients especially need to
 * preserve unknown tags when sending a completion item back to the server in
 * a resolve call.
 *
 * @since 3.15.0
 */
@Serializable
data class TagSupport(
	val valueSet: List<CompletionItemTag>
)

@Serializable
data class CompletionItem(
	/**
	 * Client supports snippets as insert text.
	 *
	 * A snippet can define tab stops and placeholders with `$1`, `$2`
	 * and `${3:foo}`. `$0` defines the final tab stop, it defaults to
	 * the end of the snippet. Placeholders with equal identifiers are linked,
	 * that is typing in one will update others too.
	 */
	val snippetSupport: Boolean?,

	/**
	 * Client supports commit characters on a completion item.
	 */
	val commitCharactersSupport: Boolean?,

	/**
	 * Client supports the follow content formats for the documentation
	 * property. The order describes the preferred format of the client.
	 */
	val documentationFormat: List<MarkupKind>?,

	/**
	 * Client supports the deprecated property on a completion item.
	 */
	val deprecatedSupport: Boolean?,

	/**
	 * Client supports the preselect property on a completion item.
	 */
	val preselectSupport: Boolean?,

	val tagSupport: TagSupport?,

	/**
	 * Client support insert replace edit to control different behavior if a
	 * completion item is inserted in the text or should replace text.
	 *
	 * @since 3.16.0 - Proposed state
	 */
	val insertReplaceSupport: Boolean?,

	/**
	 * Client supports to resolve `additionalTextEdits` in the `completionItem/resolve`
	 * request. So servers can postpone computing them.
	 *
	 * @since 3.16.0 - Proposed state
	 */
	val resolveAdditionalTextEditsSupport: Boolean?
)

/**
 * The kind of a completion entry.
 */
enum class CompletionItemKind {
	UnusedEnumAddedForParity,
	Text,
	Method,
	Function,
	Constructor,
	Field,
	Variable,
	Class,
	Interface,
	Module,
	Property,
	Unit,
	Value,
	Enum,
	Keyword,
	Snippet,
	Color,
	File,
	Reference,
	Folder,
	EnumMember,
	Constant,
	Struct,
	Event,
	Operator,
	TypeParameter,
}

@Serializable
data class CompletionItemKindValueSet(
	/**
	 * The completion item kind values the client supports. When this
	 * property exists the client also guarantees that it will
	 * handle values outside its set gracefully and falls back
	 * to a default value when unknown.
	 *
	 * If this property is not present the client only supports
	 * the completion items kinds from `Text` to `Reference` as defined in
	 * the initial version of the protocol.
	 */
	val valueSet: List<CompletionItemKind>?
)

@Serializable
data class CompletionClientCapabilities (
	/**
	 * Whether completion supports dynamic registration.
	 */
	val dynamicRegistration: Boolean?,

	/**
	 * The client supports the following `CompletionItem` specific
	 * capabilities.
	 */
	val completionItem: CompletionItem?,

	val completionItemKind: CompletionItemKindValueSet?,

	/**
	 * The client supports to send additional context information for a
	 * `textDocument/completion` requestion.
	 */
	val contextSupport: Boolean?
)

@Serializable
data class HoverClientCapabilities (
	/**
	 * Whether hover supports dynamic registration.
	 */
	val dynamicRegistration: Boolean?,

	/**
	 * Client supports the follow content formats for the content
	 * property. The order describes the preferred format of the client.
	 */
	val contentFormat: List<MarkupKind>?
)

@Serializable
data class ParameterInformation(
	/**
	 * The client supports processing label offsets instead of a
	 * simple label string.
	 *
	 * @since 3.14.0
	 */
	val labelOffsetSupport: Boolean?
)

@Serializable
data class SignatureInformation(
	/**
	 * Client supports the follow content formats for the documentation
	 * property. The order describes the preferred format of the client.
	 */
	val documentationFormat: List<MarkupKind>?,

	/**
	 * Client capabilities specific to parameter information.
	 */
	val parameterInformation: ParameterInformation?,

	/**
	 * The client support the `activeParameter` property on `SignatureInformation`
	 * literal.
	 *
	 * @since 3.16.0 - proposed state
	 */
	val activeParameterSupport: Boolean?
)

@Serializable
data class SignatureHelpClientCapabilities (
	/**
	 * Whether signature help supports dynamic registration.
	 */
	val dynamicRegistration: Boolean?,

	/**
	 * The client supports the following `SignatureInformation`
	 * specific properties.
	 */
	val signatureInformation: SignatureInformation?,

	/**
	 * The client supports to send additional context information for a
	 * `textDocument/signatureHelp` request. A client that opts into
	 * contextSupport will also support the `retriggerCharacters` on
	 * `SignatureHelpOptions`.
	 *
	 * @since 3.15.0
	 */
	val contextSupport: Boolean?
)

@Serializable
data class DeclarationClientCapabilities (
	/**
	 * Whether declaration supports dynamic registration. If this is set to `true`
	 * the client supports the new `DeclarationRegistrationOptions` return value
	 * for the corresponding server capability as well.
	 */
	val dynamicRegistration: Boolean?,

	/**
	 * The client supports additional metadata in the form of declaration links.
	 */
	val linkSupport: Boolean?
)

@Serializable
data class DefinitionClientCapabilities (
	/**
	 * Whether definition supports dynamic registration.
	 */
	val dynamicRegistration: Boolean?,

	/**
	 * The client supports additional metadata in the form of definition links.
	 *
	 * @since 3.14.0
	 */
	val linkSupport: Boolean?
)

@Serializable
data class TypeDefinitionClientCapabilities (
	/**
	 * Whether implementation supports dynamic registration. If this is set to `true`
	 * the client supports the new `TypeDefinitionRegistrationOptions` return value
	 * for the corresponding server capability as well.
	 */
	val dynamicRegistration: Boolean?,

	/**
	 * The client supports additional metadata in the form of definition links.
	 *
	 * Since 3.14.0
	 */
	val linkSupport: Boolean?
)

@Serializable
data class ImplementationClientCapabilities (
	/**
	 * Whether implementation supports dynamic registration. If this is set to `true`
	 * the client supports the new `ImplementationRegistrationOptions` return value
	 * for the corresponding server capability as well.
	 */
	val dynamicRegistration: Boolean?,

	/**
	 * The client supports additional metadata in the form of definition links.
	 *
	 * @since 3.14.0
	 */
	val linkSupport: Boolean?
)

@Serializable
data class ReferenceClientCapabilities (
	/**
	 * Whether references supports dynamic registration.
	 */
	val dynamicRegistration: Boolean?
)

@Serializable
data class DocumentHighlightClientCapabilities (
	/**
	 * Whether document highlight supports dynamic registration.
	 */
	val dynamicRegistration: Boolean?
)

@Serializable
data class DocumentSymbolClientCapabilities (
	/**
	 * Whether document symbol supports dynamic registration.
	 */
	val dynamicRegistration: Boolean?,

	/**
	 * Specific capabilities for the `SymbolKind`.
	 */
	val symbolKind: SymbolKindValueSet?,

	/**
	 * The client support hierarchical document symbols.
	 */
	val hierarchicalDocumentSymbolSupport: Boolean?,

	/**
	 * The client supports tags on `SymbolInformation`. Tags are supported on
	 * `DocumentSymbol` if `hierarchicalDocumentSymbolSupport` is set to true.
	 * Clients supporting tags have to handle unknown tags gracefully.
	 *
	 * @since 3.16.0 - Proposed state
	 */
	val  tagSupport: SymbolTagValueSet?
)

class CodeActionKindSerializer: KSerializer<CodeActionKind> {
	override val descriptor: SerialDescriptor
		get() = PrimitiveSerialDescriptor("CodeActionKind", PrimitiveKind.STRING)

	override fun deserialize(decoder: Decoder): CodeActionKind {
		val strValue = decoder.decodeString()
		return when (strValue) {
			"" -> CodeActionKind.Empty
			"quickfix" -> CodeActionKind.QuickFix
			"refactor" -> CodeActionKind.Refactor
			"refactor.extract" -> CodeActionKind.RefactorExtract
			"refactor.inline" -> CodeActionKind.RefactorInline
			"refactor.rewrite" -> CodeActionKind.RefactorRewrite
			"source" -> CodeActionKind.Source
			"source.organizeImports" -> CodeActionKind.SourceOrganizeImports
			else -> throw RuntimeException("Unknown CodeActionKind specified by $strValue")
		}
	}

	override fun serialize(encoder: Encoder, value: CodeActionKind) {
		encoder.encodeInt(value.ordinal)
	}

}

/**
 * The kind of a code action.
 *
 * Kinds are a hierarchical list of identifiers separated by `.`, e.g. `"refactor.extract.function"`.
 *
 * The set of kinds is open and client needs to announce the kinds it supports to the server during
 * initialization.
 * A set of predefined code action kinds.
 */
@Serializable(with = CodeActionKindSerializer::class)
enum class CodeActionKind {

	/**
	 * Empty kind.
	 */
	Empty,

	/**
	 * Base kind for quickfix actions: 'quickfix'.
	 */
	QuickFix,

	/**
	 * Base kind for refactoring actions: 'refactor'.
	 */
	Refactor,

	/**
	 * Base kind for refactoring extraction actions: 'refactor.extract'.
	 *
	 * Example extract actions:
	 *
	 * - Extract method
	 * - Extract function
	 * - Extract variable
	 * - Extract interface from class
	 * - ...
	 */
	RefactorExtract,

	/**
	 * Base kind for refactoring inline actions: 'refactor.inline'.
	 *
	 * Example inline actions:
	 *
	 * - Inline function
	 * - Inline variable
	 * - Inline constant
	 * - ...
	 */
	RefactorInline,

	/**
	 * Base kind for refactoring rewrite actions: 'refactor.rewrite'.
	 *
	 * Example rewrite actions:
	 *
	 * - Convert JavaScript function to class
	 * - Add or remove parameter
	 * - Encapsulate field
	 * - Make method static
	 * - Move method to base class
	 * - ...
	 */
	RefactorRewrite,

	/**
	 * Base kind for source actions: `source`.
	 *
	 * Source code actions apply to the entire file.
	 */
	Source,

	/**
	 * Base kind for an organize imports source action: `source.organizeImports`.
	 */
	SourceOrganizeImports
}

@Serializable
data class CodeActionKindValueSet(
	/**
	 * The code action kind values the client supports. When this
	 * property exists the client also guarantees that it will
	 * handle values outside its set gracefully and falls back
	 * to a default value when unknown.
	 */
	val valueSet: List<CodeActionKind> = emptyList()
)

@Serializable
data class CodeActionLiteralSupport(
	/**
	 * The code action kind is support with the following value
	 * set.
	 */
	val codeActionKind: CodeActionKindValueSet?
)

@Serializable
data class CodeActionClientCapabilities (
	/**
	 * Whether code action supports dynamic registration.
	 */
	val dynamicRegistration: Boolean? = false,

	/**
	 * The client support code action literals of type `CodeAction` as a valid
	 * response of the `textDocument/codeAction` request. If the property is not
	 * set the request can only return `Command` literals.
	 *
	 * @since 3.8.0
	 */
	val codeActionLiteralSupport: CodeActionLiteralSupport? = null,

	/**
	 * Whether code action supports the `isPreferred` property.
	 * @since 3.15.0
	 */
	val isPreferredSupport: Boolean? = false
)

@Serializable
data class CodeLensClientCapabilities (
	/**
	 * Whether code lens supports dynamic registration.
	 */
	val dynamicRegistration: Boolean? = false
)

@Serializable
data class DocumentLinkClientCapabilities (
	/**
	 * Whether document link supports dynamic registration.
	 */
	val dynamicRegistration: Boolean? = false,

	/**
	 * Whether the client support the `tooltip` property on `DocumentLink`.
	 *
	 * @since 3.15.0
	 */
	val tooltipSupport: Boolean?
)

@Serializable
data class DocumentColorClientCapabilities (
	/**
	 * Whether implementation supports dynamic registration. If this is set to `true`
	 * the client supports the new `DocumentColorRegistrationOptions` return value
	 * for the corresponding server capability as well.
	 */
	val dynamicRegistration: Boolean?
)

@Serializable
data class DocumentFormattingClientCapabilities (
	/**
	 * Whether formatting supports dynamic registration.
	 */
	val dynamicRegistration: Boolean?
)

@Serializable
data class DocumentRangeFormattingClientCapabilities (
	/**
	 * Whether range formatting supports dynamic registration.
	 */
	val dynamicRegistration: Boolean?
)

@Serializable
data class DocumentOnTypeFormattingClientCapabilities (
	/**
	 * Whether on type formatting supports dynamic registration.
	 */
	val dynamicRegistration: Boolean? = false
)

@Serializable
data class RenameClientCapabilities (
	/**
	 * Whether rename supports dynamic registration.
	 */
	val dynamicRegistration: Boolean? = false,

	/**
	 * Client supports testing for validity of rename operations
	 * before execution.
	 *
	 * @since version 3.12.0
	 */
	val prepareSupport: Boolean?
)

@Serializable
data class DiagnosticTagSupportValueSet(
	/** See [DiagnosticTag]*/
	val valueSet: List<Int> = allDiagnosticTags
)

@Serializable
data class PublishDiagnosticsClientCapabilities (
	/**
	 * Whether the clients accepts diagnostics with related information.
	 */
	val relatedInformation: Boolean? = false,

	/**
	 * Client supports the tag property to provide meta data about a diagnostic.
	 * Clients supporting tags have to handle unknown tags gracefully.
	 *
	 * @since 3.15.0
	 */
	val tagSupport: DiagnosticTagSupportValueSet,

	/**
	 * Whether the client interprets the version property of the
	 * `textDocument/publishDiagnostics` notification`s parameter.
	 *
	 * @since 3.15.0
	 */
	val versionSupport: Boolean? = false,

	/**
	 * Clients support complex diagnostic codes (e.g. code and target URI).
	 *
	 * @since 3.16.0 - Proposed state
	 */
	val complexDiagnosticCodeSupport: Boolean? = false
)

@Serializable
data class FoldingRangeClientCapabilities (
	/**
	 * Whether implementation supports dynamic registration for folding range providers. If this is set to `true`
	 * the client supports the new `FoldingRangeRegistrationOptions` return value for the corresponding server
	 * capability as well.
	 */
	val dynamicRegistration: Boolean? = false,
	/**
	 * The maximum number of folding ranges that the client prefers to receive per document. The value serves as a
	 * hint, servers are free to follow the limit.
	 */
	val rangeLimit: Long? = null,
	/**
	 * If set, the client signals that it only supports folding complete lines. If set, client will
	 * ignore specified `startCharacter` and `endCharacter` properties in a FoldingRange.
	 */
	val lineFoldingOnly: Boolean? = false
)

@Serializable
data class SelectionRangeClientCapabilities (
	/**
	 * Whether implementation supports dynamic registration for selection range providers. If this is set to `true`
	 * the client supports the new `SelectionRangeRegistrationOptions` return value for the corresponding server
	 * capability as well.
	 */
	val dynamicRegistration: Boolean? = false
)
@Serializable
data class WorkDoneProgressOptions(
	val workDoneProgress: Boolean? = false
)

