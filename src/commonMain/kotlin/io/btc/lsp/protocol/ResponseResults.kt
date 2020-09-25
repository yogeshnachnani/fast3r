package io.btc.lsp.protocol

import kotlinx.serialization.Serializable
import io.btc.lsp.capability.ServerCapabilities
import io.btc.lsp.capability.ServerInfo
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import io.btc.lsp.capability.CodeActionKind
import io.btc.lsp.capability.SymbolKind

/**
 * Data models for [ResponseMessage.result]
 * */

@Serializable
data class InitializeResult (
    /**
     * The capabilities the language server provides.
     */
    val capabilities: ServerCapabilities,

    /**
     * Information about the server.
     *
     * @since 3.15.0
     */
    val serverInfo: ServerInfo? = null
)

@Serializable
data class ApplyWorkspaceEditResponse (
    /**
     * Indicates whether the edit was applied or not.
     */
    val applied: Boolean,

    /**
     * An optional textual description for why the edit was not applied.
     * This may be used may be used by the server for diagnostic
     * logging or to provide a suitable error for a request that
     * triggered the edit.
     */
    val failureReason: String? = null
)
/**
 * The result of a hover request.
 */
@Serializable
data class HoverResult (
    /**
     * The hover's content
     */
    val contents: MarkupContent,

    /**
     * An optional range is a range inside a text document
     * that is used to visualize a hover, e.g. by changing the background color.
     */
    val range: Range? = null
)

/**
 * Signature help represents the signature of something
 * callable. There can be multiple signature but only one
 * active and only one active parameter.
 */
@Serializable
data class SignatureHelp (
    /**
     * One or more signatures. If no signaures are availabe the signature help
     * request should return `null`.
     */
    val signatures: List<SignatureInformationResult> = emptyList(),

    /**
     * The active signature. If omitted or the value lies outside the
     * range of `signatures` the value defaults to zero or is ignore if
     * the `SignatureHelp` as no signatures.
     *
     * Whenever possible implementors should make an active decision about
     * the active signature and shouldn't rely on a default value.
     *
     * In future version of the protocol this property might become
     * mandatory to better express this.
     */
    val activeSignature: Int,

    /**
     * The active parameter of the active signature. If omitted or the value
     * lies outside the range of `signatures[activeSignature].parameters`
     * defaults to 0 if the active signature has parameters. If
     * the active signature has no parameters it is ignored.
     * In future version of the protocol this property might become
     * mandatory to better express the active parameter if the
     * active signature does have any.
     */
    val activeParameter: Int
)

/**
 * Represents the signature of something callable. A signature
 * can have a label, like a function-name, a doc-comment, and
 * a set of parameters.
 */
@Serializable
data class SignatureInformationResult (
    /**
     * The label of this signature. Will be shown in
     * the UI.
     */
    val label: String,

    /**
     * The human-readable doc-comment of this signature. Will be shown
     * in the UI but can be omitted.
     */
    val documentation: MarkupContent? = null,

    /**
     * The parameters of this signature.
     */
    val parameters: List<ParameterInformationResult>
)

/**
 * Represents a parameter of a callable-signature. A parameter can
 * have a label and a doc-comment.
 */
@Serializable
data class ParameterInformationResult (

    /**
     * The label of this parameter information.
     *
     * Either a string or an inclusive start and exclusive end offsets within its containing
     * signature label. (see SignatureInformation.label). The offsets are based on a UTF-16
     * string representation as `Position` and `Range` does.
     *
     * *Note*: a label of type string should be a substring of its containing signature label.
     * Its intended use case is to highlight the parameter label part in the `SignatureInformation.label`.
     */
    val label: String,

    /**
     * The human-readable doc-comment of this parameter. Will be shown
     * in the UI but can be omitted.
     */
    val documentation: MarkupContent
)

/**
 * A document highlight is a range inside a text document which deserves
 * special attention. Usually a document highlight is visualized by changing
 * the background color of its range.
 *
 */
@Serializable
data class DocumentHighlight (
    /**
     * The range this highlight applies to.
     */
    val range: Range,

    /**
     * The highlight kind, default is DocumentHighlightKind.Text.
     */
    val kind: DocumentHighlightKind? = null
)

/**
 * A document highlight kind.
 */
