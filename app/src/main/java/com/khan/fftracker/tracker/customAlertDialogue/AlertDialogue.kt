package com.khan.fftracker.tracker.customAlertDialogue

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.khan.fftracker.Adaptor_MYPCP.Horizontal_RecycleAdaptor
import com.khan.fftracker.Adaptor_MYPCP.Xp_CountHistory_Adaptor
import com.khan.fftracker.Autoverse.OBD.Model.OBD_Response.Dtccode
import com.khan.fftracker.Autoverse.Recall.Model.Recall_Response.Recall
import com.khan.fftracker.BuildConfig
import com.khan.fftracker.Chats_View.Add_New_Chat
import com.khan.fftracker.DashBoard.DashBoard_New
import com.khan.fftracker.DashBoard.Dashboard_Constants
import com.khan.fftracker.DrawerStuff.Drawer_Admin
import com.khan.fftracker.DrawerStuff.ShowHide_Drawer_Views
import com.khan.fftracker.GoogleWallet.CheckoutActivity
import com.khan.fftracker.Item_Interface.CommonStuffInterface
import com.khan.fftracker.Navigation_Drawer.Check_EditText
import com.khan.fftracker.Navigation_Drawer.Drawer
import com.khan.fftracker.Navigation_Drawer.Open_External_URl
import com.khan.fftracker.Navigation_Drawer.Phone_Email
import com.khan.fftracker.Network_Volley.IsAdmin
import com.khan.fftracker.Network_Volley.Network_Stuffs
import com.khan.fftracker.Prefrences.Prefs_Operation
import com.khan.fftracker.R
import com.khan.fftracker.commanStuff.Comma_Separated_String
import com.khan.fftracker.commanStuff.Terms_Policy
import com.khan.fftracker.login_Stuffs.Login_Contstant
import com.khan.fftracker.login_Stuffs.Music_Clicked
import com.ortiz.touchview.TouchImageView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import org.json.JSONObject
import java.util.Locale

/**
 * Created by Abdul Jalil Khan on 5/4/2017.
 */
class AlertDialogue(private var context: Activity) : View.OnClickListener {
    private val sharedPreferences: SharedPreferences
    var alertDialog: AlertDialog? = null

    /// IsAdmin: Class for using check whether customer or admin logged in
    var isAdmin: IsAdmin

    init {
        sharedPreferences = context.getSharedPreferences(Login_Contstant.MY_PREFS, Context.MODE_PRIVATE)
        /// Initialize class
        isAdmin = IsAdmin(context)
    }

