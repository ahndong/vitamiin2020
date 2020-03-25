package app.vitamiin.com.setting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import app.vitamiin.com.Adapter.ReviewRecycleAdapter;
import app.vitamiin.com.Adapter.ReviewExperRecycleAdapter;
import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.Model.ReviewInfo;
import app.vitamiin.com.R;
import app.vitamiin.com.common.CircleImageView;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.common.SelectPhotoManager;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.common.Util;
import app.vitamiin.com.home.DetailReviewActivity;
import app.vitamiin.com.home.DetailExpActivity;
import app.vitamiin.com.home.ExpWriteActivity;
import app.vitamiin.com.home.MainActivity;
import app.vitamiin.com.home.ReviewWriteActivity;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;
import app.vitamiin.com.login.PhotoSelectDialog;

public class MyPageActivity extends BaseActivity implements View.OnClickListener, OnParseJSonListener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener {
    Context mContext = this;
    String mb_id = "";

    Boolean[] m_isMypageInfoLoaded = {false, false};
    int m_nResultType = 200;
    int m_currentPage = 1;
    int m_nMaxPage = 1;
    boolean m_bLockListView = false;

    int m_nFallowCnt = 0, m_nFallowingCnt = 0, m_nLikeCnt = 0, m_nReviewCnt = 0, m_nExperCnt = 0;
    int m_nFallowUser = 0;

    int CONNECT_TYPE_REVIEW_LIST = 0;
    int CONNECT_TYPE_EXP_REVIEW_LIST = 2;
    int apis_CHANGE_PHOTO = 3;
    int CONNECT_TYPE_FALLOW_USER = 4;

    int m_nConnectType = CONNECT_TYPE_REVIEW_LIST;

    PhotoSelectDialog m_selectPhotoDlg = null;

    LinearLayoutManager mLayoutManager;
    RecyclerView m_rcvReview;
    private ArrayList<ReviewInfo> arrList1 = new ArrayList<>();
    private ArrayList<ReviewInfo> arrList2 = new ArrayList<>();
    ReviewRecycleAdapter m_all_adapter;
    ReviewExperRecycleAdapter m_exp_adapter;

