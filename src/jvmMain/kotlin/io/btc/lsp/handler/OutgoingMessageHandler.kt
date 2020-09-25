package io.btc.lsp.handler

import io.btc.lsp.CONTENT_LENGTH
import io.btc.lsp.NEWLINE
import io.btc.lsp.protocol.Message
import io.btc.lsp.protocol.MessageSerializer
import io.btc.lsp.protocol.NotificationMessage
import io.btc.lsp.protocol.RequestMessage
import io.btc.lsp.protocol.ResponseMessage
import io.btc.lsp.protocol.getCorrespondingDeserializer
import io.ktor.utils.io.ByteWriteChannel
import jsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.io.IOException
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.channels.WritableByteChannel
import java.nio.charset.Charset
import kotlin.jvm.Throws
import kotlin.system.exitProcess

abstract class OutgoingMessageHandler (
    private val incomingMessageHandler: IncomingMessageHandler
): Closeable {
    companion object {
        private val logger = LoggerFactory.getLogger(OutgoingMessageHandler::class.java)
    }

    private val messageProcessor: MessageProcessor
    init {
        messageProcessor = MessageProcessor { messageToServer ->
            logger.debug("Will send message {} to server", messageToServer)
            writeToServer(messageToServer)
        }
    }

    suspend fun sendToServer(outgoingMessage: Message, handler: ( (Message) -> Unit )?) {
        when (outgoingMessage) {
            is RequestMessage -> {
                /** Register handler for the response */
                val messageId = outgoingMessage.id
                val chosenSerializer =  outgoingMessage.params?.getCorrespondingDeserializer()
                if (chosenSerializer != null) {
                    MessageSerializer[messageId] = chosenSerializer
                }
                handler?.apply { incomingMessageHandler[messageId] = this }
            }
            is ResponseMessage<*> -> {
                /** No special deserializer needed */
            }
            is NotificationMessage<*> -> {
                /** No special deserializer needed */
            }
        }
        messageProcessor.sendForProcessing(outgoingMessage)
    }

    private suspend fun writeToServer(message: Message) {
        try {
            val serializedRequest = jsonParser.encodeToString(Message.serializer(), message)
            val msg = "$CONTENT_LENGTH:${serializedRequest.length}$NEWLINE$NEWLINE$serializedRequest"
            logger.trace("Will send this to the server: {}", msg)
            doWrite(ByteBuffer.wrap(msg.toByteArray(Charset.defaultCharset())))
        } catch (exception: Exception) {
            val helpfulErrorMessage = when (message) {
                is RequestMessage -> {
                    "It is a RequestMessage with id : ${message.id}"
                }
                is ResponseMessage<*> -> {
                    "It is a ResponseMessage with id: ${message.id}"
                }
                is NotificationMessage<*> -> "It is a NotificaitonMessage with method ${message.method}"
            }
            logger.warn("Could not send a message to server. Perhaps this helps: $helpfulErrorMessage", exception)
        }
    }

    abstract suspend fun doWrite(byteBuffer: ByteBuffer)

    override fun close() {
        messageProcessor.close()
    }

}

class ByteWriteChannelOutgoingMessageHandler (
    private val writeChannel: ByteWriteChannel,
    incomingMessageHandler: IncomingMessageHandler
): OutgoingMessageHandler(incomingMessageHandler) {
    override suspend fun doWrite(byteBuffer: ByteBuffer) {
        writeChannel.writeFully(byteBuffer)
    }
}

class OutputChannelOutgoingMessageHandler(
   private val writeChannel: OutputStream,
   incomingMessageHandler: IncomingMessageHandler
): OutgoingMessageHandler(incomingMessageHandler) {

    override suspend fun doWrite(byteBuffer: ByteBuffer) {
        withContext(Dispatchers.IO) {
            writeChannel.write(byteBuffer.array())
            writeChannel.flush()
        }
    }
}