    fun dialogueAbout(FragmentActivity: Activity) {
        val alertDialog = AlertDialog.Builder(FragmentActivity).create()
        // alertDialog.setCanceledOnTouchOutside(false);
        /////////////// Get layout for custom dialogue
        val inflater = FragmentActivity.layoutInflater
        val view = inflater.inflate(R.layout.about_dialogue, null)
        alertDialog.setView(view)

        // LayoutInflater inflater = getActivity().getLayoutInflater();
//        View titleView = inflater.inflate(R.layout.dialogue_titleview, null);
//        alertDialog.setCustomTitle(titleView);
//        TextView tv = (TextView) titleView.findViewById(R.id.tvDialogueTitle);
//        tv.setText("About");
        FirebaseApp.initializeApp(context)
        val tvDeviceID = view.findViewById<View>(R.id.tvDeviceID) as TextView
        val tvAboutDesc = view.findViewById<View>(R.id.tvAboutDesc) as TextView
        val tvDate = view.findViewById<View>(R.id.tvDialogueDate) as TextView
        val tvVersion = view.findViewById<View>(R.id.tvVErsionDialogue) as TextView
        val tvAppStatus = view.findViewById<View>(R.id.tvAbout_AppStatus) as TextView
        val tvContractNo = view.findViewById<View>(R.id.tvAboutContract) as TextView
        val tvReference = view.findViewById<View>(R.id.tvReferenceDialogue) as TextView
        val imgDealerShip = view.findViewById<View>(R.id.imgDialogueDealership) as ImageView
        val imgLogo = view.findViewById<View>(R.id.imgDialogue) as CircleImageView
        Picasso.with(FragmentActivity).load(sharedPreferences.getString(Dashboard_Constants.ABOUT_TOP_LOGO, "https//:"))
                .placeholder(R.color.lightgray3)
                .into(imgLogo)
        Picasso.with(FragmentActivity).load(sharedPreferences.getString(Dashboard_Constants.DEALERSHIP_LOGO, "https//:"))
                .into(imgDealerShip)
        tvContractNo.text = sharedPreferences.getString(Login_Contstant.USER_KEY, "")
        tvDate.text = "05/10/2023"
        //   tvVersion.setText(BuildConfig.VERSION_NAME + "." + Network_Stuffs.URL_VERSION );
        tvVersion.text = BuildConfig.VERSION_NAME
        tvReference.text = "10.17." + Network_Stuffs.URL_VERSION
        tvDeviceID.text = "" + FirebaseInstanceId.getInstance().token

        //show detail is about dialogue
        tvAboutDesc.text = sharedPreferences.getString(Dashboard_Constants.ABOUT_DETAIL, "")
        val imgYoutube = view.findViewById<View>(R.id.imgYoutube) as ImageButton
        val imgFaceBook = view.findViewById<View>(R.id.imgFacebook) as ImageButton
        val imgInstagram = view.findViewById<View>(R.id.imgInstagram) as ImageButton
        Glide.with(context).load(Prefs_Operation.readPrefs(Dashboard_Constants.YOUTUBE_IMAGE, "")).placeholder(R.drawable.youtube).into(imgYoutube)
        Glide.with(context).load(Prefs_Operation.readPrefs(Dashboard_Constants.FACEBOOK_IMAGE, "")).placeholder(R.drawable.fb).into(imgFaceBook)
        Glide.with(context).load(Prefs_Operation.readPrefs(Dashboard_Constants.INSTA_GRAM_IMAGE, "")).placeholder(R.drawable.instagram_login).into(imgInstagram)
        imgFaceBook.setOnClickListener(this)
        imgYoutube.setOnClickListener(this)
        imgInstagram.setOnClickListener(this)
        val imgArr = arrayOf(imgYoutube, imgFaceBook, imgInstagram)
        val strArr = arrayOf(sharedPreferences.getString(Dashboard_Constants.YOUTUBE_LINK, ""),
                sharedPreferences.getString(Dashboard_Constants.FACEBOOK_LINK, ""),
                sharedPreferences.getString(Dashboard_Constants.INSTAGRAM_LINK, ""))

        //show/hide social media icons
        ShowHide_Drawer_Views(context).setSocial_linksShowhide(imgArr, strArr)
        if (!Network_Stuffs.LOGIN_URL.contains("final")) {
            tvAppStatus.visibility = View.VISIBLE
            tvAppStatus.text = "Testing"
        }
        imgLogo.setOnLongClickListener {
            tvDeviceID.visibility = View.VISIBLE
            (context as Drawer).getFragment(CheckoutActivity(), -1)
            false
        }
        alertDialog.setOnCancelListener {
            if (IsAdmin(context).isAdmin_or_Customer) {
                (FragmentActivity as Drawer_Admin).navigationView.menu.getItem(0).isChecked = true
            } else {
                Drawer.navigationView.menu.getItem(0).isChecked = true
            }
        }
        alertDialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        alertDialog.window!!.attributes.windowAnimations = R.style.CustomAnimations_slide
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    override fun onClick(v: View) {
        val external_uRl = Open_External_URl(context)
        when (v.id) {
            R.id.imgYoutube -> external_uRl.openLinkIn_Browser(sharedPreferences.getString(Dashboard_Constants.YOUTUBE_LINK, ""),
                    Prefs_Operation.readPrefs(Dashboard_Constants.ISYOUTUBEEXTERNALURL, "0"))

            R.id.imgInstagram -> external_uRl.openLinkIn_Browser(sharedPreferences.getString(Dashboard_Constants.INSTAGRAM_LINK, ""),
                    Prefs_Operation.readPrefs(Dashboard_Constants.ISINSTAGRAMEXTERNALURL, "0"))

            R.id.imgFacebook -> external_uRl.openLinkIn_Browser(sharedPreferences.getString(Dashboard_Constants.FACEBOOK_LINK, ""),
                    Prefs_Operation.readPrefs(Dashboard_Constants.ISFACEBOOKEXTERNALURL, "0"))
        }
    }

    fun showAlert() {
        val builder = AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle) // .setTitle("Login")
                .setMessage("Should be logged in to utilize this feature.")
                .setPositiveButton(android.R.string.yes) { dialog, which -> intentDrawer() }
                .setNegativeButton("Close") { dialog, which -> dialog.dismiss() }
        // .setIcon(R.drawable.logo);
        val inflater = context.layoutInflater
        val titleView = inflater.inflate(R.layout.dialogue_titleview, null)
        builder.setCustomTitle(titleView)
        val tv = titleView.findViewById<View>(R.id.tvDialogueTitle) as TextView
        tv.text = "Guest"
        builder.setNegativeButton("Close") { dialogInterface, i -> dialogInterface.dismiss() }
        context.window.attributes.windowAnimations = R.style.CustomAnimations_slide
        builder.show()
    }

