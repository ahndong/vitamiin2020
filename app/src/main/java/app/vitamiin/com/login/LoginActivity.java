package app.vitamiin.com.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.IntroActivity;
import app.vitamiin.com.NoticeDialog;
import app.vitamiin.com.R;
import app.vitamiin.com.common.Const;
import app.vitamiin.com.common.UserHealthBase;
import app.vitamiin.com.common.UserHealthDetail;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.common.Util;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.home.ManageProfileActivity;
import app.vitamiin.com.home.MainActivity;
import app.vitamiin.com.home.PresetAccountLayout;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;

import static com.kakao.util.helper.Utility.getAppVersion;

public class LoginActivity extends BaseActivity implements View.OnClickListener, OnParseJSonListener {

    private CallbackManager callbackManager;
    private SessionCallback sessionCallback;
    private LoginManager m_manager;

    public final String PROPERTY_FCM_TOKEN = "fcm_token";
    public final String PROPERTY_APP_VERSION = "appVersion";

    private static final List<String> Read_PERMISSIONS = Arrays.asList("public_profile", "email", "user_birthday");
    private static final List<String> Read_and_Pub_PERMISSIONS = Arrays.asList("public_profile", "email", "user_birthday", "publish_actions");
    private static final List<String> Pub_PERMISSIONS = Collections.singletonList("publish_actions");

    Context m_context;
    Intent m_MainAct_intent = null;
    NetUtil mNetUtil;
    NetUtil.connectAndgetServer mNetUtilConnetServer;
    int m_nConnectType = NetUtil.GET_PRESET_ACCOUNT;

    boolean isloggedIn = false;
    // 종료 기발
    private boolean m_bFinish = false;
    EditText m_edtUser, m_edtPassword;
    HorizontalScrollView m_scvPreset;
    LinearLayout m_llyPreset;

    UserHealthBase m_userHealthBase = new UserHealthBase();
    UserHealthDetail m_userHealthDetail = new UserHealthDetail();

    int m_nResultType = 0;
    private String m_strUserType = "";
    private String m_strUseremail = "";
    private String m_strUserid = "";
    private String m_strUserPass = "";

    private String m_strUserName = "";
    private String m_strUserSex = "";
    private String m_strBirth = "";
    private String urlSnsImg = "";
    public String currentVersion;

    private int index_preset = 0;
    ArrayList<PresetAccount> listPresetAcc = new ArrayList<>();

    private class PresetAccount{
        private String id="";
        private String pw="";
        private String name="";
        private String nick="";
    }
    Set setID, setPW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_context = this;
        setContentView(R.layout.activity_login);
        mNetUtil = new NetUtil();
        mNetUtilConnetServer = mNetUtil.new connectAndgetServer(this);

        try {
            currentVersion = this.getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            localNameNotFoundException.printStackTrace();
        }

//            PackageInfo info = getPackageManager().getPackageInfo(        //logcat에 해시값 출력해보기
//                    "app.vitamiin.com",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }

