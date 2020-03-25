package app.vitamiin.com.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
//import android.support.v4.app.FragmentManager;
import androidx.fragment.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentPagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.support.v4.widget.DrawerLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kakao.kakaolink.AppActionBuilder;
import com.kakao.kakaolink.AppActionInfoBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import app.vitamiin.com.MyFirebaseInstanceIDService;
import app.vitamiin.com.NoticeDialog;
import app.vitamiin.com.common.Const;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.common.UserManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.BuildConfig;
import app.vitamiin.com.Model.ReviewInfo;
import app.vitamiin.com.R;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;
import app.vitamiin.com.setting.FaqNoticeActivity;
import app.vitamiin.com.setting.ModifyActivity;
import app.vitamiin.com.setting.MyPageActivity;
import app.vitamiin.com.setting.QuestionActivity;
import app.vitamiin.com.setting.SettingActivity;
import app.vitamiin.com.login.LoginActivity;

public class MainActivity extends BaseActivity implements OnParseJSonListener, SlideMenuListener.SlideMenuClickListener, View.OnClickListener, MyFirebaseInstanceIDService.TokenRefreshCallback {
    Context mContext;
    NetUtil mNetUtil;
    NetUtil.connectAndgetServer mNetUtilConnetServer;
    public final String PROPERTY_FCM_TOKEN = "fcm_token";
    public final String PROPERTY_APP_VERSION = "appVersion";
    public SharedPreferences mpref;

    public static final int MSG_UNCOLOR_START = 0;
    public static final int MSG_UNCOLOR_STOP = 1;
    public static final int MSG_COLOR_START = 2;
    public static final int MSG_COLOR_STOP = 3;

    public static final String MESSENGER_INTENT_KEY = BuildConfig.APPLICATION_ID + ".MESSENGER_INTENT_KEY";
    public static final String WORK_DURATION_KEY = BuildConfig.APPLICATION_ID + ".WORK_DURATION_KEY";

    PagerSlidingTabStrip tabs;
    ViewPager pager;
    MyPagerAdapter adapter;
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    DrawerLayout m_dlDrawerLayout = null;
    View m_icSliderMenu = null;
    LinearLayout m_llyShare;
    SlideMenuListener m_slideListenr;

    public boolean m_LockView = true;
    HomeFragment frag_home;
    app.vitamiin.com.home.ReviewFragment frag_review;
    EventFragment frag_event;
    RecommendFragment frag_recom;
    VitaWikiFragment frag_wiki;
    ImageView m_imvKakao, m_imvFacebook;

    private boolean m_bFinish = false;
    public int event_num = 1;
    public Timer m_Timer = null;
    public String currentVersion, onlineVersion;

    private FirebaseInstanceId m_firebaseInstance;
    private MyFirebaseInstanceIDService m_myfirebaseInstanceIDservice;
    private String refreshedToken;
    MyFirebaseInstanceIDService.TokenRefreshCallback m_Cb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mNetUtil = new NetUtil();
        mNetUtilConnetServer = mNetUtil.new connectAndgetServer(this);
        mpref = getSharedPreferences("Vitamiin", 0);
        initView();

        m_Cb = this;
        m_firebaseInstance = FirebaseInstanceId.getInstance();
        m_myfirebaseInstanceIDservice = new MyFirebaseInstanceIDService();
        m_myfirebaseInstanceIDservice.setTokenRefreshCallback(m_Cb);
        registerInBackground();

        event_num = UserManager.getInstance().event_num;

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

        try {
            currentVersion = this.getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            localNameNotFoundException.printStackTrace();
        }

        GetVersionCode versionChecker = new GetVersionCode();
        versionChecker.execute();
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                m_myfirebaseInstanceIDservice.setTokenRefreshCallback(m_Cb);
                m_myfirebaseInstanceIDservice.onTokenRefresh();

