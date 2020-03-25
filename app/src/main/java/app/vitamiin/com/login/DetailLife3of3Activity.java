package app.vitamiin.com.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.R;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.common.UserHealthDetail;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;

/**
 * Created by dong8 on 2017-04-05.
 */

public class DetailLife3of3Activity extends BaseActivity implements View.OnClickListener, OnParseJSonListener {
    int m_nResultType = 200;
    NetUtil.connectAndgetServer mNetUtilConnetServer;
    int m_nConnectType;

    Context m_context;
    TextView m_tv1, m_tv2, m_tv3, m_tv4;
    int index;
    int minmax[];
    String m_n1 = null, m_n2 = null, m_n3 = null, m_n4 = null;
    Boolean doAutoStep =false;

    UserHealthDetail _userHealthDetail = new UserHealthDetail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNetUtilConnetServer = new NetUtil().new connectAndgetServer(this);
        setContentView(R.layout.activity_detail_life_3of3);

        m_context = this;
        initView();
    }

    private void initView() {
        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_next).setOnClickListener(this);

        findViewById(R.id.lly_1).setOnClickListener(this);
        findViewById(R.id.lly_2).setOnClickListener(this);
        findViewById(R.id.lly_3).setOnClickListener(this);
        findViewById(R.id.lly_4).setOnClickListener(this);

        m_tv1 = (TextView) findViewById(R.id.tv_1);
        m_tv2 = (TextView) findViewById(R.id.tv_2);
        m_tv3 = (TextView) findViewById(R.id.tv_3);
        m_tv4 = (TextView) findViewById(R.id.tv_4);

        index = getIntent().getIntExtra("fam_index",-1);
        _userHealthDetail = (UserHealthDetail)getIntent().getSerializableExtra("_userHealthDetail");

        if (getIntent().getBooleanExtra("edit_from_recomfragment", false)) {
        }

        setLife(25, _userHealthDetail.member_allergy);
        setLife(26, _userHealthDetail.member_eat_drug);
        setLife(27, _userHealthDetail.member_eat_healthfood);
        setLife(28, _userHealthDetail.member_health_state);

        checkNext();
        doAutoStep=true;
        startStep();
    }

    public void setLife(int type, String strValue) {
        if (strValue.equals("null") || strValue == null || strValue.length() == 0) return;

        if (type == 25) {
            String[] list = getResources().getStringArray(R.array.array_allergy);
            String[] arr = strValue.split(",");
            String text = "";
            for(String strOne:arr)      text += list[Integer.parseInt(strOne)] + ",";
            if (text.length() > 0)      text = text.substring(0, text.length() - 1);
            text = text.replace("\n", "");
            m_tv1.setText(text);
            m_n1 = strValue;
        } else if (type == 26) {
            String[] list = getResources().getStringArray(R.array.array_eat_drug);
            String[] arr = strValue.split(",");
            String text = "";
            for(String strOne:arr)      text += list[Integer.parseInt(strOne)] + ",";
            if (text.length() > 0)      text = text.substring(0, text.length() - 1);
            text = text.replace("\n", "");
            m_tv2.setText(text);
            m_n2 = strValue;
        } else if (type == 27) {
            String[] list = getResources().getStringArray(R.array.array_eat_healthfood);
            String[] arr = strValue.split(",");
            String text = "";
            for(String strOne:arr)      text += list[Integer.parseInt(strOne)] + ",";
            if (text.length() > 0)      text = text.substring(0, text.length() - 1);
            text = text.replace("\n", "");
            m_tv3.setText(text);
            m_n3 = strValue;
        } else if (type == 28) {
            String[] list = getResources().getStringArray(R.array.array_health_state);
            String[] arr = strValue.split(",");
            String text = "";
            for(String strOne:arr)      text += list[Integer.parseInt(strOne)] + ",";
            if (text.length() > 0)      text = text.substring(0, text.length() - 1);
            text = text.replace("\n", "");
            m_tv4.setText(text);
            m_n4 = strValue;
        }
        if(doAutoStep)startStep();
        checkNext();
    }

    public void startStep() {
        if(m_n1 == null){
            onClick(findViewById(R.id.lly_1));
            return;
        }if(m_n2 == null){
            onClick(findViewById(R.id.lly_2));
            return;
        }if(m_n3 == null){
            onClick(findViewById(R.id.lly_3));
            return;
        }if(m_n4 == null){
            onClick(findViewById(R.id.lly_4));
            return;
        }
        doAutoStep =false;
    }

    public boolean checkNext() {
        boolean ret = false;
        if(!(m_n1 == null || m_n2 == null ||
                m_n3 == null || m_n4 == null)){
            ret = true;
        }
        findViewById(R.id.tv_next).setSelected(ret);
        return ret;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_back:
                if (getIntent().getBooleanExtra("fromModi", false)) {
                    finish();
                } else {
                    _userHealthDetail.member_allergy = m_n1;
                    _userHealthDetail.member_eat_drug = m_n2;
                    _userHealthDetail.member_eat_healthfood = m_n3;
                    _userHealthDetail.member_health_state = m_n4;

                    intent = new Intent();
                    intent.putExtra("_userHealthDetail", _userHealthDetail);
                    intent.putExtra("edit_from_recomfragment", getIntent().getBooleanExtra("edit_from_recomfragment", false));
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }
                break;
            case R.id.tv_next:
                if (m_n1 == null) {
                    Toast.makeText(this, "'없음'을 선택, 또는 알레르기 음식을 알려주세요", Toast.LENGTH_SHORT).show();
                    return;                }
                if (m_n2 == null) {
                    Toast.makeText(this, "'없음'을 선택, 또는 복약정보를 알려주세요", Toast.LENGTH_SHORT).show();
                    return;                }
                if (m_n3 == null) {
                    Toast.makeText(this, "'없음'을 선택, 또는 섭취중인 건강 기능식품을 알려주세요", Toast.LENGTH_SHORT).show();
                    return;                }
                if (m_n4 == null) {
                    Toast.makeText(this, "'없음'을 선택, 또는 최근 건강 상태를 알려주세요", Toast.LENGTH_SHORT).show();
                    return;                }

                _userHealthDetail.member_allergy = m_n1;
                _userHealthDetail.member_eat_drug = m_n2;
                _userHealthDetail.member_eat_healthfood = m_n3;
                _userHealthDetail.member_health_state = m_n4;


                if (getIntent().getBooleanExtra("fromModi", false)) {
                    m_nConnectType = NetUtil.apis_STORE_INFO_DETAIL_3OF3;
                    mNetUtilConnetServer.connectServer(this, handler, m_nConnectType,
                            new String[] {String.valueOf(_userHealthDetail.member_name),
                                          String.valueOf(m_n1),
                                          String.valueOf(m_n2),
                                          String.valueOf(m_n3),
                                          String.valueOf(m_n4)});
                } else {
                    m_nConnectType = NetUtil.apis_STORE_INFO_DETAIL_ALL;
                    mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, _userHealthDetail);
                }
                break;
            case R.id.lly_1:
                intent = new Intent(this, DetailSurveyActivity.class);
                intent.putExtra("edit", (m_n1 !=null && m_n1.length()>0));       //값이 있다면 true를 넘긴다.by 동현
                intent.putExtra("type", "allergy_fd");
                intent.putExtra("allergy_fd", m_n1);
                minmax = new int[] {1,5};
                intent.putExtra("minmax", minmax);
                startActivityForResult(intent, 111);
                break;
            case R.id.lly_2:
                intent = new Intent(this, DetailSurveyActivity.class);
                intent.putExtra("edit", (m_n2!=null && m_n2.length()>0));       //값이 있다면 true를 넘긴다.by 동현
                intent.putExtra("type", "drug");
                intent.putExtra("drug", m_n2);
                minmax = new int[] {1,5};
                intent.putExtra("minmax", minmax);
                startActivityForResult(intent, 222);
                break;
            case R.id.lly_3:
                intent = new Intent(this, DetailSurveyActivity.class);
                intent.putExtra("edit", (m_n3 !=null && m_n3.length()>0));       //값이 있다면 true를 넘긴다.by 동현
                intent.putExtra("type", "health_fd");
                intent.putExtra("health_fd", m_n3);
                minmax = new int[] {1,5};
                intent.putExtra("minmax", minmax);
                startActivityForResult(intent, 333);
                break;
            case R.id.lly_4:
                intent = new Intent(this, DetailSurveyActivity.class);
                intent.putExtra("edit", (m_n4 !=null && m_n4.length()>0));       //값이 있다면 true를 넘긴다.by 동현
                intent.putExtra("type", "health_stt");
                intent.putExtra("health_stt", m_n4);
                minmax = new int[] {1,3};
                intent.putExtra("minmax", minmax);
                startActivityForResult(intent, 444);
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
        parseJSON();
        closeProgress();
        HttpRequester.getInstance().stopNetThread();

        int resultCode = HttpRequester.getInstance().getResultCode();
        String strMsg = HttpRequester.getInstance().getResultMsg();

        if (resultCode == Net.CONNECTION_SUCCSES) {
            if (m_nResultType == 200) {
                if (m_nConnectType == NetUtil.apis_STORE_INFO_DETAIL_3OF3) {
                    UserManager.getInstance().HealthDetailInfo.get(index).setUserHealthDetail(_userHealthDetail);
                    setResult(RESULT_OK);
                    finish();
                } else if (m_nConnectType == NetUtil.apis_STORE_INFO_DETAIL_ALL) {
                    UserManager.getInstance().HealthDetailInfo.get(index).setUserHealthDetail(_userHealthDetail);
                    UserManager.getInstance().HealthDetailInfo.get(index).isset=true;
                    setResult(RESULT_OK);
                    finish();
                }
            }
        } else {
            if (!"".equals(strMsg)) {Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();}
        }
    }

    JSONObject _jResult;

    public void parseJSON() {
        try {m_nResultType = _jResult.getInt(Net.NET_VALUE_CODE); //
        } catch (JSONException e) {e.printStackTrace();}
    }

    @Override
    protected void onActivityResult(int p_requestCode,
                                    int p_resultCode,
                                    Intent p_intentActivity) {
        super.onActivityResult(p_requestCode, p_resultCode, p_intentActivity);
        if (p_resultCode == RESULT_OK) {
            switch (p_requestCode) {
                case 111:
                    m_tv1.setText(p_intentActivity.getStringExtra("text"));
                    m_n1 = p_intentActivity.getStringExtra("allergy_fd");
                    startStep();
                    checkNext();
                    break;
                case 222:
                    m_tv2.setText(p_intentActivity.getStringExtra("text"));
                    m_n2 = p_intentActivity.getStringExtra("drug");
                    startStep();
                    checkNext();
                    break;
                case 333:
                    m_tv3.setText(p_intentActivity.getStringExtra("text"));
                    m_n3 = p_intentActivity.getStringExtra("health_fd");
                    startStep();
                    checkNext();
                    break;
                case 444:
                    m_tv4.setText(p_intentActivity.getStringExtra("text"));
                    m_n4 = p_intentActivity.getStringExtra("health_stt");
                    startStep();
                    checkNext();
                    break;
            }
        }
    }

    @Override
    public void onParseJSon(JSONObject j_source) {_jResult = j_source;}

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.tv_back));
    }
}
