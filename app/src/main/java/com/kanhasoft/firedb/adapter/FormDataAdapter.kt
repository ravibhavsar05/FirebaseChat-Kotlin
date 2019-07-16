package com.kanhasoft.firedb.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.kanhasoft.firedb.R
import com.kanhasoft.firedb.adapterListener.FormDataClickListener
import com.kanhasoft.firedb.common.Constant
import com.kanhasoft.firedb.model.UserData
import java.util.*

class FormDataAdapter(
    internal var context: Context,
    data: ArrayList<UserData>, internal var formDataClickListener: FormDataClickListener
) : RecyclerView.Adapter<FormDataAdapter.MyViewHolder>() {

    internal var data: ArrayList<UserData> = ArrayList()

    init {
        this.data = data
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_form_adapter, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val formData = data[position]
        holder.tvName.text = formData.display_name
        holder.tvEmail.text = formData.email
        holder.tvPhone.text = formData.phone
        holder.tvAddress.text = formData.address

        holder.ivEdit.setOnClickListener { formDataClickListener.onEditFormClickListener(position) }

        holder.ivDelete.setOnClickListener { formDataClickListener.onDeleteFormClickListener(position) }

        holder.linearDetail.setOnClickListener { formDataClickListener.onDetailFormClickListener(position) }

        holder.ivProfileImage.setImageResource(R.drawable.ic_placeholder)

        val referenseLcl = FirebaseStorage.getInstance().reference
        val islandRefLcl = referenseLcl.child(Constant.DATABASE_PATH_UPLOADS).child(formData.uid.toString() + ".jpg")
        val ONE_MEGABYTE = (512 * 512).toLong()
        islandRefLcl.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytesPrm ->
            val bmp = BitmapFactory.decodeByteArray(bytesPrm, 0, bytesPrm.size)
            holder.ivProfileImage.setImageBitmap(bmp)
        }.addOnFailureListener { holder.ivProfileImage.setImageResource(R.drawable.ic_placeholder) }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun filterList(filterdNames: MutableList<UserData>) {
        this.data.clear()
        this.data.addAll(filterdNames)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var tvName: TextView
        internal var tvEmail: TextView
        internal var tvPhone: TextView
        internal var tvAddress: TextView
        internal var ivEdit: AppCompatImageView
        internal var ivDelete: AppCompatImageView
        internal var linearDetail: LinearLayout
        internal var ivProfileImage: ImageView
        internal var cardView: CardView

        init {

            tvName = itemView.findViewById(R.id.tvName)
            tvEmail = itemView.findViewById(R.id.tvEmail)
            tvPhone = itemView.findViewById(R.id.tvPhone)
            tvAddress = itemView.findViewById(R.id.tvAddress)
            ivEdit = itemView.findViewById(R.id.ivEdit)
            ivDelete = itemView.findViewById(R.id.ivDelete)
            linearDetail = itemView.findViewById(R.id.linearDetail)
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage)
            cardView = itemView.findViewById(R.id.cardView)
        }
    }
}
