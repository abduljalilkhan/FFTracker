package com.khan.fftracker.FireBase_Config;

import static com.khan.fftracker.login_Stuffs.Login_New.TAG;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.khan.fftracker.AdminMyPCP.AdminDashBoard;
import com.khan.fftracker.AdminMyPCP.Admin_Chat.Admin_MainChat_Content;
import com.khan.fftracker.Chats_View.Chats_Content;
import com.khan.fftracker.Chats_View.Chats_List;
import com.khan.fftracker.DashBoard.TopBar_RecyclerView_Utils;
import com.khan.fftracker.DrawerStuff.Drawer_Admin;
import com.khan.fftracker.DrawerStuff.Notification_Activity;
import com.khan.fftracker.LogCalls.LogCalls_Debug;
import com.khan.fftracker.Navigation_Drawer.Drawer;
import com.khan.fftracker.Navigation_Drawer.TextViewLinkHandler;
import com.khan.fftracker.Network_Volley.IsAdmin;
import com.khan.fftracker.Notification_Specials.Notifications_Specials;
import com.khan.fftracker.Notification_Specials.Quick_Special;
import com.khan.fftracker.Prefrences.Prefs_Operation;
import com.khan.fftracker.R;
import com.khan.fftracker.Referral_Sales_Chat.Referral_Chats_Content;
import com.khan.fftracker.ScratchCard.ScratchCard;
import com.khan.fftracker.Video_Create.Job_Pending.Admin_Job_Pending;
import com.khan.fftracker.Video_Create.VideoList_MyDetail;
import com.khan.fftracker.Video_Create_Customer.Video_List_Customer_Detail;
import com.khan.fftracker.commanStuff.AppRater;
import com.khan.fftracker.login_Stuffs.Chat.ChatBasic_Information;
import com.khan.fftracker.login_Stuffs.Landing_Activity;
import com.khan.fftracker.login_Stuffs.Login_Contstant;
import com.khan.fftracker.login_Stuffs.Music_Clicked;
import com.khan.fftracker.login_Stuffs.Push_Notification;
import com.khan.fftracker.tracker.TrackerConstant;
import com.khan.fftracker.tracker.chat.ChatsContentTracker;
import com.khan.fftracker.tracker.friendsDetail.FriendDetailsTracker;
import com.khan.fftracker.tracker.trackerDashboard.dataModel.Contract;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class Show_Push_Notification {
    private Activity activity;
    private AlertDialog builder;
    private WebView webView;
    private LinearLayout layout;
    private SharedPreferences sharedPreferences;

    public Show_Push_Notification(Activity activity) {
        this.activity = activity;
        sharedPreferences = this.activity.getSharedPreferences(Login_Contstant.MY_PREFS, Context.MODE_PRIVATE);
        builder = new AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle).create();
    }

    // Show notification of push notification
    public void notificationDialogue() {
        if (sharedPreferences.getString(Push_Notification.PUSH_NOTIFY_CHAT, "").equalsIgnoreCase("4")) {
          /*  Push_user_type ==1 for admin push
            Push_user_type ==2 customer  push*/
            if (!sharedPreferences.getString(Push_Notification.PUSH_USER_TYPE, "").equalsIgnoreCase("1"))
                notificationDialogue_CodeChest();
        } else if (sharedPreferences.getString(Push_Notification.PUSH_NOTIFY_CHAT, "").equalsIgnoreCase("9")) {
            ///Push_user_type ==1 for admin push
            if (!sharedPreferences.getString(Push_Notification.PUSH_USER_TYPE, "").equalsIgnoreCase("1"))
                new AppRater().app_launched(activity);
        } else {

            notificationDialogue_ChatAnd_Other();
        }

    }

    private void notificationDialogue_CodeChest() {


        sharedPreferences.edit().putString(Push_Notification.PUSH_NOTIFY, "-00").commit();

        builder.setCanceledOnTouchOutside(false);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.push_notification_codechest, null);
        builder.setView(view);


        TextView tvMessage = (TextView) view.findViewById(R.id.tvNotifyMessage);
        ImageView imgbtn_Close = view.findViewById(R.id.imgBtn_Close);
        Button btnOpen = (Button) view.findViewById(R.id.btnScratch_Reward);

        tvMessage.setText(sharedPreferences.getString(Push_Notification.PUSH_MESSAGE, ""));

        try {

            imgbtn_Close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ///dismiss alert dialogue
                    builder.dismiss();
                }
            });

            btnOpen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ///dismiss alert dialogue
                    builder.dismiss();

                    ///Select topbar item ,select and update topbar recyclerview
                    new TopBar_RecyclerView_Utils(activity).selectTopbarItem("profile");

                    ((Drawer) activity).getFragment(new ScratchCard(), -1);

                    Log.d("json", "StrPush Url Empty");
                }
            });

            builder.getWindow().getAttributes().windowAnimations = R.style.CustomAnimations_slide;
            if (!builder.isShowing()) builder.show();
        } catch (Exception nu) {
            Log.d(TAG, "Error Push Dialogue");
        }
    }


    private void notificationDialogue_ChatAnd_Other() {
        //PUSH_NOTIFY_CHAT==21 trackerchat
        String strNotiType = sharedPreferences.getString(Push_Notification.PUSH_NOTIFY_CHAT, "");
        ///show/hide chat button, if push belong to chat then show chat button respectively open chat screen
//        if (strNotiType.equalsIgnoreCase("1") || strNotiType.equalsIgnoreCase("2")) {
//            List<Fragment> fragments = ((AppCompatActivity) activity).getSupportFragmentManager().getFragments();
//            for (Fragment f : fragments) {
//                if (f != null && f instanceof Chats_Content) {
//                    return;
//                }
//            }
//        }


        if (sharedPreferences.getString(Push_Notification.PUSH_TYPE, "").equalsIgnoreCase("xp")) {
            //play sound when clicked
            new Music_Clicked(activity).playSound(R.raw.xp_point);
        }
        ///set to -00 if user click direct from alert dialague
        //App foreground= show alert for every push
        //App background= user tap push from notification bar and PUSH_NOTIFY == 1
        sharedPreferences.edit().putString(Push_Notification.PUSH_NOTIFY, "-00").commit();

        //log.d("notification Dialogue", "notificationDialogue");
        //  builder = new AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle).create();
        //  builder.setTitle(Drawer_MyPCP.strNotifi_DialogueTitle);
        builder.setCancelable(false);
        builder.setCanceledOnTouchOutside(false);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.push_notification, null);
        builder.setView(view);


        View titleView = inflater.inflate(R.layout.dialogue_titleview, null);
        builder.setCustomTitle(titleView);
        TextView tv = (TextView) titleView.findViewById(R.id.tvDialogueTitle);
        tv.setText(sharedPreferences.getString(Push_Notification.PUSH_DIALOGUE_TITLE, ""));

        TextView tvTitle = (TextView) view.findViewById(R.id.tvNotifyTitle);
        TextView tvMessage = (TextView) view.findViewById(R.id.tvNotifyMessage);
        ImageView imgNotify = (ImageView) view.findViewById(R.id.imgNotification);
        webView = (WebView) view.findViewById(R.id.webViewPushNotification);
        layout = (LinearLayout) view.findViewById(R.id.layoutPushNotification);
        final Button btncancel = (Button) view.findViewById(R.id.btnCancel_Alert);
        Button btnOpen = (Button) view.findViewById(R.id.btnOpen_Alert);

        tvTitle.setText(sharedPreferences.getString(Push_Notification.PUSH_TITLE, ""));
        tvMessage.setText(sharedPreferences.getString(Push_Notification.PUSH_MESSAGE, ""));
        layout.setVisibility(View.VISIBLE);

        try {

            //when clicked link in textview, open open url in a browser
            tvMessage.setMovementMethod(new TextViewLinkHandler() {
                @Override
                public void onLinkClick(String url) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    activity.startActivity(browserIntent);
                }
            });


            if (sharedPreferences.getString(Push_Notification.PUSH_IMAGE, "").equals("null") || sharedPreferences.getString(Push_Notification.PUSH_IMAGE, "").equals("")) {
                imgNotify.setVisibility(View.GONE);

            } else {
                Picasso.with(activity).load(sharedPreferences.getString(Push_Notification.PUSH_IMAGE, "https://")).into(imgNotify);
            }

            ///show/hide chat button, if push belong to chat then show chat button respectively open chat screen
            if (strNotiType.equalsIgnoreCase("1") || strNotiType.equals("2") || strNotiType.equals("3") || strNotiType.equals("4") ||
                    strNotiType.equals("5") || strNotiType.equals("6") || strNotiType.equals("7") || strNotiType.equals("8") ||
                    strNotiType.equals("10") || strNotiType.equals("11") || strNotiType.equals("21") || strNotiType.equals("23") || strNotiType.equals("24") ||
                    strNotiType.equals("25")||strNotiType.equals("26")||strNotiType.equals("29")) {

                btnOpen.setVisibility(View.VISIBLE);
            }

            ///if push type is geofence then change the text and color of close button
            if (sharedPreferences.getString(Push_Notification.PUSH_TYPE, "").equalsIgnoreCase("GeoFence")) {
                btncancel.setText(R.string.view);
                btncancel.setBackgroundResource(R.drawable.round_blue_login);
            }

            // SetWebViewData(webView);

            btncancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (sharedPreferences.getString(Push_Notification.PUSH_TYPE, "").equalsIgnoreCase("GeoFence")) {
                        webView.setVisibility(View.VISIBLE);
                        sharedPreferences.edit().putString(Push_Notification.PUSH_TYPE, "NoGeoFence").commit();

                        String StrPushUrl = sharedPreferences.getString(Push_Notification.PUSH_URL, "");
                        //log.i("Dioalogue", "Dismiss");
                        ///if there is link when push is notification tap,direct to open link in browser
                        if (sharedPreferences.getBoolean(Push_Notification.PUSH_is_URL, false)) {

                            if (!StrPushUrl.startsWith("http://") && !StrPushUrl.startsWith("https://")) {
                                StrPushUrl = "http://" + StrPushUrl;
                            }
                            //log.d("Link in", "browser");
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(StrPushUrl));
                            activity.startActivity(browserIntent);
                            builder.dismiss();
                        }
                        ///else link is Html type,Open in Dialogue
                        else {
                            // builder.dismiss();
                            try {

                                if (!(StrPushUrl.isEmpty() || StrPushUrl.equals("null"))) {
                                    Log.d("json", "StrPush Url not Empty");
                                    // notificationDialogue_ChatAnd_Other();
                                    btncancel.setText(R.string.close);
                                    btncancel.setBackgroundResource(R.drawable.round_gray_rect);

                                    layout.setVisibility(View.GONE);
                                    SetWebViewData(webView);
                                }
                            } catch (NullPointerException n) {
                                layout.setVisibility(View.GONE);
                                Log.d("json", "StrPush Url Empty error" + n.getMessage());
                            }
                        }
                    } else {
                        ///dismiss alert dialogue
                        builder.dismiss();
                        Log.d("json", "StrPush Url Empty");
                    }
                }
            });

            btnOpen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ///dismiss alert dialogue
                    builder.dismiss();
                    //open chat according to chat notify type
                    open_Chat();
                    Log.d("json", "StrPush Url Empty");

                }
            });

            builder.getWindow().getAttributes().windowAnimations = R.style.CustomAnimations_slide;
            if (!builder.isShowing()) builder.show();
        } catch (Exception nu) {
            Log.d(TAG, "Error Push Dialogue");
        }
    }

    //open chat according to chat notify type
