# Firebase Chat App
This application belongs to social networking category. Using this application user
can communicate with each other.
Application includes module like register user, edit user, delete user, search user,
chatting(single).

### Requirement

- Android Studio
- Firebase Developer Account

### Usage

- User can search user, chat with user, remove chat message ( single, whole)

### Built With

* [Firebase Console](https://firebase.google.com/docs/cloud-messaging/) 
* [Glide](https://github.com/bumptech/glide/)
* [Swipe Layout](https://github.com/arman-sar/SwipeLayout/)
* [Lottie Dialog](https://github.com/airbnb/lottie-android/)

### Acknowledgments
* Hat tip to anyone whose code was used
* Inspiration
* etc


![FirebaseChat-Kotlin - Animated gif demo](app/ChatSend.gif)

![FirebaseChat-Kotlin - Animated gif demo](app/ChatReceiver.gif)


### Message List
````
 chatPresenter.getMessage(
            FirebaseAuth.getInstance().currentUser!!.uid,   // sender id (logged in user id)
            receiverUid   // receiver id
        )
 
````

### Sent Message
````
  if (message.trim().length >= 1) {
            val receiver = getIntent().getExtras()!!.getString(Constant.ARG_RECEIVER)
            val receiverUid = getIntent().getExtras()!!.getString(Constant.ARG_RECEIVER_UID)
            val sender = FirebaseAuth.getInstance().currentUser!!.email
            val senderUid = FirebaseAuth.getInstance().currentUser!!.uid

            val msg = Message(
                message,
                receiver.toString(), receiverUid.toString(),
                sender.toString(), "", senderUid,
                System.currentTimeMillis()
            )

            chatPresenter.sendMessage(this, msg, receiverFirebaseToken)
            etMessage.setText("")

        }
				
````

### Delete Message 
````

  chatPresenter.deleteSingleChatMessage(
            FirebaseAuth.getInstance().currentUser!!.uid,   // sender id (logged in user id)
            receiverUid   // receiver id,
            , message.get(position).timeStamp.toString(), position
        )	
				
````
