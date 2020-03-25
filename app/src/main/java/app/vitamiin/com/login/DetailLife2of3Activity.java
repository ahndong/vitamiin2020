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

public class DetailLife2of3Activity extends BaseActivity implements View.OnClickListener, OnParseJSonListener {
    int m_nResultType = 200;
    Context m_context;
    NetUtil.connectAndgetServer mNetUtilConnetServer;
    int m_nConnectType;

    TextView m_tvAllergySm, m_tvPeeSm, m_tvDungSm, m_tvLifePt, m_tvEatPt;
    String m_nAllergySm = null, m_nPeeSm = null, m_nDungSm = null, m_nLifePt = null, m_nEatPt = null;
    Boolean doAutoStep =false;
    int minmax[];

    int index;
    UserHealthDetail _userHealthDetail = new UserHealthDetail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNetUtilConnetServer = new NetUtil().new connectAndgetServer(this);
        setContentView(R.layout.activity_detail_life_2of3);

        m_context = this;
        initView();
    }

    private void initView() {
        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_next).setOnClickListener(this);

        findViewById(R.id.lly_allergy_symptom).setOnClickListener(this);
        findViewById(R.id.lly_pee_symptom).setOnClickListener(this);
        findViewById(R.id.lly_dung_symptom).setOnClickListener(this);
        findViewById(R.id.lly_life_pattern).setOnClickListener(this);
        findViewById(R.id.lly_eat_pattern).setOnClickListener(this);

        m_tvAllergySm = (TextView) findViewById(R.id.tv_allergy_symptom);
        m_tvPeeSm = (TextView) findViewById(R.id.tv_pee_symptom);
        m_tvDungSm = (TextView) findViewById(R.id.tv_dung_symptom);
        m_tvLifePt = (TextView) findViewById(R.id.tv_life_pattern);
        m_tvEatPt = (TextView) findViewById(R.id.tv_eat_pattern);

        m_tvAllergySm = (TextView) findViewById(R.id.tv_allergy_symptom);
        m_tvAllergySm.setOnClickListener(this);
        m_tvPeeSm = (TextView) findViewById(R.id.tv_pee_symptom);
        m_tvPeeSm.setOnClickListener(this);
        m_tvDungSm = (TextView) findViewById(R.id.tv_dung_symptom);
        m_tvDungSm.setOnClickListener(this);
        m_tvLifePt = (TextView) findViewById(R.id.tv_life_pattern);
        m_tvLifePt.setOnClickListener(this);
        m_tvEatPt = (TextView) findViewById(R.id.tv_eat_pattern);
        m_tvEatPt.setOnClickListener(this);

        index = getIntent().getIntExtra("fam_index",-1);
        _userHealthDetail = (UserHealthDetail)getIntent().getSerializableExtra("_userHealthDetail");

        if (getIntent().getBooleanExtra("edit_from_recomfragment", false)) {
        }

        setLife(18, _userHealthDetail.member_allergy_symptom);
        setLife(19, _userHealthDetail.member_pee_symptom);
        setLife(20, _userHealthDetail.member_dung_symptom);
        setLife(21, _userHealthDetail.member_life_pattern);
        setLife(22, _userHealthDetail.member_eat_pattern);

        checkNext();
        doAutoStep=true;
        startStep();
    }

    public void setLife(int type, String strValue) {
        if (strValue.equals("null") || strValue == null || strValue.length() == 0) return;
        if (type == 18) {
            String[] list = getResources().getStringArray(R.array.life_allergy_symptom);
            String[] arr = strValue.split(",");
            String text = "";
            for(String strOne:arr)      text += list[Integer.parseInt(strOne)] + ",";
            if (text.length() > 0)      text = text.substring(0, text.length() - 1);
            text = text.replace("\n", "");
            m_tvAllergySm.setText(text);
            m_nAllergySm = strValue;
        } else if (type == 19) {
            String[] list = getResources().getStringArray(R.array.life_pee_symptom);
            String[] arr = strValue.split(",");
            String text = "";
            for(String strOne:arr)      text += list[Integer.parseInt(strOne)] + ",";
            if (text.length() > 0)      text = text.substring(0, text.length() - 1);
            text = text.replace("\n", "");
            m_tvPeeSm.setText(text);
            m_nPeeSm = strValue;
        } else if (type == 20) {
            String[] list = getResources().getStringArray(R.array.life_dung_symptom);
            String[] arr = strValue.split(",");
            String text = "";
            for(String strOne:arr)      text += list[Integer.parseInt(strOne)] + ",";
            if (text.length() > 0)      text = text.substring(0, text.length() - 1);
            text = text.replace("\n", "");
            m_tvDungSm.setText(text);
            m_nDungSm = strValue;
        } else if (type == 21) {
            String[] list = getResources().getStringArray(R.array.life_life_pattern);
            String[] arr = strValue.split(",");
            String text = "";
            for(String strOne:arr)      text += list[Integer.parseInt(strOne)] + ",";
            if (text.length() > 0)      text = text.substring(0, text.length() - 1);
            text = text.replace("\n", "");
            m_tvLifePt.setText(text);
            m_nLifePt = strValue;
        } else if (type == 22) {
            String[] list = getResources().getStringArray(R.array.life_eat_pattern);
            String[] arr = strValue.split(",");
            String text = "";
            for(String strOne:arr)      text += list[Integer.parseInt(strOne)] + ",";
            if (text.length() > 0)      text = text.substring(0, text.length() - 1);
            text = text.replace("\n", "");
            m_tvEatPt.setText(text);
            m_nEatPt = strValue;
        }
        if(doAutoStep)startStep();
        checkNext();
    }

    public void startStep() {
        if(m_nAllergySm == null){
            onClick(findViewById(R.id.lly_allergy_symptom));
            return;
        }if(m_nPeeSm == null){
            onClick(findViewById(R.id.lly_pee_symptom));
            return;
        }if(m_nDungSm == null){
            onClick(findViewById(R.id.lly_dung_symptom));
            return;
        }if(m_nLifePt == null){
            onClick(findViewById(R.id.lly_life_pattern));
            return;
        }if(m_nEatPt == null){
            onClick(findViewById(R.id.lly_eat_pattern));
            return;
        }
        doAutoStep =false;
    }

    public boolean checkNext() {
        boolean ret = false;
        if(!(m_nAllergySm == null || m_nPeeSm == null || m_nDungSm == null ||
                m_nLifePt == null || m_nEatPt == null)){
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
                    _userHealthDetail.member_allergy_symptom = m_nAllergySm;
                    _userHealthDetail.member_pee_symptom = m_nPeeSm;
                    _userHealthDetail.member_dung_symptom = m_nDungSm;
                    _userHealthDetail.member_life_pattern = m_nLifePt;
                    _userHealthDetail.member_eat_pattern = m_nEatPt;

                    intent = new Intent();
                    intent.putExtra("_userHealthDetail", _userHealthDetail);
                    intent.putExtra("edit_from_recomfragment", getIntent().getBooleanExtra("edit_from_recomfragment", false));
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }
                break;
            case R.id.tv_next:
                if (m_nAllergySm == null) {
                    Toast.makeText(this, "부작용 혹은 알레르기 증상을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;                }
                if (m_nPeeSm == null) {
                    Toast.makeText(this, "소변 상태를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;                }
                if (m_nDungSm == null) {
                    Toast.makeText(this, "대변 상태를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;                }
                if (m_nLifePt == null) {
                    Toast.makeText(this, "생활 패턴을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;                }
                if (m_nEatPt == null) {
                    Toast.makeText(this, "식사 패턴을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;                }

                _userHealthDetail.member_allergy_symptom = m_nAllergySm;
                _userHealthDetail.member_pee_symptom = m_nPeeSm;
                _userHealthDetail.member_dung_symptom = m_nDungSm;
                _userHealthDetail.member_life_pattern = m_nLifePt;
                _userHealthDetail.member_eat_pattern = m_nEatPt;

                if (getIntent().getBooleanExtra("fromModi", false)) {
                    m_nConnectType = NetUtil.apis_STORE_INFO_DETAIL_2OF3;
                    mNetUtilConnetServer.connectServer(this, handler, m_nConnectType,
                            new String[] {String.valueOf(_userHealthDetail.member_name),
                                    String.valueOf(m_nAllergySm),
                                    String.valueOf(m_nPeeSm),
                                    String.valueOf(m_nDungSm),
                                    String.valueOf(m_nLifePt),
                                    String.valueOf(m_nEatPt)});
                } else {
                    intent = new Intent(this, DetailLife3of3Activity.class);
                    intent.putExtra("_userHealthDetail", _userHealthDetail);
                    intent.putExtra("fam_index", index);
                    intent.putExtra("edit_from_recomfragment", getIntent().getBooleanExtra("edit_from_recomfragment", false));
                    startActivityForResult(intent, 202);
                }
                break;
            case R.id.lly_allergy_symptom:
                intent = new Intent(this, DetailSurveyActivity.class);
                intent.putExtra("edit", (m_nAllergySm!=null && m_nAllergySm.length()>0));       //값이 있다면 true를 넘긴다.by 동현
                intent.putExtra("type", "allergy_sm");
                intent.putExtra("allergy_sm", m_nAllergySm);
                minmax = new int[] {1,5};
                intent.putExtra("minmax", minmax);
                startActivityForResult(intent, 11);
                break;
            case R.id.lly_pee_symptom:
                intent = new Intent(this, DetailSurveyActivity.class);
                intent.putExtra("edit", (m_nPeeSm!=null && m_nPeeSm.length()>0));       //값이 있다면 true를 넘긴다.by 동현
                intent.putExtra("type", "pee_sm");
                intent.putExtra("pee_sm", m_nPeeSm);
                minmax = new int[] {1,5};
                intent.putExtra("minmax", minmax);
                startActivityForResult(intent, 22);
                break;
            case R.id.lly_dung_symptom:
                intent = new Intent(this, DetailSurveyActivity.class);
                intent.putExtra("edit", (m_nDungSm!=null && m_nDungSm.length()>0));       //값이 있다면 true를 넘긴다.by 동현
                intent.putExtra("type", "dung_sm");
                intent.putExtra("dung_sm", m_nDungSm);
                minmax = new int[] {1,5};
                intent.putExtra("minmax", minmax);
                startActivityForResult(intent, 33);
                break;
            case R.id.lly_life_pattern:
                intent = new Intent(this, DetailSurveyActivity.class);
                intent.putExtra("edit", (m_nLifePt!=null && m_nLifePt.length()>0));       //값이 있다면 true를 넘긴다.by 동현
                intent.putExtra("type", "life_pt");
                intent.putExtra("life_pt", m_nLifePt);
                minmax = new int[] {1,5};
                intent.putExtra("minmax", minmax);
                startActivityForResult(intent, 44);
                break;
            case R.id.lly_eat_pattern:
                intent = new Intent(this, DetailSurveyActivity.class);
                intent.putExtra("edit", (m_nEatPt!=null && m_nEatPt.length()>0));       //값이 있다면 true를 넘긴다.by 동현
                intent.putExtra("type", "eat_pt");
                intent.putExtra("eat_pt", m_nEatPt);
                minmax = new int[] {1,5};
                intent.putExtra("minmax", minmax);
                startActivityForResult(intent, 55);
                break;
        }
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Net.THREAD_REQUEST_END) {processForNetEnd();}
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
                UserManager.getInstance().HealthDetailInfo.get(index).setUserHealthDetail(_userHealthDetail);
                setResult(RESULT_OK);
                finish();
            }
        } else {
            if (!"".equals(strMsg)) {Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();}
        }
    }

    @Override
    protected void onActivityResult(int p_requestCode,
                                    int p_resultCode,
                                    Intent p_intentActivity) {
        super.onActivityResult(p_requestCode, p_resultCode, p_intentActivity);
        if (p_resultCode == RESULT_OK) {
            switch (p_requestCode) {
                case 11:
                    m_tvAllergySm.setText(p_intentActivity.getStringExtra("text"));
                    m_nAllergySm = p_intentActivity.getStringExtra("allergy_sm");
                    startStep();
                    checkNext();
                    break;
                case 22:
                    m_tvPeeSm.setText(p_intentActivity.getStringExtra("text"));
                    m_nPeeSm = p_intentActivity.getStringExtra("pee_sm");
                    startStep();
                    checkNext();
                    break;
                case 33:
                    m_tvDungSm.setText(p_intentActivity.getStringExtra("text"));
                    m_nDungSm = p_intentActivity.getStringExtra("dung_sm");
                    startStep();
                    checkNext();
                    break;
                case 44:
                    m_tvLifePt.setText(p_intentActivity.getStringExtra("text"));
                    m_nLifePt = p_intentActivity.getStringExtra("life_pt");
                    startStep();
                    checkNext();
                    break;
                case 55:
                    m_tvEatPt.setText(p_intentActivity.getStringExtra("text"));
                    m_nEatPt = p_intentActivity.getStringExtra("eat_pt");
                    startStep();
                    checkNext();
                    break;
                case 202:
                    setResult(RESULT_OK, p_intentActivity);
                    finish();
                    break;
            }
        } else if(p_resultCode == 0){//back 눌러서 돌아온 경우 이곳으로 by 동현
            if(p_intentActivity.getSerializableExtra("_userHealthDetail")!=null)
                _userHealthDetail = (UserHealthDetail)p_intentActivity.getSerializableExtra("_userHealthDetail");
            getIntent().putExtra("edit_from_recomfragment", p_intentActivity.getBooleanExtra("edit_from_recomfragment", false));
        }
    }

    JSONObject _jResult;

    public void parseJSON() {
        try {
            m_nResultType = _jResult.getInt(Net.NET_VALUE_CODE); //
//            JSONObject result = _jResult.getJSONObject(Net.NET_VALUE_RESULT);
        } catch (JSONException e) {e.printStackTrace();}
    }

    @Override
    public void onParseJSon(JSONObject j_source) {_jResult = j_source;}

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.tv_back));
    }
}
