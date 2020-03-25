package app.vitamiin.com.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.vitamiin.com.Adapter.FoodListAdapter;
import app.vitamiin.com.Adapter.GoodListAdapter;
import app.vitamiin.com.Adapter.ReviewExpListAdapter;
import app.vitamiin.com.Adapter.ReviewListAdapter;
import app.vitamiin.com.Adapter.ExpertColumnListAdapter;
import app.vitamiin.com.Adapter.PowerReviewRecycleAdapter;
import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.Model.FoodInfo;
import app.vitamiin.com.Model.FoodInfoDeux;
import app.vitamiin.com.Model.GoodInfo;
import app.vitamiin.com.Model.ReviewInfo;
import app.vitamiin.com.Model.WikiInfo;
import app.vitamiin.com.NoticeDialog;
import app.vitamiin.com.R;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.home.DetailExpActivity;
import app.vitamiin.com.home.DetailFoodActivity;
import app.vitamiin.com.home.DetailGoodActivity;
import app.vitamiin.com.home.DetailReviewActivity;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;
import app.vitamiin.com.login.ListSelectDialog;

public class LikeActivity extends BaseActivity implements View.OnClickListener, OnParseJSonListener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener {
    Context mContext = this;
    NetUtil mNetUtil;
    NetUtil.connectAndgetServer mNetUtilConnetServer;

    String mb_id = "";
    int m_nSort = 0; //0.제품, 1.리뷰, 2.노하우 공유, 3.파워리뷰, 4.전문가칼럼, 5.성분분석
//    String[] arrFilterValue = new String[6];
    String[] arrFilterValue = new String[4];
    int[] arrNum = new int[6];
    int LeftOrRight;

    int m_nResultType = 200;
    int CONNECT_TYPE_LIKE = 1;
    int m_nConnectType = CONNECT_TYPE_LIKE;

//    ListView m_lsvExpertColumn;
    ListView m_lsvGood, m_lsvReview, m_lsvReviewExp, m_lsvFood;
    TextView m_tvTitle;
//    LinearLayoutManager mLayoutManager;
//    RecyclerView m_rcvPowerReview;

