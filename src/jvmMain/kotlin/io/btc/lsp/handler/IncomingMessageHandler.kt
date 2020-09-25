package io.btc.lsp.handler

import io.btc.lsp.CONTENT_LENGTH_REGEX
import io.btc.lsp.protocol.DidOpenTextDocumentParams
import io.btc.lsp.protocol.LogMessageParams
import io.btc.lsp.protocol.Message
import io.btc.lsp.protocol.MessageSerializer
import io.btc.lsp.protocol.NotificationMessage
import io.btc.lsp.protocol.ProgressParams
import io.btc.lsp.protocol.RequestMessage
import io.btc.lsp.protocol.ResponseMessage
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import jsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.slf4j.LoggerFactory
import java.io.Closeable

class IncomingMessageHandler (
    readChannel: ByteReadChannel
): Closeable {
    private val messageHandlers: MutableMap<String, (Message) -> Unit> = mutableMapOf()

    companion object {
        private val logger = LoggerFactory.getLogger(IncomingMessageHandler::class.java)
        private val BYTE_BUFFER_FOR_NEWLINE = ByteArray(2)
    }

    private val messageProcessor: MessageProcessor

    init {
        messageProcessor = MessageProcessor { message ->
            when (message) {
                is RequestMessage -> TODO()
                is ResponseMessage<*> -> {
                    val registeredHandler = messageHandlers[message.id]
                    registeredHandler
                        ?.invoke(message)
                        ?.also {
                            messageHandlers.remove(message.id)
                        }
                        ?: logger.debug("Could not find incoming message handler for {}. Just printing message {} ", message.id ,message)
                }
                is NotificationMessage<*> -> {
                    when (message.params) {
                        is ProgressParams<*> -> TODO()
                        is LogMessageParams -> logger.debug("Log: {}", message.params.message)
                        is DidOpenTextDocumentParams -> TODO()
                    }
                }
            }
        }
        initializeIncomingMessageProcessor(readChannel)
    }

    operator fun set(id: String, handler: (Message) -> Unit) {
        messageHandlers[id] = handler
    }
    internal operator fun get(id: String): ((Message) -> Unit)? {
        return messageHandlers[id]
    }

    override fun close() {
        messageProcessor.close()
    }

    private fun initializeIncomingMessageProcessor(readChannel: ByteReadChannel) {
        GlobalScope.async(Dispatchers.IO) {
            while (!readChannel.isClosedForRead) {
                try {
                    readChannel.readFromServer()
                        ?.let {
                            messageProcessor.sendForProcessing(it)
                        }
                } catch (exception: Exception) {
                    logger.warn("Error while reading from server. This is skipped for now but could be dangerous later", exception)
                }
            }
        }
    }

    private suspend fun ByteReadChannel.readFromServer(): Message? {
        val initialLine = readUTF8Line()
        logger.trace("Received line from server: {}", initialLine)
        val bytesToRead = CONTENT_LENGTH_REGEX.matchEntire(initialLine ?: "")
            ?.let {
                it.groupValues[1].toInt()
            }
        return if (bytesToRead != null) {
            /** First consume [NEWLINE] */
            readFully(BYTE_BUFFER_FOR_NEWLINE, 0, 2)
            /** Now consume the actual message */
            val byteBuffer = ByteArray(bytesToRead)
            readFully(byteBuffer, 0, bytesToRead)
            val messageReceivedAsString = String(byteBuffer)
            jsonParser.decodeFromString(MessageSerializer, messageReceivedAsString)
        } else {
            logger.warn("Ignoring read line {} since it didn't have content length", initialLine)
            null
        }
    }
}