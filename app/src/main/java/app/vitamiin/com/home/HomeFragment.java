package app.vitamiin.com.home;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
//import android.support.v4.view.ViewPager;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.bumptech.glide.Glide;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import app.vitamiin.com.Model.GoodInfo;
import app.vitamiin.com.Model.WikiInfo;
import app.vitamiin.com.R;
import app.vitamiin.com.common.FoodImageSlidingAdapter;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.setting.QuestionActivity;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;

public class HomeFragment extends Fragment implements View.OnClickListener, OnParseJSonListener {
    MainActivity m_MainAct;
    Fragment m_fragment =this;
    int m_nResultType = 200;
    NetUtil mNetUtil;
    long lastServerConBySecond = 0;

    PullRefreshLayout layout;
    LinearLayout m_llyRank1, m_llyRank2, m_llyRank3;
//    LinearLayout m_llyRecent1, m_llyRecent2, m_llyRecent3;
    RelativeLayout m_rlyReview, m_rlyFeature1, m_rlyFeature2, m_rlyFeature3, m_rlyFeature4;
    TextView m_tvFeatureA, m_tvFeatureB, m_tvFeatureC, m_tvFeatureD;

    String strPerson;
    int user_age;

    ArrayList<GoodInfo> m_mainList = new ArrayList<>();
    WikiInfo m_power_review = new WikiInfo();
    int[] arrType = {0,0};

    private ArrayList<WikiInfo> arrEventList = new ArrayList<>();

    int position_event = 0;

