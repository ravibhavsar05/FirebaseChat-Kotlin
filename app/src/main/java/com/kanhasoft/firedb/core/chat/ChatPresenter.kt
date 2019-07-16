package com.kanhasoft.firedb.core.chat

import android.content.Context
import com.kanhasoft.firedb.model.chat.Message

class ChatPresenter
    (view: ChatContract.View) : ChatContract.Presenter, ChatContract.OnSendMessageListener,
    ChatContract.OnGetMessagesListener, ChatContract.OnClearMessageListener,
    ChatContract.onDeleteSingleMessageListener {


    val chatInteractor: ChatInteractor
    val view: ChatContract.View

    init {
        this.view = view
        chatInteractor = ChatInteractor(this, this, this, this)
    }

    override fun sendMessage(context: Context, chat: Message, receiverFirebaseToken: String) {
        chatInteractor.sendMessageToFirebaseUser(context, chat, receiverFirebaseToken)
    }

    override fun getMessage(senderUid: String, receiverUid: String) {
        chatInteractor.getMessageFromFirebaseUser(senderUid, receiverUid)
    }

    override fun clearChat(senderUid: String, receiverUid: String) {
        chatInteractor.removeChatFromFirebase(senderUid, receiverUid)
    }

    override fun deleteSingleChatMessage(senderUid: String, receiverUid: String, timeStamp: String, pos: Int) {
        chatInteractor.deleteSingleChatMessageFromFirebase(senderUid, receiverUid, timeStamp, pos)
    }

    override fun onDeleteMessageSuccess(pos: Int) {
        view.onDeleteMessageSuccess(pos)
    }

    override fun onDeleteMessageFailure(message: String) {
        view.onDeleteMessageFailure(message)
    }

    override fun onGetMessagesSuccess(msg: Message) {
        view.onGetMessagesSuccess(msg)
    }

    override fun onSendMessageSuccess() {
        view.onSendMessageSuccess()
    }

    override fun onSendMessageFailure(message: String) {
        view.onSendMessageFailure(message)
    }

    override fun onGetMessagesFailure(message: String) {
        view.onGetMessagesFailure(message)
    }

    override fun onClearMessageSuccess() {
        view.onClearMessageSuccess()
    }

    override fun onClearMessageFailure(message: String) {
        view.onClearMessageFailure(message)
    }


    override fun onGetAllMessagesSuccess(fcmChat: ArrayList<Message>) {
        view.onGetAllMessageSuccess(fcmChat)
    }

}