package app.vitamiin.com.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.kakao.kakaolink.AppActionBuilder;
import com.kakao.kakaolink.AppActionInfoBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.Model.FoodInfo;
import app.vitamiin.com.Model.FoodInfoDeux;
import app.vitamiin.com.R;
import app.vitamiin.com.common.FoodImageSlidingAdapter;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;
import app.vitamiin.com.login.LoginActivity;

public class DetailFoodActivity extends BaseActivity implements View.OnClickListener, OnParseJSonListener {
    Context mContext = this;
    NetUtil mNetUtil;
    NetUtil.connectAndgetServer mNetUtilConnetServer;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    boolean isloggedIn = false;
    boolean is_m_llyShare_visible = false;

    boolean m_isModified = false;
    int m_nConnectType;
    int m_nResultType = 200;
    FoodInfoDeux mf_info;
    int LeftOrRight;
    int Id, V_cnt, L_cnt, Like, NoDetails;
    String Name, Img;

    TextView m_tvTitle;
    ImageView m_imvLike, m_imvShare, m_imvKakao, m_imvFacebook;
    LinearLayout m_llyType, m_llyDetail, m_llyShare;

    ViewPager m_uiVwpShopPhoto;
    ViewPager.OnPageChangeListener m_listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            ((TextView)findViewById(R.id.imv_number1)).setText("- " + String.valueOf(position+1) + " o");
            hideShareLly();
        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };
    CirclePageIndicator mHelpIndicator;
    private ArrayList<String> m_arrPhotoName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_food);
        mNetUtil = new NetUtil();
        mNetUtilConnetServer = mNetUtil.new connectAndgetServer(this);
        Intent intent = getIntent();

        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            try{
                if(uri.toString().contains("fb.me")){       //페북에서 옴//페북에서 올때 _id찾기
                    Id = Integer.parseInt((uri.toString().split("_id%3D"))[uri.toString().split("_id%3D").length-1]);
                }else {
                    Id = Integer.valueOf(uri.getQueryParameter("_id"));
                }
                LeftOrRight = 0;
            } catch (Exception e) {            e.printStackTrace();        }
            SharedPreferences pref = getSharedPreferences("Vitamiin", 0);
            if(UserManager.getInstance().member_type.equals("") && !pref.getString("type", "").equals("")){
                showProgress();
                m_nConnectType = NetUtil.apis_LOGIN;
                isloggedIn = mNetUtil.LoginInUtil(this, this, handler, pref.getString("type", ""),pref.getString("email", ""),pref.getString("id", ""),pref.getString("pass", ""));
            }else{
                showProgress();
                m_nConnectType = NetUtil.apis_GET_HEALTH_FOOD_BY_ID;
                mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, Id);
            }
        }else{
            mf_info = (FoodInfoDeux) getIntent().getSerializableExtra("info");
            LeftOrRight = getIntent().getIntExtra("LeftOrRight", 0);

            initView();
            updateInfo();
            showProgress();//아래는 성분별 항목 화면을 스킵하고 바로 내용 화면으로 들어가기 위한 진입용 코드임 by 동현
            m_nConnectType = NetUtil.apis_GET_HEALTH_FOOD_DETAIL;
            mNetUtilConnetServer.connectServer(mContext, handler, m_nConnectType, LeftOrRight==0 ? mf_info._id:mf_info._id2);
        }

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {Toast.makeText(mContext, "onSuccess", Toast.LENGTH_SHORT).show();}
            @Override
            public void onCancel() {Toast.makeText(mContext, "onCancel", Toast.LENGTH_SHORT).show();}
            @Override
            public void onError(FacebookException error) {Toast.makeText(mContext, "onError: " + error.toString(), Toast.LENGTH_SHORT).show();}
        });
    }

    private void initView() {
        findViewById(R.id.imv_back).setOnClickListener(this);
        findViewById(R.id.rly_bg).setOnClickListener(this);
        m_tvTitle = (TextView) findViewById(R.id.tv_title);

        m_uiVwpShopPhoto = (ViewPager) findViewById(R.id.vwp_shop_photo);
        m_uiVwpShopPhoto.setOnClickListener(this);
        mHelpIndicator = (CirclePageIndicator) findViewById(R.id.cpi_photo);
        mHelpIndicator.setOnPageChangeListener(m_listener);
//        m_llyDetail = (LinearLayout) findViewById(R.id.lly_detail);
//        m_llyType = (LinearLayout) findViewById(R.id.lly_type);
//        findViewById(R.id.lly_type1).setOnClickListener(this);
//        findViewById(R.id.lly_type2).setOnClickListener(this);
//        findViewById(R.id.lly_type3).setOnClickListener(this);
//        findViewById(R.id.lly_type4).setOnClickListener(this);
//        findViewById(R.id.lly_type5).setOnClickListener(this);
//        findViewById(R.id.lly_type6).setOnClickListener(this);

        m_imvLike = (ImageView) findViewById(R.id.imv_like);
        m_imvLike.setOnClickListener(this);
        m_imvShare = (ImageView) findViewById(R.id.imv_share);
        m_imvShare.setOnClickListener(this);

        m_llyShare = (LinearLayout) findViewById(R.id.lly_share);
        m_llyShare.setOnClickListener(this);
        m_imvKakao = (ImageView) findViewById(R.id.imv_kakao);
        m_imvKakao.setOnClickListener(this);
        m_imvFacebook = (ImageView) findViewById(R.id.imv_facebook);
        m_imvFacebook.setOnClickListener(this);

        if(LeftOrRight==0){
            Id = mf_info._id;
            Name = mf_info._name;
            Img = mf_info._imagePath;
            V_cnt = mf_info._view_cnt;
            L_cnt = mf_info._like_cnt;
            Like = mf_info.like_wiki;
        }else if(LeftOrRight==1){
            Id = mf_info._id2;
            Name = mf_info._name2;
            Img = mf_info._imagePath2;
            V_cnt = mf_info._view_cnt2;
            L_cnt = mf_info._like_cnt2;
            Like = mf_info.like_wiki2;
        }
        hideShareLly();
    }

    private void updateInfo() {
        m_tvTitle.setText(Name);
    }
    private void hideShareLly() {
        m_llyShare.animate().translationY(m_llyShare.getHeight()*3).setInterpolator(new DecelerateInterpolator(1));
        m_imvKakao.animate().translationY(m_imvKakao.getHeight()*3).setInterpolator(new DecelerateInterpolator(3));
        m_imvFacebook.animate().translationY(m_imvFacebook.getHeight()*3).setInterpolator(new DecelerateInterpolator(3));
        is_m_llyShare_visible = false;
        (findViewById(R.id.rly_bg)).setVisibility(View.GONE);
    }

    private void showShareLly() {
        m_llyShare.setVisibility(View.VISIBLE);
        m_llyShare.animate().translationY(0).setInterpolator(new DecelerateInterpolator(1));
        m_imvKakao.animate().translationY(0).setInterpolator(new DecelerateInterpolator(3));
        m_imvFacebook.animate().translationY(0).setInterpolator(new DecelerateInterpolator(3));
        is_m_llyShare_visible = true;
        (findViewById(R.id.rly_bg)).setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imv_back:
                Intent intent;
                if(UserManager.getInstance().member_type.equals("")) {
                    intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                }else {
                    if(isTaskRoot()){
                        intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        intent = new Intent();
                        if (m_isModified) {
                        }
                        intent.putExtra("view_cnt", V_cnt);
                        intent.putExtra("like_cnt", L_cnt);
                        if(!m_imvLike.isSelected())
                            intent.putExtra("unlike", true);

                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
                break;
            case R.id.rly_bg:
                hideShareLly();
                break;
            case R.id.imv_like:
                m_isModified = true;
                like();
                break;
            case R.id.imv_share:
                if(is_m_llyShare_visible)
                    hideShareLly();
                else
                    showShareLly();
                break;
            case R.id.vwp_shop_photo:
                hideShareLly();
                break;
            case R.id.imv_kakao:
                shareToKakaoTalk();
                hideShareLly();
                break;
            case R.id.imv_facebook:
                shareToFacebook();
                hideShareLly();
                break;
//            case R.id.lly_type1:
//                connectServer(1);
//                m_tvTitle.setText(m_p_info._name + " - 정의");
//                break;
//            case R.id.lly_type2:
//                connectServer(2);
//                m_tvTitle.setText(m_p_info._name + " - 효능");
//                break;
//            case R.id.lly_type3:
//                connectServer(3);
//                m_tvTitle.setText(m_p_info._name + " - 주의사항 및 부작용");
//                break;
//            case R.id.lly_type4:
//                connectServer(4);
//                m_tvTitle.setText(m_p_info._name + " - 권장량 및 식품");
//                break;
//            case R.id.lly_type5:
//                connectServer(5);
//                m_tvTitle.setText(m_p_info._name + " - 관련 연구");
//                break;
//            case R.id.lly_type6:
//                connectServer(6);
//                m_tvTitle.setText(m_p_info._name + " - 맛있게 먹기");
//                break;
        }
    }

    private void shareToFacebook() {
        String title = "[성분 정보] " + Name + " by. Vitamiin";

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            try {
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setImageUrl(Uri.parse(Net.URL_SERVER2 + Img))
                        .setContentTitle(title)
                        .setContentDescription("비타미인에서 제공하는 건강기능식품의 기능성 성분 정보입니다.")
                        .setContentUrl(Uri.parse("https://fb.me/1090625894376010?_id=" + String.valueOf(Id)))
//                        .sexxtContentUrl(Uri.parse("https://fb.me/1089876024450997?_id=" + LeftOrRight + Id))
//                        .setContentUrl(Uri.parse("https://fb.me/1089876024450997?_id=" + m_p_info._id))
//                        .setQuote("인용문 추가 테스트")
//                        .setShareHashtag(new ShareHashtag.Builder()
//                                .setHashtag("#ConnectTheWorld")
//                                .build())
                        .build();

                shareDialog.show(content);
            } catch (Exception e) {
                System.out.println("Exc2=" + e);
            }
        }
    }

    private void shareToKakaoTalk() {
        try {
            final Map<String, String> properties = new HashMap<String, String>();
            properties.put("_id", String.valueOf(Id));
            properties.put("_activity", "fd");

            final KakaoLink kakaoLink = KakaoLink.getKakaoLink(this);
            final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink
                    .createKakaoTalkLinkMessageBuilder();

            String title = "[성분 정보] " + Name + " by. Vitamiin";

            kakaoTalkLinkMessageBuilder.addText(title + "\n " + "비타미인에서 제공하는 건강기능식품의 기능성 성분 정보입니다.")
                    .addImage(Net.URL_SERVER2 + Img, 100, 100)
                    .addAppButton("앱으로 이동",
                            new AppActionBuilder()
                                    .addActionInfo(AppActionInfoBuilder
                                            .createAndroidActionInfoBuilder()
                                            .setExecuteParam(properties)
                                            .setMarketParam("referrer=kakaotalklink")
                                            .build())
                                    .addActionInfo(AppActionInfoBuilder
                                            .createiOSActionInfoBuilder()
                                            .setExecuteParam(properties)
                                            .build())
                                    .setUrl("http://vitamiin.co.kr")
                                    .build());
            kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, this);
        } catch (Exception e) {
            Toast.makeText(this, "공유 실패", Toast.LENGTH_SHORT).show();
        }
    }

    private void like() {
        m_nConnectType = NetUtil.apis_SET_LIKE_FOOD;
        mNetUtilConnetServer.connectServer(mContext, handler, m_nConnectType, new int[] {LeftOrRight==0 ? mf_info._id:mf_info._id2, Like});
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
        // 성공
        if (resultCode == Net.CONNECTION_SUCCSES) {
            if (m_nResultType == 200) {
                if (m_nConnectType == NetUtil.apis_LOGIN) {
                    showProgress();
                    m_nConnectType = NetUtil.apis_GET_HEALTH_FOOD_BY_ID;
                    mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, Id);
                }
                else if(m_nConnectType == NetUtil.apis_GET_HEALTH_FOOD_BY_ID){
                    initView();
                    updateInfo();
                    m_nConnectType = NetUtil.apis_GET_HEALTH_FOOD_DETAIL;
                    mNetUtilConnetServer.connectServer(mContext, handler, m_nConnectType, LeftOrRight==0 ? mf_info._id:mf_info._id2);
                }
                else if(m_nConnectType == NetUtil.apis_GET_HEALTH_FOOD_DETAIL){
                    m_imvLike.setSelected(Like == 1);
                    V_cnt++;
                }
                else if(m_nConnectType == NetUtil.apis_SET_LIKE_FOOD){//like 아이콘 selected하기
                    if (Like == 1) {
                        m_imvLike.setSelected(true);
                        L_cnt++;
                    } else {
                        m_imvLike.setSelected(false);
                        L_cnt--;
                    }
                }
            }
        } else {
            if (!"".equals(strMsg)) {Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();}
        }
    }

    public void parseJSON() {
        try {
            m_nResultType = _jResult.getInt(Net.NET_VALUE_CODE);
            JSONObject obj = _jResult.getJSONObject(Net.NET_VALUE_RESULT);

            if (m_nConnectType == NetUtil.apis_LOGIN) {
                if (m_nResultType == 200) {
                    mNetUtil.transProfile_ObjToUM(this,0,obj);
                    UserManager.getInstance().member_type = getSharedPreferences("Vitamiin", 0).getString("type", "");
                    UserManager.getInstance().event_num = _jResult.getJSONObject("result").getInt("event_num");
                }
            }
            else if(m_nConnectType== NetUtil.apis_GET_HEALTH_FOOD_BY_ID){
                ArrayList<FoodInfo> arrFoodInfoList = new ArrayList<>();

                JSONObject obj_fod = obj.getJSONObject("food");
                arrFoodInfoList.add(mNetUtil.parseFood(obj_fod));
                mf_info =  mNetUtil.PackFood2to1(arrFoodInfoList).get(0);
            }
            else if(m_nConnectType == NetUtil.apis_GET_HEALTH_FOOD_DETAIL){
                Like = obj.getInt("like");
                JSONArray arr = obj.getJSONArray("detail");
                NoDetails = arr.length();
                ((TextView)findViewById(R.id.imv_number2)).setText("f " + String.valueOf(NoDetails) + " -");

                m_arrPhotoName.clear();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject OneObj = arr.getJSONObject(i);
                    m_arrPhotoName.add(OneObj.getString("image"));
                }
                FoodImageSlidingAdapter adapter = new FoodImageSlidingAdapter(this, m_arrPhotoName, 2);
                m_uiVwpShopPhoto.setAdapter(adapter);
                mHelpIndicator.setViewPager(m_uiVwpShopPhoto);
            }
            else if(m_nConnectType == NetUtil.apis_SET_LIKE_FOOD){
                Like = obj.getInt("like");
            }
        } catch (JSONException e) {e.printStackTrace();}
    }

    JSONObject _jResult;

    @Override
    public void onParseJSon(JSONObject j_source) {
        _jResult = j_source;}

    @Override
    public void onBackPressed() {
        if(m_llyShare.getVisibility()==View.VISIBLE){
            hideShareLly();
        } else {
            onClick(findViewById(R.id.imv_back));
        }
    }
}