    View.OnClickListener m_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.imv_back:
                    intent = new Intent();
                    intent.putExtra("f_type", getIntent().getIntExtra("f_type", 1));
                    intent.putExtra("fallow_user", m_nFallowUser);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case R.id.tv_fallow_user:
                    connectServer(CONNECT_TYPE_FALLOW_USER);
                    break;
                case R.id.imv_family:
                    intent = new Intent(getBaseContext(), ModifyActivity.class);
                    startActivity(intent);
                    break;
                case R.id.imv_my:
                    intent = new Intent(getBaseContext(), SettingActivity.class);
                    startActivity(intent);
                    break;
                case R.id.imv_profile:
                case R.id.imv_dummy:
                case R.id.imv_edit:
                    openPhotoDialog();
                    break;
                case R.id.lly_fallowing:
                    intent = new Intent(getBaseContext(), FollowingAndFollowerActivity.class);
                    intent.putExtra("f_type", 1);
                    intent.putExtra("f_id", mb_id);
                    startActivityForResult(intent, 171);
                    break;
                case R.id.lly_fallow:
                    intent = new Intent(getBaseContext(), FollowingAndFollowerActivity.class);
                    intent.putExtra("f_type", 2);
                    intent.putExtra("f_id", mb_id);
                    startActivityForResult(intent, 172);
                    break;
                case R.id.lly_like:
                    intent = new Intent(getBaseContext(), LikeActivity.class);
                    intent.putExtra("f_id", mb_id);
                    startActivityForResult(intent, 300);
                    break;
                case R.id.tv_review:
                    switchReviewTab();
                    break;
                case R.id.tv_exper:
                    switchExpTab();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mb_id = getIntent().getStringExtra("mb_id");
        setContentView(R.layout.activity_my_page);
        initView();
        connectServer(CONNECT_TYPE_REVIEW_LIST);
    }

    private void initView() {
        m_rcvReview = (RecyclerView) findViewById(R.id.lsv_review);
        mLayoutManager = new LinearLayoutManager(this);
        m_rcvReview.setLayoutManager(mLayoutManager);
        m_rcvReview.setOnScrollListener(m_hidingScrollListener);

        m_all_adapter = new ReviewRecycleAdapter(this, arrList1, true, m_listener);
        m_exp_adapter = new ReviewExperRecycleAdapter(this, arrList2, m_listener);

        arrList1.clear();
        arrList2.clear();
        ReviewInfo info = new ReviewInfo();
        info.price=0;
        arrList1.add(info);
        arrList2.add(info);

        m_rcvReview.setAdapter(m_all_adapter);

        findViewById(R.id.imv_write).setOnClickListener(this);
    }

    public void afterServerInit(){  //arrListX.get(0)의 값 변경, arrListX.get(0).price=1 구문, notifyDataSetChanged()까지 포함한 것임. by 동현
        ReviewInfo info = new ReviewInfo();
        info.price=1;               //이 afterServerInit 자체가 헤더인포를 갱신하기 위해 있는 것이므로 당연히. by 동현

        info.comment_cnt = m_nFallowingCnt;
        info.like_cnt = m_nFallowCnt;
        info.view_cnt = m_nLikeCnt;
        info.period = m_nReviewCnt;
        info.person = m_nExperCnt;
        info._mb_nick = getIntent().getStringExtra("mb_nick");
        if (UserManager.getInstance().member_id.equals(mb_id)){  //본인일 경우
            info._mb_sex = 1;
        }else{                                                   //본인이 아닌 경우
            info._mb_sex = 0;

            if (m_nFallowUser == 1)            info.fallow_user = 1;
            else                               info.fallow_user = 0;

            info._good_photo_urls = new ArrayList<>();
            info._good_photo_urls.add(0,getIntent().getStringExtra("mb_photo"));
        }
        arrList1.set(0, info);
        arrList2.set(0, info);
        m_rcvReview.getAdapter().notifyDataSetChanged();
        //SetOnClickListener는 이곳에서 할 필요가 없다. all_adapter 내부에서 해주며, OnClick(View v)은 adapter 생성자에서 넘겨준다.  by 동현
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
        m_hidingScrollListener.controlsVisible = false;
        m_hidingScrollListener.scrolledDistance = 0;

        m_rcvReview.setPadding(0,0,0,0);
    }

    private void showViews() {
        m_hidingScrollListener.controlsVisible = true;
        m_hidingScrollListener.scrolledDistance = 0;
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

            if(pastVisiblesItems>2)
                viewPositionTextView();
            else
                hidePositionTextView();

            if (pastVisiblesItems >= count && totalItemCount != 1
                    && m_currentPage < m_nMaxPage && !m_bLockListView) {
                m_currentPage = m_currentPage + 1;
                connectServer(m_nConnectType);
            }
        }
        public abstract void onHide();
        public abstract void onShow();

    }

    public void openPhotoDialog(){
        if (m_selectPhotoDlg == null)
            m_selectPhotoDlg = new PhotoSelectDialog(this, 2);
        m_selectPhotoDlg.show();
    }

    public void switchReviewTab() {
        m_nConnectType = CONNECT_TYPE_REVIEW_LIST;
        m_currentPage = 1;
        m_nMaxPage = 1;
        m_rcvReview.setAdapter(m_all_adapter);
        if(!m_isMypageInfoLoaded[0]) {
            connectServer(CONNECT_TYPE_REVIEW_LIST);
        }
        else{       //header만 있는 것이 empty 이므로 0이 아니라 1임 by 동현
            if(arrList1.size()==1 && UserManager.getInstance().member_id.equals(mb_id)) {
                findViewById(R.id.lly_why_dont_you).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.tv_why_dont_you)).setText("작성하신 리뷰가 하나도 없네요. \n 첫 리뷰를 작성해주세요");
            }
            else {
                findViewById(R.id.lly_why_dont_you).setVisibility(View.GONE);
            }

            afterServerInit();
        }
    }

    public void switchExpTab() {
        m_nConnectType = CONNECT_TYPE_EXP_REVIEW_LIST;
        m_currentPage = 1;
        m_nMaxPage = 1;
        m_rcvReview.setAdapter(m_exp_adapter);
        if(!m_isMypageInfoLoaded[1]) {
            connectServer(CONNECT_TYPE_EXP_REVIEW_LIST);
        }
        else{       //header만 있는 것이 empty 이므로 0이 아니라 1임 by 동현
            if(arrList2.size()==1 && UserManager.getInstance().member_id.equals(mb_id)) {
                findViewById(R.id.lly_why_dont_you).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.tv_why_dont_you)).setText("작성하신 노하우 공유 게시글이 하나도 없네요. \n 새 글을 작성해주세요~!");
            }
            else{
                findViewById(R.id.lly_why_dont_you).setVisibility(View.GONE);
            }

            afterServerInit();
        }
    }
    public void gotoMyPageActivityDetail(int pos) {
        m_nSeletedPos = pos;
        if (m_nConnectType == CONNECT_TYPE_REVIEW_LIST) {
            ReviewInfo info = arrList1.get(pos);

            Intent intent = new Intent(this, DetailReviewActivity.class);
            intent.putExtra("info", info);
            startActivityForResult(intent, 400);
        } else if (m_nConnectType == CONNECT_TYPE_EXP_REVIEW_LIST) {
            ReviewInfo info = arrList2.get(pos);

            Intent intent = new Intent(this, DetailExpActivity.class);
            intent.putExtra("info", info);
            intent.putExtra("exptitle", info.title);
            startActivityForResult(intent, 400);
        }
    }

    private void connectServer(int strAct) {
        m_bLockListView = true;
        m_nConnectType = strAct;

        showProgress();
        String[] paramFields = new String[]{Net.NET_POST_FIELD_ACT,
                Net.NET_POST_FIELD_REQUEST};
        String[] paramFileNames = null;
        String[] paramValues = null;

        // 파라메터 입력
        JSONObject w_objJSonData = new JSONObject();
        try {
            w_objJSonData.put(Net.NET_VALUE_MEMBER_ID, UserManager.getInstance().member_id);
            if (m_nConnectType == CONNECT_TYPE_REVIEW_LIST) {
                w_objJSonData.put("user_id", UserManager.getInstance().member_id);
                w_objJSonData.put(Net.NET_VALUE_PAGE, m_currentPage);
                w_objJSonData.put("order", "regdate");
                w_objJSonData.put("category", "-1");
                w_objJSonData.put(Net.NET_VALUE_MEMBER_ID, mb_id);  //내가 아닐 수 있는, 이 화면 주인공의 id by 동현

                paramValues = new String[]{
                        Net.apis_GET_MY_REVIEW_LIST,
                        w_objJSonData.toString()};
            }
            else if (m_nConnectType == CONNECT_TYPE_EXP_REVIEW_LIST) {
                w_objJSonData.put("user_id", UserManager.getInstance().member_id);
                w_objJSonData.put(Net.NET_VALUE_PAGE, m_currentPage);
                w_objJSonData.put("order", "regdate");
                w_objJSonData.put("category", "-1");
                w_objJSonData.put(Net.NET_VALUE_MEMBER_ID, mb_id);  //내가 아닐 수 있는, 이 화면 주인공의 id by 동현

                paramValues = new String[]{
                        Net.apis_GET_MY_EXP_LIST,
                        w_objJSonData.toString()};
            }
            else if (m_nConnectType == apis_CHANGE_PHOTO) {

                if (m_uploadImageResID != -1) {//resID가 -1이 아닌값으로 세팅이 되어 있다면 이미지 파일은 다 null로 하고 resID를 올린다.
                    UserManager.getInstance().arr_profile_photo_resID.set(0, m_uploadImageResID);
                    UserManager.getInstance().arr_profile_photo_URL.set(0, null);
                    UserManager.getInstance().arr_profile_photo_file.set(0, null);
                    UserManager.getInstance().arr_profile_photo_bitmap.set(0, null);
                }
                else {
                    if (m_uploadImageFile != null) {
                        UserManager.getInstance().arr_profile_photo_resID.set(0, -1);
                        UserManager.getInstance().arr_profile_photo_file.set(0,m_uploadImageFile);
                        UserManager.getInstance().arr_profile_photo_bitmap.set(0, m_uploadBitmap);
                    } else {
                        UserManager.getInstance().arr_profile_photo_resID.set(0, new Util().getAutoImageResID(0));
                        UserManager.getInstance().arr_profile_photo_URL.set(0, null);
                        UserManager.getInstance().arr_profile_photo_file.set(0, null);
                        UserManager.getInstance().arr_profile_photo_bitmap.set(0, null);
                    }
                }
                w_objJSonData.put(Net.NET_VALUE_MEMBER_NO, UserManager.getInstance().member_no);
                w_objJSonData.put("f_res_id", "" + m_uploadImageResID);

                paramFileNames = new String[1];
                for (int i = 0; i < 1; i++) {
                    if (m_uploadImageFile != null && m_uploadImageFile.exists()) {
                        paramFileNames[i] = m_uploadImageFile.getPath();
                    } else {
                        paramFileNames[i] = "";
                    }
                }
                paramValues = new String[]{
                        Net.apis_CHANGE_PHOTO,
                        w_objJSonData.toString()};
            } else if (m_nConnectType == CONNECT_TYPE_FALLOW_USER) {
                w_objJSonData.put("fallow_id", mb_id);
                w_objJSonData.put("fallow", "" + m_nFallowUser);

                paramValues = new String[]{
                        Net.POST_FIELD_ACT_FALLOW_USER,
                        w_objJSonData.toString()};
            }
        } catch (Exception e) {e.printStackTrace();}

        String netUrl = Net.URL_SERVER + Net.URL_SERVER_API;
        if (m_nConnectType == apis_CHANGE_PHOTO && m_uploadImageFile != null && m_uploadImageFile.exists()) {
            HttpRequester.getInstance().init(this, this, handler, netUrl,
                    paramFields, paramValues, paramFileNames);
        } else {
            HttpRequester.getInstance().init(this, this, handler, netUrl,
                    paramFields, paramValues, false);
        }
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
        // 성공
        if (resultCode == Net.CONNECTION_SUCCSES) {
            if (m_nResultType == 200) {
                if (m_nConnectType == CONNECT_TYPE_REVIEW_LIST) {
                    ((TextView)findViewById(R.id.tv_why_dont_you)).setText("작성하신 리뷰가 하나도 없네요. \n 첫 리뷰를 작성해주세요");
                    m_isMypageInfoLoaded[0]=true;
                    afterServerInit();
                }
                else if (m_nConnectType == CONNECT_TYPE_EXP_REVIEW_LIST) {
                    ((TextView)findViewById(R.id.tv_why_dont_you)).setText("작성하신 노하우 공유 게시글이 하나도 없네요. \n 새 글을 작성해주세요~!");
                    m_isMypageInfoLoaded[1]=true;
                    afterServerInit();
                }
                else if (m_nConnectType == apis_CHANGE_PHOTO) {
                    arrList1.get(0).price = 1;
                    arrList2.get(0).price = 1;

                    m_rcvReview.getAdapter().notifyDataSetChanged();
                }
                else if (m_nConnectType == CONNECT_TYPE_FALLOW_USER) {
                    arrList1.get(0).fallow_user = m_nFallowUser;
                    arrList2.get(0).fallow_user = m_nFallowUser;
                    arrList1.get(0).price=1;
                    arrList2.get(0).price=1;
                    m_rcvReview.getAdapter().notifyDataSetChanged();
                    return;
                }
            }
        } else {
            if (!"".equals(strMsg)) {Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();}
        }
        m_bLockListView = false;
    }

    JSONObject _jResult;

    public void parseJSON() {
        try {
            m_nResultType = _jResult.getInt(Net.NET_VALUE_CODE); //
            JSONObject result = _jResult.getJSONObject(Net.NET_VALUE_RESULT);

            if (m_nConnectType == CONNECT_TYPE_REVIEW_LIST || m_nConnectType == CONNECT_TYPE_EXP_REVIEW_LIST) {
                m_nFallowingCnt = result.getInt("fallowing_cnt");
                m_nFallowCnt = result.getInt("fallow_cnt");
                m_nLikeCnt = result.getInt("like_cnt");
                m_nReviewCnt = result.getInt("review_cnt");
                m_nExperCnt = result.getInt("exper_cnt");
                m_nFallowUser = result.getInt("fallow_user");

                m_nMaxPage = result.getInt(Net.NET_VALUE_TOTAL_PAGE);

                JSONArray arr = result.getJSONArray("review");
                if (arr.length() == 0 && UserManager.getInstance().member_id.equals(mb_id)) {
                    findViewById(R.id.lly_why_dont_you).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.lly_why_dont_you).setVisibility(View.GONE);
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj_rev = arr.getJSONObject(i);
                        ReviewInfo m_r_info;
                        if (m_rcvReview.getAdapter().equals(m_all_adapter)) {
                            m_r_info = new NetUtil().parseReview(obj_rev, true);
                            arrList1.add(m_r_info);
                        } else {
                            m_r_info = new NetUtil().parseReview(obj_rev, false);
                            arrList2.add(m_r_info);
                        }
                    }
                }
            }
            else if (m_nConnectType == apis_CHANGE_PHOTO) {
                UserManager.getInstance().arr_profile_photo_URL.set(0, result.getString("photoURL"));
                for(ReviewInfo eachInfo : arrList1) eachInfo.f_photo=result.getString("photoURL");
                for(ReviewInfo eachInfo : arrList2) eachInfo.f_photo=result.getString("photoURL");
            }
            else if (m_nConnectType == CONNECT_TYPE_FALLOW_USER) {   //MaxPage나 생일 정보가 필요없는 fallowuser는 먼저 해결하고 return 보내버린다.by e동현
                m_nFallowUser = result.getInt("fallow");
            }
        } catch (JSONException e) {e.printStackTrace();}
    }

    @Override
    public void onParseJSon(JSONObject j_source) {
        _jResult = j_source;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SelectPhotoManager.CROP_FROM_CAMERA:
            case SelectPhotoManager.PICK_FROM_CAMERA:
            case SelectPhotoManager.PICK_FROM_FILE:
                if (m_selectPhotoDlg != null) {
                    m_selectPhotoDlg.onActivityResult(requestCode, resultCode, data);
                }
                break;
        }

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 171:
                    m_nFallowingCnt = data.getIntExtra("f_count", 0);
                    arrList1.get(0).comment_cnt=m_nFallowingCnt;
                    arrList2.get(0).comment_cnt=m_nFallowingCnt;
                    arrList1.get(0).price=1;
                    arrList2.get(0).price=1;
                    afterServerInit();
