/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package app.msg.jelly;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;

import org.apache.cordova.*;

public class MainActivity extends CordovaActivity
{

    Intent mIntent;
    boolean mBound =false;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mIntent = new Intent(this, JellyService.class);

        // enable Cordova apps to be started in the background
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean("cdvStartInBackground", false)) {
            moveTaskToBack(true);
        }

        // Set by <content src="index.html" /> in config.xml
        //launchUrl = "file:///android_asset/www/jellymain/main.html";
        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.main, null);
        String url = "https://localhost/jellymain/main.html";
        loadUrl(url, view);
    }

    @Override
    public void sendMsg(int idx, String msg) {
        super.sendMsg(idx, msg);
        mIntent.putExtra("idx", idx);
        mIntent.putExtra("msg", msg);
        //startService(mIntent);
        bindService(mIntent, connection, BIND_AUTO_CREATE);
        new Handler().postDelayed(()->{
            if(mIntent != null) {
                unbindService(connection);
                mBound = false;
            }
        }, 10000);
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            JellyService.JellyBinder binder = (JellyService.JellyBinder) service;
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mIntent != null) {
            stopService(mIntent);
        }
    }
}
