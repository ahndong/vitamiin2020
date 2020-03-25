package app.vitamiin.com.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.R;
import app.vitamiin.com.common.Const;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.common.Util;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener, OnParseJSonListener {
    Context m_context;
    EditText m_edtOld, m_edtPassword, m_edtConfirm;
    NetUtil.connectAndgetServer mNetUtilConnetServer;
    public int m_nConnectType = 0;

    int m_nResultType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        m_context = this;
        mNetUtilConnetServer = new NetUtil().new connectAndgetServer(this);

        initView();
    }

    private void initView() {
        findViewById(R.id.imv_back).setOnClickListener(this);
        findViewById(R.id.tv_next).setOnClickListener(this);

        m_edtOld = (EditText) findViewById(R.id.edt_old);
        m_edtPassword = (EditText) findViewById(R.id.edt_password);
        m_edtConfirm = (EditText) findViewById(R.id.edt_confirm);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imv_back:
                finish();
                break;
            case R.id.tv_next:
                if (m_edtOld.getText().toString().length() == 0) {
                    Toast.makeText(this, "이전 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (m_edtPassword.getText().toString().length() == 0) {
                    Toast.makeText(this, "새 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (m_edtConfirm.getText().toString().length() == 0) {
                    Toast.makeText(this, "새 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!m_edtConfirm.getText().toString().equals(m_edtPassword.getText().toString())) {
                    Toast.makeText(this, "비밀번호가 일치 하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (m_edtPassword.getText().toString().length() < 6 || m_edtOld.getText().toString().length() < 6) {
                    Toast.makeText(this, "비밀번호는 영문 숫자를 포함한 6자리 이상으로 구성하십시오.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Util.hideKeyPad(this);
                m_nConnectType = NetUtil.apis_UPDATE_PW;
                mNetUtilConnetServer.connectServer(this, handler, m_nConnectType,
                                    new String[] {m_edtOld.getText().toString(), m_edtPassword.getText().toString()});
                break;
        }
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Net.THREAD_REQUEST_END) {
                processForNetEnd();}
        }
    };

    private void processForNetEnd() {
        closeProgress();
        HttpRequester.getInstance().stopNetThread();

        int resultCode = HttpRequester.getInstance().getResultCode();
        String strMsg = HttpRequester.getInstance().getResultMsg();

        if (resultCode == Net.CONNECTION_SUCCSES) {
            if (m_nResultType == 0)
                finish();
            else
                Toast.makeText(this, "이전 비밀번호가 옳바르지 않습니다.", Toast.LENGTH_SHORT).show();
        } else {
            if (!"".equals(strMsg))
                Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();
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
    public void onBackPressed() {
        onClick(findViewById(R.id.imv_back));
    }
}
