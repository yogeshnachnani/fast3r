package io.btc.lsp.handler

import io.btc.lsp.INITIALIZE
import io.btc.lsp.TextDocument
import io.btc.lsp.WorkspaceMethods
import io.btc.lsp.capability.ClientCapabilities
import io.btc.lsp.capability.CodeLensClientCapabilities
import io.btc.lsp.capability.DeclarationClientCapabilities
import io.btc.lsp.capability.DefinitionClientCapabilities
import io.btc.lsp.capability.DidChangeConfigurationClientCapabilities
import io.btc.lsp.capability.DidChangeWatchedFilesClientCapabilities
import io.btc.lsp.capability.DocumentFormattingClientCapabilities
import io.btc.lsp.capability.DocumentHighlightClientCapabilities
import io.btc.lsp.capability.DocumentLinkClientCapabilities
import io.btc.lsp.capability.DocumentOnTypeFormattingClientCapabilities
import io.btc.lsp.capability.DocumentRangeFormattingClientCapabilities
import io.btc.lsp.capability.DocumentSymbolClientCapabilities
import io.btc.lsp.capability.ExecuteCommandClientCapabilities
import io.btc.lsp.capability.FailureHandlingKind
import io.btc.lsp.capability.FoldingRangeClientCapabilities
import io.btc.lsp.capability.HoverClientCapabilities
import io.btc.lsp.capability.ImplementationClientCapabilities
import io.btc.lsp.capability.MarkupKind
import io.btc.lsp.capability.ParameterInformation
import io.btc.lsp.capability.ReferenceClientCapabilities
import io.btc.lsp.capability.ResourceOperationKind
import io.btc.lsp.capability.SelectionRangeClientCapabilities
import io.btc.lsp.capability.SignatureHelpClientCapabilities
import io.btc.lsp.capability.SignatureInformation
import io.btc.lsp.capability.SymbolKind
import io.btc.lsp.capability.SymbolKindValueSet
import io.btc.lsp.capability.SymbolTagValueSet
import io.btc.lsp.capability.TextDocumentClientCapabilities
import io.btc.lsp.capability.TextDocumentSyncClientCapabilities
import io.btc.lsp.capability.TypeDefinitionClientCapabilities
import io.btc.lsp.capability.WorkDoneProgressOptions
import io.btc.lsp.capability.WorkspaceCapabilities
import io.btc.lsp.capability.WorkspaceEditClientCapabilities
import io.btc.lsp.capability.WorkspaceSymbolClientCapabilities
import io.btc.lsp.capability.allSymbolKind
import io.btc.lsp.capability.allSymbolTags
import io.btc.lsp.protocol.ApplyWorkspaceEditParams
import io.btc.lsp.protocol.ClientInfo
import io.btc.lsp.protocol.InitializeParams
import io.btc.lsp.protocol.Message
import io.btc.lsp.protocol.Position
import io.btc.lsp.protocol.ReferenceContext
import io.btc.lsp.protocol.ReferenceParams
import io.btc.lsp.protocol.RequestMessage
import io.btc.lsp.protocol.ResponseMessage
import io.btc.lsp.protocol.SymbolInformation
import io.btc.lsp.protocol.TextDocumentIdentifier
import io.btc.lsp.protocol.WorkspaceEdit
import io.btc.lsp.protocol.WorkspaceFolder
import io.btc.lsp.protocol.WorkspaceSymbolParams
import io.btc.utils.TestUtils
import kotlinx.coroutines.runBlocking
import org.junit.AfterClass
import org.junit.BeforeClass
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Connect with Kotlin LSP server and check E2E functionality
 *
 */
class ServerCommunicatorIntegrationTest {
    @Test
    fun `find symbols test`() {
        val symbolsRequest = RequestMessage(
            id = generateUUid(),
            method = WorkspaceMethods.symbol.toString(),
            params = WorkspaceSymbolParams(
                query = "SingletonFoo",
                workDoneToken = "find-my-foo",
                partialResultToken = "foo-is-found"
            )
        )
        sendAndWait(symbolsRequest) { serverResponse ->
            logger.debug("Received server response {}", serverResponse)
            val symbolMessageResponse = serverResponse as ResponseMessage<List<SymbolInformation>>
            assertEquals(1, symbolMessageResponse.result!!.size)
            with(symbolMessageResponse.result!!.first()) {
                assertEquals(kind, SymbolKind.Class)
                assertTrue(location.uri.endsWith("DummyResourceForLspIntegrationTest.kt"))
            }
        }
    }

    @Test
    fun `find references test`() {
        val referencesRequest = RequestMessage( id = generateUUid(),
            method = TextDocument.references.toString(),
            params = ReferenceParams(
                context = ReferenceContext(
                    includeDeclaration = true
                ),
                textDocument = TextDocumentIdentifier(uri = "file://${TestUtils.btcRepoDir}/kotlin/src/jvmTest/kotlin/io/btc/lsp/handler/DummyResourceForLspIntegrationTest.kt"),
                position = Position(5 , 6),
                workDoneToken = "find-my-refs",
                partialResultToken = "find-my-refs-please"
            )
        )
        sendAndWait(referencesRequest) { serverResponse ->
            logger.debug("Received server response {}", serverResponse)
        }
    }


    @Test
    fun `workspace edit test`() {
        val editRequest = RequestMessage(
            id =  generateUUid(),
            method = WorkspaceMethods.applyEdit.toString(),
            params = ApplyWorkspaceEditParams(
                label = "testing",
                edit = WorkspaceEdit(
                    changes = mapOf(

                    ),
                )
            )
        )
    }

