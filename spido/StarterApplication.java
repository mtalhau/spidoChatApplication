package com.example.spido;

import android.app.ActionBar;
import android.app.Application;
import android.util.Log;

import com.onesignal.OneSignal;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class StarterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("283xfuuhFLPxa1YlhyvgjF2y1Z0FCmKbkTB5IDUD")
                .clientKey("DaBaD9u6p5bQbdfXAvt7x9ajK4mSQLa3g9prgmEq")
                .server("https://parseapi.back4app.com/")
                .build()
        );

//        ParseObject object = new ParseObject("TestObject");
//        object.put("myNumber", "123");
//        object.put("myString", "talha");
//
//        object.saveInBackground(new SaveCallback () {
//            @Override
//            public void done(ParseException ex) {
//                if (ex == null) {
//                    Log.i("Parse Result", "Successful!");
//                } else {
//                    Log.i("Parse Result", "Failed" + ex.toString());
//                }
//            }
//        });
//
//
//        ParseUser.enableAutomaticUser();


        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

    }
}

