package app.vitamiin.com.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import app.vitamiin.com.Adapter.ReviewRecycleAdapter;
import app.vitamiin.com.Adapter.ReviewExperRecycleAdapter;
import app.vitamiin.com.Model.ReviewInfo;
import app.vitamiin.com.R;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;
import app.vitamiin.com.login.ListSelectDialog;
import app.vitamiin.com.login.ReviewSelectDialog;

public class ReviewFragment extends Fragment implements View.OnClickListener, OnParseJSonListener {
    MainActivity m_MainAct;
    Fragment m_fragment =this;
    int m_nResultType = 200;
    long lastServerConBySecond[] = {0,0};

    int CONNECT_TYPE_REVIEW_LIST = 0;
    int CONNECT_TYPE_REVIEW_EXP = 1;
    int m_nConnectType = CONNECT_TYPE_REVIEW_LIST;

    //    PullRefreshLayout layout;
    TextView m_tvSort1, m_tvSort2, m_tvOrder, m_tvCategory;
    ImageView m_imvWrite, m_imvPosionArrow;
    RelativeLayout m_llyOrderPopup;
    LinearLayout m_uiLlyTopbar, m_imvWrite1;
    LinearLayoutManager mLayoutManager;
    RecyclerView m_rcvReview;

    public ReviewRecycleAdapter m_review_adapter;
    private ArrayList<ReviewInfo> arrList_rev = new ArrayList<>();
    public ReviewExperRecycleAdapter m_exp_review_adapter;
    private ArrayList<ReviewInfo> arrList_exp = new ArrayList<>();

    String m_order[] = {"match", "regdate"};
    int[] m_category1 = new int[45];
    boolean isSetCat = false;
    int m_category = -1;
    int m_currentPage[] = {1,1};
    int m_nMaxPage[] = {1,1};

    boolean notyet = false;
    TextView m_tvConfirm;

    public static ReviewFragment newInstance() {
        return new ReviewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_review, container, false);
        m_MainAct = (MainActivity) getActivity();

        initView(rootView);