@Serializable(with = DocumentHighlightKindSerializer::class)
enum class DocumentHighlightKind {
    UnusedEnumAddedForParity,
    /**
     * A textual occurrence.
     */
    Text,

    /**
     * Read-access of a symbol, like reading a variable.
     */
    Read,

    /**
     * Write-access of a symbol, like writing to a variable.
     */
    Write,
}

class DocumentHighlightKindSerializer: KSerializer<DocumentHighlightKind> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("DocumentHighlightKind", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): DocumentHighlightKind {
        val ordinalValue = decoder.decodeInt()
        return DocumentHighlightKind.values()[ordinalValue]
    }

    override fun serialize(encoder: Encoder, value: DocumentHighlightKind) {
        encoder.encodeInt(value.ordinal)
    }

}

/**
 * A code lens represents a command that should be shown along with
 * source text, like the number of references, a way to run tests, etc.
 *
 * A code lens is _unresolved_ when no command is associated to it. For performance
 * reasons the creation of a code lens and resolving should be done in two stages.
 */
@Serializable
data class CodeLens (
    /**
     * The range in which this code lens is valid. Should only span a single line.
     */
    val range: Range,

    /**
     * The command this code lens represents.
     */
    val command: Command? = null,

    /**
     * A data entry field that is preserved on a code lens item between
     * a code lens and a code lens resolve request.
     */
    val data: String? = null
)

/**
 * A code action represents a change that can be performed in code, e.g. to fix a problem or
 * to refactor code.
 *
 * A CodeAction must set either `edit` and/or a `command`. If both are supplied, the `edit` is applied first, then the `command` is executed.
 */
@Serializable
data class CodeAction (

    /**
     * A short, human-readable, title for this code action.
     */
    val title: String,

    /**
     * The kind of the code action.
     *
     * Used to filter code actions.
     */
    val kind: CodeActionKind? = null,

    /**
     * The diagnostics that this code action resolves.
     */
    val diagnostics: List<Diagnostic> = emptyList(),

    /**
     * Marks this as a preferred action. Preferred actions are used by the `auto fix` command and can be targeted
     * by keybindings.
     *
     * A quick fix should be marked preferred if it properly addresses the underlying error.
     * A refactoring should be marked preferred if it is the most reasonable choice of actions to take.
     *
     * @since 3.15.0
     */
    val isPreferred: Boolean? = false,

    /**
     * The workspace edit this code action performs.
     */
    val edit: WorkspaceEdit? = null,

    /**
     * A command this code action executes. If a code action
     * provides an edit and a command, first the edit is
     * executed and then the command.
     */
    val command: Command? = null
)

@Serializable
data class Command(
    val title: String,
    val command: String,
    val arguments: List<String> = emptyList()
)
@Serializable
data class TextEdit(
    val range: Range,
    val newText: String
)
/**
 * Enum of known range kinds
 */
enum class FoldingRangeKind {
    /**
     * Folding range for a comment
     */
    comment,
    /**
     * Folding range for a imports or includes
     */
    imports,
    /**
     * Folding range for a region (e.g. `#region`)
     */
    region
}

/**
 * Represents a folding range.
 */
@Serializable
data class FoldingRange (

    /**
     * The zero-based line number from where the folded range starts.
     */
    val startLine: Int,

    /**
     * The zero-based character offset from where the folded range starts. If not defined, defaults to the length of the start line.
     */
    val startCharacter: Int? = null,

    /**
     * The zero-based line number where the folded range ends.
     */
    val endLine: Int? = null,

    /**
     * The zero-based character offset before the folded range ends. If not defined, defaults to the length of the end line.
     */
    val endCharacter: Int? = null,

    /**
     * Describes the kind of the folding range such as `comment` or `region`. The kind
     * is used to categorize folding ranges and used by commands like 'Fold all comments'. See
     * [FoldingRangeKind](#FoldingRangeKind) for an enumeration of standardized kinds.
     */
    val kind: String? = null
)

@Serializable
data class SelectionRange (
    /**
     * The [range](#Range) of this selection range.
     */
    val range: Range,
    /**
     * The parent selection range containing this range. Therefore `parent.range` must contain `this.range`.
     */
    val parent: SelectionRange
)

