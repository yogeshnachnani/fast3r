package io.btc.lsp.handler

import io.btc.lsp.protocol.Message
import io.btc.lsp.protocol.RequestMessage
import io.btc.lsp.SHUTDOWN
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.consumeEachBufferRange
import io.ktor.utils.io.jvm.javaio.toByteReadChannel
import io.ktor.utils.io.nio.asOutput
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.nio.channels.Channels
import java.nio.channels.WritableByteChannel

/**
 * Manages all communication with the Language Server.
 * Invokes various handlers whenever we get a message from the server
 *
 * @param inetSocketAddress : If present, the [ServerCommunicator] will establish a tcp connection with the process listening on given address
 * @param executablePath: If present, the [ServerCommunicator] will spawn an LSP server process and communicate with it via it's stdio/stdout
 * One of these must be specified
 */
class ServerCommunicator constructor(
    inetSocketAddress: InetSocketAddress?,
    executablePath: String?
) {
    companion object {
        private val logger = LoggerFactory.getLogger(ServerCommunicator::class.java)
    }

    private val socket: Socket?
    private val process: Process?
    private val incomingMessageProcessor: IncomingMessageHandler
    private val outgoingMessageHandler: OutgoingMessageHandler

    init {
        if (inetSocketAddress != null) {
            socket = initialiseSocket(inetSocketAddress)
            process = null
            incomingMessageProcessor = IncomingMessageHandler(socket.openReadChannel())
            outgoingMessageHandler = ByteWriteChannelOutgoingMessageHandler(socket.openWriteChannel(autoFlush = true), incomingMessageProcessor)
        } else {
            process = ProcessBuilder(executablePath!!).start()
            socket = null
            incomingMessageProcessor = IncomingMessageHandler(process.inputStream.toByteReadChannel())
            outgoingMessageHandler = OutputChannelOutgoingMessageHandler(process.outputStream, incomingMessageProcessor)
        }
    }

    suspend fun closeAsync(): Deferred<Boolean> {
        val serverShutdown = CompletableDeferred<Boolean>()
        val shutdownMessage = RequestMessage(
            id = generateUUid(),
            method = SHUTDOWN,
            params = null
        )
        send(shutdownMessage)  { shutdownResponse ->
            logger.info("Received shutdown response: {}", shutdownResponse)
            socket?.close()?.apply {
                logger.debug("Closed socket with LSP server")
                outgoingMessageHandler.close()
                incomingMessageProcessor.close()
                serverShutdown.complete(true)
            }
            process?.onExit()?.apply {
                logger.debug("Shutdown of spawned LSP server complete")
                outgoingMessageHandler.close()
                incomingMessageProcessor.close()
                serverShutdown.complete(true)
            }
        }
        return serverShutdown
    }

    fun send(message: Message, handle: ((Message) -> Unit )? = null) {
        runBlocking(Dispatchers.IO) {
            outgoingMessageHandler.sendToServer(message, handle)
        }
    }


    private fun initialiseSocket(inetSocketAddress: InetSocketAddress) = runBlocking {
        try {
            aSocket(ActorSelectorManager(Dispatchers.IO))
                .tcp()
                .connect(inetSocketAddress)
        } catch (e: Exception) {
            logger.warn("Could not initialise connection with Server", e)
            throw e
        }
    }

}

