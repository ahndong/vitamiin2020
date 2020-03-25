package app.vitamiin.com.setting;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.R;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;

public class QuestionActivity extends BaseActivity implements View.OnClickListener, OnParseJSonListener {

    int m_nResultType = 200;

    TextView m_tvPart;
    TextView[] m_tvChecks;
    int m_nPart = -1; //0~제휴,1-문의

    EditText m_edtTitle, m_edtContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        initView();
    }

    private void initView() {
        findViewById(R.id.imv_back).setOnClickListener(this);
        findViewById(R.id.tv_check_01).setOnClickListener(this);
        findViewById(R.id.tv_check_02).setOnClickListener(this);
        findViewById(R.id.tv_check_03).setOnClickListener(this);
        findViewById(R.id.tv_check_04).setOnClickListener(this);
        findViewById(R.id.tv_check_05).setOnClickListener(this);
        findViewById(R.id.tv_check_06).setOnClickListener(this);
        m_tvChecks = new TextView[6];
        m_tvChecks[0] = (TextView) findViewById(R.id.tv_check_01);
        m_tvChecks[1] = (TextView) findViewById(R.id.tv_check_02);
        m_tvChecks[2] = (TextView) findViewById(R.id.tv_check_03);
        m_tvChecks[3] = (TextView) findViewById(R.id.tv_check_04);
        m_tvChecks[4] = (TextView) findViewById(R.id.tv_check_05);
        m_tvChecks[5] = (TextView) findViewById(R.id.tv_check_06);

        m_edtContent = (EditText) findViewById(R.id.edt_content);
        findViewById(R.id.tv_done).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imv_back:
                finish();
                break;
            case R.id.tv_check_01:
                m_nPart = 0;
                m_tvChecks[0].setSelected(true);
                m_tvChecks[1].setSelected(false);
                m_tvChecks[2].setSelected(false);
                m_tvChecks[3].setSelected(false);
                m_tvChecks[4].setSelected(false);
                m_tvChecks[5].setSelected(false);
                break;
            case R.id.tv_check_02:
                m_nPart = 1;
                m_tvChecks[0].setSelected(false);
                m_tvChecks[1].setSelected(true);
                m_tvChecks[2].setSelected(false);
                m_tvChecks[3].setSelected(false);
                m_tvChecks[4].setSelected(false);
                m_tvChecks[5].setSelected(false);
                break;
            case R.id.tv_check_03:
                m_nPart = 2;
                m_tvChecks[0].setSelected(false);
                m_tvChecks[1].setSelected(false);
                m_tvChecks[2].setSelected(true);
                m_tvChecks[3].setSelected(false);
                m_tvChecks[4].setSelected(false);
                m_tvChecks[5].setSelected(false);
                break;
            case R.id.tv_check_04:
                m_nPart = 3;
                m_tvChecks[0].setSelected(false);
                m_tvChecks[1].setSelected(false);
                m_tvChecks[2].setSelected(false);
                m_tvChecks[3].setSelected(true);
                m_tvChecks[4].setSelected(false);
                m_tvChecks[5].setSelected(false);
                break;
            case R.id.tv_check_05:
                m_nPart = 4;
                m_tvChecks[0].setSelected(false);
                m_tvChecks[1].setSelected(false);
                m_tvChecks[2].setSelected(false);
                m_tvChecks[3].setSelected(false);
                m_tvChecks[4].setSelected(true);
                m_tvChecks[5].setSelected(false);
                break;
            case R.id.tv_check_06:
                m_nPart = 5;
                m_tvChecks[0].setSelected(false);
                m_tvChecks[1].setSelected(false);
                m_tvChecks[2].setSelected(false);
                m_tvChecks[3].setSelected(false);
                m_tvChecks[4].setSelected(false);
                m_tvChecks[5].setSelected(true);
                break;
            case R.id.tv_done:
                if (m_nPart == -1) {
                    Toast.makeText(this, "분야를 선택해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (m_edtContent.getText().toString().length() == 0) {
                    Toast.makeText(this, "내용을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                connectServer();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.imv_back));
    }

    private void connectServer() {
        showProgress();
        String[] paramFields = new String[]{Net.NET_POST_FIELD_ACT,
                Net.NET_POST_FIELD_REQUEST};
        String[] paramValues = null;

        // 파라메터 입력
        JSONObject w_objJSonData = new JSONObject();
        try {
            w_objJSonData.put(Net.NET_VALUE_MEMBER_ID, UserManager.getInstance().member_id);
            w_objJSonData.put("part", "" + m_nPart);
            w_objJSonData.put("title", "" + m_tvChecks[m_nPart].getText());
            w_objJSonData.put("content", m_edtContent.getText().toString());

            paramValues = new String[]{
                    Net.POST_FIELD_ACT_ADD_QUESTION,
                    w_objJSonData.toString()};

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

            if (msg.what == Net.THREAD_REQUEST_END) {

                processForNetEnd();

            }
        }
    };

    private void processForNetEnd() {
        parseJSON();
        closeProgress();
        HttpRequester.getInstance().stopNetThread();

        int resultCode = HttpRequester.getInstance().getResultCode();
        String strMsg = HttpRequester.getInstance().getResultMsg();
        // 성공
        if (resultCode == Net.CONNECTION_SUCCSES) {
            if (m_nResultType == 200) {
                Toast.makeText(this, "등록되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {// 실패
            // test();
            // ---------------------------
            if (!"".equals(strMsg)) {
                Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    JSONObject _jResult;

    public void parseJSON() {
        try {
            m_nResultType = _jResult.getInt(Net.NET_VALUE_CODE);
            //JSONObject result = _jResult.getJSONObject(Net.NET_VALUE_RESULT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onParseJSon(JSONObject j_source) {
        // TODO Auto-generated method stub
        _jResult = j_source;
    }
}
