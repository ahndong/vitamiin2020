package app.vitamiin.com.setting;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.ConfirmDialog;
import app.vitamiin.com.R;
import app.vitamiin.com.TutorialActivity;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;
import app.vitamiin.com.login.LoginActivity;
import app.vitamiin.com.login.PolicyActivity;

public class SettingActivity extends BaseActivity implements View.OnClickListener, OnParseJSonListener {
    int m_nResultType = 200;
    int CONNECT_TYPE_SETTING = 1;
    int CONNECT_TYPE_UPDATE_NOTI = 2;
    int m_nConnectType = CONNECT_TYPE_SETTING;

    String currentVersion, onlineVersion;
    TextView m_tvNotiComment, m_tvNotiLike;
    int noti_comment, noti_like;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
        connectServer(CONNECT_TYPE_SETTING);
    }

    private void initView() {
        findViewById(R.id.imv_back).setOnClickListener(this);
        findViewById(R.id.lly_question).setOnClickListener(this);
        findViewById(R.id.lly_signout).setOnClickListener(this);
        findViewById(R.id.tv_update).setOnClickListener(this);
        findViewById(R.id.lly_go_policy).setOnClickListener(this);
        findViewById(R.id.lly_go_tutorial).setOnClickListener(this);
        findViewById(R.id.tv_logout).setOnClickListener(this);

        findViewById(R.id.lly_profile).setOnClickListener(this);
        findViewById(R.id.lly_password).setOnClickListener(this);

        m_tvNotiComment = (TextView) findViewById(R.id.tv_noti_comment);
        m_tvNotiLike = (TextView) findViewById(R.id.tv_noti_like);
        m_tvNotiComment.setOnClickListener(this);
        m_tvNotiLike.setOnClickListener(this);

        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            localNameNotFoundException.printStackTrace();
        }
        onlineVersion = UserManager.getInstance().onlineVesion;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.imv_back:
                finish();
                break;
            case R.id.lly_question:
                intent = new Intent(this, QuestionActivity.class);
                startActivity(intent);
                break;
            case R.id.lly_go_policy:
                intent = new Intent(this, PolicyActivity.class);
                intent.putExtra("fromSetting", true);
                startActivity(intent);
                break;
            case R.id.lly_go_tutorial:
                intent = new Intent(this, TutorialActivity.class);
                intent.putExtra("fromSetting", true);
                startActivity(intent);
                break;
            case R.id.tv_update:
                new ConfirmDialog(this, "업데이트", "비타미인 앱을 업데이트 하시겠습니까?", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getString(R.string.applicationId))));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getString(R.string.applicationId))));
                        }
                    }
                }).show();
                break;
            case R.id.tv_noti_comment:
                noti_comment = 1 - noti_comment;
                connectServer(CONNECT_TYPE_UPDATE_NOTI);
                break;
            case R.id.tv_noti_like:
                noti_like = 1 - noti_like;
                connectServer(CONNECT_TYPE_UPDATE_NOTI);
                break;
            case R.id.lly_password:
                intent = new Intent(this, ChangePasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.lly_profile:
                intent = new Intent(this, ModifyActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_logout:    //로그아웃
                UserManager.getInstance().releaseUserData();
                UserManager.getInstance().saveData(this,"","","","");
                intent = new Intent(this, LoginActivity.class);
                intent.putExtra("turnBack", true);
                startActivity(intent);
                finish();
                break;
            case R.id.lly_signout:   //탈퇴
                intent = new Intent(this, SignoutActivity.class);
                startActivityForResult(intent, 1000);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.imv_back));
    }

    @Override
    protected void onActivityResult(int p_requestCode,
                                    int p_resultCode,
                                    Intent p_intentActivity) {

        super.onActivityResult(p_requestCode, p_resultCode, p_intentActivity);
        if (p_resultCode == RESULT_OK) {
            switch (p_requestCode) {
                case 1000:
                    Intent intent = new Intent();
                    intent.putExtra("signout", true);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
        }
    }

    private void connectServer(int strAct) {
        m_nConnectType = strAct;

        showProgress();
        String[] paramFields = new String[]{Net.NET_POST_FIELD_ACT,
                Net.NET_POST_FIELD_REQUEST};
        String[] paramValues = null;

        JSONObject w_objJSonData = new JSONObject();
        try {
            w_objJSonData.put(Net.NET_VALUE_MEMBER_ID, UserManager.getInstance().member_id);
            if (m_nConnectType == CONNECT_TYPE_SETTING) {

                paramValues = new String[]{
                        Net.POST_FIELD_ACT_APP_SETTING,
                        w_objJSonData.toString()};
            } else if (m_nConnectType == CONNECT_TYPE_UPDATE_NOTI) {
                w_objJSonData.put("noti_comment", noti_comment);
                w_objJSonData.put("noti_like", noti_like);

                paramValues = new String[]{
                        Net.POST_FIELD_ACT_UPDATE_NOTI,
                        w_objJSonData.toString()};
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String netUrl = Net.URL_SERVER + Net.URL_SERVER_API;
        HttpRequester.getInstance().init(this, this, handler, netUrl,
                paramFields, paramValues, false);

        HttpRequester.getInstance().setProgressMessage(
                Net.NET_COMMON_STRING_WAITING);
        HttpRequester.getInstance().startNetThread();
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Net.THREAD_REQUEST_END)
                processForNetEnd();
        }
    };

    private void processForNetEnd() {
        parseJSON();
        closeProgress();
        HttpRequester.getInstance().stopNetThread();

        int resultCode = HttpRequester.getInstance().getResultCode();
        String strMsg = HttpRequester.getInstance().getResultMsg();

        if (resultCode == Net.CONNECTION_SUCCSES) {
            if (m_nResultType == 200) {
                if (m_nConnectType == CONNECT_TYPE_SETTING) {
                    ((TextView) findViewById(R.id.tv_latest_version)).setText(onlineVersion);
                    ((TextView) findViewById(R.id.tv_current_version)).setText(currentVersion);
                    if (currentVersion.equals(onlineVersion))
                        findViewById(R.id.tv_update).setVisibility(View.GONE);
                    else
                        findViewById(R.id.tv_update).setVisibility(View.VISIBLE);
                }
                if (noti_comment == 1) {
                    m_tvNotiComment.setSelected(true);
                    m_tvNotiComment.setText("ON");
                } else {
                    m_tvNotiComment.setSelected(false);
                    m_tvNotiComment.setText("OFF");
                }
                if (noti_like == 1) {
                    m_tvNotiLike.setSelected(true);
                    m_tvNotiLike.setText("ON");
                } else {
                    m_tvNotiLike.setSelected(false);
                    m_tvNotiLike.setText("OFF");
                }
            }
        } else {// 로그인 다시해야합니다. by 동현            // test();            // ---------------------------
            if (!"".equals(strMsg)) {
                Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();}
        }
    }

    JSONObject _jResult;

    public void parseJSON() {
        try {
            m_nResultType = _jResult.getInt(Net.NET_VALUE_CODE); //
            JSONObject result = _jResult.getJSONObject(Net.NET_VALUE_RESULT);
            if (m_nConnectType == CONNECT_TYPE_SETTING) {
                if(onlineVersion.equals(""))
                    onlineVersion = result.getString("latest_version");
            }

            noti_comment = result.getInt("noti_comment");
            noti_like = result.getInt("noti_like");
        } catch (JSONException e) {e.printStackTrace();}
    }

    @Override
    public void onParseJSon(JSONObject j_source) {
        _jResult = j_source;}
}
