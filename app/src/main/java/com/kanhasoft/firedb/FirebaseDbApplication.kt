package com.kanhasoft.firedb

import android.app.Application
import com.kanhasoft.firedb.common.Utils

class FirebaseDbApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        Utils.createLists()
//        val refreshedToken = FirebaseInstanceId.getInstance().token
    }

    companion object {

        private var sIsChatActivityOpen = false

        fun isChatActivityOpen(): Boolean {
            return sIsChatActivityOpen
        }

        fun setChatActivityOpen(isChatActivityOpen: Boolean) {
            this.sIsChatActivityOpen = isChatActivityOpen
        }
    }
}
