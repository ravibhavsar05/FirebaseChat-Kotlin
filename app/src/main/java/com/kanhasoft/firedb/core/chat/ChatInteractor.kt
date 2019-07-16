package com.kanhasoft.firedb.core.chat

import android.content.Context
import android.util.Log
import com.google.firebase.database.*
import com.kanhasoft.firedb.common.Constant
import com.kanhasoft.firedb.common.SharedPrefUtil
import com.kanhasoft.firedb.fcm.FcmNotificationBuilder
import com.kanhasoft.firedb.model.chat.Message
import java.util.*

class ChatInteractor(
    onSendMessageListener: ChatContract.OnSendMessageListener,
    onGetMessagesListener: ChatContract.OnGetMessagesListener,
    onClearMessageListener: ChatContract.OnClearMessageListener,
    onDeleteSingleMessageListener: ChatContract.onDeleteSingleMessageListener
) : ChatContract.Interactor {


    internal var datas = ArrayList<Message>()
    private var setDataCounts = 0
    private var lastTimeZone: Long = 0
    val onSendMessageListener: ChatContract.OnSendMessageListener
    val onGetMessagesListener: ChatContract.OnGetMessagesListener
    val onClearMessageListener: ChatContract.OnClearMessageListener
    val onDeleteSingleMessageListener: ChatContract.onDeleteSingleMessageListener

    val TAG: String = ChatInteractor::class.java.simpleName

    init {
        this.onSendMessageListener = onSendMessageListener
        this.onGetMessagesListener = onGetMessagesListener
        this.onClearMessageListener = onClearMessageListener
        this.onDeleteSingleMessageListener = onDeleteSingleMessageListener
    }

    override fun deleteSingleChatMessageFromFirebase(
        senderUid: String,
        receiverUid: String,
        timeStamp: String,
        pos: Int
    ) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val room_type_1 = senderUid + "_" + receiverUid
        val room_type_2 = receiverUid + "_" + senderUid
        databaseReference.child(Constant.ARG_CHAT_ROOMS).ref.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                onDeleteSingleMessageListener.onDeleteMessageFailure("Something went wrong")
            }


            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {
                    databaseReference.child(Constant.ARG_CHAT_ROOMS).child(room_type_1)
                        .child(Constant.FIREBASE_MESSAGES).child(timeStamp).removeValue()
                        .addOnSuccessListener {
                            onDeleteSingleMessageListener.onDeleteMessageSuccess(pos)
                        }
                        .addOnFailureListener {
                            onDeleteSingleMessageListener.onDeleteMessageFailure("Something went wrong")
                        }
                } else if (dataSnapshot.hasChild(room_type_2)) {
                    databaseReference.child(Constant.ARG_CHAT_ROOMS).child(room_type_2)
                        .child(Constant.FIREBASE_MESSAGES).child(timeStamp).removeValue()
                        .addOnSuccessListener {
                            onDeleteSingleMessageListener.onDeleteMessageSuccess(pos)
                        }
                        .addOnFailureListener {
                            onDeleteSingleMessageListener.onDeleteMessageFailure("Something went wrong")
                        }
                }
            }
        })
    }

    override fun removeChatFromFirebase(senderUid: String, receiverUid: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val room_type_1 = senderUid + "_" + receiverUid
        val room_type_2 = receiverUid + "_" + senderUid
        databaseReference.child(Constant.ARG_CHAT_ROOMS).ref.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                onClearMessageListener.onClearMessageFailure("Something went wrong")
            }


            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {
                    databaseReference.child(Constant.ARG_CHAT_ROOMS).child(room_type_1).removeValue()
                        .addOnSuccessListener {
                            onClearMessageListener.onClearMessageSuccess()
                        }
                        .addOnFailureListener {
                            onClearMessageListener.onClearMessageFailure("Something went wrong")
                        }
                } else if (dataSnapshot.hasChild(room_type_2)) {
                    databaseReference.child(Constant.ARG_CHAT_ROOMS).child(room_type_2).removeValue()
                        .addOnSuccessListener {
                            onClearMessageListener.onClearMessageSuccess()
                        }
                        .addOnFailureListener {
                            onClearMessageListener.onClearMessageFailure("Something went wrong")
                        }
                }
            }
        })

    }

    override fun sendMessageToFirebaseUser(context: Context, chat: Message, receiverFirebaseToken: String) {
        val room_type_1 = chat.senderUid + "_" + chat.receiverUid
        val room_type_2 = chat.receiverUid + "_" + chat.senderUid

        val databaseReference = FirebaseDatabase.getInstance().reference

        databaseReference.child(Constant.ARG_CHAT_ROOMS).ref.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {
                    if (dataSnapshot.child(room_type_1).hasChild(Constant.FIREBASE_UNREADCOUNT) == false) {
                        val dataTobePush = HashMap<String, Any>()
                        dataTobePush[chat.senderUid] = 0
                        dataTobePush[chat.receiverUid] = 0
                        databaseReference.child(Constant.ARG_CHAT_ROOMS).child(room_type_1)
                            .child(Constant.FIREBASE_UNREADCOUNT).setValue(dataTobePush)
                    }
                    databaseReference.child(Constant.ARG_CHAT_ROOMS).child(room_type_1)
                        .child(Constant.FIREBASE_MESSAGES)
                        .child(chat.timeStamp.toString()).setValue(chat)
                    updateData(
                        databaseReference.child(Constant.ARG_CHAT_ROOMS).child(room_type_1).child(Constant.FIREBASE_UNREADCOUNT).ref,
                        chat.receiverUid
                    )

                } else if (dataSnapshot.hasChild(room_type_2)) {
                    if (dataSnapshot.child(room_type_2).hasChild(Constant.FIREBASE_UNREADCOUNT) == false) {
                        val dataTobePush = HashMap<String, Any>()
                        dataTobePush[chat.senderUid] = 0
                        dataTobePush[chat.receiverUid] = 0
                        databaseReference.child(Constant.ARG_CHAT_ROOMS).child(room_type_2)
                            .child(Constant.FIREBASE_UNREADCOUNT).setValue(dataTobePush)
                    }
                    databaseReference.child(Constant.ARG_CHAT_ROOMS).child(room_type_2)
                        .child(Constant.FIREBASE_MESSAGES)
                        .child(chat.timeStamp.toString()).setValue(chat)
                    updateData(
                        databaseReference.child(Constant.ARG_CHAT_ROOMS).child(room_type_2).child(Constant.FIREBASE_UNREADCOUNT).ref,
                        chat.receiverUid
                    )
                } else {
                    if (dataSnapshot.child(room_type_1).hasChild(Constant.FIREBASE_UNREADCOUNT) == false) {
                        val dataTobePush = HashMap<String, Any>()
                        dataTobePush[chat.senderUid] = 0
                        dataTobePush[chat.receiverUid] = 0
                        databaseReference.child(Constant.ARG_CHAT_ROOMS).child(room_type_1)
                            .child(Constant.FIREBASE_UNREADCOUNT).setValue(dataTobePush)
                    }
                    databaseReference.child(Constant.ARG_CHAT_ROOMS).child(room_type_1)
                        .child(Constant.FIREBASE_MESSAGES)
                        .child(chat.timeStamp.toString()).setValue(chat)
                    getMessageFromFirebaseUser(
                        chat.senderUid,
                        chat.receiverUid
                    )
                    updateData(
                        databaseReference.child(Constant.ARG_CHAT_ROOMS).child(room_type_1).child(Constant.FIREBASE_UNREADCOUNT).ref,
                        chat.receiverUid
                    )
                }
                // send push notification to the receiver
                sendPushNotificationToReceiver(
                    chat.sender,
                    chat.message,
                    chat.senderUid,
                    SharedPrefUtil(context).getString(Constant.ARG_FIREBASE_TOKEN).toString(),
                    receiverFirebaseToken
                )
                onSendMessageListener.onSendMessageSuccess()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onSendMessageListener.onSendMessageFailure("Unable to send message: " + databaseError.message)
            }
        })
    }

    private fun sendPushNotificationToReceiver(
        username: String,
        message: String,
        uid: String,
        firebaseToken: String,
        receiverFirebaseToken: String
    ) {
        FcmNotificationBuilder.initialize()
            .title(username)
            .message(message)
            .username(username)
            .uid(uid)
            .firebaseToken(firebaseToken)
            .receiverFirebaseToken(receiverFirebaseToken)
            .send()
    }

    override fun getMessageFromFirebaseUser(senderUid: String, receiverUid: String) {
        val room_type_1 = senderUid + "_" + receiverUid
        val room_type_2 = receiverUid + "_" + senderUid

        Log.e("TAG Rooms ", room_type_1 + " ::  :: " + room_type_2)

        val databaseReference = FirebaseDatabase.getInstance().reference

        databaseReference.child(Constant.ARG_CHAT_ROOMS).ref.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {
                    updateCount(
                        databaseReference.child(Constant.ARG_CHAT_ROOMS).child(room_type_1).child(Constant.FIREBASE_UNREADCOUNT).ref,
                        senderUid
                    )
                    setDataCounts =
                        dataSnapshot.child(room_type_1).child(Constant.FIREBASE_MESSAGES).childrenCount.toInt()


                    if (setDataCounts == 0)
                        onGetMessagesListener.onGetMessagesFailure("Unable to get message: ")
                    else {
                        FirebaseDatabase.getInstance().reference.child(Constant.ARG_CHAT_ROOMS)
                            .child(room_type_1).child(Constant.FIREBASE_MESSAGES)
                            .addChildEventListener(object : ChildEventListener {
                                override fun onCancelled(p0: DatabaseError) {

                                }

                                override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                                }

                                override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                                }

                                override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {

                                    Log.e("Key 1 ", dataSnapshot.children.toString())

                                    val fcmChat = dataSnapshot.getValue(Message::class.java)
                                    datas.add(fcmChat!!)
                                    if (lastTimeZone != 0L) {
                                        if (lastTimeZone < fcmChat.timeStamp) {
                                            lastTimeZone = fcmChat.timeStamp
                                            onGetMessagesListener.onGetMessagesSuccess(fcmChat)
                                        }
                                    } else {
                                        if (datas.size == setDataCounts) {
                                            if (datas.size > 0) {
                                                lastTimeZone = datas.get(datas.size - 1).timeStamp
                                                onGetMessagesListener.onGetAllMessagesSuccess(datas)
                                            }
                                        }
                                    }
                                }

                                override fun onChildRemoved(p0: DataSnapshot) {
                                }

                            })
                    }
                } else if (dataSnapshot.hasChild(room_type_2)) {
                    updateCount(
                        databaseReference.child(Constant.ARG_CHAT_ROOMS).child(room_type_2).child(Constant.FIREBASE_UNREADCOUNT).ref,
                        senderUid
                    )
                    setDataCounts =
                        dataSnapshot.child(room_type_2).child(Constant.FIREBASE_MESSAGES).childrenCount.toInt()

                    if (setDataCounts == 0)
                        onGetMessagesListener.onGetMessagesFailure("Unable to get message: ")
                    else {
                        FirebaseDatabase.getInstance().reference.child(Constant.ARG_CHAT_ROOMS)
                            .child(room_type_2).child(Constant.FIREBASE_MESSAGES)
                            .addChildEventListener(object : ChildEventListener {
                                override fun onCancelled(p0: DatabaseError) {

                                }

                                override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                                }

                                override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                                }

                                override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {

                                    val fcmChat = dataSnapshot.getValue(Message::class.java)
                                    datas.add(fcmChat!!)
                                    if (lastTimeZone != 0L) {
                                        if (lastTimeZone < fcmChat.timeStamp) {
                                            lastTimeZone = fcmChat.timeStamp
                                            onGetMessagesListener.onGetMessagesSuccess(fcmChat)
                                        }
                                    } else {
                                        if (datas.size == setDataCounts) {
                                            if (datas.size > 0) {
                                                lastTimeZone = datas.get(datas.size - 1).timeStamp
                                                onGetMessagesListener.onGetAllMessagesSuccess(datas)
                                            }
                                        }
                                    }
                                }

                                override fun onChildRemoved(p0: DataSnapshot) {

                                }


                            })
                    }
                } else {
                    Log.e(TAG, "getMessageFromFirebaseUser: no such room available")
                    onGetMessagesListener.onGetMessagesFailure("Unable to get message: ")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.message)
            }
        })
    }


    private fun updateData(
        reference: DatabaseReference,
        keyValues: String
    ) {
        try {
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val data = dataSnapshot.value as HashMap<String, Object>?
                    if (data != null) {
                        val currentCounts = data.get(keyValues) as Long
                        dataSnapshot.ref.child(keyValues).setValue(currentCounts + 1)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("FcmUser", databaseError.message)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun updateCount(
        reference: DatabaseReference,
        keyValues: String
    ) {
        try {
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.e("TAG unread count ", keyValues)
                    dataSnapshot.ref.child(keyValues).setValue(0)

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("FcmUser", databaseError.message)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}

