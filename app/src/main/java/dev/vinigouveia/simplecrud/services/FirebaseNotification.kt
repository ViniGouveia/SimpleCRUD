package dev.vinigouveia.simplecrud.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class FirebaseNotification: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("Refreshed Token", token)
    }

}
