package app.vitamiin.com.setting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.vitamiin.com.Adapter.FollowAndFollowerListAdapter;
import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.ConfirmDialog;
import app.vitamiin.com.Model.FallowInfo;
import app.vitamiin.com.R;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;

public class FollowingAndFollowerActivity extends BaseActivity implements View.OnClickListener, OnParseJSonListener, AbsListView.OnScrollListener, AdapterView.OnItemClickListener {
    String current_page_owner_id = "", _selectedUser = "";
    FallowInfo delFal;

    int m_nResultType = 200;
    int CONNECT_TYPE_FALLOWING = 1;
    int CONNECT_TYPE_FALLOWER = 2;
    int CONNECT_TYPE_DISABLE_FALLOWING = 3;
    int m_nConnectType = CONNECT_TYPE_FALLOWING;

    int m_currentPage = 1, m_nMaxPage = 1, f_type = 1;
    boolean m_bLockListView = false;

    ListView m_lsvList;
    FollowAndFollowerListAdapter m_adapter;
    TextView m_tvCnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following_and_follower);

        current_page_owner_id = getIntent().getStringExtra("f_id");
        f_type = getIntent().getIntExtra("f_type", 1);
        initView();

        connectServer(f_type==1? CONNECT_TYPE_FALLOWING:CONNECT_TYPE_FALLOWER);
    }