                refreshedToken = m_firebaseInstance.getToken();
                return "";
            }
        }.execute(null, null, null);
    }

    @Override
    public void TokenCompare_SaveToServer(String newToken){     //여기 오는 것 자체가 account 정보가 있다는 것
        if (newToken != null && !newToken.isEmpty() && newToken.length() != 0) {        //새 토큰이 유효하면 이것을 저장보내기. by 동현
            Const.g_fcmToken = newToken;
            storeFcmTokenToSharedPref(MainActivity.this, Const.g_fcmToken);
            mNetUtilConnetServer.connectServer(mContext, handler, NetUtil.apis_REG_FCMTOKEN_TO_SERVER, Const.g_fcmToken);
        } else{
            if (Const.g_fcmToken != null && !Const.g_fcmToken.isEmpty() && Const.g_fcmToken.length() != 0) {        //새 토큰은 의미가 없고, 기존 토큰이 유효하면..  by 동현
                storeFcmTokenToSharedPref(MainActivity.this, Const.g_fcmToken);
                mNetUtilConnetServer.connectServer(mContext, handler, NetUtil.apis_REG_FCMTOKEN_TO_SERVER, Const.g_fcmToken);
            } else {
            }
        }
    }

    private void storeFcmTokenToSharedPref(Context context, String TokenToSave) {
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = mpref.edit();
        editor.putString(PROPERTY_FCM_TOKEN, TokenToSave);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
        Log.i("reviewsee", "Saving TokenToSave on app version " + appVersion);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {  // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void initView() {
        findViewById(R.id.imv_setting).setOnClickListener(this);
        findViewById(R.id.imv_BI_logo).setOnClickListener(this);
        findViewById(R.id.rly_search).setOnClickListener(this);
        findViewById(R.id.imv_mypage).setOnClickListener(this);

        m_llyShare = (LinearLayout) findViewById(R.id.lly_share);
        m_llyShare.setOnClickListener(this);
        m_imvKakao = (ImageView) findViewById(R.id.imv_kakao);
        m_imvKakao.setOnClickListener(this);
        m_imvFacebook = (ImageView) findViewById(R.id.imv_facebook);
        m_imvFacebook.setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_share_title)).setText("비타미인을 지인들에게 추천해주세요! :)");

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setShouldExpand(false);

        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);

        m_dlDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        m_dlDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {            }

            @Override
            public void onDrawerOpened(View drawerView) {
                m_slideListenr.updateMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {            }

            @Override
            public void onDrawerStateChanged(int newState) {            }
        });

        m_icSliderMenu = findViewById(R.id.ic_slide_menu);
        m_slideListenr = new SlideMenuListener(this, this, m_icSliderMenu);
        findViewById(R.id.rly_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideShareLly();
            }
        });
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        int m_event_num = 1;

        private final String[] TITLES_0 = {"HOME", "리뷰", "제품추천",  "비타위키"};
        private final String[] TITLES_1 = {"HOME", "리뷰", "이벤트", "제품추천",  "비타위키"};

        public MyPagerAdapter(FragmentManager fm) {super(fm);}

        @Override
        public CharSequence getPageTitle(int position) {
            if(event_num==0)
                return TITLES_0[position];
            else
                return TITLES_1[position];
        }

        @Override
        public int getCount() {
            if(event_num==0)
                return TITLES_0.length;
            else
                return TITLES_1.length;
        }

        @Override
        public Fragment getItem(int position) {
            if(event_num==0){
                if (position == 0) {
                    frag_home = HomeFragment.newInstance();
                    return frag_home;
                } else if (position == 1) {
                    frag_review = app.vitamiin.com.home.ReviewFragment.newInstance();
                    return frag_review;
                } else if (position == 2) {
                    frag_recom = RecommendFragment.newInstance();
                    return frag_recom;
                } else {
                    frag_wiki = VitaWikiFragment.newInstance();
                    return frag_wiki;
                }
            }
            else{
                if (position == 0) {
                    frag_home = HomeFragment.newInstance();
                    return frag_home;
                } else if (position == 1) {
                    frag_review = app.vitamiin.com.home.ReviewFragment.newInstance();
                    return frag_review;
                } else if (position == 2) {
                    frag_event = EventFragment.newInstance();
                    return frag_event;
                } else if (position == 3) {
                    frag_recom = RecommendFragment.newInstance();
                    return frag_recom;
                } else {
                    frag_wiki = VitaWikiFragment.newInstance();
                    return frag_wiki;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int p_requestCode,
                                    int p_resultCode,
                                    Intent p_intentActivity) {
        super.onActivityResult(p_requestCode, p_resultCode, p_intentActivity);
        if (p_resultCode == RESULT_OK) {                //p_resultCode 는 back 또는 취소를 눌러서 돌아오면 0 이 된다. (p_resultCode = -1 이 OK) by 동현
            switch (p_requestCode) {
                case 400:       //가족 건강정보 관리(ModifyActivity)에서 돌아오는 곳. by 동현
                    if(frag_recom!=null)
                        frag_recom.updatePhotoListView_All();
                    m_slideListenr.updateMenu();
                    break;
                case 500:       //가족 추가 후, 돌아오는 곳
                    if(UserManager.getInstance().arr_profile_photo_resID.size()!=frag_recom.m_arrPhotoName.size()) {
                        frag_recom.m_arrPhotoName.remove(frag_recom.m_arrPhotoName.size() - 1);
                        frag_recom.REALscrollToHereIndex=0;
                    }
                    frag_recom.updatePhotoListView_All();
                    break;
                case 501:       //가족 정보 변경 후 돌아오는 곳.
                    frag_recom.updatePhotoListView_All();
                    break;
                case 502:       //상세 정보 변경 후 돌아오는 곳.(할게 없는데 괜히 만들었다;;; by 동현
                    if (frag_recom != null)
                        frag_recom.m_tvRecommendBtn.setSelected(true);
                    break;
                case 600:       //리뷰쓰기 and 노하우 공유 쓰기 activity 의 결과
                    if (frag_review != null)
                        frag_review.updateUIafterWriting();
                    break;
                case 6011:       //review frag 의 게시물에서 돌아오는 곳_review
                    if (frag_review != null) {
                        if (p_intentActivity.getBooleanExtra("delete", false))
                            frag_review.deleteItem();
                        else
                            if(p_intentActivity.getSerializableExtra("editedReview")!=null){
                                ReviewInfo m_info = (ReviewInfo) p_intentActivity.getSerializableExtra("editedReview");
                                frag_review.updateReviewItem(m_info);
                            }else
                                frag_review.m_review_adapter.notifyDataSetChanged();
                    }
                    break;
                case 6012:       //review frag 의 게시물에서 돌아오는 곳_exper
                    if (frag_review != null) {
                        if (p_intentActivity.getBooleanExtra("delete", false))
                            frag_review.deleteItem();
                        else
                            if(p_intentActivity.getSerializableExtra("editedReview")!=null){
                                ReviewInfo m_info = (ReviewInfo) p_intentActivity.getSerializableExtra("editedReview");
                                frag_review.updateReviewItem(m_info);
                            }else
                                frag_review.m_exp_review_adapter.notifyDataSetChanged();
                    }
                    break;
                case 602:                         //HomeFragment에서 노출된 파워리뷰를 보고 돌아오는 경우. by 동현
                    if (frag_home != null) {
                        frag_home.updateHomeTabPowerRevieItem(p_intentActivity.getIntExtra("view_cnt", 0),
                                             p_intentActivity.getIntExtra("like_cnt", 0),
                                             p_intentActivity.getIntExtra("comment_cnt", 0));
                    }
                    break;
                case 701:                           //WikiFragment 에서 파워리뷰를 보고 오는 by 동현
                    if (frag_wiki != null) {
                        frag_wiki.updateWikiItem(p_intentActivity.getIntExtra("view_cnt", 0),
                                             p_intentActivity.getIntExtra("like_cnt", 0),
                                             p_intentActivity.getIntExtra("comment_cnt", 0));
                    }
                    break;
                case 702:                           //WikiFragment 에서 성분분석 보고 오는 by 동현(no code)
                    if (frag_wiki != null) {
                        frag_wiki.updateWikiItem(p_intentActivity.getIntExtra("view_cnt", 0),
                                             p_intentActivity.getIntExtra("like_cnt", 0),
                                             0);
                    }
                    break;
                case 703:                           //WikiFragment 에서 전문가 칼럼을 보고 오는 by 동현
                    if (frag_wiki != null) {
                        frag_wiki.updateWikiItem(p_intentActivity.getIntExtra("view_cnt", 0),
                                             p_intentActivity.getIntExtra("like_cnt", 0),
                                             p_intentActivity.getIntExtra("comment_cnt", 0));
                    }
                    break;
                case 801:
                    if (frag_event != null) {
                        frag_event.updateEventItem(p_intentActivity.getIntExtra("view_cnt", 0),
                                p_intentActivity.getIntExtra("like_cnt", 0),
                                p_intentActivity.getIntExtra("comment_cnt", 0));
                    }
                    break;
                case 900:                           //MypageActivity 에서 돌아오는 길 by 동현
                    if (frag_recom != null)
                        frag_recom.updatePhotoListView_All();
                    break;
                case 1000:
                    if (p_intentActivity!=null && p_intentActivity.getBooleanExtra("signout", false)) {
                        Toast.makeText(this, "회원 탈퇴 되었습니다.", Toast.LENGTH_SHORT).show();
                        UserManager.getInstance().saveData(this, "", "",  "", "");
                        UserManager.getInstance().releaseUserData();
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (p_intentActivity!=null && p_intentActivity.getBooleanExtra("logout", false)) {
                        gotoLogout();
                    }
                    break;
            }
        }
    }

    @Override
    public void gotoMyPage() {
        m_dlDrawerLayout.closeDrawers();
        Intent intent = new Intent(this, MyPageActivity.class);
        intent.putExtra("mb_id", UserManager.getInstance().member_id);
        intent.putExtra("mb_nick", UserManager.getInstance().member_nick_name);
        intent.putExtra("mb_photo", UserManager.getInstance().arr_profile_photo_file.get(0));
        startActivityForResult(intent, 900);
    }
    @Override
    public void gotoHealth() {
        m_dlDrawerLayout.closeDrawers();
        Intent intent = new Intent(this, ModifyActivity.class);
        intent.putExtra("fromMenu", true);
        startActivityForResult(intent, 400);
    }
    @Override
    public void gotoSetting() {
        m_dlDrawerLayout.closeDrawers();
        Intent intent = new Intent(this, SettingActivity.class);
        startActivityForResult(intent, 1000);
    }
    @Override
    public void gotoNotice() {
        m_dlDrawerLayout.closeDrawers();
        Intent intent = new Intent(this, FaqNoticeActivity.class);
        intent.putExtra("type", 1);
        startActivity(intent);
    }
    @Override
    public void gotoFaq() {
        m_dlDrawerLayout.closeDrawers();
        Intent intent = new Intent(this, FaqNoticeActivity.class);
        intent.putExtra("type", 0);
        startActivity(intent);
    }
    @Override
    public void gotoCenter() {
        m_dlDrawerLayout.closeDrawers();
        Intent intent = new Intent(this, QuestionActivity.class);
        startActivity(intent);
    }
    @Override
    public void gotoRecommendToSns() {
        m_dlDrawerLayout.closeDrawers();
        showShareLly();
    }

    @Override
    public void gotoLogout() {
        UserManager.getInstance().releaseUserData();
        UserManager.getInstance().saveData(this,"","","","");
        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public boolean m_bGoVitaWiki = false;
    public void gotoVitaWiki() {
        m_bGoVitaWiki = true;
        pager.setCurrentItem(3);
    }
    public void setFamilyPhotoPage(int pos) {
        if (frag_recom != null)
            frag_recom.setTextLabel(pos);
    }
    public void gotoDetailEvent(int pos) {
        if (frag_event != null)
            frag_event.gotoDetailEvent(pos);
    }
    public void setReviewCategory(int pos) {
        if (frag_review != null)
            frag_review.setReviewCategory(pos);
    }
    public void setReviewDetailCategory(int[] pos) {
        if (frag_review != null)
            frag_review.setReviewDetailCategory(pos);
    }
    public void gotoDetailFromWikiTab(int pos) {
        if (frag_wiki != null)
            frag_wiki.gotoDetailFromWikiTab(pos);
    }
    public void gotoReviewtabDetail(int pos) {
        if (frag_review != null)
            frag_review.gotoReviewtabDetail(pos);
    }

    private void shareToFacebook() {
        String title = "[비타미인]으로 초대합니다. by." + UserManager.getInstance().member_name + "님";

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            try {
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setImageUrl(Uri.parse("https://admin.vitamiin.co.kr/data/file/company/vitamiin_icon.png"))
                        .setContentTitle(title)
                        .setContentDescription(title + "\n 솔직한 후기만 있는 비타미인에서 다양한 제품정보와 리뷰, 유익한 정보들을 확인해보세요!")
                        .setContentUrl(Uri.parse("https://fb.me/1100147390090527"))
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
            properties.put("_activity", "it");

            final KakaoLink kakaoLink = KakaoLink.getKakaoLink(this);
            final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink
                    .createKakaoTalkLinkMessageBuilder();

            String title = "[비타미인]으로 초대합니다. by." + UserManager.getInstance().member_name + "님";
            kakaoTalkLinkMessageBuilder.addText(title + "\n 솔직한 후기만 있는 비타미인에서 다양한 제품정보와 리뷰, 유익한 정보들을 확인해보세요!")
                    .addInWebLink("http://vitamiin.co.kr")
                    .addImage("https://admin.vitamiin.co.kr/data/file/company/vitamiin_icon.png", 100, 100)
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
            case R.id.imv_setting:
                m_dlDrawerLayout.openDrawer(Gravity.START);
                break;
            case R.id.imv_mypage:
                intent = new Intent(this, MyPageActivity.class);
                intent.putExtra("mb_id", UserManager.getInstance().member_id);
                intent.putExtra("mb_nick", UserManager.getInstance().member_nick_name);
                intent.putExtra("mb_photo", UserManager.getInstance().arr_profile_photo_URL.get(0));
                startActivityForResult(intent, 900);
                break;
            case R.id.rly_search:
                intent = new Intent(this, SearchInitActivity.class);
                intent.putExtra("isInitSearch", true);
                startActivity(intent);
                break;
            case R.id.imv_kakao:
                shareToKakaoTalk();
                hideShareLly();
                break;
            case R.id.imv_facebook:
                shareToFacebook();
                hideShareLly();
                break;
            case R.id.imv_BI_logo:
//                gotoRecommendToSns();
                pager.setCurrentItem(0);
                break;
        }
    }

    Handler m_hndBackKey = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0)
                m_bFinish = false;
        }
    };

    @Override
    public void onBackPressed() {
        if(m_llyShare.getVisibility()==View.VISIBLE){
            hideShareLly();
        }else {
            if(m_dlDrawerLayout.isDrawerOpen(Gravity.START)){
                m_dlDrawerLayout.closeDrawers();
            }else {
                if (frag_recom != null && frag_recom.m_llyRecommendProduct.getVisibility() == View.VISIBLE) {
                    frag_recom.m_llyMain.setVisibility(View.VISIBLE);
                    frag_recom.m_llyRecommend.setVisibility(View.GONE);
                    frag_recom.m_llyRecommendProduct.setVisibility(View.GONE);
                } else {
                    if (!m_bFinish) {
                        m_bFinish = true;
                        Toast.makeText(this,
                                getResources().getString(R.string.app_finish_message),
                                Toast.LENGTH_SHORT).show();
                        m_hndBackKey.sendEmptyMessageDelayed(0, 2000);
                    } else {// 앱을 종료한다.
                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    }
                }
            }
        }
    }

    private class GetVersionCode extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String newVersion = null;
            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName() + "&hl=kr").timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return newVersion;
        }

        @Override
        protected void onPostExecute(String _onlineVersion) {
            super.onPostExecute(_onlineVersion);
            if (_onlineVersion != null && !_onlineVersion.isEmpty()) {

                onlineVersion = _onlineVersion;
                UserManager.getInstance().onlineVesion = _onlineVersion;

                if (Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)) {
                    new NoticeDialog(mContext, "업데이트 알림", "비타미인 앱 업데이트가 가능합니다.\ncurrentVersion: " + currentVersion + "\nonlineVersion: " + _onlineVersion, "업데이트하러 가기", false, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getString(R.string.applicationId))));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getString(R.string.applicationId))));
                            }
                        }
                    }).show();
                }
            }
            Log.d("update", "Current version " + currentVersion + "playstore version " + onlineVersion);
        }
    }

    @Override
    public void onParseJSon(JSONObject j_source) {
        try {
            JSONObject result = j_source.getJSONObject(Net.NET_VALUE_RESULT);
            Const.g_nDeviceNo = result.getInt(Net.NET_VALUE_DEVICE_NO);
        }
        catch (JSONException e) {e.printStackTrace();}
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Net.THREAD_REQUEST_END) {
                processForNetEnd();}
        }
    };

    private void processForNetEnd() {
//        rotateLoading.stop();
        HttpRequester.getInstance().stopNetThread();

        int resultCode = HttpRequester.getInstance().getResultCode();
        String strMsg = HttpRequester.getInstance().getResultMsg();

        if (resultCode == Net.CONNECTION_SUCCSES) {
        } else {
            if (!"".equals(strMsg))
                Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();
        }
    }
}
