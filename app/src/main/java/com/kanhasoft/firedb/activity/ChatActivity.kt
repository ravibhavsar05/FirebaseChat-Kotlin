package com.kanhasoft.firedb.activity

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.kanhasoft.firedb.FirebaseDbApplication
import com.kanhasoft.firedb.R
import com.kanhasoft.firedb.adapter.ChatMessageAdapter
import com.kanhasoft.firedb.common.Constant
import com.kanhasoft.firedb.common.LinearLayoutManagerWithSmoothScroller
import com.kanhasoft.firedb.common.SharedPrefUtil
import com.kanhasoft.firedb.common.SwipeToDismiss
import com.kanhasoft.firedb.core.chat.ChatContract
import com.kanhasoft.firedb.core.chat.ChatPresenter
import com.kanhasoft.firedb.model.chat.Message
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity(), ChatContract.View, ChatMessageAdapter.onChatMessageClickListener {

    lateinit var chatPresenter: ChatPresenter
    internal lateinit var chatMessageAdapter: ChatMessageAdapter

    var message = ArrayList<Message>()
    var receiverUid: String = ""
    var receiverFirebaseToken: String = ""
    var sharedPreferencesUtils: SharedPrefUtil? = null
    var alertDialog: LottieAlertDialog? = null
    var p = Paint()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        sharedPreferencesUtils = SharedPrefUtil(this@ChatActivity)
        chatPresenter = ChatPresenter(this)

        alertDialog = LottieAlertDialog.Builder(this@ChatActivity, DialogTypes.TYPE_LOADING)
            .setTitle(getString(R.string.loading))
            .setDescription(getString(R.string.msg_please_wait))
            .build()
        alertDialog!!.setCancelable(true)
        alertDialog!!.show()

        initData()
    }

    override fun onDeleteChatMessageDeleteClickListener(position: Int) {
        chatPresenter.deleteSingleChatMessage(
            FirebaseAuth.getInstance().currentUser!!.uid,   // sender id (logged in user id)
            receiverUid   // receiver id,
            , message.get(position).timeStamp.toString(), position
        )
    }

    private fun initData() {
        message.clear()

        if (intent.extras != null) {
            tvHeader.text = getIntent().getExtras()!!.getString(Constant.ARG_RECEIVER)
            receiverUid = getIntent().getExtras()!!.getString(Constant.ARG_RECEIVER_UID).toString()
            receiverFirebaseToken = getIntent().getExtras()!!.getString(Constant.ARG_FIREBASE_TOKEN).toString()
        }

        chatMessageAdapter = ChatMessageAdapter(this, message, this)
        recyclerChatMessage.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManagerWithSmoothScroller(applicationContext)
        recyclerChatMessage.layoutManager = mLayoutManager
        recyclerChatMessage.itemAnimator = DefaultItemAnimator() as RecyclerView.ItemAnimator?
        recyclerChatMessage.adapter = chatMessageAdapter

        recyclerChatMessage.addOnLayoutChangeListener(View.OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) {

                val lastAdapterItem = chatMessageAdapter.getItemCount() - 1
                recyclerChatMessage.post(Runnable {
                    var recyclerViewPositionOffset = -1000000
                    val bottomView = mLayoutManager.findViewByPosition(lastAdapterItem)
                    if (bottomView != null) {
                        recyclerViewPositionOffset = 0 - bottomView!!.getHeight()
                    }
                    mLayoutManager.scrollToPositionWithOffset(lastAdapterItem, recyclerViewPositionOffset)
                })
            }
        })

        // Getting list of messages
        chatPresenter.getMessage(
            FirebaseAuth.getInstance().currentUser!!.uid,   // sender id (logged in user id)
            receiverUid   // receiver id
        )

        ivBack.setOnClickListener(clickListener)
        ivSendMessage.setOnClickListener(clickListener)
        ivDeleteChat.setOnClickListener(clickListener)

        ivSendMessage.visibility = GONE
        ivDeleteChat.visibility = GONE
        etMessage?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (s!!.isEmpty())
                    ivSendMessage.visibility = GONE
                else
                    ivSendMessage.visibility = VISIBLE
            }

        })
        etMessage?.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {

                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE
                    || event!!.getAction() == KeyEvent.ACTION_DOWN
                    && event!!.getKeyCode() == KeyEvent.KEYCODE_ENTER
                ) {
                    if (etMessage.text.toString().trim().length >= 1)
                        sendMessage()
                    else
                        Toast.makeText(applicationContext, "Message can't be null", LENGTH_SHORT).show()
                    return true
                }
                return false
            }
        })

        initSwipe()
    }

    private fun getCallback(adapter: ChatMessageAdapter): SwipeToDismiss.SwipetoDismissCallBack {
        return object : SwipeToDismiss.SwipetoDismissCallBack {
            override fun onSwipedLeft(viewHolder: RecyclerView.ViewHolder) {
                adapter!!.deleteMessage(viewHolder.adapterPosition)
            }

            override fun onSwipedRight(viewHolder: RecyclerView.ViewHolder) {
                adapter!!.deleteMessage(viewHolder.adapterPosition)
            }
        }
    }

    //Swipe to delete single message
    private fun initSwipe() {
        val swipeToDismiss = SwipeToDismiss(this, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        swipeToDismiss.setLeftBackgroundColor(R.color.color_D3D3DA)
        swipeToDismiss.setRightBackgroundColor(R.color.color_D3D3DA)
        swipeToDismiss.setLeftImg(R.drawable.ic_delete_message)
        swipeToDismiss.setRightImg(R.drawable.ic_delete_message)
        swipeToDismiss.setSwipetoDismissCallBack(getCallback(chatMessageAdapter))

        val itemTouchHelper = ItemTouchHelper(swipeToDismiss)
        itemTouchHelper.attachToRecyclerView(recyclerChatMessage)
    }

    override fun onResume() {
        super.onResume()
        FirebaseDbApplication.setChatActivityOpen(true)
    }

    override fun onPause() {
        super.onPause()
        FirebaseDbApplication.setChatActivityOpen(false)
    }

    override fun onBackPressed() {
        val backStackEntryCount = supportFragmentManager.backStackEntryCount
        if (backStackEntryCount == 0) {
            val intent = Intent(this, ListActivity::class.java)
            startActivity(intent)
            finishAffinity()
        } else {
            super.onBackPressed()
        }
    }

    private val clickListener: View.OnClickListener = View.OnClickListener { view ->
        if (view.id == ivBack.id) {
            onBackPressed()
        }
        if (view.id == ivSendMessage.id) {
            sendMessage()
        }
        if (view.id == ivDeleteChat.id) {
            deleteChatMessages()
        }
    }

    private fun deleteChatMessages() {

        var alertDialog: LottieAlertDialog? = null
        alertDialog = LottieAlertDialog.Builder(this@ChatActivity, DialogTypes.TYPE_DELETE)
            .setTitle(getString(R.string.clear_chat))
            .setDescription(getString(R.string.clear_chat_message))
            .setPositiveText(getString(R.string.text_yes))
            .setPositiveListener(object : ClickListener {
                override fun onClick(dialog: LottieAlertDialog) {
                    alertDialog!!.hide()
                    this@ChatActivity.alertDialog!!.show()
                    chatPresenter.clearChat(
                        FirebaseAuth.getInstance().currentUser!!.uid,
                        getIntent().getExtras()!!.getString(Constant.ARG_RECEIVER_UID).toString()
                    )
                }
            })
            .setNegativeText(getString(R.string.text_no))
            .setNegativeListener(object : ClickListener {
                override fun onClick(dialog: LottieAlertDialog) {
                    alertDialog!!.hide()
                }

            })
            .build()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun sendMessage() {
        val message = etMessage.getText().toString()
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

        } else
            Toast.makeText(applicationContext, getString(R.string.err_empty_message), LENGTH_SHORT).show()
        ivSendMessage.visibility = GONE
    }

    override fun onGetMessagesSuccess(msg: Message) {
        alertDialog!!.hide()
        chatMessageAdapter.addMessage(msg)
        updateAdapter()
    }

    override fun onGetAllMessageSuccess(fcmChats: ArrayList<Message>) {
        alertDialog!!.hide()
        message.addAll(fcmChats)
        updateAdapter()
    }

    private fun updateAdapter() {
        alertDialog!!.hide()
        chatMessageAdapter.notifyDataSetChanged()
        if (message.size > 1)
            recyclerChatMessage.smoothScrollToPosition(chatMessageAdapter.getItemCount() - 1)
        if (message.size > 0)
            ivDeleteChat.visibility = VISIBLE
    }

    override fun onDeleteMessageSuccess(position: Int) {
        message.removeAt(position)
        updateAdapter()
    }

    override fun onDeleteMessageFailure(message: String) {
        alertDialog!!.hide()
    }

    override fun onSendMessageSuccess() {
        alertDialog!!.hide()
    }

    override fun onSendMessageFailure(message: String) {
        Toast.makeText(this@ChatActivity, message, LENGTH_SHORT).show()
        alertDialog!!.hide()
    }

    override fun onGetMessagesFailure(message: String) {
        Toast.makeText(this@ChatActivity, message, LENGTH_SHORT).show()
        alertDialog!!.hide()
    }

    override fun onClearMessageSuccess() {
        alertDialog!!.hide()
        message.clear()
        chatMessageAdapter.notifyDataSetChanged()
        ivDeleteChat.visibility = GONE
    }

    override fun onClearMessageFailure(message: String) {
        Toast.makeText(applicationContext, message, LENGTH_SHORT).show()
        alertDialog!!.hide()
    }

}
