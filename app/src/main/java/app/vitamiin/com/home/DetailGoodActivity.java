package app.vitamiin.com.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import app.vitamiin.com.Adapter.MaterialListAdapter;
import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.IntroActivity;
import app.vitamiin.com.Model.GoodInfo;
import app.vitamiin.com.Model.IngredientInfo;
import app.vitamiin.com.Model.ContentInfo;
import app.vitamiin.com.Model.ReviewInfo;
import app.vitamiin.com.R;
import app.vitamiin.com.setting.FaqNoticeActivity;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;
import app.vitamiin.com.login.LoginActivity;

public class DetailGoodActivity extends BaseActivity implements View.OnClickListener, OnParseJSonListener {
    Context mContext = this;
    NetUtil mNetUtil;
    NetUtil.connectAndgetServer mNetUtilConnetServer;
    int m_nResultType = 200;
    JSONObject _jResult;

    int m_nConnectType = NetUtil.GET_PRODUCT_DETAIL;

    CallbackManager callbackManager;
    ShareDialog shareDialog;
    boolean isloggedIn = false;

    int m_currentPage = 1, m_nMaxPage = 1, m_nTotalCount = 0;
    int index = 0;

    boolean m_bLockListView = false;
    GoodInfo m_info_gd;
    int mGood_id, m_nEWG12, m_nHarmful, m_nAdditive;

    ArrayList<ContentInfo> arrContent = new ArrayList<>();
    ArrayList<IngredientInfo> arrIngredientTotal = new ArrayList<>();
    ArrayList<IngredientInfo> arrIngredientOneElement = new ArrayList<>();
    ArrayList<ArrayList<IngredientInfo>> arrarrIngredient = new ArrayList<>();
    public ArrayList<String> f_name= new ArrayList<>();
    public ArrayList<String> f_des= new ArrayList<>();
    ArrayList<ReviewInfo> arr_r_info = new ArrayList<>();

    MaterialListAdapter m_material_adapter;

    LinearLayout m_llyBar, m_llyShare, m_llyFunc, m_llyReview;
//    LinearLayoutManager mLayoutManager;
    ImageView m_imvKakao, m_imvFacebook;

    int m_nReportType = -1;
    String m_strReport = "";

    int total_func = 0, total_price = 0, total_take = 0, total_taste = 0;
    ScrollView m_scvView;