    private ArrayList<WikiInfo> arrPowerReviewList = new ArrayList<>();
    private ArrayList<FoodInfo> arrFoodInfoList = new ArrayList<>();
    private ArrayList<FoodInfoDeux> arrFoodInfoDoubleList = new ArrayList<>();
    GoodListAdapter m_adapter_good;
    ReviewListAdapter m_adapter_review;
    ReviewExpListAdapter m_adapter_review_exper;
    PowerReviewRecycleAdapter m_adapter_power_review;
    ExpertColumnListAdapter m_adapter_expert_column;
    FoodListAdapter m_adapter_food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);
        mNetUtil = new NetUtil();
        mNetUtilConnetServer = mNetUtil.new connectAndgetServer(this);

        mb_id = getIntent().getStringExtra("f_id");
        initView();

        connectServer(CONNECT_TYPE_LIKE);
    }

    private void initView() {
        m_tvTitle = (TextView) findViewById(R.id.tv_title);
        findViewById(R.id.imv_back).setOnClickListener(this);
        findViewById(R.id.imv_filter).setOnClickListener(this);

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        m_lsvGood = (ListView) findViewById(R.id.lsv_good);
        m_lsvReview = (ListView) findViewById(R.id.lsv_review);
        m_lsvReviewExp = (ListView) findViewById(R.id.lsv_review_exper);
//        m_rcvPowerReview = (RecyclerView) findViewById(R.id.rcv_power_review);
//        mLayoutManager = new LinearLayoutManager(this);
//        m_rcvPowerReview.setLayoutManager(mLayoutManager);
//        m_rcvPowerReview.setOnScrollListener(m_hidingScrollListener);
//        m_lsvExpertColumn = (ListView) findViewById(R.id.lsv_expert_column);
        m_lsvFood = (ListView) findViewById(R.id.lsv_food);

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        m_adapter_good = new GoodListAdapter(this, new ArrayList<GoodInfo>(), false);
        m_lsvGood.setAdapter(m_adapter_good);

        m_adapter_review = new ReviewListAdapter(this, new ArrayList<ReviewInfo>(), true);
        m_lsvReview.setAdapter(m_adapter_review);

//        m_adapter_power_review = new PowerReviewRecycleAdapter(this, arrPowerReviewList);
//        arrPowerReviewList.clear();
//        m_rcvPowerReview.setAdapter(m_adapter_power_review);

        m_adapter_review_exper = new ReviewExpListAdapter(this, new ArrayList<ReviewInfo>());
        m_lsvReviewExp.setAdapter(m_adapter_review_exper);

//        m_adapter_expert_column = new ExpertColumnListAdapter(this, new ArrayList<WikiInfo>());
//        m_lsvExpertColumn.setAdapter(m_adapter_expert_column);

        m_adapter_food = new FoodListAdapter(this, arrFoodInfoDoubleList);
        arrFoodInfoDoubleList.clear();
        m_lsvFood.setAdapter(m_adapter_food);
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        m_lsvGood.setOnItemClickListener(this);
        m_lsvReview.setOnItemClickListener(this);
        m_lsvReviewExp.setOnItemClickListener(this);
        /////////////////////////////////////////////
//        m_lsvExpertColumn.setOnItemClickListener(this);
        m_lsvFood.setOnItemClickListener(this);
        m_lsvFood.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                LeftOrRight = (int)event.getX() < (view.getMeasuredWidth()/2) ? 0:1;
                return false;
            }
        });
        m_lsvFood.setOnScrollListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imv_back:
                Intent intent = new Intent();
                intent.putExtra("like_cnt", arrNum[0]+ arrNum[1]+ arrNum[2]+ arrNum[3]+ arrNum[4]+ arrNum[5]);
                intent.putExtra("like_cnt", arrNum[0]+ arrNum[1]+ arrNum[2]+ arrNum[3]);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.imv_filter:
                new ListSelectDialog(this, "필터", arrFilterValue, 15, m_nSort).show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.imv_back));
    }

    public void setSort(int index) {
        m_nSort = index;
        m_tvTitle.setText("좋아요 - " + arrFilterValue[m_nSort]);
        m_lsvGood.setVisibility(View.GONE);
        m_lsvReview.setVisibility(View.GONE);
        m_lsvReviewExp.setVisibility(View.GONE);
//        m_rcvPowerReview.setVisibility(View.GONE);
//        m_lsvExpertColumn.setVisibility(View.GONE);
        m_lsvFood.setVisibility(View.GONE);

        if (m_nSort == 0) {
            m_lsvGood.setVisibility(View.VISIBLE);
            m_lsvGood.setAdapter(m_adapter_good);
//            setListViewHeightBasedOnChildren(m_lsvGood);
        }
        else if (m_nSort == 1) {
            m_lsvReview.setVisibility(View.VISIBLE);
            m_lsvReview.setAdapter(m_adapter_review);
//            setListViewHeightBasedOnChildren(m_lsvReview);
        }
        else if (m_nSort == 2) {
            m_lsvReviewExp.setVisibility(View.VISIBLE);
            m_lsvReviewExp.setAdapter(m_adapter_review_exper);
//            setListViewHeightBasedOnChildren(m_lsvReviewExp);
        }
//        else if (m_nSort == 3) {
//            m_rcvPowerReview.setVisibility(View.VISIBLE);
//            m_adapter_power_review.notifyDataSetChanged();
//        }
//        else if (m_nSort == 4) {
//            m_lsvExpertColumn.setVisibility(View.VISIBLE);
//            m_lsvExpertColumn.setAdapter(m_adapter_expert_column);
////            setListViewHeightBasedOnChildren(m_lsvExpertColumn);
//        }
        else if (m_nSort == 3) {
            m_lsvFood.setVisibility(View.VISIBLE);
//            m_adapter_food.notifyDataSetChanged();
            m_adapter_food = new FoodListAdapter(this, arrFoodInfoDoubleList);
            m_lsvFood.setAdapter(m_adapter_food);
        }
    }

    private void connectServer(int strAct) {
        m_nConnectType = strAct;

        showProgress();
        String[] paramFields = new String[]{Net.NET_POST_FIELD_ACT,
                Net.NET_POST_FIELD_REQUEST};
        String[] paramValues = null;

        JSONObject w_objJSonData = new JSONObject();
        try {
            w_objJSonData.put(Net.NET_VALUE_MEMBER_ID, mb_id);
            if (m_nConnectType == CONNECT_TYPE_LIKE) {
                w_objJSonData.put("sort", "" + m_nSort);
                paramValues = new String[]{
                        Net.POST_FIELD_ACT_MY_LIKE,
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
        // 성공
        if (resultCode == Net.CONNECTION_SUCCSES) {
            if (m_nResultType == 200) {
                if (m_nConnectType == CONNECT_TYPE_LIKE) {
                    arrFilterValue[0] = "제품(" + arrNum[0] + "개)";
                    arrFilterValue[1] = "제품 리뷰(" + arrNum[1] + "개)";
                    arrFilterValue[2] = "노하우 공유(" + arrNum[2] + "개)";
//                    arrFilterValue[3] = "파워 리뷰(" + arrNum[3] + "개)";
//                    arrFilterValue[4] = "전문가 칼럼(" + arrNum[4] + "개)";
                    arrFilterValue[3] = "기능성분(" + arrNum[3] + "개)";

                    m_lsvGood.setVisibility(View.GONE);
                    m_lsvReview.setVisibility(View.GONE);
                    m_lsvReviewExp.setVisibility(View.GONE);
//                    m_rcvPowerReview.setVisibility(View.GONE);
//                    m_lsvExpertColumn.setVisibility(View.GONE);
                    m_lsvFood.setVisibility(View.GONE);

                    if (arrNum[0]>0){
                        m_lsvGood.setVisibility(View.VISIBLE);
                        m_lsvGood.setAdapter(m_adapter_good);
//                        m_adapter_good.notifyDataSetChanged();
                        m_tvTitle.setText("좋아요 - " + arrFilterValue[0]);
                        m_nSort=0;
                    }else if (arrNum[1]>0){
                        m_lsvReview.setVisibility(View.VISIBLE);
                        m_lsvReview.setAdapter(m_adapter_review);
//                        m_adapter_review.notifyDataSetChanged();
                        m_tvTitle.setText("좋아요 - " + arrFilterValue[1]);
                        m_nSort=1;
                    }else if (arrNum[2]>0){
                        m_lsvReviewExp.setVisibility(View.VISIBLE);
                        m_lsvReviewExp.setAdapter(m_adapter_review_exper);
//                        m_adapter_review_exper.notifyDataSetChanged();
                        m_tvTitle.setText("좋아요 - " + arrFilterValue[2]);
                        m_nSort=2;
                    }
//                    else if (arrNum[3]>0){
//                        m_rcvPowerReview.setVisibility(View.VISIBLE);
//                        m_adapter_power_review.notifyDataSetChanged();
//                        m_tvTitle.setText("좋아요 - " + arrFilterValue[3]);
//                        m_nSort=3;
//                    }else if (arrNum[4]>0){
//                        m_lsvExpertColumn.setVisibility(View.VISIBLE);
//                        m_lsvExpertColumn.setAdapter(m_adapter_expert_column);
////                        m_adapter_expert_column.notifyDataSetChanged();
//                        m_tvTitle.setText("좋아요 - " + arrFilterValue[4]);
//                        m_nSort=4;
//                    }
                    else if (arrNum[3]>0){
                        m_lsvFood.setVisibility(View.VISIBLE);
                        m_adapter_food = new FoodListAdapter(this, arrFoodInfoDoubleList);
                        m_lsvFood.setAdapter(m_adapter_food);
                        m_adapter_food.notifyDataSetChanged();
                        m_tvTitle.setText("좋아요 - " + arrFilterValue[3]);
                        m_nSort=3;
                    }else {
                        m_nSort=0;
                        new NoticeDialog(this, "'좋아요'를 한 적이 없으세요.", "현재 페이지에서 보여드릴 내용이 없습니다.ㅠㅠ","잠시 후에 자동으로 닫힙니다.", true).show();
                    }
                }
            }
        } else {
            if (!"".equals(strMsg)) {Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();}
        }
    }

    JSONObject _jResult;

    public void parseJSON() {
        try {
            m_nResultType = _jResult.getInt(Net.NET_VALUE_CODE); //
            JSONObject result = _jResult.getJSONObject(Net.NET_VALUE_RESULT);
            if (m_nConnectType == CONNECT_TYPE_LIKE) {

                GoodInfo m_info;
                JSONArray arr = result.getJSONArray("good");
                arrNum[0] = arr.length();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    m_info =  mNetUtil.parseGood(obj);
                    m_adapter_good.add(m_info);
                }

                ReviewInfo m_r_info;
                arr = result.getJSONArray("review");
                arrNum[1] = arr.length();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj_rev = arr.getJSONObject(i);
                    m_r_info =  mNetUtil.parseReview(obj_rev, true);
                    m_adapter_review.add(m_r_info);
                }

                arr = result.getJSONArray("exper");
                arrNum[2] = arr.length();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj_rev = arr.getJSONObject(i);
                    m_r_info =  mNetUtil.parseReview(obj_rev, false);
                    m_adapter_review_exper.add(m_r_info);
                }
//
//                WikiInfo mp_info;
//                arr = result.getJSONArray("power_review");
//                arrNum[3] = arr.length();
//                for (int i = 0; i < arr.length(); i++) {
//                    JSONObject obj_wki = arr.getJSONObject(i);
//                    mp_info = mNetUtil.parseWiki(obj_wki);
//                    arrPowerReviewList.add(mp_info);
//                }
//
//                arr = result.getJSONArray("expert_column");
//                arrNum[4] = arr.length();
//                for (int i = 0; i < arr.length(); i++) {
//                    JSONObject obj_wki = arr.getJSONObject(i);
//                    mp_info = mNetUtil.parseWiki(obj_wki);
//                    m_adapter_expert_column.add(mp_info);
//                }

                FoodInfo mf_info;
                arr = result.getJSONArray("food");
                arrNum[3] = arr.length();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj_fod = arr.getJSONObject(i);
                    mf_info = mNetUtil.parseFood(obj_fod);
                    arrFoodInfoList.add(mf_info);
                }
                arrFoodInfoDoubleList = mNetUtil.PackFood2to1(arrFoodInfoList);
            }
        } catch (JSONException e) {e.printStackTrace();}
    }

    int m_nSeletedPos = -1;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        m_nSeletedPos = pos;
        Intent intent;
        switch (m_nSort){
            case 0:
                GoodInfo m_info = m_adapter_good.getItem(pos);

                intent = new Intent(this, DetailGoodActivity.class);
                intent.putExtra("info", m_info);
                startActivityForResult(intent, 600);
                break;
            case 1:
                ReviewInfo r_info = m_adapter_review.getItem(pos);
                if (r_info == null)                return;

                intent = new Intent(this, DetailReviewActivity.class);
                intent.putExtra("info", r_info);
                startActivityForResult(intent, 601);
                break;
            case 2:
                ReviewInfo rx_info = m_adapter_review_exper.getItem(pos);
                if (rx_info == null)                return;

                intent = new Intent(this, DetailExpActivity.class);
                intent.putExtra("info", rx_info);
                intent.putExtra("exptitle", rx_info.title);
                startActivityForResult(intent, 602);
                break;
//            case 3:
//                break;
//            case 4:
//                WikiInfo ec_info = m_adapter_expert_column.getItem(pos);
//                if (ec_info == null)                return;
//
//                intent = new Intent(this, DetailPowerActivity.class);
//                intent.putExtra("info", ec_info);
//                startActivityForResult(intent, 604);
//                break;
            case 3:
                FoodInfoDeux fd_info = m_adapter_food.getItem(pos);
                if (fd_info == null)                return;

                intent = new Intent(this, DetailFoodActivity.class);
                if((pos +1) * 2 > arrFoodInfoList.size())
                    LeftOrRight= 0;
                intent.putExtra("LeftOrRight", LeftOrRight);
                intent.putExtra("info", fd_info);
                startActivityForResult(intent, 605);
                break;
        }
    }

