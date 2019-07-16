package com.kanhasoft.firedb.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.kanhasoft.firedb.R
import com.kanhasoft.firedb.common.Constant
import com.kanhasoft.firedb.common.SharedPrefUtil
import com.kanhasoft.firedb.model.chat.Message

class ChatMessageAdapter(
    internal var context: Context,
    message: ArrayList<Message>,
    onMessageClickListener: onChatMessageClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    interface onChatMessageClickListener {
        fun onDeleteChatMessageDeleteClickListener(position: Int)
    }

    var sharedPreferencesUtils: SharedPrefUtil? = null
    internal var message: ArrayList<Message> = ArrayList()
    internal var type_sender: Int = 0
    internal var type_receiver: Int = 1
    internal var onMessageClickListener: onChatMessageClickListener

    init {
        this.message = message
        sharedPreferencesUtils = SharedPrefUtil(context)
        this.onMessageClickListener = onMessageClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            type_sender -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_sender_chat, parent, false)

                return MySenderViewHolder(itemView)
            }
            type_receiver -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_receiver_chat, parent, false)

                return MyReceiverViewHolder(itemView)
            }

            else -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_form_adapter, parent, false)

                return MySenderViewHolder(itemView)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        if (message.size > 0) {
            return if (TextUtils.equals(
                    message.get(position).senderUid,
                    FirebaseAuth.getInstance().currentUser!!.uid
                )
            ) {
                type_sender
            } else {
                type_receiver
            }
        }
        return -1

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (message.get(position).senderUid.equals(sharedPreferencesUtils!!.getString(Constant.ARG_UID))) {
            configureMyChatViewHolder(holder as MySenderViewHolder, position)
        } else {
            configureReceiverChatViewHolder(holder as MyReceiverViewHolder, position)
        }

    }

    private fun configureMyChatViewHolder(myChatViewHolder: MySenderViewHolder, position: Int) {

        myChatViewHolder.ivDelete.visibility = GONE
        myChatViewHolder.tvSenderMessage.text = message.get(position).message

        myChatViewHolder.ivDelete.setOnClickListener(View.OnClickListener {
            onMessageClickListener.onDeleteChatMessageDeleteClickListener(position)
        })
    }

    private fun configureReceiverChatViewHolder(myChatViewHolder: MyReceiverViewHolder, position: Int) {
        myChatViewHolder.tvReceiverMessage.text = message.get(position).message
    }

    override fun getItemCount(): Int {
        return message.size
    }

    fun addMessage(msg: Message) {
        message.add(msg)
        // notifyDataSetChanged()
    }

    fun clearMessage() {
        message.clear()
        notifyDataSetChanged()
    }

    fun deleteMessage(position: Int) {
        onMessageClickListener.onDeleteChatMessageDeleteClickListener(position)
    }

    fun removeItem(position: Int) {
        message.removeAt(position)
        notifyItemRangeRemoved(position, message.size)
    }

    inner class MySenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var tvSenderMessage: TextView
        internal var ivDelete: ImageView

        init {
            tvSenderMessage = itemView.findViewById(R.id.tvSenderMessage)
            ivDelete = itemView.findViewById(R.id.ivDelete)
        }
    }

    inner class MyReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var tvReceiverMessage: TextView

        init {
            tvReceiverMessage = itemView.findViewById(R.id.tvReceiverMessage)
        }
    }

}
