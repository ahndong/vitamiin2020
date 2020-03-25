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

import static android.os.SystemClock.sleep;

public class DetailLife1of3Activity extends BaseActivity implements View.OnClickListener, OnParseJSonListener {
    Context m_context;
    int m_nResultType = 200;
    NetUtil.connectAndgetServer mNetUtilConnetServer;
    public int m_nConnectType = 0;

    TextView m_tvUnmarry, m_tvMarry, m_tv_sleep, m_tv_drink_amount, m_tv_drink_count, m_tv_smoke, m_tv_exercise;
    int int_m_tv_sleep = -1, int_m_tv_drink_amount = -1, int_m_tv_drink_count = -1, int_m_tv_smoke = -1, int_m_tv_exercise = -1;
    Boolean doAutoStep =false;

    int index;
    UserHealthDetail _userHealthDetail = new UserHealthDetail();
    ListSelectDialog m_selectLifeDlg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNetUtilConnetServer = new NetUtil().new connectAndgetServer(this);
        setContentView(R.layout.activity_detail_life_1of3);
        m_context = this;

        initView();
    }

    private void initView() {
        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_next).setOnClickListener(this);

        m_tvUnmarry = (TextView) findViewById(R.id.tv_un_marry);
        m_tvMarry = (TextView) findViewById(R.id.tv_marry);
        m_tvUnmarry.setSelected(true);
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

        index = getIntent().getIntExtra("fam_index",-1);
        _userHealthDetail = (UserHealthDetail)getIntent().getSerializableExtra("_userHealthDetail");

        if (getIntent().getBooleanExtra("edit_from_recomfragment", false)) {
//            ((TextView) findViewById(R.id.tv_title)).setText("생활 정보 관리 1");
        }

        m_tvUnmarry.setSelected(_userHealthDetail.member_marry == 0);
        m_tvMarry.setSelected(!(_userHealthDetail.member_marry == 0));
        setLife(1, _userHealthDetail.member_sleep);
        setLife(2, _userHealthDetail.member_drink_amount);
        setLife(3, _userHealthDetail.member_drink_count);
        setLife(4, _userHealthDetail.member_smoke);
        setLife(5, _userHealthDetail.member_exercise);

        checkNext();
        doAutoStep=true;
        startStep();
    }

    public void setLife(int type, int index) {
        if (index == -1) return;

        if (type == 1) {
            m_tv_sleep.setText(getResources().getStringArray(R.array.life_sleep)[index]);
            int_m_tv_sleep = index;
        } else if (type == 2) {
            m_tv_drink_amount.setText(getResources().getStringArray(R.array.life_drink_amount)[index]);
            int_m_tv_drink_amount = index;
        } else if (type == 3) {
            m_tv_drink_count.setText(getResources().getStringArray(R.array.life_drink_count)[index]);
            int_m_tv_drink_count = index;
        } else if (type == 4) {
            m_tv_smoke.setText(getResources().getStringArray(R.array.life_smoke)[index]);
            int_m_tv_smoke = index;
        } else if (type == 5) {
            m_tv_exercise.setText(getResources().getStringArray(R.array.life_exercise)[index]);
            int_m_tv_exercise = index;
        }
        if(doAutoStep)startStep();
        checkNext();
    }

    public void startStep() {
        if(int_m_tv_sleep == -1){
            sleep(300);
            onClick(findViewById(R.id.lly_sleep));
            return;
        }if(int_m_tv_drink_amount == -1){
            sleep(300);
            onClick(findViewById(R.id.lly_drink_amount));
            return;
        }if(int_m_tv_drink_count == -1){
            sleep(300);
            onClick(findViewById(R.id.lly_drink_count));
            return;
        }if(int_m_tv_smoke == -1){
            sleep(300);
            onClick(findViewById(R.id.lly_smoke));
            return;
        }if(int_m_tv_exercise == -1){
            sleep(300);
            onClick(findViewById(R.id.lly_exercise));
            return;
        }
        doAutoStep =false;
    }

    public boolean checkNext() {
        boolean ret = false;
        if(!(int_m_tv_sleep == -1 || int_m_tv_drink_amount == -1 ||
                int_m_tv_drink_count == -1 || int_m_tv_smoke == -1 ||
                int_m_tv_exercise == -1)){
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
                finish();
                break;
            case R.id.tv_next:
                if (int_m_tv_sleep == -1) {
                    Toast.makeText(this, "수면 시간을 선택해 주세요", Toast.LENGTH_SHORT).show();
                    return;                }
                if (int_m_tv_drink_amount == -1) {
                    Toast.makeText(this, "음주량을 선택해 주세요", Toast.LENGTH_SHORT).show();
                    return;                }
                if (int_m_tv_drink_count == -1) {
                    Toast.makeText(this, "음주 빈도를 선택해 주세요", Toast.LENGTH_SHORT).show();
                    return;                }
                if (int_m_tv_smoke == -1) {
                    Toast.makeText(this, "흡연 빈도를 선택해 주세요", Toast.LENGTH_SHORT).show();
                    return;                }
                if (int_m_tv_exercise == -1) {
                    Toast.makeText(this, "운동 횟수를 선택해 주세요", Toast.LENGTH_SHORT).show();
                    return;                }

                _userHealthDetail.member_marry = m_tvUnmarry.isSelected()? 0:1;
                _userHealthDetail.member_sleep = int_m_tv_sleep;
                _userHealthDetail.member_drink_amount = int_m_tv_drink_amount;
                _userHealthDetail.member_drink_count = int_m_tv_drink_count;
                _userHealthDetail.member_smoke = int_m_tv_smoke;
                _userHealthDetail.member_exercise = int_m_tv_exercise;

                if (getIntent().getBooleanExtra("fromModi", false)) {       //finish는 커넥트 서버 아래에서 수행됨
                    m_nConnectType = NetUtil.apis_STORE_INFO_DETAIL_1OF3;
                    mNetUtilConnetServer.connectServer(this, handler, m_nConnectType,
                            new String[] {String.valueOf(_userHealthDetail.member_name),
                                          String.valueOf(_userHealthDetail.member_marry),
                                          String.valueOf(_userHealthDetail.member_sleep),
                                          String.valueOf(_userHealthDetail.member_drink_amount),
                                          String.valueOf(_userHealthDetail.member_drink_count),
                                          String.valueOf(_userHealthDetail.member_smoke),
                                          String.valueOf(_userHealthDetail.member_exercise)});
                } else {
                    intent = new Intent(this, DetailLife2of3Activity.class);
                    intent.putExtra("_userHealthDetail", _userHealthDetail);
                    intent.putExtra("fam_index", index);
                    intent.putExtra("edit_from_recomfragment", getIntent().getBooleanExtra("edit_from_recomfragment", false));
                    startActivityForResult(intent, 201);
                }
                break;
            case R.id.tv_un_marry:
                m_tvUnmarry.setSelected(true);
                m_tvMarry.setSelected(false);
                break;
            case R.id.tv_marry:
                m_tvUnmarry.setSelected(false);
                m_tvMarry.setSelected(true);
                break;
            case R.id.lly_sleep:
                m_selectLifeDlg = null;
                m_selectLifeDlg = new ListSelectDialog(this, "수면 시간 (일 평균)", getResources().getStringArray(R.array.life_sleep), 1, int_m_tv_sleep);
                m_selectLifeDlg.show();
                break;
            case R.id.lly_drink_amount:
                m_selectLifeDlg = null;
                m_selectLifeDlg = new ListSelectDialog(this, "음주량 (회당)", getResources().getStringArray(R.array.life_drink_amount), 2, int_m_tv_drink_amount);
                m_selectLifeDlg.show();
                break;
            case R.id.lly_drink_count:
                m_selectLifeDlg = null;
                m_selectLifeDlg = new ListSelectDialog(this, "음주 빈도 (주 평균)", getResources().getStringArray(R.array.life_drink_count), 3, int_m_tv_drink_count);
                m_selectLifeDlg.show();
                break;
            case R.id.lly_smoke:
                m_selectLifeDlg = null;
                m_selectLifeDlg = new ListSelectDialog(this, "흡연 (일 평균)", getResources().getStringArray(R.array.life_smoke), 4, int_m_tv_smoke);
                m_selectLifeDlg.show();
                break;
            case R.id.lly_exercise:
                m_selectLifeDlg = null;
                m_selectLifeDlg = new ListSelectDialog(this, "운동 (주 평균)", getResources().getStringArray(R.array.life_exercise), 5, int_m_tv_exercise);
                m_selectLifeDlg.show();
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

    JSONObject _jResult;

    public void parseJSON() {
        try {
            m_nResultType = _jResult.getInt(Net.NET_VALUE_CODE);
//            JSONObject result = _jResult.getJSONObject(Net.NET_VALUE_RESULT);
        } catch (JSONException e) {e.printStackTrace();}
    }

    @Override
    protected void onActivityResult(int p_requestCode,
                                    int p_resultCode,
                                    Intent p_intentActivity) {
        super.onActivityResult(p_requestCode, p_resultCode, p_intentActivity);
        if (p_resultCode == RESULT_OK) {
            switch (p_requestCode) {
                case 201:
//                    p_intentActivity.putExtra("fam_index", getIntent().getIntExtra("fam_index", -1));
                    setResult(RESULT_OK, p_intentActivity);
                    finish();
                    break;
            }
        } else if(p_resultCode == 0){   //back 눌러서 돌아온 경우 이곳으로 by 동현
            if(p_intentActivity.getSerializableExtra("_userHealthDetail")!=null)
                _userHealthDetail = (UserHealthDetail)p_intentActivity.getSerializableExtra("_userHealthDetail");
            getIntent().putExtra("edit_from_recomfragment", p_intentActivity.getBooleanExtra("edit_from_recomfragment", false));
        }
    }

    @Override
    public void onParseJSon(JSONObject j_source) {
        _jResult = j_source;
    }

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.tv_back));
    }
}
