接入 Authing + FinClip + Authing-FinClip-SDK，快速实现身份认证系统+小程序开发+小程序网络请求和微信登录能力



1.创建 Authing 和 FinClip 应用

Authing 应用注册地址：https://www.authing.cn/

Finclip 应用注册地址：https://www.finclip.com/



2.添加依赖

在工程的 build.gradle 中添加 maven 仓库的地址，添加 Kotlin 的 gradle 插件，完整配置如下：

```groovy
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath "com.android.tools.build:gradle:3.5.2"
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.61"
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven {
            url "https://gradle.finogeeks.club/repository/applet/"
            credentials {
                username "applet"
                password "123321"
            }
        }
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```

在 gradle 文件的 dependencies 中添加依赖 (guardfinclipsdk.aar 可在工程中获取)

```groovy
    implementation files('libs/guardfinclipsdk.aar')
    implementation 'cn.authing:guard:+'
    implementation 'com.finogeeks.lib:finapplet:+'
    implementation "com.tencent.tbs.tbssdk:sdk:+"
```

需要在 App module 下的`build.gradle` 中增加 `doNotStrip` 配置：

```groovy
packagingOptions {
    // libsdkcore.so、libyuvutil.so是被加固过的，不能被压缩，否则加载动态库时会报错
    doNotStrip "*/x86/libsdkcore.so"
    doNotStrip "*/x86_64/libsdkcore.so"
    doNotStrip "*/armeabi/libsdkcore.so"
    doNotStrip "*/armeabi-v7a/libsdkcore.so"
    doNotStrip "*/arm64-v8a/libsdkcore.so"

    doNotStrip "*/x86/libyuvutil.so"
    doNotStrip "*/x86_64/libyuvutil.so"
    doNotStrip "*/armeabi/libyuvutil.so"
    doNotStrip "*/armeabi-v7a/libyuvutil.so"
    doNotStrip "*/arm64-v8a/libyuvutil.so"
}
```

如果您项目配置了混淆，为了避免SDK中部分不能被混淆的代码被混淆，请配置

```groovy
-keep class com.finogeeks.** {*;}
```



3.在 Application 中初始化

```java
FinAppConfig config = new FinAppConfig.Builder()
        .setSdkKey("FinClip App SDK Key信息")   // SDK Key
        .setSdkSecret("FinClip App SDK Secret信息")   // SDK Secret
        .setApiUrl("服务器地址")   // 服务器地址
        .setApiPrefix("/api/v1/mop/")   // 服务器接口请求路由前缀
        .setEncryptionType("加密方式")   // 加密方式，国密:SM，md5: MD5
        .build();
AuthingFinClip.getInstance().init(this, "your_authing_app_id", config, new FinCallback<Object>() {
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
```



4.初始化登录界面

在合适的地方调用如下代码快速进入 Authing Guard 内置登录界面

```
AuthFlow.start(this);
```

登录成功之后，获取用户信息可调用：

```java
UserInfo userInfo = Authing.getCurrentUser();
```



5.启动小程序

```java
FinAppClient.INSTANCE.getAppletApiManager().startApplet(this, "6244175278c1a7000142b2c5");
```



6.小程序调用原生网络请求 API 示例

在小程序工程的根目录创建 `FinClipConf.js` 文件，在 `FinClipConf.js ` 中配置原生提供的接口

```javascript
module.exports = {
    extApi:[
        {
            name: 'guardRequest', //使用 guardRequest 可以调用 Authing 原生 API
            params: {
                url: '',
                body: {},
                method: '',
            }   
        },
        {
            name: 'encryptPassword',  //密码加密，调用密码相关 API 时需要先调用此方法密码加密
            params: {
                password: '',
            }
        }
    ]
}
```

新建 guard.js 定义业务网络请求接口

```javascript
//获取用户信息
function getCurrentUser() {
  return {
    url: '/api/v2/users/me',
    method: 'GET',
    body: {}
  }
}

module.exports = {
  getCurrentUser
}
```

在小程序的 app.js 中导入定义的网络请求接口

```javascript
const guard = require('./guard/guard.js')
// app.js
App({
  guard:guard,
  onLaunch() {
    // 展示本地存储能力
    const logs = ft.getStorageSync('logs') || []
    logs.unshift(Date.now())
    ft.setStorageSync('logs', logs)
  }
})
```

调用业务接口获取业务数据

```javascript
getCurrentUser() {
    var _this = this
    let getUser = app.guard.getCurrentUser();
    ft.guardRequest({
      url: getUser.url,
      body: getUser.body,
      method: getUser.method,
      success: function (res) {
      _this.setData({
	    photo:res.data.photo,
            userInfoItems:[
              res.data.nickname,
              res.data.name,
              res.data.username,
              res.data.phone,
              res.data.email
            ]
        })
        console.log("getCurrentUser success");
      },
      fail: function (res) {
        console.log("getCurrentUser fail");
        console.log(JSON.stringify(res["errMsg"]));
      }
    });
```



7.如果小程序想实现微信授权登录，按照 [Authing 微信登录接入文档](https://docs.authing.cn/v2/reference/sdk-for-android/social/wechat.html) 进行相关配置，即可直接在小程序端调用 wx.login 实现微信授权登录并且返回 token




参考资料

Authing Android 接入文档：https://docs.authing.cn/v2/reference/sdk-for-android/

FinClip Android 接入文档：https://www.finclip.com/mop/document/runtime-sdk/android/android-integrate.html

