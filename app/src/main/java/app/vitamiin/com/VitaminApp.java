package app.vitamiin.com;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.kakao.auth.KakaoSDK;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import androidx.appcompat.app.AppCompatActivity;
import app.vitamiin.com.login.KaKaoSDKAdapter;


public class VitaminApp extends Application {

    private static VitaminApp _instance;
    private static AppCompatActivity m_act = null;

    public static final String TAG = VitaminApp.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;

        initImageLoader(getApplicationContext());

        FacebookSdk.sdkInitialize(this);
        KakaoSDK.init(new KaKaoSDKAdapter());
    }

    public static AppCompatActivity getCurrentActivity() {
        return m_act;
    }

    public static void setCurrentActivity(AppCompatActivity currentActivity) {
        m_act = currentActivity;
    }

    public void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    public void setMainAct(BaseActivity act) {
        m_act = act;
    }

    public static synchronized VitaminApp getInstance() {
        return _instance;
    }
}