//                    m_rcvReview.getAdapter().notifyDataSetChanged();
                    break;
                case 300:
                    m_nLikeCnt = data.getIntExtra("like_cnt", 0);
                    arrList1.get(0).view_cnt=m_nLikeCnt;
                    arrList2.get(0).view_cnt=m_nLikeCnt;
                    arrList1.get(0).price=1;
                    arrList2.get(0).price=1;
                    afterServerInit();
//                    m_rcvReview.getAdapter().notifyDataSetChanged();
                    break;
                case 400:
                    if (m_nSeletedPos == -1)
                        return;
                    if (data.getBooleanExtra("delete", false)) {
                        if (m_nConnectType == CONNECT_TYPE_REVIEW_LIST) {
                            m_nReviewCnt--;

                            arrList1.remove(m_nSeletedPos);
                            arrList1.get(0).period--;
                            //요소 삭제이므로 HeaderInfo 재작업 할 필요가 있다. 리뷰 또는 노하우 공유의 글 갯수를 -1해야 함 by 동현
                            arrList1.get(0).price=1;
                            m_rcvReview.getAdapter().notifyDataSetChanged();
                            if(arrList1.size()==1)
                                findViewById(R.id.lly_why_dont_you).setVisibility(View.VISIBLE);
                        }
                        else if (m_nConnectType == CONNECT_TYPE_EXP_REVIEW_LIST) {
                            m_nExperCnt--;

                            arrList2.remove(m_nSeletedPos);
                            arrList2.get(0).person--;
                            //요소 삭제이므로 HeaderInfo 재작업 할 필요가 있다. 리뷰 또는 노하우 공유의 글 갯수를 -1해야 함 by 동현
                            arrList2.get(0).price=1;
                            m_rcvReview.getAdapter().notifyDataSetChanged();
                            if(arrList2.size()==1)
                                findViewById(R.id.lly_why_dont_you).setVisibility(View.VISIBLE);
                        }
                    } else {
                        int view_cnt = data.getIntExtra("view_cnt", 0);
                        int like_cnt = data.getIntExtra("like_cnt", 0);
                        int comment_cnt = data.getIntExtra("comment_cnt", 0);
                        String content = data.getStringExtra("content");
                        double rate = data.getDoubleExtra("rate", 0.0);
                        if (m_nConnectType == CONNECT_TYPE_REVIEW_LIST) {
                            arrList1.get(m_nSeletedPos).view_cnt = view_cnt;
                            arrList1.get(m_nSeletedPos).like_cnt = like_cnt;
                            arrList1.get(m_nSeletedPos).comment_cnt = comment_cnt;
                            arrList1.get(m_nSeletedPos).rate = rate;
                            arrList1.get(m_nSeletedPos).content = content;
                        }
                        else if (m_nConnectType == CONNECT_TYPE_EXP_REVIEW_LIST) {
                            arrList2.get(m_nSeletedPos).view_cnt = view_cnt;
                            arrList2.get(m_nSeletedPos).like_cnt = like_cnt;
                            arrList2.get(m_nSeletedPos).comment_cnt = comment_cnt;
                        }//헤더인포를 바꿀 필요가 없으므로 arrListX.get(0).price=1을 하지 않고 바로 notifyDataSetChanged();함 by 동현
                        m_rcvReview.getAdapter().notifyDataSetChanged();
                    }
                    break;
                case 600:
                    ReviewInfo m_r_info = (ReviewInfo)data.getSerializableExtra("info");
                    if (m_nConnectType == CONNECT_TYPE_REVIEW_LIST) {
                        m_nReviewCnt++;
                        arrList1.get(0).period++;
                        arrList1.add(m_r_info);
                        arrList1.get(0).price=1;
                    }
                    else if (m_nConnectType == CONNECT_TYPE_EXP_REVIEW_LIST) {
                        m_nExperCnt++;
                        arrList2.get(0).person++;
                        arrList2.add(m_r_info);
                        arrList2.get(0).price=1;
                    }//헤더인포를 바꿀 필요가 없으므로 arrListX.get(0).price=1을 하지 않고 바로 notifyDataSetChanged();함 by 동현
