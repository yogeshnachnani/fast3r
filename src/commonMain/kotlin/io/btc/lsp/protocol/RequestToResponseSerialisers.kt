package io.btc.lsp.protocol

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer

fun RequestMessageParams.getCorrespondingDeserializer(): KSerializer<out Any> {
    return when (this) {
        is InitializeParams -> InitializeResult.serializer()
        is WorkspaceSymbolParams -> ListSerializer(SymbolInformation.serializer())
        is DeclarationParams -> ListSerializer(LocationLink.serializer())
        is ReferenceParams -> ListSerializer(Location.serializer())
        is DocumentHighlightParams -> ListSerializer(DocumentHighlight.serializer())
        is DocumentSymbolParams -> ListSerializer((DocumentSymbol.serializer()))
        is CodeActionParams -> ListSerializer(CodeAction.serializer())
        is CodeLensParams -> ListSerializer(CodeLens.serializer())
        is DocumentLinkParams -> ListSerializer(DocumentLink.serializer())
        is DocumentColorParams -> ListSerializer(ColorInformation.serializer())
        is ColorPresentationParams -> ListSerializer(ColorPresentation.serializer())
        is DocumentFormattingParams -> ListSerializer(TextEdit.serializer())
        is DocumentRangeFormattingParams -> ListSerializer(TextEdit.serializer())
        is DocumentOnTypeFormattingParams -> ListSerializer(TextEdit.serializer())
        is RenameParams -> WorkspaceEdit.serializer()
        is PrepareRenameParams -> Range.serializer()
        is FoldingRangeParams -> ListSerializer(FoldingRange.serializer())
        is SelectionRangeParams -> ListSerializer(SelectionRange.serializer())
        is ApplyWorkspaceEditParams -> TODO()
        is HoverParams -> TODO()
        is SignatureHelpParams -> TODO()
        is DefinitionParams -> TODO()
        is TypeDefinitionParams -> TODO()
        is ImplementationParams -> TODO()
    }
}