    ViewPager m_uiVwpShopPhoto;
    ViewPager.OnPageChangeListener m_listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            position_event = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };
    CirclePageIndicator mHelpIndicator;
    private ArrayList<String> m_arrPhotoName = new ArrayList<>();

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        m_MainAct = (MainActivity) getActivity();

        mNetUtil = new NetUtil();
        initView(rootView);

        m_MainAct.m_LockView = false;
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getView() != null) {
                if(lastServerConBySecond == 0 || System.currentTimeMillis() - lastServerConBySecond> 600000) {
                    m_MainAct.showProgress();
                    m_MainAct.m_LockView = true;
                    m_mainList.clear();
                    connectServer();
                }
                doAutoEvent();
            } else {
                Thread t = new Thread() {
                    public void run() {
                        while (true) {
                            if (getView() != null) {
//                                m_mainList.clear();
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
            if (msg.what == 0) {   // Message id 가 0 이면
                if(lastServerConBySecond == 0 || System.currentTimeMillis() - lastServerConBySecond > 600000) {
                    m_MainAct.showProgress();
                    m_MainAct.m_LockView = true;
                    m_mainList.clear();
                    connectServer();
                }
            }
            doAutoEvent();
        }
    };

    private void initView(View v) {
        v.findViewById(R.id.lly_link_to_instagram).setOnClickListener(this);
        v.findViewById(R.id.lly_link_to_facebook).setOnClickListener(this);
        v.findViewById(R.id.lly_link_to_naverblog).setOnClickListener(this);

        v.findViewById(R.id.tv_raking_more).setOnClickListener(this);
//        v.findViewById(R.id.tv_search_more).setOnClickListener(this);

        v.findViewById(R.id.lly_product_family).setOnClickListener(this);
        v.findViewById(R.id.lly_functionality).setOnClickListener(this);
        v.findViewById(R.id.lly_target_type).setOnClickListener(this);

        v.findViewById(R.id.tv_power_review_more).setOnClickListener(this);
        v.findViewById(R.id.tv_expert).setOnClickListener(this);
        v.findViewById(R.id.lly_praise).setOnClickListener(this);
        v.findViewById(R.id.lly_direct_question).setOnClickListener(this);

        m_llyRank1 = (LinearLayout) v.findViewById(R.id.ic_ranking_1);
        m_llyRank2 = (LinearLayout) v.findViewById(R.id.ic_ranking_2);
        m_llyRank3 = (LinearLayout) v.findViewById(R.id.ic_ranking_3);
//        m_llyRecent1 = (LinearLayout) v.findViewById(R.id.ic_recent_1);
//        m_llyRecent2 = (LinearLayout) v.findViewById(R.id.ic_recent_2);
//        m_llyRecent3 = (LinearLayout) v.findViewById(R.id.ic_recent_3);
        m_rlyReview = (RelativeLayout) v.findViewById(R.id.ic_power_review);
        m_rlyFeature1 = (RelativeLayout) v.findViewById(R.id.ic_feature_1);
        m_rlyFeature2 = (RelativeLayout) v.findViewById(R.id.ic_feature_2);
        m_rlyFeature3 = (RelativeLayout) v.findViewById(R.id.ic_feature_3);
        m_rlyFeature4 = (RelativeLayout) v.findViewById(R.id.ic_feature_4);
        m_tvFeatureA = (TextView) v.findViewById(R.id.tv_featureA);
        m_tvFeatureB = (TextView) v.findViewById(R.id.tv_featureB);
        m_tvFeatureC = (TextView) v.findViewById(R.id.tv_featureC);
        m_tvFeatureD = (TextView) v.findViewById(R.id.tv_featureD);

        m_llyRank1.setOnClickListener(this);
        m_llyRank2.setOnClickListener(this);
        m_llyRank3.setOnClickListener(this);
//        m_llyRecent1.setOnClickListener(this);
//        m_llyRecent2.setOnClickListener(this);
//        m_llyRecent3.setOnClickListener(this);
        m_rlyReview.setOnClickListener(this);
        m_rlyFeature1.setOnClickListener(this);
        m_rlyFeature2.setOnClickListener(this);
        m_rlyFeature3.setOnClickListener(this);
        m_rlyFeature4.setOnClickListener(this);

        layout = (PullRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        layout.setRefreshStyle(PullRefreshLayout.STYLE_SMARTISAN);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                if(lastServerConBySecond == 0 || System.currentTimeMillis() - lastServerConBySecond > 600000) {
                    m_mainList.clear();
                    set_tvABCD();
                    connectServer();
//                }
                layout.setRefreshing(false);
            }
        });

        set_tvABCD();

        if(m_mainList.size()==10) {
            LoadView(m_llyRank1, m_mainList.get(0), true, 1, false, false);
            LoadView(m_llyRank2, m_mainList.get(1), true, 2, false, false);
            LoadView(m_llyRank3, m_mainList.get(2), true, 3, false, false);
//            LoadView(m_llyRecent1, m_mainList.get(3), false, 1, false, true);
//            LoadView(m_llyRecent2, m_mainList.get(4), false, 1, false, true);
//            LoadView(m_llyRecent3, m_mainList.get(5), false, 1, false, true);
            LoadView(m_rlyReview, m_power_review);
            LoadView(m_rlyFeature1, m_mainList.get(6), false, 1, true, false);
            LoadView(m_rlyFeature2, m_mainList.get(7), false, 1, true, false);
            LoadView(m_rlyFeature3, m_mainList.get(8), false, 1, true, false);
            LoadView(m_rlyFeature4, m_mainList.get(9), false, 1, true, false);
        }

        m_uiVwpShopPhoto = (ViewPager) v.findViewById(R.id.vwp_shop_photo);
        m_uiVwpShopPhoto.setOnClickListener(this);
        mHelpIndicator = (CirclePageIndicator) v.findViewById(R.id.cpi_photo);
        mHelpIndicator.setOnPageChangeListener(m_listener);
    }

    private void set_tvABCD(){
        if(UserManager.getInstance().HealthBaseInfo.get(0).member_interest_health.equals("")
                || UserManager.getInstance().HealthBaseInfo.get(0).member_interest_health.length() == 0)
            m_tvFeatureA.setText("관심건강 분야 입력 안함");
        else
            m_tvFeatureA.setText(getResources().getStringArray(R.array.array_interest_health)[Integer.valueOf(UserManager.getInstance().HealthBaseInfo.get(0).member_interest_health.split(",")[0])].replace("\n",""));

        if(UserManager.getInstance().HealthBaseInfo.get(0).member_prefer_healthfood.equals("")
                || UserManager.getInstance().HealthBaseInfo.get(0).member_prefer_healthfood.length() == 0)
            m_tvFeatureB.setText("선호 건강식품 입력 안함");
        else
            m_tvFeatureB.setText(getResources().getStringArray(R.array.array_prefer_healthfood)[Integer.valueOf(UserManager.getInstance().HealthBaseInfo.get(0).member_prefer_healthfood.split(",")[0])].replace("\n",""));

        GregorianCalendar cal = new GregorianCalendar();
        user_age = cal.get(Calendar.YEAR) - Integer.parseInt((UserManager.getInstance().member_birth).substring(0, 4)) + 1;

        strPerson = UserManager.getInstance().member_sex == 0 ? ",남성":",여성";
        if(user_age<4){
            strPerson = "영유아" + strPerson;
            arrType[0] = 8;
        }else if(user_age<10){
            strPerson = "어린이" + strPerson;
            arrType[0] = 9;
        }else if(user_age<20){
            strPerson = "청소년" + strPerson;
            arrType[0] = 1;
        }else if(user_age<50){
            strPerson = "성인" + strPerson;
            arrType[0] = 4 + UserManager.getInstance().member_sex;
        }else{
            strPerson = "노인" + strPerson;
            arrType[0] = 6 + UserManager.getInstance().member_sex;
        }
        m_tvFeatureC.setText(strPerson + " 추천");

        if(UserManager.getInstance().HealthBaseInfo.get(0).member_pregnant == 0) {
            m_tvFeatureD.setText("임산부 추천");
            arrType[1] = 10;
        } else if(UserManager.getInstance().HealthBaseInfo.get(0).member_examinee == 0) {
            m_tvFeatureD.setText("수험생 추천");
            arrType[1] = 2;
        } else if(UserManager.getInstance().HealthBaseInfo.get(0).member_lactating == 0) {
            m_tvFeatureD.setText("수유부 추천");
            arrType[1] = 11;
        } else if(UserManager.getInstance().HealthBaseInfo.get(0).member_climacterium == 0) {
            m_tvFeatureD.setText("갱년기 추천");
            arrType[1] = 3;
        } else {
            m_tvFeatureD.setText(strPerson + " 추천");
            arrType[1] = arrType[0];
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        Uri uri;
        switch (v.getId()) {
            case R.id.tv_raking_more:
                intent = new Intent(getActivity(), GoodListActivity.class);
                intent.putExtra("list_name", "랭킹 제품");
                intent.putExtra("isRank", true);
                startActivity(intent);
                break;


//            case R.id.tv_search_more:
//                intent = new Intent(m_MainAct, SearchInitActivity.class);
//                intent.putExtra("isInitSearch", true);
//                startActivity(intent);
//                break;
            case R.id.lly_product_family:
                intent = new Intent(m_MainAct, SearchInitActivity.class);
                intent.putExtra("isInitSearch", true);
                intent.putExtra("quicksearch", 1);
                startActivity(intent);
                break;
            case R.id.lly_functionality:
                intent = new Intent(m_MainAct, SearchInitActivity.class);
                intent.putExtra("isInitSearch", true);
                intent.putExtra("quicksearch", 2);
                startActivity(intent);
                break;
            case R.id.lly_target_type:
                intent = new Intent(m_MainAct, SearchInitActivity.class);
                intent.putExtra("isInitSearch", true);
                intent.putExtra("quicksearch", 3);
                startActivity(intent);
                break;


            case R.id.tv_power_review_more:
                m_MainAct.gotoVitaWiki();
                break;
            case R.id.tv_expert:
                final Dialog mDlg_builder = new Dialog(getActivity());
                mDlg_builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mDlg_builder.getWindow().setBackgroundDrawable(
                        new ColorDrawable(android.graphics.Color.TRANSPARENT));
                mDlg_builder.setContentView(R.layout.popup_introduce_expert);
                mDlg_builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {}
                });
                final LinearLayout lly = (LinearLayout) mDlg_builder.findViewById(R.id.lly);
                lly.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDlg_builder.cancel();
                    }
                });

