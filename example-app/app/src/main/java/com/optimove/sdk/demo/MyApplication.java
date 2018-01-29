package com.optimove.sdk.demo;

import android.app.Application;

import com.optimove.sdk.optimove_sdk.main.Optimove;
import com.optimove.sdk.optimove_sdk.main.TenantInfo;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //This info is provided by Optimove during integration
        TenantInfo tenantInfo = new TenantInfo(
                "https://provided.by.optimove/", //The Initialization end point
                "demo_apps", //The token
                "1.0.0"); //The configurations name
//                "exampleapp.android.1.0.0"); //The configurations name

        //The Configure function must be called as soon as the app starts
        Optimove.configure(this, tenantInfo);
    }


}