@Serializable
data class ColorPresentation (
    /**
     * The label of this color presentation. It will be shown on the color
     * picker header. By default this is also the text that is inserted when selecting
     * this color presentation.
     */
    val label: String,
    /**
     * An [edit](#TextEdit) which is applied to a document when selecting
     * this presentation for the color.  When `falsy` the [label](#ColorPresentation.label)
     * is used.
     */
    val textEdit: TextEdit? = null,
    /**
     * An optional array of additional [text edits](#TextEdit) that are applied when
     * selecting this color presentation. Edits must not overlap with the main [edit](#ColorPresentation.textEdit) nor with themselves.
     */
    val additionalTextEdits: List<TextEdit> = emptyList()
)

@Serializable
data class ColorInformation (
    /**
     * The range in the document where this color appears.
     */
    val range: Range,

    /**
     * The actual color value for this color range.
     */
    val color: Color
)

/**
 * Represents a color in RGBA space.
 */
@Serializable
data class Color (

    /**
     * The red component of this color in the range [0-1].
     */
    val red: Double,

    /**
     * The green component of this color in the range [0-1].
     */
    val green: Double,

    /**
     * The blue component of this color in the range [0-1].
     */
    val blue: Double,

    /**
     * The alpha component of this color in the range [0-1].
     */
    val alpha: Double
)

@Serializable
data class DocumentLink (
    /**
     * The range this link applies to.
     */
    val range: Range,

    /**
     * The uri this link points to. If missing a resolve request is sent later.
     */
    val target: DocumentUri? = null,

    /**
     * The tooltip text when you hover over this link.
     *
     * If a tooltip is provided, is will be displayed in a string that includes instructions on how to
     * trigger the link, such as `{0} (ctrl + click)`. The specific instructions vary depending on OS,
     * user settings, and localization.
     *
     * @since 3.15.0
     */
    val tooltip: String,

    /**
     * A data entry field that is preserved on a document link between a
     * DocumentLinkRequest and a DocumentLinkResolveRequest.
     */
    val data: String? = null
)

/**
 * Represents programming constructs like variables, classes, interfaces etc. that appear in a document. Document symbols can be
 * hierarchical and they have two ranges: one that encloses its definition and one that points to its most interesting range,
 * e.g. the range of an identifier.
 */
@Serializable
data class DocumentSymbol (

    /**
     * The name of this symbol. Will be displayed in the user interface and therefore must not be
     * an empty string or a string only consisting of white spaces.
     */
    val name: String,

    /**
     * More detail for this symbol, e.g the signature of a function.
     */
    val detail: String? = null,

    /**
     * The kind of this symbol.
     */
    val kind: SymbolKind,

    /**
     * Indicates if this symbol is deprecated.
     */
    val deprecated: Boolean? = null,

    /**
     * The range enclosing this symbol not including leading/trailing whitespace but everything else
     * like comments. This information is typically used to determine if the clients cursor is
     * inside the symbol to reveal in the symbol in the UI.
     */
    val range: Range,

    /**
     * The range that should be selected and revealed when this symbol is being picked, e.g the name of a function.
     * Must be contained by the `range`.
     */
    val selectionRange: Range,

    /**
     * Children of this symbol, e.g. properties of a class.
     */
    val children: List<DocumentSymbol>? = emptyList()
)

/**
 * Represents information about programming constructs like variables, classes,
 * interfaces etc.
 */
@Serializable
data class SymbolInformation (
    /**
     * The name of this symbol.
     */
    val name: String,

    /**
     * The kind of this symbol.
     */
    val kind: SymbolKind,

    /**
     * Indicates if this symbol is deprecated.
     */
    val deprecated: Boolean? = null,

    /**
     * The location of this symbol. The location's range is used by a tool
     * to reveal the location in the editor. If the symbol is selected in the
     * tool the range's start information is used to position the cursor. So
     * the range usually spans more then the actual symbol's name and does
     * normally include things like visibility modifiers.
     *
     * The range doesn't have to denote a node range in the sense of a abstract
     * syntax tree. It can therefore not be used to re-construct a hierarchy of
     * the symbols.
     */
    val location: Location,

    /**
     * The name of the symbol containing this symbol. This information is for
     * user interface purposes (e.g. to render a qualifier in the user interface
     * if necessary). It can't be used to re-infer a hierarchy for the document
     * symbols.
     */
    val containerName: String? = null
)