    // TODO: Write some more test cases for error handling scenarios
    companion object {
        private val logger = LoggerFactory.getLogger(ServerCommunicatorIntegrationTest::class.java)

        private val serverCommunicator: ServerCommunicator = ServerCommunicator(
            inetSocketAddress = null,
            executablePath = "/home/yogesh/work/kotlin-language-server/server/build/install/server/bin/kotlin-language-server"
        )

        @JvmStatic
        @BeforeClass
        fun initLspServer() {
            val initMessage = RequestMessage(
                id = generateUUid(),
                method = INITIALIZE,
                params = defaultInitParams()
            )
            sendAndWait(initMessage) {
                logger.debug("Seems like server is all set and initialised, {} ", it)
            }
        }

        @JvmStatic
        @AfterClass
        fun shutdownEverything() {
            runBlocking {
                val closed = serverCommunicator.closeAsync()
                logger.debug("Will await shutdown of lsp server")
                closed.await()
                logger.debug("Lsp server shutdown complete..")
            }
        }

        private fun sendAndWait(messageToServer: Message, handler: (Message) -> Unit) {
            val countDownLatch = CountDownLatch(1)
            serverCommunicator.send(messageToServer) { responseMessage ->
                handler.invoke(responseMessage)
                countDownLatch.countDown()
            }
            countDownLatch.await()
        }

        private fun defaultInitParams(): InitializeParams {
            return InitializeParams(
                workDoneToken = "1234",
                processId = null,
                clientInfo = ClientInfo(
                    name = "test-kotlin-client",
                    version = "1.0.0"
                ),
                rootPath = "/home/yogesh/work/btc/kotlin",
                rootUri = File("/home/yogesh/work/btc/kotlin").toURI().toString(),
                initializationOptions = null,
                capabilities = ClientCapabilities(
                    workspace = WorkspaceCapabilities(
                        applyEdit = true,
                        workspaceEdit = WorkspaceEditClientCapabilities(
                            documentChanges = true,
                            resourceOperations = listOf(ResourceOperationKind.create, ResourceOperationKind.delete, ResourceOperationKind.rename),
                            failureHandling = FailureHandlingKind.transactional
                        ),
                        didChangeConfiguration = DidChangeConfigurationClientCapabilities(true),
                        didChangeWatchedFiles = DidChangeWatchedFilesClientCapabilities(true),
                        symbol = WorkspaceSymbolClientCapabilities(
                            dynamicRegistration = true,
                            symbolKind = SymbolKindValueSet(
                                valueSet = allSymbolKind
                            ),
                            tagSupport = SymbolTagValueSet(
                                valueSet = allSymbolTags
                            )
                        ),
                        executeCommand = ExecuteCommandClientCapabilities(true),
                        workspaceFolders = true,
                        configuration = true
                    ),
                    textDocument = TextDocumentClientCapabilities(
                        synchronization = TextDocumentSyncClientCapabilities(
                            dynamicRegistration = true,
                            willSave = false,
                            willSaveWaitUntil = false,
                            didSave = false
                        ),
                        completion = null,
                        hover = HoverClientCapabilities(dynamicRegistration = true, contentFormat = listOf(MarkupKind.plaintext)),
                        signatureHelp = SignatureHelpClientCapabilities(
                            dynamicRegistration = true,
                            signatureInformation = SignatureInformation(
                                documentationFormat = listOf(MarkupKind.plaintext),
                                parameterInformation = ParameterInformation(labelOffsetSupport = true),
                                activeParameterSupport = true
                            ),
                            contextSupport = true
                        ),
                        declaration = DeclarationClientCapabilities(
                            dynamicRegistration = true,
                            linkSupport = true
                        ),
                        definition = DefinitionClientCapabilities(
                            dynamicRegistration = true,
                            linkSupport = true
                        ),
                        typeDefinition = TypeDefinitionClientCapabilities(
                            dynamicRegistration = true,
                            linkSupport = true
                        ),
                        implementation = ImplementationClientCapabilities(
                            dynamicRegistration = true,
                            linkSupport = true
                        ),
                        references = ReferenceClientCapabilities(
                            dynamicRegistration = true
                        ),
                        documentHighlight = DocumentHighlightClientCapabilities(
                            dynamicRegistration = true
                        ),
                        documentSymbol = DocumentSymbolClientCapabilities(
                            dynamicRegistration = true,
                            symbolKind = SymbolKindValueSet(
                                valueSet = allSymbolKind
                            ),
                            hierarchicalDocumentSymbolSupport = false,
                            tagSupport = SymbolTagValueSet(
                                valueSet = allSymbolTags
                            )
                        ),
                        codeAction = null,
                        codeLens = CodeLensClientCapabilities(
                            dynamicRegistration = true
                        ),
                        documentLink = DocumentLinkClientCapabilities(
                            dynamicRegistration = true,
                            tooltipSupport = true
                        ),
                        colorProvider = null,
                        formatting = DocumentFormattingClientCapabilities(
                            dynamicRegistration = true
                        ),
                        rangeFormatting = DocumentRangeFormattingClientCapabilities(
                            dynamicRegistration = true
                        ),
                        onTypeFormatting = DocumentOnTypeFormattingClientCapabilities(
                            dynamicRegistration = true
                        ),
                        rename = null,
                        publishDiagnostics = null,
                        foldingRange = FoldingRangeClientCapabilities(
                            dynamicRegistration = true
                        ),
                        selectionRange = SelectionRangeClientCapabilities(
                            dynamicRegistration = true
                        )
                    ),
                    window = WorkDoneProgressOptions(
                        workDoneProgress = true
                    ),
                    experimental = null
                ),
                workspaceFolders = listOf(WorkspaceFolder(File("/home/yogesh/work/btc/kotlin").toURI().toString(), "BTC"))
            )
        }
    }

}