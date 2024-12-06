package com.khan.fftracker.Network_Volley;

import static com.khan.fftracker.logCalls.LogCalls_Debug.TAG;

import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.khan.fftracker.AdminMyPCP.Inspection.InspectionDetailList.Adapter.Horizontal_ImageAdaptor;
import com.khan.fftracker.logCalls.LogCalls_Debug;
import com.khan.fftracker.Navigation_Drawer.DataPart;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Json_Response_MVP {

    private JSONObject jsonObject;
    private HashMap<String, String> map;
    private OnWebserviceFinishedLisetner onWebserviceFinishedLisetner;
    private Bitmap bitmap;
    private  HttpStringRequest httpStringRequest;

    public Json_Response_MVP(HashMap<String, String> hashMap) {
        try {

            map = hashMap;
            Log.d("json Json_Response", map.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//    //retain user interaction when progressbar visibility is gone
//    private void enable_UserInteraction() {
//        context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//    }

    public JSONObject call_Webservices(OnWebserviceFinishedLisetner onWebserviceFinishedLisetne, String strFunction) {
        this.onWebserviceFinishedLisetner=onWebserviceFinishedLisetne;
        try {

            httpStringRequest = new HttpStringRequest(strFunction, map, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    try {


                        int success;
                         Log.d("json String ", response);
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

                        onWebserviceFinishedLisetner.onWebServiceSuccess(jsonObject);
                    } catch (Exception e) {
                        Log.d("json", e.getMessage());
                        onWebserviceFinishedLisetner.onWebServiceSuccess(jsonObject);
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    onWebserviceFinishedLisetner.onWebserviceFailed(error);
                    jsonObject = null;
                    Log.d("Json", "Proble in jsonresponse");


                }
            });

            httpStringRequest.setShouldCache(false);
            httpStringRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppSingleton.getInstance().addToRequestQueue(httpStringRequest);

        }catch (Exception e){
            LogCalls_Debug.d(TAG, "call_Webservices: "+e.getMessage());
        }
        return jsonObject;

    }

    public void cancelRequest(){
        httpStringRequest.cancel();
    }



    public JSONObject multipart_Volley(OnWebserviceFinishedLisetner onWebserviceFinishedLisetne, Bitmap bitmap, String strURl) {
        Log.d("json", "multipart_Volley");
        this.bitmap = bitmap;
        this.onWebserviceFinishedLisetner=onWebserviceFinishedLisetne;

        Multipart_Volley multipart_volley = new Multipart_Volley(Request.Method.POST, strURl,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        String resultResponse = new String(response.data);

                        Log.d("json multipart_Response", resultResponse);
                        try {

                            int success;
                            Log.d("json String ", resultResponse);
                            jsonObject = new JSONObject(resultResponse);
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

                            onWebserviceFinishedLisetner.onWebServiceSuccess(jsonObject);
                        } catch (Exception e) {
                            Log.d("json", e.getMessage());
                            onWebserviceFinishedLisetner.onWebServiceSuccess(jsonObject);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onWebserviceFinishedLisetner.onWebserviceFailed(error);
                jsonObject = null;
                //  bitmap = null;

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Error";
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
                        Log.d("json error", "onErrorResponse: ");
                    }
                }
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

    public JSONObject multipart_VolleyPng(OnWebserviceFinishedLisetner onWebserviceFinishedLisetne, Bitmap bitmap, String strURl) {
        Log.d("json", "multipart_Volley");
        this.bitmap = bitmap;
        this.onWebserviceFinishedLisetner=onWebserviceFinishedLisetne;

        Multipart_Volley multipart_volley = new Multipart_Volley(Request.Method.POST, strURl,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        String resultResponse = new String(response.data);

                        Log.d("json multipart_Response", resultResponse);
                        try {

                            int success;
                            Log.d("json String ", resultResponse);
                            jsonObject = new JSONObject(resultResponse);
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

                            onWebserviceFinishedLisetner.onWebServiceSuccess(jsonObject);
                        } catch (Exception e) {
                            Log.d("json", e.getMessage());
                            onWebserviceFinishedLisetner.onWebServiceSuccess(jsonObject);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onWebserviceFinishedLisetner.onWebserviceFailed(error);
                jsonObject = null;
                //  bitmap = null;

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Error";
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
                        Log.d("json error", "onErrorResponse: ");
                    }
                }
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
                params.put("uploadimg", new DataPart("file_avatar.jpg", imgConvert_ToBase64PNG(), "image/png"));
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

    public JSONObject multipart_Volley_MultipleImages(OnWebserviceFinishedLisetner onWebserviceFinishedLisetne, ArrayList<HashMap<String, String>> arrMap, String strURl) {
        Log.d("json", "multipart_Volley");
        this.bitmap = bitmap;
        this.onWebserviceFinishedLisetner=onWebserviceFinishedLisetne;

        Multipart_Volley multipart_volley = new Multipart_Volley(Request.Method.POST, strURl,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        String resultResponse = new String(response.data);

                        Log.d("json multipart_Response", resultResponse);
                        try {

                            int success;
                            Log.d("json String ", resultResponse);
                            jsonObject = new JSONObject(resultResponse);
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

                            onWebserviceFinishedLisetner.onWebServiceSuccess(jsonObject);
                        } catch (Exception e) {
                            Log.d("json", e.getMessage());
                            onWebserviceFinishedLisetner.onWebServiceSuccess(jsonObject);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onWebserviceFinishedLisetner.onWebserviceFailed(error);
                jsonObject = null;
                //  bitmap = null;

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Error";
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
                        Log.d("json error", "onErrorResponse: ");
                    }
                }
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
                //  Log.d("glovie Boolean", Glovie.editOrNot + " " + Glovie.uploadImages + " " + Glovie.isPics);
                for (int a = 0; a < arrMap.size(); a++) {
                    //   Log.d("Map Image",path.get(a));

                    HashMap<String, String> map = arrMap.get(a);
                    if (!map.get("img").startsWith("http")) {
                        bitmap = Horizontal_ImageAdaptor.decodeFile(map.get("img"));

                        params.put("uploadimg_" + a, new DataPart(map.get("img"), imgConvert_ToBase64(), "image/jpeg"));
                    }
                }

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

    private byte[] imgConvert_ToBase64PNG() {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);
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

