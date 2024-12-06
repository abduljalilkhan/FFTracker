package com.khan.fftracker.tracker.customAlertDialogue

import android.app.Activity
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.khan.fftracker.Item_Interface.CommonStuffInterface
import com.khan.fftracker.R
import com.khan.fftracker.databinding.AllowLocTrackerBinding
import com.khan.fftracker.databinding.BatteryOptimizeDialogueBinding
import com.khan.fftracker.databinding.DialogthreebtnBinding

class AlertDialogueTracker(private val activity: Activity) {

    private var isDismiss = true

    fun dialogueBatteryOptimize(commonStuffInterface: CommonStuffInterface) {

        var binding:BatteryOptimizeDialogueBinding?=null

        //   AlertDialog alertDialog = new AlertDialog.Builder(FragmentActivity, R.style.AppCompatAlertDialogStyle).create();
       // val alertDialog = AlertDialog.Builder(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen).create()
        val alertDialog = AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle).create()
        /////////////// Get layout for custom dialogue
        val inflater = activity.layoutInflater
        binding=BatteryOptimizeDialogueBinding.inflate(inflater)
        val view=binding.root
       // val view: View = inflater.inflate(R.layout.battery_optimize_dialogue, null)
        ///set custom view to alertDialogue
        alertDialog.setView(view)


        binding.btnAllow.setOnClickListener {
            isDismiss=false
            alertDialog.dismiss()
            commonStuffInterface.commonStuffListener("allowBatterOptimize")

        }
        alertDialog.setOnDismissListener {
            if (isDismiss) {
                commonStuffInterface.commonStuffListener("cancelAllowBatterOptimize")
            }
            }
        alertDialog.window!!.attributes.windowAnimations = R.style.CustomAnimations_slide
        // dialogueEmail.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.window!!.setBackgroundDrawableResource(R.drawable.round_white_border_white_bg)

        alertDialog.show()
    }

    fun dialoguePermissionSetting(commonStuffInterface: CommonStuffInterface) {

        var binding:AllowLocTrackerBinding?=null

        //   AlertDialog alertDialog = new AlertDialog.Builder(FragmentActivity, R.style.AppCompatAlertDialogStyle).create();
        // val alertDialog = AlertDialog.Builder(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen).create()
        val alertDialog = AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle).create()
        /////////////// Get layout for custom dialogue
        val inflater = activity.layoutInflater
        binding=AllowLocTrackerBinding.inflate(inflater)
        val view=binding.root
        // val view: View = inflater.inflate(R.layout.battery_optimize_dialogue, null)
        ///set custom view to alertDialogue
        alertDialog.setView(view)


        binding.btnOpenSettings.setOnClickListener {
            alertDialog.dismiss()
            commonStuffInterface.commonStuffListener("accessFine")

        }
        binding.imgBtnCancel.setOnClickListener{
            alertDialog.dismiss()
        }

        alertDialog.window!!.attributes.windowAnimations = R.style.CustomAnimations_slide
        // dialogueEmail.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.window!!.setBackgroundDrawableResource(R.drawable.round_white_border_white_bg)

        alertDialog.show()
    }


    //dialog with three vertical button
    fun dialogThreeButton(
        list: List<String>,
        commonStuffInterface: CommonStuffInterface) {

        var binding:DialogthreebtnBinding?=null

        val alertDialog = AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle).create()
        /////////////// Get layout for custom dialogue
        val inflater = activity.layoutInflater
        binding=DialogthreebtnBinding.inflate(inflater)
        val view=binding.root
        ///set custom view to alertDialogue
        alertDialog.setView(view)

        binding.tvTitle.text=list[0]
        binding.tvDesc.text=list[1]
        binding.btnDone.text=list[2]
        binding.btnOk.text=list[3]
        binding.btnNo.text=list[4]


        binding.btnDone.setOnClickListener {
            alertDialog.dismiss()
            commonStuffInterface.commonStuffListener("btnDoneDialog")

        }
        binding.btnOk.setOnClickListener{
            alertDialog.dismiss()
            commonStuffInterface.commonStuffListener("btnOkDialog")
        }
        binding.btnNo.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.window!!.attributes.windowAnimations = R.style.CustomAnimations_slide
        // dialogueEmail.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.window!!.setBackgroundDrawableResource(R.drawable.round_white_border_white_bg)

        alertDialog.show()
    }

    fun showHiddenPrivacyDialogue() {
        val builder = AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)
                .setTitle("Hidden")
                .setMessage("You can't see the location. Privacy level is set to \"Hide information\"")
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, which ->
                    dialog.dismiss()
                }
        activity.window.attributes.windowAnimations = R.style.CustomAnimations_slide
        builder.show()
    }
}