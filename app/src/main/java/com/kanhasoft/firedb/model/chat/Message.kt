package com.kanhasoft.firedb.model.chat

class Message {
    var message: String = ""

    var receiver: String = ""

    var receiverUid: String = ""

    var sender: String = ""

    var senderName: String = ""

    var senderUid: String = ""

    var timeStamp: Long = 0


    constructor(
        message: String,
        receiver: String,
        receiverUid: String,
        sender: String,
        senderName: String,
        senderUid: String,
        timeStamp: Long
    ) {
        this.message = message
        this.receiver = receiver
        this.receiverUid = receiverUid
        this.sender = sender
        this.senderName = senderName
        this.senderUid = senderUid
        this.timeStamp = timeStamp
    }

    constructor() {}
}