//                    afterServerInit();
                    m_rcvReview.getAdapter().notifyDataSetChanged();
                    findViewById(R.id.lly_why_dont_you).setVisibility(View.GONE);
                    break;
            }
        }
    }

    private Bitmap m_uploadBitmap = null;
    private File m_uploadImageFile = null;
    private int m_uploadImageResID = -1;

    public void setSelectedImage(Bitmap b, File f) {
        if (b != null && f != null) {
            //아래 한 줄은 이미지를 선택했기 때문에 ResID를 -1로 unsetting 해주는 것이다. by 동현
            m_uploadImageResID = -1;
            m_uploadBitmap = b;
            m_uploadImageFile = f;
            Glide.with(mContext).load(m_uploadBitmap).into((CircleImageView) findViewById(R.id.imv_profile));
            m_uploadImageResID = -1;
            connectServer(apis_CHANGE_PHOTO);
        }
    }

    public void setSelectedImage(int resID) {
        m_uploadImageResID = resID;
        m_uploadBitmap = null;
        m_uploadImageFile = null;

        int resArr[] = {R.drawable.ic_male_1, R.drawable.ic_male_2, R.drawable.ic_male_3, R.drawable.ic_male_4,
                R.drawable.ic_female_1, R.drawable.ic_female_2, R.drawable.ic_female_3, R.drawable.ic_female_4};
        Glide.with(mContext).load(resArr[resID]).into((CircleImageView) findViewById(R.id.imv_profile));

        connectServer(apis_CHANGE_PHOTO);
    }

    int m_nSeletedPos = -1;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        m_nSeletedPos = position;
        if(m_rcvReview.getAdapter().equals(m_all_adapter)){
            ReviewInfo info = arrList1.get(position);
            Intent intent = new Intent(this, DetailReviewActivity.class);
            intent.putExtra("info", info);
            startActivityForResult(intent, 400);
        } else{
            ReviewInfo info = arrList2.get(position);

            Intent intent = new Intent(this, DetailExpActivity.class);
            intent.putExtra("info", info);
            startActivityForResult(intent, 400);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int count = totalItemCount - visibleItemCount;
        if (firstVisibleItem >= count && totalItemCount != 1
                && m_currentPage < m_nMaxPage && !m_bLockListView) {
            m_currentPage = m_currentPage + 1;
            connectServer(m_nConnectType);
        }
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Net.THREAD_REQUEST_END) {processForNetEnd();}
        }
    };

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.imv_write:
                if (m_nConnectType == CONNECT_TYPE_EXP_REVIEW_LIST) {
                    intent = new Intent(getBaseContext(), ExpWriteActivity.class);
                    intent.putExtra("review_type", 1);
                    intent.putExtra("fromMypage", true);
                    startActivityForResult(intent, 600);
                } else if (m_nConnectType == CONNECT_TYPE_REVIEW_LIST) {
                    intent = new Intent(getBaseContext(), ReviewWriteActivity.class);
                    intent.putExtra("fromMypage", true);
                    startActivityForResult(intent, 600);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("f_type", getIntent().getIntExtra("f_type", 1));
        intent.putExtra("fallow_user", m_nFallowUser);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void viewPositionTextView(){
//        _tvPosition.animate().alpha(1f);
//        findViewById(R.id.imv_position_arrow).setVisibility(View.VISIBLE);
//        findViewById(R.id.imv_position_arrow).animate().alpha(0.75f);
    }

    public void hidePositionTextView(){
//        m_tvPosition.animate().alpha(0f);
//        findViewById(R.id.imv_position_arrow).animate().alpha(0f);
    }
}
