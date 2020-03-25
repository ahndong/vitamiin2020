package app.vitamiin.com.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.vitamiin.com.Adapter.HashListAdapter;
import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.Model.GoodInfo;
import app.vitamiin.com.NoticeDialog;
import app.vitamiin.com.R;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.common.Util;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;
import app.vitamiin.com.login.ListSelectDialog;

/**
 * Created by dong8 on 2016-12-22.
 */

public class SearchInitActivity extends BaseActivity implements View.OnClickListener, OnParseJSonListener, AbsListView.OnScrollListener, AdapterView.OnItemClickListener {
    Context mContext = this;
    NetUtil mNetUtil;
    NetUtil.connectAndgetServer mNetUtilConnetServer;
    int m_nResultType = 200;

    int m_nConnectType = NetUtil.GET_PRODUCT_LIST;

    int filterType_ProductFamily = 4;
    int filterType_Functionality = 5;
    int filterType_Target = 6;
    int filterType_Keyword = 7;

    int m_total_product_count;
    String m_strPerson, m_strProductGroup, m_strFunctionality, m_strBusiness_ID_to_get, m_strPF_ID_to_get;
    String m_strSearchKeyword = "";
    AppCompatActivity m_context;
    ViewGroup.LayoutParams m_llyParams, m_rcvParams;
    LinearLayoutManager mLayoutManager;
    LinearLayout m_uiLlyTopbar, m_uiLlySortSelectArea, m_uiLlyChosenBrand;
    LinearLayout m_llyStars;

    EditText m_edtSearch;
    TextView m_tvRecommendedWord, m_tvHistoryWord, m_tvProductType, m_tvFunction, m_tvPerson, m_tvOrderReview, m_tvOrderRate, m_tvOrderRegdate;
    ListView m_lsvRecommendList, m_lsvHistoryList;
    RecyclerView m_rcvProductList;
    private ArrayList<GoodInfo> arrGoodList = new ArrayList<>();
    ProductRecycleAdapter m_adapter;
    HashListAdapter m_adapterRecKeyword, m_adapterHistory, m_adapterShowKEYWORD;
    String m_strOrder = "b.business asc";

    String[] arrProductGroup, arrFunctionality, arrTarget;

    ImageView m_imvPhoto;
    TextView m_tvBusiness, m_tvProductCnt;

    String m_filter_type, m_filter_value;
    String m_strBusinessImgPath, m_strBusinessName, m_strBusinessCnt;

    int m_currentPage = 1;
    int m_nMaxPage = 1;
    boolean m_bLockListView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_init);
        mNetUtil = new NetUtil();
        mNetUtilConnetServer = mNetUtil.new connectAndgetServer(this);

        m_context = this;
        initView();
        //updateList();
    }

    private void initView() {
        findViewById(R.id.imv_back).setOnClickListener(this);
        findViewById(R.id.imv_deleteInput).setOnClickListener(this);

        m_uiLlyTopbar = (LinearLayout) findViewById(R.id.lly_topbar);
        m_uiLlySortSelectArea = (LinearLayout) findViewById(R.id.lly_sort_select_area);
        m_uiLlyChosenBrand = (LinearLayout) findViewById(R.id.lly_chosen_brand);
        mLayoutManager = new LinearLayoutManager(this);

        m_tvRecommendedWord = (TextView) findViewById(R.id.tv_recommended_word);
        m_tvHistoryWord = (TextView) findViewById(R.id.tv_history_word);

        m_tvProductType = (TextView) findViewById(R.id.tv_product_type);
        m_tvFunction = (TextView) findViewById(R.id.tv_function);
        m_tvPerson = (TextView) findViewById(R.id.tv_person);

        m_tvOrderReview = (TextView) findViewById(R.id.tv_order_review);
        m_tvOrderRate = (TextView) findViewById(R.id.tv_order_rate);
        m_tvOrderRegdate = (TextView) findViewById(R.id.tv_order_regdate);

        findViewById(R.id.lly_select_brand_sort).setOnClickListener(this);
        findViewById(R.id.lly_select_pf_sort).setOnClickListener(this);
        m_tvRecommendedWord.setOnClickListener(this);
        m_tvHistoryWord.setOnClickListener(this);
        m_tvProductType.setOnClickListener(this);
        m_tvFunction.setOnClickListener(this);
        m_tvPerson.setOnClickListener(this);
        m_tvOrderReview.setOnClickListener(this);
        m_tvOrderRate.setOnClickListener(this);
        m_tvOrderRegdate.setOnClickListener(this);

        m_rcvProductList = (RecyclerView) findViewById(R.id.lsv_product_list);
        m_lsvRecommendList = (ListView) findViewById(R.id.lsv_recommend_list);
        m_lsvHistoryList = (ListView) findViewById(R.id.lsv_history_list);

        m_edtSearch = (EditText) findViewById(R.id.edt_search);
        m_edtSearch.setImeActionLabel("검색", KeyEvent.KEYCODE_ENTER);
        m_edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Util.hideKeyPad(m_context);
                    //updateList();
                    m_strSearchKeyword = v.getText().toString();
                    generateNewActivity(filterType_Keyword);
                    return true;
                }
                return false;
            }
        });
        m_edtSearch.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    findViewById(R.id.tv_search_btn).performClick();
                    return true;
                }
                return false;
            }
        });

        m_edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to Filter
