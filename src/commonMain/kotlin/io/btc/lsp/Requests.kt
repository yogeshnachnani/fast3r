package io.btc.lsp

const val SHUTDOWN = "shutdown"
const val INITIALIZE = "initialize"

enum class WorkspaceMethods {
    executeCommand,

    /**
     * The workspace/applyEdit request is sent from the server to the client to modify resource on the client side.
     */
    applyEdit,
    symbol;

    override fun toString(): String {
        return "workspace/${this.name}"
    }
}

enum class TextDocument {
    hover,
    signatureHelp,
    declaration,
    definition,
    typeDefinition,
    implementation,
    references,
    documentHighlight,
    documentSymbol,
    codeAction,
    codeLens,
    documentLink,
    documentColor,
    colorPresentation,
    formatting,
    rangeFormatting,
    onTypeFormatting,
    rename,
    prepareRename,
    foldingRange,
    selectionRange,
    didOpen;

    override fun toString(): String {
        return "textDocument/${this.name}"
    }
}