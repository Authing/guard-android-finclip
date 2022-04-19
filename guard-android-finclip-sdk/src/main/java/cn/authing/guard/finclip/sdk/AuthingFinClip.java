package cn.authing.guard.finclip.sdk;

import android.app.Application;

import com.finogeeks.lib.applet.client.FinAppClient;
import com.finogeeks.lib.applet.client.FinAppConfig;
import com.finogeeks.lib.applet.interfaces.FinCallback;

import cn.authing.guard.Authing;

public class AuthingFinClip {

    private AuthingFinClip() {
    }

    private static class Singleton{
        private static final AuthingFinClip mInstance = new AuthingFinClip();
    }

    public static AuthingFinClip getInstance(){
        return Singleton.mInstance;
    }

    public void init(Application application, String authingAppId, FinAppConfig config, FinCallback<Object> callback){
        Authing.init(application, authingAppId);
        if (FinAppClient.INSTANCE.isFinAppProcess(application)) {
            // 小程序进程不执行任何初始化操作
            return;
        }
        FinAppClient.INSTANCE.init(application, config, new FinCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
                FinAppClient.INSTANCE.getExtensionApiManager().registerApi(new GuardApi());
                callback.onSuccess(result);
            }

            @Override
            public void onError(int code, String error) {
                callback.onError(code, error);
            }

            @Override
            public void onProgress(int status, String error) {
                callback.onProgress(status, error);
            }
        });
    }
}