    fun showQrScannedAlert() {
        val builder = AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
                .setTitle("QR Code Scan!")
                .setMessage("QR Code successfully scanned. Your details will load on dealership page.")
                .setNegativeButton("Close") { dialog, which ->
                    dialog.dismiss()
                    (context as Drawer).getFragment(DashBoard_New(), -2)
                }
        context.window.attributes.windowAnimations = R.style.CustomAnimations_slide
        builder.show()
    }

    fun dialogueGiftedServices(FragmentActivity: Activity) {
        val alertDialog = AlertDialog.Builder(FragmentActivity, R.style.AppCompatAlertDialogStyle).create()
        // alertDialog.setCanceledOnTouchOutside(false);
        /////////////// Get layout for custom dialogue
        val inflater = FragmentActivity.layoutInflater
        val view = inflater.inflate(R.layout.xp_dialogue, null)
        alertDialog.setView(view)
        pbGitfedDialogue = view.findViewById<View>(R.id.pbxp_dialogue) as ProgressBar
        listXp = view.findViewById<View>(R.id.lvxpDialogue) as ListView
        val tvxpCount = view.findViewById<View>(R.id.tvCpCountDialogue) as TextView
        tvNoXP = view.findViewById<View>(R.id.tvNoXpPoint) as TextView
        tvxpCount.text = sharedPreferences.getString(Dashboard_Constants.XP_COUNT, "")
        // alertDialog.setTitle("Xp Points");
        //  alertDialog.setIcon(R.drawable.ico_xp__drawer);
        // setDialogueListView();
        alertDialog.window!!.attributes.windowAnimations = R.style.CustomAnimations_slide
        alertDialog.show()
    }