//                m_adapter.getFilter().filter(s.toString());
                if(m_tvRecommendedWord.isSelected() || m_tvHistoryWord.isSelected()){
                    m_adapterShowKEYWORD.clear();
                    m_currentPage = 1;
                    m_nMaxPage = 1;
                    if(m_tvRecommendedWord.isSelected()){
                        for (int i = 0; i < m_adapterRecKeyword.getCount(); i++) {
                            if(m_adapterRecKeyword.getItem(i).contains(s.toString()))
                                m_adapterShowKEYWORD.add(m_adapterRecKeyword.getItem(i));
                        }
                        m_lsvRecommendList.setAdapter(m_adapterShowKEYWORD);
                        m_adapterRecKeyword.notifyDataSetChanged();
                    } else if(m_tvHistoryWord.isSelected()){
                        for (int i = 0; i < m_adapterHistory.getCount(); i++) {
                            if(m_adapterHistory.getItem(i).contains(s.toString()))
                                m_adapterShowKEYWORD.add(m_adapterHistory.getItem(i));
                        }
                        m_lsvHistoryList.setAdapter(m_adapterShowKEYWORD);
                        m_adapterHistory.notifyDataSetChanged();
                    }
                    m_strSearchKeyword = s.toString();
                }
            }
            @Override            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }
            @Override            public void afterTextChanged(Editable s) {            }
        });

        arrTarget = m_context.getResources().getStringArray(R.array.taking_target);
        arrProductGroup = m_context.getResources().getStringArray(R.array.array_eat_healthfood);
        arrFunctionality = m_context.getResources().getStringArray(R.array.array_interest_health);

        //리뷰작성 또는 노하우 공유작성 중에 제품 선택하러 온 경우 by 동현
        if (getIntent().getBooleanExtra("select_good", false)) {
            //마지막 파라메터는 canSelect임 by 동현
            m_adapter = new ProductRecycleAdapter(this, arrGoodList, false);
            findViewById(R.id.imv_chart).setVisibility(View.GONE);
        } else {
            m_adapter = new ProductRecycleAdapter(this, arrGoodList, true);
        }
        //추천 화면에서 오는지의 분기 by 동현
        if (getIntent().getBooleanExtra("isRecommendList", false)) {
            ((TextView)findViewById(R.id.tv_title)).setText("제품 추천 리스트");
        }

        (findViewById(R.id.tv_search_btn)).setOnClickListener(this);        //언제든 이버튼을 누르면 결과내 재검색이 아니라 키워드 검색 초기 실행으로 by 동현

        m_llyParams = (findViewById(R.id.rly_title_area)).getLayoutParams();
        if (getIntent().getBooleanExtra("isInitSearch", false)) {
            m_adapterRecKeyword = new HashListAdapter(this, new ArrayList<String>(), 2);
            m_lsvRecommendList.setOnScrollListener(this);
            m_lsvRecommendList.setOnItemClickListener(this);

            m_adapterHistory = new HashListAdapter(this, new ArrayList<String>(), 2);
            m_lsvHistoryList.setOnScrollListener(this);
            m_lsvHistoryList.setOnItemClickListener(this);

            m_adapterShowKEYWORD = new HashListAdapter(this, new ArrayList<String>(), 2);

            //검색 기능에 초기 진입 시에는
            findViewById(R.id.tv_title_sub).setVisibility(View.GONE);
            m_uiLlyTopbar.setVisibility(View.GONE);
            m_uiLlySortSelectArea.setVisibility(View.GONE);
            findViewById(R.id.lly_keyword_area).setVisibility(View.VISIBLE);
            findViewById(R.id.lly_three_buttons_area).setVisibility(View.VISIBLE);
            findViewById(R.id.lly_order_select).setVisibility(View.GONE);
            m_uiLlyChosenBrand.setVisibility(View.GONE);

            m_llyParams.height = this.getResources().getDimensionPixelSize(R.dimen.search_title_hight_big);
            (findViewById(R.id.rly_title_area)).setLayoutParams(m_llyParams);
            m_tvRecommendedWord.setSelected(true);

            m_nConnectType = NetUtil.GET_SEARCH_KEYWORD;
            mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, "");

            switch (getIntent().getIntExtra("quicksearch", 0)) {
                case 1:
                    onClick(findViewById(R.id.tv_product_type));
                    break;
                case 2:
                    onClick(findViewById(R.id.tv_function));
                    break;
                case 3:
                    onClick(findViewById(R.id.tv_person));
                    break;
            }
        }else{
            mLayoutManager = new LinearLayoutManager(this);
            m_rcvProductList.setLayoutManager(mLayoutManager);

            m_rcvProductList.setOnScrollListener(m_hidingScrollListener);
            m_rcvProductList.setAdapter(m_adapter);

            findViewById(R.id.imv_chart).setOnClickListener(this);
            findViewById(R.id.tv_detail).setOnClickListener(this);

            m_filter_type = getIntent().getStringExtra("filter");
            m_filter_value = getIntent().getStringExtra("value");
            if(m_filter_type.equals("productgroup")){
                ((TextView)findViewById(R.id.tv_title_sub)).setText("제품군 : " + arrProductGroup[Integer.parseInt(m_filter_value)]);
                findViewById(R.id.lly_select_pf_sort).setVisibility(View.GONE);
            }else if(m_filter_type.equals("functionality")){
                ((TextView)findViewById(R.id.tv_title_sub)).setText("효능 및 기능 선택 : " + arrFunctionality[Integer.parseInt(m_filter_value)]);
            }else if(m_filter_type.equals("person")){
                ((TextView)findViewById(R.id.tv_title_sub)).setText("복용 대상 : " + arrTarget[Integer.parseInt(m_filter_value)]);
            }else if(m_filter_type.equals("keyword")){
                if(m_filter_value.length()==0)
                    ((TextView)findViewById(R.id.tv_title_sub)).setText("검색어 : 없음");
                else
                    ((TextView)findViewById(R.id.tv_title_sub)).setText("검색어 : " + m_filter_value);
            }

            m_strBusiness_ID_to_get = "";
            m_strPF_ID_to_get = "";
            if(getIntent().getIntExtra("sort_type", 1) == 1 && getIntent().getStringExtra("Business_ID_to_get") != null){
                m_strBusiness_ID_to_get = getIntent().getStringExtra("Business_ID_to_get");
                m_uiLlyChosenBrand.setVisibility(View.VISIBLE);
                m_uiLlySortSelectArea.setVisibility(View.GONE);

                m_imvPhoto = (ImageView) findViewById(R.id.imv_good);
                m_tvBusiness = (TextView) findViewById(R.id.tv_business);
                m_tvProductCnt = (TextView) findViewById(R.id.tv_product_cnt);

                if (getIntent().getStringExtra("Business_imagePath").equals("")){
                    Glide.with(m_context)
                            .load(R.drawable.default_review)
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.default_review)
                                    .centerCrop()
                                    .dontAnimate()
                                    .dontTransform())
                            .into(m_imvPhoto);

//                    Glide.with(m_context).load(R.drawable.default_review).centerCrop().into(m_imvPhoto);
                } else {
                    Glide.with(m_context)
                            .load(Net.URL_SERVER1 + getIntent().getStringExtra("Business_imagePath"))
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.default_review)
                                    .centerCrop()
                                    .dontAnimate()
                                    .dontTransform())
                            .into(m_imvPhoto);