//    @Override
//    protected void onRestart(){
//        super.onRestart();
//        setContentView(R.layout.activity_following_and_follower);
//
////        current_page_owner_id = getIntent().getStringExtra("f_id");
////        f_type = getIntent().getIntExtra("f_type", 1);
//        initView();
//
//        connectServer(f_type==1? CONNECT_TYPE_FALLOWING:CONNECT_TYPE_FALLOWER);
//    }

    private void initView() {
        findViewById(R.id.imv_back).setOnClickListener(this);

        m_lsvList = (ListView) findViewById(R.id.lsv_list);
        Boolean isMine = UserManager.getInstance().member_id.equals(current_page_owner_id);
        m_adapter = new FollowAndFollowerListAdapter(this, new ArrayList<FallowInfo>(), f_type, isMine);
        m_lsvList.setAdapter(m_adapter);
        m_lsvList.setOnScrollListener(this);
        m_lsvList.setOnItemClickListener(this);

        m_tvCnt = (TextView) findViewById(R.id.tv_f_and_f_cnt);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imv_back:
                Intent intent = new Intent();
                intent.putExtra("f_count", m_adapter.getCount());
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int count = totalItemCount - visibleItemCount;
        if (firstVisibleItem >= count && totalItemCount != 1
                && m_currentPage < m_nMaxPage && !m_bLockListView) {
            m_currentPage = m_currentPage + 1;
            connectServer(f_type==1? CONNECT_TYPE_FALLOWING:CONNECT_TYPE_FALLOWER);
        }
    }

    public void disableFallowing(FallowInfo info) {
        delFal = info;
        _selectedUser = info.mb_id;

//        new NoticeDialog(this, "팔로잉 취소", info._nick + "님을 팔로잉하기를 \n 취소하시겠습니까?", "확인", false).show();
        new ConfirmDialog(this, "팔로잉 취소", info._nick + "님을 팔로잉하기를 \n 취소하시겠습니까?", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_adapter.remove(delFal);
                connectServer(CONNECT_TYPE_DISABLE_FALLOWING);
            }
        }).show();
    }

    private void connectServer(int strAct) {
        m_bLockListView = true;
        m_nConnectType = strAct;

        showProgress();
        String[] paramFields = new String[]{Net.NET_POST_FIELD_ACT,
                Net.NET_POST_FIELD_REQUEST};
        String[] paramValues = null;

        JSONObject w_objJSonData = new JSONObject();
        try {
            w_objJSonData.put(Net.NET_VALUE_MEMBER_ID, current_page_owner_id);
            if (m_nConnectType == CONNECT_TYPE_FALLOWING) {
                w_objJSonData.put("page", "" + m_currentPage);
                paramValues = new String[]{
                        Net.POST_FIELD_ACT_FALLOWING,
                        w_objJSonData.toString()};
            } else if (m_nConnectType == CONNECT_TYPE_FALLOWER) {
                w_objJSonData.put("page", "" + m_currentPage);
                paramValues = new String[]{
                        Net.POST_FIELD_ACT_FALLOWER,
                        w_objJSonData.toString()};
            } else if (m_nConnectType == CONNECT_TYPE_DISABLE_FALLOWING) {
                w_objJSonData.put("fallow_id", _selectedUser);
                paramValues = new String[]{
                        Net.POST_FIELD_ACT_DISABLE_FALLOWING,
                        w_objJSonData.toString()};
            }
        } catch (Exception e) {e.printStackTrace();}

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
                if (m_nConnectType == CONNECT_TYPE_FALLOWING || m_nConnectType == CONNECT_TYPE_DISABLE_FALLOWING) {
                    m_tvCnt.setText("팔로잉 (" + m_adapter.getCount() + ")명");
                } else if (m_nConnectType == CONNECT_TYPE_FALLOWER) {
                    m_tvCnt.setText("팔로워 (" + m_adapter.getCount() + ")명");
                }
                m_adapter.notifyDataSetChanged();
            }
        } else {
            if (!"".equals(strMsg)) {
                Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();}
        }
        m_bLockListView = false;
    }

    JSONObject _jResult;

    public void parseJSON() {
        try {
            m_nResultType = _jResult.getInt(Net.NET_VALUE_CODE); //
            if (m_nConnectType == CONNECT_TYPE_FALLOWING) {
                JSONObject result = _jResult.getJSONObject(Net.NET_VALUE_RESULT);
                m_nMaxPage = result.getInt(Net.NET_VALUE_TOTAL_PAGE);
                JSONArray arr = result.getJSONArray("list");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    FallowInfo info = new FallowInfo();
                    info.mb_id = obj.getString("fallow_id");
                    info._nick = obj.getString("f_nick");
                    info.imagePath = obj.getString("f_photo");
                    m_adapter.add(info);
                }
            } else if (m_nConnectType == CONNECT_TYPE_FALLOWER) {
                JSONObject result = _jResult.getJSONObject(Net.NET_VALUE_RESULT);
                m_nMaxPage = result.getInt(Net.NET_VALUE_TOTAL_PAGE);
                JSONArray arr = result.getJSONArray("list");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    FallowInfo info = new FallowInfo();
                    info.mb_id = obj.getString("mb_id");
                    info._nick = obj.getString("f_nick");
                    info.imagePath = obj.getString("f_photo");
                    m_adapter.add(info);
                }
            } else if (m_nConnectType == CONNECT_TYPE_DISABLE_FALLOWING) {
//                JSONArray result = _jResult.getJSONArray(Net.NET_VALUE_RESULT);
            }
        } catch (JSONException e) {e.printStackTrace();}
    }

    @Override
    public void onParseJSon(JSONObject j_source) {
        _jResult = j_source;}

    public void gotoUserProfile(FallowInfo info) {
        Intent intent = new Intent(this, MyPageActivity.class);
        intent.putExtra("f_type", f_type);
        intent.putExtra("mb_id", info.mb_id);
        intent.putExtra("mb_nick", info._nick);
        intent.putExtra("mb_photo", info.imagePath);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 100:
                    m_currentPage = 1;
                    m_nMaxPage = 1;
                    m_adapter.clear();
//                    connectServer(CONNECT_TYPE_FALLOWING);
                    connectServer(f_type==1? CONNECT_TYPE_FALLOWING:CONNECT_TYPE_FALLOWER);
                    break;
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.imv_back));
    }
}
