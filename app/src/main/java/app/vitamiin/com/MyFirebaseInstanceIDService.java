package app.vitamiin.com;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Interpolator;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import app.vitamiin.com.common.Const;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;

/**
 * Created by dong8 on 2017-03-27.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService implements OnParseJSonListener {
    private static final String TAG = "MyFirebaseIIDService";
    NetUtil mNetUtil;
    NetUtil.connectAndgetServer mNetUtilConnetServer;
    public final String PROPERTY_FCM_TOKEN = "fcm_token";
    public final String PROPERTY_APP_VERSION = "appVersion";
    private TokenRefreshCallback mTokenRefreshCB = null;
//    public SharedPreferences mpref;

    public interface TokenRefreshCallback {
        void TokenCompare_SaveToServer(String strToken);
    }

    public void setTokenRefreshCallback(TokenRefreshCallback cb){
        mTokenRefreshCB = cb;
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {      // Get updated InstanceID token.
//        mpref = getSharedPreferences("Vitamiin", 0);
//        mpref = getBaseContext().getSharedPreferences("Vitamiin", 0);
//        mpref=getApplication().getSharedPreferences("Vitamiin", MODE_PRIVATE);

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
//         If you want to send messages to this application instance or
//         manage this apps subscriptions on the server side, send the
//         Instance ID token to your app server.


        Const.g_fcmToken = refreshedToken;
//        storeFcmTokenToSharedPref(MyFirebaseInstanceIDService.this, refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param refreshedToken The new token.
     */
    private void sendRegistrationToServer(String refreshedToken) {
        mNetUtil = new NetUtil();
        mNetUtilConnetServer = mNetUtil.new connectAndgetServer(this);
        if(mTokenRefreshCB!=null && refreshedToken!=null) {
            mTokenRefreshCB.TokenCompare_SaveToServer(refreshedToken);
        }
        else
            mNetUtilConnetServer.connectServer(this, handler, NetUtil.apis_REG_FCMTOKEN_TO_SERVER, refreshedToken);
    }

    private void storeFcmTokenToSharedPref(Context context, String TokenToSave) {
        int appVersion = getAppVersion(context);
//        SharedPreferences.Editor editor = mpref.edit();
//        editor.putString(PROPERTY_FCM_TOKEN, TokenToSave);
//        editor.putInt(PROPERTY_APP_VERSION, appVersion);
//        editor.apply();
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

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Net.THREAD_REQUEST_END) {
                HttpRequester.getInstance().stopNetThread();

                int resultCode = HttpRequester.getInstance().getResultCode();
                String strMsg = HttpRequester.getInstance().getResultMsg();

                if (resultCode == Net.CONNECTION_SUCCSES) {}
                else {
                    if (!"".equals(strMsg)) {}
                }
            }
        }
    };

    @Override
    public void onParseJSon(JSONObject j_source) {
        try {
            JSONObject result = j_source.getJSONObject(Net.NET_VALUE_RESULT);
            Const.g_nDeviceNo = result.getInt(Net.NET_VALUE_DEVICE_NO);
        } catch (JSONException e) {e.printStackTrace();}
    }
}