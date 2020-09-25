package io.btc.lsp.handler

import io.btc.lsp.CONTENT_LENGTH
import io.btc.lsp.NEWLINE
import io.btc.lsp.protocol.MessageSerializer
import io.btc.lsp.protocol.SymbolInformation
import io.btc.supercr.git.processor.DiffProcessorTest
import io.ktor.utils.io.jvm.javaio.toByteReadChannel
import kotlinx.serialization.builtins.ListSerializer
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.nio.charset.Charset
import java.util.concurrent.*
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

class IncomingMessageHandlerTest {

   val pipedOutputStream = PipedOutputStream()
   val pipedInputStream = PipedInputStream(pipedOutputStream)
   val incomingMessageHandler = IncomingMessageHandler(
      readChannel = pipedInputStream.toByteReadChannel()
   )

   @AfterTest
   fun tearDown() {
      incomingMessageHandler.close()
      pipedInputStream.close()
      pipedOutputStream.close()
   }

   @Test
   fun shouldInvokeRegisteredMessageHandlerForResponseMessage() {
      /** First, register the deserializer */
      MessageSerializer["157ca92c41ab45c081db014ea8155338"]  = ListSerializer(SymbolInformation.serializer())
      /** Next, read the sample contents */
      val data = getPayloadFor("kotlin_language_server_sample_symbol_result.json")
      sendMessage("157ca92c41ab45c081db014ea8155338", data) {
         true
      }
   }

   @Test
   fun shouldRemoveHandlerAfterMessageProcessed() {
      val messageId = "157ca92c41ab45c081db014ea8155338"
      MessageSerializer[messageId]  = ListSerializer(SymbolInformation.serializer())
      val data = getPayloadFor("kotlin_language_server_sample_symbol_result.json")
      sendMessage(messageId, data) {
         true
      }
      assertNull(incomingMessageHandler[messageId])
   }


   private fun sendMessage(messageId: String, data: ByteArray, assertionBlock: () -> Boolean) {
      var assertionResult = false
      val countDownLatch = CountDownLatch(1)
      incomingMessageHandler[messageId] = {
         assertionResult = assertionBlock.invoke()
         countDownLatch.countDown()
      }
      /** Connect the input & output stream so that data is available for reading in [incomingMessageHandler] */
      pipedOutputStream.write(data)
      countDownLatch.await()
      assertTrue(assertionResult)
   }

   private fun getPayloadFor(testFileName: String): ByteArray {
      return DiffProcessorTest::class.java.classLoader
         .getResource(testFileName)!!
         .file
         .let {
            val jsonContents = FileUtils.readFileToString(File(it), Charset.defaultCharset())
            val contentLength = jsonContents.length
            "$CONTENT_LENGTH: $contentLength$NEWLINE$NEWLINE$jsonContents"
               .toByteArray(Charset.defaultCharset())
         }
   }
}