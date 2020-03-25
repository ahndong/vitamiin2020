package app.vitamiin.com.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.R;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.common.UserHealthBase;
import app.vitamiin.com.common.UserHealthDetail;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;

public class ManageLivingInfoActivity extends BaseActivity implements View.OnClickListener, OnParseJSonListener {
    int m_nResultType = 200;
    Context m_context;
    NetUtil mNetUtil;
    NetUtil.connectAndgetServer mNetUtilConnetServer;

    Intent m_intent;
    public int m_nConnectType = 0;

    int index;
    int m_nSelectedIndex = 0;

    UserHealthBase m_userHealthBase = new UserHealthBase();
    UserHealthDetail m_userHealthDetail = new UserHealthDetail();
    LinearLayout m_llyDwell1, m_llyDwell2, m_llyDwell3, m_llyDwell4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_living_info);
        m_context = this;
        mNetUtil = new NetUtil();
        mNetUtilConnetServer = mNetUtil.new connectAndgetServer(this);

        m_intent = getIntent();
        initView();
        updateInfo();
    }

    private void initView() {
        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_next).setOnClickListener(this);

        m_llyDwell1 = (LinearLayout) findViewById(R.id.lly_dwell_1);
        m_llyDwell2 = (LinearLayout) findViewById(R.id.lly_dwell_2);
        m_llyDwell3 = (LinearLayout) findViewById(R.id.lly_dwell_3);
        m_llyDwell4 = (LinearLayout) findViewById(R.id.lly_dwell_4);
        m_llyDwell1.setOnClickListener(this);
        m_llyDwell2.setOnClickListener(this);
        m_llyDwell3.setOnClickListener(this);
        m_llyDwell4.setOnClickListener(this);
    }
    private void updateInfo() {
        index = m_intent.getIntExtra("fam_index",-1);
        m_userHealthBase = (UserHealthBase) m_intent.getSerializableExtra("HealthBase");
        m_userHealthDetail = (UserHealthDetail) m_intent.getSerializableExtra("HealthDetail");

        if (m_intent.getBooleanExtra("registerEMAIL", false)) {
        } else if (m_intent.getBooleanExtra("registerSNS", false)) {
        } else if (m_intent.getBooleanExtra("isAdd", false)){
            ((TextView) findViewById(R.id.tv_title)).setText("가족 프로필 추가 - 2/3");
        } else if (m_intent.getBooleanExtra("fromReco", false)){
            ((TextView) findViewById(R.id.tv_title)).setText("프로필 정보 수정 - 2/3");
        } else if (m_intent.getBooleanExtra("fromModi", false)){
            ((TextView) findViewById(R.id.tv_title)).setText("주거 정보 수정");
            ((TextView) findViewById(R.id.tv_next)).setText("완료");
        }

        if (m_userHealthBase.member_dwelling>0) {
            m_nSelectedIndex = m_userHealthBase.member_dwelling;
            switch (m_nSelectedIndex){
                case 1:                    m_llyDwell1.setSelected(true);                    break;
                case 2:                    m_llyDwell2.setSelected(true);                    break;
                case 3:                    m_llyDwell3.setSelected(true);                    break;
                case 4:                    m_llyDwell4.setSelected(true);                    break;
            }
            checkNext();
        }
    }

    public boolean checkNext() {
        if (m_nSelectedIndex == 0) {
            findViewById(R.id.tv_next).setSelected(false);
            return false;
        }
        findViewById(R.id.tv_next).setSelected(true);
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_back:
                if (m_intent.getBooleanExtra("fromModi", false)){
                    finish();
                } else { // isAdd 또는 registerSNS 또는 email 가입중인 경우
                    m_userHealthBase.member_dwelling = m_nSelectedIndex;

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
            case R.id.tv_next:
                if (m_nSelectedIndex < 1) {
                    Toast.makeText(this, "주거정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                m_userHealthBase.member_dwelling = m_nSelectedIndex;
                UserManager.getInstance().currentFamIndex = index;
                UserManager.getInstance().HealthBaseInfo.get(index).setUserHealthBase(m_userHealthBase);
                UserManager.getInstance().HealthDetailInfo.get(index).setUserHealthDetail(m_userHealthDetail);

                if (m_intent.getBooleanExtra("fromModi", false)) {
                    m_nConnectType = NetUtil.apis_STORE_INFO_LIVING;
                    mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, true);
                } else {
                    intent = new Intent(this, ManageBodyInfoActivity.class);
                    intent.putExtra("fam_index", index);
                    intent.putExtra("HealthBase", m_userHealthBase);
                    intent.putExtra("HealthDetail", m_userHealthDetail);
                    intent.putExtra("registerEMAIL", getIntent().getBooleanExtra("registerEMAIL", false));
                    intent.putExtra("registerSNS", getIntent().getBooleanExtra("registerSNS", false));
                    intent.putExtra("isAdd", getIntent().getBooleanExtra("isAdd", false));
                    intent.putExtra("fromReco", getIntent().getBooleanExtra("fromReco", false));

                    startActivityForResult(intent, 142);
                }
                break;
            case R.id.lly_dwell_1:
                m_nSelectedIndex = 1;
                m_llyDwell1.setSelected(true);
                m_llyDwell1.setTranslationX(6);
                m_llyDwell2.setSelected(false);
                m_llyDwell3.setSelected(false);
                m_llyDwell4.setSelected(false);
                checkNext();
                break;
            case R.id.lly_dwell_2:
                m_nSelectedIndex = 2;
                m_llyDwell1.setSelected(false);
                m_llyDwell2.setSelected(true);
                m_llyDwell2.setTranslationX(6);
                m_llyDwell3.setSelected(false);
                m_llyDwell4.setSelected(false);
                checkNext();
                break;
            case R.id.lly_dwell_3:
                m_nSelectedIndex = 3;
                m_llyDwell1.setSelected(false);
                m_llyDwell2.setSelected(false);
                m_llyDwell3.setSelected(true);
                m_llyDwell3.setTranslationX(6);
                m_llyDwell4.setSelected(false);
                checkNext();
                break;
            case R.id.lly_dwell_4:
                m_nSelectedIndex = 4;
                m_llyDwell1.setSelected(false);
                m_llyDwell2.setSelected(false);
                m_llyDwell3.setSelected(false);
                m_llyDwell4.setSelected(true);
                m_llyDwell4.setTranslationX(6);
                checkNext();
                break;
        }
    }

    private void processForNetEnd() {
        try {
            m_nResultType = _jResult.getInt(Net.NET_VALUE_CODE);
        } catch (JSONException e) {e.printStackTrace();}

        closeProgress();
        HttpRequester.getInstance().stopNetThread();

        int resultCode = HttpRequester.getInstance().getResultCode();
        String strMsg = HttpRequester.getInstance().getResultMsg();
        if (resultCode == Net.CONNECTION_SUCCSES) {
            if (m_nResultType == 200) {
                UserManager.getInstance().HealthBaseInfo.get(index).setUserHealthBase(m_userHealthBase);
                setResult(RESULT_OK);
                finish();
            }
        } else {if (!"".equals(strMsg)) {Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();}}
    }

    @Override
    public void onParseJSon(JSONObject j_source) {_jResult = j_source;}

    @Override
    protected void onActivityResult(int p_requestCode,
                                    int p_resultCode,
                                    Intent p_intentActivity) {
        super.onActivityResult(p_requestCode, p_resultCode, p_intentActivity);
        if (p_resultCode == RESULT_OK) {
            switch (p_requestCode) {
                case 142:
                    setResult(RESULT_OK, p_intentActivity);
                    finish();
                    break;
            }
            setResult(RESULT_OK);
            finish();
        }else if (p_resultCode == RESULT_CANCELED) {
            m_intent = p_intentActivity;
            updateInfo();
        }
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Net.THREAD_REQUEST_END) {
                processForNetEnd();            }
        }
    };

    JSONObject _jResult;

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.tv_back));
    }
}