    private fun intentDrawer() {
        val intent1 = Intent(context, Login_Contstant::class.java)
        intent1.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent1)
    }

    //////////////////////////// set data to xp listview adaptor, show xp title and xp count and details
    fun setDialogueListView(jsonObject: JSONObject) {
        val listXPDialogue = ArrayList<HashMap<String, String>>()
        try {
            val jsonArray = jsonObject.getJSONArray("xppointlist")
            for (a in 0 until jsonArray.length()) {
                val jsonItem = jsonArray.getJSONObject(a)
                val map = HashMap<String, String>()
                map[XpName] = jsonItem.getString(XpName)
                map[XpCount] = jsonItem.getString(XpCount)
                listXPDialogue.add(map)
            }
            val xpCountHistroyAdaptor = Xp_CountHistory_Adaptor(context, listXPDialogue)
            if (xpCountHistroyAdaptor.count != 0) {
                listXp!!.adapter = xpCountHistroyAdaptor
            } else {
                tvNoXP!!.text = jsonObject.getString("message")
                tvNoXP!!.visibility = View.VISIBLE
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun dialogueGlovie_Image(FragmentActivity: Activity, editOrNot: Boolean, uploadImages: Boolean, s: String, g: String) {
        var strTitle = "Glovie"
        //   AlertDialog alertDialog = new AlertDialog.Builder(FragmentActivity, R.style.AppCompatAlertDialogStyle).create();
        val alertDialog = AlertDialog.Builder(FragmentActivity, android.R.style.Theme_Black_NoTitleBar_Fullscreen).create()

        // alertDialog.setCanceledOnTouchOutside(false);
        /////////////// Get layout for custom dialogue

        // retrieve display dimensions
        val displayRectangle = Rect()
        val window = FragmentActivity.window
        window.decorView.getWindowVisibleDisplayFrame(displayRectangle)
        val inflater = FragmentActivity.layoutInflater
        val view = inflater.inflate(R.layout.gloive_image_dialogue, null)
        view.minimumWidth = (displayRectangle.width() * 0.9f).toInt()
        view.minimumHeight = (displayRectangle.height() * 0.9f).toInt()
        alertDialog.setView(view)
        // LayoutInflater inflater = getActivity().getLayoutInflater();
        val titleView = inflater.inflate(R.layout.dialogue_titleview, null)
        alertDialog.setCustomTitle(titleView)
        val tv = titleView.findViewById<View>(R.id.tvDialogueTitle) as TextView
        val imgLogo = titleView.findViewById<View>(R.id.imgDialogue) as ImageView
        imgLogo.visibility = View.GONE
        val img = view.findViewById<View>(R.id.imgGlovie_Adaptor) as TouchImageView
        val imgLogoNZ = view.findViewById<View>(R.id.imgGlovie_AdaptorNZ) as ImageView
        if (g == "c") {
            strTitle = "Chat"
            if (s.endsWith(".gif")) {
                val target: SimpleTarget<GifDrawable?> = object : SimpleTarget<GifDrawable?>() {
                    override fun onResourceReady(resource: GifDrawable, transition: Transition<in GifDrawable?>?) {
                        resource.start()
                        img.setImageDrawable(resource)
                        img.setZoom(1f)
                    }
                }
                Glide.with(view.context).asGif().load(s).into(target)
            } else {
                Picasso.with(context).load(s).placeholder(R.drawable.placeholder_glovie).into(img)
            }
        } else if (g.equals("nz", ignoreCase = true)) {
            img.visibility = View.GONE
            imgLogoNZ.visibility = View.VISIBLE
            ///Glide lib is used for both normal and gif image
            Glide.with(context).load(s).into(imgLogoNZ)
        } else {
            strTitle = "Glovie"
            if (s.startsWith("http")) {
                Picasso.with(context).load(s).placeholder(R.drawable.placeholder_glovie).into(img)
            } else {
                bitmap = Horizontal_RecycleAdaptor.decodeFile(s)
                img.setImageBitmap(rotateImage(s, bitmap))
            }
        }


        // tv.setText(strTitle);
        tv.text = ""
        alertDialog.window!!.attributes.windowAnimations = R.style.CustomAnimations_slide
        alertDialog.show()

        // (That new View is just there to have something inside the dialog that can grow big enough to cover the whole screen.)

//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(alertDialog.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//        alertDialog.show();
//        alertDialog.getWindow().setAttributes(lp);
    }

    fun rotateImage(path: String?, bitmap: Bitmap?): Bitmap? {
        var bitmap = bitmap
        try {
            val ei = ExifInterface(path!!)
            val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL)
            val matrix = Matrix()
            bitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> {
                    matrix.postRotate(90f)
                    Bitmap.createBitmap(bitmap!!, 0, 0,
                            bitmap.width, bitmap.height,
                            matrix, true)
                }

                ExifInterface.ORIENTATION_ROTATE_180 -> {
                    matrix.postRotate(180f)
                    Bitmap.createBitmap(bitmap!!, 0, 0,
                            bitmap.width, bitmap.height,
                            matrix, true)
                }

                ExifInterface.ORIENTATION_ROTATE_270 -> {
                    matrix.postRotate(270f)
                    Bitmap.createBitmap(bitmap!!, 0, 0,
                            bitmap.width, bitmap.height,
                            matrix, true)
                }

                else -> Bitmap.createBitmap(bitmap!!, 0, 0,
                        bitmap.width, bitmap.height,
                        matrix, true)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return bitmap
    }

    fun showImage(FragmentActivity: Activity, bitmap: Bitmap?) {
        val alertDialog = AlertDialog.Builder(FragmentActivity, android.R.style.Theme_Black_NoTitleBar_Fullscreen).create()

        /////////////// Get layout for custom dialogue

        // retrieve display dimensions
        val displayRectangle = Rect()
        val window = FragmentActivity.window
        window.decorView.getWindowVisibleDisplayFrame(displayRectangle)
        val inflater = FragmentActivity.layoutInflater
        val view = inflater.inflate(R.layout.gloive_image_dialogue, null)
        view.minimumWidth = (displayRectangle.width() * 0.9f).toInt()
        view.minimumHeight = (displayRectangle.height() * 0.9f).toInt()
        alertDialog.setView(view)
        // LayoutInflater inflater = getActivity().getLayoutInflater();
        val titleView = inflater.inflate(R.layout.dialogue_titleview, null)
        alertDialog.setCustomTitle(titleView)
        val tv = titleView.findViewById<View>(R.id.tvDialogueTitle) as TextView
        val imgLogo = titleView.findViewById<View>(R.id.imgDialogue) as ImageView
        imgLogo.visibility = View.GONE
        val img = view.findViewById<View>(R.id.imgGlovie_Adaptor) as TouchImageView
        val imgLogoNZ = view.findViewById<View>(R.id.imgGlovie_AdaptorNZ) as ImageView
        if (bitmap != null) {
            img.setImageBitmap(bitmap)
        }
        // tv.setText(strTitle);
        tv.text = ""
        alertDialog.window!!.attributes.windowAnimations = R.style.CustomAnimations_slide
        alertDialog.show()
    }

    fun show_et_Dialogue(arr: Array<Any>, commanStuffInterface: CommonStuffInterface) {
        val alertDialog = AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle).create()
        val inflater = context.layoutInflater
        val view = inflater.inflate(R.layout.et_dialogue, null)
        alertDialog.setView(view)

        //custom title
        val titleView = inflater.inflate(R.layout.dialogue_titleview, null)
        alertDialog.setCustomTitle(titleView)
        val tv = titleView.findViewById<View>(R.id.tvDialogueTitle) as TextView
        val et = view.findViewById<View>(R.id.etDialogue) as EditText
        val ti = view.findViewById<View>(R.id.tiDialogue) as TextInputLayout
        val tvDetail = view.findViewById<View>(R.id.tvEtDialogue_Detail) as TextView
        val btncancel = view.findViewById<View>(R.id.btnEtDialogue_Cancel) as Button
        val btnSubmit = view.findViewById<View>(R.id.btnEtDialogue_Submit) as Button
        btncancel.text = arr[0].toString() + ""
        btnSubmit.text = arr[1].toString() + ""
        et.hint = arr[2].toString() + ""
        tv.text = arr[3].toString() + "".uppercase(Locale.getDefault())
        tvDetail.text = arr[4].toString() + ""
        btncancel.setBackgroundResource((arr[5] as Int))
        btnSubmit.setBackgroundResource((arr[6] as Int))

        //multicolor textview text
        btncancel.setOnClickListener {
            Log.d("json", "cancel cliked")
            try {
                alertDialog.dismiss()
            } catch (nu: NullPointerException) {
                nu.printStackTrace()
            }
        }
        btnSubmit.setOnClickListener(View.OnClickListener {
            Log.d("json", "btnSubmit cliked")
            try {
                if (!Check_EditText(context).checkUserame(et, ti, "Type")) {
                    return@OnClickListener
                }
                alertDialog.dismiss()
                commanStuffInterface.commonStuffListener(et.text.toString())
            } catch (nu: NullPointerException) {
                nu.printStackTrace()
            }
        })
        alertDialog.window!!.attributes.windowAnimations = R.style.CustomAnimations_slide
        alertDialog.show()
    }

    fun dilague_Delete(strTitle: String?, strDesc: String?, strOk_Btn: String?, strNo_Btn: String?, commanStuffInterface: CommonStuffInterface) {
        val alertBuilder = AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
        // alertBuilder.setTitle("Delete Glovie")
        alertBuilder.setMessage(strDesc) // .setIcon(R.drawable.ico_delete)
                .setCancelable(false)
                .setPositiveButton(strOk_Btn) { dialogInterface, i -> // Log.d("Position of arraylist", String.valueOf(position));
//play sound when clicked
                    Music_Clicked(context).playSound(R.raw.all_button_press)
                    commanStuffInterface.commonStuffListener("y")
                }
                .setNegativeButton(strNo_Btn) { dialogInterface, i -> //play sound when clicked
                    Music_Clicked(context).playSound(R.raw.all_button_press)
                    commanStuffInterface.commonStuffListener("n")
                    dialogInterface.dismiss()
                }
        val inflater = context.layoutInflater
        val titleView = inflater.inflate(R.layout.dialogue_titleview, null)
        alertBuilder.setCustomTitle(titleView)
        val tv = titleView.findViewById<View>(R.id.tvDialogueTitle) as TextView
        tv.text = strTitle
        context.window.attributes.windowAnimations = R.style.CustomAnimations_slide
        alertBuilder.show()
    }

    fun dilague_AccountDeleted(strTitle: String?, strDesc: String?, strOk_Btn: String?, commanStuffInterface: CommonStuffInterface) {
        val alertBuilder = AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
        // alertBuilder.setTitle("Delete Glovie")
        alertBuilder.setMessage(strDesc) // .setIcon(R.drawable.ico_delete)
                .setCancelable(false)
                .setPositiveButton(strOk_Btn) { dialogInterface, i -> // Log.d("Position of arraylist", String.valueOf(position));
                    //play sound when clicked
                    Music_Clicked(context).playSound(R.raw.all_button_press)
                    commanStuffInterface.commonStuffListener("d")
                }
        val inflater = context.layoutInflater
        val titleView = inflater.inflate(R.layout.dialogue_titleview, null)
        alertBuilder.setCustomTitle(titleView)
        val tv = titleView.findViewById<View>(R.id.tvDialogueTitle) as TextView
        tv.text = strTitle
        context.window.attributes.windowAnimations = R.style.CustomAnimations_slide
        alertBuilder.show()
    }

    fun show_obd_Dialogue(dtccode: Dtccode) {
        val alertDialog = AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle).create()
        val inflater = context.layoutInflater
        val view = inflater.inflate(R.layout.obd_dialogue, null)
        alertDialog.setView(view)
        val tvCode = view.findViewById<View>(R.id.tvCode) as TextView
        val tvDesc = view.findViewById<View>(R.id.tv_Desc) as TextView
        val tvEffect = view.findViewById<View>(R.id.tv_effect) as TextView
        val tvDefinition = view.findViewById<View>(R.id.tv_definition) as TextView
        val tvTech = view.findViewById<View>(R.id.tv_tech) as TextView
        tvCode.text = dtccode.code
        tvDesc.text = dtccode.urgencyDesc
        tvDefinition.text = dtccode.techDefinition
        tvEffect.text = dtccode.effectOnVehicle
        tvTech.text = dtccode.techDefinition
        val btnClose = view.findViewById<View>(R.id.btnClose) as Button
        val imgCancel = view.findViewById<View>(R.id.imgBtn_Close) as ImageView

        //multicolor textview text
        btnClose.setOnClickListener { alertDialog.dismiss() }
        imgCancel.setOnClickListener {
            Log.d("json", "btnSubmit cliked")
            alertDialog.dismiss()
        }
        alertDialog.window!!.attributes.windowAnimations = R.style.CustomAnimations_slide
        alertDialog.show()
    }

    fun show_Recall_Dialogue(recall: Recall) {
        val alertDialog = AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle).create()
        val inflater = context.layoutInflater
        val view = inflater.inflate(R.layout.recall_dialogue, null)
        alertDialog.setView(view)
        val tvdate = view.findViewById<View>(R.id.tvdate) as TextView
        val tvnumber = view.findViewById<View>(R.id.tvnumber) as TextView
        val tvcampaignNo = view.findViewById<View>(R.id.tvcampaignNo) as TextView
        val tvDesc = view.findViewById<View>(R.id.tv_Desc) as TextView
        val tv_action = view.findViewById<View>(R.id.tv_action) as TextView
        val tv_cons = view.findViewById<View>(R.id.tv_cons) as TextView
        tvdate.text = recall.recallDate
        tvDesc.text = recall.desc
        tvnumber.text = recall.recallNumber
        tvcampaignNo.text = recall.campaignNumber
        tv_action.text = recall.correctiveAction
        tv_cons.text = recall.consequence
        val btnClose = view.findViewById<View>(R.id.btnClose) as Button
        val imgCancel = view.findViewById<View>(R.id.imgBtn_Close) as ImageView

        //multicolor textview text
        btnClose.setOnClickListener { alertDialog.dismiss() }
        imgCancel.setOnClickListener {
            Log.d("json", "btnSubmit cliked")
            alertDialog.dismiss()
        }
        alertDialog.window!!.attributes.windowAnimations = R.style.CustomAnimations_slide
        alertDialog.show()
    }

    fun show_RenewPolicy_Dialogue(phone: String, email: String, link: String) {
        val alertDialog = AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle).create()
        val inflater = context.layoutInflater
        val view = inflater.inflate(R.layout.renew_policy_dialogue, null)
        alertDialog.setView(view)
        val btnCall = view.findViewById<View>(R.id.btnCall) as Button
        val btnEmail = view.findViewById<View>(R.id.btnEmail) as Button
        val btnWebsite = view.findViewById<View>(R.id.btnwebsite) as Button
        val btnClose = view.findViewById<View>(R.id.btnClose) as Button

        //check if value of call email and url-link not empty then show button on according
        if (!phone.isEmpty()) btnCall.visibility = View.VISIBLE
        if (!email.isEmpty()) btnEmail.visibility = View.VISIBLE
        if (!link.isEmpty()) btnWebsite.visibility = View.VISIBLE
        btnCall.setOnClickListener {
            alertDialog.dismiss()
            Phone_Email(context).make_Call(phone)
        }
        btnEmail.setOnClickListener {
            alertDialog.dismiss()
            Phone_Email(context).send_Email(email)
        }
        btnWebsite.setOnClickListener {
            alertDialog.dismiss()
            Open_External_URl(context).openLinkIn_Browser(link, "0")
        }
        btnClose.setOnClickListener { alertDialog.dismiss() }

        //check if all three value are empty then don't show dialogue
        if (phone.isEmpty() && email.isEmpty() && link.isEmpty()) {
        } else {
            alertDialog.window!!.attributes.windowAnimations = R.style.CustomAnimations_slide
            alertDialog.show()
        }
    }

    // Show Game Center Intro privacy and terms condtion dialogue
    fun GameCenter_intro_Dialogue(activity: Activity, stuffInterface: CommonStuffInterface) {
        val builder = AlertDialog.Builder(activity).create()
        //alertDialog.setCanceledOnTouchOutside(false);
        /////////////// Get layout for custom dialogue
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.gc_intro_dialogue, null)
        builder.setCancelable(true)
        builder.setView(view)
        val tv_Privacypolicy = view.findViewById<View>(R.id.tv_Privacypolicy) as TextView
        val cbGuestAccount = view.findViewById<View>(R.id.cbGuestAccount) as CheckBox
        val btnStarted = view.findViewById<View>(R.id.btnGetStarted) as Button
        tv_Privacypolicy.movementMethod = LinkMovementMethod.getInstance()
        val word: Spannable = SpannableString("I accept the ")
        word.setSpan(ForegroundColorSpan(activity.resources.getColor(R.color.black)), 0, word.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv_Privacypolicy.text = word
        val wordTwo: Spannable = SpannableString("Terms and Conditions")
        val myClickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // There is the OnCLick. put your intent to Register class here
                widget.invalidate()
                //  privacy_TermsCondition("terms");
                Terms_Policy(activity).GameCenter_TermsCondition("https://example/game-term-condition")
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ContextCompat.getColor(activity, R.color.deepskyblue)
                ds.isUnderlineText = false
            }
        }
        wordTwo.setSpan(myClickableSpan, 0, wordTwo.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv_Privacypolicy.append(wordTwo)
        val strSpan: Spannable = SpannableString(" and acknowledge that I have read and understood the")
        strSpan.setSpan(ForegroundColorSpan(context.resources.getColor(R.color.black)), 0, strSpan.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv_Privacypolicy.append(strSpan)
        val wordPrivacy: Spannable = SpannableString(" Privacy Policy")
        val myClickablePrivacy: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // There is the OnCLick. put your intent to Register class here
                widget.invalidate()

                // privacy_TermsCondition("privacy");
                Terms_Policy(context).privacy_TermsCondition("privacy")
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ContextCompat.getColor(context, R.color.deepskyblue)
                ds.isUnderlineText = false
            }
        }
        wordPrivacy.setSpan(myClickablePrivacy, 0, wordPrivacy.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv_Privacypolicy.append(wordPrivacy)
        btnStarted.setOnClickListener {
            if (cbGuestAccount.isChecked) {
                builder.dismiss()
                stuffInterface.commonStuffListener("yes")
            }
        }
        builder.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        builder.window!!.attributes.windowAnimations = R.style.CustomAnimations_slide
        builder.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        builder.show()
    }

    fun dialogueMileage(FragmentActivity: Activity, strMileage: String?, strContract: String, strFunction: String?,
                        comman_stuff_interface: CommonStuffInterface) {
        val alertDialog = AlertDialog.Builder(FragmentActivity).create()
        // alertDialog.setCanceledOnTouchOutside(false);
        /////////////// Get layout for custom dialogue
        val inflater = FragmentActivity.layoutInflater
        val view = inflater.inflate(R.layout.mileage_dialogue, null)
        alertDialog.setView(view)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val et_Mileage = view.findViewById<EditText>(R.id.et_Mileage)
        val btnUpdate = view.findViewById<Button>(R.id.btn_Update)
        val layoutChat = view.findViewById<LinearLayout>(R.id.layout_Chat)
        var strTitle = ""

        //if gps=1 and serial number is empty then user can edit mileage
        //if gps=1 and serial number is not empty then user can't edit mileage
        //if gps=0 then user can update mileage
        if (Prefs_Operation.readPrefs(Dashboard_Constants.ENABLE_GPS_AUTOVERSE, null) == "1" && Prefs_Operation.readPrefs(Dashboard_Constants.SERIAL, "") == "") {
            btnUpdate.visibility = View.VISIBLE
            et_Mileage.isFocusable = true
        } else if (Prefs_Operation.readPrefs(Dashboard_Constants.ENABLE_GPS_AUTOVERSE, null) == "1" &&
                Prefs_Operation.readPrefs(Dashboard_Constants.SERIAL, "") != "") {
            strTitle = "Request to update car mileage - $strContract"
            tvTitle.text = strTitle
            layoutChat.visibility = View.VISIBLE
            et_Mileage.isFocusable = false
        } else if (Prefs_Operation.readPrefs(Dashboard_Constants.ENABLE_GPS_AUTOVERSE, null) == "0") {
            btnUpdate.visibility = View.VISIBLE
            et_Mileage.isFocusable = true
        }
        et_Mileage.setText(Comma_Separated_String(context).commaSeparated_String(strMileage))
        btnUpdate.setOnClickListener {
            if (et_Mileage.text.toString().isEmpty()) {
                et_Mileage.error = Check_EditText.str_loginMsg
                et_Mileage.requestFocus()
            } else {
                alertDialog.dismiss()
                DashBoard_New.str_Mileage = et_Mileage.text.toString()
                comman_stuff_interface.commonStuffListener(strFunction)
            }
        }
        layoutChat.setOnClickListener {
            try {
                alertDialog.dismiss()
                Add_New_Chat.str_Title = "Request to update car mileage"
                Drawer.FRAGEMNT_TRANSCATION = "n" //set value n for not finish add chat fragment after resuming the add chat fragment
                (context as Drawer).navItem_Index = 1
                (context as Drawer).setNavMenu_Item()
                (context as Drawer).getFragment(Add_New_Chat(), -1) //after logged in user can chat feature
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        alertDialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        alertDialog.window!!.attributes.windowAnimations = R.style.CustomAnimations_slide
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    fun dialogue_CarImage(activity: FragmentActivity, strImage: String?, strFunction: String?, comman_stuff_interface: CommonStuffInterface) {
        context = activity
        alertDialog = AlertDialog.Builder(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen).create()

        /////////////// Get layout for custom dialogue

        // retrieve display dimensions
        val displayRectangle = Rect()
        val window = activity.window
        window.decorView.getWindowVisibleDisplayFrame(displayRectangle)
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.car_image_dialogue, null)
        view.minimumWidth = (displayRectangle.width() * 0.9f).toInt()
        view.minimumHeight = (displayRectangle.height() * 0.9f).toInt()
        alertDialog!!.setView(view)
        imgTouch = view.findViewById<View>(R.id.imgTouch) as TouchImageView
        val imgCar = view.findViewById<View>(R.id.img) as ImageView
        val imgBtn_Close = view.findViewById<View>(R.id.imgBtn_Close) as ImageView
        val btnUpload = view.findViewById<View>(R.id.btn_Update) as Button
        Picasso.with(activity).load(strImage).placeholder(R.drawable.dash_car_icon).into(imgTouch)
        //Glide.with(context).load(strImage).into(imgTouch);
        imgBtn_Close.setOnClickListener {
            bitmap = null
            alertDialog!!.dismiss()
        }
        btnUpload.setOnClickListener {
            if (bitmap != null) {
                bitmap = null
                alertDialog!!.dismiss()
                comman_stuff_interface.commonStuffListener(strFunction)
            } else {
                comman_stuff_interface.commonStuffListener("change_image")
            }
        }
        alertDialog!!.window!!.attributes.windowAnimations = R.style.CustomAnimations_slide
        alertDialog!!.show()
    }

    fun dialogue_CarUpload(bit: Bitmap?) {
        bitmap = bit
        imgTouch!!.setImageBitmap(bitmap)
    }

    companion object {
        var pbGitfedDialogue: ProgressBar? = null
        var listXp: ListView? = null
        var XpName = "Name"
        const val XpCount = "xp_point"
        private var bitmap: Bitmap? = null
        private var tvNoXP: TextView? = null
        var imgTouch: TouchImageView? = null
    }
}