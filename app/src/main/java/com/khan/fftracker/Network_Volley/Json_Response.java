package com.khan.fftracker.Network_Volley;

import static com.khan.fftracker.login_Stuffs.Login_New.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.khan.fftracker.R;
import com.khan.fftracker.login_Stuffs.Login_Contstant;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Json_Response {
    private SharedPreferences sharedPreferences;
    private View view_Progressbar;
    private Activity context;
    private JSONObject jsonObject;
    private HashMap<String,String>map;
    private Json_Callback json_callback;
    private Bitmap bitmap;
    private  HttpStringRequest httpStringRequest;

    public Json_Response(Activity mContext, View progressBar, HashMap<String, String> hashMap) {
        try {
            sharedPreferences = mContext.getSharedPreferences(Login_Contstant.MY_PREFS, Context.MODE_PRIVATE);
            context = mContext;
            view_Progressbar = progressBar;
            map = hashMap;
            Log.d("json Json_Response", map.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //retain user interaction when progressbar visibility is gone
    private void enable_UserInteraction() {
        context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public JSONObject call_Webservices(final Json_Callback jsonCallback) {
        json_callback=jsonCallback;
        try {
             httpStringRequest = new HttpStringRequest(Network_Stuffs.LOGIN_URL, map, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    try {
                    view_Progressbar.setVisibility(View.GONE);
                        //retain user interaction when progressbar visibility is gone
                        enable_UserInteraction();

                    int success;
                       // Log.d("json ", response);
                        jsonObject = new JSONObject(response);
                       // Log.d("json Response", String.valueOf(jsonObject));
                        String veryLongString = jsonObject.toString();
                        int maxLogSize = 1000;
                        for (int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
                            int start = i * maxLogSize;
                            int end = (i + 1) * maxLogSize;
                            end = end > veryLongString.length() ? veryLongString.length() : end;
                            Log.d("json response", veryLongString.substring(start, end));
                        }

                        success = jsonObject.getInt(Network_Stuffs.SUCCESS);
                        if (success == 1) {

                        } else {
                            view_Progressbar.setVisibility(View.GONE);
                           // Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                        json_callback.update_Response(jsonObject);
                    } catch (Exception e) {
                        json_callback.update_Response(jsonObject);
                        view_Progressbar.setVisibility(View.GONE);
                        Log.d("json", e.getMessage());
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //retain user interaction when progressbar visibility is gone
                    enable_UserInteraction();
                    view_Progressbar.setVisibility(View.GONE);
                    json_callback.update_Response(jsonObject);
                    jsonObject = null;
                    Log.d("Json", "Proble in jsonresponse");
                    try {

                        NetworkResponse networkResponse = error.networkResponse;
                         String errorMessage = context.getString(R.string.errorMessage);
                        if (networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
                                errorMessage = "Request timeout";
                            } else if (error.getClass().equals(NoConnectionError.class)) {
                                errorMessage = "Failed to connect server";
                            }
                        }
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();

                    } catch (NullPointerException nu) {
                        nu.printStackTrace();
                        json_callback.update_Response(jsonObject);
                        view_Progressbar.setVisibility(View.GONE);
                        Log.d("json", "error_in jsonresponse");
                    }

                }
            });

            httpStringRequest.setShouldCache(false);
            httpStringRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppSingleton.getInstance().addToRequestQueue(httpStringRequest);

        }catch (Exception e){
            Log.d(TAG, "call_Webservices: "+e.getMessage());
        }
            return jsonObject;

    }

    public void cancelRequest(){
        httpStringRequest.cancel();
    }



    public JSONObject multipart_Volley(final Json_Callback jsonCallback, Bitmap bitmap) {
        Log.d("json", "multipart_Volley");
        this.bitmap = bitmap;
        json_callback = jsonCallback;

        Multipart_Volley multipart_volley = new Multipart_Volley(Request.Method.POST, Network_Stuffs.LOGIN_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        view_Progressbar.setVisibility(View.GONE);

                        int success;
                        String resultResponse = new String(response.data);

                        Log.d("json", resultResponse);
                        try {
                            jsonObject = new JSONObject(resultResponse);

                            success = jsonObject.getInt(Network_Stuffs.SUCCESS);

                            String veryLongString = jsonObject.toString();
                            int maxLogSize = 1000;
                            for (int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
                                int start = i * maxLogSize;
                                int end = (i + 1) * maxLogSize;
                                end = end > veryLongString.length() ? veryLongString.length() : end;
                                Log.v("json", veryLongString.substring(start, end));
                            }

                            if (success == 1) {


                            } else {
                                view_Progressbar.setVisibility(View.GONE);
                                Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }

                            json_callback.update_Response(jsonObject);

                        } catch (Exception e) {
                            view_Progressbar.setVisibility(View.GONE);
                            Log.d("json error", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view_Progressbar.setVisibility(View.GONE);
                json_callback.update_Response(jsonObject);
                jsonObject = null;
                //  bitmap = null;

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = context.getString(R.string.errorMessage);;
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
                        //log.d("JSonREsponsem multipart", String.valueOf(response));
                        String status = response.getString("status");
                        String message = response.getString("message");

                        //log.e("Error Status", status);
                        //log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + " Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (Exception e) {
                        view_Progressbar.setVisibility(View.GONE);
                        Log.d("json error", "onErrorResponse: ");
                    }
                }
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                //log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                Log.d(TAG, "getParams: "+map.toString());
                return map;
                //  return new GetHashmap_Values(context).getMap_Values();
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

        return jsonObject;

    }

    private byte[] imgConvert_ToBase64() {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] imagebyte = baos.toByteArray();
            // String strBase64 = Base64.encodeToString(imagebyte, Base64.DEFAULT);
            // Log.d("json bitmap",strBase64);
            return imagebyte;
        } catch (Exception nu) {
            Log.d("json error",nu.getMessage());
        }
        return null;
    }

}
