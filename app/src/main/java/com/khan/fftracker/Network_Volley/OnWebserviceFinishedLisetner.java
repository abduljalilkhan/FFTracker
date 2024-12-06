package com.khan.fftracker.Network_Volley;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface OnWebserviceFinishedLisetner {

        void onWebServiceSuccess(JSONObject jsonObject);
        void onWebserviceFailed(VolleyError error);
}
