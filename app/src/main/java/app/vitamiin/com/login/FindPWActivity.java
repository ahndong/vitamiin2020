package app.vitamiin.com.login;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.ConfirmDialog;
import app.vitamiin.com.R;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.common.Util;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;

public class FindPWActivity extends BaseActivity implements View.OnClickListener, OnParseJSonListener {
    Context m_context;
    NetUtil mNetUtil;
    NetUtil.connectAndgetServer mNetUtilConnetServer;

    EditText m_edtNamePwd, m_edtUserId;
    TextView m_tvBirthPwd;

    public int m_nConnectType = 0;
    int m_nResultType = 0;
    String m_strFindPwd = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);
        m_context = this;
        mNetUtil = new NetUtil();
        mNetUtilConnetServer = mNetUtil.new connectAndgetServer(this);

        initView();
    }

    private void initView() {
        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_find_pwd).setOnClickListener(this);

        m_edtNamePwd = (EditText) findViewById(R.id.edt_name1);
        m_tvBirthPwd = (TextView) findViewById(R.id.tv_birth1);
        m_tvBirthPwd.setOnClickListener(this);
        m_edtUserId = (EditText) findViewById(R.id.edt_userid);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_birth1:
                new BirthdaySelectDialog(this, 3).show();
                break;
            case R.id.tv_find_pwd:
                if (m_edtNamePwd.getText().toString().length() == 0) {
                    Toast.makeText(this, "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (m_edtUserId.getText().toString().length() == 0) {
                    Toast.makeText(this, "이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Util.isValidEmail(m_edtUserId.getText().toString())) {
                    Toast.makeText(this, "이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (m_tvBirthPwd.getText().toString().length() == 0) {
                    Toast.makeText(this, "생년월일을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Util.hideKeyPad(this);

                m_nConnectType = NetUtil.apis_FIND_PW;
                mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, new String[] {m_edtNamePwd.getText().toString(), m_tvBirthPwd.getText().toString(), m_edtUserId.getText().toString()});
                break;
        }
    }

    private void processForNetEnd() {
        closeProgress();
        HttpRequester.getInstance().stopNetThread();

        int resultCode = HttpRequester.getInstance().getResultCode();
        String strMsg = HttpRequester.getInstance().getResultMsg();

        if (resultCode == Net.CONNECTION_SUCCSES) {
            if (m_nResultType == 0) {
                new ConfirmDialog(this, "비밀번호 찾기", "회원님의 비밀번호를\n이메일로 발송하였습니다.", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }).show();
            } else if (m_nResultType == 1) {
                Toast.makeText(this, "일치하는 회원이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (!"".equals(strMsg)) {
                if (m_nResultType == 0) {
                    new ConfirmDialog(this, "비밀번호 찾기", "회원님의 비밀번호를\n이메일로 발송하였습니다.", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    }).show();
                }
                else if (m_nResultType == 1) {Toast.makeText(this, "일치하는 회원이 없습니다.", Toast.LENGTH_SHORT).show();}
            }
        }
    }

    @Override
    public void onParseJSon(JSONObject j_source) {
        try {
            JSONObject result = j_source.getJSONObject(Net.NET_VALUE_RESULT);
            m_nResultType = result.getInt(Net.NET_VALUE_TYPE); //
            m_strFindPwd = result.getString("f_password");
        }
        catch (JSONException e) {e.printStackTrace();}
    }

    public void setBirthday1(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        int iyear = calendar.get(Calendar.YEAR) - year;
        int imonth = month + 1;
        int iday = day + 1;

        String smonth = "" + imonth;
        if (imonth < 10)
            smonth = "0" + smonth;
        String sday = "" + iday;
        if (iday < 10)
            sday = "0" + sday;
        m_tvBirthPwd.setText("" + iyear + "-" + smonth + "-" + sday);
    }

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.tv_back));
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Net.THREAD_REQUEST_END) {
                processForNetEnd();            }
        }
    };
}
