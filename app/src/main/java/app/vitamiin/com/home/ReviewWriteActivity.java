package app.vitamiin.com.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kakao.usermgmt.response.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.Model.GoodInfo;
import app.vitamiin.com.Model.ReviewInfo;
import app.vitamiin.com.NoticeDialog;
import app.vitamiin.com.R;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.common.SelectPhotoManager;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.common.Util;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;
import app.vitamiin.com.login.ListSelectDialog;
import app.vitamiin.com.login.ReviewSelectDialog;
import app.vitamiin.com.setting.FaqNoticeActivity;


public class ReviewWriteActivity extends BaseActivity implements View.OnClickListener, OnParseJSonListener {
    Context m_context = this;
    NetUtil mNetUtil;
    NetUtil.connectAndgetServer mNetUtilConnetServer;
    JSONObject _jResult;
    int m_nResultType = 200;

    int WRITE_REVIEW = 1;
    int m_nConnectType = NetUtil.apis_GET_REVIEW_WRITE_CATEGORY;
    ScrollView m_scvView;

    ImageView m_imvGood;
    ImageView m_imvStar11, m_imvStar12, m_imvStar13, m_imvStar14, m_imvStar15;
    ImageView m_imvStar21, m_imvStar22, m_imvStar23, m_imvStar24, m_imvStar25;
    ImageView m_imvStar31, m_imvStar32, m_imvStar33, m_imvStar34, m_imvStar35;
    ImageView m_imvStar41, m_imvStar42, m_imvStar43, m_imvStar44, m_imvStar45;
    int m_nFuncRate = 0, m_nPriceRate = 0, m_nTakeRate = 0, m_nTasteRate = 0;

    TextView m_tvGoodName, m_tvBusiness;
    EditText m_edtContent, m_edtContent2, m_edtContent3;
    TextView m_tvHash, m_tvCategory, m_tvPeriod, m_tvBuy, m_tvYES, m_tvNO, m_tvPerson;
    int m_nRetake = 1;//1:yes, 0:no
    int m_nBuyPath = -1; //0~
    Integer[] arrintCat = null;
    String[] arrstrCat = null;
    int m_nCategory = -1; //0~
    int m_nPeriod = -1; //0~
    int m_nPerson = -1;
    GoodInfo m_info_gd = new GoodInfo();
    ReviewInfo m_r_info = new ReviewInfo();

