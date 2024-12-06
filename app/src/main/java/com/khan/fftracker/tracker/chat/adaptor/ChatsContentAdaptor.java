package com.khan.fftracker.tracker.chat.adaptor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.khan.fftracker.Alert_Dialogue.AlertDialogue;
import com.khan.fftracker.Navigation_Drawer.Get_Visible_Fragment;
import com.khan.fftracker.Navigation_Drawer.TextViewLinkHandler;
import com.khan.fftracker.R;

import com.khan.fftracker.Schedule_Appointment.Chats_MYPCP;
import com.khan.fftracker.login_Stuffs.Login_Contstant;
import com.khan.fftracker.tracker.chat.ChatsContentTracker;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by DELL on 1/20/2017.
 */
public class ChatsContentAdaptor extends BaseAdapter {
    FragmentActivity activity;
    ArrayList<HashMap<String, String>> arrayList;
    LayoutInflater inflater;
    String strSender;
    SharedPreferences sharedPreferences;
    Fragment fragment;

    public ChatsContentAdaptor(FragmentActivity context, ArrayList<HashMap<String, String>> array) {
        arrayList = array;
        activity = context;
        sharedPreferences = context.getSharedPreferences(Login_Contstant.MY_PREFS, Context.MODE_PRIVATE);

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        fragment = new Get_Visible_Fragment(activity).getVisibleFragment();
        Log.d("json adaptor", String.valueOf(fragment));

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final ViewHolder_Chat holderChat;
        View view = convertView;
        if (view == null) {

            if (getItemViewType(i) == 0) {

                view = inflater.inflate(R.layout.chats_receiver_tracker, null);
                holderChat = new ViewHolder_Chat();
                holderChat.tvReciverMsg = (TextView) view.findViewById(R.id.tvChatContent_Recvr);
                holderChat.tvChatRec_Date = (TextView) view.findViewById(R.id.tvChat_Rec_Date);
                holderChat.imgChatRecvrShow = (ImageView) view.findViewById(R.id.imgChatRecShow);

            } else {
                view = inflater.inflate(R.layout.chats_sender_tracker, null);
                holderChat = new ViewHolder_Chat();
                holderChat.tvMessage = (TextView) view.findViewById(R.id.tvChatContent);
                holderChat.tv_SenderDate = (TextView) view.findViewById(R.id.tvChatSender_Date);
                holderChat.imgChatSender_Show = (ImageView) view.findViewById(R.id.imgChat_SenderShow);
            }
            view.setTag(holderChat);
        } else {
            holderChat = (ViewHolder_Chat) view.getTag();
        }

        if (getItemViewType(i) == 0) {
            HashMap<String, String> map = arrayList.get(i);
            holderChat.tvReciverMsg.setText(map.get(ChatsContentTracker.CHAT_MESSAGE).replace("\\n","\n"));
            holderChat.tvChatRec_Date.setText(map.get(ChatsContentTracker.DATE));

            //show/hide image view
            if (!map.get(ChatsContentTracker.UPLOAD_IMAGE).equals("")) {
                holderChat.imgChatRecvrShow.setVisibility(View.VISIBLE);
                Picasso.with(activity).load(map.get(ChatsContentTracker.UPLOAD_IMAGE))
//                        .resize(50,50)
//                        .centerCrop()
                        .into(holderChat.imgChatRecvrShow);
            } else {
                holderChat.imgChatRecvrShow.setVisibility(View.GONE);
            }
            holderChat.imgChatRecvrShow.setTag(i);
            holderChat.imgChatRecvrShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HashMap<String, String> map = arrayList.get((Integer) view.getTag());
                    new AlertDialogue(activity).dialogueGlovie_Image(activity, false, false,
                            map.get(Chats_MYPCP.UPLOAD_IMAGE), "c");

                }
            });


            try {
                //when clicked link in textview, open open url in a browser
                holderChat.tvReciverMsg.setMovementMethod(new TextViewLinkHandler() {
                    @Override
                    public void onLinkClick(String url) {

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        activity.startActivity(browserIntent);
                    }
                });
            } catch (Exception e) {
                Log.d("json", e.getMessage());
            }

        } else {

            HashMap<String, String> map = arrayList.get(i);
            // Log.d("Chat Data",map.get(Chats_MYPCP.DATE));
            holderChat.tvMessage.setText(map.get(ChatsContentTracker.CHAT_MESSAGE).replace("\\n","\n"));
            holderChat.tv_SenderDate.setText(map.get(ChatsContentTracker.DATE));


            //show/hide imageview
            if (!map.get(ChatsContentTracker.UPLOAD_IMAGE).equals("")) {
                holderChat.imgChatSender_Show.setVisibility(View.VISIBLE);
                Picasso.with(activity).load(map.get(ChatsContentTracker.UPLOAD_IMAGE))
//                        .resize(50,50)
//                        .centerCrop()
                        .into(holderChat.imgChatSender_Show);
            } else {
                holderChat.imgChatSender_Show.setVisibility(View.GONE);
            }
            holderChat.imgChatSender_Show.setTag(i);
            holderChat.imgChatSender_Show.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HashMap<String, String> map = arrayList.get((Integer) view.getTag());
                    new AlertDialogue(activity).dialogueGlovie_Image(activity, false, false,
                            map.get(Chats_MYPCP.UPLOAD_IMAGE), "c");

                }
            });


            //when clicked link in textview, open open url in a browser
            holderChat.tvMessage.setMovementMethod(new TextViewLinkHandler() {
                @Override
                public void onLinkClick(String url) {
                    //check if string contain specific link i.e(http://bit.ly/) then call to webservices
                    if (holderChat.tvMessage.getText().toString().contains("http://bit.ly/")) {

                        if (fragment instanceof ChatsContentTracker) {
                            new ChatsContentTracker().services_Webservices("link");

                            Log.d("json adaptor", "Chat contect");
                            // Logic here...
                        }
                    }
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    activity.startActivity(browserIntent);

                }
            });

        }

        return view;
    }

    private class ViewHolder_Chat {
        TextView tvMessage, tv_SenderDate, tvChatRec_Date, tvReciverMsg;

        ImageView  imgChatRecvrShow, imgChatSender_Show;


    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        HashMap<String, String> map = arrayList.get(position);
        strSender = (map.get(Chats_MYPCP.USER));
        if (strSender.equals(sharedPreferences.getString(Login_Contstant.CONTARCT_ID, null))) {
            return 1;
        } else {
            return 0;
        }
    }

}
