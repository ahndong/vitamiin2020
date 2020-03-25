package app.vitamiin.com.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import app.vitamiin.com.Adapter.SelectBrandAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.vitamiin.com.Model.GoodInfo;
import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.R;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.common.Util;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;

/**
 * Created by dong8 on 2016-12-28.
 */

public class SearchBrandDialog  extends BaseActivity implements View.OnClickListener, OnParseJSonListener, AbsListView.OnScrollListener, AdapterView.OnItemClickListener {
    private AppCompatActivity m_context;

    int CONNECT_TYPE_BUSINESS_LIST = 2;
    int m_nConnectType = CONNECT_TYPE_BUSINESS_LIST;
    int m_nResultType = 200;

    ListView m_lsvBrands;
    EditText m_edtSearch;
    TextView m_tvTitle, m_tvBrandCnt, m_tvProductCnt, m_tvDescMid;
    SelectBrandAdapter m_Adapter, m_Adapter_filtered;
    int m_nSelectedIndex = -1;
    int m_total_product_count=0;
    int m_currentPage = 1;
    int m_nMaxPage = 1;
    boolean m_bLockListView = false;

    String m_strSearchKeyword, m_strBusiness_ID_to_get;
    Integer m_sort_type;
    String m_filter_type;
    String[] m_arrFilter_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dlg_search_brand);

        m_context = this;
        initView();

        //updateList();
    }

