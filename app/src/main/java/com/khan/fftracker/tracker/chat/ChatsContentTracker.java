package com.khan.fftracker.tracker.chat;

import static android.app.Activity.RESULT_OK;
import static com.khan.fftracker.login_Stuffs.Login_New.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import com.khan.fftracker.Adaptor_MYPCP.Chats_Content_Adaptor;
import com.khan.fftracker.Ancillary_Coverages.GetImage_Path;
import com.khan.fftracker.Ancillary_Coverages.RefreshGallery;
import com.khan.fftracker.Chats_View.Chats_List;
import com.khan.fftracker.Chats_View.RelativeLayoutTouchListener;
import com.khan.fftracker.DashBoard.Dashboard_Constants;
import com.khan.fftracker.DrawerStuff.Keyboard_Close;
import com.khan.fftracker.logCalls.LogCalls_Debug;
import com.khan.fftracker.Navigation_Drawer.DataPart;
import com.khan.fftracker.Navigation_Drawer.Drawer;
import com.khan.fftracker.Navigation_Drawer.Hide_Show_Xp_FloatBtn;
import com.khan.fftracker.Network_Volley.AppSingleton;
import com.khan.fftracker.Network_Volley.HttpStringRequest;
import com.khan.fftracker.Network_Volley.IsAdmin;
import com.khan.fftracker.Network_Volley.Multipart_Volley;
import com.khan.fftracker.Network_Volley.Network_Stuffs;
import com.khan.fftracker.Prefrences.Prefs_OperationKotlin;
import com.khan.fftracker.R;
import com.khan.fftracker.Video_Create_Customer.VideoList_QuoteRO;
import com.khan.fftracker.Video_Create_Customer.Video_List_Customer_Detail;
import com.khan.fftracker.commanStuff.OnBack_Pressed;
import com.khan.fftracker.login_Stuffs.Login_Contstant;
import com.khan.fftracker.tracker.TrackerConstant;
import com.khan.fftracker.tracker.chat.adaptor.ChatsContentAdaptor;
import com.khan.fftracker.tracker.friendsDetail.FriendDetailsTracker;
import com.khan.fftracker.tracker.trackerDashboard.TrackerDashboard;
import com.khan.fftracker.tracker.trackerDashboard.dataModel.Contract;
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by DELL on 1/19/2017.
 */
public class ChatsContentTracker extends Fragment implements View.OnClickListener {


    String BASE_URL="https://example/v/17/Familytracker/";
    String API_URL="";

    public static final String CHAT_MESSAGE = "message";
    public static final String DATE = "chatdate";
    public static final String CHAT_IMAGE = "User1Image";
    public static final String USER = "user1";
    public static final String SENDER_NAME = "user1Name";
    public static final String SENDER_PHONE = "User1Phone";
    public static final String UPLOAD_IMAGE = "upload_img";
    public static final String CHAT_ID = "ChatID";
    private ArrayList<HashMap<String, String>> arrayList;
    private ListView chats_content_lv;
    private ChatsContentAdaptor chatsContentAdaptor;
    private static SharedPreferences sharedPreferences;
    private JSONObject jsonObject;
    private TextView tvNoChat, tvChatTitle;
    private RelativeLayout layout;

    private static String imgPath;
    private static File file;
    SparkButton imgSend;

    SparkButton imgupload;
    ImageView imgShow, imgDelete;
    CircleImageView imgUser;
    EditText etChat;
    private String strMsg;
    Handler handler;
    Runnable runnable;
    HttpStringRequest stringRequest;
    Bitmap bitmap = null;
    int camera_Capture = 1;
    int gallery_Image = 2;
    private static final int REQUEST_CAMERA = 323;
    private HashMap<String, String> map;
    private String strWebservice = "";