//                    noti_type=1 for chat (normal guest, two video)
//                    noti_type=2 schedule appointment
//                    noti_type=3 for referral chat
    //                   noti_type=21 for tracker chat
    private void open_Chat() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String strNotifyChat = sharedPreferences.getString(Push_Notification.PUSH_NOTIFY_CHAT, "");
        //  if (sharedPreferences.getString(Push_Notification.PUSH_TYPE, "").equalsIgnoreCase("Chat")) {
        if (!new IsAdmin(activity).isAdmin_or_Customer()) {
            ///Push_user_type ==1 for admin push
            if (sharedPreferences.getString(Push_Notification.PUSH_USER_TYPE, "").equalsIgnoreCase("1")) {
                return;
            }

            ///IsSecondary_DashBoard should be false after app is opened
            Drawer.IsSecondary_DashBoard = false;

            String strJsonDetail = sharedPreferences.getString(Push_Notification.PUSH_CHAT_DETAIL, "{}");
            try {
                JSONObject jsonObject = new JSONObject(strJsonDetail);

                TopBar_RecyclerView_Utils topBar_recyclerView_utils = new TopBar_RecyclerView_Utils(activity);

                if (strNotifyChat.equalsIgnoreCase("1") || strNotifyChat.equalsIgnoreCase("2")) {

                    editor.putString(Chats_List.THREAD_ID, jsonObject.getString("threadid"));

                    ///check whether message is normal chat or appointment
                    if (jsonObject.getString(Chats_List.APPOINT_SERVICE).equals("null")) {
                        editor.putString(Chats_List.APPOINT_SERVICE, "null");
                    } else {
                        editor.putString(Chats_List.APPOINT_SERVICE, jsonObject.getString(Chats_List.APPOINT_SERVICE));
                        editor.putString(Chats_List.APPOINT_DATE_TIME, jsonObject.getString(Chats_List.APPOINT_DATE_TIME));
                        // editor.putString(Chats_List.CHAT_TITLE, arr_Notify.get(pos).get(Chats_List.CHAT_TITLE));
                        editor.putString(Chats_List.DATE, jsonObject.getString(Chats_List.DATE));
                        editor.putString(Chats_List.UPLOAD_IMAGE, jsonObject.getString(Chats_List.UPLOAD_IMAGE));
                    }
                    editor.putString(Chats_List.CHAT_TITLE, jsonObject.getString(Chats_List.CHAT_TITLE));
                    editor.putString(Admin_MainChat_Content.JSON_PREFS, "no_json");//for data update, if user do new msgs the upload prevoius screen after back, no_json means no new message
                    editor.putInt("chatPos", 0);
                    editor.putBoolean("chatPosBoolean", true);
                    editor.commit();

                    ///Select topbar item ,select and update topbar recyclerview
                    topBar_recyclerView_utils.selectTopbarItem("Notifications");

                    ((Drawer) activity).getFragment(new Chats_Content(), -1);


                } else if (strNotifyChat.equalsIgnoreCase("3")) {
                    ///Select topbar item ,select and update topbar recyclerview
                    topBar_recyclerView_utils.selectTopbarItem("Dashboard");

                    ((Drawer) activity).getFragment(new Referral_Chats_Content(), -1);
                } else if (strNotifyChat.equalsIgnoreCase("5")) {
                    ///Select topbar item ,select and update topbar recyclerview
                    topBar_recyclerView_utils.selectTopbarItem("Notifications");

                    sharedPreferences.edit().putBoolean(Notification_Activity.Is2WAYS_VIDEOS, false).commit();
                    ((Drawer) activity).getFragment(new Video_List_Customer_Detail(), -1);

                } else if (strNotifyChat.equalsIgnoreCase("6")) {
                    ///Select topbar item ,select and update topbar recyclerview
                    topBar_recyclerView_utils.selectTopbarItem("Notifications");

                    editor.putBoolean(Notification_Activity.Is2WAYS_VIDEOS, true).commit();
                    ((Drawer) activity).getFragment(new Video_List_Customer_Detail(), -1);

                } else if (strNotifyChat.equalsIgnoreCase("8")) {

                    editor.putString(Notifications_Specials.QID, jsonObject.getString(Notifications_Specials.QID));
                    editor.putString(Notifications_Specials.TITLE, jsonObject.getString(Notifications_Specials.TITLE));
                    editor.commit();

                    ///Select topbar item ,select and update topbar recyclerview
                    topBar_recyclerView_utils.selectTopbarItem("Specials");

                    ((Drawer) activity).getFragment(new Quick_Special(), -1);

                }
                if (strNotifyChat.equalsIgnoreCase("10") || strNotifyChat.equalsIgnoreCase("11")) {
                    ///Anonymous chat push: In Case of anonymous user is logged in, Not normal contract user or admin logged in
                    setAnonymousChatPush(jsonObject);

                }
                if (strNotifyChat.equalsIgnoreCase("21")) {
                    //Tracker data
                    trackerPushData();
                    ///Select topbar item ,select and update topbar recyclerview
                    topBar_recyclerView_utils.selectTopbarItem("autoverse tracker");

                    ((Drawer) activity).getFragment(new ChatsContentTracker(), -1);

                }
                if (strNotifyChat.equalsIgnoreCase("23") || strNotifyChat.equalsIgnoreCase("24") ||
                        strNotifyChat.equalsIgnoreCase("25") || strNotifyChat.equalsIgnoreCase("26") ||
                        strNotifyChat.equalsIgnoreCase("29")) {
                    trackerPushData();
                    ///Select topbar item ,select and update topbar recyclerview
                    topBar_recyclerView_utils.selectTopbarItem("autoverse tracker");

                    ((Drawer) activity).getFragment(new FriendDetailsTracker(), -1);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            ///Push_user_type ==1 for admin push
            if (!sharedPreferences.getString(Push_Notification.PUSH_USER_TYPE, "").equalsIgnoreCase("1")) {
                return;
            }
            if (strNotifyChat.equalsIgnoreCase("1") || strNotifyChat.equalsIgnoreCase("2")) {
                ((Drawer_Admin) activity).getFragment(new Admin_MainChat_Content(), -1);
            } else if (strNotifyChat.equalsIgnoreCase("5")) {
                ////if admin is logged in then and oending job is > 1 then show alert dilaogue globally
                editor.putBoolean(Notification_Activity.Is2WAYS_VIDEOS, false).commit();
                ((Drawer_Admin) activity).getFragment(new VideoList_MyDetail(), -1);

            } else if (strNotifyChat.equalsIgnoreCase("6")) {
                ////if admin is logged in then and oending job is > 1 then show alert dilaogue globally

                editor.putBoolean(Notification_Activity.Is2WAYS_VIDEOS, true);
                editor.putBoolean(AdminDashBoard.CLAIM_PRODUCT, false).commit();
                ((Drawer_Admin) activity).getFragment(new VideoList_MyDetail(), -1);

            } else if (strNotifyChat.equalsIgnoreCase("7")) {
                ////if admin is logged in then and oending job is > 1 then show alert dilaogue globally
                ((Drawer_Admin) activity).getFragment(new Admin_Job_Pending(), -1);

            }
        }

        Log.d("json", "StrPush Url Empty");

    }

    //Tracker data
    private void trackerPushData() {
        try {
            //get push data
            String strJson = Prefs_Operation.readPrefs(Push_Notification.PUSH_JSON, "{}");
            JSONObject jsonObject = new JSONObject(strJson);
            //get contract json object
            Contract response = new Gson().fromJson(jsonObject.getJSONObject("contracts").toString(), Contract.class);
            LogCalls_Debug.d(LogCalls_Debug.TAG, response.getCustomerFName());
            //save model class in prefs
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String gson = new Gson().toJson(response);
            editor.putString(TrackerConstant.FRIEND_CONTRACT_ITEM_PREFS, gson).apply();
        } catch (Exception e) {
            LogCalls_Debug.d(LogCalls_Debug.TAG, e.getMessage());
        }
    }

    ///Anonymous chat push: In Case of anonymous user is logged in, Not normal contract user or admin logged in
    private void setAnonymousChatPush(JSONObject jsonObject) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Chats_List.THREAD_ID, jsonObject.getString("threadid"));

            ///check whether message is normal chat or appointment
            if (jsonObject.getString(Chats_List.APPOINT_SERVICE).equals("null")) {
                editor.putString(Chats_List.APPOINT_SERVICE, "null");
            } else {
                editor.putString(Chats_List.APPOINT_SERVICE, jsonObject.getString(Chats_List.APPOINT_SERVICE));
                editor.putString(Chats_List.APPOINT_DATE_TIME, jsonObject.getString(Chats_List.APPOINT_DATE_TIME));
                // editor.putString(Chats_List.CHAT_TITLE, arr_Notify.get(pos).get(Chats_List.CHAT_TITLE));
                editor.putString(Chats_List.DATE, jsonObject.getString(Chats_List.DATE));
                editor.putString(Chats_List.UPLOAD_IMAGE, jsonObject.getString(Chats_List.UPLOAD_IMAGE));
            }
            editor.putString(Chats_List.CHAT_TITLE, jsonObject.getString(Chats_List.CHAT_TITLE));
            editor.putString(Admin_MainChat_Content.JSON_PREFS, "no_json");//for data update, if user do new msgs the upload prevoius screen after back, no_json means no new message
            editor.putInt("chatPos", 0);
            editor.putBoolean("chatPosBoolean", true);

            ///ANONYMOUS_PUSH_CHAT== for navigating direct to chat main screen
            editor.putBoolean(ChatBasic_Information.ANONYMOUS_PUSH_CHAT, true);

            editor.commit();

            ///anonymous chat profile screen
            Intent intent = new Intent(activity, ChatBasic_Information.class);
            //pass json object to next screen
            intent.putExtra(Landing_Activity.strJson, sharedPreferences.getString(Login_Contstant.JSON_LOGIN, "{}"));

            //make transition whiel navigation in activities
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());

            } else {
                activity.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void SetWebViewData(WebView webView) {
        Log.d(TAG, "webiview push");

        layout.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        try {

            //log.d("HTML Link in", "Webview");
            String text = null;
            try {
                // Receiving side
                Log.d(TAG, "SetWebViewData: " + sharedPreferences.getString(Push_Notification.PUSH_URL, "nodata"));
                byte[] data = Base64.decode(sharedPreferences.getString(Push_Notification.PUSH_URL, "nodata"), Base64.DEFAULT);

                text = new String(data, "UTF-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);


            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    //log.i("webview", "Processing webview url click...");
                    Log.d(TAG, "url: " + "prcessing webview");
                    view.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    Log.d(TAG, "on page: " + "finished");
                    // progressBarWebview.setVisibility(View.GONE);
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    Log.d(TAG, "Error: " + error);
                }
            });
            webView.loadData(text, "text/html", "utf-8");
        } catch (Exception e) {
            layout.setVisibility(View.GONE);
            e.printStackTrace();
            Log.d(TAG, "SetWebViewData: error " + e.getMessage());
        }
    }

}
//                        if (sharedPreferences.getString(Push_Notification.PUSH_TYPE, "").equalsIgnoreCase("Chat")) {
//                            Log.d("json", "StrPush Url Empty");
//                            Drawer_RecycleAdaptor.pos = 4;
//                            //notified recylcerview
//                            ((Drawer) activity).drawer_recycleAdaptor.notifyDataSetChanged();
//                            ((Drawer) activity).getFragment(new Chats_List(), -1);
//                        }
//                        ////if admin is logged in then and oending job is > 1 then show alert dilaogue globally
//                        else if (sharedPreferences.getString(Push_Notification.PUSH_TYPE, "").equalsIgnoreCase("Service Video")) {
//                            if (new IsAdmin(activity).isAdmin_or_Customer()) {
//                                ((Drawer_Admin) activity).getFragment(new Admin_Job_Pending(), -1);
//                            }
//                        }