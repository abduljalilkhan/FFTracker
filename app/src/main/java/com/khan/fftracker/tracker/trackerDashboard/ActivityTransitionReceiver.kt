package com.khan.fftracker.tracker.trackerDashboard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.ActivityTransitionResult
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.logCalls.LogCalls_Debug.TAG
import java.text.SimpleDateFormat
import java.util.*
class ActivityTransitionReceiver  : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        LogCalls_Debug.d(TAG,"ActivityTransitionReceiver")
        if (ActivityTransitionResult.hasResult(intent)) {
            val result1 = ActivityRecognitionResult.extractResult(intent)
            val result = ActivityTransitionResult.extractResult(intent)
            val detectedActivities = result1.probableActivities as ArrayList<*>
            result?.let {
                result.transitionEvents.forEach { event ->
                    // Info about activity
                    val info =
                        "Transition: " + ActivityTransitionsUtil().toActivityString(event.activityType) +
                                " (" + ActivityTransitionsUtil().toTransitionType(event.transitionType) + ")" + "   " +
                                SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
                    // notification details
                    Toast.makeText(context,
                     "Activity Detected"+"I can see you are in ${
                                ActivityTransitionsUtil().toActivityString(event.activityType)
                            } state",Toast.LENGTH_LONG).show()

                    LogCalls_Debug.d(TAG,"Activity Detected"+"I can see you are in ${
                        ActivityTransitionsUtil().toActivityString(event.activityType)
                    } state")

                    Toast.makeText(context, info, Toast.LENGTH_LONG).show()
                LogCalls_Debug.d(TAG,info)

                }
            }
        }
    }
}