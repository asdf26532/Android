package com.kakaologin;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class KakaoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        KakaoSdk.init(this, "a498e92bdd18a69b014bc63043ca5a49");

    }
}
