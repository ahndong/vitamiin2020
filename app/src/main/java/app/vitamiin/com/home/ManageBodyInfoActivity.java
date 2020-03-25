package app.vitamiin.com.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Locale;

import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.R;
import app.vitamiin.com.common.Const;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.common.UserHealthBase;
import app.vitamiin.com.common.UserHealthDetail;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.common.Util;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;
import app.vitamiin.com.login.DetailSurveyActivity;
import app.vitamiin.com.login.RegFinishActivity;

import static android.os.SystemClock.sleep;


public class ManageBodyInfoActivity extends BaseActivity implements View.OnClickListener, OnParseJSonListener {
    int m_nResultType;
    Context m_context;
    NetUtil.connectAndgetServer mNetUtilConnetServer;
    int m_nConnectType;
    public final String PROPERTY_FCM_TOKEN = "fcm_token";
    public final String PROPERTY_APP_VERSION = "appVersion";

    EditText m_edtHeight, m_edtWeight;
    TextView m_tvBmi, m_tvDisease, m_tvPrefer, m_tvInterest;
    String m_disease = "", m_prefer_healthfood = "", m_interest = "";
    File m_uploadImageFile = null;
    Boolean doAutoStep =false;
    int minmax[];

    int index;
    UserHealthBase m_userHealthBase = new UserHealthBase();
    UserHealthDetail m_userHealthDetail = new UserHealthDetail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNetUtilConnetServer = new NetUtil().new connectAndgetServer(this);
        setContentView(R.layout.activity_manage_body_info);