        m_MainAct.m_LockView = false;
        return rootView;
    }

    @Override   //탭을 선택해서 들어오면 여기 코드가 실행 by 동현
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getView() != null) {
                m_nConnectType = CONNECT_TYPE_REVIEW_LIST;
                m_tvCategory.setText("리뷰검색");
                m_llyOrderPopup.setVisibility(View.GONE);
                m_tvSort1.setSelected(true);
                m_tvSort2.setSelected(false);
                if(lastServerConBySecond[0] == 0 || System.currentTimeMillis() - lastServerConBySecond[0] > 30000) {
                    m_MainAct.showProgress();
                    m_MainAct.m_LockView = true;
                    m_order[0] = "regdate";
                    Arrays.fill(m_category1, 1);
                    m_currentPage[0] = 1;
                    m_nMaxPage[0] = 1;
                    arrList_rev.clear();
//                    arrList_rev.add(new ReviewInfo());
                    m_rcvReview.setAdapter(m_review_adapter);
                    connectServer(m_nConnectType);
                    showViews();
                }
            } else {
                Thread t = new Thread() {
                    public void run() {
                        while (true) {
                            if (getView() != null) {
                                connectHandler.sendEmptyMessage(0);
                                break;  }
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
            if (msg.what == 0) {   // Message id 가 0 이면
                m_nConnectType = CONNECT_TYPE_REVIEW_LIST;
                m_tvCategory.setText("리뷰검색");
                m_llyOrderPopup.setVisibility(View.GONE);
                m_tvSort1.setSelected(true);
                m_tvSort2.setSelected(false);
                if(lastServerConBySecond[0] == 0 || System.currentTimeMillis() - lastServerConBySecond[0] > 30000) {
                    m_MainAct.showProgress();
                    m_MainAct.m_LockView = true;
                    m_order[0] = "regdate";
                    Arrays.fill(m_category1, 1);
                    m_currentPage[0] = 1;
                    m_nMaxPage[0] = 1;
                    arrList_rev.clear();
//                    arrList_rev.add(new ReviewInfo());
                    m_rcvReview.setAdapter(m_review_adapter);
                    connectServer(m_nConnectType);
                    showViews();
                }
            }
        }
    };

    private void initView(View v) {
        m_imvPosionArrow = (ImageView) v.findViewById(R.id.imv_position_arrow);
        m_imvPosionArrow.setOnClickListener(this);
        v.findViewById(R.id.rly_background).setOnClickListener(this);

        m_rcvReview = (RecyclerView) v.findViewById(R.id.lsv_review);
        mLayoutManager = new LinearLayoutManager(m_MainAct);
        m_rcvReview.setLayoutManager(mLayoutManager);
        m_rcvReview.addOnScrollListener(m_hidingScrollListener);

        m_uiLlyTopbar = (LinearLayout) v.findViewById(R.id.lly_topbar);
        m_tvCategory = (TextView) v.findViewById(R.id.tv_category);

        m_review_adapter = new ReviewRecycleAdapter(m_MainAct, arrList_rev, true);
        m_exp_review_adapter = new ReviewExperRecycleAdapter(m_MainAct, arrList_exp);
//        arrList_rev.clear();
//        arrList_exp.clear();
//        arrList_rev.add(new ReviewInfo());
//        arrList_exp.add(new ReviewInfo());
        m_rcvReview.setAdapter(m_review_adapter);

        m_tvOrder = (TextView) v.findViewById(R.id.tv_order);
        m_tvSort1 = (TextView) v.findViewById(R.id.tv_sort_1);
        m_tvSort2 = (TextView) v.findViewById(R.id.tv_sort_2);
        m_tvSort1.setOnClickListener(this);
        m_tvSort2.setOnClickListener(this);
        m_tvSort1.setSelected(true);

        m_imvWrite = (ImageView) v.findViewById(R.id.imv_write);
        m_imvWrite1 = (LinearLayout) v.findViewById(R.id.imv_write1);
        m_imvWrite.setOnClickListener(this);

        v.findViewById(R.id.lly_order).setOnClickListener(this);
        m_llyOrderPopup = (RelativeLayout) v.findViewById(R.id.rly_background);
        v.findViewById(R.id.tv_order_regdate).setOnClickListener(this);
        v.findViewById(R.id.tv_order_like).setOnClickListener(this);
        v.findViewById(R.id.lly_category).setOnClickListener(this);
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        m_tvConfirm = (TextView) v.findViewById(R.id.tv_confirm);
        m_tvConfirm.setOnClickListener(this);
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    HidingScrollListener m_hidingScrollListener = new HidingScrollListener() {
        @Override
        public void onHide() {hideViews();}
        @Override
        public void onShow() {showViews();}
    };

    private void hideViews() {
        m_hidingScrollListener.controlsVisible = false;
        m_hidingScrollListener.scrolledDistance = 0;

        m_rcvReview.setPadding(0,0,0,0);
        m_uiLlyTopbar.animate().translationY(-m_uiLlyTopbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
        m_imvWrite.animate().translationY(m_imvWrite.getHeight()*2).setInterpolator(new DecelerateInterpolator(1));
        m_imvWrite1.animate().translationY(m_imvWrite1.getHeight()*2).setInterpolator(new DecelerateInterpolator(3));
//        m_imvWrite.animate().translationY(m_imvWrite.getHeight() * 2).setInterpolator(new AccelerateInterpolator(2));
    }

    private void showViews() {
        if(m_order[m_nConnectType].equals("like_cnt"))
            m_tvOrder.setText("좋아요 ");
        else
            m_tvOrder.setText("최신 순");
        m_hidingScrollListener.controlsVisible = true;
        m_hidingScrollListener.scrolledDistance = 0;

        m_rcvReview.setPadding(0, m_uiLlyTopbar.getHeight(), 0, 0);
        m_uiLlyTopbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        m_imvWrite.animate().translationY(0).setInterpolator(new DecelerateInterpolator(1));
        m_imvWrite1.animate().translationY(0).setInterpolator(new DecelerateInterpolator(3));
        //        m_imvWrite.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
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

            //m_tvPosition.setText(String.valueOf(pastVisiblesItems));
            if(pastVisiblesItems>2)
                viewPositionTextView();
            else
                hidePositionTextView();

            if (pastVisiblesItems >= count && totalItemCount != 1
                    && m_currentPage[m_nConnectType] < m_nMaxPage[m_nConnectType] && !m_MainAct.m_LockView) {
                m_MainAct.showProgress();
                m_MainAct.m_LockView=true;
                m_currentPage[m_nConnectType]++;
                connectServer(m_nConnectType);
            }
        }
        public abstract void onHide();
        public abstract void onShow();
    }

    public void viewPositionTextView(){
        m_imvPosionArrow.setVisibility(View.VISIBLE);
        m_imvPosionArrow.animate().alpha(0.8f);
    }

    public void hidePositionTextView(){
        m_imvPosionArrow.animate().alpha(0f);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rly_background:
                m_llyOrderPopup.setVisibility(View.GONE);
                break;
            case R.id.imv_position_arrow:
                m_rcvReview.smoothScrollToPosition(0);
                m_hidingScrollListener.controlsVisible = true;
                m_hidingScrollListener.scrolledDistance = 0;
                m_rcvReview.setPadding(0, m_uiLlyTopbar.getHeight(), 0, 0);
                m_uiLlyTopbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
                break;
            case R.id.tv_sort_1:
                if(m_MainAct.m_LockView)
                    break;
                m_nConnectType = CONNECT_TYPE_REVIEW_LIST;
                m_tvCategory.setText("리뷰검색");
                m_imvWrite.setVisibility(View.VISIBLE);
                m_imvWrite1.setVisibility(View.VISIBLE);
                m_tvSort1.setSelected(true);
                m_tvSort2.setSelected(false);
                m_rcvReview.setAdapter(m_review_adapter);
                m_review_adapter.notifyDataSetChanged();
                showViews();
                if(lastServerConBySecond[CONNECT_TYPE_REVIEW_LIST] == 0 || System.currentTimeMillis() - lastServerConBySecond[CONNECT_TYPE_REVIEW_LIST] > 60000) {
                    m_MainAct.showProgress();
                    m_MainAct.m_LockView=true;
                    m_order[0] = "regdate";
                    m_tvOrder.setText("최신 순");
                    Arrays.fill(m_category1, 1);
                    isSetCat = true;
                    m_currentPage[0] = 1;
                    m_nMaxPage[0] = 1;
                    arrList_rev.clear();
//                    arrList_rev.add(new ReviewInfo());
                    m_rcvReview.setAdapter(m_review_adapter);
                    connectServer(m_nConnectType);
                }
                break;
            case R.id.tv_sort_2:
                if(m_MainAct.m_LockView)
                    break;
                m_nConnectType = CONNECT_TYPE_REVIEW_EXP;
                m_tvCategory.setText("카테고리");
                m_imvWrite.setVisibility(View.VISIBLE);
                m_imvWrite1.setVisibility(View.VISIBLE);
                m_tvSort2.setSelected(true);
                m_tvSort1.setSelected(false);
                m_rcvReview.setAdapter(m_exp_review_adapter);
                m_exp_review_adapter.notifyDataSetChanged();
                showViews();
                if(lastServerConBySecond[CONNECT_TYPE_REVIEW_EXP] == 0 || System.currentTimeMillis() - lastServerConBySecond[CONNECT_TYPE_REVIEW_EXP] > 60000) {
                    m_MainAct.showProgress();
                    m_MainAct.m_LockView = true;
                    m_order[1] = "regdate";
                    m_tvOrder.setText("최신 순");
                    m_category = -1;
                    m_currentPage[1] = 1;
                    m_nMaxPage[1] = 1;
                    arrList_exp.clear();
//                    arrList_exp.add(new ReviewInfo());
                    m_rcvReview.setAdapter(m_exp_review_adapter);
                    connectServer(m_nConnectType);
                }
                break;
            case R.id.lly_category:
                if (m_nConnectType == CONNECT_TYPE_REVIEW_LIST) {
                    new ReviewSelectDialog(getActivity(), "리뷰검색", getResources().getStringArray(R.array.review_category_reg), getResources().getStringArray(R.array.array_disease), 11, m_category1).show();
                } else {
                    new ListSelectDialog(getActivity(), "카테고리", getResources().getStringArray(R.array.exp_category), 11, m_category).show();
                }
                break;
            case R.id.lly_order:
//                new ListSelectDialog(getActivity(), "정렬", getResources().getStringArray(R.array.review_order), 10).show();
                m_llyOrderPopup.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_order_regdate:
                m_MainAct.showProgress();
                m_MainAct.m_LockView=true;
                m_llyOrderPopup.setVisibility(View.GONE);

                m_order[m_nConnectType] = "regdate";
                m_tvOrder.setText("최신 순");
                m_currentPage[m_nConnectType] = 1;
                m_nMaxPage[m_nConnectType] = 1;
                if (m_nConnectType == CONNECT_TYPE_REVIEW_LIST) {
                    arrList_rev.clear();
//                    arrList_rev.add(new ReviewInfo());
                }else{
                    arrList_exp.clear();
//                    arrList_exp.add(new ReviewInfo());
                }
                connectServer(m_nConnectType);
                break;
            case R.id.tv_order_like:
                m_MainAct.showProgress();
                m_MainAct.m_LockView=true;
                m_llyOrderPopup.setVisibility(View.GONE);

                m_order[m_nConnectType] = "like_cnt";
                m_tvOrder.setText("좋아요 ");
                m_currentPage[m_nConnectType] = 1;
                m_nMaxPage[m_nConnectType] = 1;
                if (m_nConnectType == CONNECT_TYPE_REVIEW_LIST) {
                    arrList_rev.clear();
//                    arrList_rev.add(new ReviewInfo());
                }else{
                    arrList_exp.clear();
//                    arrList_exp.add(new ReviewInfo());
                }
                connectServer(m_nConnectType);
                break;
            case R.id.imv_write:
                if (m_nConnectType == 0) {
                    Intent intent = new Intent(getActivity(), ReviewWriteActivity.class);
                    m_MainAct.startActivityForResult(intent, 600);
                } else {
                    Intent intent = new Intent(getActivity(), ExpWriteActivity.class);
                    intent.putExtra("review_type", 1);
                    m_MainAct.startActivityForResult(intent, 600);
                }
                break;
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////
            case R.id.tv_confirm:
                m_MainAct.showProgress();
                m_MainAct.m_LockView=true;
                m_currentPage[m_nConnectType] = 1;
                m_nMaxPage[m_nConnectType] = 1;

                if(!notyet){
                    notyet = true;
                    m_tvConfirm.setText("미승인\n리뷰 보기\nON");
                    m_tvConfirm.setTextColor(Color.parseColor("#FFFFFF"));
                }else {
                    notyet = false;
                    m_tvConfirm.setText("미승인\n리뷰 보기\nOFF");
                    m_tvConfirm.setTextColor(Color.parseColor("#000000"));
                }

                if (m_nConnectType == CONNECT_TYPE_REVIEW_LIST) {
                    arrList_rev.clear();
//                    arrList_rev.add(new ReviewInfo());
                }else{
                    arrList_exp.clear();
//                    arrList_exp.add(new ReviewInfo());
                }
                connectServer(m_nConnectType);
                break;
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        }
    }

    public void deleteItem() {
        m_MainAct.showProgress();
        m_MainAct.m_LockView=true;
        m_currentPage[m_nConnectType] = 1;
        m_nMaxPage[m_nConnectType] = 1;
        if (m_nConnectType == CONNECT_TYPE_REVIEW_LIST) {
            arrList_rev.clear();
//            arrList_rev.add(new ReviewInfo());
        }else{
            arrList_exp.clear();
//            arrList_exp.add(new ReviewInfo());
        }
        connectServer(m_nConnectType);
    }

    int m_nSeletedPos = -1;
    public void updateReviewItem(ReviewInfo m_info) {
        if (m_nConnectType == CONNECT_TYPE_REVIEW_LIST) {
            arrList_rev.set(m_nSeletedPos, m_info);
            m_review_adapter.notifyDataSetChanged();
        } else if (m_nConnectType == CONNECT_TYPE_REVIEW_EXP) {
            arrList_exp.set(m_nSeletedPos, m_info);
            m_exp_review_adapter.notifyDataSetChanged();
        }
    }

    public void updateUIafterWriting() {
        m_MainAct.showProgress();
        m_MainAct.m_LockView=true;
        m_order[m_nConnectType] = "regdate";
        m_category = -1;
        if (m_nConnectType == CONNECT_TYPE_REVIEW_LIST) {
            m_tvCategory.setText("리뷰검색");
        } else if (m_nConnectType == CONNECT_TYPE_REVIEW_EXP) {
            m_tvCategory.setText("카테고리");
        }
        m_currentPage[m_nConnectType] = 1;
        m_nMaxPage[m_nConnectType] = 1;
        if (m_nConnectType == CONNECT_TYPE_REVIEW_LIST) {
            arrList_rev.clear();
//            arrList_rev.add(new ReviewInfo());
        }else{
            arrList_exp.clear();
//            arrList_exp.add(new ReviewInfo());
        }
        connectServer(m_nConnectType);
    }

    public void gotoReviewtabDetail(int pos) {
        m_nSeletedPos = pos;
        if (m_nConnectType == CONNECT_TYPE_REVIEW_LIST) {
            ReviewInfo info = arrList_rev.get(pos);
            if(!info._mb_id.equals(UserManager.getInstance().member_id))
                info.view_cnt++;

            Intent intent = new Intent(getActivity(), DetailReviewActivity.class);
            intent.putExtra("info", info);
            m_MainAct.startActivityForResult(intent, 6011);
        } else if (m_nConnectType == CONNECT_TYPE_REVIEW_EXP) {
            ReviewInfo info = arrList_exp.get(pos);
            if(!info._mb_id.equals(UserManager.getInstance().member_id))
                info.view_cnt++;

            Intent intent = new Intent(getActivity(), DetailExpActivity.class);
            intent.putExtra("info", info);
            intent.putExtra("exptitle", info.title);
            m_MainAct.startActivityForResult(intent, 6012);
        }
    }

    public void setReviewDetailCategory(int[] pos) {
        m_MainAct.showProgress();
        m_MainAct.m_LockView=true;
        m_category1 = pos;
        isSetCat = true;

        m_order[m_nConnectType] = "match";
        m_tvOrder.setText("필터기준");
        m_currentPage[m_nConnectType] = 1;
        m_nMaxPage[m_nConnectType] = 1;
        arrList_rev.clear();
//        arrList_rev.add(new ReviewInfo());
        connectServer(m_nConnectType);
    }

    public void setReviewCategory(int pos) {
        m_MainAct.showProgress();
        m_MainAct.m_LockView=true;
        //이곳에 오는 경우는 노하우 공유 필터 선택을 완료했을 때 뿐이다. by 동현
        m_category = pos;
        isSetCat = true;
        String[] arr;
        arr = getResources().getStringArray(R.array.exp_category);
        m_tvCategory.setText(arr[pos]);

        m_currentPage[m_nConnectType] = 1;
        m_nMaxPage[m_nConnectType] = 1;
        arrList_exp.clear();
//        arrList_exp.add(new ReviewInfo());
        connectServer(m_nConnectType);
    }

    private void connectServer(int strAct) {
        lastServerConBySecond[m_nConnectType] = System.currentTimeMillis();

        String[] paramFields = new String[]{Net.NET_POST_FIELD_ACT, Net.NET_POST_FIELD_REQUEST};
        String[] paramValues = null;

        // 파라메터 입력
        JSONObject w_objJSonData = new JSONObject();
        try {
            w_objJSonData.put(Net.NET_VALUE_MEMBER_ID, UserManager.getInstance().member_id);
            if (strAct == CONNECT_TYPE_REVIEW_LIST) {
                if(!isSetCat){
                    for (int i = 0; i < m_category1.length; i++) m_category1[i]=0;

                    if(UserManager.getInstance().HealthBaseInfo.get(0).member_examinee == 0)
                        m_category1[0]=1;
                    if(UserManager.getInstance().HealthBaseInfo.get(0).member_pregnant == 0)
                        m_category1[1]=1;
                    if(UserManager.getInstance().HealthBaseInfo.get(0).member_lactating == 0)
                        m_category1[2]=1;
                    if(UserManager.getInstance().HealthBaseInfo.get(0).member_climacterium == 0)
                        m_category1[3]=1;
                    if(UserManager.getInstance().HealthBaseInfo.get(0).member_sex == 0)
                        m_category1[4]=1;
                    else
                        m_category1[5]=1;

                    GregorianCalendar cal = new GregorianCalendar();
                    int curY = cal.get(Calendar.YEAR);
                    int user_age = curY - Integer.parseInt((UserManager.getInstance().member_birth).substring(0, 4)) + 1;
                    if(user_age<4){
                        m_category1[6] = 1;
                    }else if(user_age<10){
                        m_category1[7] = 1;
                    }else if(user_age<20){
                        m_category1[8] = 1;
                    }else if(user_age<50){
                        m_category1[9] = 1;
                    }else{
                        m_category1[10] = 1;
                    }

                    if(UserManager.getInstance().HealthBaseInfo.get(0).member_interest_health.length()>0){
                        String[] arr = UserManager.getInstance().HealthBaseInfo.get(0).member_interest_health.split(",");
                        for (String strOne:arr)
                            m_category1[11 + Integer.parseInt(strOne)]=1;
                    }
                    //임시로 전체 보기를 초기로 하는 코드(m_category1이 null 인 경우, 즉 초기 진입 시) by 동현
                    for(int j=0;j<m_category1.length;j++) m_category1[j]=1;
                }
                w_objJSonData.put("f_no", UserManager.getInstance().member_no);
                w_objJSonData.put(Net.NET_VALUE_PAGE, m_currentPage[m_nConnectType]);
                w_objJSonData.put("order", m_order[m_nConnectType]);

                w_objJSonData.put("examinee", m_category1[0]);
                w_objJSonData.put("pregnant", m_category1[1]);
                w_objJSonData.put("lactating", m_category1[2]);
                w_objJSonData.put("climacterium", m_category1[3]);
                w_objJSonData.put("male", m_category1[4]);
                w_objJSonData.put("female", m_category1[5]);
                w_objJSonData.put("infants", m_category1[6]);
                w_objJSonData.put("young", m_category1[7]);
                w_objJSonData.put("age_10s", m_category1[8]);
                w_objJSonData.put("age_2040s", m_category1[9]);
                w_objJSonData.put("age_over_50s", m_category1[10]);
                if(notyet)
                    w_objJSonData.put("notyet", true);
                String tmp = "";
                for(int i=11; i < m_category1.length;i++) {
                    if(m_category1[i]==1)   tmp = tmp + String.valueOf(i-11 + 1) + ",";     //" + 1" 을 하는 이유는 서버에서의 카테고리는 1부터 이기때문에
                }
                if(tmp.length()!=0){
                    tmp = tmp.substring(0, tmp.length()-1);
                    w_objJSonData.put("category", tmp);
                }
                paramValues = new String[]{
                        Net.apis_GET_REVIEW_LIST,
                        w_objJSonData.toString()};
            }
            else if (strAct == CONNECT_TYPE_REVIEW_EXP) {
                w_objJSonData.put(Net.NET_VALUE_PAGE, m_currentPage[m_nConnectType]);
                w_objJSonData.put("order", m_order[m_nConnectType]);
                w_objJSonData.put("category", "" + (m_category==-1? -1:m_category + 1));
                if(notyet)
                    w_objJSonData.put("notyet", true);

                paramValues = new String[]{
                        Net.apis_GET_EXP_LIST,
                        w_objJSonData.toString()};
            }
        } catch (Exception e) {            e.printStackTrace();        }

        String netUrl = Net.URL_SERVER + Net.URL_SERVER_API;
        HttpRequester.getInstance().init(m_MainAct, this, handler, netUrl,
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
        try {
            HttpRequester.getInstance().stopNetThread();
            int resultCode = HttpRequester.getInstance().getResultCode();
            String strMsg = HttpRequester.getInstance().getResultMsg();

            m_nResultType = _jResult.getInt(Net.NET_VALUE_CODE);
            JSONObject obj = _jResult.getJSONObject(Net.NET_VALUE_RESULT);

            m_nMaxPage[m_nConnectType] = obj.getInt(Net.NET_VALUE_TOTAL_PAGE);

            if (resultCode == Net.CONNECTION_SUCCSES) {// 성공
                if (m_nResultType == 200) {
                    JSONArray arr_rev = obj.getJSONArray("review");
                    ReviewInfo m_r_info;

                    if (m_nConnectType == CONNECT_TYPE_REVIEW_LIST) {
                        if(arrList_rev.size()==0)
                            arrList_rev.add(new ReviewInfo());
                        for (int i = 0; i < arr_rev.length(); i++) {
                            JSONObject obj_rev = arr_rev.getJSONObject(i);
                            m_r_info = new NetUtil().parseReview(obj_rev, true);
                            arrList_rev.add(m_r_info);
                        }
                        m_review_adapter.notifyDataSetChanged();
                    }
                    else if (m_nConnectType == CONNECT_TYPE_REVIEW_EXP) {
                        if(arrList_exp.size()==0)
                            arrList_exp.add(new ReviewInfo());
                        for (int i = 0; i < arr_rev.length(); i++) {
                            JSONObject obj_rev = arr_rev.getJSONObject(i);
                            m_r_info = new NetUtil().parseReview(obj_rev, false);
                            arrList_exp.add(m_r_info);
                        }
                        m_exp_review_adapter.notifyDataSetChanged();
                    }
                }
            } else {
                if (!"".equals(strMsg))
                    Toast.makeText(m_MainAct, strMsg, Toast.LENGTH_SHORT).show();
            }
            m_MainAct.closeProgress();
            m_MainAct.m_LockView = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    JSONObject _jResult;

    @Override
    public void onParseJSon(JSONObject j_source) {
        _jResult = j_source;
    }
}