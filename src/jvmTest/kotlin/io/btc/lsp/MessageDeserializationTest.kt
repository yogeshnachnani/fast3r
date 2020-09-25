package io.btc.lsp

import io.btc.lsp.protocol.InitializeResult
import io.btc.lsp.protocol.InternalError
import io.btc.lsp.protocol.LogMessageParams
import io.btc.lsp.protocol.MessageSerializer
import io.btc.lsp.protocol.NotificationMessage
import io.btc.lsp.protocol.ProgressParams
import io.btc.lsp.protocol.ResponseMessage
import io.btc.lsp.protocol.WorkDoneProgressBegin
import kotlinx.serialization.json.Json
import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.charset.Charset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class MessageDeserializationTest  {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun `test polymorphic deserialization for NotificationMessage`() {
        val serializedNotificationMessage = """
           {"jsonrpc":"2.0","method":"window/logMessage","params":{"id":3,"message":"main      Connected to client"}}
        """.trimIndent()

        val result = json.decodeFromString(MessageSerializer , serializedNotificationMessage) as NotificationMessage<LogMessageParams>
        assertTrue {
            result.params.id == 3L
        }

        val serializedWorkProgressMessage =  """
        {
            "jsonrpc":"2.0",
            "method":"$/progress",
            "params":
            {
                "token": "1d546990-40a3-4b77-b134-46622995f6ae",
                "value": {
                    "kind": "begin",
                    "title": "Finding references for A#foo",
                    "cancellable": false,
                    "message": "Processing file X.ts",
                    "percentage": 0
                }
            }
        }
        """.trimIndent()
        val progressMessage =
            json.decodeFromString(MessageSerializer , serializedWorkProgressMessage) as NotificationMessage<ProgressParams<WorkDoneProgressBegin>>
        assertTrue {
            progressMessage.params.value.title == "Finding references for A#foo"
        }
    }

    @Test
    fun `test deserialization for Init Param results uses known serializer`() {
        val str = """
            {
              "jsonrpc": "2.0",
              "id": "1",
              "result": {
                "capabilities": {
                  "textDocumentSync": 2,
                  "hoverProvider": true,
                  "completionProvider": {
                    "resolveProvider": false,
                    "triggerCharacters": [
                      "."
                    ]
                  },
                  "signatureHelpProvider": {
                    "triggerCharacters": [
                      "(",
                      ","
                    ]
                  },
                  "definitionProvider": true,
                  "referencesProvider": true,
                  "documentSymbolProvider": true,
                  "workspaceSymbolProvider": true,
                  "codeActionProvider": true,
                  "documentFormattingProvider": true,
                  "documentRangeFormattingProvider": true,
                  "executeCommandProvider": {
                    "commands": [
                      "convertJavaToKotlin"
                    ]
                  },
                  "workspace": {
                    "workspaceFolders": {
                      "supported": true,
                      "changeNotifications": true
                    }
                  }
                }
              }
            }
        """.trimIndent()
        MessageSerializer["1"] = InitializeResult.serializer()
        val initResult = json.decodeFromString(MessageSerializer , str)

    }

    @Test
    fun `test deserialization for ResponseMessage uses String deserializer if id not registered`() {
        val str = """
            {
              "jsonrpc": "2.0",
              "id": "1",
              "result": "This is a result string"
            }
        """.trimIndent()
        json.decodeFromString(MessageSerializer , str)
    }

    @Test
    fun `test deserialization for ResponseMessage with error`() {
        val str = MessageDeserializationTest::class.java.classLoader
            .getResource("kotlin_lsp_server_error_response_example1.json")!!
            .file
            .let {
                FileUtils.readFileToString(File(it), Charset.defaultCharset())
            }
        val errorResponseMessage: ResponseMessage<*> = json.decodeFromString(MessageSerializer, str) as ResponseMessage<*>
        assertEquals(InternalError ,errorResponseMessage.error!!.code)
    }

}