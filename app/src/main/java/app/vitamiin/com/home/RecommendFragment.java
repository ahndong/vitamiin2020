package app.vitamiin.com.home;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.vitamiin.com.Adapter.FoodListAdapter;
import app.vitamiin.com.Model.FoodInfoDeux;
import app.vitamiin.com.NoticeDialog;
import app.vitamiin.com.R;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.common.UILoader;
import app.vitamiin.com.common.UserHealthBase;
import app.vitamiin.com.common.UserHealthDetail;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;
import app.vitamiin.com.login.DetailLife1of3Activity;
import cn.lightsky.infiniteindicator.InfiniteIndicator;
import cn.lightsky.infiniteindicator.indicator.CircleIndicator;
import cn.lightsky.infiniteindicator.page.Page;
import jp.wasabeef.blurry.Blurry;

public class RecommendFragment extends Fragment implements View.OnClickListener, OnParseJSonListener, NetUtil.PhotoDownCompleteCallback {
    MainActivity m_MainAct;
    Fragment m_fragment =this;
    int m_nResultType = 200;
    NetUtil mNetUtil;
    NetUtil.connectAndgetServer mNetUtilConnetServer;

    int m_nConnectType;

    LinearLayout m_llyRecommendProduct, m_llyMain, m_llyRecommend;
    RelativeLayout m_rlyView, m_rlyFrg;
    ImageView m_imvRecom1, m_imvRecom2, m_imvRecom3, m_imvForBlur;
    TextView m_tvRecom1, m_tvRecom2, m_tvRecom3, m_tvRecommendBtn, m_tvEditProfile;
    InfiniteIndicator mDefaultIndicator;

    UserHealthBase m_userHealthBase = new UserHealthBase();
    UserHealthDetail m_userHealthDetail = new UserHealthDetail();
    ArrayList<String> m_arrPhotoName = new ArrayList<>();
    FoodListAdapter m_food_adapter;

    int m_currentPage = 1;
    int m_nMaxPage = 1;
    public int REALscrollToHereIndex = 0;
    boolean m_bLockListView = false;
    int current_family_index = 0, selected_index = 0;

    public static RecommendFragment newInstance() {
        return new RecommendFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recommend, container, false);
        m_MainAct = (MainActivity) getActivity();
        mNetUtil = new NetUtil();
        mNetUtilConnetServer = mNetUtil.new connectAndgetServer(m_MainAct);

