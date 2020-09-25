package io.btc.lsp.handler

import io.btc.lsp.protocol.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import org.slf4j.LoggerFactory
import java.io.Closeable

/**
 * Ensure single threaded processing of messages
 */
class MessageProcessor(
    private val processMessage: suspend (Message) -> Unit
): Closeable {

    companion object {
        private val logger = LoggerFactory.getLogger(OutgoingMessageHandler::class.java)
    }

    private val messageProcessingChannel: Channel<Message> = Channel(200)

    init {
        GlobalScope.async(Dispatchers.IO) {
            messageProcessingChannel.consumeEach {
                processMessage(it)
            }
        }
    }

    suspend fun sendForProcessing(message: Message) {
        messageProcessingChannel.send(message)
    }

    override fun close() {
        messageProcessingChannel.close()
    }

}