        String permissions = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        ActivityCompat.requestPermissions(this, new String[] {permissions}, PackageManager.PERMISSION_GRANTED);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        initView();            //init페북은 클릭 시에 initialize 자동 진행 됨. byt 동현
        initkakao();
        initFacebook();
        LoginInUtil();
    }

    private void initView() {
        m_edtUser = (EditText) findViewById(R.id.edt_user);
        m_edtPassword = (EditText) findViewById(R.id.edt_password);

        findViewById(R.id.tv_login).setOnClickListener(this);
        findViewById(R.id.imv_kakao_login).setOnClickListener(this);
        findViewById(R.id.imv_facebook_login).setOnClickListener(this);
        findViewById(R.id.tv_register).setOnClickListener(this);
        findViewById(R.id.tv_find).setOnClickListener(this);

        m_scvPreset = (HorizontalScrollView) findViewById(R.id.scv_presetAccount);
        m_llyPreset = (LinearLayout) findViewById(R.id.lly_presetAccount);
        if(currentVersion.contains("manager")){
            m_scvPreset.setVisibility(View.VISIBLE);
            mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, "");
            ///////////////////////////////////////////////////////////////////////////////////////
//            listPresetAcc.clear();
//
//            SharedPreferences prefpres = getSharedPreferences("VitamiinPreset", 0);
//            setID = prefpres.getStringSet("presetID", null);
//            setPW = prefpres.getStringSet("presetPW", null);
//            if (setID != null && setPW != null){
//                ArrayList<String> listID = new ArrayList<>(setID);
//                ArrayList<String> listPW = new ArrayList<>(setPW);
//
//                for(int j = 0;j<listID.size();j++) {
//                    PresetAccount OnePreset = new PresetAccount();
//                    OnePreset.id = listID.get(j);
//                    OnePreset.pw = listPW.get(j);
//
//                    listPresetAcc.add(OnePreset);
//                }
//            } else {
////                setID.clear();
////                setPW.clear();
//            }
//
//            listPresetAcc.add(new PresetAccount());
//
//            m_llyPreset.removeAllViews();
//
//            for (int i = 0; i < listPresetAcc.size(); i++) {
//                final PresetAccountLayout layout = new PresetAccountLayout(this, i, listPresetAcc.get(i).id, listPresetAcc.get(i).pw, m_listener, m_listener2);
//                m_llyPreset.addView(layout);
//            }
            //////////////////////////////////////////////////////////////////////////////////
        }else {
            m_scvPreset.setVisibility(View.GONE);
        }
    }

    View.OnClickListener m_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_login_preset:
                    index_preset = (Integer) v.getTag();

                    m_edtUser.setText(listPresetAcc.get(index_preset).id);
                    m_edtPassword.setText(listPresetAcc.get(index_preset).pw);

                    doLogin();
                    break;
            }
        }
    };

    private void doLogin(){
        findViewById(R.id.tv_login).performClick();
    }

    View.OnFocusChangeListener m_listener2 = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean bool) {
            switch (v.getId()) {
                case R.id.edt_id:
                    index_preset = (Integer) v.getTag();
                    listPresetAcc.get(index_preset).id = ((EditText)m_llyPreset.getChildAt(index_preset).findViewById(R.id.edt_id)).getText().toString();

                    m_edtUser.setText(listPresetAcc.get(index_preset).id);
                    m_edtPassword.setText(listPresetAcc.get(index_preset).pw);
//                    if(index_preset == listPresetAcc.size() - 1){
//                        listPresetAcc.add(new PresetAccount());
//                        m_llyPreset.addView(new PresetAccountLayout(m_context, index_preset + 1, "", "", "", "", m_listener, m_listener2));
//                    }
                    break;
                case R.id.edt_pw:
                    index_preset = (Integer) v.getTag();
                    listPresetAcc.get(index_preset).pw = ((EditText)m_llyPreset.getChildAt(index_preset).findViewById(R.id.edt_pw)).getText().toString();

                    m_edtUser.setText(listPresetAcc.get(index_preset).id);
                    m_edtPassword.setText(listPresetAcc.get(index_preset).pw);
//                    if(index_preset == listPresetAcc.size() - 1){
//                        listPresetAcc.add(new PresetAccount());
//                        m_llyPreset.addView(new PresetAccountLayout(m_context, index_preset + 1, "", "", "", "", m_listener, m_listener2));
//                    }
                    break;
            }
        }
    };

    private void LoginInUtil() {
        if(getIntent().getBooleanExtra("turnBack", false)){
            UserManager.getInstance().saveData(this,"","","","");
            UserManager.getInstance().releaseUserData();
        }else{
            SharedPreferences pref = getSharedPreferences("Vitamiin", 0);
            m_strUserType = pref.getString("type", "");
            m_strUseremail = pref.getString("email", "");
            m_strUserid = pref.getString("id", "");
            m_strUserPass = pref.getString("pass", "");

            if (!m_strUserType.equals("") && !m_strUseremail.equals("") && !m_strUserid.equals("")) {
                if(m_strUserType.equals("emailaccount")){
                    m_edtUser.setText(m_strUserid);
                    m_edtPassword.setText(m_strUserPass);
                    showProgress();
                    if(!Const.g_fcmToken.equals(""))
                        storeFcmTokenToSharedPref(LoginActivity.this, Const.g_fcmToken);

                    m_nConnectType = -1;
                    isloggedIn = mNetUtil.LoginInUtil(m_context, m_context, handler, m_strUserType, m_strUseremail, m_strUserid, m_strUserPass);
                }
                if(m_strUserType.equals("kakao")){
                    Session.getCurrentSession().checkAndImplicitOpen();
                }
                if(m_strUserType.equals("facebook")){
                    showProgress();
                    if(!Const.g_fcmToken.equals(""))
                        storeFcmTokenToSharedPref(LoginActivity.this, Const.g_fcmToken);
                    m_nConnectType = -1;
                    isloggedIn = mNetUtil.LoginInUtil(m_context, m_context, handler, m_strUserType, m_strUseremail, m_strUserid, m_strUserPass);

//                    m_manager.logInWithReadPermissions(this, Read_PERMISSIONS);
//                    m_manager.logInWithPublishPermissions(this, Arrays.asList("public_profile", "email", "user_birthday"));
//                    findViewById(R.id.login_button).performClick();
                }
            }
        }
    }

    private void initkakao() {
        sessionCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(sessionCallback);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            Log.d("test", "onSessionOpened 성공");
            requestMe();        }
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.d("error", "Session Fail Error is " + exception.getMessage());        }
    }

    public void requestMe(){
        List<String> propertyKeys = new ArrayList<>();
        propertyKeys.add("kaccount_email");
        propertyKeys.add("nickname");
        propertyKeys.add("profile_image");
        propertyKeys.add("thumbnail_image");

        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Toast.makeText(LoginActivity.this, "Session Closed Error is " + errorResult.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(UserProfile userProfile) {   //카카오톡으로 간편로그인 클릭하면 이곳 실행 by 동현
                Logger.d("UserProfile : " + userProfile);

                showProgress();
//                Toast.makeText(LoginActivity.this, "사용자 이름은 " + userProfile.getNickname(), Toast.LENGTH_SHORT).show();

                m_strUserid = String.valueOf(userProfile.getId());
                m_strUseremail = userProfile.getEmail();
                m_strUserName = userProfile.getNickname();
                urlSnsImg = userProfile.getProfileImagePath();

                m_strUserType = "kakao";
                if(!Const.g_fcmToken.equals(""))
                    storeFcmTokenToSharedPref(LoginActivity.this, Const.g_fcmToken);
                m_nConnectType = -1;
                isloggedIn = mNetUtil.LoginInUtil(m_context, m_context, handler, m_strUserType, m_strUseremail, m_strUserid, m_strUserid);
            }

            @Override
            public void onNotSignedUp() {
            }
        }, propertyKeys, false);
    }

    public void initFacebook(){
        callbackManager = CallbackManager.Factory.create();

        m_manager = LoginManager.getInstance();
        m_manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                m_strUserPass = loginResult.getAccessToken().getToken();

//                m_manager.logInWithPublishPermissions(LoginActivity.this, Pub_PERMISSIONS);

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject user, GraphResponse response) {
                                if (response.getError() == null) {
                                    com.facebook.Profile fbProfile = com.facebook.Profile.getCurrentProfile();
                                    String facebookId = "";
                                    if (fbProfile != null && fbProfile.getId().length() > 0) {
                                        facebookId = fbProfile.getId();
                                    }
                                    if (facebookId.isEmpty()) {
//                                        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Collections.singletonList("email"));
                                        m_manager.logInWithReadPermissions(LoginActivity.this, Read_PERMISSIONS);
                                    } else {
                                        try{
                                            m_strUserType = "facebook";
                                            UserManager.getInstance().member_type = "facebook";
                                            m_strUserid = fbProfile.getId();       //숫자냐? yes by 동현
                                            m_strUserName = fbProfile.getName();
                                            m_strUseremail  = user.getString("email");
                                            m_strUserSex = user.getString("gender");

                                            String[] arrBirth;
                                            if(!user.isNull("birthday")) {
                                                arrBirth = user.getString("birthday").split("/");
                                                m_strBirth = arrBirth[2] + "-" + arrBirth[0] + "-" + arrBirth[1];
                                            }

                                            urlSnsImg = fbProfile.getProfilePictureUri(160, 160).toString();
//                                            urlSnsImg = fbProfile.getLinkUri() + "picture?type=large";
                                        } catch (Exception e) {e.printStackTrace();
                                        }
                                    }
                                    showProgress();
                                    if(!Const.g_fcmToken.equals(""))
                                        storeFcmTokenToSharedPref(LoginActivity.this, Const.g_fcmToken);
                                    m_nConnectType = -1;
                                    isloggedIn = mNetUtil.LoginInUtil(m_context, m_context, handler, m_strUserType, m_strUseremail, m_strUserid, m_strUserPass);
                                }
                            }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }
            @Override
            public void onCancel() {
                m_manager.logOut();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("test", "Error: " + error);
            }
        });
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Net.THREAD_REQUEST_END)
                processForNetEnd();}
    };

    private void processForNetEnd() {
        try {
            closeProgress();
            HttpRequester.getInstance().stopNetThread();
            int resultCode = HttpRequester.getInstance().getResultCode();
            String strMsg = HttpRequester.getInstance().getResultMsg();

//            if(_jResult==null) {
//                  m_nConnectType = -1;
//                isloggedIn = mNetUtil.LoginInUtil(m_context, m_context, handler, m_strUserType, m_strUseremail, m_strUserid, m_strUserPass);
//                return;
//            }

            if (resultCode == Net.CONNECTION_SUCCSES) {   //로그인 성공         // 이후 처리
                if (_jResult == null) return;
                if(m_nConnectType == NetUtil.GET_PRESET_ACCOUNT){
                    JSONArray result = _jResult.getJSONArray(Net.NET_VALUE_RESULT);
                    listPresetAcc.clear();

                    for (int i = 0; i < result.length(); i++) {
                        JSONObject obj_preset = result.getJSONObject(i);
                        PresetAccount OnePreset = new PresetAccount();
                        OnePreset.id = obj_preset.getString("id");
                        OnePreset.pw = obj_preset.getString("pw");
                        OnePreset.name = obj_preset.getString("name");
                        OnePreset.nick = obj_preset.getString("nick");

                        listPresetAcc.add(OnePreset);
                    }
                    listPresetAcc.add(new PresetAccount());

//                    m_llyPreset.removeAllViews();

                    for (int i = 0; i < listPresetAcc.size(); i++) {
                        final PresetAccountLayout layout = new PresetAccountLayout(this, i, listPresetAcc.get(i).id, listPresetAcc.get(i).pw, listPresetAcc.get(i).name, listPresetAcc.get(i).nick, m_listener, m_listener2);
                        m_llyPreset.addView(layout);
                    }
                } else {
                    JSONObject result = _jResult.getJSONObject(Net.NET_VALUE_RESULT);
                    m_nResultType = result.getInt(Net.NET_VALUE_TYPE); //

                    UserManager.getInstance().event_num = _jResult.getJSONObject("result").getInt("event_num");

                    if (m_nResultType == 0) {
                        UserManager.getInstance().saveData(this, m_strUserType, m_strUseremail, m_strUserid, m_strUserPass);

                        UserManager.getInstance().HealthBaseInfo.add(m_userHealthBase);
                        UserManager.getInstance().HealthDetailInfo.add(m_userHealthDetail);

                        UserManager.getInstance().member_type = m_strUserType;
                        UserManager.getInstance().member_id = m_strUseremail;
                        UserManager.getInstance().member_name = m_strUserName;

                        UserManager.getInstance().arr_profile_photo_URL.add(null);
                        UserManager.getInstance().arr_profile_photo_file.add(null);
                        UserManager.getInstance().arr_profile_photo_bitmap.add(null);
                        UserManager.getInstance().arr_profile_photo_resID.add(-1);
                        if(mNetUtil.transProfile_ObjToUM(this,0, result)){
                            UserManager.getInstance().HealthBaseInfo.get(0).member_no = UserManager.getInstance().member_no;
                            UserManager.getInstance().HealthBaseInfo.get(0).member_name = UserManager.getInstance().member_name;
                            UserManager.getInstance().HealthBaseInfo.get(0).member_nick_name = UserManager.getInstance().member_nick_name;
                            UserManager.getInstance().HealthDetailInfo.get(0).member_no = UserManager.getInstance().member_no;
                            UserManager.getInstance().HealthDetailInfo.get(0).member_name = UserManager.getInstance().member_name;
                            UserManager.getInstance().HealthDetailInfo.get(0).member_nick_name = UserManager.getInstance().member_nick_name;

                            m_MainAct_intent = new Intent(this, MainActivity.class);
                            startActivity(m_MainAct_intent);
                            finish();
                        }
                    }
                    else if(m_nResultType==1){  //일반 이메일 로그인 실패
                        String message = null;
                        switch (m_strUserType){
                            case ("facebook"):
                                message = "FACE BOOK";
                                break;
                            case ("kakao"):
                                message = "KAKAKO TALK";
                                break;
                            case ("emailaccount"):
                                message = "E-MAIL";
                                break;
                        }
                        new NoticeDialog(this, "SNS자동 로그인 실패 알림", message + " 자동 로그인이 실패하였습니다. 다시 시도해주세요..","잠시 후에 자동으로 닫힙니다.", true).show();
                        UserManager.getInstance().saveData(this, "", "",  "", "");
                    }
                    else if(m_nResultType==2){  //페이스북 연결 인증했으나 기존회원이 아니어서 레지스터액티비티로 가야하는 분기 by 돟련
                        try{/////////  페북 계정 성공, 아직 회원은 아님, 가입 절차(기본 설문) 진행으로!  /////////
                            UserManager.getInstance().member_type = "facebook";
                            UserManager.getInstance().member_id = m_strUseremail;
                            UserManager.getInstance().member_name = m_strUserName;
                            m_userHealthBase.member_name = UserManager.getInstance().member_name;
                            m_userHealthDetail.member_name = UserManager.getInstance().member_name;
                            UserManager.getInstance().member_fb_id = m_strUserid;
                            UserManager.getInstance().fb_token = m_strUserPass;
                            UserManager.getInstance().member_sex =  m_strUserSex.equals("male") ? 0 : 1;
                            m_userHealthBase.member_sex = m_strUserSex.equals("male") ? 0 : 1;
                            UserManager.getInstance().member_birth = m_strBirth;
                            m_userHealthBase.member_birth = m_strBirth;

                            UserManager.getInstance().arr_profile_photo_URL.add(null);
                            UserManager.getInstance().arr_profile_photo_file.add(null);
                            UserManager.getInstance().arr_profile_photo_bitmap.add(null);
                            UserManager.getInstance().arr_profile_photo_resID.add(-1);
                            mNetUtil.setProfilePhoto(this, 0, urlSnsImg);

                            Intent intent = new Intent(LoginActivity.this, ManageProfileActivity.class);
                            intent.putExtra("fam_index", 0);
                            intent.putExtra("HealthBase", m_userHealthBase);
                            intent.putExtra("HealthDetail", m_userHealthDetail);
                            intent.putExtra("registerSNS", true);

                            setResult(RESULT_OK);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {e.printStackTrace();}
                    }
                    else if(m_nResultType==3){  //카톡 연결 인증했으나 기존회원이 아니어서 로그인액티비티로 가야하는 분기 by 돟련
                        try{////////////카톡 계정 성공, 아직 회원은 아님, 가입 절차(기본 설문) 진행///////////////
                            UserManager.getInstance().member_type = "kakao";
                            UserManager.getInstance().member_id = m_strUseremail;
                            UserManager.getInstance().member_name = m_strUserName;
                            m_userHealthBase.member_name = UserManager.getInstance().member_name;
                            m_userHealthDetail.member_name = UserManager.getInstance().member_name;
                            UserManager.getInstance().member_kakao_id = m_strUserid;
                            UserManager.getInstance().fb_token = m_strUserPass;
//                        UserManager.getInstance().member_sex =  m_strUserSex.equals("male") ? 0 : 1;
//                        m_userHealthBase.member_sex = m_strUserSex.equals("male") ? 0 : 1;
                            /////////////////////카톡에서 생일???///////////////////////

                            UserManager.getInstance().arr_profile_photo_URL.add(null);
                            UserManager.getInstance().arr_profile_photo_file.add(null);
                            UserManager.getInstance().arr_profile_photo_bitmap.add(null);
                            UserManager.getInstance().arr_profile_photo_resID.add(-1);
                            mNetUtil.setProfilePhoto(this, 0, urlSnsImg);

                            Intent intent = new Intent(LoginActivity.this, ManageProfileActivity.class);
                            intent.putExtra("fam_index", 0);
                            intent.putExtra("HealthBase", m_userHealthBase);
                            intent.putExtra("HealthDetail", m_userHealthDetail);
                            intent.putExtra("registerSNS", true);

                            setResult(RESULT_OK);
                            startActivity(intent);
                            finish();

                        } catch (Exception e) {e.printStackTrace();}
                    }
                    else if(m_nResultType>10) {
                        String SNStype = null;
                        if(m_strUserType.equals("facebook")){
                            SNStype = "카카오톡";
                        }else if(m_strUserType.equals("kakao")){
                            SNStype = "페이스북";
                            m_nResultType = m_nResultType - 10;
                        }

                        String message = null;
                        switch (m_nResultType){
                            case 11:
                                message = "해당 이메일은 [이메일-" + SNStype + "] 연동 계정으로 이미 비타미인에서 " +
                                        "\n 사용 중입니다. 다른 메일 계정을 사용해서 로그인해주세요.";
                                break;
                            case 12:
                                message = "해당 이메일은 [이메일] 연동 계정으로 이미 비타미인에서 " +
                                        "\n 사용 중입니다. 다른 메일 계정을 사용해서 로그인해주세요.";
                                break;
                            case 13:
                                message = "해당 이메일은 [" + SNStype + "] 연동 계정으로 이미 비타미인에서 " +
                                        "\n 사용 중입니다. 다른 메일 계정을 사용해서 로그인해주세요.";
                                break;
                        }
                        new NoticeDialog(this, "SNS 로그인 실패 알림", message, "잠시 후에 자동으로 닫힙니다.", true).show();
                        UserManager.getInstance().saveData(this, "", "",  "", "");
                    }
                    else {
                        Toast.makeText(this, "로그인을 실패하였습니다.\n" + strMsg, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(m_context, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            } else {
                if (!"".equals(strMsg)) {Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();}
            }
        } catch (JSONException e) {e.printStackTrace();}
    }

    JSONObject _jResult;

    @Override
    public void onParseJSon(JSONObject j_source) {
        _jResult = j_source;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_login:
//                if(currentVersion.contains("manager")){
//                    SharedPreferences mpref = getSharedPreferences("VitamiinPreset", 0);
//                    SharedPreferences.Editor editor = mpref.edit();
//
////                    setID.clear();
////                    setPW.clear();
//                    listPresetAcc.remove(listPresetAcc.size());
//                    for(PresetAccount onePreset:listPresetAcc){
//                        setID.add(onePreset.id);
//                        setPW.add(onePreset.pw);
//                    }
//
//                    editor.putStringSet("presetID", setID);
//                    editor.putStringSet("presetPW", setPW);
//                    editor.apply();
//                }

                UserManager.getInstance().saveData(this, "", "", "" ,"");
                if (m_edtUser.getText().toString().length() == 0) {
                    Toast.makeText(this, "이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Util.isValidEmail(m_edtUser.getText().toString())) {
                    Toast.makeText(this, "이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (m_edtPassword.getText().toString().length() == 0) {
                    Toast.makeText(this, "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                showProgress();
                Util.hideKeyPad(this);
                m_strUserType = "emailaccount";
                UserManager.getInstance().member_type = "emailaccount";
                m_strUseremail = m_edtUser.getText().toString();
                m_strUserid = m_edtUser.getText().toString();
                m_strUserPass = m_edtPassword.getText().toString();

                if(!Const.g_fcmToken.equals(""))
                    storeFcmTokenToSharedPref(LoginActivity.this, Const.g_fcmToken);
                m_nConnectType = -1;
                isloggedIn = mNetUtil.LoginInUtil(m_context, m_context, handler, m_strUserType, m_strUseremail, m_strUserid, m_strUserPass);
                break;
            case R.id.imv_kakao_login:
                m_strUserType = "kakao";
                UserManager.getInstance().member_type = "kakao";
                findViewById(R.id.btnKakaoLogin).performClick();
                break;
            case R.id.imv_facebook_login:
                m_strUserType = "facebook";
                UserManager.getInstance().member_type = "facebook";
//                findViewById(R.id.login_button).performClick();
//                m_manager.logInWithPublishPermissions(LoginActivity.this, Read_and_Pub_PERMISSIONS);      //리드퍼미션을 펍퍼미션에 넘길 수 없데. by 동현
//                m_manager.logInWithReadPermissions(LoginActivity.this, Read_and_Pub_PERMISSIONS);
                m_manager.logInWithReadPermissions(LoginActivity.this, Read_PERMISSIONS);   //되는거
                break;
            case R.id.tv_register:
                UserManager.getInstance().releaseUserData();
                UserManager.getInstance().member_type = "emailaccount";

                UserManager.getInstance().arr_profile_photo_URL.add(null);
                UserManager.getInstance().arr_profile_photo_file.add(null);
                UserManager.getInstance().arr_profile_photo_bitmap.add(null);
                UserManager.getInstance().arr_profile_photo_resID.add(-1);

                intent = new Intent(this, RegisterIDActivity.class);
                intent.putExtra("fam_index", 0);
                intent.putExtra("registerEMAIL", true);

                intent.putExtra("HealthBase", new UserHealthBase());
                intent.putExtra("HealthDetail", new UserHealthDetail());
                startActivity(intent);
                break;
            case R.id.tv_find:
                intent = new Intent(this, FindPWActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(m_strUserType.equals("facebook")){
            callbackManager.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
        }
        else if(m_strUserType.equals("kakao")){
            if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)){
                return ;
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void storeFcmTokenToSharedPref(Context context, String TokenToSave) {
        int appVersion = getAppVersion(context);
        SharedPreferences mpref = getSharedPreferences("Vitamiin", 0);
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

    Handler m_hndBackKey = new Handler() {   // BACK key handler
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0)                m_bFinish = false;        }
    };

    @Override
    public void onBackPressed() {
        if (!m_bFinish) {
            m_bFinish = true;
            Toast.makeText(this, getResources().getString(R.string.app_finish_message), Toast.LENGTH_SHORT).show();
            m_hndBackKey.sendEmptyMessageDelayed(0, 2000);
        } else {    // 앱을 종료한다.
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(sessionCallback);
    }
}
