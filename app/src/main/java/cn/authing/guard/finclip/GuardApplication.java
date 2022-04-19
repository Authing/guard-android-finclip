package cn.authing.guard.finclip;

import android.app.Application;
import android.util.Log;

import com.finogeeks.lib.applet.client.FinAppClient;
import com.finogeeks.lib.applet.client.FinAppConfig;
import com.finogeeks.lib.applet.interfaces.FinCallback;

import cn.authing.guard.Authing;
import cn.authing.guard.finclip.sdk.GuardApi;

public class GuardApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //60caaf41df670b771fd08937
        //62356d37e1dc5bb66e108584
        Authing.init(this, "6244398c8a4575cdb2cb5656");



        if (FinAppClient.INSTANCE.isFinAppProcess(this)) {
            // 小程序进程不执行任何初始化操作
            return;
        }
//        FinAppConfig config = new FinAppConfig.Builder()
//                .setSdkKey("TvlO38fdISYJe1RGndcRPfMvRe8MPiEZ1aascD2TUFQ=")   // SDK Key
//                .setSdkSecret("3a0d38e68cb88be8")   // SDK Secret
//                .setApiUrl("https://api.finclip.com")   // 服务器地址
//                .setApiPrefix("/api/v1/mop/")   // 服务器接口请求路由前缀
//                .setEncryptionType("SM")   // 加密方式，国密:SM，md5: MD5
//                .setEnableAppletDebug(true)
//                .build();

        FinAppConfig config = new FinAppConfig.Builder()
                .setSdkKey("8QLZmDxYi9qY5L6V1biny8bvSPmrKcweecWZYDUJmPVndG2JOPsS9yA5fww36gSp")   // SDK Key
                .setSdkSecret("45b3801c5b863bc8")   // SDK Secret
                .setApiUrl("https://api.finclip.com")   // 服务器地址
                .setApiPrefix("/api/v1/mop/")   // 服务器接口请求路由前缀
                .setEncryptionType("SM")   // 加密方式，国密:SM，md5: MD5
                .build();

        FinAppClient.INSTANCE.init(this, config, new FinCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
                Log.e("GuardApplication", "fin app client init success: result = " + result);

                FinAppClient.INSTANCE.getExtensionApiManager().registerApi(new AuthingApi());
                FinAppClient.INSTANCE.getExtensionApiManager().registerApi(new GuardApi());
            }

            @Override
            public void onError(int code, String error) {
                //Toast.makeText(AppletApplication.this, "SDK初始化失败", Toast.LENGTH_SHORT).show();
                Log.e("GuardApplication", "fin app client init error: code = " + code + " error = " + error);
            }

            @Override
            public void onProgress(int status, String error) {
                Log.e("GuardApplication", "fin app client init progress: status = " + status + " error = " + error);
            }
        });

    }
}
