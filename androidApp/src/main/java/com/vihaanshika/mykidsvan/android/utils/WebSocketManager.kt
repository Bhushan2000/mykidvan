package com.vihaanshika.mykidsvan.android.utils


import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

class WebSocketManager(
    private val httpClient: HttpClient
) {
    private var session: WebSocketSession? = null

    suspend fun connect(url: String) {
        session = httpClient.webSocketSession { url { url } }
    }

    suspend fun send(message: String) {
        session?.send(Frame.Text(message))
    }

    fun observeMessage(scope: CoroutineScope, onMessage: (String) -> Unit) {
        scope.launch {
            session?.incoming?.consumeAsFlow()?.collect { frame->
                if (frame is Frame.Text){
                    onMessage(frame.readText())
                }
            }
        }
    }
    suspend fun disconnect(){
        session?.close()
    }

}