    private LinearLayout m_uiLlyPhoto;
    ArrayList<String> oldphotoid = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        m_context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);
        mNetUtil = new NetUtil();
        mNetUtilConnetServer = mNetUtil.new connectAndgetServer(this);

        initView();

        m_r_info = new ReviewInfo();

        if (getIntent().getBooleanExtra("edit", false)) {
//            findViewById(R.id.tv_change).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.tv_title)).setText("리뷰 수정하기");

            m_r_info = (ReviewInfo) getIntent().getSerializableExtra("info");
            updateReview();
            updateGood();

            oldphotoid = (ArrayList<String>) m_r_info.photos_id.clone();
        } else {
            m_info_gd = (GoodInfo) getIntent().getSerializableExtra("info_gd");
            if (m_info_gd != null) {
                setGoodInfo_to_RInfo(m_info_gd);
                updateGood();
            } else {
                findViewById(R.id.lly_before_select).setVisibility(View.VISIBLE);
                findViewById(R.id.lly_after_select).setVisibility(View.GONE);
            }
        }
    }
    private void initView() {
        if (getIntent().getBooleanExtra("edit", false)) {
//            ((TextView)findViewById(R.id.tv_change)).setTextColor(Color.parseColor("#777777"));
            findViewById(R.id.tv_change).setOnClickListener(this);
        }else{
            findViewById(R.id.lly_after_select).setOnClickListener(this);
            findViewById(R.id.tv_change).setOnClickListener(this);
            findViewById(R.id.tv_change1).setOnClickListener(this);
        }
        findViewById(R.id.imv_back).setOnClickListener(this);
        m_imvGood = (ImageView) findViewById(R.id.imv_good);
        m_tvGoodName = (TextView) findViewById(R.id.tv_good);
        m_tvBusiness = (TextView) findViewById(R.id.tv_business);

        m_imvStar11 = (ImageView) findViewById(R.id.imv_star11);
        m_imvStar12 = (ImageView) findViewById(R.id.imv_star12);
        m_imvStar13 = (ImageView) findViewById(R.id.imv_star13);
        m_imvStar14 = (ImageView) findViewById(R.id.imv_star14);
        m_imvStar15 = (ImageView) findViewById(R.id.imv_star15);

        m_imvStar21 = (ImageView) findViewById(R.id.imv_star21);
        m_imvStar22 = (ImageView) findViewById(R.id.imv_star22);
        m_imvStar23 = (ImageView) findViewById(R.id.imv_star23);
        m_imvStar24 = (ImageView) findViewById(R.id.imv_star24);
        m_imvStar25 = (ImageView) findViewById(R.id.imv_star25);

        m_imvStar31 = (ImageView) findViewById(R.id.imv_star31);
        m_imvStar32 = (ImageView) findViewById(R.id.imv_star32);
        m_imvStar33 = (ImageView) findViewById(R.id.imv_star33);
        m_imvStar34 = (ImageView) findViewById(R.id.imv_star34);
        m_imvStar35 = (ImageView) findViewById(R.id.imv_star35);

        m_imvStar41 = (ImageView) findViewById(R.id.imv_star41);
        m_imvStar42 = (ImageView) findViewById(R.id.imv_star42);
        m_imvStar43 = (ImageView) findViewById(R.id.imv_star43);
        m_imvStar44 = (ImageView) findViewById(R.id.imv_star44);
        m_imvStar45 = (ImageView) findViewById(R.id.imv_star45);

        m_imvStar11.setOnClickListener(this);
        m_imvStar12.setOnClickListener(this);
        m_imvStar13.setOnClickListener(this);
        m_imvStar14.setOnClickListener(this);
        m_imvStar15.setOnClickListener(this);

        m_imvStar21.setOnClickListener(this);
        m_imvStar22.setOnClickListener(this);
        m_imvStar23.setOnClickListener(this);
        m_imvStar24.setOnClickListener(this);
        m_imvStar25.setOnClickListener(this);

        m_imvStar31.setOnClickListener(this);
        m_imvStar32.setOnClickListener(this);
        m_imvStar33.setOnClickListener(this);
        m_imvStar34.setOnClickListener(this);
        m_imvStar35.setOnClickListener(this);

        m_imvStar41.setOnClickListener(this);
        m_imvStar42.setOnClickListener(this);
        m_imvStar43.setOnClickListener(this);
        m_imvStar44.setOnClickListener(this);
        m_imvStar45.setOnClickListener(this);

        m_edtContent = (EditText) findViewById(R.id.edt_content);
        m_edtContent2 = (EditText) findViewById(R.id.edt_content2);
        m_edtContent3 = (EditText) findViewById(R.id.edt_content3);

        findViewById(R.id.tv_reg_hash).setOnClickListener(this);
        m_tvHash = (TextView) findViewById(R.id.tv_hash);

        findViewById(R.id.imv_add).setOnClickListener(this);
        findViewById(R.id.imv_add_btn).setOnClickListener(this);

        findViewById(R.id.lly_buy_path).setOnClickListener(this);
        m_tvBuy = (TextView) findViewById(R.id.tv_buy_path);

        m_tvYES = (TextView) findViewById(R.id.tv_yes);
        m_tvNO = (TextView) findViewById(R.id.tv_no);
        m_tvYES.setSelected(true);
        m_tvNO.setSelected(false);
        m_tvYES.setOnClickListener(this);
        m_tvNO.setOnClickListener(this);

        findViewById(R.id.tv_finish).setOnClickListener(this);

        m_uiLlyPhoto = (LinearLayout) findViewById(R.id.lly_photo);

        findViewById(R.id.tv_category).setOnClickListener(this);
        m_tvCategory = (TextView) findViewById(R.id.tv_category);
        findViewById(R.id.lly_period).setOnClickListener(this);
        m_tvPeriod = (TextView) findViewById(R.id.tv_period);
        findViewById(R.id.lly_person).setOnClickListener(this);
        m_tvPerson = (TextView) findViewById(R.id.tv_person);

        m_scvView = (ScrollView) findViewById(R.id.scv_view);
        m_scvView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                hideKeypad();
            }
        });
        m_tvHash.setText("");

        findViewById(R.id.tv_standard).setOnClickListener(this);
    }

    private void updateReview() {
        m_edtContent.setText(m_r_info.content);
        m_edtContent2.setText(m_r_info.content2);
        m_edtContent3.setText(m_r_info.content3);
        m_tvHash.setText(m_r_info.hash_tag);

        m_nFuncRate = m_r_info.func_rate;
        m_nPriceRate = m_r_info.price_rate;
        m_nTakeRate = m_r_info.take_rate;
        m_nTasteRate = m_r_info.taste_rate;

        m_imvStar11.setSelected(false);
        m_imvStar12.setSelected(false);
        m_imvStar13.setSelected(false);
        m_imvStar14.setSelected(false);
        m_imvStar15.setSelected(false);

        if (m_r_info.func_rate >= 1)
            m_imvStar11.setSelected(true);
        if (m_r_info.func_rate >= 2)
            m_imvStar12.setSelected(true);
        if (m_r_info.func_rate >= 3)
            m_imvStar13.setSelected(true);
        if (m_r_info.func_rate >= 4)
            m_imvStar14.setSelected(true);
        if (m_r_info.func_rate >= 5)
            m_imvStar15.setSelected(true);

        m_imvStar21.setSelected(false);
        m_imvStar22.setSelected(false);
        m_imvStar23.setSelected(false);
        m_imvStar24.setSelected(false);
        m_imvStar25.setSelected(false);

        if (m_r_info.price_rate >= 1)
            m_imvStar21.setSelected(true);
        if (m_r_info.price_rate >= 2)
            m_imvStar22.setSelected(true);
        if (m_r_info.price_rate >= 3)
            m_imvStar23.setSelected(true);
        if (m_r_info.price_rate >= 4)
            m_imvStar24.setSelected(true);
        if (m_r_info.price_rate >= 5)
            m_imvStar25.setSelected(true);

        m_imvStar31.setSelected(false);
        m_imvStar32.setSelected(false);
        m_imvStar33.setSelected(false);
        m_imvStar34.setSelected(false);
        m_imvStar35.setSelected(false);

        if (m_r_info.take_rate >= 1)
            m_imvStar31.setSelected(true);
        if (m_r_info.take_rate >= 2)
            m_imvStar32.setSelected(true);
        if (m_r_info.take_rate >= 3)
            m_imvStar33.setSelected(true);
        if (m_r_info.take_rate >= 4)
            m_imvStar34.setSelected(true);
        if (m_r_info.take_rate >= 5)
            m_imvStar35.setSelected(true);

        m_imvStar41.setSelected(false);
        m_imvStar42.setSelected(false);
        m_imvStar43.setSelected(false);
        m_imvStar44.setSelected(false);
        m_imvStar45.setSelected(false);

        if (m_r_info.taste_rate >= 1)
            m_imvStar41.setSelected(true);
        if (m_r_info.taste_rate >= 2)
            m_imvStar42.setSelected(true);
        if (m_r_info.taste_rate >= 3)
            m_imvStar43.setSelected(true);
        if (m_r_info.taste_rate >= 4)
            m_imvStar44.setSelected(true);
        if (m_r_info.taste_rate >= 5)
            m_imvStar45.setSelected(true);

        m_nCategory = m_r_info.category;
        m_tvCategory.setText(getResources().getStringArray(R.array.review_category_reg)[m_r_info.category]);
        m_nPerson = m_r_info.person;

//        for(i=0;i<4;i++){
//            if(pos[i]==1)   {m_nPerson += 100*(i+1);  break;}
//        }
        String strPerson="";
        int temp=0;
        if(m_nPerson<100){
            if(m_nPerson>=10){
                strPerson = ",여성";
                temp = m_nPerson - 10;
            }else{
                strPerson = ",남성";
                temp = m_nPerson;
            }
            switch (temp){
                case 0:
                    strPerson = "영유아" + strPerson;
                    break;
                case 1:
                    strPerson = "어린이" + strPerson;
                    break;
                case 2:
                    strPerson = "청소년" + strPerson;
                    break;
                case 3:
                    strPerson = "성인" + strPerson;
                    break;
                case 4:
                    strPerson = "노인" + strPerson;
                    break;
            }
        }else{
            switch (m_nPerson){
                case 100:
                    strPerson = "수험생";
                    break;
                case 200:
                    strPerson = "임산부";
                    break;
                case 300:
                    strPerson = "수유부";
                    break;
                case 400:
                    strPerson = "갱년기";
                    break;
            }
        }
        m_tvPerson.setText(strPerson);
        //m_tvPerson.setText(getResources().getStringArray(R.array.taking_target)[m_r_info.person]);
        m_nBuyPath = m_r_info.buy_path;
        m_tvBuy.setText(getResources().getStringArray(R.array.buy_path)[m_r_info.buy_path]);
        m_nPeriod = m_r_info.period;
        m_tvPeriod.setText(getResources().getStringArray(R.array.taken_period)[m_r_info.period]);
        m_tvYES.setSelected(false);
        m_tvNO.setSelected(false);
        m_nRetake = m_r_info.retake;
        if (m_r_info.retake == 0)
            m_tvYES.setSelected(true);
        else
            m_tvNO.setSelected(true);

        if (m_r_info.photos.size() == 0)
            m_uiLlyPhoto.removeAllViews();

        for (int i = 0; i < m_r_info.photos.size(); i++) {
            final PhotoListLayout layout = new PhotoListLayout(this, m_nAddImage, m_r_info.photos.get(i), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = (Integer) view.getTag();
                    for (int i = index + 1; i < m_uiLlyPhoto.getChildCount(); i++) {
                        View child = m_uiLlyPhoto.getChildAt(i);
                        child.setTag((Integer) child.getTag() - 1);
                    }
                    m_uiLlyPhoto.removeViewAt(index);
                    m_uploadImageFile.remove(index);
                    m_uploadImageID.remove(index);
                    m_nAddImage--;
                    m_r_info.photos.remove(index);
                    m_r_info.photos_id.remove(index);
                    m_r_info.photosFile.remove(index);
                }
            }, 0);
            layout.setLocalImage(m_r_info.photos_id.get(i).equals("NA"));
            m_uiLlyPhoto.addView(layout, m_nAddImage);
            m_nAddImage++;

            File file = new File(Net.URL_SERVER1 + m_r_info.photos.get(i));
            m_uploadImageFile.add(file);
            m_uploadImageID.add(m_r_info.photos_id.get(i));
        }
    }
    private void setGoodInfo_to_RInfo(GoodInfo m_info_gd){
        if(m_r_info._good_id.size()==0)
            m_r_info._good_id.add(m_info_gd._id);
        else
            m_r_info._good_id.set(0, m_info_gd._id);
        if(m_r_info._good_photo_urls.size()==0)
            m_r_info._good_photo_urls.add(m_info_gd._imagePath);
        else
            m_r_info._good_photo_urls.set(0, m_info_gd._imagePath);
        if(m_r_info._good_business.size()==0)
            m_r_info._good_business.add(m_info_gd._business);
        else
            m_r_info._good_business.set(0, m_info_gd._business);
        m_r_info.title = m_info_gd._name;

        m_r_info.good_photo = m_info_gd._imagePath;
        m_r_info.business = m_info_gd._business;
    }

    private void updateGood() {
        m_tvGoodName.setText(m_r_info.title);
        m_tvBusiness.setText(m_r_info._good_business.get(0));
        if (m_r_info._good_photo_urls.get(0).equals(""))
            Glide.with(m_context).load(R.drawable.default_review).into(m_imvGood);
        else
            Glide.with(m_context).load(Net.URL_SERVER1 + m_r_info._good_photo_urls.get(0)).into(m_imvGood);

        findViewById(R.id.lly_before_select).setVisibility(View.GONE);
        findViewById(R.id.lly_after_select).setVisibility(View.VISIBLE);
    }

    public void hideKeypad() {
        Util.hideKeyPad(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.imv_back:
                finish();
                break;
            case R.id.tv_finish:
                clickFinish();
                break;
            case R.id.tv_standard:
                intent = new Intent(this, FaqNoticeActivity.class);
                intent.putExtra("write_activity", true);
                intent.putExtra("type", 0);
                startActivity(intent);
                break;
            case R.id.lly_after_select:
            case R.id.tv_change:
            case R.id.tv_change1:
                intent = new Intent(this, SearchInitActivity.class);
                intent.putExtra("select_good", true);
                intent.putExtra("isInitSearch", true);
                startActivityForResult(intent, 200);
                break;
            case R.id.tv_reg_hash:
                intent = new Intent(this, HashListActivity.class);
                intent.putExtra("hash", m_tvHash.getText().toString());
                startActivityForResult(intent, 100);
                break;
            case R.id.imv_add:
            case R.id.imv_add_btn:
                if (m_nAddImage == 5) {
                    Toast.makeText(this, "최대 5개까지 선택할 수 있습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                takePhoto();
                break;
            case R.id.tv_category:
                if(m_r_info._good_id.size()!=0 && m_r_info._good_id.get(0)!=null) {
                    if (arrintCat == null) {
                        m_nConnectType = NetUtil.apis_GET_REVIEW_WRITE_CATEGORY;
                        mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, m_r_info._good_id.get(0));
                    } else
                        new ListSelectDialog(this, "리뷰 카테고리", arrstrCat, 12, m_nCategory).show();
                }else
                    new NoticeDialog(this, "카테고리 선택 불가", "제품 선택을 먼저 해주세요..","잠시 후에 자동으로 닫힙니다.", true).show();
                break;
            case R.id.lly_period:
                new ListSelectDialog(this, "복용 기간", getResources().getStringArray(R.array.taken_period), 17, m_nPeriod).show();
                break;
            case R.id.lly_person:
                //new ListSelectDialog(this, "복용대상", getResources().getStringArray(R.array.taking_target), 24, m_nPerson).show();
                new ReviewSelectDialog(this, "복용대상 선택", null, null, 12, null).show();
                break;
            case R.id.lly_buy_path:
                new ListSelectDialog(this, "구입경로", getResources().getStringArray(R.array.buy_path), 8, m_nBuyPath).show();
                break;
            case R.id.tv_yes:
                m_nRetake = 0;
                m_r_info.retake = 0;
                m_tvYES.setSelected(true);
                m_tvNO.setSelected(false);
                break;
            case R.id.tv_no:
                m_nRetake = 1;
                m_r_info.retake = 1;
                m_tvYES.setSelected(false);
                m_tvNO.setSelected(true);
                break;
            case R.id.imv_star11:
                m_nFuncRate = 1;
                m_r_info.func_rate = 1;
                m_imvStar11.setSelected(true);
                m_imvStar12.setSelected(false);
                m_imvStar13.setSelected(false);
                m_imvStar14.setSelected(false);
                m_imvStar15.setSelected(false);
                break;
            case R.id.imv_star12:
                m_nFuncRate = 2;
                m_r_info.func_rate = 2;
                m_imvStar11.setSelected(true);
                m_imvStar12.setSelected(true);
                m_imvStar13.setSelected(false);
                m_imvStar14.setSelected(false);
                m_imvStar15.setSelected(false);
                break;
            case R.id.imv_star13:
                m_nFuncRate = 3;
                m_r_info.func_rate = 3;
                m_imvStar11.setSelected(true);
                m_imvStar12.setSelected(true);
                m_imvStar13.setSelected(true);
                m_imvStar14.setSelected(false);
                m_imvStar15.setSelected(false);
                break;
            case R.id.imv_star14:
                m_nFuncRate = 4;
                m_r_info.func_rate = 4;
                m_imvStar11.setSelected(true);
                m_imvStar12.setSelected(true);
                m_imvStar13.setSelected(true);
                m_imvStar14.setSelected(true);
                m_imvStar15.setSelected(false);
                break;
            case R.id.imv_star15:
                m_nFuncRate = 5;
                m_r_info.func_rate = 5;
                m_imvStar11.setSelected(true);
                m_imvStar12.setSelected(true);
                m_imvStar13.setSelected(true);
                m_imvStar14.setSelected(true);
                m_imvStar15.setSelected(true);
                break;
            case R.id.imv_star21:
                m_nPriceRate = 1;
                m_r_info.price_rate = 1;
                m_imvStar21.setSelected(true);
                m_imvStar22.setSelected(false);
                m_imvStar23.setSelected(false);
                m_imvStar24.setSelected(false);
                m_imvStar25.setSelected(false);
                break;
            case R.id.imv_star22:
                m_nPriceRate = 2;
                m_r_info.price_rate = 2;
                m_imvStar21.setSelected(true);
                m_imvStar22.setSelected(true);
                m_imvStar23.setSelected(false);
                m_imvStar24.setSelected(false);
                m_imvStar25.setSelected(false);
                break;
            case R.id.imv_star23:
                m_nPriceRate = 3;
                m_r_info.price_rate = 3;
                m_imvStar21.setSelected(true);
                m_imvStar22.setSelected(true);
                m_imvStar23.setSelected(true);
                m_imvStar24.setSelected(false);
                m_imvStar25.setSelected(false);
                break;
            case R.id.imv_star24:
                m_nPriceRate = 4;
                m_r_info.price_rate = 4;
                m_imvStar21.setSelected(true);
                m_imvStar22.setSelected(true);
                m_imvStar23.setSelected(true);
                m_imvStar24.setSelected(true);
                m_imvStar25.setSelected(false);
                break;
            case R.id.imv_star25:
                m_nPriceRate = 5;
                m_r_info.price_rate = 5;
                m_imvStar21.setSelected(true);
                m_imvStar22.setSelected(true);
                m_imvStar23.setSelected(true);
                m_imvStar24.setSelected(true);
                m_imvStar25.setSelected(true);
                break;
            case R.id.imv_star31:
                m_nTakeRate = 1;
                m_r_info.take_rate = 1;
                m_imvStar31.setSelected(true);
                m_imvStar32.setSelected(false);
                m_imvStar33.setSelected(false);
                m_imvStar34.setSelected(false);
                m_imvStar35.setSelected(false);
                break;
            case R.id.imv_star32:
                m_nTakeRate = 2;
                m_r_info.take_rate = 2;
                m_imvStar31.setSelected(true);
                m_imvStar32.setSelected(true);
                m_imvStar33.setSelected(false);
                m_imvStar34.setSelected(false);
                m_imvStar35.setSelected(false);
                break;
            case R.id.imv_star33:
                m_nTakeRate = 3;
                m_r_info.take_rate = 3;
                m_imvStar31.setSelected(true);
                m_imvStar32.setSelected(true);
                m_imvStar33.setSelected(true);
                m_imvStar34.setSelected(false);
                m_imvStar35.setSelected(false);
                break;
            case R.id.imv_star34:
                m_nTakeRate = 4;
                m_r_info.take_rate = 4;
                m_imvStar31.setSelected(true);
                m_imvStar32.setSelected(true);
                m_imvStar33.setSelected(true);
                m_imvStar34.setSelected(true);
                m_imvStar35.setSelected(false);
                break;
            case R.id.imv_star35:
                m_nTakeRate = 5;
                m_r_info.take_rate = 5;
                m_imvStar31.setSelected(true);
                m_imvStar32.setSelected(true);
                m_imvStar33.setSelected(true);
                m_imvStar34.setSelected(true);
                m_imvStar35.setSelected(true);
                break;
            case R.id.imv_star41:
                m_nTasteRate = 1;
                m_r_info.taste_rate = 1;
                m_imvStar41.setSelected(true);
                m_imvStar42.setSelected(false);
                m_imvStar43.setSelected(false);
                m_imvStar44.setSelected(false);
                m_imvStar45.setSelected(false);
                break;
            case R.id.imv_star42:
                m_nTasteRate = 2;
                m_r_info.taste_rate = 2;
                m_imvStar41.setSelected(true);
                m_imvStar42.setSelected(true);
                m_imvStar43.setSelected(false);
                m_imvStar44.setSelected(false);
                m_imvStar45.setSelected(false);
                break;
            case R.id.imv_star43:
                m_nTasteRate = 3;
                m_r_info.taste_rate = 3;
                m_imvStar41.setSelected(true);
                m_imvStar42.setSelected(true);
                m_imvStar43.setSelected(true);
                m_imvStar44.setSelected(false);
                m_imvStar45.setSelected(false);
                break;
            case R.id.imv_star44:
                m_nTasteRate = 4;
                m_r_info.taste_rate = 4;
                m_imvStar41.setSelected(true);
                m_imvStar42.setSelected(true);
                m_imvStar43.setSelected(true);
                m_imvStar44.setSelected(true);
                m_imvStar45.setSelected(false);
                break;
            case R.id.imv_star45:
                m_nTasteRate = 5;
                m_r_info.taste_rate = 5;
                m_imvStar41.setSelected(true);
                m_imvStar42.setSelected(true);
                m_imvStar43.setSelected(true);
                m_imvStar44.setSelected(true);
                m_imvStar45.setSelected(true);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.imv_back));
    }

    SelectReviewPhotoDialog m_selectPhotoDlg = null;

    private void takePhoto() {
        if (m_selectPhotoDlg == null)
            m_selectPhotoDlg = new SelectReviewPhotoDialog(this, null, 0);
        m_selectPhotoDlg.show();
    }

    private List<Bitmap> m_uploadBitmap = new ArrayList<Bitmap>();
    private List<File> m_uploadImageFile = new ArrayList<File>();
    private List<String> m_uploadImageID = new ArrayList<String>();
    private int m_nAddImage = 0;

    public void setSelectedImage(Bitmap b, File f) {
        if (b != null && f != null) {
            if (m_nAddImage == 5) {
                Toast.makeText(this, "최대 5개까지 선택할 수 있습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (m_nAddImage == 0)
                m_uiLlyPhoto.removeAllViews();
            final PhotoListLayout layout = new PhotoListLayout(this, m_nAddImage, f.getPath(), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = (Integer) view.getTag();
                    for (int i = index + 1; i < m_uiLlyPhoto.getChildCount(); i++) {
                        View child = m_uiLlyPhoto.getChildAt(i);
                        child.setTag((Integer) child.getTag() - 1);
                    }
                    m_uiLlyPhoto.removeViewAt(index);
                    m_uploadImageFile.remove(index);
                    m_uploadBitmap.remove(index);
                    m_uploadImageID.remove(index);
                    m_nAddImage--;
                    m_r_info.photos.remove(index);
                    m_r_info.photos_id.remove(index);
                }
            }, 0);
            layout.setLocalImage(true);
            m_uiLlyPhoto.addView(layout, m_nAddImage);
            m_nAddImage++;
            m_uploadImageFile.add(f);
            m_uploadBitmap.add(b);
            m_uploadImageID.add("");
            m_r_info.photos.add(f.getPath());
            m_r_info.photos_id.add("NA");
            m_r_info.photosFile.add(f);
        }
    }

    private void clickFinish() {
        if (m_r_info._good_id.size() == 0) {
            Toast.makeText(this, "제품을 선택해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (m_nFuncRate == 0 || m_nPriceRate == 0 || m_nTakeRate == 0 || m_nTasteRate == 0) {
            Toast.makeText(this, "별점을 선택해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (m_edtContent.getText().toString().length() == 0) {
            Toast.makeText(this, "만족한 부분을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (m_edtContent2.getText().toString().length() == 0) {
            Toast.makeText(this, "아쉬운 부분을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (m_edtContent.getText().toString().length() < 20) {
            Toast.makeText(this, "만족한 부분의 입력은 20글자 이상 부탁드려요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (m_edtContent2.getText().toString().length() < 20) {
            Toast.makeText(this, "아쉬운 부분의 입력은 20글자 이상 부탁드려요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (m_edtContent3.getText().toString().length() == 0) {
            Toast.makeText(this, "추천사항을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
//        if (m_nAddImage == 0) {
//            Toast.makeText(this, "사진을 입력해 주세요.", Toast.LENGTH_SHORT).show();
//            return;
//        }
        if (m_nCategory == -1) {
            Toast.makeText(this, "카테고리를 선택해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (m_nPerson == -1) {
            Toast.makeText(this, "뵥용대상을 선택해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (m_nBuyPath == -1) {
            Toast.makeText(this, "구입경로를 선택해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (m_nPeriod == -1) {
            Toast.makeText(this, "복용기간을 선택해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        new NoticeDialog(this, "리뷰 작성 완료!", "신뢰도 있는 제품 리뷰와 평점을 제공하기 위하여, 저희 비타미인에서는 모든 리뷰를 직접 확인하고 있어요. 자체의 심의 기준과 맞지 않을 경우 게시되지 않을 수도 있음을 양해 부탁드려요. :D\n\n 리뷰 게시가 승인되기 전에도 마이페이지에서는 작성하신 리뷰를 확인하고, 수정할 수 있습니다.", "리뷰 등록하기", false,
            new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    m_nConnectType = WRITE_REVIEW;
                    connectServer();
                }
            }).show();
    }

    private String[] m_strPhotoArray;

    private void connectServer() {
        showProgress();
        String[] paramFields = new String[]{Net.NET_POST_FIELD_ACT,
                Net.NET_POST_FIELD_REQUEST};
        String[] paramValues = null;

        // 파라메터 입력
        JSONObject w_objJSonData = new JSONObject();
        try {
            if (m_nConnectType == WRITE_REVIEW) {
                m_r_info.content = m_edtContent.getText().toString();
                m_r_info.content2 = m_edtContent2.getText().toString();
                m_r_info.content3 = m_edtContent3.getText().toString();
                m_r_info.hash_tag = m_tvHash.getText().toString();
                m_r_info.rate = ((m_nFuncRate + m_nPriceRate + m_nTakeRate + m_nTasteRate) / 4.0);
                m_r_info.buy_path = m_nBuyPath;
                m_r_info.category = m_nCategory;
                m_r_info.person = m_nPerson;
                m_r_info.period = m_nPeriod;
                m_r_info.retake = m_nRetake;

                GregorianCalendar cal = new GregorianCalendar();
                m_r_info.regdate = cal.get(Calendar.YEAR) + "." + cal.get(Calendar.MONDAY) + "." + cal.get(Calendar.DAY_OF_MONTH);

                w_objJSonData.put(Net.NET_VALUE_MEMBER_ID, UserManager.getInstance().member_id);
                w_objJSonData.put("f_no", UserManager.getInstance().member_no);
                w_objJSonData.put("content", m_edtContent.getText().toString());
                w_objJSonData.put("content2", m_edtContent2.getText().toString());
                w_objJSonData.put("content3", m_edtContent3.getText().toString());
                w_objJSonData.put("hash_tag", m_tvHash.getText());
                w_objJSonData.put("good_id", "" + m_r_info._good_id.get(0));
                w_objJSonData.put("func_rate", "" + m_nFuncRate);
                w_objJSonData.put("price_rate", "" + m_nPriceRate);
                w_objJSonData.put("take_rate", "" + m_nTakeRate);
                w_objJSonData.put("taste_rate", "" + m_nTasteRate);
                w_objJSonData.put("rate", "" + ((m_nFuncRate + m_nPriceRate + m_nTakeRate + m_nTasteRate) / 4.0));
                w_objJSonData.put("buy_path", "" + m_nBuyPath);
                w_objJSonData.put("category", "" + (m_nCategory + 1));
                w_objJSonData.put("person", "" + m_nPerson);
                w_objJSonData.put("period", "" + m_nPeriod);
                w_objJSonData.put("retake", "" + m_nRetake);

                w_objJSonData.put("type", "review");

                if (m_nAddImage > 0) {
                    m_strPhotoArray = new String[m_nAddImage];
                    int j = 0;
                    for (int i = m_nAddImage - 1; i >= 0; i--) {
                        File file = m_uploadImageFile.get(i);
                        if (file.exists()) {
                            m_strPhotoArray[j] = file.getPath();
                        } else {
                            m_strPhotoArray[j] = null;
                        }
                        j++;
                    }
                }

                if (getIntent().getBooleanExtra("edit", false)) {
                    w_objJSonData.put("review_id", "" + m_r_info._id);

                    String delPhotoIDs = "";
                    for (int i = 0; i < oldphotoid.size(); i++) {
                        String _id = oldphotoid.get(i);
                        boolean _isfound = false;
                        for (int j = 0; j < m_r_info.photos_id.size(); j++) {
                            String _other_id = m_r_info.photos_id.get(j);

                            if (_id.equals(_other_id)) {
                                _isfound = true;
                                break;
                            }
                        }
                        if (!_isfound) {
                            delPhotoIDs += _id + ",";
                        }
                    }
                    if (delPhotoIDs.length() > 0)
                        delPhotoIDs = delPhotoIDs.substring(0, delPhotoIDs.length() - 1);
                    w_objJSonData.put(Net.NET_VALUE_DELETE_PHOTO_IDS, delPhotoIDs);

                    paramValues = new String[]{
                            Net.apis_UPDATE_REVIEW,
                            w_objJSonData.toString()};
                } else {
                    paramValues = new String[]{
                            Net.apis_WRITE_REVIEW,
                            w_objJSonData.toString()};
                }
            }
        } catch (Exception e) {e.printStackTrace();}

        String netUrl = Net.URL_SERVER + Net.URL_SERVER_API;
        if (m_nAddImage == 0)
            HttpRequester.getInstance().init(this, this, handler, netUrl,
                    paramFields, paramValues, false);
        else
            HttpRequester.getInstance().init(this, this, handler, netUrl,
                    paramFields, paramValues, m_strPhotoArray);

        HttpRequester.getInstance().setProgressMessage(
                Net.NET_COMMON_STRING_WAITING);
        HttpRequester.getInstance().startNetThread();
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Net.THREAD_REQUEST_END) {processForNetEnd();}
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
                if (m_nConnectType == WRITE_REVIEW) {
                    Intent intent = new Intent();
                    m_r_info.f_photo = UserManager.getInstance().arr_profile_photo_URL.get(0);
                    m_r_info._mb_id = UserManager.getInstance().member_id;
                    m_r_info._mb_nick = UserManager.getInstance().member_nick_name;
                    int yearOfBirth = Integer.parseInt(UserManager.getInstance().member_birth.substring(0, 4));
                    int yearOfCurrent = Calendar.getInstance().get(Calendar.YEAR);
                    m_r_info._mb_age =  yearOfCurrent - yearOfBirth;
                    m_r_info._mb_sex = UserManager.getInstance().member_sex;
                    m_r_info.Author_examinee = UserManager.getInstance().HealthBaseInfo.get(0).member_examinee==0? 0:1;
                    m_r_info.Author_pregnant = UserManager.getInstance().HealthBaseInfo.get(0).member_examinee==0? 0:1;
                    m_r_info.Author_lactating = UserManager.getInstance().HealthBaseInfo.get(0).member_examinee==0? 0:1;
                    m_r_info.Author_climacterium = UserManager.getInstance().HealthBaseInfo.get(0).member_examinee==0? 0:1;
                    intent.putExtra("info", m_r_info);
                    /*
                    if (getIntent().getBooleanExtra("edit", false)) {
                    } else if (getIntent().getBooleanExtra("fromMypage", false) || getIntent().getBooleanExtra("fromDetailGood", false)) {
                    }
                    */
                    setResult(RESULT_OK, intent);
                    finish();
                } else if (m_nConnectType == NetUtil.apis_GET_REVIEW_WRITE_CATEGORY) {
                    new ListSelectDialog(this, "리뷰 카테고리", arrstrCat, 12, m_nCategory).show();
                }
            }
        } else {
            if (!"".equals(strMsg)) {Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();}
        }
    }

    public void parseJSON() {
        try {
            m_nResultType = _jResult.getInt(Net.NET_VALUE_CODE);
            JSONObject result = _jResult.getJSONObject(Net.NET_VALUE_RESULT);
            if (m_nConnectType == NetUtil.apis_GET_REVIEW_WRITE_CATEGORY) {
                boolean has34 = false;
                String strReview_write_cat = result.getString("cat_for_good");
                strReview_write_cat = strReview_write_cat.equals("null")? "34":strReview_write_cat;
                String[] arrReview_write_cat = strReview_write_cat.split(",");

                for(String One:arrReview_write_cat)
                    if(One.equals("34")) has34 = true;
                if(has34) {
                    arrintCat = new Integer[arrReview_write_cat.length];
                    arrstrCat = new String[arrReview_write_cat.length];
                }else{
                    arrintCat = new Integer[arrReview_write_cat.length + 1];
                    arrstrCat = new String[arrReview_write_cat.length + 1];
                }

                for (int i = 0; i < arrReview_write_cat.length; i++) {
                    arrintCat[i] = Integer.valueOf(arrReview_write_cat[i]) - 1;     // 서버는 1부터 34(34가 종합건강)이므로, array 에 있는 리스트는 0부터 33까지임
                    arrstrCat[i] = getResources().getStringArray(R.array.review_category_reg)[arrintCat[i]];
                }
                if(!has34) {
                    arrintCat[arrReview_write_cat.length] = 34 - 1;
                    arrstrCat[arrReview_write_cat.length] = "종합건강";
                }
            }
        } catch (JSONException e) {e.printStackTrace();}
    }

    public void setCategory(int index) {
//        index로 m_nCategory 구하는 코드 삽입
        m_nCategory = arrintCat[index];
        m_tvCategory.setText(getResources().getStringArray(R.array.review_category_reg)[m_nCategory]);
    }

    public void setPeroid(int index) {
        m_nPeriod = index;
        m_tvPeriod.setText(getResources().getStringArray(R.array.taken_period)[index]);
    }

    public void setBuyPath(int index) {
        m_nBuyPath = index;
        m_tvBuy.setText(getResources().getStringArray(R.array.buy_path)[index]);
    }
    public void setPerson(int[] pos) {
        int i;
        m_nPerson=0;
        String strPerson="";

        //100의 자리는 1~4가 지정 status이고,
        //0이면 status가 없고 성별과 연령으로 지정됨.
        //10자리는 0~1, 1의 자리는 0~4
        for(i=0;i<4;i++){
            if(pos[i]==1)   {m_nPerson += 100*(i+1);  break;}
        }
        switch (m_nPerson){
            case 0:
                for(i=6;i<11;i++){
                    if(pos[i]==1)   break;
                    m_nPerson += 1;
                }
                switch (m_nPerson){
                    case 0:
                        strPerson = "영유아";
                        break;
                    case 1:
                        strPerson = "어린이";
                        break;
                    case 2:
                        strPerson = "청소년";
                        break;
                    case 3:
                        strPerson = "성인";
                        break;
                    case 4:
                        strPerson = "노인";
                        break;
                }
                if(pos[4]==1){
                    if(strPerson.length()!=0)   strPerson = strPerson + ",";
                    strPerson = strPerson + "남성";
                }else if(pos[5]==1){
                    m_nPerson += 10;
                    if(strPerson.length()!=0)   strPerson = strPerson + ",";
                    strPerson = strPerson + "여성";
                }
                break;
            case 100:
                strPerson = "수험생";
                break;
            case 200:
                strPerson = "임산부";
                break;
            case 300:
                strPerson = "수유부";
                break;
            case 400:
                strPerson = "갱년기";
                break;
        }

        m_tvPerson.setText(strPerson);
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
                case 100:
                    m_tvHash.setText(data.getStringExtra("hash"));
                    break;
                case 200:
                    m_info_gd = (GoodInfo) data.getSerializableExtra("info");
                    if (m_info_gd != null) {
                        setGoodInfo_to_RInfo(m_info_gd);
                    }
                    updateGood();
                    m_nConnectType = NetUtil.apis_GET_REVIEW_WRITE_CATEGORY;
                    mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, m_r_info._good_id.get(0));
                    break;
            }
        }
    }

    @Override
    public void onParseJSon(JSONObject j_source) {
        _jResult = j_source;
    }
}