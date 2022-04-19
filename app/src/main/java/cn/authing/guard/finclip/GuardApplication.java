package cn.authing.guard.finclip;

import android.app.Application;
import android.util.Log;

import com.finogeeks.lib.applet.client.FinAppConfig;
import com.finogeeks.lib.applet.interfaces.FinCallback;

import cn.authing.guard.finclip.sdk.AuthingFinClip;

public class GuardApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FinAppConfig config = new FinAppConfig.Builder()
                .setSdkKey("8QLZmDxYi9qY5L6V1biny8bvSPmrKcweecWZYDUJmPVndG2JOPsS9yA5fww36gSp")   // SDK Key
                .setSdkSecret("45b3801c5b863bc8")   // SDK Secret
                .setApiUrl("https://api.finclip.com")   // 服务器地址
                .setApiPrefix("/api/v1/mop/")   // 服务器接口请求路由前缀
                .setEncryptionType("SM")   // 加密方式，国密:SM，md5: MD5
                .build();
        AuthingFinClip.getInstance().init(this, "6244398c8a4575cdb2cb5656", config, new FinCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
                Log.i("GuardApplication", "fin app client init success: result = " + result);
            }

            @Override
            public void onError(int code, String error) {
                Log.e("GuardApplication", "fin app client init error: code = " + code + " error = " + error);
            }

            @Override
            public void onProgress(int status, String error) {
                Log.i("GuardApplication", "fin app client init progress: status = " + status + " error = " + error);
            }
        });

    }
}