    TextView m_tvReviewCnt, m_tvReviewCnt2, m_tvReviewCnt3, m_tvLikeCnt, m_tvRate, m_tvSource, m_tvManufacturer, m_tvImport_company;
    ImageView m_imvLike;
    int m_nBarWidth = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_good);
        mNetUtil = new NetUtil();
        mNetUtilConnetServer = mNetUtil.new connectAndgetServer(this);
        Intent intent = getIntent();

        try{
            if (Intent.ACTION_VIEW.equals(intent.getAction())) {
                Uri uri = intent.getData();
                if(uri.toString().contains("fb.me")){       //페북에서 옴//페북에서 올때 _id찾기
                    mGood_id = Integer.valueOf((String.valueOf(uri).split("_id%3D"))[String.valueOf(uri).split("_id%3D").length-1]);
                }else{                          //카톡에서 옴
                    if(uri.getQueryParameter("_activity").equals("it")){
                        intent = new Intent(this, IntroActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }else if(uri.getQueryParameter("_activity").equals("rv")){
                        intent = new Intent(this, DetailReviewActivity.class);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(uri);
                        startActivity(intent);
                        finish();
                        return;
                    }else if(uri.getQueryParameter("_activity").equals("rx")){
                        intent = new Intent(this, DetailExpActivity.class);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(uri);
                        startActivity(intent);
                        finish();
                        return;
                    }else if(uri.getQueryParameter("_activity").equals("pw")){
                        intent = new Intent(this, DetailPowerActivity.class);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(uri);
                        startActivity(intent);
                        finish();
                        return;
                    }else if(uri.getQueryParameter("_activity").equals("fd")){
                        intent = new Intent(this, DetailFoodActivity.class);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(uri);
                        startActivity(intent);
                        finish();
                        return;
                    }else if(uri.getQueryParameter("_activity").equals("ev")){
                        intent = new Intent(this, DetailEventActivity.class);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(uri);
                        startActivity(intent);
                        finish();
                        return;
                    } else
                        mGood_id = Integer.valueOf(uri.getQueryParameter("_id"));
                }
                SharedPreferences pref = getSharedPreferences("Vitamiin", 0);
                if(UserManager.getInstance().member_type.equals("") && !pref.getString("type", "").equals("")){
                    showProgress();
                    m_nConnectType = NetUtil.apis_LOGIN;
                    isloggedIn = mNetUtil.LoginInUtil(this, this, handler, pref.getString("type", ""),pref.getString("email", ""),pref.getString("id", ""),pref.getString("pass", ""));
                }else{
                    showProgress();
                    m_nConnectType = NetUtil.GET_PRODUCT_BY_ID;
                    mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, mGood_id);
                }
            }else{
                m_info_gd = (GoodInfo) intent.getSerializableExtra("info");

                initView();
                updateInfo();
                m_nConnectType = NetUtil.GET_PRODUCT_DETAIL;
                mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, m_info_gd._id);
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
        } catch (Exception e) {            e.printStackTrace();        }
    }

    private void initView() {
        findViewById(R.id.imv_back).setOnClickListener(this);
        findViewById(R.id.rly_bg).setOnClickListener(this);
        findViewById(R.id.tv_write_review).setOnClickListener(this);
        findViewById(R.id.tv_view_more).setOnClickListener(this);
        findViewById(R.id.tv_ingredient).setOnClickListener(this);
        findViewById(R.id.lly_more_material).setOnClickListener(this);

        m_tvReviewCnt = (TextView) findViewById(R.id.tv_review_cnt);
        m_tvReviewCnt2 = (TextView) findViewById(R.id.tv_review_cnt2);
        m_tvReviewCnt3 = (TextView) findViewById(R.id.tv_review_cnt3);
        m_imvLike = (ImageView) findViewById(R.id.imv_like);
        m_tvLikeCnt = (TextView) findViewById(R.id.tv_like_cnt);
        m_tvRate = (TextView) findViewById(R.id.tv_rate);
        m_tvManufacturer = (TextView) findViewById(R.id.tv_Manufacturer);
        m_tvImport_company = (TextView) findViewById(R.id.tv_Import_company);
        m_tvSource = (TextView) findViewById(R.id.tv_source);
        m_tvSource.setOnClickListener(this);

        m_scvView = (ScrollView) findViewById(R.id.scv_view);
        m_scvView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_SCROLL:
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        hideShareLly();
                        break;
                }
                return false;
            }
        });

        findViewById(R.id.lsv_material).setVisibility(View.GONE);
        findViewById(R.id.lly_comment_for_lsv).setVisibility(View.GONE);

        m_llyFunc = (LinearLayout) findViewById(R.id.lly_func);
        m_llyReview = (LinearLayout) findViewById(R.id.lly_review);

        m_material_adapter = new MaterialListAdapter(this, new ArrayList<ArrayList<IngredientInfo>>());

        m_llyBar = (LinearLayout) findViewById(R.id.lly_bar);

        findViewById(R.id.imv_like).setOnClickListener(this);
        findViewById(R.id.imv_share).setOnClickListener(this);
        findViewById(R.id.imv_report).setOnClickListener(this);
        findViewById(R.id.lly_like).setOnClickListener(this);
        findViewById(R.id.lly_shareBtn).setOnClickListener(this);
        findViewById(R.id.lly_report).setOnClickListener(this);

        m_llyShare = (LinearLayout) findViewById(R.id.lly_share);
        m_llyShare.setOnClickListener(this);
        m_imvKakao = (ImageView) findViewById(R.id.imv_kakao);
        m_imvKakao.setOnClickListener(this);
        m_imvFacebook = (ImageView) findViewById(R.id.imv_facebook);
        m_imvFacebook.setOnClickListener(this);

        m_llyShare.setVisibility(View.GONE);
    }

    private void drawChart() {
        m_nBarWidth = m_llyBar.getMeasuredWidth();

        int oneByOne = m_nBarWidth / m_material_adapter.getCount();

        int count1 = 0;
        int count2 = 0;
        int count3 = 0;
        int count4 = 0;
        int count5 = 0;

        for (int i = 0; i < m_material_adapter.getCount(); i++) {
            IngredientInfo m_IngredientInfo = m_material_adapter.getItem(i).get(0);

            if (m_IngredientInfo!=null){
                switch (m_IngredientInfo._ewg){
                    case 0:
                        count1++;
                        break;
                    case 1:
                        count2++;
                        break;
                    case 2:
                        count3++;
                        break;
                    case 3:
                        count4++;
                        break;
                    case 4:
                        count5++;
                        break;
                }
            }
        }

        View view05 = new View(this);
        view05.setLayoutParams(new LinearLayout.LayoutParams(count5 * oneByOne, LinearLayout.LayoutParams.MATCH_PARENT));
        view05.setBackgroundColor(Color.parseColor("#ff6666"));
        m_llyBar.addView(view05);

        View view04 = new View(this);
        view04.setLayoutParams(new LinearLayout.LayoutParams(count4 * oneByOne, LinearLayout.LayoutParams.MATCH_PARENT));
        view04.setBackgroundColor(Color.parseColor("#ffe066"));
        m_llyBar.addView(view04);

        View view03 = new View(this);
        view03.setLayoutParams(new LinearLayout.LayoutParams(count3 * oneByOne, LinearLayout.LayoutParams.MATCH_PARENT));
        view03.setBackgroundColor(Color.parseColor("#71da71"));
        m_llyBar.addView(view03);

        View view02 = new View(this);
        view02.setLayoutParams(new LinearLayout.LayoutParams(count2 * oneByOne, LinearLayout.LayoutParams.MATCH_PARENT));
        view02.setBackgroundColor(Color.parseColor("#80bfff"));
        m_llyBar.addView(view02);

        View view01 = new View(this);
        view01.setLayoutParams(new LinearLayout.LayoutParams(count1 * oneByOne, LinearLayout.LayoutParams.MATCH_PARENT));
        view01.setBackgroundColor(Color.parseColor("#cccccc"));
        m_llyBar.addView(view01);
    }
    private void updateInfo() {
        ((TextView) findViewById(R.id.tv_name)).setText(m_info_gd._name);
        ((TextView) findViewById(R.id.tv_business)).setText(m_info_gd._business);
        ((TextView) findViewById(R.id.tv_report)).setText(m_info_gd._report);
        ((TextView) findViewById(R.id.tv_regdate)).setText(m_info_gd._regdate);
        ((TextView) findViewById(R.id.tv_expiredate)).setText(m_info_gd._expiredate);
        ((TextView) findViewById(R.id.tv_ikon)).setText(m_info_gd._ikon);
        ((TextView) findViewById(R.id.tv_kind)).setText(m_info_gd._kind);
        ((TextView) findViewById(R.id.tv_takein)).setText(m_info_gd._intake);
        ((TextView) findViewById(R.id.tv_preserve)).setText(m_info_gd._preserve);
        ((TextView) findViewById(R.id.tv_warning)).setText(m_info_gd._warning);
        m_tvRate.setText(String.format(Locale.KOREA,"%.01f", m_info_gd._rate));

        ImageView m_imvImage = (ImageView) findViewById(R.id.imv_good);
        Glide.with(mContext).load(Net.URL_SERVER1 + m_info_gd._imagePath).into(m_imvImage);

        updateStar();
    }
    private void updateStar() {
        ImageView[] arr_mimvStars = new ImageView[5];
        arr_mimvStars[0] = (ImageView) findViewById(R.id.imv_star1);
        arr_mimvStars[1] = (ImageView) findViewById(R.id.imv_star2);
        arr_mimvStars[2] = (ImageView) findViewById(R.id.imv_star3);
        arr_mimvStars[3] = (ImageView) findViewById(R.id.imv_star4);
        arr_mimvStars[4] = (ImageView) findViewById(R.id.imv_star5);

        for(int i = 0; i < 5; i++)
            if(m_info_gd._rate < i + 0.5)
                Glide.with(mContext).load(R.drawable.ic_star_empty).into(arr_mimvStars[i]);
            else if(m_info_gd._rate < i+1)
                Glide.with(mContext).load(R.drawable.ic_star_half).into(arr_mimvStars[i]);
            else
                Glide.with(mContext).load(R.drawable.ic_star_one).into(arr_mimvStars[i]);

        if (m_nTotalCount == 0) {
            total_func = 0;
            total_price = 0;
            total_take = 0;
            total_taste = 0;
        } else {
            double aver_func = (double) total_func / (double) m_nTotalCount;
            double aver_price = (double) total_price / (double) m_nTotalCount;
            double aver_take = (double) total_take / (double) m_nTotalCount;
            double aver_taste = (double) total_taste / (double) m_nTotalCount;

            ImageView[] arr_mimvStars1 = new ImageView[5];
            arr_mimvStars1[0] = (ImageView) findViewById(R.id.imv_star11);
            arr_mimvStars1[1] = (ImageView) findViewById(R.id.imv_star12);
            arr_mimvStars1[2] = (ImageView) findViewById(R.id.imv_star13);
            arr_mimvStars1[3] = (ImageView) findViewById(R.id.imv_star14);
            arr_mimvStars1[4] = (ImageView) findViewById(R.id.imv_star15);

            for(int i = 0; i < 5; i++)
                if(aver_func < i + 0.5)
                    Glide.with(mContext).load(R.drawable.ic_star_empty).into(arr_mimvStars1[i]);
                else if(aver_func < i+1)
                    Glide.with(mContext).load(R.drawable.ic_star_half).into(arr_mimvStars1[i]);
                else
                    Glide.with(mContext).load(R.drawable.ic_star_one).into(arr_mimvStars1[i]);

            ImageView[] arr_mimvStars2 = new ImageView[5];
            arr_mimvStars2[0] = (ImageView) findViewById(R.id.imv_star21);
            arr_mimvStars2[1] = (ImageView) findViewById(R.id.imv_star22);
            arr_mimvStars2[2] = (ImageView) findViewById(R.id.imv_star23);
            arr_mimvStars2[3] = (ImageView) findViewById(R.id.imv_star24);
            arr_mimvStars2[4] = (ImageView) findViewById(R.id.imv_star25);

            for(int i = 0; i < 5; i++)
                if(aver_price < i + 0.5)
                    Glide.with(mContext).load(R.drawable.ic_star_empty).into(arr_mimvStars2[i]);
                else if(aver_price < i+1)
                    Glide.with(mContext).load(R.drawable.ic_star_half).into(arr_mimvStars2[i]);
                else
                    Glide.with(mContext).load(R.drawable.ic_star_one).into(arr_mimvStars2[i]);

            ImageView[] arr_mimvStars3 = new ImageView[5];
            arr_mimvStars3[0] = (ImageView) findViewById(R.id.imv_star31);
            arr_mimvStars3[1] = (ImageView) findViewById(R.id.imv_star32);
            arr_mimvStars3[2] = (ImageView) findViewById(R.id.imv_star33);
            arr_mimvStars3[3] = (ImageView) findViewById(R.id.imv_star34);
            arr_mimvStars3[4] = (ImageView) findViewById(R.id.imv_star35);

            for(int i = 0; i < 5; i++)
                if(aver_take < i + 0.5)
                    Glide.with(mContext).load(R.drawable.ic_star_empty).into(arr_mimvStars3[i]);
                else if(aver_take < i+1)
                    Glide.with(mContext).load(R.drawable.ic_star_half).into(arr_mimvStars3[i]);
                else
                    Glide.with(mContext).load(R.drawable.ic_star_one).into(arr_mimvStars3[i]);

            ImageView[] arr_mimvStars4 = new ImageView[5];
            arr_mimvStars4[0] = (ImageView) findViewById(R.id.imv_star41);
            arr_mimvStars4[1] = (ImageView) findViewById(R.id.imv_star42);
            arr_mimvStars4[2] = (ImageView) findViewById(R.id.imv_star43);
            arr_mimvStars4[3] = (ImageView) findViewById(R.id.imv_star44);
            arr_mimvStars4[4] = (ImageView) findViewById(R.id.imv_star45);

            for(int i = 0; i < 5; i++)
                if(aver_taste < i + 0.5)
                    Glide.with(mContext).load(R.drawable.ic_star_empty).into(arr_mimvStars4[i]);
                else if(aver_taste < i+1)
                    Glide.with(mContext).load(R.drawable.ic_star_half).into(arr_mimvStars4[i]);
                else
                    Glide.with(mContext).load(R.drawable.ic_star_one).into(arr_mimvStars4[i]);
        }
    }

    public void setReport(int type, String content) {       //type: 0~3
        m_nReportType = type;
        m_strReport = content;
        m_nConnectType = NetUtil.REPORT_PRODUCT;
        mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, new String[] {String.valueOf(m_info_gd._id),
                                                                                        String.valueOf(m_nReportType),
                                                                                        m_strReport});
    }

    private void hideShareLly() {
        if(m_llyShare.getVisibility()==View.VISIBLE){
            m_llyShare.animate().translationY(m_llyShare.getHeight()*2).setInterpolator(new DecelerateInterpolator(1));
            m_imvKakao.animate().translationY(m_imvKakao.getHeight()*2).setInterpolator(new DecelerateInterpolator(3));
            m_imvFacebook.animate().translationY(m_imvFacebook.getHeight()*2).setInterpolator(new DecelerateInterpolator(3));
            m_llyShare.setVisibility(View.GONE);
            (findViewById(R.id.rly_bg)).setVisibility(View.GONE);
        }
    }

    private void showShareLly() {
        m_llyShare.setVisibility(View.VISIBLE);
        m_llyShare.animate().translationY(0).setInterpolator(new DecelerateInterpolator(1));
        m_imvKakao.animate().translationY(0).setInterpolator(new DecelerateInterpolator(3));
        m_imvFacebook.animate().translationY(0).setInterpolator(new DecelerateInterpolator(3));
        (findViewById(R.id.rly_bg)).setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.imv_back:
                if(UserManager.getInstance().member_type.equals("")) {
                    intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                }else{
                    if(isTaskRoot()){
                        intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        intent = new Intent();
                        intent.putExtra("view_cnt", m_info_gd._view_cnt);
                        intent.putExtra("like_cnt", m_info_gd._like_cnt);
                        intent.putExtra("review_cnt", m_info_gd._review_cnt);
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
            case R.id.lly_like:
                like();
                break;
            case R.id.imv_share:
            case R.id.lly_shareBtn:
                showShareLly();
                break;
            case R.id.imv_report:
            case R.id.lly_report:
                new ReportGoodDialog(this).show();
            break;

            case R.id.imv_kakao:
                shareToKakaoTalk();
                hideShareLly();
                break;
            case R.id.imv_facebook:
                shareToFacebook();
                hideShareLly();
            break;

            case R.id.tv_write_review:
                intent = new Intent(this, ReviewWriteActivity.class);
                intent.putExtra("info_gd", m_info_gd);
                intent.putExtra("fromDetailGood", true);
                startActivityForResult(intent, 111);
                break;
            case R.id.tv_view_more:
                m_currentPage++;
                m_nConnectType = NetUtil.apis_GET_MORE_REVIEW_IN_DETAIL_GOOD;
                mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, new int[] {m_info_gd._id, m_currentPage});
                break;
            case R.id.tv_ingredient:
                intent = new Intent(this, ContentDialog.class);
                intent.putExtra("info", m_info_gd);
                intent.putExtra("ContentsInfo", arrContent);
                startActivity(intent);
//                m_contentDlg = new ContentDialog(this, m_r_info, arrContent);
//                m_contentDlg.show();
                break;
            case R.id.lly_more_material:
                intent = new Intent(this, MaterialDetailDialog.class);
                intent.putExtra("info", m_info_gd);
                intent.putExtra("IngredientInfo", arrIngredientTotal);
                intent.putExtra("EWG12", m_nEWG12);
                intent.putExtra("Harmful", m_nHarmful);
                intent.putExtra("Additive", m_nAdditive);
                startActivity(intent);
                break;
            case R.id.tv_source:
                intent = new Intent(this, FaqNoticeActivity.class);
                intent.putExtra("from_source", true);
                intent.putExtra("type", 0);
                startActivity(intent);
                break;
        }
    }


    private void shareToFacebook() {
//        String str = "섭취방법:\n" + m_r_info._intake + "\n보존 및 유통기준:\n" + m_r_info._preserve + "\n섭취 시 주의사항:\n" + m_r_info._warning;
//.putString("og:title", "[" + m_r_info._business + "]의 제품 '" + m_r_info._name + "'을 소개합니다. by 비타미인")
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            try {
                ////////////////////////////////////////////////////////////////////////////
                //////////////////////////우선 ShareLinkContent.Builder()를 보내는 것은 성공//
                /////////////////////////////logInWithReadPermissions으로 로그인했음 by 동현//
//                Bitmap bitmap = BitmapFactory.decodeFile(UserManager.getInstance().arr_profile_photo_file.get(0).getPath());
//                SharePhoto photo = new SharePhoto.Builder()
////                        .setImageUrl(Uri.parse(Net.URL_SERVER2 + m_r_info._imagePath))
//                        .setBitmap(bitmap)
//                        .setCaption("Description")
//                        .build();
//                SharePhotoContent content2 = new SharePhotoContent.Builder()
////                        .setContentUrl(Uri.parse("https://fb.me/1085983591506907?_id=" + String.valueOf(m_r_info._id)))
//                        .addPhoto(photo)
////                        .setPhotos()
//                        .build();
//                shareDialog.show(content2);
        //////////////////////////////     or      ////////////////////////////
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setImageUrl(Uri.parse(Net.URL_SERVER2 + m_info_gd._imagePath))
                        .setContentTitle("[" + m_info_gd._business + "] " + m_info_gd._name)
                        .setContentDescription("비타미인에서 상세한 제품 정보와 다른 사람들이 남긴 리뷰를 확인해보세요!")
                        .setContentUrl(Uri.parse("https://fb.me/1085983591506907?_id=" + String.valueOf(m_info_gd._id)))
                        .build();

                shareDialog.show(content);
//                        .setContentUrl(Uri.parse("http://vitamiin.co.kr"))

                //우선 ShareLinkContent.Builder()를 보내는 것은 성공//////////////////////////
                //logInWithReadPermissions으로 로그인했음 by 동현/////////////////////////////
                ////////////////////////////////////////////////////////////////////////////


                /*
                ////////////////////////////////////////////////////////////////////////////
                //////////////////////////우선 ShareOpenGraphContent.Builder()를 보내는 것은 실패//
                /////////////////////////////logInWithPublishPermissions으로 로그인했음 by 동현//
                ShareOpenGraphObject object2 = new ShareOpenGraphObject.Builder()
                        .putString("og:type", "Object")
                        .putString("og:title", m_r_info._name)
                        .putString("og:description", m_r_info._business)
//                        .putString("og:url", "http://vitamiin.co.kr")
//                        .putString("og:url","https://fb.me/1085983591506907?_id=" + String.valueOf(m_r_info._id))
                        .putString("og:image", Net.URL_SERVER1 + m_r_info._imagePath)
                        .build();
                ShareOpenGraphAction action2 = new ShareOpenGraphAction.Builder()
                        .setActionType("detail.share")
                        .putObject("detail", object2)
                        .build();
                ShareOpenGraphContent content2 = new ShareOpenGraphContent.Builder()
                        .setPreviewPropertyName("detail")
                        .setAction(action2)
                        .build();
                ShareDialog.show(this,content2);
//                shareDialog.show(content2);
//                shareDialog.show(content2, ShareDialog.Mode.AUTOMATIC);
                //우선 ShareOpenGraphContent.Builder()를 보내는 것은 싷패//////////////////////////
                //logInWithPublishPermissions으로 로그인했음 by 동현/////////////////////////////
                ////////////////////////////////////////////////////////////////////////////
                */

            } catch (Exception e) {
                System.out.println("Exc2=" + e);
            }
        }
    }

    private void shareToKakaoTalk() {
        try {
            final Map<String, String> properties = new HashMap<>();
            properties.put("_id", String.valueOf(m_info_gd._id));
            properties.put("_activity", "gd");

            final KakaoLink kakaoLink = KakaoLink.getKakaoLink(this);
            final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink
                    .createKakaoTalkLinkMessageBuilder();

            String strText = "[" + getString(R.string.app_name) + "]\n" + m_info_gd._name + "\n" + m_info_gd._business;
            kakaoTalkLinkMessageBuilder.addText(strText)
                                       .addImage(Net.URL_SERVER1 + m_info_gd._imagePath, 100, 100)
//                                       .addWebButton("카카오톡 홈페이지로 이동", "vitamiin://detailgood?_id=" + String.valueOf(m_r_info._id))
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

//            kakaoTalkLinkMessageBuilder.addAppLink("자세히 보기",
//                    new AppActionBuilder()
//                            .addActionInfo(AppActionInfoBuilder
//                                    .createAndroidActionInfoBuilder()
//                                    .setExecuteParam("execparamkey1=1111")
//                                    .setMarketParam("referrer=kakaotalklink")
//                                    .build())
//                            .addActionInfo(AppActionInfoBuilder.createiOSActionInfoBuilder()
//                                    .setExecuteParam("execparamkey1=1111")
//                                    .build())
//                            .setUrl("http://www.naver.com") // PC 카카오톡 에서 사용하게 될 웹사이트 주소
//                            .build());

            kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, this);
        } catch (Exception e) {Toast.makeText(this, "공유 실패", Toast.LENGTH_SHORT).show();}
    }

    private void like() {
        m_nConnectType = NetUtil.LIKE_PRODUCT;
        mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, new int[] {m_info_gd._id, m_info_gd.like_good});

        m_imvLike.setSelected(!m_imvLike.isSelected());
        m_info_gd.like_good = m_info_gd.like_good==1 ? 0:1;
        m_tvLikeCnt.setText(String.valueOf(++m_info_gd._like_cnt));
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Net.THREAD_REQUEST_END)
                processForNetEnd();
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
                if (m_nConnectType == NetUtil.apis_LOGIN) {
                    showProgress();
                    m_nConnectType = NetUtil.GET_PRODUCT_BY_ID;
                    mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, mGood_id);
                }
                else if (m_nConnectType == NetUtil.GET_PRODUCT_BY_ID) {     //6
                    showProgress();
                    m_nConnectType = NetUtil.GET_PRODUCT_DETAIL;
                    mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, mGood_id);
                }
                else if (m_nConnectType == NetUtil.GET_PRODUCT_DETAIL) {          //1
                    ((TextView) findViewById(R.id.tv_ewg12)).setText(m_nEWG12 + "개");
                    ((TextView) findViewById(R.id.tv_harmful)).setText(m_nHarmful + "개");
                    ((TextView) findViewById(R.id.tv_additive)).setText(m_nAdditive + "개");

                    m_tvReviewCnt.setText("리뷰 (" + m_nTotalCount + ")");
                    m_tvReviewCnt2.setText(String.valueOf(m_nTotalCount));
                    m_tvReviewCnt3.setText(String.valueOf(m_nTotalCount));
                    m_tvLikeCnt.setText(String.valueOf(m_info_gd._like_cnt));
                    m_tvRate.setText(String.format("%.01f", m_info_gd._rate));
                    m_tvManufacturer.setText(m_info_gd.manufacturer);
                    m_tvImport_company.setText(m_info_gd.import_company);
                    updateStar();

                    if (m_currentPage == 1)
                        m_scvView.setScrollY(0);

                    if (m_nTotalCount > 2 && m_nMaxPage != m_currentPage) {
                        findViewById(R.id.tv_view_more).setVisibility(View.VISIBLE);
                    } else
                        findViewById(R.id.tv_view_more).setVisibility(View.GONE);

                    if (m_info_gd.like_good == 1)
                        m_imvLike.setSelected(true);
                    else
                        m_imvLike.setSelected(false);
                }
                else if (m_nConnectType == NetUtil.apis_GET_MORE_REVIEW_IN_DETAIL_GOOD) {
                    if (m_nMaxPage == m_currentPage)
                        findViewById(R.id.tv_view_more).setVisibility(View.GONE);
                }
                else if (m_nConnectType == NetUtil.REPORT_PRODUCT) {
                    Toast.makeText(this, "해당 제품이 신고 되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            if (!"".equals(strMsg)) {Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();}
        }
        m_bLockListView = false;
    }

    public void parseJSON() {
        try {
            m_nResultType = _jResult.getInt(Net.NET_VALUE_CODE); //
            JSONObject obj = _jResult.getJSONObject(Net.NET_VALUE_RESULT);

            if (m_nConnectType == NetUtil.apis_LOGIN) {
                if (m_nResultType == 0) {
                    mNetUtil.transProfile_ObjToUM(this,0,obj);
                    UserManager.getInstance().member_type = getSharedPreferences("Vitamiin", 0).getString("type", "");
                    UserManager.getInstance().event_num = _jResult.getJSONObject("result").getInt("event_num");
                }
            } else if (m_nConnectType == NetUtil.GET_PRODUCT_BY_ID) {     //6
                m_info_gd =  mNetUtil.parseGood(obj);
                initView();
                updateInfo();
            } else if (m_nConnectType == NetUtil.GET_PRODUCT_DETAIL) {        //1
                JSONArray arr_f_des = obj.getJSONArray("f_descriptions");
                for (int i = 0; i < arr_f_des.length(); i++) {
                    f_name.add(arr_f_des.getJSONObject(i).getString("f_name"));
                    f_des.add(arr_f_des.getJSONObject(i).getString("f_description"));
                }

                JSONArray arr = obj.getJSONArray("material");
                m_nEWG12 =0;
                m_nHarmful=0;
                m_nAdditive=0;
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj_mat = arr.getJSONObject(i);
                    IngredientInfo m_IngredientInfo = new IngredientInfo();
                    m_IngredientInfo._id = obj_mat.getInt("id");
                    m_IngredientInfo._mat_id = obj_mat.getInt("material_id");
                    m_IngredientInfo._type = obj_mat.getInt("type");
                    m_IngredientInfo._name= obj_mat.getString("name_0");
                    m_IngredientInfo._name_kor= obj_mat.getString("name");
                    m_IngredientInfo._name_eng= obj_mat.getString("name_eng");
                    m_IngredientInfo._functionality= obj_mat.getString("functionality");
                    m_IngredientInfo._definition= obj_mat.getString("definition");
                    m_IngredientInfo._daily= obj_mat.getString("daily");
                    m_IngredientInfo._caution= obj_mat.getString("caution");
                    m_IngredientInfo._class= obj_mat.getString("class");
                    m_IngredientInfo._ewg= obj_mat.getInt("ewg");
                    m_IngredientInfo._ewg12= !obj_mat.getString("ewg12").equals("0");
                    m_IngredientInfo._harmful= !obj_mat.getString("harmful").equals("0");

                    arrIngredientTotal.add(m_IngredientInfo);

                    if(m_IngredientInfo._ewg12){
                        m_nEWG12++;
                    }
                    if(m_IngredientInfo._harmful){
                        m_nHarmful++;
                    }
                    if(m_IngredientInfo._type==4 || m_IngredientInfo._type==5){
                        m_nAdditive++;
                    }
                }

                int fore_mat_id = -1;
                for (int i=0; i < arrIngredientTotal.size(); i++) {
                    if(i==0)
                        fore_mat_id = arrIngredientTotal.get(i)._mat_id;

                    if(fore_mat_id != arrIngredientTotal.get(i)._mat_id) {
                        if(arrIngredientOneElement.size()!=0)
                            arrarrIngredient.add(arrIngredientOneElement);

                        arrIngredientOneElement = new ArrayList<>();
                        arrIngredientOneElement.add(arrIngredientTotal.get(i));
                    }else{
                        arrIngredientOneElement.add(arrIngredientTotal.get(i));
                    }
                    fore_mat_id = arrIngredientTotal.get(i)._mat_id;
                }
                if(arrIngredientOneElement.size()!=0)
                    arrarrIngredient.add(arrIngredientOneElement);

                int refEWG = arrarrIngredient.get(0).get(0)._ewg;
                int refIval = 0;

                for(int j = 0; j < arrarrIngredient.size(); j++){
                    for(int i = 0; i < arrarrIngredient.size(); i++) {
                        if (arrarrIngredient.get(i).get(0)._ewg >= refEWG) {
                            refIval = i;
                            refEWG = arrarrIngredient.get(refIval).get(0)._ewg;
                        }
                    }

                    m_material_adapter.add(arrarrIngredient.get(refIval));
                    arrarrIngredient.remove(refIval);
                    refIval = 0;
                    if(arrarrIngredient.size()!=0)
                        refEWG = arrarrIngredient.get(0).get(0)._ewg;
                    j--;
                }

//                for(ArrayList<> arr: arrarrIngredient)
//                    m_material_adapter.add(arr);

                m_material_adapter.notifyDataSetChanged();

                drawChart();
                // material 데이터에서 세 개의 숫자를 계산해 와서 각 TextView에 넣어주어야 한다. by 동현
                // m_nEWG12,m_nHarmful,m_nAdditive
                arr = obj.getJSONArray("content");
                for(int i=0; i<arr.length();i++){
                    JSONObject obj_con = arr.getJSONObject(i);
                    ContentInfo m_ContentInfo = new ContentInfo();
                    m_ContentInfo._content_id = obj_con.getInt("content_id");
                    m_ContentInfo._content_unit = obj_con.getString("content_unit");
                    m_ContentInfo._per_day = obj_con.getString("per_day");
                    m_ContentInfo._ppds = obj_con.getString("ppds");
                    m_ContentInfo._f_name = obj_con.getString("f_name");
                    m_ContentInfo._f_description = obj_con.getString("f_description");

                    arrContent.add(m_ContentInfo);
                }

                m_info_gd._rate = obj.getDouble("rate");
                m_info_gd.manufacturer = obj.getString("manufacturer");
                m_info_gd.import_company = obj.getString("import_company");
                m_info_gd.like_good = obj.getInt("like");

                total_func = obj.isNull("sum_func")? 0:obj.getInt("sum_func");
                total_price = obj.isNull("sum_price")? 0:obj.getInt("sum_price");
                total_take = obj.isNull("sum_take")? 0:obj.getInt("sum_take");
                total_taste = obj.isNull("sum_taste")? 0:obj.getInt("sum_taste");
                m_nTotalCount = obj.getInt(Net.NET_VALUE_TOTAL_COUNT);
                m_nMaxPage = obj.getInt(Net.NET_VALUE_TOTAL_PAGE);

                JSONArray arr_rev = obj.getJSONArray("review");
                for (int i = 0; i < arr_rev.length(); i++) {
                    JSONObject obj_rev = arr_rev.getJSONObject(i);
                    ReviewInfo m_r_info = mNetUtil.parseReview(obj_rev, true);
                    arr_r_info.add(m_r_info);
                    addLineearLayout_ReviewList(i);
                }
                if(m_currentPage==1)
                    fillLineearLayout_FunctionalityTable();
            } else if (m_nConnectType == NetUtil.apis_GET_MORE_REVIEW_IN_DETAIL_GOOD) {
                JSONArray arr_rev = obj.getJSONArray("review");
                for (int i = 0; i < arr_rev.length(); i++) {
                    JSONObject obj_rev = arr_rev.getJSONObject(i);
                    ReviewInfo m_r_info = mNetUtil.parseReview(obj_rev, true);
                    arr_r_info.add(m_r_info);
                    addLineearLayout_ReviewList(arr_r_info.size()-1);
                }
            } else if (m_nConnectType == NetUtil.LIKE_PRODUCT) {
                m_info_gd.like_good = obj.getInt("like");
            }
        } catch (JSONException e) { e.printStackTrace(); }
    }

    private void fillLineearLayout_FunctionalityTable() {
        String[] oneFunc;
        for (int i = 0; i < f_name.size(); i++) {
            oneFunc = new String[]{f_name.get(i), f_des.get(i)};

            ListLayout_Func layout = new ListLayout_Func(this, i, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
            layout.setFuncContent(oneFunc);
            m_llyFunc.addView(layout, i);
        }
    }

    private void addLineearLayout_ReviewList(int addRv) {
        ListLayout_Review layout = new ListLayout_Review(this, addRv, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index = (Integer) view.getTag();
                Intent intent = new Intent(mContext, DetailReviewActivity.class);
                intent.putExtra("info", arr_r_info.get(index));
                startActivityForResult(intent, 222);
            }
        }, false);
        layout.setReviewContent(arr_r_info.get(addRv));
        m_llyReview.addView(layout, addRv);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 111:
                    ReviewInfo m_r_info = (ReviewInfo)data.getSerializableExtra("info");
                    m_tvReviewCnt.setText("리뷰 (" + ++m_nTotalCount + ")");
                    m_tvReviewCnt2.setText(String.valueOf(m_nTotalCount));
                    m_tvReviewCnt3.setText(String.valueOf(m_nTotalCount));

                    arr_r_info.add(m_r_info);
                    addLineearLayout_ReviewList(arr_r_info.size()-1);
                    break;
                case 222:
                    if (data.getBooleanExtra("delete", false)) {
                        m_tvReviewCnt.setText("리뷰 (" + --m_nTotalCount + ")");
                        m_tvReviewCnt2.setText(String.valueOf(m_nTotalCount));
                        m_tvReviewCnt3.setText(String.valueOf(m_nTotalCount));

                        arr_r_info.remove(index);
                        m_llyReview.removeViewAt(index);
                        for(int i = index; i < arr_r_info.size(); i++) {
                            ListLayout_Review layout = (ListLayout_Review) m_llyReview.getChildAt(i);
                            layout.setTag(i);
                        }
                    } else {
                        ListLayout_Review layout = (ListLayout_Review)m_llyReview.getChildAt(index);
                        layout.setReviewContent(data.getIntExtra("comment_cnt", 0), data.getDoubleExtra("rate", 0.0), data.getStringExtra("content"));
                    }
                    break;
            }
        }
    }

    @Override
    public void onParseJSon(JSONObject j_source) {_jResult = j_source;}

    @Override
    public void onBackPressed() {
        if(m_llyShare.getVisibility()==View.VISIBLE){
            hideShareLly();
        } else {
            onClick(findViewById(R.id.imv_back));
        }
    }
}
