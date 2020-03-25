package app.vitamiin.com.setting;

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
import app.vitamiin.com.login.DetailSurveyActivity;
import app.vitamiin.com.login.ListSelectDialog;

/**
 * Created by dong8 on 2016-11-18.
 */

public class ModifyDetailLifeActivity extends BaseActivity implements View.OnClickListener, OnParseJSonListener {
    int m_nResultType = 200;
    NetUtil.connectAndgetServer mNetUtilConnetServer;
    Context m_context;

    TextView m_tvMarry, m_tvUnmarry, m_tv_sleep, m_tv_drink_amount, m_tv_drink_count, m_tv_smoke, m_tv_exercise,
            m_tvAllergySm, m_tvPeeSm, m_tvDungSm, m_tvLifePt, m_tvEatPt,
            m_tv1, m_tv2, m_tv3, m_tv4;
    int index;
    int int_m_tv_sleep = -1, int_m_tv_drink_amount = -1, int_m_tv_drink_count = -1, int_m_tv_smoke = -1, int_m_tv_exercise = -1;
    String m_nAllergySm = null, m_nPeeSm = null, m_nDungSm = null, m_nLifePt = null, m_nEatPt = null,
            m_n1 = null, m_n2 = null, m_n3 = null, m_n4 = null;
    int minmax[];

    int CON_saveAllDetail = 11;
    int m_nConnectType = CON_saveAllDetail;

    ListSelectDialog m_selectLifeDlg = null;
    UserHealthDetail _userHealthDetail = new UserHealthDetail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_context = this;
        setContentView(R.layout.activity_modify_detail);
        mNetUtilConnetServer = new NetUtil().new connectAndgetServer(m_context);

        index = getIntent().getIntExtra("fam_index",-1);
        _userHealthDetail = (UserHealthDetail)getIntent().getSerializableExtra("_userHealthDetail");

