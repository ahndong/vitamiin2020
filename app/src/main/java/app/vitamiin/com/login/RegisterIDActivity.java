package app.vitamiin.com.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.R;
import app.vitamiin.com.common.Const;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.common.UserHealthBase;
import app.vitamiin.com.common.UserHealthDetail;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.common.Util;
import app.vitamiin.com.home.ManageProfileActivity;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;

public class RegisterIDActivity extends BaseActivity implements View.OnClickListener, OnParseJSonListener {
    Context m_context;
    NetUtil mNetUtil;
    NetUtil.connectAndgetServer mNetUtilConnetServer;
    EditText m_edtUserId, m_edtPassword, m_edtConfirm;
    ImageView m_imvAgreeAll, m_imvAgree1, m_imvAgree2, m_imvAgree3;
    TextView m_tvCheckId;

    public int m_nConnectType = 0;
    int m_nResultType = 0;

    Intent m_intent;
    int index;
    boolean m_checked = false;
    UserHealthBase m_userHealthBase = new UserHealthBase();
    UserHealthDetail m_userHealthDetail = new UserHealthDetail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_id);
        m_context = this;
        mNetUtil = new NetUtil();
        mNetUtilConnetServer = mNetUtil.new connectAndgetServer(this);
        m_intent = getIntent();

        initView();
        updateInfo();
    }

    private void updateInfo() {//수정이던 add이던 fam_index는 있다. by 동현
        index = m_intent.getIntExtra("fam_index", -1);
        m_userHealthBase = (UserHealthBase) m_intent.getSerializableExtra("HealthBase");
        m_userHealthDetail = (UserHealthDetail) m_intent.getSerializableExtra("HealthDetail");

        if(m_intent.getBooleanExtra("turnBack", false)){
            m_imvAgreeAll.setSelected(true);
            m_imvAgree1.setSelected(true);
            m_imvAgree2.setSelected(true);
            m_imvAgree3.setSelected(true);
        }
    }

    private void initView() {
        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_next).setOnClickListener(this);

        findViewById(R.id.imv_agree_all).setOnClickListener(this);
        findViewById(R.id.tv_agree_1).setOnClickListener(this);
        findViewById(R.id.tv_agree_2).setOnClickListener(this);
        findViewById(R.id.tv_agree_3).setOnClickListener(this);
        m_imvAgreeAll = (ImageView) findViewById(R.id.imv_agree_all);
        m_imvAgree1 = (ImageView) findViewById(R.id.imv_agree_1);
        m_imvAgree2 = (ImageView) findViewById(R.id.imv_agree_2);
        m_imvAgree3 = (ImageView) findViewById(R.id.imv_agree_3);

        m_imvAgree1.setOnClickListener(this);
        m_imvAgree2.setOnClickListener(this);
        m_imvAgree3.setOnClickListener(this);

        m_edtUserId = (EditText) findViewById(R.id.edt_userid);
        m_edtUserId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkNext();
                m_checked = false;}

            @Override
            public void afterTextChanged(Editable s) {}
        });
        m_edtPassword = (EditText) findViewById(R.id.edt_password);
        m_edtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                checkNext();
                return false;
            }
        });
        m_edtConfirm = (EditText) findViewById(R.id.edt_confirm);
        m_edtConfirm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                checkNext();
                return false;
            }
        });

        m_tvCheckId = (TextView) findViewById(R.id.tv_checkid);
        findViewById(R.id.tv_checkid).setOnClickListener(this);
        m_tvCheckId.setSelected(false);

        if (getIntent().getBooleanExtra("turnBack", false)) {
            m_edtUserId.setText(UserManager.getInstance().member_id);
            m_edtPassword.setText(UserManager.getInstance().member_password);
            m_edtConfirm.setText(UserManager.getInstance().member_password);

            m_imvAgree1.setSelected(true);
            m_imvAgree2.setSelected(true);
            m_imvAgree3.setSelected(true);
            m_imvAgreeAll.setSelected(true);

            m_checked = true;
            checkNext();
        }
    }

    public boolean checkNext() {
        findViewById(R.id.tv_next).setSelected(false);
        if (m_edtUserId.getText().toString().length() == 0)
            return false;

        if (!Util.isValidEmail(m_edtUserId.getText().toString()))
            return false;

        if (m_edtPassword.getText().toString().length() == 0)
            return false;

        if (m_edtConfirm.getText().toString().length() == 0)
            return false;

        if (!m_edtConfirm.getText().toString().equals(m_edtPassword.getText().toString()))
            return false;

        if (m_edtPassword.getText().toString().length() < 6)
            return false;

        if (!m_checked)
            return false;

        if (!m_imvAgree1.isSelected() || !m_imvAgree2.isSelected() || !m_imvAgree3.isSelected())
            return false;

        findViewById(R.id.tv_next).setSelected(true);
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_back:
                UserManager.getInstance().releaseUserData();
                finish();
                break;
            case R.id.imv_agree_all:
                if (!m_imvAgreeAll.isSelected()) {
                    m_imvAgree1.setSelected(true);
                    m_imvAgree2.setSelected(true);
                    m_imvAgree3.setSelected(true);
                    m_imvAgreeAll.setSelected(true);
                } else {
                    m_imvAgree1.setSelected(false);
                    m_imvAgree2.setSelected(false);
                    m_imvAgree3.setSelected(false);
                    m_imvAgreeAll.setSelected(false);
                }
                checkNext();
                break;
            case R.id.tv_agree_1:
                intent = new Intent(this, PolicyViewActivity.class);
                intent.putExtra("title", "이용약관 동의");
                intent.putExtra("type", 0);
                startActivityForResult(intent, 100);
                break;
            case R.id.tv_agree_2:
                intent = new Intent(this, PolicyViewActivity.class);
                intent.putExtra("title", "개인정보 수집 및 이용에 대한 방침 동의");
                intent.putExtra("type", 1);
                startActivityForResult(intent, 200);
                break;
            case R.id.tv_agree_3:
                intent = new Intent(this, PolicyViewActivity.class);
                intent.putExtra("title", "개인정보 제3자 제공에 대한 방침 동의");
                intent.putExtra("type", 2);
                startActivityForResult(intent, 300);
                break;
            case R.id.imv_agree_1:
                m_imvAgree1.setSelected(!m_imvAgree1.isSelected());
                m_imvAgreeAll.setSelected(m_imvAgree1.isSelected() && m_imvAgree2.isSelected() && m_imvAgree3.isSelected());
                checkNext();
                break;
            case R.id.imv_agree_2:
                m_imvAgree2.setSelected(!m_imvAgree2.isSelected());
                m_imvAgreeAll.setSelected(m_imvAgree1.isSelected() && m_imvAgree2.isSelected() && m_imvAgree3.isSelected());
                checkNext();
                break;
            case R.id.imv_agree_3:
                m_imvAgree3.setSelected(!m_imvAgree3.isSelected());
                m_imvAgreeAll.setSelected(m_imvAgree1.isSelected() && m_imvAgree2.isSelected() && m_imvAgree3.isSelected());
                checkNext();
                break;
            case R.id.tv_next:
                if (m_edtUserId.getText().toString().length() == 0) {
                    Toast.makeText(this, "이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Util.isValidEmail(m_edtUserId.getText().toString())) {
                    Toast.makeText(this, "이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (m_edtPassword.getText().toString().length() == 0) {
                    Toast.makeText(this, "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (m_edtConfirm.getText().toString().length() == 0) {
                    Toast.makeText(this, "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!m_edtConfirm.getText().toString().equals(m_edtPassword.getText().toString())) {
                    Toast.makeText(this, "비밀번호가 일치 하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (m_edtPassword.getText().toString().length() < 6) {
                    Toast.makeText(this, "비밀번호는 영문 숫자를 포함한 6자리 이상으로 구성하십시오.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!m_checked) {
                    Toast.makeText(this, "아이디 중복확인을 해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!m_imvAgree1.isSelected() || !m_imvAgree2.isSelected() || !m_imvAgree3.isSelected()) {
                    Toast.makeText(this, "약관 내용에 동의해 주십시오.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Util.hideKeyPad(this);
                UserManager.getInstance().member_id = m_edtUserId.getText().toString();
                UserManager.getInstance().member_password = m_edtPassword.getText().toString();

                m_userHealthBase = new UserHealthBase();
                m_userHealthDetail = new UserHealthDetail();
                intent = new Intent(this, ManageProfileActivity.class);
                intent.putExtra("registerEMAIL", getIntent().getBooleanExtra("registerEMAIL", false));
                intent.putExtra("fam_index", 0);
                intent.putExtra("HealthBase", m_userHealthBase);
                intent.putExtra("HealthDetail", m_userHealthDetail);
                startActivity(intent);
                finish();
                break;
            case R.id.tv_checkid:
                if (m_edtUserId.getText().toString().length() == 0) {
                    Toast.makeText(this, "이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Util.isValidEmail(m_edtUserId.getText().toString())) {
                    Toast.makeText(this, "이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Util.hideKeyPad(this);
                m_nConnectType = NetUtil.apis_CHECK_ID;
                mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, m_edtUserId.getText().toString());
                break;
        }
    }

    private void processForNetEnd() {
        closeProgress();
        HttpRequester.getInstance().stopNetThread();

        int resultCode = HttpRequester.getInstance().getResultCode();
        String strMsg = HttpRequester.getInstance().getResultMsg();

        if (resultCode == Net.CONNECTION_SUCCSES) {     // 성공
            if (m_nResultType == 10) {                  // 이후 처리
                Toast.makeText(this, "사용 가능한 이메일입니다.", Toast.LENGTH_SHORT).show();
                m_tvCheckId.setSelected(true);
                m_checked = true;
            } else if(m_nResultType == 11) {
                Toast.makeText(this, "사용 중인 이메일입니다.", Toast.LENGTH_SHORT).show();
                m_tvCheckId.setSelected(false);
                m_checked = false;
            }
            checkNext();
        } else {
            if (!"".equals(strMsg)) {
                Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onParseJSon(JSONObject j_source) {
        try {
            JSONObject result = j_source.getJSONObject(Net.NET_VALUE_RESULT);
            m_nResultType = result.getInt(Net.NET_VALUE_TYPE); //
        } catch (JSONException e) {e.printStackTrace();}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 100:
                    if (getIntent().getBooleanExtra("fromSetting", false))
                        return;
                    m_imvAgree1.setSelected(true);
                    if (m_imvAgree2.isSelected() && m_imvAgree3.isSelected()) {
                        m_imvAgreeAll.setSelected(true);
                    }
                    break;
                case 200:
                    if (getIntent().getBooleanExtra("fromSetting", false))
                        return;
                    m_imvAgree2.setSelected(true);
                    if (m_imvAgree1.isSelected() && m_imvAgree3.isSelected()) {
                        m_imvAgreeAll.setSelected(true);
                    }
                    break;
                case 300:
                    if (getIntent().getBooleanExtra("fromSetting", false))
                        return;
                    m_imvAgree3.setSelected(true);
                    if (m_imvAgree2.isSelected() && m_imvAgree1.isSelected()) {
                        m_imvAgreeAll.setSelected(true);
                    }
                    break;
            }
        }
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Net.THREAD_REQUEST_END) {
                processForNetEnd();            }
        }
    };

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.tv_back));
    }
}
