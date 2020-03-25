package app.vitamiin.com;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import app.vitamiin.com.common.Const;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;
import app.vitamiin.com.login.LoginActivity;
import io.fabric.sdk.android.Fabric;

public class IntroActivity extends AppCompatActivity implements OnParseJSonListener, MyFirebaseInstanceIDService.TokenRefreshCallback {
    Context m_context;
    NetUtil mNetUtil;
    NetUtil.connectAndgetServer mNetUtilConnetServer;
    public final String PROPERTY_FCM_TOKEN = "fcm_token";
    public final String PROPERTY_APP_VERSION = "appVersion";
    public SharedPreferences mpref;

    private FirebaseInstanceId m_firebaseInstance;
    private MyFirebaseInstanceIDService m_myfirebaseInstanceIDservice;
    MyFirebaseInstanceIDService.TokenRefreshCallback m_Cb;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private String m_strUserType, m_strUseremail, m_strUserid, m_strUserPass, refreshedToken;
    private Boolean updateTokenbyNewOneAlived = false;
    private RotateLoading rotateLoading;

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        Fabric.with(this, new Answers());
        setContentView(R.layout.activity_intro);

        m_context = this;
        mNetUtil = new NetUtil();
        mNetUtilConnetServer = mNetUtil.new connectAndgetServer(this);
        mpref = getSharedPreferences("Vitamiin", 0);

        m_Cb = this;
        m_firebaseInstance = FirebaseInstanceId.getInstance();
        m_myfirebaseInstanceIDservice = new MyFirebaseInstanceIDService();
        m_myfirebaseInstanceIDservice.setTokenRefreshCallback(m_Cb);

//////////////////////////Find FCM Token From Shared Preference//////////////////////////////
        Const.g_fcmToken = getFcmTokenFromSharedPref(this);

//////////////////////////Find Account Data From Shared Preference//////////////////////////////
        m_strUserType = mpref.getString("type", "");
        m_strUseremail = mpref.getString("email", "");
        m_strUserid = mpref.getString("id", "");
        m_strUserPass = mpref.getString("pass", "");

//        rotateLoading = (RotateLoading) findViewById(R.id.rotateloading);
//        rotateLoading.start();

//////////////////////////whatever.. request FCM token/////////////////////////
//        if (checkPlayServices()) {
            if (!m_strUserType.equals("") && !m_strUseremail.equals("") && !m_strUserid.equals("")) {
                loginInBackground();
            } else { //Shared Preference 에서 account 정보가 없기 때문에 토큰이 있던 없던 상관없이 LoginActivity로 넘어간다. by 동현
                if (!m_strUserType.equals("") && !m_strUseremail.equals("") && !m_strUserid.equals("")) {
                    Intent intent = new Intent(m_context, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(m_context, TutorialActivity.class);                 //Intent intent = new Intent(m_context, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
//        }else {
//        }
    }

    private void loginInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                m_myfirebaseInstanceIDservice.setTokenRefreshCallback(m_Cb);
                m_myfirebaseInstanceIDservice.onTokenRefresh();

                refreshedToken = m_firebaseInstance.getToken();
                return refreshedToken;
            }
            @Override
            protected void onPostExecute(String newToken) {
                TokenCompare_SaveToServer(newToken);
            }
        }.execute(null, null, null);
    }

    @Override
    public void TokenCompare_SaveToServer(String newToken){     //여기 오는 것 자체가 account 정보가 있다는 것
        if (newToken != null && !newToken.isEmpty() && newToken.length() != 0) {        //새 토큰이 유효하면 이것을 저장보내기. by 동현
            Const.g_fcmToken = newToken;
            storeFcmTokenToSharedPref(IntroActivity.this, Const.g_fcmToken);
            mNetUtilConnetServer.connectServer(m_context, handler, NetUtil.apis_REG_FCMTOKEN_TO_SERVER, Const.g_fcmToken);
        } else{
            if (Const.g_fcmToken != null && !Const.g_fcmToken.isEmpty() && Const.g_fcmToken.length() != 0) {        //새 토큰은 의미가 없고, 기존 토큰이 유효하면..  by 동현
                storeFcmTokenToSharedPref(IntroActivity.this, Const.g_fcmToken);
                mNetUtilConnetServer.connectServer(m_context, handler, NetUtil.apis_REG_FCMTOKEN_TO_SERVER, Const.g_fcmToken);
            } else {            //두 토큰이 다 의미가 없으면(비어있거나 null) 토큰 저장 과정 없이 그냥 LoginActivity를 시작한다. 쓸쓸.. by 동현
//                Intent intent = new Intent(m_context, LoginActivity.class);
//                startActivity(intent);
            }
        }
//        updateTokenbyNewOneAlived = true;
    }

    private String getFcmTokenFromSharedPref(Context context) {
        String FcmTokenFromSharedPref = mpref.getString(PROPERTY_FCM_TOKEN, "");
        if (FcmTokenFromSharedPref.isEmpty()) {
            FcmTokenFromSharedPref = "";
            Log.i("reviewsee", "Fcm token not found from shared preference.");
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = mpref.getInt(PROPERTY_APP_VERSION, getAppVersion(context));
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i("reviewsee", "App version changed.");
            FcmTokenFromSharedPref = "";
        }
        return FcmTokenFromSharedPref;
    }

    private void storeFcmTokenToSharedPref(Context context, String TokenToSave) {
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = mpref.edit();
        editor.putString(PROPERTY_FCM_TOKEN, TokenToSave);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
        Log.i("reviewsee", "Saving TokenToSave on app version " + appVersion);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {  // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    @Override
    public void onParseJSon(JSONObject j_source) {
        try {
            JSONObject result = j_source.getJSONObject(Net.NET_VALUE_RESULT);
            Const.g_nDeviceNo = result.getInt(Net.NET_VALUE_DEVICE_NO);
        }
        catch (JSONException e) {e.printStackTrace();}
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Net.THREAD_REQUEST_END) {
                processForNetEnd();}
        }
    };

    private void processForNetEnd() {
//        rotateLoading.stop();
        HttpRequester.getInstance().stopNetThread();

        int resultCode = HttpRequester.getInstance().getResultCode();
        String strMsg = HttpRequester.getInstance().getResultMsg();

        if (resultCode == Net.CONNECTION_SUCCSES) {
//            if(updateTokenbyNewOneAlived) {
//                finish();
//            }else{
                checkNextState();
//            }
        } else {
            if (!"".equals(strMsg))
                Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();
        }
    }

    private void checkNextState() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!m_strUserType.equals("") && !m_strUseremail.equals("") && !m_strUserid.equals("")) {
                    Intent intent = new Intent(m_context, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(m_context, TutorialActivity.class);                 //Intent intent = new Intent(m_context, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 1);
    }
}