        initView();
    }

    private void initView() {
        findViewById(R.id.imv_back).setOnClickListener(this);
        findViewById(R.id.tv_save_exit).setOnClickListener(this);

        m_tvUnmarry = (TextView) findViewById(R.id.tv_un_marry);
        m_tvMarry = (TextView) findViewById(R.id.tv_marry);
        m_tvUnmarry.setOnClickListener(this);
        m_tvMarry.setOnClickListener(this);
        m_tv_sleep = (TextView) findViewById(R.id.tv_sleep);
        m_tv_drink_amount = (TextView) findViewById(R.id.tv_drink_amount);
        m_tv_drink_count = (TextView) findViewById(R.id.tv_drink_count);
        m_tv_smoke = (TextView) findViewById(R.id.tv_smoke);
        m_tv_exercise = (TextView) findViewById(R.id.tv_exercise);
        findViewById(R.id.lly_sleep).setOnClickListener(this);
        findViewById(R.id.lly_drink_amount).setOnClickListener(this);
        findViewById(R.id.lly_drink_count).setOnClickListener(this);
        findViewById(R.id.lly_smoke).setOnClickListener(this);
        findViewById(R.id.lly_exercise).setOnClickListener(this);

        m_tvAllergySm = (TextView) findViewById(R.id.tv_allergy_symptom);
        m_tvPeeSm = (TextView) findViewById(R.id.tv_pee_symptom);
        m_tvDungSm = (TextView) findViewById(R.id.tv_dung_symptom);
        m_tvLifePt = (TextView) findViewById(R.id.tv_life_pattern);
        m_tvEatPt = (TextView) findViewById(R.id.tv_eat_pattern);
        findViewById(R.id.lly_allergy_symptom).setOnClickListener(this);
        findViewById(R.id.lly_pee_symptom).setOnClickListener(this);
        findViewById(R.id.lly_dung_symptom).setOnClickListener(this);
        findViewById(R.id.lly_life_pattern).setOnClickListener(this);
        findViewById(R.id.lly_eat_pattern).setOnClickListener(this);

        m_tv1 = (TextView) findViewById(R.id.tv_1);
        m_tv2 = (TextView) findViewById(R.id.tv_2);
        m_tv3 = (TextView) findViewById(R.id.tv_3);
        m_tv4 = (TextView) findViewById(R.id.tv_4);
        findViewById(R.id.lly_1).setOnClickListener(this);
        findViewById(R.id.lly_2).setOnClickListener(this);
        findViewById(R.id.lly_3).setOnClickListener(this);
        findViewById(R.id.lly_4).setOnClickListener(this);

        m_tvUnmarry.setSelected(_userHealthDetail.member_marry == 0);
        m_tvMarry.setSelected(!(_userHealthDetail.member_marry == 0));
        setLife(101, _userHealthDetail.member_sleep);
        setLife(102, _userHealthDetail.member_drink_amount);
        setLife(103, _userHealthDetail.member_drink_count);
        setLife(104, _userHealthDetail.member_smoke);
        setLife(105, _userHealthDetail.member_exercise);

        setLife(18, _userHealthDetail.member_allergy_symptom);
        setLife(19, _userHealthDetail.member_pee_symptom);
        setLife(20, _userHealthDetail.member_dung_symptom);
        setLife(21, _userHealthDetail.member_life_pattern);
        setLife(22, _userHealthDetail.member_eat_pattern);

        setLife(25, _userHealthDetail.member_allergy);
        setLife(26, _userHealthDetail.member_eat_drug);
        setLife(27, _userHealthDetail.member_eat_healthfood);
        setLife(28, _userHealthDetail.member_health_state);
    }

    public void setLife(int type, String strValue) {
        if (strValue == null || strValue.length() == 0) return;
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
        else if (type == 25) {
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
    }

    public void setLife(int type, int index) {
        if (index == -1) return;

        if (type == 101) {
            m_tv_sleep.setText(getResources().getStringArray(R.array.life_sleep)[index]);
            int_m_tv_sleep = index;
        } else if (type == 102) {
            m_tv_drink_amount.setText(getResources().getStringArray(R.array.life_drink_amount)[index]);
            int_m_tv_drink_amount = index;
        } else if (type == 103) {
            m_tv_drink_count.setText(getResources().getStringArray(R.array.life_drink_count)[index]);
            int_m_tv_drink_count = index;
        } else if (type == 104) {
            m_tv_smoke.setText(getResources().getStringArray(R.array.life_smoke)[index]);
            int_m_tv_smoke = index;
        } else if (type == 105) {
            m_tv_exercise.setText(getResources().getStringArray(R.array.life_exercise)[index]);
            int_m_tv_exercise = index;
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.imv_back:
                finish();
                break;
            case R.id.tv_save_exit:
                _userHealthDetail.member_marry = m_tvUnmarry.isSelected()? 0:1;
                _userHealthDetail.member_sleep = int_m_tv_sleep;
                _userHealthDetail.member_drink_amount = int_m_tv_drink_amount;
                _userHealthDetail.member_drink_count = int_m_tv_drink_count;
                _userHealthDetail.member_smoke = int_m_tv_smoke;
                _userHealthDetail.member_exercise = int_m_tv_exercise;

                _userHealthDetail.member_allergy_symptom = m_nAllergySm;
                _userHealthDetail.member_pee_symptom = m_nPeeSm;
                _userHealthDetail.member_dung_symptom = m_nDungSm;
                _userHealthDetail.member_life_pattern = m_nLifePt;
                _userHealthDetail.member_eat_pattern = m_nEatPt;

                _userHealthDetail.member_allergy = m_n1;
                _userHealthDetail.member_eat_drug = m_n2;
                _userHealthDetail.member_eat_healthfood = m_n3;
                _userHealthDetail.member_health_state = m_n4;

                UserManager.getInstance().currentFamIndex = index;

                m_nConnectType = NetUtil.apis_STORE_INFO_DETAIL_ALL;
                mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, _userHealthDetail);
            break;
            case R.id.tv_un_marry:
                m_tvMarry.setSelected(false);
                m_tvUnmarry.setSelected(true);
                break;
            case R.id.tv_marry:
                m_tvMarry.setSelected(true);
                m_tvUnmarry.setSelected(false);
                break;
            case R.id.lly_sleep:
                m_selectLifeDlg = null;
                m_selectLifeDlg = new ListSelectDialog(this, "수면 시간 (일 평균)", getResources().getStringArray(R.array.life_sleep), 101, int_m_tv_sleep);
                m_selectLifeDlg.show();
                break;
            case R.id.lly_drink_amount:
                m_selectLifeDlg = null;
                m_selectLifeDlg = new ListSelectDialog(this, "음주량 (회당)", getResources().getStringArray(R.array.life_drink_amount), 102, int_m_tv_drink_amount);
                m_selectLifeDlg.show();
                break;
            case R.id.lly_drink_count:
                m_selectLifeDlg = null;
                m_selectLifeDlg = new ListSelectDialog(this, "음주 빈도 (주 평균)", getResources().getStringArray(R.array.life_drink_count), 103, int_m_tv_drink_count);
                m_selectLifeDlg.show();
                break;
            case R.id.lly_smoke:
                m_selectLifeDlg = null;
                m_selectLifeDlg = new ListSelectDialog(this, "흡연 (일 평균)", getResources().getStringArray(R.array.life_smoke), 104, int_m_tv_smoke);
                m_selectLifeDlg.show();
                break;
            case R.id.lly_exercise:
                m_selectLifeDlg = null;
                m_selectLifeDlg = new ListSelectDialog(this, "운동 (주 평균)", getResources().getStringArray(R.array.life_exercise), 105, int_m_tv_exercise);
                m_selectLifeDlg.show();
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
        closeProgress();
        HttpRequester.getInstance().stopNetThread();

        int resultCode = HttpRequester.getInstance().getResultCode();
        String strMsg = HttpRequester.getInstance().getResultMsg();

        if (resultCode == Net.CONNECTION_SUCCSES) { // 성공
            if (m_nResultType == 200) {
                UserManager.getInstance().HealthDetailInfo.get(index).setUserHealthDetail(_userHealthDetail);
                UserManager.getInstance().HealthDetailInfo.get(index).isset=true;
                setResult(RESULT_OK);
                finish();
            }
        } else {    // 실패
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
                case 11:
                    m_tvAllergySm.setText(p_intentActivity.getStringExtra("text"));
                    m_nAllergySm = p_intentActivity.getStringExtra("allergy_sm");
                    break;
                case 22:
                    m_tvPeeSm.setText(p_intentActivity.getStringExtra("text"));
                    m_nPeeSm = p_intentActivity.getStringExtra("pee_sm");
                    break;
                case 33:
                    m_tvDungSm.setText(p_intentActivity.getStringExtra("text"));
                    m_nDungSm = p_intentActivity.getStringExtra("dung_sm");
                    break;
                case 44:
                    m_tvLifePt.setText(p_intentActivity.getStringExtra("text"));
                    m_nLifePt = p_intentActivity.getStringExtra("life_pt");
                    break;
                case 55:
                    m_tvEatPt.setText(p_intentActivity.getStringExtra("text"));
                    m_nEatPt = p_intentActivity.getStringExtra("eat_pt");
                    break;
                case 111:
                    m_tv1.setText(p_intentActivity.getStringExtra("text"));
                    m_n1 = p_intentActivity.getStringExtra("allergy_fd");
                    break;
                case 222:
                    m_tv2.setText(p_intentActivity.getStringExtra("text"));
                    m_n2 = p_intentActivity.getStringExtra("drug");
                    break;
                case 333:
                    m_tv3.setText(p_intentActivity.getStringExtra("text"));
                    m_n3 = p_intentActivity.getStringExtra("health_fd");
                    break;
                case 444:
                    m_tv4.setText(p_intentActivity.getStringExtra("text"));
                    m_n4 = p_intentActivity.getStringExtra("health_stt");
                    break;
            }
        }
    }

    @Override
    public void onParseJSon(JSONObject j_source) {_jResult = j_source;}

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.imv_back));
    }
}
