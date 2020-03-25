package app.vitamiin.com.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.vitamiin.com.Adapter.GoodListAdapter;
import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.Model.GoodInfo;
import app.vitamiin.com.R;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;

public class GoodListActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, OnParseJSonListener {
    NetUtil mNetUtil;
    int m_nResultType = 200;

    ListView m_lsvList;
    GoodListAdapter m_adapter;
    PullRefreshLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_list);
        mNetUtil = new NetUtil();

        initView();
        connectServer();
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_title)).setText(getIntent().getStringExtra("list_name"));

        findViewById(R.id.imv_back).setOnClickListener(this);

        m_lsvList = (ListView) findViewById(R.id.lsv_list);
        m_adapter = new GoodListAdapter(this, new ArrayList<GoodInfo>(), getIntent().getBooleanExtra("isRank", false));
        m_lsvList.setAdapter(m_adapter);

        m_lsvList.setOnItemClickListener(this);

        layout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        layout.setRefreshStyle(PullRefreshLayout.STYLE_SMARTISAN);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                m_adapter.clear();
                connectServer();
                layout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imv_back:
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.imv_back));
    }

    int m_nSeletedPos = -1;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GoodInfo info = m_adapter.getItem(position);
        if (info == null)
            return;
        m_nSeletedPos = position;

        Intent intent = new Intent(this, DetailGoodActivity.class);
        intent.putExtra("info", info);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int p_requestCode,
                                    int p_resultCode,
                                    Intent p_intentActivity) {

        super.onActivityResult(p_requestCode, p_resultCode, p_intentActivity);
        if (p_resultCode == RESULT_OK) {
            m_adapter.clear();
            connectServer();
        }
    }

    private void connectServer() {
        showProgress();
        String[] paramFields = new String[]{Net.NET_POST_FIELD_ACT,
                Net.NET_POST_FIELD_REQUEST};
        String[] paramValues = null;

        JSONObject w_objJSonData = new JSONObject();
        try {
            w_objJSonData.put(Net.NET_VALUE_MEMBER_ID, UserManager.getInstance().member_id);
            if (getIntent().getBooleanExtra("isRank", false))
                w_objJSonData.put("order", "rate");
            else if (getIntent().getBooleanExtra("isRecent", false))
                w_objJSonData.put("order", "regdate");
            paramValues = new String[]{
                    Net.POST_FIELD_ACT_DETAILLIST,
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
                processForNetEnd();            }
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
                m_adapter.notifyDataSetChanged();
            }
        } else {
            if (!"".equals(strMsg)) {
                Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    JSONObject _jResult;

    public void parseJSON() {
        try {
            m_nResultType = _jResult.getInt(Net.NET_VALUE_CODE); //

            JSONObject obj = _jResult.getJSONObject(Net.NET_VALUE_RESULT);
            JSONArray arr = obj.getJSONArray("list");
            for (int i = 0; i < arr.length(); i++)
                m_adapter.add(mNetUtil.parseGood(arr.getJSONObject(i)));
        } catch (JSONException e) {e.printStackTrace();}
    }

    @Override
    public void onParseJSon(JSONObject j_source) {_jResult = j_source;}
}
