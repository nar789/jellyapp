package app.msg.jelly;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.apache.cordova.JellyServiceBase;

public class JellyService extends JellyServiceBase {
    private String TAg = "JellyService";

    private final IBinder binder = new JellyBinder();
    private String mUrl = "https://localhost/happy/index.html";
    private String msg = "";

    public class JellyBinder extends Binder {
        JellyService getService() {
            return JellyService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Bundle b = intent.getExtras();
        if(b!=null) {
            int idx = b.getInt("idx");

            msg = b.getString("msg");
            Log.d("JellyService", "idx = " + idx + ", msg = " + msg);
            mUrl = (idx == 2) ? "https://localhost/sad/index.html" : "https://localhost/happy/index.html";
            loadUrl(mUrl);
            new Handler().postDelayed(()->{
                loadUrl("javascript:play('" + msg + "')");
            }, 1000);
        }

        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