        initView(rootView);

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getView() != null) {
                m_llyMain.setVisibility(View.VISIBLE);
                m_llyRecommend.setVisibility(View.GONE);
                selected_index = 0;

                m_food_adapter.clear();
                m_arrPhotoName.clear();
                while (m_arrPhotoName.size() < UserManager.getInstance().HealthBaseInfo.size())
                    m_arrPhotoName.add(null);

                Blurry.with(m_MainAct)
                        .radius(25)
                        .sampling(2)
                        .async()
                        .capture(m_llyMain)
                        .into(m_imvForBlur);
                getView().findViewById(R.id.lly_block).setVisibility(View.VISIBLE);

//                if (!UserManager.getInstance().is_familyinfo_loaded) {
//                    m_MainAct.showProgress();
//                    m_nConnectType = NetUtil.apis_GET_FAMILY;
//                    mNetUtilConnetServer.connectServer(m_fragment, handler, m_nConnectType, "");
//                }else {
//                    updatePhotoListView_All();
//                }
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
            if (msg.what == 0) {   // Message id 가 0 이면
                m_llyMain.setVisibility(View.VISIBLE);
                m_llyRecommend.setVisibility(View.GONE);
                selected_index = 0;

                m_food_adapter.clear();
                m_arrPhotoName.clear();
                while (m_arrPhotoName.size() < UserManager.getInstance().HealthBaseInfo.size())
                    m_arrPhotoName.add(null);

                Blurry.with(m_MainAct)
                        .radius(25)
                        .sampling(2)
                        .async()
                        .capture(m_llyMain)
                        .into(m_imvForBlur);
                getView().findViewById(R.id.lly_block).setVisibility(View.VISIBLE);

//                if (!UserManager.getInstance().is_familyinfo_loaded) {
//                    m_MainAct.showProgress();
//                    m_nConnectType = NetUtil.apis_GET_FAMILY;
//                    mNetUtilConnetServer.connectServer(m_fragment, handler, m_nConnectType, "");
//                }else {
//                    updatePhotoListView_All();
//                }
            }
        }
    };

    private void initView(View v) {
        m_rlyFrg = (RelativeLayout)v.findViewById(R.id.rly_frg_rec);
        m_llyMain = (LinearLayout) v.findViewById(R.id.lly_main);
        m_imvForBlur = (ImageView)v.findViewById(R.id.imv_for_blur);
        m_arrPhotoName.clear();
        m_tvRecommendBtn = (TextView)v.findViewById(R.id.tv_recommend);
        m_tvRecommendBtn.setOnClickListener(this);
        m_tvEditProfile = (TextView)v.findViewById(R.id.tv_edit_profile);
        m_tvEditProfile.setOnClickListener(this);
        v.findViewById(R.id.tv_add_family).setOnClickListener(this);
        v.findViewById(R.id.tv_detail_survey).setOnClickListener(this);
        v.findViewById(R.id.tv_detail).setOnClickListener(this);

        m_rlyView = (RelativeLayout) v.findViewById(R.id.rly_family);
        m_llyRecommend = (LinearLayout) v.findViewById(R.id.lly_recommend);

        m_food_adapter = new FoodListAdapter(m_MainAct, new ArrayList<FoodInfoDeux>());

        v.findViewById(R.id.tv_backtoprofileselect_view).setOnClickListener(this);
        m_llyRecommendProduct = (LinearLayout) v.findViewById(R.id.lly_recom_product);
        m_imvRecom1 = (ImageView) v.findViewById(R.id.imv_recom_1);
        m_imvRecom2 = (ImageView) v.findViewById(R.id.imv_recom_2);
        m_imvRecom3 = (ImageView) v.findViewById(R.id.imv_recom_3);
        m_tvRecom1 = (TextView) v.findViewById(R.id.tv_recom_1);
        m_tvRecom2 = (TextView) v.findViewById(R.id.tv_recom_2);
        m_tvRecom3 = (TextView) v.findViewById(R.id.tv_recom_3);
        v.findViewById(R.id.rly_recom_1).setOnClickListener(this);
        v.findViewById(R.id.rly_recom_2).setOnClickListener(this);
        v.findViewById(R.id.rly_recom_3).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.rly_recom_1:
                intent = new Intent(getContext(), SearchInitActivity.class);
                intent.putExtra("isRecommendList", true);
                intent.putExtra("isInitSearch", false);
                intent.putExtra("filter", "productgroup");
                intent.putExtra("value", String.valueOf(m_food_adapter.getItem(0)._id - 1));

                startActivity(intent);
                break;
            case R.id.rly_recom_2:
                intent = new Intent(getContext(), SearchInitActivity.class);
                intent.putExtra("isRecommendList", true);
                intent.putExtra("isInitSearch", false);
                intent.putExtra("filter", "productgroup");
                intent.putExtra("value", String.valueOf(m_food_adapter.getItem(1)._id - 1));

                startActivity(intent);
                break;
            case R.id.rly_recom_3:
                intent = new Intent(getContext(), SearchInitActivity.class);
                intent.putExtra("isRecommendList", true);
                intent.putExtra("isInitSearch", false);
                intent.putExtra("filter", "productgroup");
                intent.putExtra("value", String.valueOf(m_food_adapter.getItem(2)._id - 1));

                startActivity(intent);
                break;
            case R.id.tv_add_family:
                REALscrollToHereIndex = UserManager.getInstance().arr_profile_photo_resID.size();
                //HealthBaseInfo 리스트의 size는 본인+가족 의 수이다. 수를 인덱스로 보내면 그대로 사용하면 된다. (-1을 하지 않고) by 동현
                m_userHealthBase = new UserHealthBase();
                m_userHealthBase.member_no = UserManager.getInstance().member_no;
                m_userHealthDetail = new UserHealthDetail();
                m_userHealthDetail.member_no = UserManager.getInstance().member_no;

                intent = new Intent(getActivity(), ManageProfileActivity.class);
                intent.putExtra("fam_index", UserManager.getInstance().HealthBaseInfo.size());
                intent.putExtra("HealthBase", m_userHealthBase);
                intent.putExtra("HealthDetail", m_userHealthDetail);
                intent.putExtra("isAdd", true);

                m_arrPhotoName.add(null);
                UserManager.getInstance().arr_profile_photo_URL.add(null);
                UserManager.getInstance().arr_profile_photo_file.add(null);
                UserManager.getInstance().arr_profile_photo_bitmap.add(null);
                UserManager.getInstance().arr_profile_photo_resID.add(-1);

                (getActivity()).startActivityForResult(intent, 500);
                break;
            case R.id.tv_edit_profile:
                REALscrollToHereIndex = current_family_index;

                intent = new Intent(getActivity(), ManageProfileActivity.class);
                intent.putExtra("fam_index", current_family_index);
                intent.putExtra("HealthBase", UserManager.getInstance().HealthBaseInfo.get(current_family_index));
                intent.putExtra("HealthDetail", UserManager.getInstance().HealthDetailInfo.get(current_family_index));
                intent.putExtra("fromReco", true);
                (getActivity()).startActivityForResult(intent, 501);
                break;
            case R.id.tv_detail_survey:
                intent = new Intent(getActivity(), DetailLife1of3Activity.class);
                intent.putExtra("fam_index", current_family_index);
                intent.putExtra("_userHealthDetail", UserManager.getInstance().HealthDetailInfo.get(current_family_index));
                if(UserManager.getInstance().HealthDetailInfo.get(current_family_index).isset)
                    intent.putExtra("edit_from_recomfragment", true);
                (getActivity()).startActivityForResult(intent, 502);
                break;
            case R.id.tv_recommend:
                final Dialog mDlg_builder = new Dialog(getActivity());
                mDlg_builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mDlg_builder.getWindow().setBackgroundDrawable(
                        new ColorDrawable(android.graphics.Color.TRANSPARENT));
                mDlg_builder.setContentView(R.layout.popup_ready_recommend);
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
                mDlg_builder.show();
                mDlg_builder.setCanceledOnTouchOutside(true);

/*
                if(UserManager.getInstance().HealthDetailInfo.get(current_family_index).isset){
                    m_llyMain.setVisibility(View.GONE);
                    m_llyRecommend.setVisibility(View.VISIBLE);
                    m_llyRecommendProduct.setVisibility(View.VISIBLE);

                    m_currentPage = 1;
                    m_nMaxPage = 5;
                    m_MainAct.showProgress();
                    m_nConnectType = NetUtil.GET_RECOMMEND_FOOD;
                    mNetUtilConnetServer.connectServer(m_fragment, handler, m_nConnectType, current_family_index);
                } else
                    new NoticeDialog(getActivity(), "상세건강 설문 입력", "상세 설문을 하시면 더 정확한\n건강 상태 분석 및 건강기능식품\n 추천을 받으실 수 있습니다.","닫기", true).show();
*/
                break;
            case R.id.tv_detail:
                new NoticeDialog(getActivity(), "상세건강 설문 입력", "상세 설문을 하시면 더 정확한\n건강 상태 분석 및 건강기능식품\n 추천을 받으실 수 있습니다.","닫기", true).show();
                break;
            case R.id.tv_backtoprofileselect_view:
                m_llyMain.setVisibility(View.VISIBLE);
                m_llyRecommend.setVisibility(View.GONE);
                m_llyRecommendProduct.setVisibility(View.GONE);
                break;
        }
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Net.THREAD_REQUEST_END) {
                processForNetEnd();}
        }
    };

    private void processForNetEnd() {
        parseJSON();
        m_MainAct.closeProgress();
        HttpRequester.getInstance().stopNetThread();

        int resultCode = HttpRequester.getInstance().getResultCode();
        String strMsg = HttpRequester.getInstance().getResultMsg();
        // 성공
        if (resultCode == Net.CONNECTION_SUCCSES) {
            if (m_nResultType == 200) {
                if (m_nConnectType == NetUtil.apis_GET_FAMILY) {
//                    updatePhotoListView_All();
                    updatePhotoListView(0);
                    m_tvRecommendBtn.setSelected(UserManager.getInstance().HealthDetailInfo.get(0).isset);
                    UserManager.getInstance().is_familyinfo_loaded = true;
                } else if (m_nConnectType == NetUtil.GET_RECOMMEND_FOOD) {
                    m_tvRecom1.setText("1. " + m_food_adapter.getItem(0)._name);
                    m_tvRecom2.setText("2. " + m_food_adapter.getItem(1)._name);
                    m_tvRecom3.setText("3. " + m_food_adapter.getItem(2)._name);

                    if (m_food_adapter.getItem(0)._imagePath.equals(""))
                        Glide.with(m_MainAct).load(R.drawable.default_review).into(m_imvRecom1);
                    else
                        Glide.with(m_MainAct).load(Net.URL_SERVER1 + m_food_adapter.getItem(0)._imagePath).into(m_imvRecom1);
                    if (m_food_adapter.getItem(1)._imagePath.equals(""))
                        Glide.with(m_MainAct).load(R.drawable.default_review).into(m_imvRecom2);
                    else
                        Glide.with(m_MainAct).load(Net.URL_SERVER1 + m_food_adapter.getItem(1)._imagePath).into(m_imvRecom2);
                    if (m_food_adapter.getItem(2)._imagePath.equals(""))
                        Glide.with(m_MainAct).load(R.drawable.default_review).into(m_imvRecom3);
                    else
                        Glide.with(m_MainAct).load(Net.URL_SERVER1 + m_food_adapter.getItem(2)._imagePath).into(m_imvRecom3);
                }
            }
        } else {
            if (!"".equals(strMsg)) {Toast.makeText(m_MainAct, strMsg, Toast.LENGTH_SHORT).show();}
        }
        m_bLockListView = false;
    }

    JSONObject _jResult;

    @Override
    public void onParseJSon(JSONObject j_source) {_jResult = j_source;}

    public void parseJSON() {
        try {
            m_nResultType = _jResult.getInt(Net.NET_VALUE_CODE); //가족을 최초로 찾아 등록하는 곳
            JSONObject result = _jResult.getJSONObject(Net.NET_VALUE_RESULT); //result에 가족 정보

            if (m_nConnectType == NetUtil.apis_GET_FAMILY) {
                JSONArray arr = result.getJSONArray("family");
                JSONObject obj;
                mNetUtil.setPhotoDownCompleteCallback(this);
                for (int i = 0; i < arr.length(); i++) {
                    obj = arr.getJSONObject(i);

                    UserHealthBase healthBase = new UserHealthBase();
                    healthBase.releaseUserHealthBase();
                    healthBase.isset = obj.getInt("f_isset1") == 1;
                    if (healthBase.isset)
                        healthBase = mNetUtil.transHealthBase_ObjToUM(obj);

                    UserHealthDetail healthDetail = new UserHealthDetail();
                    healthDetail.releaseUserHealthDetail();
                    healthDetail.member_nick_name = "" + obj.getString("f_nick");
                    healthDetail.member_name = "" + obj.getString("f_name");
                    healthDetail.isset = obj.getInt("f_isset2") == 1;
                    if (healthDetail.isset)
                        healthDetail = mNetUtil.transHealthDetail_ObjToUM(obj);

                    UserManager.getInstance().updateHealthInfo(i + 1, healthBase, healthDetail);
                    mNetUtil.setProfilePhoto(m_MainAct, i + 1, obj.getString("f_photo"));
                }
            } else if (m_nConnectType == NetUtil.GET_RECOMMEND_FOOD) {
                m_food_adapter = new FoodListAdapter(m_MainAct, new ArrayList<FoodInfoDeux>());

                JSONObject obj1 = result.getJSONObject("food1");
                FoodInfoDeux info1 = new FoodInfoDeux();
                info1._id = obj1.getInt("id");
                info1._name = obj1.getString("name");
                info1._imagePath = obj1.getString("image");
                m_food_adapter.add(info1);

                JSONObject obj2 = result.getJSONObject("food2");
                FoodInfoDeux info2 = new FoodInfoDeux();
                info2._id = obj2.getInt("id");
                info2._name = obj2.getString("name");
                info2._imagePath = obj2.getString("image");
                m_food_adapter.add(info2);

                JSONObject obj3 = result.getJSONObject("food3");
                FoodInfoDeux info3 = new FoodInfoDeux();
                info3._id = obj3.getInt("id");
                info3._name = obj3.getString("name");
                info3._imagePath = obj3.getString("image");
                m_food_adapter.add(info3);
            }
        } catch (JSONException e) {e.printStackTrace();}
    }

    @Override
    public void updatePhotoListView(Integer famindex) {
        while(m_arrPhotoName.size() < famindex+1)
            m_arrPhotoName.add(null);

//        if (UserManager.getInstance().arr_profile_photo_resID.get(famindex) != -1)
//            m_arrPhotoName.set(famindex, "file://" + new File(fileName_preset + String.valueOf(UserManager.getInstance().arr_profile_photo_resID.get(famindex)) + ".png").getPath());
//        else
            m_arrPhotoName.set(famindex, "file://" + UserManager.getInstance().arr_profile_photo_file.get(famindex).getPath());

        initCircleIndicator();
    }

    public void updatePhotoListView_All() {
        m_arrPhotoName.clear();
        for (int i = 0; i < UserManager.getInstance().HealthBaseInfo.size(); i++) {
//            if (UserManager.getInstance().arr_profile_photo_resID.get(i) != -1)
//                m_arrPhotoName.add("file://" + new File(fileName_preset + String.valueOf(UserManager.getInstance().arr_profile_photo_resID.get(i)) + ".png").getPath());
//            else
                m_arrPhotoName.add("file://" + UserManager.getInstance().arr_profile_photo_file.get(i).getPath());
        }
        initCircleIndicator();
    }

    public void initCircleIndicator() {
        ArrayList<Page> mPageViews;

        mPageViews = new ArrayList<>();

        for (int i = 0; i < m_arrPhotoName.size(); i++) {
            mPageViews.add(new Page("Page" + i, m_arrPhotoName.get(i)));
        }

        m_rlyView.removeAllViews();
        mDefaultIndicator = (InfiniteIndicator) LayoutInflater.from(getActivity()).inflate(R.layout.layout_infiniteindicator, null);
        m_rlyView.addView(mDefaultIndicator);

        mDefaultIndicator.setImageLoader(new UILoader());
//        UILoader m_imageLoader = new UILoader();
//        m_imageLoader.load(getContext(), m_cliPhoto, null);
//        mDefaultIndicator.setImageLoader(m_imageLoader);

        mDefaultIndicator.addPages(mPageViews);
        mDefaultIndicator.setPosition(InfiniteIndicator.IndicatorPosition.Center_Bottom);
        CircleIndicator indicator = (CircleIndicator) mDefaultIndicator.getPagerIndicator();
        indicator.setFillColor(ContextCompat.getColor(m_MainAct, R.color.main_color_1));
        indicator.setStrokeColor(ContextCompat.getColor(m_MainAct, R.color.main_color_2));
        indicator.setPageColor(Color.parseColor("#AAFFFFFF"));
        indicator.setStrokeWidth((float)2.0);
        mDefaultIndicator.setCustomIndicator(indicator);
        mDefaultIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                setTextLabel(position % m_arrPhotoName.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        for (int i = 0; i < REALscrollToHereIndex; i++)
            mDefaultIndicator.scrollOnce();
        setTextLabel(REALscrollToHereIndex);
    }

    public void setTextLabel(int pos) {
        try {
            current_family_index = pos;

            View v = getView();
            m_tvEditProfile.setTag(UserManager.getInstance().HealthBaseInfo.get(pos));
            ((TextView) v.findViewById(R.id.tv_nickname)).setText(UserManager.getInstance().HealthBaseInfo.get(pos).member_nick_name);
            ((TextView) v.findViewById(R.id.tv_name)).setText(UserManager.getInstance().HealthBaseInfo.get(pos).member_name);
            if (UserManager.getInstance().HealthBaseInfo.get(pos).member_sex == 0) {
                ((TextView) v.findViewById(R.id.tv_sex)).setText("남자");
            } else if (UserManager.getInstance().HealthBaseInfo.get(pos).member_sex == 1) {
                ((TextView) v.findViewById(R.id.tv_sex)).setText("여자");
            } else {
                ((TextView) v.findViewById(R.id.tv_sex)).setText("");            }
            ((TextView) v.findViewById(R.id.tv_birthday)).setText(UserManager.getInstance().HealthBaseInfo.get(pos).member_birth);

            m_tvRecommendBtn.setSelected(UserManager.getInstance().HealthDetailInfo.get(pos).isset);
        } catch (Exception e) {e.printStackTrace();}
    }
}