        m_context = this;
        initView();
        updateInfo();
    }

    private void initView() {
        findViewById(R.id.tv_back).setOnClickListener(this);

        m_edtHeight = (EditText) findViewById(R.id.edt_height);
        m_edtHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                calcBMI();
                checkNext();
            }
        });
        m_edtWeight = (EditText) findViewById(R.id.edt_weight);
        m_edtWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                calcBMI();
                checkNext();
            }
        });

        m_tvBmi = (TextView) findViewById(R.id.tv_bmi);
        findViewById(R.id.tv_add_family).setOnClickListener(this);

        m_tvDisease = (TextView) findViewById(R.id.tv_disease);
        m_tvDisease.setOnClickListener(this);
        findViewById(R.id.tv_select_disease).setOnClickListener(this);
        m_tvInterest = (TextView) findViewById(R.id.tv_interest);
        m_tvInterest.setOnClickListener(this);
        findViewById(R.id.tv_select_interest).setOnClickListener(this);
        m_tvPrefer = (TextView) findViewById(R.id.tv_prefer_healthfood);
        m_tvPrefer.setOnClickListener(this);
        findViewById(R.id.tv_select_prefer_healthfood).setOnClickListener(this);
    }

    private void updateInfo() {
        index = getIntent().getIntExtra("fam_index",-1);
        m_userHealthBase = (UserHealthBase) getIntent().getSerializableExtra("HealthBase");
        m_userHealthDetail = (UserHealthDetail) getIntent().getSerializableExtra("HealthDetail");

        if (getIntent().getBooleanExtra("registerEMAIL", false)) {
        } else if (getIntent().getBooleanExtra("registerSNS", false)) {
        } else if (getIntent().getBooleanExtra("isAdd", false)){
            ((TextView) findViewById(R.id.tv_title)).setText("가족 프로필 추가 - 3/3");
        } else if (getIntent().getBooleanExtra("fromReco", false)){
            ((TextView) findViewById(R.id.tv_title)).setText("프로필 정보 수정 - 3/3");
        } else if (getIntent().getBooleanExtra("fromModi", false)){
            ((TextView) findViewById(R.id.tv_title)).setText("주거 정보 수정");
        }

        if(m_userHealthBase.member_height!=-1)
            m_edtHeight.setText(String.valueOf(m_userHealthBase.member_height));
        if(m_userHealthBase.member_weight!=-1)
            m_edtWeight.setText(String.valueOf(m_userHealthBase.member_weight));
        calcBMI();

        m_disease = m_userHealthBase.member_disease;
        m_interest = m_userHealthBase.member_interest_health;
        m_prefer_healthfood = m_userHealthBase.member_prefer_healthfood;

        setInfo(1, m_disease);
        setInfo(2, m_interest);
        setInfo(3, m_prefer_healthfood);

        doAutoStep=true;
        startStep();
    }

    public void startStep() {
        if(m_disease.equals("")){
            sleep(300);
            onClick(findViewById(R.id.tv_select_disease));
            return;
        }if(m_interest.equals("")){
            sleep(300);
            onClick(findViewById(R.id.tv_select_interest));
            return;
        }if(m_prefer_healthfood.equals("")){
            sleep(300);
            onClick(findViewById(R.id.tv_select_prefer_healthfood));
            return;
        }
        doAutoStep =false;
    }

    public void setInfo(int type, String index) {
        if (index == "")    return;

        if (type == 1) {
            if (index.length() > 0) {
                String[] list = getResources().getStringArray(R.array.array_disease);
                String[] arr = index.split(",");
                String text = "";

                for (String One : arr)     text += list[Integer.parseInt(One)] + ",";
                if (text.length() > 0)
                    text = text.substring(0, text.length() - 1);
                text = text.replace("\n", "");
                m_tvDisease.setText(text);
            }
        } else if (type == 2) {
            if (index.length() > 0) {
                String[] list = getResources().getStringArray(R.array.array_interest_health);
                String[] arr = index.split(",");
                String text = "";

                for (String One : arr)     text += list[Integer.parseInt(One)] + ",";
                if (text.length() > 0)
                    text = text.substring(0, text.length() - 1);
                text = text.replace("\n", "");
                m_tvInterest.setText(text);
            }
        } else if (type == 3) {
            if (index.length() > 0) {
                String[] list = getResources().getStringArray(R.array.array_prefer_healthfood);
                String[] arr = index.split(",");
                String text = "";
                for (String One : arr)     text += list[Integer.parseInt(One)] + ",";
                if (text.length() > 0)
                    text = text.substring(0, text.length() - 1);
                text = text.replace("\n", "");
                m_tvPrefer.setText(text);
            }
        }
        if(doAutoStep)startStep();
        checkNext();
    }

    public boolean checkNext() {
        findViewById(R.id.tv_add_family).setSelected(!(m_disease.equals("") || m_interest.equals("") || m_prefer_healthfood.equals("")
                                                        || m_edtHeight.getText().toString().equals("")
                                                        || m_edtWeight.getText().toString().equals("")));
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_back:
                if (getIntent().getBooleanExtra("fromModi", false)) {
                    finish();
                } else {
                    m_userHealthBase.member_height = m_edtHeight.getText().toString().length()==0? -1:Double.parseDouble(m_edtHeight.getText().toString());
                    m_userHealthBase.member_weight = m_edtWeight.getText().toString().length()==0? -1:Double.parseDouble(m_edtWeight.getText().toString());
                    m_userHealthBase.member_bmi = Double.parseDouble(calcBMI());
                    m_userHealthBase.member_prefer_healthfood = m_prefer_healthfood;
                    m_userHealthBase.member_disease = m_disease;
                    m_userHealthBase.member_interest_health = m_interest;

                    intent = new Intent();
                    intent.putExtra("fam_index", index);
                    intent.putExtra("HealthBase", m_userHealthBase);
                    intent.putExtra("HealthDetail", m_userHealthDetail);
                    intent.putExtra("turnBack", true);
                    intent.putExtra("registerEMAIL", getIntent().getBooleanExtra("registerEMAIL", false));
                    intent.putExtra("registerSNS", getIntent().getBooleanExtra("registerSNS", false));
                    intent.putExtra("isAdd", getIntent().getBooleanExtra("isAdd", false));
                    intent.putExtra("fromReco", getIntent().getBooleanExtra("fromReco", false));

                    setResult(RESULT_CANCELED, intent);
                    finish();
                }
                break;
            case R.id.tv_add_family:
                Util.hideKeyPad(this);
                if (m_edtHeight.getText().toString().length() == 0) {
                    Toast.makeText(this, "키를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (m_edtWeight.getText().toString().length() == 0) {
                    Toast.makeText(this, "몸무게를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (m_interest.length() == 0) {
                    Toast.makeText(this, "관심 건강 분야를 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (m_prefer_healthfood.length() == 0) {
                    Toast.makeText(this, "선호 건강 제품군을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                m_userHealthBase.member_height = Double.parseDouble(m_edtHeight.getText().toString());
                m_userHealthBase.member_weight = Double.parseDouble(m_edtWeight.getText().toString());
                m_userHealthBase.member_bmi = Double.parseDouble(calcBMI());
                m_userHealthBase.member_disease = m_disease;
                m_userHealthBase.member_interest_health = m_interest;
                m_userHealthBase.member_prefer_healthfood = m_prefer_healthfood;

                UserManager.getInstance().currentFamIndex = index;
                UserManager.getInstance().HealthBaseInfo.get(index).setUserHealthBase(m_userHealthBase);
                UserManager.getInstance().HealthDetailInfo.get(index).setUserHealthDetail(m_userHealthDetail);

                if (getIntent().getBooleanExtra("registerEMAIL", false) || getIntent().getBooleanExtra("registerSNS", false)) {//회원 가입의 분기        by 동현
                    if (UserManager.getInstance().member_type.equals("kakao")) {                        //SNS 회원 가입일 경우
                        UserManager.getInstance().saveData(this, "kakao", UserManager.getInstance().member_id, UserManager.getInstance().member_kakao_id, UserManager.getInstance().member_kakao_id);  //두번째 pass는 token 이어야 한다. by 동현
                    }
                    else if (UserManager.getInstance().member_type.equals("facebook")) {                        //SNS 회원 가입일 경우
                        UserManager.getInstance().saveData(this, "facebook", UserManager.getInstance().member_id, UserManager.getInstance().member_fb_id, UserManager.getInstance().fb_token);  //두번째 pass는 token 이어야 한다. by 동현
                    }
                    else if(UserManager.getInstance().member_type.equals("emailaccount")){                      //이메일 회원 가입일 경우
                        UserManager.getInstance().saveData(this, "emailaccount", UserManager.getInstance().member_id, UserManager.getInstance().member_id, UserManager.getInstance().member_password);
                    }

                    if(!(Const.g_fcmToken).equals(""))
                        storeFcmTokenToSharedPref(ManageBodyInfoActivity.this, Const.g_fcmToken);

                    m_nConnectType = NetUtil.apis_REGISTER_USER;
                    mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, true);
                } else if (getIntent().getBooleanExtra("isAdd", false)){
                    m_nConnectType = NetUtil.apis_REGISTER_FAMILY;
                    mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, m_userHealthBase);
                } else if (getIntent().getBooleanExtra("fromReco", false)){
                    m_nConnectType = NetUtil.apis_UPDATE_BASE;
                    mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, true);
                } else if (getIntent().getBooleanExtra("fromModi", false)){
                    m_nConnectType = NetUtil.apis_STORE_INFO_BODY_ALL;
                    mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, true);
                }
                break;
            case R.id.tv_select_disease:
            case R.id.tv_disease:
                intent = new Intent(this, DetailSurveyActivity.class);
                intent.putExtra("edit", getIntent().getBooleanExtra("fromReco", false)||getIntent().getBooleanExtra("fromModi", false));
                intent.putExtra("type", "disease");
                intent.putExtra("disease", m_disease);
                minmax = new int[] {1,5};
                intent.putExtra("minmax", minmax);
                startActivityForResult(intent, 41);
                break;
            case R.id.tv_select_interest:
            case R.id.tv_interest:
                intent = new Intent(this, DetailSurveyActivity.class);
                intent.putExtra("edit", getIntent().getBooleanExtra("fromReco", false)||getIntent().getBooleanExtra("fromModi", false));
                intent.putExtra("is_element_named_none", false);
                intent.putExtra("type", "interest");
                intent.putExtra("interest", m_interest);
                minmax = new int[] {1,5};
                intent.putExtra("minmax", minmax);
                startActivityForResult(intent, 42);
                break;
            case R.id.tv_select_prefer_healthfood:
            case R.id.tv_prefer_healthfood:
                intent = new Intent(this, DetailSurveyActivity.class);
                intent.putExtra("edit", getIntent().getBooleanExtra("fromReco", false)||getIntent().getBooleanExtra("fromModi", false));
                intent.putExtra("is_element_named_none", false);
                intent.putExtra("type", "prefer");
                intent.putExtra("prefer", m_prefer_healthfood);
                minmax = new int[] {1,5};
                intent.putExtra("minmax", minmax);
                startActivityForResult(intent, 43);
                break;
        }
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Net.THREAD_REQUEST_END) {processForNetEnd();}}
    };

    private void processForNetEnd() {
        closeProgress();
        HttpRequester.getInstance().stopNetThread();

        int resultCode = HttpRequester.getInstance().getResultCode();
        String strMsg = HttpRequester.getInstance().getResultMsg();

        if (resultCode == Net.CONNECTION_SUCCSES) {
            if (m_nResultType == 0) {
                Intent intent;
                UserManager.getInstance().updateHealthInfo(index, m_userHealthBase, m_userHealthDetail);
                if (m_nConnectType == NetUtil.apis_REGISTER_USER){
                    intent = new Intent(this, RegFinishActivity.class);
                    startActivity(intent);
                }else {
                    setResult(RESULT_OK);
                }
                finish();
            }
        } else {
            if (!"".equals(strMsg)) {Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();}}
    }

    @Override
    public void onParseJSon(JSONObject j_source) {
        try {
            JSONObject result = j_source.getJSONObject(Net.NET_VALUE_RESULT);
            m_nResultType = result.getInt(Net.NET_VALUE_TYPE);

            if (m_nConnectType == NetUtil.apis_REGISTER_USER){
                UserManager.getInstance().arr_profile_photo_URL.set(index, result.getString("photo"));
                UserManager.getInstance().member_no = result.getInt("f_no");
                UserManager.getInstance().event_num = result.getInt("event_num");
            }
//            else if (m_nConnectType == NetUtil.apis_STORE_INFO_BODY_ALL) {        //할 것이 없는 부분 by 동현
//            }
            else if (m_nConnectType == NetUtil.apis_REGISTER_FAMILY  || m_nConnectType == NetUtil.apis_UPDATE_BASE){
                if(UserManager.getInstance().arr_profile_photo_resID.get(index) == -1)
                    UserManager.getInstance().arr_profile_photo_URL.set(index, result.getString("photo"));
            }
        } catch (JSONException e) {e.printStackTrace();}
    }

    @Override
    protected void onActivityResult(int p_requestCode,
                                    int p_resultCode,
                                    Intent p_intentActivity) {
        super.onActivityResult(p_requestCode, p_resultCode, p_intentActivity);
        if (p_resultCode == RESULT_OK) {
            switch (p_requestCode) {
                case 41:
                    m_tvDisease.setText(p_intentActivity.getStringExtra("text"));
                    m_disease = p_intentActivity.getStringExtra("disease");
                    m_userHealthBase.member_disease = m_disease;
                    if(doAutoStep)startStep();
                    checkNext();
                    break;
                case 42:
                    m_tvInterest.setText(p_intentActivity.getStringExtra("text"));
                    m_interest = p_intentActivity.getStringExtra("interest");
                    m_userHealthBase.member_interest_health = m_interest;
                    if(doAutoStep)startStep();
                    checkNext();
                    break;
                case 43:
                    m_tvPrefer.setText(p_intentActivity.getStringExtra("text"));
                    m_prefer_healthfood = p_intentActivity.getStringExtra("prefer");
                    m_userHealthBase.member_prefer_healthfood = m_prefer_healthfood;
                    if(doAutoStep)startStep();
                    checkNext();
                    break;
            }
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

    private String calcBMI() {
        String sHeight = m_edtHeight.getText().toString();
        String sWeight = m_edtWeight.getText().toString();
        Double dHeight = 0.0, dWeight = 0.0, dBMI = 0.0;

        if (sHeight.length() > 0) {
            dHeight = Double.parseDouble(sHeight);
            dHeight = dHeight / 100.0;
        }
        if (sWeight.length() > 0)
            dWeight = Double.parseDouble(sWeight);
        if (dHeight != 0.0 && dWeight != 0.0) {
            dBMI = dWeight / (dHeight * dHeight);
        }

        String sBMI = String.format(Locale.KOREA, "%.2f", dBMI);
        m_tvBmi.setText(sBMI);
        return sBMI;
    }

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.tv_back));
    }
}
