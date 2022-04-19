package cn.authing.guard.finclip.sdk;

import android.util.Log;

import com.finogeeks.lib.applet.api.AbsApi;
import com.finogeeks.lib.applet.interfaces.ICallback;

import org.json.JSONException;
import org.json.JSONObject;

import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.Guardian;
import cn.authing.guard.util.Util;

public class GuardApi extends AbsApi {

    private static final String TAG = "GuardApi";
    private static final String METHOD_GUARD_REQUEST = "guardRequest";
    private static final String METHOD_ENCRYPT_PASSWORD = "encryptPassword";
    private static final String METHOD_GET_ID_TOKEN = "getIdToken";

    private static final String HTTP_URL = "url";
    private static final String HTTP_METHOD = "method";
    private static final String HTTP_BODY = "body";
    private static final String PARAMS_PASSWORD = "password";
    private static final String PARAMS_ID_TOKEN = "idToken";
    private static final String PARAMS_ERROR_MESSAGE = "errMsg";
    private static final String PARAMS_CODE = "code";
    private static final String PARAMS_DATA = "data";


    @Override
    public String[] apis() {
        return new String[]{
                METHOD_GUARD_REQUEST,
                METHOD_ENCRYPT_PASSWORD,
                METHOD_GET_ID_TOKEN};
    }

    @Override
    public void invoke(String event, JSONObject param, ICallback callback) {
        Log.i(TAG, "invoke event = " + event);
        if (METHOD_GUARD_REQUEST.equals(event)){
            guardRequest(param, callback);
        } else if (METHOD_ENCRYPT_PASSWORD.equals(event)){
            encryptPassword(param, callback);
        } else if (METHOD_GET_ID_TOKEN.equals(event)){
            getIdToken(callback);
        }
    }

    private void encryptPassword(JSONObject param, ICallback callback) {
        try {
            String password = Util.encryptPassword(param.getString(PARAMS_PASSWORD));
            JSONObject result = new JSONObject();
            result.put(PARAMS_PASSWORD, password);
            callback.onSuccess(result);
        } catch (JSONException e) {
            callback.onFail();
            e.printStackTrace();
        }
    }

    private void guardRequest(JSONObject param, ICallback callback) {
        try {
            String method = param.getString(HTTP_METHOD);
            String url = param.getString(HTTP_URL);
            JSONObject body = param.getJSONObject(HTTP_BODY);

            Authing.getPublicConfig(config -> new Thread() {
                public void run() {
                    String fullUrl = Authing.getScheme() + "://" + Util.getHost(config) + url;
                    String newBody = body.toString();
                    if ("{}".equals(newBody)){
                        if ("POST".equalsIgnoreCase(method)){
                            newBody = new JSONObject().toString();
                        } else {
                            newBody = null;
                        }
                    }
                    Guardian.request(config, fullUrl, method, newBody, response -> {
                        if (response.getCode() == 200){
                            try {
                                JSONObject result = new JSONObject();
                                result.put(PARAMS_CODE, response.getCode());
                                result.put(PARAMS_DATA, response.getData());
                                callback.onSuccess(result);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            try {
                                JSONObject result = new JSONObject();
                                result.put(PARAMS_ERROR_MESSAGE, response.getMessage());
                                callback.onFail(result);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }.start());
        } catch (Exception e) {
            callback.onFail();
            e.printStackTrace();
        }
    }

    private void getIdToken(ICallback callback){
        UserInfo userInfo = Authing.getCurrentUser();
        String idToken = userInfo.getIdToken();
        try {
            JSONObject params = new JSONObject();
            params.put(PARAMS_ID_TOKEN, idToken);
            callback.onSuccess(params);
        } catch (JSONException e) {
            callback.onFail();
            e.printStackTrace();
        }
    }

}
