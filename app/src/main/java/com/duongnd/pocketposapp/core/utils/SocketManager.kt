package com.duongnd.pocketposapp.core.utils

import io.socket.client.IO
import io.socket.client.Socket

object SocketManager {

    private var socket: Socket? = null

    fun connect(baseUrl: String) {
        if (socket?.connected() == true) return

        socket = IO.socket(baseUrl)

        socket?.connect()

        socket?.on(Socket.EVENT_CONNECT) {
            println("Socket connected: ${socket?.id()}")
        }

        socket?.on(Socket.EVENT_DISCONNECT) {
            println("Socket disconnected")
        }
    }

    fun joinStore(storeId: String) {
        socket?.emit("join_store", storeId)
    }

    fun joinOrder(orderId: String) {
        socket?.emit("join_order", orderId)
    }

    fun leaveStore(storeId: String) {
        socket?.emit("leave_store", storeId)
    }

    fun leaveOrder(orderId: String) {
        socket?.emit("leave_order", orderId)
    }

    fun listenPaymentSuccess(onSuccess: (String) -> Unit) {
        socket?.on("payment_success") { args ->
            val data = args[0] as org.json.JSONObject

            val orderId = data.getString("orderId")
            val status = data.getString("paymentStatus")

            if (status == "paid") {
                onSuccess(orderId)
            }
        }
    }

    fun disconnect() {
        socket?.disconnect()
        socket = null
    }
}