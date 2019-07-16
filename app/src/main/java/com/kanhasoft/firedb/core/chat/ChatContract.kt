package com.kanhasoft.firedb.core.chat

import android.content.Context
import com.kanhasoft.firedb.model.chat.Message

interface ChatContract {
    interface Presenter {
        fun sendMessage(context: Context, chat: Message, receiverFirebaseToken: String)

        fun getMessage(senderUid: String, receiverUid: String)

        fun clearChat(senderUid: String, receiverUid: String)

        fun deleteSingleChatMessage(senderUid: String, receiverUid: String, timeStamp: String, pos: Int)
    }

    interface Interactor {
        fun sendMessageToFirebaseUser(context: Context, chat: Message, receiverFirebaseToken: String)

        fun getMessageFromFirebaseUser(senderUid: String, receiverUid: String)

        fun removeChatFromFirebase(senderUid: String, receiverUid: String)

        fun deleteSingleChatMessageFromFirebase(senderUid: String, receiverUid: String, timeStamp: String, pos: Int)

    }

    interface onDeleteSingleMessageListener {
        fun onDeleteMessageSuccess(pos: Int)

        fun onDeleteMessageFailure(message: String)
    }

    interface OnSendMessageListener {
        fun onSendMessageSuccess()

        fun onSendMessageFailure(message: String)
    }

    interface OnGetMessagesListener {
        fun onGetMessagesSuccess(msg: Message)

        fun onGetMessagesFailure(message: String)

        fun onGetAllMessagesSuccess(fcmChat: ArrayList<Message>)
    }

    interface OnClearMessageListener {
        fun onClearMessageSuccess()

        fun onClearMessageFailure(message: String)
    }

    interface View {
        fun onSendMessageSuccess()

        fun onSendMessageFailure(message: String)

        fun onGetMessagesSuccess(msg: Message)

        fun onGetMessagesFailure(message: String)

        fun onClearMessageSuccess()

        fun onClearMessageFailure(message: String)

        fun onGetAllMessageSuccess(fcmChats: ArrayList<Message>)

        fun onDeleteMessageSuccess(pos: Int)

        fun onDeleteMessageFailure(message: String)

    }
}