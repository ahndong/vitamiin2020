package app.vitamiin.com.setting;

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

import app.vitamiin.com.Adapter.NoticeListAdapter;
import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.Model.NoticeInfo;
import app.vitamiin.com.R;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;

public class FaqNoticeActivity extends BaseActivity implements View.OnClickListener, OnParseJSonListener, AbsListView.OnScrollListener, AdapterView.OnItemClickListener {
    int m_nResultType = 200, m_currentPage = 1, m_nMaxPage = 1,  m_type = 0;
    boolean m_bLockListView = false;
    boolean from_source = false;
    boolean from_content_dialog = false;
    boolean write_activity = false;

    TextView m_tvTitle;
    ListView m_lsvList;
    NoticeListAdapter m_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        initView();
        connectServer();
    }

    private void initView() {
        findViewById(R.id.imv_back).setOnClickListener(this);

        m_tvTitle = (TextView) findViewById(R.id.tvTitle);
        m_lsvList = (ListView) findViewById(R.id.lsv_list);
        m_adapter = new NoticeListAdapter(this, new ArrayList<NoticeInfo>());
        m_lsvList.setAdapter(m_adapter);
        m_lsvList.setOnScrollListener(this);
        m_lsvList.setOnItemClickListener(this);

        from_source = getIntent().getBooleanExtra("from_source", false);
        from_content_dialog = getIntent().getBooleanExtra("from_content_dialog", false);
        write_activity = getIntent().getBooleanExtra("write_activity", false);

        m_type = getIntent().getIntExtra("type",0);
        switch (m_type){
            case 0:
                m_tvTitle.setText("FAQ");
                break;
            case 1:
                m_tvTitle.setText("공지사항");
                break;
        }
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

    private void connectServer() {
        m_bLockListView = true;

        showProgress();
        String[] paramFields = new String[]{Net.NET_POST_FIELD_ACT,
                Net.NET_POST_FIELD_REQUEST};
        String[] paramValues = null;

        JSONObject w_objJSonData = new JSONObject();
        try {
            w_objJSonData.put(Net.NET_VALUE_MEMBER_ID, UserManager.getInstance().member_id);
            w_objJSonData.put("page", "" + m_currentPage);
            if(m_type==0) {
                paramValues = new String[]{
                        Net.POST_FIELD_ACT_VIEW_FAQ,
                        w_objJSonData.toString()};
            } else {
                paramValues = new String[]{
                        Net.POST_FIELD_ACT_VIEW_NOTICE,
                        w_objJSonData.toString()};
            }
        }
        catch (Exception e) {e.printStackTrace();}

        String netUrl = Net.URL_SERVER + Net.URL_SERVER_API;
        HttpRequester.getInstance().init(this, this, handler, netUrl,
                paramFields, paramValues, false);

        HttpRequester.getInstance().setProgressMessage(
                Net.NET_COMMON_STRING_WAITING);
        HttpRequester.getInstance().startNetThread();
    }

    private void processForNetEnd() {
        parseJSON();
        closeProgress();
        HttpRequester.getInstance().stopNetThread();

        int resultCode = HttpRequester.getInstance().getResultCode();
        String strMsg = HttpRequester.getInstance().getResultMsg();

        if (resultCode == Net.CONNECTION_SUCCSES) {
            if (m_nResultType == 200) {
                if(from_source)
                    if(from_content_dialog)
                        for (int i = 2; i < 4; i++)         m_adapter.getItem(i).view = true;
                    else
                        for (int i = 0; i < 5; i++)         m_adapter.getItem(i).view = true;
                else if(write_activity)
                    m_adapter.getItem(7).view = true;
            }
        } else {
            if (!"".equals(strMsg)) {Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();}
        }
        m_bLockListView = false;
    }

    public void parseJSON() {
        try {
            m_nResultType = _jResult.getInt(Net.NET_VALUE_CODE); //
            JSONObject result = _jResult.getJSONObject(Net.NET_VALUE_RESULT);
            m_nMaxPage = result.getInt(Net.NET_VALUE_TOTAL_PAGE);
            JSONArray arr;
            NoticeInfo info;
            if(m_type==0)
                arr = result.getJSONArray("faqList");
            else
                arr = result.getJSONArray("noticeList");

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                info = new NoticeInfo();
                info._subject = obj.getString("subject");
                info._content = obj.getString("contents");
                info.regdate = obj.getString("regdate");
                m_adapter.add(info);
            }
            if(m_adapter.getCount() > 5){
                if(from_source)
                    if(from_content_dialog)
                        for (int i = 2; i < 4; i++)         m_adapter.getItem(i).view = true;
                    else
                        for (int i = 0; i < 5; i++)         m_adapter.getItem(i).view = true;
                else if(write_activity)
                    m_adapter.getItem(7).view = true;
            }
        } catch (JSONException e) {e.printStackTrace();}
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {}

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int count = totalItemCount - visibleItemCount;
        if (firstVisibleItem >= count && totalItemCount != 1
                && m_currentPage < m_nMaxPage && !m_bLockListView) {
            m_currentPage = m_currentPage + 1;
            connectServer();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        m_adapter.getItem(position).view = !m_adapter.getItem(position).view;
        m_adapter.notifyDataSetChanged();
    }

    JSONObject _jResult;
    @Override
    public void onParseJSon(JSONObject j_source) {
        _jResult = j_source;}

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Net.THREAD_REQUEST_END) {processForNetEnd();            }
        }
    };
}