    /// IsAdmin: Class for using check whether customer or admin logged in
    IsAdmin isAdmin;
    private Uri photoURI;
    private Contract friendContractItemPref;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_content_tracker, container, false);

        sharedPreferences = getActivity().getSharedPreferences(Login_Contstant.MY_PREFS, Context.MODE_PRIVATE);
        init_Widgets(view);//Initilizing All ui elements

        //backpressed
        requireActivity().getOnBackPressedDispatcher().addCallback(
                requireActivity(),
                onBackPressedCallback
        );

        /// Initialize class
        isAdmin= new IsAdmin(getActivity());

        isAdmin.showhideLoader(View.VISIBLE);
        prefsData();//set title to main chat,saved in chatlist


        services_Webservices("data");//Call to web services using volley lib
        timer();//using to handler delay webservices call for 10 secs


        //which is placed in bottom of the screen
        new Hide_Show_Xp_FloatBtn(getActivity()).hideXp_FloatingBtn();


        return view;
    }

    ///set title to main chat,saved in chatlist
    private void prefsData() {
        friendContractItemPref = Prefs_OperationKotlin.INSTANCE.getModelPref(TrackerConstant.FRIEND_CONTRACT_ITEM_PREFS, Contract.class);

        tvChatTitle.setText(friendContractItemPref.getCustomerFName()+" "+friendContractItemPref.getCustomerLName());

        String strCustomer_Image = friendContractItemPref.getCustomerImage();

        Picasso.with(getActivity()).load(strCustomer_Image).placeholder(R.drawable.male_avatar).into(imgUser);

    }

    //Initilizing All ui elements
    @SuppressLint("ClickableViewAccessibility")
    private void init_Widgets(View view) {

        tvNoChat = (TextView) view.findViewById(R.id.tvNoChatContent);
        tvChatTitle = (TextView) view.findViewById(R.id.tvChatTitle);
        imgSend = (SparkButton) view.findViewById(R.id.imgChatContent_Send);
        imgupload = (SparkButton) view.findViewById(R.id.imgChatGallery);
        imgShow = (ImageView) view.findViewById(R.id.imgChat_Show);
        imgUser = (CircleImageView) view.findViewById(R.id.imgChatUser);
        imgDelete = (ImageView) view.findViewById(R.id.img_Delete_recycle);
        etChat = (EditText) view.findViewById(R.id.etChatContent);

        layout = (RelativeLayout) view.findViewById(R.id.relative_Recycle);

        imgSend.setOnClickListener(this);
        imgupload.setOnClickListener(this);
        imgDelete.setOnClickListener(this);

        //// Dismiss ,Hide image while swiping
        layout.setOnTouchListener(new RelativeLayoutTouchListener(getActivity()) {
            @Override
            public void onLeftToRightSwipe() {
                super.onLeftToRightSwipe();
                bitmap = null;
                layout.setVisibility(View.GONE);
            }

            @Override
            public void onRightToLeftSwipe() {
                super.onRightToLeftSwipe();
                bitmap = null;
                layout.setVisibility(View.GONE);
            }
        });
        //   new RelativeLayoutTouchListener(getActivity()).onLeftToRightSwipe();
    }



    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        arrayList = new ArrayList<>();

        chats_content_lv = (ListView) view.findViewById(R.id.chats_content_lv);


    }

    //Call to web services using volley lib
    public void services_Webservices(final String data) {
        // imgShow.setVisibility(View.GONE);
        // bitmap=null;
        strWebservice = data;
        HashMap<String, String> hashmap=map(data);

        stringRequest = new HttpStringRequest(API_URL, hashmap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                isAdmin.showhideLoader(View.GONE);
                int success;
                try {

                    Log.d("json ", response);
                    jsonObject = new JSONObject(response);
                    Log.d("json Response", String.valueOf(jsonObject));
                    String veryLongString = jsonObject.toString();
                    int maxLogSize = 1000;
                    for (int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
                        int start = i * maxLogSize;
                        int end = (i + 1) * maxLogSize;
                        end = end > veryLongString.length() ? veryLongString.length() : end;
                        Log.d("json", veryLongString.substring(start, end));
                    }


                    success = jsonObject.getInt(Network_Stuffs.SUCCESS);
                    strMsg = jsonObject.getString("message");

                    if (success == 1) {
                        setEntireThread(data);//set all data to listview and textview

                    } else {
                        isAdmin.showhideLoader(View.GONE);
                        // Toast.makeText(getActivity(), strMsg, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException nu) {
                    nu.printStackTrace();
                    //  Log.d("Null", "App Crashed");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    if (data.equals("msg")) {
                        //start timer after getting response
                        timer();
                    }
                    isAdmin.showhideLoader(View.GONE);
                    NetworkResponse networkResponse = error.networkResponse;
                     String errorMessage = getActivity().getString(R.string.errorMessage);
                    if (networkResponse == null) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorMessage = "Request timeout";
                        } else if (error.getClass().equals(NoConnectionError.class)) {
                            errorMessage = "Failed to connect server";
                        }
                    }
                    // Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();

                    // Toast.makeText(getActivity(), getActivity().getString(R.string.errorMessage);, Toast.LENGTH_LONG).show();
                } catch (NullPointerException nu) {
                    nu.printStackTrace();
                    //Log.d("Null", "App Crashed");
                }


            }
        });
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppSingleton.getInstance().addToRequestQueue(stringRequest);

    }

    private void setEntireThread(String data) {

        try {
            if (data.equals("msg")) {
                //start timer after getting response
                timer();
            }
            tvNoChat.setText(strMsg);
            setData_ListView();//set data to listview
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, String> map(String data) {
        map = new HashMap<>();
        //for entire data fst time call webservices
        switch (data) {
            case "data":
                API_URL=BASE_URL+"allchat";
                break;
            //for call after delay of 10 secs
            case "timer":
                API_URL=BASE_URL+"allchat";
                map.put(CHAT_ID, "0");
                if (!arrayList.isEmpty()){
                    HashMap<String, String> lastItem = arrayList.get(arrayList.size() - 1);
                    map.put(CHAT_ID, lastItem.get(CHAT_ID));
                }

                break;

            //send msg from mobile
            default:
                API_URL=BASE_URL+"chat";
                map.put("Message", etChat.getText().toString());
                map.put("isSendEmailNow","0");
                map.put("isSendSMS","0");
                break;
        }
// get regular values(Params) of hashmap
        //params that sending in all web api call
        map = new IsAdmin(getActivity()).hashMap_Params(map);

        map.put("FriendContractID", friendContractItemPref.getContractID());
        map.put("FriendIsGuest" , friendContractItemPref.getIsGuest());
        map.put("GroupID", Network_Stuffs.GROUP_ID);
        Log.d("json key", map.toString());
        return map;
    }

    //////////////////////////// set data to listview adaptor
    private void setData_ListView() {
        try {

            JSONArray jsonArray = jsonObject.getJSONArray("allchats");
            if (jsonArray.length() == 0) {
                // Log.d("Chat","Listview empty");
                return;
            }
            for (int a = 0; a < jsonArray.length(); a++) {
                //  Log.d("Chat","List view have data");
                JSONObject jsonItem = jsonArray.getJSONObject(a);
                HashMap<String, String> map = new HashMap<>();
                map.put(USER, jsonItem.getString("ContractID"));
                map.put(DATE, jsonItem.getString("MessageDateTime"));
                map.put(CHAT_MESSAGE, jsonItem.getString("Message"));
                map.put(UPLOAD_IMAGE, jsonItem.getString("UploadImg"));
                map.put(CHAT_ID, jsonItem.getString(CHAT_ID));

                arrayList.add(map);

            }

            int currentPostion = chats_content_lv.getFirstVisiblePosition();
             chatsContentAdaptor = new ChatsContentAdaptor(getActivity(), arrayList);
            if (chatsContentAdaptor.getCount() != 0) {
                chats_content_lv.setAdapter(chatsContentAdaptor);
                chats_content_lv.setSelectionFromTop(currentPostion, 0);
                lastPosition();
            } else {
                tvNoChat.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            Log.d("json error", e.getMessage());
        }
        //  timer();//using to handler delay webservices call for 10 secs
    }


    private void multipart_Volley() {
        strWebservice = "msg";
        // create_ProgressBar();//Create progressbar at the center of the screen
        layout.setVisibility(View.GONE);
        imgShow.setVisibility(View.GONE);
        isAdmin.showhideLoader(View.VISIBLE);
        HashMap<String, String> hashmap=map(strWebservice);
        Multipart_Volley multipart_volley = new Multipart_Volley(Request.Method.POST, API_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        isAdmin.showhideLoader(View.GONE);
                        bitmap = null;
                        String resultResponse = new String(response.data);

                        //  Log.d("str Respone", resultResponse);
                        try {
                            jsonObject = new JSONObject(resultResponse);
                            Log.d("jsonRespone", String.valueOf(jsonObject));
                            int success = jsonObject.getInt(Network_Stuffs.SUCCESS);
                            if (success == 1) {

                                setEntireThread("msg");//set all data to listview and textview
                                etChat.setText("");
                            } else {
                                isAdmin.showhideLoader(View.GONE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException nu) {
                            nu.printStackTrace();
                            //  Log.d("Null", "App Crashed");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //start timer after getting response
                isAdmin.showhideLoader(View.GONE);
                timer();
                bitmap = null;

                NetworkResponse networkResponse = error.networkResponse;
                 String errorMessage = getActivity().getString(R.string.errorMessage);
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        //  Log.d("JSonREsponsem multipart", String.valueOf(response));
                        String status = response.getString("status");
                        String message = response.getString("message");

                        //   Log.e("Error Status", status);
                        //   Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + " Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException nu) {
                        //   Log.d("App crasehd","Scd Apointment");
                    }
                }
                //  Toast.makeText(getActivity(),errorMessage,Toast.LENGTH_LONG).show();
                //  Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
               // map("msg");

                return hashmap;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("uploadimg", new DataPart("file_avatar.jpg", imgConvert_ToBase64(), "image/jpeg"));
                // params.put("cover", new DataPart("file_cover.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), mCoverImage.getDrawable()), "image/jpeg"));
                return params;
            }
        };
        // AppSingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
        multipart_volley.setShouldCache(false);
        multipart_volley.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppSingleton.getInstance().addToRequestQueue(multipart_volley);


    }


    private void lastPosition() {
        chats_content_lv.post(new Runnable() {
            @Override
            public void run() {
                chats_content_lv.setSelection(arrayList.size());

            }
        });
    }

    //using to handler delay webservices call for 10 secs
    private void timer() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                //   Log.d("Timer","Timer");
                services_Webservices("timer");
                handler.postDelayed(this, 10000);

            }
        };
        handler.postDelayed(runnable, 10000);

    }



    ///Image convert to bitmap and send to server
    private byte[] imgConvert_ToBase64() {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imagebyte = baos.toByteArray();
            //  strBase64= Base64.encodeToString(imagebyte, Base64.DEFAULT);;
            return imagebyte;
        } catch (NullPointerException nu) {
            nu.printStackTrace();
            //Log.d("App crasehd", "bitmap Null schedule appoint");
        }
        return null;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        Log.d(TAG, "called onDestroy: ");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isAdmin.showhideLoader(View.GONE);
        try {
            handler.removeCallbacks(runnable);
            stringRequest.cancel();
        } catch (Exception e) {
            Log.d(TAG, "onDestroy: " + e.getMessage());
        }
    }


    ///////////// On click listener
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgChatGallery:
                getImage_Dialogue();
                break;
            case R.id.imgChatContent_Send:
                ///For hide keyboard
                new Keyboard_Close(getActivity()).keyboard_Close_Down();
                checkInput_Data();
                break;
            case R.id.img_Delete_recycle:
                layout.setVisibility(View.GONE);
                bitmap = null;
                break;
            default:
                break;

        }
    }
    ////////// Check if edittext is not empty then send message to server(DB)
    private void checkInput_Data() {
        try {
            //Check if there is picture attached
            if (bitmap != null) {
                if (etChat.getText().toString().length() != 0) {
                    handler.removeCallbacks(runnable);
                    stringRequest.cancel();
                    multipart_Volley();
                    //etChat.setText("");
                } else {
                    Toast.makeText(getActivity(), "Write Your Text", Toast.LENGTH_LONG).show();
                }
            } else {
                if (etChat.getText().toString().length() != 0) {
                    handler.removeCallbacks(runnable);
                    stringRequest.cancel();
                    // handler.removeCallbacksAndMessages(null);
                    isAdmin.showhideLoader(View.VISIBLE);

                    services_Webservices("msg");
                    etChat.setText("");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /////////////////////////////// Dialogue for taking picture from gallery or camera
    private void getImage_Dialogue() {
        final CharSequence[] chars = {"Camera", "Gallery"};

        final AlertDialog.Builder aBuilder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View titleView = inflater.inflate(R.layout.dialogue_titleview, null);
        aBuilder.setCustomTitle(titleView);
        TextView tv = (TextView) titleView.findViewById(R.id.tvDialogueTitle);
        tv.setText("Take Picture");
        //aBuilder.setTitle("Take Picture");
        //  aBuilder.setIcon(R.drawable.ic_menu_camera);
        aBuilder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        aBuilder.setItems(chars, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (chars[i].equals("Camera")) {
                    if (verifyCameraPermissions(getActivity())) {
                        return;
                    }
                    takePhoto_Intent();

                } else if (chars[i].equals("Gallery")) {
                    if (Build.VERSION.SDK_INT < 19) {

                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);

                        try {
                            getActivity().startActivityForResult(
                                    Intent.createChooser(intent, "Select a File to Upload"), gallery_Image);
                        } catch (android.content.ActivityNotFoundException ex) {
                            // Potentially direct the user to the Market with a Dialog
                            Toast.makeText(getActivity(), "Please install a File Manager.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        getActivity().startActivityForResult(intent, gallery_Image);
                    }
                }
//                else if (chars[i].equals("Cancel")) {
//                    dialogInterface.dismiss();
//                }
            }
        });

        getActivity().getWindow().getAttributes().windowAnimations = R.style.CustomAnimations_slide;
        aBuilder.show();
    }

    private void takePhoto_Intent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
        getActivity().startActivityForResult(cameraIntent, camera_Capture);
    }

   /* private void takePhoto_Intent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());

        intentActivityResultLauncher.launch(intent);

    }*/

    // GetContent creates an ActivityResultLauncher<String> to let you pass
    // in the mime type you want to let the user select
    ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        refreshGallery(file);

                        bitmap = BitmapFactory.decodeFile(imgPath);

                       // imgShow.setImageBitmap(bitmap);
                        try {
                            int width = bitmap.getWidth();
                            int height = bitmap.getHeight();

                            int newWidth = 1200; // Replace with new width
                            int newHeight = 800; // Replace with new height

                            float scaleWidth = ((float) newWidth) / width;
                            float scaleHeight = ((float) newHeight) / height;

                            Matrix matrix = new Matrix();
                            matrix.postScale(scaleWidth, scaleHeight);

                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
                            imgShow.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        Log.d("jsonCAPTURE_CAMERA", String.valueOf(bitmap));
                    }
                }
            }
    );


    private Uri setImageUri() {
        file = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "image" + new Date().getTime() + ".jpg");
        // Uri imgUri = Uri.fromFile(file);

        photoURI = FileProvider.getUriForFile(getActivity(), "com.khan.fftracker" + ".provider",
                file);

        imgPath = file.getAbsolutePath();
        return photoURI;
    }


    ///////////////////////// Refresh gallery after taking pic from camera
    public void refreshGallery(File mediaFile) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent mediaScanIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(mediaFile); //out is your file you saved/deleted/moved/copied
            mediaScanIntent.setData(contentUri);
            getActivity().sendBroadcast(mediaScanIntent);
        } else {
            getActivity().sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://"
                            + Environment.getExternalStorageDirectory())));
        }
    }
    /**
     * Checks if the app has permission to write to device Camera
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    private boolean verifyCameraPermissions(FragmentActivity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 212);
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    takePhoto_Intent();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "Permission denied using camera", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            try {
                layout.setVisibility(View.VISIBLE);
                imgShow.setVisibility(View.VISIBLE);
                Uri uri = null;
                if (requestCode == camera_Capture) {

                    Log.d("json image path", "" + imgPath);
                    bitmap = BitmapFactory.decodeFile(imgPath);

                    Log.d("json raw", bitmap.getWidth() + "bitheight" + bitmap.getHeight() + "size" + bitmap.getByteCount());
                    ///refresh gallery
                    new RefreshGallery(getActivity());

                    bitmap=new GetImage_Path(getActivity()).handleSamplingAndRotationBitmap(photoURI);

                    imgShow.setImageBitmap(bitmap);
                    imgShow.setVisibility(View.VISIBLE);

                    Log.d("json  ", bitmap.getWidth() + " bitheight " + bitmap.getHeight() + " size " + bitmap.getByteCount());

                } else if (requestCode == gallery_Image) {
                    uri = data.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    imgConvert_ToBase64();
                }

                imgShow.setImageBitmap(bitmap);


            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Log.d("image not", "upload Successful");
        }
    }

    private void resizeImage() {
        try {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int newWidth = 1200; // Replace with new width
            int newHeight = 800; // Replace with new height

            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;

            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);

            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            imgShow.setImageBitmap(bitmap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        // Log.d("Chat",path);
        return Uri.parse(path);
    }



    private final OnBackPressedCallback onBackPressedCallback=new OnBackPressedCallback(true/* Enabled by default */) {
        @Override
        public void handleOnBackPressed() {
            int size = ((Drawer) getActivity()).visibleFragments.size() - 1;
            Fragment fragment = ((Drawer) getActivity()).visibleFragments.get(size);
            if (!(fragment instanceof FriendDetailsTracker)) {

                getActivity().getSupportFragmentManager().popBackStack();
                ((Drawer) getActivity()).getFragment(new TrackerDashboard(), -1);
            }
            else {
                closeFragment();
            }
            LogCalls_Debug.d(TAG, "handleOnBackPressed false");
        }
    };

    private void closeFragment() {

        requireActivity().getSupportFragmentManager().popBackStack();

    }
}