//                final ImageView imageView = new ImageView(getActivity());
//                Glide.with(m_MainAct).load(R.drawable.image_expert).into(imageView);
//                imageView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        mDlg_builder.cancel();
//                    }
//                });
//                mDlg_builder.addContentView(imageView, new RelativeLayout.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT));
                mDlg_builder.show();
                mDlg_builder.setCanceledOnTouchOutside(true);
                break;
            case R.id.lly_praise:
//                Toast.makeText(getActivity(), "칭찬하기", Toast.LENGTH_SHORT).show();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getString(R.string.applicationId))));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getString(R.string.applicationId))));
                }
                break;
            case R.id.ic_ranking_1:
                gotoDetail(m_mainList.get(0));
                break;
            case R.id.ic_ranking_2:
                gotoDetail(m_mainList.get(1));
                break;
            case R.id.ic_ranking_3:
                gotoDetail(m_mainList.get(2));
                break;
//            case R.id.ic_recent_1:
//                gotoDetail(m_mainList.get(3));
//                break;
//            case R.id.ic_recent_2:
//                gotoDetail(m_mainList.get(4));
//                break;
//            case R.id.ic_recent_3:
//                gotoDetail(m_mainList.get(5));
//                break;
            case R.id.ic_power_review:
//                intent = new Intent(getActivity(), DetailPowerActivity.class);
//                intent.putExtra("info", m_power_review);
//                m_MainAct.startActivityForResult(intent, 602);
                Intent webOpen = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(m_power_review._content));
                startActivityForResult(webOpen, 602);
                break;
            case R.id.ic_feature_1:
                gotoDetail(m_mainList.get(6));
                break;
            case R.id.ic_feature_2:
                gotoDetail(m_mainList.get(7));
                break;
            case R.id.ic_feature_3:
                gotoDetail(m_mainList.get(8));
                break;
            case R.id.ic_feature_4:
                gotoDetail(m_mainList.get(9));
                break;
            case R.id.lly_direct_question:
                intent = new Intent(getActivity(), QuestionActivity.class);
                startActivity(intent);
                break;
            case R.id.lly_link_to_instagram:
                uri = Uri.parse("http://instagram.com/_u/vitamiin_");
                intent = new Intent(Intent.ACTION_VIEW, uri);

                intent.setPackage("com.instagram.android");

                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/vitamiin_")));
                }
                break;
            case R.id.lly_link_to_facebook:
                //vitamiin.by.7AM
                String FACEBOOK_URL = "https://www.facebook.com/vitamiin.by.7AM/";
                String FACEBOOK_PAGE_ID = "YourPageName";

                intent = new Intent(Intent.ACTION_VIEW);
                PackageManager packageManager = m_MainAct.getPackageManager();
                try {
                    int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
                    if (versionCode >= 3002850) { //newer versions of fb app
                        intent.setData(Uri.parse("fb://facewebmodal/f?href=" + FACEBOOK_URL));
                    } else { //older versions of fb app
                        intent.setData(Uri.parse("fb://page/" + FACEBOOK_PAGE_ID));
                    }
                    startActivity(intent);
                } catch (PackageManager.NameNotFoundException e) {
                    intent.setData(Uri.parse(FACEBOOK_URL));
                    startActivity(intent);
                }
                break;
            case R.id.lly_link_to_naverblog:
                intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://blog.naver.com/vitamiin_blog"));
                startActivity(intent);
                break;
            case R.id.vwp_shop_photo:
                WikiInfo info = arrEventList.get(position_event);

                intent = new Intent(getActivity(), DetailEventActivity.class);
                intent.putExtra("info", info);
                m_MainAct.startActivity(intent);
        }
    }

    public void updateHomeTabPowerRevieItem(int view_cnt, int like_cnt, int comment_cnt) {
        m_power_review._view_cnt = view_cnt;
        m_power_review._like_cnt = like_cnt;
        m_power_review._comment_cnt = comment_cnt;
        LoadView(m_rlyReview, m_power_review);
    }

    private void gotoDetail(GoodInfo info) {
        Intent intent = new Intent(m_MainAct, DetailGoodActivity.class);
        intent.putExtra("info", info);
        startActivity(intent);
    }

    public void connectServer() {
        lastServerConBySecond = System.currentTimeMillis();

        String[] paramFields = new String[]{Net.NET_POST_FIELD_ACT,
                Net.NET_POST_FIELD_REQUEST};
        String[] paramValues = null;

        JSONObject w_objJSonData = new JSONObject();
        try {
            w_objJSonData.put(Net.NET_VALUE_MEMBER_ID, UserManager.getInstance().member_id);
            w_objJSonData.put("type1", arrType[0]);
            w_objJSonData.put("type2", arrType[1]);

            paramValues = new String[]{
                    Net.POST_FIELD_ACT_MAINGOOD,
                    w_objJSonData.toString()};
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
        HttpRequester.getInstance().stopNetThread();

        int resultCode = HttpRequester.getInstance().getResultCode();
        String strMsg = HttpRequester.getInstance().getResultMsg();

        if (resultCode == Net.CONNECTION_SUCCSES) {
            if (m_nResultType == 200) {
                LoadView(m_llyRank1, m_mainList.get(0), true, 1, false, false);
                LoadView(m_llyRank2, m_mainList.get(1), true, 2, false, false);
                LoadView(m_llyRank3, m_mainList.get(2), true, 3, false, false);
//                LoadView(m_llyRecent1, m_mainList.get(3), false, -1, false, true);
//                LoadView(m_llyRecent2, m_mainList.get(4), false, -1, false, true);
//                LoadView(m_llyRecent3, m_mainList.get(5), false, -1, false, true);
                LoadView(m_rlyReview, m_power_review);
                LoadView(m_rlyFeature1, m_mainList.get(6), false, -1, true, false);
                LoadView(m_rlyFeature2, m_mainList.get(7), false, -1, true, false);
                LoadView(m_rlyFeature3, m_mainList.get(8), false, -1, true, false);
                LoadView(m_rlyFeature4, m_mainList.get(9), false, -1, true, false);

                doAutoEvent();
            }
        } else {
            if (!"".equals(strMsg)) {
                Toast.makeText(m_MainAct, strMsg, Toast.LENGTH_SHORT).show();}
        }
        m_MainAct.closeProgress();
        m_MainAct.m_LockView = false;
    }

    @Override
    public void onParseJSon(JSONObject j_source) {
        try {
            m_nResultType = j_source.getInt(Net.NET_VALUE_CODE); //
            JSONObject result = j_source.getJSONObject(Net.NET_VALUE_RESULT);

            JSONArray arr = result.getJSONArray("ranking");
            for (int i = 0; i < arr.length(); i++)
                m_mainList.add(mNetUtil.parseGood(arr.getJSONObject(i)));

            arr = result.getJSONArray("recent");
            for (int i = 0; i < arr.length(); i++)
                m_mainList.add(mNetUtil.parseGood(arr.getJSONObject(i)));

            m_power_review = mNetUtil.parseWiki(result.getJSONObject("review"));

            m_mainList.add(mNetUtil.parseGood(result.getJSONObject("func1")));
            m_mainList.add(mNetUtil.parseGood(result.getJSONObject("func2")));
            m_mainList.add(mNetUtil.parseGood(result.getJSONObject("age1")));
            m_mainList.add(mNetUtil.parseGood(result.getJSONObject("ill1")));

            m_arrPhotoName.clear();
            arr = result.getJSONArray("event");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj_evt = arr.getJSONObject(i);
                WikiInfo info = new NetUtil().parseWiki(obj_evt);
                arrEventList.add(info);
                m_arrPhotoName.add(info._imagePath);
            }
        } catch (JSONException e) {e.printStackTrace();}
    }

    private void doAutoEvent() {
        FoodImageSlidingAdapter adapter = new FoodImageSlidingAdapter(m_MainAct, m_arrPhotoName, 3);
        m_uiVwpShopPhoto.setAdapter(adapter);
        mHelpIndicator.setViewPager(m_uiVwpShopPhoto);

        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            public void run() {
                position_event = position_event==2 ? 0:position_event + 1;
                m_uiVwpShopPhoto.setCurrentItem(position_event, true);
            }
        };

        if(m_MainAct.m_Timer == null) {
            m_MainAct.m_Timer = new Timer();
            m_MainAct.m_Timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(update);
                }
            }, 1000, 4000);
        }
    }

    private void LoadView(View v, WikiInfo info) {
        v.setVisibility(View.VISIBLE);

        ImageView m_imvImage = (ImageView) v.findViewById(R.id.imv_good);
        Glide.with(m_MainAct).load(Net.URL_SERVER1 + info._imagePath).into(m_imvImage);

        ((TextView) v.findViewById(R.id.tv_name)).setText(String.valueOf(info.title));
        ((TextView) v.findViewById(R.id.tv_view_cnt)).setText(String.valueOf(info._view_cnt));
        ((TextView) v.findViewById(R.id.tv_review_cnt)).setText(String.valueOf(info._comment_cnt));
        ((TextView) v.findViewById(R.id.tv_like_cnt)).setText(String.valueOf(info._like_cnt));
    }

    private void LoadView(View v, GoodInfo info, Boolean isRank, int rankNum, Boolean isFunc, Boolean isRecent) {
        v.setVisibility(View.VISIBLE);

        if (isRank) {
            v.findViewById(R.id.rly_rank).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tv_rank_num)).setText(String.valueOf(rankNum));
        }else if (isRecent) {
            v.findViewById(R.id.rly_rank).setVisibility(View.GONE);
        }else if (isFunc) {
            ((TextView) v.findViewById(R.id.tv_rate)).setText(String.valueOf(info._rate));
        }

        ImageView m_imvImage = (ImageView) v.findViewById(R.id.imv_good);
        Glide.with(m_MainAct).load(Net.URL_SERVER1 + info._imagePath).into(m_imvImage);

        ((TextView) v.findViewById(R.id.tv_name)).setText(info._name);
        ((TextView) v.findViewById(R.id.tv_business)).setText(info._business);

        ImageView[] arr_mimvStars = new ImageView[5];
        arr_mimvStars[0] = (ImageView) v.findViewById(R.id.imv_star1);
        arr_mimvStars[1] = (ImageView) v.findViewById(R.id.imv_star2);
        arr_mimvStars[2] = (ImageView) v.findViewById(R.id.imv_star3);
        arr_mimvStars[3] = (ImageView) v.findViewById(R.id.imv_star4);
        arr_mimvStars[4] = (ImageView) v.findViewById(R.id.imv_star5);

        for(int i = 0; i < 5; i++)
            if(info._rate < i + 0.5)
                Glide.with(m_MainAct).load(R.drawable.ic_star_empty).into(arr_mimvStars[i]);
            else if(info._rate < i+1)
                Glide.with(m_MainAct).load(R.drawable.ic_star_half).into(arr_mimvStars[i]);
            else
                Glide.with(m_MainAct).load(R.drawable.ic_star_one).into(arr_mimvStars[i]);
    }
}