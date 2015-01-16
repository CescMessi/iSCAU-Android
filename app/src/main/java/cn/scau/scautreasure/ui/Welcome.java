package cn.scau.scautreasure.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.rest.RestService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import cn.scau.scautreasure.AppContext;
import cn.scau.scautreasure.R;
import cn.scau.scautreasure.api.SplashApi;
import cn.scau.scautreasure.guideview.GuideView_;
import cn.scau.scautreasure.helper.PackageHelper;
import cn.scau.scautreasure.helper.SplashHelper;
import cn.scau.scautreasure.model.SplashModel;
import cn.scau.scautreasure.service.ActivityCountService_;
import cn.scau.scautreasure.service.AlertClassSerice_;
import cn.scau.scautreasure.service.FoodShopService_;
import cn.scau.scautreasure.service.NetworkStatusService_;
import cn.scau.scautreasure.service.UpLoadUsersService_;

/**
 * User: special
 * Date: 13-9-28
 * Time: 下午12:36
 * Mail: specialcyci@gmail.com
 */
@EActivity(R.layout.welcome)
public class Welcome extends Activity {

    @App
    AppContext app;
    @RestService
    SplashApi api;
    @Bean
    SplashHelper splashHelper;

    @Bean
    PackageHelper packageHelper;

    @ViewById
    TextView tv_version;
    @Pref
    cn.scau.scautreasure.AppConfig_ appConfig;
    boolean wantToExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //开启课程提醒
        if (appConfig.isAlertClass().get()) {
//            AlertClassSerice_.intent(this).start();
            //注解那个方法是绑定的
            Intent sevice = new Intent(this, AlertClassSerice_.class);
            startService(sevice);
        }

        //拿外卖
        FoodShopService_.intent(this).start();

        //上传用户资料
        if (!appConfig.hasUpdatedUsers().get() && !appConfig.userName().equals("")) {
            UpLoadUsersService_.intent(getApplicationContext()).start();
        }

        //开启网络监控
        Intent networkService = new Intent(this, NetworkStatusService_.class);
        startService(networkService);
       /* //校园活动是否有更新
        ActivityCountService_.intent(getApplicationContext()).start();*/

        //判断设备类型
        if (isPad()) {
            if(appConfig.forceMobile().get()){ //用户认为是手机，则直接认为是手机
                appConfig.isThePad().put(false);
            }else {
                appConfig.isThePad().put(true);
            }
        }

        splashHelper.initHelper(api, getApplication());
        //SplashHelper splashHelper = new SplashHelper(getApplicationContext());
        SplashModel splashModel = splashHelper.getSuitableSplash();
        if (splashModel != null) {
            Intent splash = new Intent(this, Splash_.class);
            splash.putExtra("title", splashModel.getEdit_time()+"");
            startActivity(splash);
            wantToExit = true;
            finish();
        }
            Log.d("splash","start");
            splashHelper.loadData();

    }

    @AfterViews
    void init() {
        if (wantToExit) return;
        String originText = tv_version.getText().toString();
        String versionName = packageHelper.getAppVersionName();
        tv_version.setText(originText + " " + versionName);
        close();
    }

    @UiThread(delay = 2000)
    protected void close() {
        if (hasSetAccount()) {
            Main_.intent(this).start();
        } else {
            Login_.intent(this).runMainActivity(true).start();
        }

        /*if (appConfig.isFirstStartApp().get()) {
            GuideView_.intent(this).start();
        }*/
        finish();
    }

    private boolean hasSetAccount() {
        return app.userName != null &&
                (app.eduSysPassword != null || app.libPassword != null || app.cardPassword != null);
    }

    /**
     * 判断是否为平板
     *
     * @return
     */
    private boolean isPad() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        // 屏幕宽度
        float screenWidth = display.getWidth();
        // 屏幕高度
        float screenHeight = display.getHeight();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        // 屏幕尺寸
        double screenInches = Math.sqrt(x + y);
        // 大于6尺寸则为Pad
        if (screenInches >= 6.0) {
            Log.i("设备类型:", "平板--尺寸:" + String.valueOf(screenInches));
            return true;
        }
        Log.i("设备类型:", "手机--尺寸:" + String.valueOf(screenInches));
        return false;
    }
}