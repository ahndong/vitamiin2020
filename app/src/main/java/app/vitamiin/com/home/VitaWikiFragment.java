package app.vitamiin.com.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.vitamiin.com.Adapter.FoodListAdapter;
import app.vitamiin.com.Adapter.PowerReviewRecycleAdapter;
import app.vitamiin.com.Adapter.ExpertColumnListAdapter;
import app.vitamiin.com.Model.FoodInfo;
import app.vitamiin.com.Model.FoodInfoDeux;
import app.vitamiin.com.Model.WikiInfo;
import app.vitamiin.com.R;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;

public class VitaWikiFragment extends Fragment implements View.OnClickListener, OnParseJSonListener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener {
    Fragment m_fragment =this;
    MainActivity m_MainAct;
    int m_nResultType = 200;
    NetUtil mNetUtil;
    NetUtil.connectAndgetServer mNetUtilConnetServer;
    int Power1_Food2_Exper3 = 0;
    Boolean[] m_isListLoaded = {false, false, false};

    int m_nConnectType = NetUtil.apis_GET_POWER_REVIEW_LIST;

    int m_currentPage = 1;
    int m_nMaxPage_power = 1;
    int m_nMaxPage_food = 1;
    int m_nMaxPage_expert = 1;

    RecyclerView m_rcvPowerReview;
    PowerReviewRecycleAdapter m_PowerReviewAdapter;
    LinearLayoutManager mLayoutManager;

    ListView m_lsvFoodAndColumn;
    FoodListAdapter m_FoodAdapter;
    ExpertColumnListAdapter m_ExpertAdapter;

    TextView m_tvSort1, m_tvSort2, m_tvSort3, m_tvPosition;
    ImageView m_imvPositionArrow;

    private ArrayList<WikiInfo> arrPowerReviewList = new ArrayList<>();
    private ArrayList<FoodInfo> arrFoodInfoList = new ArrayList<>();
    private ArrayList<FoodInfoDeux> arrFoodInfoDoubleList = new ArrayList<>();
    private ArrayList<WikiInfo> arrExpertColumnList = new ArrayList<>();
    private WikiInfo m_wikiInfo = new WikiInfo();

    int m_category = -1;
    int LeftOrRight;

    public static VitaWikiFragment newInstance() {
        return new VitaWikiFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vitawiki, container, false);
        m_MainAct = (MainActivity) getActivity();
        mNetUtil = new NetUtil();
        mNetUtilConnetServer = mNetUtil.new connectAndgetServer(m_MainAct);

        initView(rootView);