//    public SearchBrandDialog (Context context, String filter_type, String[] filter_value, String[] brands, String[] brand_images, Integer[] cnt_prod) {
//        super(context);
//    }

    private void  initView() {
        m_sort_type = getIntent().getIntExtra("sort_type", 1);
        m_filter_type = getIntent().getStringExtra("filter_type");
        m_arrFilter_value = getIntent().getStringArrayExtra("filter_value");
        m_total_product_count = getIntent().getIntExtra("total_count", 0);
        findViewById(R.id.imv_deleteInput).setOnClickListener(this);
        m_tvBrandCnt = (TextView)findViewById(R.id.tv_brand_cnt);
        m_tvProductCnt = (TextView)findViewById(R.id.tv_product_cnt);
        m_lsvBrands = (ListView) findViewById(R.id.lsv_list);
        m_tvDescMid = (TextView) findViewById(R.id.tv_description_middle);

        m_tvTitle = (TextView)findViewById(R.id.tv_title);
        if(m_sort_type==1) {
            m_tvTitle.setText("브랜드 선택");
            m_tvDescMid.setText("브랜드, 총 ");
        }else {
            m_tvTitle.setText("제품군 선택");
            m_tvDescMid.setText("제품군, 총 ");
        }

        findViewById(R.id.imv_close).setOnClickListener(this);
        String SearchBrandDialog_title;
        if(m_filter_type.equals("productgroup")){
            SearchBrandDialog_title = "제품군";
            ((TextView)findViewById(R.id.tv_category)).setText(SearchBrandDialog_title + ": " + m_context.getResources().getStringArray(R.array.array_eat_healthfood)[Integer.valueOf(m_arrFilter_value[0])]);
        }else if(m_filter_type.equals("functionality")){
            SearchBrandDialog_title = "효능 및 기능 선택";
            ((TextView)findViewById(R.id.tv_category)).setText(SearchBrandDialog_title + ": " + m_context.getResources().getStringArray(R.array.array_interest_health)[Integer.valueOf(m_arrFilter_value[0])]);
        }else if(m_filter_type.equals("person")){
            SearchBrandDialog_title = "복용 대상";
            String[] arrPerson = {"청소년","수험생","갱년기여성","성인남성","성인여성","노인남성","노인여성","영유아","어린이","임산부","수유부"};
            ((TextView)findViewById(R.id.tv_category)).setText(SearchBrandDialog_title + ": " + arrPerson[Integer.valueOf(m_arrFilter_value[0])]);
        }else if(m_filter_type.equals("keyword")){
            SearchBrandDialog_title = "검색어";
            ((TextView)findViewById(R.id.tv_category)).setText(SearchBrandDialog_title + ": " + m_arrFilter_value[0]);
        }

        m_edtSearch = (EditText) findViewById(R.id.edt_search);
        m_edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Util.hideKeyPad(m_context);
                    //updateList();
                    m_strSearchKeyword = v.getText().toString();
//                    generateNewActivity(filterType_Keyword);
                    return true;
                }
                return false;
            }
        });

        m_edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                m_Adapter_filtered.clear();
                m_currentPage = 1;
                m_nMaxPage = 1;

                for (int i = 0; i < m_Adapter.getCount(); i++) {
                    if(m_Adapter.getItem(i)._business.contains(s.toString()))
                        m_Adapter_filtered.add(m_Adapter.getItem(i));
                }
                m_lsvBrands.setAdapter(m_Adapter_filtered);
                m_Adapter.notifyDataSetChanged();

                m_strSearchKeyword = s.toString();
            }
            @Override            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }
            @Override            public void afterTextChanged(Editable s) {            }
        });
        m_tvProductCnt.setText(String.valueOf(m_total_product_count));

        m_lsvBrands.setOnScrollListener(this);
        m_lsvBrands.setOnItemClickListener(this);
        m_Adapter = new SelectBrandAdapter(m_context, m_sort_type, new ArrayList<GoodInfo>());
        m_Adapter_filtered = new SelectBrandAdapter(m_context, m_sort_type, new ArrayList<GoodInfo>());

        m_lsvBrands.setAdapter(m_Adapter);
        connectServer(CONNECT_TYPE_BUSINESS_LIST);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imv_close:
                finish();
                break;
            case R.id.imv_deleteInput:
                m_edtSearch.setText("");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.imv_close));
    }

    JSONObject _jResult;

    private void connectServer(int strAct) {
        m_nConnectType = strAct;
        m_bLockListView = true;
        showProgress();

        String[] paramFields = new String[]{Net.NET_POST_FIELD_ACT,
                Net.NET_POST_FIELD_REQUEST};
        String[] paramValues = null;

        // 파라메터 입력
        JSONObject w_objJSonData = new JSONObject();
        try {
            w_objJSonData.put(Net.NET_VALUE_MEMBER_ID, UserManager.getInstance().member_id);
            if (m_nConnectType == CONNECT_TYPE_BUSINESS_LIST) {
                w_objJSonData.put(Net.NET_VALUE_PAGE, m_currentPage);

                w_objJSonData.put("sort_type", m_sort_type);
                if(m_filter_type.equals("productgroup")){
                    w_objJSonData.put("product", String.valueOf(Integer.valueOf(m_arrFilter_value[0])+1));
                }else if(m_filter_type.equals("functionality")){
                    w_objJSonData.put("feature", String.valueOf(Integer.valueOf(m_arrFilter_value[0])+1));
                }else if(m_filter_type.equals("person")){
                    w_objJSonData.put("taking", String.valueOf(Integer.valueOf(m_arrFilter_value[0])+1));
                }else if(m_filter_type.equals("keyword")){
                    w_objJSonData.put("keyword", m_arrFilter_value[0]);
                }
                if(m_strBusiness_ID_to_get != "" && m_strBusiness_ID_to_get != null)
                    w_objJSonData.put("business", m_strBusiness_ID_to_get);

                paramValues = new String[]{
                        Net.POST_FIELD_ACT_BUSINESSLIST,
                        w_objJSonData.toString()};
            }
        } catch (Exception e) {
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
                if (m_nConnectType == CONNECT_TYPE_BUSINESS_LIST) {
                    m_Adapter.notifyDataSetChanged();
                }
            }
        } else {
            if (!"".equals(strMsg)) {
                Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();
            }
        }
        m_bLockListView = false;
    }

    public void parseJSON() {
        try {
            m_nResultType = _jResult.getInt(Net.NET_VALUE_CODE); //
            JSONObject result = _jResult.getJSONObject(Net.NET_VALUE_RESULT);

            if(m_nConnectType == CONNECT_TYPE_BUSINESS_LIST){
                m_nMaxPage = result.getInt(Net.NET_VALUE_TOTAL_PAGE);
                m_tvBrandCnt.setText(String.valueOf(result.getInt(Net.NET_VALUE_TOTAL_COUNT)));
                JSONArray arr = result.getJSONArray("list");

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    GoodInfo info = new GoodInfo();
                    if(m_sort_type==1){
                        info._business_id = Integer.valueOf(obj.getString("business_id"));
                        info._business = obj.getString("business");
                        info._imagePath = obj.getString("filename");
                    }else{
                        info._pf_id = obj.getInt("pf_id");
                        info._business = obj.getString("f_name");
                    }
                    info.like_good = obj.getInt("COUNT(*)");

                    m_Adapter.add(info);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onParseJSon(JSONObject j_source) {
        _jResult = j_source;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
//        if(scrollState==SCROLL_STATE_IDLE){
//            findViewById(R.id.lly_three_buttons_area).animate().alpha(1f);
//        }else if(scrollState==SCROLL_STATE_TOUCH_SCROLL){
//            findViewById(R.id.lly_three_buttons_area).animate().alpha(0f);
//        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int count = totalItemCount - visibleItemCount;
        if (firstVisibleItem >= count && totalItemCount != 1
                && m_currentPage < m_nMaxPage && !m_bLockListView) {
            m_currentPage = m_currentPage + 1;
            connectServer(CONNECT_TYPE_BUSINESS_LIST);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        m_Adapter.setPos(m_nSelectedIndex);
        m_Adapter.notifyDataSetChanged();

        m_strBusiness_ID_to_get = String.valueOf(((GoodInfo)parent.getItemAtPosition(position))._business_id);
        Intent intent = new Intent(this, SearchInitActivity.class);
        intent.putExtra("filter", m_filter_type);
        intent.putExtra("value", m_arrFilter_value[0]);
        intent.putExtra("sort_type", m_sort_type);
        if(m_sort_type==1) {
            intent.putExtra("Business_ID_to_get", m_strBusiness_ID_to_get);
            intent.putExtra("Business_imagePath", String.valueOf(((GoodInfo)parent.getItemAtPosition(position))._imagePath));
            intent.putExtra("Business_business", String.valueOf(((GoodInfo)parent.getItemAtPosition(position))._business));
            intent.putExtra("Business_ProductCnt", ((GoodInfo)parent.getItemAtPosition(position)).like_good);
        }else{
            intent.putExtra("PF_ID_to_get", String.valueOf(((GoodInfo)parent.getItemAtPosition(position))._pf_id));
            intent.putExtra("PF_name", String.valueOf(((GoodInfo)parent.getItemAtPosition(position))._business));
            intent.putExtra("PF_ProductCnt", ((GoodInfo)parent.getItemAtPosition(position)).like_good);
        }

        //추천 화면에서 오는지의 분기 by 동현
        intent.putExtra("isRecommendList", getIntent().getBooleanExtra("isRecommendList", false));
        intent.putExtra("select_good", getIntent().getBooleanExtra("select_good", false));
        startActivityForResult(intent, getIntent().getBooleanExtra("select_good", false)?9:0);
//        ((SearchInitActivity) mContext).setBusinessID(m_nSelectedIndex);
//        finish();
    }

    @Override
    protected void onActivityResult(int p_requestCode,
                                    int p_resultCode,
                                    Intent p_intentActivity) {
        super.onActivityResult(p_requestCode, p_resultCode, p_intentActivity);
        if (p_resultCode == RESULT_OK) {
            switch (p_requestCode) {
                case 9:
                    setResult(RESULT_OK, p_intentActivity);     //리뷰, 경험공유 작성 중 선택하러 왔을 시. by 동현
                    finish();
                    break;
            }
        }
    }
}
