package com.kanhasoft.firedb.fcm

import android.util.Log
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class FcmNotificationBuilder private constructor() {

    private var mTitle: String? = null
    private var mMessage: String? = null
    private var mUsername: String? = null
    private var mUid: String? = null
    private var mFirebaseToken: String? = null
    private var mReceiverFirebaseToken: String? = null


    fun title(title: String): FcmNotificationBuilder {
        mTitle = title
        return this
    }

    fun message(message: String): FcmNotificationBuilder {
        mMessage = message
        return this
    }

    fun username(username: String): FcmNotificationBuilder {
        mUsername = username
        return this
    }

    fun uid(uid: String): FcmNotificationBuilder {
        mUid = uid
        return this
    }

    fun firebaseToken(firebaseToken: String): FcmNotificationBuilder {
        mFirebaseToken = firebaseToken
        return this
    }

    fun receiverFirebaseToken(receiverFirebaseToken: String): FcmNotificationBuilder {
        mReceiverFirebaseToken = receiverFirebaseToken
        return this
    }

    fun send() {
        var requestBody: RequestBody? = null
        try {
            requestBody = RequestBody.create(MEDIA_TYPE_JSON, getValidJsonBody().toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val request = Request.Builder()
            .addHeader(CONTENT_TYPE, APPLICATION_JSON)
            .addHeader(AUTHORIZATION, AUTH_KEY)
            .url(FCM_URL)
            .post(requestBody)
            .build()

        val call = OkHttpClient().newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call?, e: IOException?) {
            }

            override fun onResponse(call: okhttp3.Call?, response: Response?) {

            }

        })
    }

    companion object {
        val MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8")
        private val TAG = "FcmNotificationBuilder"
        private val SERVER_API_KEY =
            "AAAA8cNu0uo:APA91bEhbps0DxHnQBIW-kW7tEXYGm90Nx-tTduUK2KYZTsT9jnKmpWwDQmhMrzszsPZ4LV53eVJV9e9eGKvJi_TgTDnvo6dHvQX5BF0nOr7xNCfVU3zJ9MjSGR834lZAZ7cRShEz8Rh"
        private val CONTENT_TYPE = "Content-Type"
        private val APPLICATION_JSON = "application/json"
        private val AUTHORIZATION = "Authorization"
        private val AUTH_KEY = "key=$SERVER_API_KEY"
        private val FCM_URL = "https://fcm.googleapis.com/fcm/send"
        // json related keys
        private val KEY_TO = "to"
        private val KEY_NOTIFICATION = "notification"
        private val KEY_TITLE = "title"
        private val KEY_TEXT = "text"
        private val KEY_DATA = "data"
        private val KEY_USERNAME = "username"
        private val KEY_UID = "uid"
        private val KEY_FCM_TOKEN = "fcm_token"

        fun initialize(): FcmNotificationBuilder {
            return FcmNotificationBuilder()
        }
    }

    private fun getValidJsonBody(): JSONObject {
        val jsonObjectBody = JSONObject()
        jsonObjectBody.put(KEY_TO, mReceiverFirebaseToken)

        val jsonObjectData = JSONObject()
        jsonObjectData.put(KEY_TITLE, mTitle)
        jsonObjectData.put(KEY_TEXT, mMessage)
        jsonObjectData.put(KEY_USERNAME, mUsername)
        jsonObjectData.put(KEY_UID, mUid)
        jsonObjectData.put(KEY_FCM_TOKEN, mFirebaseToken)
        jsonObjectBody.put(KEY_DATA, jsonObjectData)
        Log.e("TAG ", "push notification json " + jsonObjectBody.toString())

        return jsonObjectBody
    }
}