        m_MainAct.m_LockView = false;
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getView() != null) {//위키 탭에 들어온 경로임. "파워리뷰를 새로 로드한다" 또는 이미 불러왔을 경우, 맨 위로 스크롤만 올려준다. by 동현
                m_nConnectType = NetUtil.apis_GET_POWER_REVIEW_LIST;
                Power1_Food2_Exper3 = 0;
                if(m_isListLoaded[Power1_Food2_Exper3]){
                    m_MainAct.m_LockView = false;
                    //rcv_power_review를 맨 위로 스크롤 한다. (새로 불러올 필요는 없다.) by 동현
                }else{
                    m_MainAct.showProgress();
                    m_MainAct.m_LockView = true;
                    m_rcvPowerReview.setVisibility(View.VISIBLE);
                    m_lsvFoodAndColumn.setVisibility(View.GONE);
                    m_tvSort1.setSelected(true);
                    m_tvSort2.setSelected(false);
                    m_tvSort3.setSelected(false);
                    m_currentPage = 1;
                    m_nMaxPage_power = 1;
                    m_nMaxPage_food = 1;
                    m_nMaxPage_expert = 1;
                    m_category = -1;

                    arrPowerReviewList.clear();
                    m_rcvPowerReview.setAdapter(m_PowerReviewAdapter);
                    mNetUtilConnetServer.connectServer(m_fragment, handler, m_nConnectType, String.valueOf(m_currentPage));
                }
            } else {
                Thread t = new Thread() {
                    public void run() {
                        while (true) {
                            if (getView() != null) {
                                connectHandler.sendEmptyMessage(0);
                                break;
                            }
                            try {Thread.sleep(1000);}
                            catch (InterruptedException e) {e.printStackTrace();}
                        }
                    }
                };
                t.start();
            }
        }
    }

    Handler connectHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {  //??어디서 들어온 경로?? "파워리뷰를 새로 로드한다" 또는 이미 불러왔을 경우, 맨 위로 스크롤만 올려준다. by 동현
                m_nConnectType = NetUtil.apis_GET_POWER_REVIEW_LIST;
                Power1_Food2_Exper3 = 0;
                if(m_isListLoaded[Power1_Food2_Exper3]){//rcv_power_review를 맨 위로 스크롤 한다. (새로 불러올 필요는 없다.) by 동현
                    m_MainAct.m_LockView = false;
                }else{
                    m_MainAct.showProgress();
                    m_MainAct.m_LockView = true;
                    m_rcvPowerReview.setVisibility(View.VISIBLE);
                    m_lsvFoodAndColumn.setVisibility(View.GONE);
                    m_tvSort1.setSelected(true);
                    m_tvSort2.setSelected(false);
                    m_tvSort3.setSelected(false);
                    m_currentPage = 1;
                    m_nMaxPage_power = 1;
                    m_nMaxPage_food = 1;
                    m_nMaxPage_expert = 1;
                    m_category = -1;

                    arrPowerReviewList.clear();
                    m_rcvPowerReview.setAdapter(m_PowerReviewAdapter);
                    mNetUtilConnetServer.connectServer(m_fragment, handler, m_nConnectType, String.valueOf(m_currentPage));
                }
            }
        }
    };

    private void initView(View v) {
        m_currentPage = 1;
        m_nMaxPage_power = 1;
        m_nMaxPage_food = 1;
        m_nMaxPage_expert = 1;

        m_tvPosition = (TextView) v.findViewById(R.id.tv_position);
        m_tvPosition.setOnClickListener(this);

        m_imvPositionArrow = (ImageView)v.findViewById(R.id.imv_position_arrow);
        m_imvPositionArrow.setOnClickListener(this);

        m_rcvPowerReview = (RecyclerView) v.findViewById(R.id.rcv_power_review);
        mLayoutManager = new LinearLayoutManager(m_MainAct);
        m_rcvPowerReview.setLayoutManager(mLayoutManager);
        m_rcvPowerReview.setOnScrollListener(m_hidingScrollListener);
        m_lsvFoodAndColumn = (ListView) v.findViewById(R.id.lsv_food_column);
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        m_PowerReviewAdapter = new PowerReviewRecycleAdapter(m_MainAct, arrPowerReviewList);
        m_FoodAdapter = new FoodListAdapter(m_MainAct, arrFoodInfoDoubleList);
        m_ExpertAdapter = new ExpertColumnListAdapter(m_MainAct, arrExpertColumnList);
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        m_rcvPowerReview.setAdapter(m_PowerReviewAdapter);
        m_lsvFoodAndColumn.setAdapter(m_FoodAdapter);
        //m_lsvFoodAndColumn.setAdapter(m_ExpertAdapter);
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        m_lsvFoodAndColumn.setOnItemClickListener(this);
        m_lsvFoodAndColumn.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                LeftOrRight = (int)event.getX() < (view.getMeasuredWidth()/2) ? 0:1;
                return false;
            }
        });
        m_lsvFoodAndColumn.setOnScrollListener(this);

        m_rcvPowerReview.setVisibility(View.VISIBLE);
        m_lsvFoodAndColumn.setVisibility(View.GONE);
        m_tvSort1 = (TextView) v.findViewById(R.id.tv_sort_1);
        m_tvSort2 = (TextView) v.findViewById(R.id.tv_sort_2);
        m_tvSort3 = (TextView) v.findViewById(R.id.tv_sort_3);
        m_tvSort1.setOnClickListener(this);
        m_tvSort2.setOnClickListener(this);
        m_tvSort3.setOnClickListener(this);
        m_tvSort1.setSelected(true);
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
//        m_hidingScrollListener.controlsVisible = false;
//        m_hidingScrollListener.scrolledDistance = 0;
//
//        m_rcvReview.setPadding(0,0,0,0);
//        m_uiLlyTopbar.animate().translationY(-m_uiLlyTopbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
//        m_imvWrite.animate().translationY(m_imvWrite.getHeight()*2).setInterpolator(new DecelerateInterpolator(1));
    }

    private void showViews() {
//        m_hidingScrollListener.controlsVisible = true;
//        m_hidingScrollListener.scrolledDistance = 0;
//
//        m_rcvReview.setPadding(0, m_uiLlyTopbar.getHeight(), 0, 0);
//        m_uiLlyTopbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    private abstract class HidingScrollListener extends RecyclerView.OnScrollListener {
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

            if(pastVisiblesItems>4)
                viewPositionTextView();
            else
                hidePositionTextView();

            if (pastVisiblesItems >= count && totalItemCount != 1
                    && m_currentPage < m_nMaxPage_power && !m_MainAct.m_LockView) {
                m_MainAct.showProgress();
                m_MainAct.m_LockView = true;
                m_currentPage++;
                mNetUtilConnetServer.connectServer(m_fragment, handler, m_nConnectType, String.valueOf(m_currentPage));
            }
        }
        public abstract void onHide();
        public abstract void onShow();

    }

    public void viewPositionTextView(){
        m_imvPositionArrow.setVisibility(View.VISIBLE);
        m_imvPositionArrow.animate().alpha(0.8f);
    }

    public void hidePositionTextView(){
        m_imvPositionArrow.animate().alpha(0f);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imv_position_arrow:
                if (m_nConnectType == NetUtil.apis_GET_POWER_REVIEW_LIST)
                    m_rcvPowerReview.smoothScrollToPosition(0);
                else if(m_nConnectType == NetUtil.apis_GET_HEALTH_FOOD_LIST || m_nConnectType == NetUtil.apis_GET_EXP_COLUMN_LIST)
                    m_lsvFoodAndColumn.smoothScrollToPositionFromTop(0, 0, 300);
                break;
            case R.id.tv_sort_1:
                if(m_MainAct.m_LockView)
                    break;
                m_rcvPowerReview.setVisibility(View.VISIBLE);
                m_lsvFoodAndColumn.setVisibility(View.GONE);
                m_imvPositionArrow.setVisibility(View.GONE);
                m_tvSort1.setSelected(true);
                m_tvSort2.setSelected(false);
                m_tvSort3.setSelected(false);
                m_PowerReviewAdapter.notifyDataSetChanged();

                m_nConnectType = NetUtil.apis_GET_POWER_REVIEW_LIST;
                Power1_Food2_Exper3 = 0;
                if(m_isListLoaded[Power1_Food2_Exper3]){//rcv_power_review를 맨 위로 스크롤 한다. (새로 불러올 필요 없다) by 동현
                    m_rcvPowerReview.smoothScrollToPosition(0);
                    m_MainAct.m_LockView = false;
                }else{
                    m_MainAct.showProgress();
                    m_MainAct.m_LockView=true;
                    m_currentPage = 1;
                    m_nMaxPage_power = 1;
                    m_category = -1;

                    arrPowerReviewList.clear();
                    mNetUtilConnetServer.connectServer(m_fragment, handler, m_nConnectType, String.valueOf(m_currentPage));
                }
                break;
            case R.id.tv_sort_2:
                if(m_MainAct.m_LockView)
                    break;
                m_rcvPowerReview.setVisibility(View.GONE);
                m_lsvFoodAndColumn.setVisibility(View.VISIBLE);
                m_imvPositionArrow.setVisibility(View.GONE);
                m_tvSort1.setSelected(false);
                m_tvSort2.setSelected(true);
                m_tvSort3.setSelected(false);
                m_FoodAdapter = new FoodListAdapter(m_MainAct, arrFoodInfoDoubleList);
                m_lsvFoodAndColumn.setAdapter(m_FoodAdapter);
                m_FoodAdapter.notifyDataSetChanged();

                m_nConnectType = NetUtil.apis_GET_HEALTH_FOOD_LIST;
                Power1_Food2_Exper3 = 1;
                if(m_isListLoaded[Power1_Food2_Exper3]){//food list를 맨 위로 스크롤 한다. (새로 불러올 필요 없다.) by 동현
                    m_lsvFoodAndColumn.smoothScrollToPosition(0);
                    m_MainAct.m_LockView = false;
                }else{
                    m_MainAct.showProgress();
                    m_MainAct.m_LockView=true;
                    m_currentPage = 1;
                    m_nMaxPage_food = 1;

                    arrFoodInfoDoubleList.clear();
//                    m_FoodAdapter.clear();
                    mNetUtilConnetServer.connectServer(m_fragment, handler, m_nConnectType, String.valueOf(m_currentPage));
                }
                break;
            case R.id.tv_sort_3:
                if(m_MainAct.m_LockView)
                    break;
                m_rcvPowerReview.setVisibility(View.GONE);
                m_lsvFoodAndColumn.setVisibility(View.VISIBLE);
                m_imvPositionArrow.setVisibility(View.GONE);
                m_tvSort1.setSelected(false);
                m_tvSort2.setSelected(false);
                m_tvSort3.setSelected(true);
                m_lsvFoodAndColumn.setAdapter(m_ExpertAdapter);
                m_ExpertAdapter.notifyDataSetChanged();

                m_nConnectType = NetUtil.apis_GET_EXP_COLUMN_LIST;
                Power1_Food2_Exper3 = 2;
                if(m_isListLoaded[Power1_Food2_Exper3]){//expercolumn을 맨 위로 스크롤 한다. (새로 불러올 필요 없다.) by 동현
                    m_lsvFoodAndColumn.smoothScrollToPosition(0);
                    m_MainAct.m_LockView = false;
                }else{
                    m_MainAct.showProgress();
                    m_MainAct.m_LockView=true;
                    m_currentPage = 1;
                    m_nMaxPage_expert = 1;

                    arrExpertColumnList.clear();
                    mNetUtilConnetServer.connectServer(m_fragment, handler, m_nConnectType, String.valueOf(m_currentPage));
                }
                break;
        }
    }

    private void processForNetEnd() {
        parseJSON();
        m_MainAct.closeProgress();
        HttpRequester.getInstance().stopNetThread();

        int resultCode = HttpRequester.getInstance().getResultCode();
        String strMsg = HttpRequester.getInstance().getResultMsg();

        if (resultCode == Net.CONNECTION_SUCCSES) {
            if (m_nResultType == 200) {
                m_isListLoaded[Power1_Food2_Exper3] = true;
                if (m_nConnectType == NetUtil.apis_GET_POWER_REVIEW_LIST) {
                    m_PowerReviewAdapter.notifyDataSetChanged();
                } else if(m_nConnectType == NetUtil.apis_GET_HEALTH_FOOD_LIST) {
//                    m_FoodAdapter.notifyDataSetChanged();
                    m_FoodAdapter = new FoodListAdapter(m_MainAct, arrFoodInfoDoubleList);
                    m_lsvFoodAndColumn.setAdapter(m_FoodAdapter);
                } else if (m_nConnectType == NetUtil.apis_GET_EXP_COLUMN_LIST) {
                    m_ExpertAdapter.notifyDataSetChanged();
                } else if(m_nConnectType == NetUtil.apis_ADD_VIEWCNT_POWER_REVIEW || m_nConnectType == NetUtil.apis_ADD_VIEWCNT_EXP_COLUMN){
                    Intent webOpen = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(m_wikiInfo._content));
                    startActivityForResult(webOpen, 701);
                    m_nConnectType = m_nConnectType==NetUtil.apis_ADD_VIEWCNT_POWER_REVIEW? NetUtil.apis_GET_POWER_REVIEW_LIST:NetUtil.apis_GET_EXP_COLUMN_LIST;
                }
            }
        } else {
            if (!"".equals(strMsg))
                Toast.makeText(m_MainAct, strMsg, Toast.LENGTH_SHORT).show();
        }
        m_MainAct.closeProgress();
        m_MainAct.m_LockView = false;
    }

    public void parseJSON() {
        try {
            if(_jResult==null) return;
            m_nResultType = _jResult.getInt(Net.NET_VALUE_CODE); //
            JSONObject obj = _jResult.getJSONObject(Net.NET_VALUE_RESULT);

            if (m_nConnectType == NetUtil.apis_GET_HEALTH_FOOD_LIST) {
                m_nMaxPage_food = obj.getInt(Net.NET_VALUE_TOTAL_PAGE);
                FoodInfo mf_info;
                JSONArray arr_fod = obj.getJSONArray("food");
                for (int i = 0; i < arr_fod.length(); i++) {
                    JSONObject obj_fod = arr_fod.getJSONObject(i);
                    mf_info = new NetUtil().parseFood(obj_fod);
                    arrFoodInfoList.add(mf_info);
                }
                arrFoodInfoDoubleList = new NetUtil().PackFood2to1(arrFoodInfoList);
            } else if(m_nConnectType == NetUtil.apis_GET_POWER_REVIEW_LIST || m_nConnectType == NetUtil.apis_GET_EXP_COLUMN_LIST) {
                JSONArray arr = obj.getJSONArray("wiki");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj_wki = arr.getJSONObject(i);
                    WikiInfo info = new NetUtil().parseWiki(obj_wki);
                    if (m_nConnectType == NetUtil.apis_GET_POWER_REVIEW_LIST){
                        m_nMaxPage_power = obj.getInt(Net.NET_VALUE_TOTAL_PAGE);
                        arrPowerReviewList.add(info);}
                    else if(m_nConnectType == NetUtil.apis_GET_EXP_COLUMN_LIST){
                        m_nMaxPage_expert = obj.getInt(Net.NET_VALUE_TOTAL_PAGE);
                        arrExpertColumnList.add(info);}
                }
            } else if(m_nConnectType == NetUtil.apis_ADD_VIEWCNT_POWER_REVIEW){
                arrPowerReviewList.get(m_nSeletedPos)._view_cnt++;
                m_PowerReviewAdapter.notifyDataSetChanged();
            } else if(m_nConnectType == NetUtil.apis_ADD_VIEWCNT_EXP_COLUMN){
                m_ExpertAdapter.getItem(m_nSeletedPos)._view_cnt++;
                m_ExpertAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    int m_nSeletedPos = -1;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        m_nSeletedPos = pos;
        ///////////한 개는 RecyclerView이고, 두 개는 ListView이므로 아래 세 if 문이 다 통과하지 않는다.
        if (m_nConnectType == NetUtil.apis_GET_HEALTH_FOOD_LIST) {
            FoodInfoDeux fd_info = arrFoodInfoDoubleList.get(pos);
            if (fd_info == null)                return;

            Intent intent = new Intent(m_MainAct, DetailFoodActivity.class);
            if((pos +1) * 2 > arrFoodInfoList.size())
                LeftOrRight= 0;
            intent.putExtra("LeftOrRight", LeftOrRight);
            intent.putExtra("info", fd_info);
            m_MainAct.startActivityForResult(intent, 702);
        } else if (m_nConnectType == NetUtil.apis_GET_EXP_COLUMN_LIST) {
            m_wikiInfo = arrExpertColumnList.get(pos);
            if (m_wikiInfo == null)                 return;

//            Intent intent = new Intent(m_MainAct, DetailPowerActivity.class);
//            intent.putExtra("info", m_wikiInfo);
//            intent.putExtra("isPower", 2);
//            m_MainAct.startActivityForResult(intent, 703);

            m_nConnectType = NetUtil.apis_ADD_VIEWCNT_EXP_COLUMN;
            mNetUtilConnetServer.connectServer(m_fragment, handler, m_nConnectType, m_wikiInfo._id);
        }
    }

    public void gotoDetailFromWikiTab(int pos) {
        m_nSeletedPos = pos;
        if (m_nConnectType == NetUtil.apis_GET_POWER_REVIEW_LIST) {
            m_wikiInfo = arrPowerReviewList.get(pos);

//            Intent intent = new Intent(getActivity(), DetailPowerActivity.class);
//            intent.putExtra("info", m_wikiInfo);
//            intent.putExtra("isPower", 1);
//            m_MainAct.startActivityForResult(intent, 701);

            m_nConnectType = NetUtil.apis_ADD_VIEWCNT_POWER_REVIEW;
            mNetUtilConnetServer.connectServer(m_fragment, handler, m_nConnectType, m_wikiInfo._id);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int count = totalItemCount - visibleItemCount;

        int theMaxPage=m_nMaxPage_expert;
        if (m_nConnectType == NetUtil.apis_GET_HEALTH_FOOD_LIST) {
            theMaxPage = m_nMaxPage_food;
        } else if (m_nConnectType == NetUtil.apis_GET_EXP_COLUMN_LIST) {
            theMaxPage = m_nMaxPage_expert;
        }

        if (firstVisibleItem >= count && totalItemCount != 1
                && m_currentPage < theMaxPage && !m_MainAct.m_LockView) {
            m_currentPage++;
            m_MainAct.showProgress();
            m_MainAct.m_LockView = true;
            mNetUtilConnetServer.connectServer(m_fragment, handler, m_nConnectType, String.valueOf(m_currentPage));
        }
//        m_tvPosition.setText(String.valueOf(totalItemCount + "-" + visibleItemCount) + "=" + count + "/" + firstVisibleItem);
        if (firstVisibleItem > 4 && m_imvPositionArrow.getAlpha()!=0.8)
            viewPositionTextView();
        else if(getView()!=null)
            hidePositionTextView();
    }

    public void updateWikiItem(int view_cnt, int like_cnt, int comment_cnt) {
        if (m_nSeletedPos == -1)
            return;
        if (m_nConnectType == NetUtil.apis_GET_POWER_REVIEW_LIST) {
        }
        else if (m_nConnectType == NetUtil.apis_GET_HEALTH_FOOD_LIST) {
            if (m_FoodAdapter.getItem(m_nSeletedPos) != null) {
                if(LeftOrRight==0){
                    arrFoodInfoDoubleList.get(m_nSeletedPos)._view_cnt = view_cnt;
                    arrFoodInfoDoubleList.get(m_nSeletedPos)._like_cnt = like_cnt;
                } else if(LeftOrRight==1){
                    arrFoodInfoDoubleList.get(m_nSeletedPos)._view_cnt2 = view_cnt;
                    arrFoodInfoDoubleList.get(m_nSeletedPos)._like_cnt2 = like_cnt;
                }
                m_FoodAdapter.notifyDataSetChanged();
            }
        }
        else if (m_nConnectType == NetUtil.apis_GET_EXP_COLUMN_LIST) {
        }
    }

    JSONObject _jResult;
    @Override
    public void onParseJSon(JSONObject j_source) {
        _jResult = j_source;
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Net.THREAD_REQUEST_END) {
                processForNetEnd();}
        }
    };
}