//                    Glide.with(m_context)
//                            .load(Net.URL_SERVER1 + getIntent().getStringExtra("Business_imagePath"))
//                            .centerCrop().into(m_imvPhoto);
                }

                m_tvBusiness.setText(getIntent().getStringExtra("Business_business"));
                m_tvProductCnt.setText(String.valueOf(getIntent().getIntExtra("Business_ProductCnt", 0)) + "개 제품");
            } else if(getIntent().getIntExtra("sort_type", 1) == 2 && getIntent().getStringExtra("PF_ID_to_get") != null){
                m_strPF_ID_to_get = getIntent().getStringExtra("PF_ID_to_get");
                m_uiLlyChosenBrand.setVisibility(View.VISIBLE);
                m_uiLlySortSelectArea.setVisibility(View.GONE);

                m_imvPhoto = (ImageView) findViewById(R.id.imv_good);
                m_tvBusiness = (TextView) findViewById(R.id.tv_business);
                m_tvProductCnt = (TextView) findViewById(R.id.tv_product_cnt);
                m_llyStars = (LinearLayout) findViewById(R.id.lly_stars);

                m_llyStars.setVisibility(View.GONE);
                m_imvPhoto.setVisibility(View.GONE);

                m_tvBusiness.setText("제품군: " + getIntent().getStringExtra("PF_name"));
                m_tvBusiness.setTextSize(18f);
                m_tvBusiness.setGravity(Gravity.CENTER_HORIZONTAL);
                m_tvProductCnt.setText(String.valueOf(getIntent().getIntExtra("PF_ProductCnt", 0)) + "개 제품");
                m_tvProductCnt.setTextSize(16f);
                m_tvProductCnt.setGravity(Gravity.CENTER_HORIZONTAL);
            } else{
                m_uiLlyChosenBrand.setVisibility(View.GONE);
                m_uiLlySortSelectArea.setVisibility(View.VISIBLE);
                m_uiLlySortSelectArea.setOnClickListener(this);
            }

            m_tvOrderReview.setSelected(true);

            m_lsvRecommendList.setVisibility(View.GONE);
            m_lsvHistoryList.setVisibility(View.GONE);
            m_rcvProductList.setVisibility(View.VISIBLE);

            m_uiLlyTopbar.setVisibility(View.VISIBLE);
            findViewById(R.id.lly_keyword_area).setVisibility(View.GONE);
            findViewById(R.id.lly_three_buttons_area).setVisibility(View.GONE);
            findViewById(R.id.lly_order_select).setVisibility(View.VISIBLE);

            if(m_filter_type.equals("keyword")){
                m_edtSearch.setText(m_filter_value);
            }else{
                findViewById(R.id.lly_searchbar).setVisibility(View.GONE);
                findViewById(R.id.tv_title_sub).setVisibility(View.VISIBLE);

                m_llyParams.height = this.getResources().getDimensionPixelSize(R.dimen.search_title_hight_small);
                (findViewById(R.id.rly_title_area)).setLayoutParams(m_llyParams);
            }

            m_nConnectType = NetUtil.GET_PRODUCT_LIST;
            mNetUtilConnetServer.connectServer(mContext, handler, m_nConnectType, new String[] {
                                                                        m_filter_type,
                                                                        m_filter_value,
                                                                        m_strBusiness_ID_to_get,
                                                                        m_strPF_ID_to_get,
                                                                        String.valueOf(m_currentPage),
                                                                        m_strOrder});
        }
    }
    private void updateList() {
        arrGoodList.clear();
        m_adapter.notifyDataSetChanged();
        m_currentPage = 1;
        m_nMaxPage = 1;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.imv_back:
                finish();
                break;
            case R.id.imv_chart:
                int cnt = 0;
                GoodInfo[] m_selectedItem = new GoodInfo[2];
                for (int i = 0; i < arrGoodList.size(); i++) {
                    if (arrGoodList.get(i).isCheck) {
                        m_selectedItem[cnt] = arrGoodList.get(i);
                        cnt++;
                    }
                }
                if (cnt != 2) {
                    Toast.makeText(this, "2개의 제품을 선택하셔야 합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent = new Intent(this, ChartActivity.class);
                intent.putExtra("good1", m_selectedItem[0]);
                intent.putExtra("good2", m_selectedItem[1]);
                startActivity(intent);
                break;
            case R.id.tv_recommended_word:
                m_tvRecommendedWord.setSelected(true);
                m_tvHistoryWord.setSelected(false);
                m_lsvRecommendList.setVisibility(View.VISIBLE);
                m_lsvHistoryList.setVisibility(View.GONE);
                break;
            case R.id.tv_history_word:
                m_tvRecommendedWord.setSelected(false);
                m_tvHistoryWord.setSelected(true);
                m_lsvRecommendList.setVisibility(View.GONE);
                m_lsvHistoryList.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_product_type:
                new SearchSelectConditionDialog(this, "제품군 선택하기", 0, arrProductGroup, m_strProductGroup).show();
                break;
            case R.id.tv_function:
                new SearchSelectConditionDialog(this, "효능 및 기능 선택하기", 1, arrFunctionality, m_strFunctionality).show();
                break;
            case R.id.tv_person:
                new ListSelectDialog(this, "복용 대상 선택", arrTarget, 28, -1).show();
                break;
            case R.id.lly_select_brand_sort:
                intent = new Intent(this, SearchBrandDialog.class);

                m_filter_type = getIntent().getStringExtra("filter");
                intent.putExtra("sort_type", 1);
                intent.putExtra("filter_type", m_filter_type);
                intent.putExtra("filter_value", new String[] {m_filter_value});
                intent.putExtra("total_count", m_total_product_count);
                //추천 화면에서 오는지의 분기 by 동현
                intent.putExtra("isRecommendList", getIntent().getBooleanExtra("isRecommendList", false));
                intent.putExtra("select_good", getIntent().getBooleanExtra("select_good", false));

                startActivityForResult(intent, getIntent().getBooleanExtra("select_good", false)?9:0);
                //connectServer(CONNECT_TYPE_BUSINESS_LIST);
//                    new SearchBrandDialog(this, SearchBrandDialog_title, new String[] {m_filter_value},
//                            m_Business_names,
//                            m_Business_images,
//                            m_Prod_cnt_per_Business).show();
                break;
            case R.id.lly_select_pf_sort:
                intent = new Intent(this, SearchBrandDialog.class);

                m_filter_type = getIntent().getStringExtra("filter");
                intent.putExtra("sort_type", 2);
                intent.putExtra("filter_type", m_filter_type);
                intent.putExtra("filter_value", new String[] {m_filter_value});
                intent.putExtra("total_count", m_total_product_count);
                //추천 화면에서 오는지의 분기 by 동현
                intent.putExtra("isRecommendList", getIntent().getBooleanExtra("isRecommendList", false));
                intent.putExtra("select_good", getIntent().getBooleanExtra("select_good", false));

                startActivityForResult(intent, getIntent().getBooleanExtra("select_good", false)?9:0);
                break;
            case R.id.tv_order_review:
                m_tvOrderReview.setSelected(true);
                m_tvOrderRate.setSelected(false);
                m_tvOrderRegdate.setSelected(false);
                m_strOrder = "g.review_cnt desc";
                updateList();

                m_nConnectType = NetUtil.GET_PRODUCT_LIST;
                mNetUtilConnetServer.connectServer(mContext, handler, m_nConnectType, new String[] {
                        m_filter_type,
                        m_filter_value,
                        m_strBusiness_ID_to_get,
                        m_strPF_ID_to_get,
                        String.valueOf(m_currentPage),
                        m_strOrder});
                break;
            case R.id.tv_order_rate:
                m_tvOrderReview.setSelected(false);
                m_tvOrderRate.setSelected(true);
                m_tvOrderRegdate.setSelected(false);
                m_strOrder = "g.rate desc";
                updateList();

                m_nConnectType = NetUtil.GET_PRODUCT_LIST;
                mNetUtilConnetServer.connectServer(mContext, handler, m_nConnectType, new String[] {
                        m_filter_type,
                        m_filter_value,
                        m_strBusiness_ID_to_get,
                        m_strPF_ID_to_get,
                        String.valueOf(m_currentPage),
                        m_strOrder});
                break;
            case R.id.tv_order_regdate:
                m_tvOrderReview.setSelected(false);
                m_tvOrderRate.setSelected(false);
                m_tvOrderRegdate.setSelected(true);
                m_strOrder = "g.regdate desc";
                updateList();

                m_nConnectType = NetUtil.GET_PRODUCT_LIST;
                mNetUtilConnetServer.connectServer(mContext, handler, m_nConnectType, new String[] {
                        m_filter_type,
                        m_filter_value,
                        m_strBusiness_ID_to_get,
                        m_strPF_ID_to_get,
                        String.valueOf(m_currentPage),
                        m_strOrder});
                break;
            case R.id.tv_search_btn:
                if(m_edtSearch.getText().toString().length()<2)
                    new NoticeDialog(this, "키워드를 입력해주세요.", "검색어를 2자 이상 입력하셔야 검색이 가능합니다. ^^", "잠시 후에 자동으로 닫힙니다.", true).show();
                else {
                    Util.hideKeyPad(m_context);
                    generateNewActivity(filterType_Keyword);
                }
                break;
            case R.id.lly_chosen_brand:
                intent = new Intent(this, SearchInitActivity.class);
                intent.putExtra("filter", m_filter_type);
                intent.putExtra("value", m_strSearchKeyword);
                intent.putExtra("Business_ID_to_get", m_strBusiness_ID_to_get);
                intent.putExtra("Business_imagePath", m_strBusinessImgPath);
                intent.putExtra("Business_business", m_strBusinessName);
                intent.putExtra("Business_ProductCnt", Integer.valueOf(m_strBusinessCnt));

                intent.putExtra("isRecommendList", getIntent().getBooleanExtra("isRecommendList", false));
                intent.putExtra("select_good", getIntent().getBooleanExtra("select_good", false));
                startActivity(intent);
                break;
            case R.id.imv_deleteInput:
                m_edtSearch.setText("");
                break;
            case R.id.tv_detail:
                new NoticeDialog(this, "제품 성분표 비교 기능", "저희 비타미인에서만 제공하는 기능을 통해 두가지 제품의 성분 함량표를 한눈에 비교하며 볼 수 있습니다.","닫기", false).show();
                break;
        }
    }
    public void setProductGroup(String strIndex){
        m_strProductGroup = strIndex;
        generateNewActivity(filterType_ProductFamily);
    }
    public void setFunctionality(String strIndex){
        m_strFunctionality = strIndex;
        generateNewActivity(filterType_Functionality);
    }
    public void setPerson(String strIndex){
        m_strPerson = strIndex;
        generateNewActivity(filterType_Target);
    }

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.imv_back));
    }

    JSONObject _jResult;

    private void generateNewActivity(int strAct){
        m_nConnectType = strAct;

        Intent intent = new Intent(this, SearchInitActivity.class);
        if(getIntent().getBooleanExtra("select_good", false))
            intent.putExtra("select_good", true);

        if (m_nConnectType == filterType_ProductFamily) {
            intent.putExtra("filter", "productgroup");
            intent.putExtra("value", m_strProductGroup);
        }
        else if (m_nConnectType == filterType_Functionality) {
            intent.putExtra("filter", "functionality");
            intent.putExtra("value", m_strFunctionality);
        }
        else if(m_nConnectType == filterType_Target){
            intent.putExtra("filter", "person");
            intent.putExtra("value", m_strPerson);
        }
        else if(m_nConnectType == filterType_Keyword){
            intent.putExtra("filter", "keyword");
            intent.putExtra("value", m_edtSearch.getText().toString());
        }
        startActivityForResult(intent, 200);
    }

    public void gotoProductDetail(int pos) {
        GoodInfo info = arrGoodList.get(pos);
        if (info == null)
            return;
        if (getIntent().getBooleanExtra("select_good", false)) {
            //리뷰작성 또는 노하우 공유작성 중에 제품 선택하러 온 경우 by 동현
            Intent intent = new Intent();
            intent.putExtra("select_good", getIntent().getBooleanExtra("select_good", false));
            intent.putExtra("info", info);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            //제품 검색 또는 추천화면에서 온 경우, 즉 상세 제품 화면으로 넘어가는 경우 by 동현
            Intent intent = new Intent(this, DetailGoodActivity.class);
            intent.putExtra("isRecommendList", getIntent().getBooleanExtra("isRecommendList", false));
            intent.putExtra("info", info);
            startActivity(intent);
        }
    }

    private void processForNetEnd() {
        parseJSON();
        closeProgress();
        HttpRequester.getInstance().stopNetThread();

        int resultCode = HttpRequester.getInstance().getResultCode();
        String strMsg = HttpRequester.getInstance().getResultMsg();

        if (resultCode == Net.CONNECTION_SUCCSES) {
            if (m_nResultType == 200) {
                if (m_nConnectType == NetUtil.GET_PRODUCT_LIST)
                    m_adapter.notifyDataSetChanged();
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

            if (m_nConnectType == NetUtil.GET_SEARCH_KEYWORD) {
                JSONArray arr1 = result.getJSONArray("list_recom");
                for (int i = 0; i < arr1.length(); i++) {
                    m_adapterRecKeyword.add(arr1.getString(i));
                }
                JSONArray arr2 = result.getJSONArray("list_history");
                for (int i = 0; i < arr2.length(); i++) {
                    m_adapterHistory.add(arr2.getString(i));
                }
                m_lsvRecommendList.setAdapter(m_adapterRecKeyword);
                m_lsvHistoryList.setAdapter(m_adapterHistory);
            }
            else if (m_nConnectType == NetUtil.GET_PRODUCT_LIST) {
                m_total_product_count = result.getInt("total_count");
                ((TextView)findViewById(R.id.tv_product_totalcnt)).setText("총 " + m_total_product_count + "개 제품");
                m_nMaxPage = result.getInt(Net.NET_VALUE_TOTAL_PAGE);
                JSONArray arr = result.getJSONArray("list");

                for (int i = 0; i < arr.length(); i++) {
                    arrGoodList.add(mNetUtil.parseGood(arr.getJSONObject(i)));
                }

                if(result.getInt("B_bool")==1){
                    JSONArray arrB_info = result.getJSONArray("business");
                    JSONObject objB = arrB_info.getJSONObject(0);

                    m_uiLlyChosenBrand.setVisibility(View.VISIBLE);
                    m_uiLlyChosenBrand.setOnClickListener(this);
                    m_uiLlySortSelectArea.setVisibility(View.GONE);

                    m_imvPhoto = (ImageView) findViewById(R.id.imv_good);
                    m_tvBusiness = (TextView) findViewById(R.id.tv_business);
                    m_tvProductCnt = (TextView) findViewById(R.id.tv_product_cnt);

                    m_strBusiness_ID_to_get = String.valueOf(objB.getInt("business_id"));
                    m_strBusinessImgPath = objB.getString("filename");
                    m_strBusinessName = objB.getString("business");
                    m_strBusinessCnt = String.valueOf(objB.getInt("COUNT(*)"));

                    if (m_strBusinessImgPath.equals("")) {
//                        Glide.with(m_context).load(R.drawable.default_review).centerCrop().into(m_imvPhoto);

                        Glide.with(m_context)
                                .load(R.drawable.default_review)
                                .apply(new RequestOptions()
                                        .placeholder(R.drawable.default_review)
                                        .centerCrop()
                                        .dontAnimate()
                                        .dontTransform())
                                .into(m_imvPhoto);
                    }else {
                        Glide.with(m_context)
                                .load(Net.URL_SERVER1 + getIntent().getStringExtra("Business_imagePath"))
                                .apply(new RequestOptions()
                                        .placeholder(R.drawable.default_review)
                                        .centerCrop()
                                        .dontAnimate()
                                        .dontTransform())
                                .into(m_imvPhoto);

//                        Glide.with(m_context)
//                                .load(Net.URL_SERVER1 + getIntent().getStringExtra("Business_imagePath"))
//                                .centerCrop()
//                                .into(m_imvPhoto);
                    }

                    (m_tvBusiness).setText(m_strBusinessName);
                    (m_tvProductCnt).setText(m_strBusinessCnt + "개 제품");
                }
            }
        } catch (JSONException e) {e.printStackTrace();}
    }


    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Net.THREAD_REQUEST_END) {
                processForNetEnd();}
        }
    };

    @Override
    public void onParseJSon(JSONObject j_source) {
        _jResult = j_source;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(view == m_lsvHistoryList || view == m_lsvRecommendList){
            if(scrollState==SCROLL_STATE_IDLE){
                ((LinearLayout)findViewById(R.id.lly_three_buttons_area)).animate().alpha(1f);
            }else if(scrollState==SCROLL_STATE_TOUCH_SCROLL){
                ((LinearLayout)findViewById(R.id.lly_three_buttons_area)).animate().alpha(0f);
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int count = totalItemCount - visibleItemCount;
        if (firstVisibleItem >= count && totalItemCount != 1
                && m_currentPage < m_nMaxPage && !m_bLockListView) {

            m_bLockListView = true;
            m_currentPage++;
            m_nConnectType = NetUtil.GET_PRODUCT_LIST;
            mNetUtilConnetServer.connectServer(mContext, handler, m_nConnectType, new String[] {
                    m_filter_type,
                    m_filter_value,
                    m_strBusiness_ID_to_get,
                    m_strPF_ID_to_get,
                    String.valueOf(m_currentPage),
                    m_strOrder});
        }
    }

    HidingScrollListener m_hidingScrollListener = new HidingScrollListener() {
        @Override
        public void onHide() {
            hideViews();
        }

        @Override
        public void onShow() {
            showViews();
        }
    };

    private void hideViews() {
        if(m_total_product_count>4){
            m_hidingScrollListener.controlsVisible = false;
            m_hidingScrollListener.scrolledDistance = 0;

            //m_rcvParams = m_rcvProductList.getLayoutParams();
            if(m_uiLlySortSelectArea.getVisibility()==View.VISIBLE)
                m_rcvProductList.setPadding(0,0,0,0);
            else
                m_rcvProductList.setPadding(0,m_uiLlyChosenBrand.getHeight(),0,0);
            m_uiLlyTopbar.animate().translationY(-m_uiLlyTopbar.getHeight()- m_uiLlySortSelectArea.getHeight()+9).setInterpolator(new AccelerateInterpolator(1));
            m_uiLlySortSelectArea.animate().translationY(-m_uiLlySortSelectArea.getHeight()+7).setInterpolator(new AccelerateInterpolator(3));
//        m_imvWrite.animate().translationY(m_imvWrite.getHeight() * 2).setInterpolator(new AccelerateInterpolator(2));
        }
    }

    private void showViews() {
        m_hidingScrollListener.controlsVisible = true;
        m_hidingScrollListener.scrolledDistance = 0;

        //m_rcvProductList.setPadding(0,(int)(getResources().getDimension(R.dimen.search_num_of_product_area)+getResources().getDimension(R.dimen.search_num_of_product_area)),0,0);
        if(m_uiLlySortSelectArea.getVisibility()==View.VISIBLE)
            m_rcvProductList.setPadding(0, m_uiLlyTopbar.getHeight()+ m_uiLlySortSelectArea.getHeight(),0,0);
        else
            m_rcvProductList.setPadding(0, m_uiLlyTopbar.getHeight()+m_uiLlyChosenBrand.getHeight(),0,0);
        m_uiLlyTopbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(1));
        m_uiLlySortSelectArea.animate().translationY(0).setInterpolator(new DecelerateInterpolator(3));
//        m_imvWrite.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    public abstract class HidingScrollListener extends RecyclerView.OnScrollListener {
        private static final int HIDE_THRESHOLD = 30;
        private int scrolledDistance = 0;
        private boolean controlsVisible = true;

        int pastVisiblesItems, visibleItemCount, totalItemCount;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                onHide();
            } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                onShow();
            }

            if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
                scrolledDistance += dy;
            }

            visibleItemCount = mLayoutManager.getChildCount();
            totalItemCount = mLayoutManager.getItemCount();
            pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

            int count = totalItemCount - visibleItemCount;
            if (pastVisiblesItems >= count && totalItemCount != 1
                    && m_currentPage < m_nMaxPage && !m_bLockListView) {

                m_bLockListView = true;
                m_currentPage++;
                m_nConnectType = NetUtil.GET_PRODUCT_LIST;
                mNetUtilConnetServer.connectServer(mContext, handler, m_nConnectType, new String[] {
                        m_filter_type,
                        m_filter_value,
                        m_strBusiness_ID_to_get,
                        m_strPF_ID_to_get,
                        String.valueOf(m_currentPage),
                        m_strOrder});
            }
        }
        public abstract void onHide();

        public abstract void onShow();
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(parent == m_lsvHistoryList || parent == m_lsvRecommendList){
            m_strSearchKeyword = parent.getItemAtPosition(position).toString();
            m_edtSearch.setText(m_strSearchKeyword);
            generateNewActivity(filterType_Keyword);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 200:
                    setResult(RESULT_OK, data);
                    finish();
                    break;
                case 9:
                    setResult(RESULT_OK, data);     //리뷰, 경험공유 작성 중 선택하러 왔을 시. by 동현
                    finish();
                    break;
            }
        }
    }
}

