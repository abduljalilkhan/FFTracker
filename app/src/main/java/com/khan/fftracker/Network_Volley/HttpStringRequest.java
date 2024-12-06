package com.khan.fftracker.Network_Volley;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.khan.fftracker.Prefrences.Prefs_Operation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Abdul Jalil Khan on 11/3/2016.
 */
public class HttpStringRequest extends StringRequest {
    private Map<String,String>mParams;
    public HttpStringRequest(String url, HashMap<String, String> map, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);
        mParams=map;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        if (mParams!=null){
            return mParams;
        }
        return super.getParams();
    }
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        // Basic Authentication
        //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);


        headers.put("Authorization",  Prefs_Operation.readPrefs(ShopBossConstant.AUTH_TOKEN,""));
        return headers;
    }
}