//    public void gotoDetailFromLikeActivity(int pos) {
//        m_nSeletedPos = pos;
//        if (m_nSort == 3) {
//            WikiInfo info = arrPowerReviewList.get(pos);
//            Intent intent = new Intent(this, DetailPowerActivity.class);
//            intent.putExtra("info", info);
//            startActivityForResult(intent, 603);
//        }
//    }

    @Override
    protected void onActivityResult(int p_requestCode,
                                    int p_resultCode,
                                    Intent p_intentActivity) {

        super.onActivityResult(p_requestCode, p_resultCode, p_intentActivity);
        if (p_resultCode == RESULT_OK) {
            int view_cnt = p_intentActivity.getIntExtra("view_cnt", 0);
            int like_cnt = p_intentActivity.getIntExtra("like_cnt", 0);
            int comment_cnt = p_intentActivity.getIntExtra("comment_cnt", 0);
            int review_cnt = p_intentActivity.getIntExtra("review_cnt", 0);
            boolean unlike = p_intentActivity.getBooleanExtra("unlike", false);

            if(unlike){
                arrNum[m_nSort]--;

                arrFilterValue[0] = "제품(" + arrNum[0] + "개)";
                arrFilterValue[1] = "제품 리뷰(" + arrNum[1] + "개)";
                arrFilterValue[2] = "노하우 공유(" + arrNum[2] + "개)";
//                arrFilterValue[3] = "파워 리뷰(" + arrNum[3] + "개)";
//                arrFilterValue[4] = "전문가 칼럼(" + arrNum[4] + "개)";
                arrFilterValue[3] = "기능성분(" + arrNum[5] + "개)";

                m_tvTitle.setText("좋아요 - " + arrFilterValue[m_nSort]);
            }

            switch (p_requestCode) {
                case 600:
                    m_adapter_good.getItem(m_nSeletedPos)._view_cnt = view_cnt;
                    m_adapter_good.getItem(m_nSeletedPos)._review_cnt = review_cnt;
                    m_adapter_good.getItem(m_nSeletedPos)._like_cnt = like_cnt;
                    if(unlike)
                        m_adapter_good.remove(m_adapter_good.getItem(m_nSeletedPos));
                    m_adapter_review.notifyDataSetChanged();
                    break;
                case 601:
                    m_adapter_review.getItem(m_nSeletedPos).view_cnt = view_cnt;
                    m_adapter_review.getItem(m_nSeletedPos).comment_cnt = comment_cnt;
                    m_adapter_review.getItem(m_nSeletedPos).like_cnt = like_cnt;
                    if(unlike)
                        m_adapter_review.remove(m_adapter_review.getItem(m_nSeletedPos));
                    m_adapter_review.notifyDataSetChanged();
                    break;
                case 602:
                    m_adapter_review_exper.getItem(m_nSeletedPos).view_cnt = view_cnt;
                    m_adapter_review_exper.getItem(m_nSeletedPos).comment_cnt = comment_cnt;
                    m_adapter_review_exper.getItem(m_nSeletedPos).like_cnt = like_cnt;
                    if(unlike)
                        m_adapter_review_exper.remove(m_adapter_review_exper.getItem(m_nSeletedPos));
                    m_adapter_review_exper.notifyDataSetChanged();
                    break;
//                case 603:
//                    arrPowerReviewList.get(m_nSeletedPos)._view_cnt = view_cnt;
//                    arrPowerReviewList.get(m_nSeletedPos)._like_cnt = like_cnt;
//                    arrPowerReviewList.get(m_nSeletedPos)._comment_cnt = comment_cnt;
//                    if(unlike)
//                      arrPowerReviewList.remove(m_nSeletedPos);
//                    m_adapter_power_review = new PowerReviewRecycleAdapter(this, arrPowerReviewList);
//                    m_rcvPowerReview.setAdapter(m_adapter_power_review);
//                    break;
//                case 604:
//                    m_adapter_expert_column.getItem(m_nSeletedPos)._view_cnt = view_cnt;
//                    m_adapter_expert_column.getItem(m_nSeletedPos)._comment_cnt = comment_cnt;
//                    m_adapter_expert_column.getItem(m_nSeletedPos)._like_cnt = like_cnt;
//                      if(unlike)
//                        m_adapter_expert_column.remove(m_adapter_expert_column.getItem(m_nSeletedPos));
//                    m_adapter_expert_column.notifyDataSetChanged();
//                    break;
                case 605:
                    if(LeftOrRight==0){
                        arrFoodInfoDoubleList.get(m_nSeletedPos)._view_cnt = view_cnt;
                        arrFoodInfoDoubleList.get(m_nSeletedPos)._like_cnt = like_cnt;
                    } else if(LeftOrRight==1){
                        arrFoodInfoDoubleList.get(m_nSeletedPos)._view_cnt2 = view_cnt;
                        arrFoodInfoDoubleList.get(m_nSeletedPos)._like_cnt2 = like_cnt;
                    }
                    if(unlike){
                        arrFoodInfoList.remove(m_nSeletedPos*2+LeftOrRight);
                        arrFoodInfoDoubleList = mNetUtil.PackFood2to1(arrFoodInfoList);
                        m_adapter_food = new FoodListAdapter(this, arrFoodInfoDoubleList);
                        m_lsvFood.setAdapter(m_adapter_food);
                    }
                    m_adapter_food.notifyDataSetChanged();
                    break;
            }
        }
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onParseJSon(JSONObject j_source) {
        _jResult = j_source;
    }
}
