package com.khan.fftracker.Network_Volley;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


import java.util.HashMap;
import java.util.StringTokenizer;

public class GetHashmap_Values {
    Activity activity;
    SharedPreferences sharedPreferences;


    public GetHashmap_Values(Activity activity) {
        this.activity = activity;
        sharedPreferences=activity.getSharedPreferences(Login_Contstant.MY_PREFS, Context.MODE_PRIVATE);

      //  Log.d("json chec k box", sharedPreferences.getString(Prefs_Constant.GUEST_CHECKBOX_OPTION,null));
    }

    public HashMap<String, String> getMap_Values(HashMap<String, String> map) {
        //// Get valuef from editext,spinner etc for buy plan
        mapValueForPlan(map);

        map.put("function", "ldsplan");
        map.put("DealerID", sharedPreferences.getString("dealerIdScan", null));
        map.put("ApiDealerID", Guest_More_Products.API_DealerID);
        map.put("product_id", Guest_More_Products.ProductID_Scan);

        map.put("os", "android");
        map.put("Login_Contstant.AUTH_TOKEN", sharedPreferences.getString(Login_Contstant.AUTH_TOKEN, null));


        Log.d("json Map KEy VAlue", map.toString());
        return map;
    }

    public void mapValueForPlan(HashMap<String, String> map) {
       String strPhone = sharedPreferences.getString("Prefs_Constant.GUEST_PHONE",null);
        strPhone = strPhone.replace("(", "").replace(")", "").replace(" ", "-");

        String[] strMapKey = new String[]{"Email", "vin",
                "phone", "Mileage", "PurchaseDate",
                "inserviceDate", "MakeID",
                "ModelID", "Year",
                "NewUsed"};


        String[] strMapValue = {sharedPreferences.getString(Prefs_Constant.GUEST_EMAIL,null), sharedPreferences.getString(Prefs_Constant.VIN,null), strPhone,
                sharedPreferences.getString(Prefs_Constant.GUEST_MILEAGE,null), sharedPreferences.getString(Prefs_Constant.PURCHASE_DATE,null),
                sharedPreferences.getString(Prefs_Constant.INSERVICE_DATE,null), sharedPreferences.getString(Prefs_Constant.MAKE_ID,null),
                sharedPreferences.getString(Prefs_Constant.MODEL_ID,null), sharedPreferences.getString(Prefs_Constant.VEH_YEAR,null),
                sharedPreferences.getString(Prefs_Constant.MANUFACTURER,null)};


        for (int a = 0; a < strMapValue.length; a++) {

                if (strMapValue[a].equals("-00")) {
                    strMapValue[a] = "";

            }
            map.put(strMapKey[a], strMapValue[a]);


            Log.d("json Map KEy VAlue", strMapKey[a] + " " + strMapValue[a]);
        }
    }


}
