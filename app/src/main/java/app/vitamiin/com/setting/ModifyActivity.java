package app.vitamiin.com.setting;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.R;
import app.vitamiin.com.common.CircleImageView;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.common.UserHealthBase;
import app.vitamiin.com.common.UserHealthDetail;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.common.Util;
import app.vitamiin.com.home.MainActivity;
import app.vitamiin.com.home.ManageLivingInfoActivity;
import app.vitamiin.com.home.ManageProfileActivity;
import app.vitamiin.com.home.PhotoListLayout;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;
import app.vitamiin.com.login.DetailSurveyActivity;

public class ModifyActivity extends BaseActivity implements View.OnClickListener, OnParseJSonListener, NetUtil.PhotoDownCompleteCallback {
    Context mContext = this;
    int m_nResultType = 200;
    NetUtil mNetUtil;
    NetUtil.connectAndgetServer mNetUtilConnetServer;
    int m_nConnectType;

    TextView m_tvNickName, m_tvName,m_tvBirth,m_tvSex,m_tvHeight,m_tvWeight,m_tvPregnant,m_tvDwelling,m_tvDisease,m_tvInterest,m_tvPrefer;

    CircleImageView m_imvImage;
    CircleImageView m_imv_me_in_list;

    int minmax[];

    UserHealthBase m_userHealthBase = new UserHealthBase();
    UserHealthDetail m_userHealthDetail = new UserHealthDetail();

    private LinearLayout m_uiLlyPhoto;
    private int index=0;
    private int index_del=0;
    private int resArr[] = {R.drawable.ic_male_1, R.drawable.ic_male_2, R.drawable.ic_male_3, R.drawable.ic_male_4,
                            R.drawable.ic_female_1, R.drawable.ic_female_2, R.drawable.ic_female_3, R.drawable.ic_female_4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        mNetUtil = new NetUtil();
        mNetUtilConnetServer = mNetUtil.new connectAndgetServer(this);

        initView();
        //앱 구동 후, 한번만 실행하는지? 맞다면 가족 정보를 로드하는 부분이 이곳에 있는 것이 맞다만...
        if(!UserManager.getInstance().is_familyinfo_loaded) {
            this.showProgress();
            m_nConnectType = NetUtil.apis_GET_FAMILY;
            mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, "");
        }else{
            updatePhotoListView();
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        updatePhotoListView();
    }

    private void initView() {
        m_uiLlyPhoto = (LinearLayout) findViewById(R.id.lly_photo);
        m_tvNickName = (TextView) findViewById(R.id.tv_nickname);
        m_tvName = (TextView) findViewById(R.id.tv_name);
        m_tvBirth = (TextView) findViewById(R.id.tv_birth);
        m_tvSex = (TextView) findViewById(R.id.tv_sex);
        m_tvHeight = (TextView) findViewById(R.id.tv_height);
        m_tvWeight = (TextView) findViewById(R.id.tv_weight);

        m_tvPregnant = (TextView) findViewById(R.id.tv_pregnant);
        m_tvDwelling = (TextView) findViewById(R.id.tv_dwelling1);
        m_tvDisease = (TextView) findViewById(R.id.tv_disease);
        m_tvInterest = (TextView) findViewById(R.id.tv_interest);
        m_tvPrefer = (TextView) findViewById(R.id.tv_prefer);

        m_imvImage = (CircleImageView) findViewById(R.id.imv_profile);
        m_imv_me_in_list = (CircleImageView) findViewById(R.id.imv_me_photo_in_list);
        //m_imv_me_in_list.setBorderWidth(2);
        //m_imv_me_in_list.setBorderColor(0xFFff2e74);

        findViewById(R.id.imv_back).setOnClickListener(this);
        findViewById(R.id.imv_modify).setOnClickListener(this);
        findViewById(R.id.imv_add).setOnClickListener(this);

        findViewById(R.id.lly_pregnant).setOnClickListener(this);
        findViewById(R.id.lly_dwelling1).setOnClickListener(this);

        findViewById(R.id.lly_disease1).setOnClickListener(this);
        findViewById(R.id.lly_interest1).setOnClickListener(this);
        findViewById(R.id.lly_prefer1).setOnClickListener(this);

        findViewById(R.id.imv_me_photo_in_list).setOnClickListener(this);

        findViewById(R.id.get_detail_health_info).setOnClickListener(this);
        //arr_file 참조가 가능하도록 변경할 것 by 동현

        if(UserManager.getInstance().arr_profile_photo_resID.get(0)!=-1){
            Glide.with(this).load(resArr[UserManager.getInstance().arr_profile_photo_resID.get(0)]).into(m_imvImage);
            Glide.with(this).load(resArr[UserManager.getInstance().arr_profile_photo_resID.get(0)]).into(m_imv_me_in_list);
        }else{
            if(UserManager.getInstance().arr_profile_photo_file.get(0)!=null){
                Glide.with(this).load(UserManager.getInstance().arr_profile_photo_file.get(0).getPath()).into(m_imvImage);
                Glide.with(this).load(UserManager.getInstance().arr_profile_photo_file.get(0).getPath()).into(m_imv_me_in_list);
            }else {
                UserManager.getInstance().arr_profile_photo_resID.set(0, new Util().getAutoImageResID(index));
                Glide.with(this).load(resArr[UserManager.getInstance().arr_profile_photo_resID.get(0)]).into(m_imvImage);
                Glide.with(this).load(resArr[UserManager.getInstance().arr_profile_photo_resID.get(0)]).into(m_imv_me_in_list);
            }
        }

        index = 0;
        updatHealthDataView();

        m_uiLlyPhoto.removeAllViews();
        for(int i=0; i < UserManager.getInstance().HealthBaseInfo.size(); i++)
            m_uiLlyPhoto.addView(new LinearLayout(this), i);
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Net.THREAD_REQUEST_END) {
                processForNetEnd();}
        }
    };

    private void processForNetEnd() {
        parseJSON();
        this.closeProgress();
        HttpRequester.getInstance().stopNetThread();

        int resultCode = HttpRequester.getInstance().getResultCode();
        String strMsg = HttpRequester.getInstance().getResultMsg();

        if (resultCode == Net.CONNECTION_SUCCSES) {
        } else {
            if (!"".equals(strMsg)) {Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();}}
    }

    JSONObject _jResult;

    public void parseJSON() {
        try {
            m_nResultType = _jResult.getInt(Net.NET_VALUE_CODE); //가족을 최초로 찾아 등록하는 곳
            JSONObject result = _jResult.getJSONObject(Net.NET_VALUE_RESULT); //result에 가족 정보

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
                mNetUtil.setProfilePhoto(this, i + 1, obj.getString("f_photo"));
            }
            UserManager.getInstance().is_familyinfo_loaded = true;
        } catch (JSONException e) {e.printStackTrace();}
    }

    @Override
    public void onParseJSon(JSONObject j_source) {
        _jResult = j_source;
    }

    private void updatHealthDataView() {
        String strTEMP;
        if(UserManager.getInstance().arr_profile_photo_resID.get(index)!=-1){
            Glide.with(this).load(resArr[UserManager.getInstance().arr_profile_photo_resID.get(index)]).into(m_imvImage);
        }else{
            Glide.with(this).load(UserManager.getInstance().arr_profile_photo_file.get(index).getPath()).into(m_imvImage);
        }

        //index는 0이 User본인, 0부터 가족이다.
        if(index==0){
            m_tvNickName.setText(UserManager.getInstance().member_nick_name);
            m_tvName.setText(UserManager.getInstance().member_name);
            strTEMP = UserManager.getInstance().member_birth + " /";
            m_tvBirth.setText(strTEMP);
            if (UserManager.getInstance().member_sex == 0)
                m_tvSex.setText("남자");
            else
                m_tvSex.setText("여자");
        }else if(index>0){
            m_tvNickName.setText(UserManager.getInstance().HealthBaseInfo.get(index).member_nick_name);
            m_tvName.setText(UserManager.getInstance().HealthBaseInfo.get(index).member_name);
            strTEMP = UserManager.getInstance().HealthBaseInfo.get(index).member_birth + " /";
            m_tvBirth.setText(strTEMP);
            if (UserManager.getInstance().HealthBaseInfo.get(index).member_sex == 0)
                m_tvSex.setText("남자");
            else
                m_tvSex.setText("여자");
        }
        strTEMP = UserManager.getInstance().HealthBaseInfo.get(index).member_height + "cm /";
        m_tvHeight.setText(strTEMP);
        strTEMP = UserManager.getInstance().HealthBaseInfo.get(index).member_weight + "kg";
        m_tvWeight.setText(strTEMP);

        //m_tvPregnant.setText(getResources().getStringArray(R.array.life_pregnant)[UserManager.getInstance().HealthBaseInfo.get(index).member_pregnant]);
        if(UserManager.getInstance().HealthBaseInfo.get(index).member_examinee==0) {
            m_tvPregnant.setText("수험생");
        }else if(UserManager.getInstance().HealthBaseInfo.get(index).member_pregnant==0){
            m_tvPregnant.setText("임산부");
        }else if(UserManager.getInstance().HealthBaseInfo.get(index).member_lactating==0){
            m_tvPregnant.setText("수유부");
        }else if(UserManager.getInstance().HealthBaseInfo.get(index).member_climacterium==0){
            m_tvPregnant.setText("갱년기 여성");
        }else { //수험생, 임산부, 수유부, 갱년기 여성 아무것도 선택되지 않은 사용자의 경우
            int yearOfBirth = Integer.parseInt(UserManager.getInstance().HealthBaseInfo.get(index).member_birth.substring(0, 4));
            int yearOfCurrent = Calendar.getInstance().get(Calendar.YEAR);
            int age = yearOfCurrent - yearOfBirth;
            int Nof10s = Math.round(age/10)*10;
            String strAge = Nof10s==0? "어린이":Nof10s+ "대";
            String strSex = UserManager.getInstance().HealthBaseInfo.get(index).member_sex == 0 ? ",남성" : ",여성";
            m_tvPregnant.setText(strAge + strSex);
        }

        if (UserManager.getInstance().HealthBaseInfo.get(index).member_dwelling == 1){
            m_tvDwelling.setText("1인 가구");
        } else if (UserManager.getInstance().HealthBaseInfo.get(index).member_dwelling == 2){
            m_tvDwelling.setText("2인 부부");
        } else if (UserManager.getInstance().HealthBaseInfo.get(index).member_dwelling == 3){
            m_tvDwelling.setText("3~4인 가구");
        } else if (UserManager.getInstance().HealthBaseInfo.get(index).member_dwelling == 4){
            m_tvDwelling.setText("5인 이상 가구");
        } else{
            m_tvDwelling.setText("선택 없음");
        }

        if (!UserManager.getInstance().HealthBaseInfo.get(index).member_disease.isEmpty()) {
            String[] arr = UserManager.getInstance().HealthBaseInfo.get(index).member_disease.split(",");
            String t = "";
            for (int i = 0; i < arr.length; i++) {
                if (i+1 < arr.length)
                    t = t + getResources().getStringArray(R.array.array_disease)[Integer.parseInt(arr[i])] + ", ";
                else if (i+1 == arr.length)
                    t = t + getResources().getStringArray(R.array.array_disease)[Integer.parseInt(arr[i])];
            }
            m_tvDisease.setText(t);
        }
        if (!UserManager.getInstance().HealthBaseInfo.get(index).member_interest_health.isEmpty()) {
            String[] list = getResources().getStringArray(R.array.array_interest_health);
            String[] arr = UserManager.getInstance().HealthBaseInfo.get(index).member_interest_health.split(",");
            String text = "";

            for (String One : arr)     text += list[Integer.parseInt(One)] + ",";
            if (text.length() > 0)
                text = text.substring(0, text.length() - 1);
            text = text.replace("\n", "");
            m_tvInterest.setText(text);
        }

        if (!UserManager.getInstance().HealthBaseInfo.get(index).member_prefer_healthfood.isEmpty()) {
            String[] arr = UserManager.getInstance().HealthBaseInfo.get(index).member_prefer_healthfood.split(",");
            String t = "";

            for (int i = 0; i < arr.length; i++) {
                if (i+1 < arr.length)
                    t = t + getResources().getStringArray(R.array.array_prefer_healthfood)[Integer.parseInt(arr[i])] + ", ";
                else if (i+1 == arr.length)
                    t = t + getResources().getStringArray(R.array.array_prefer_healthfood)[Integer.parseInt(arr[i])];
            }
            m_tvPrefer.setText(t);
        }
    }

    private void updatePhotoListView() {
        //이 아래는 본인을 제외한 리스트만 사진을 update하는 내용임 by 동현
        ////////////////////////////////////////우선은 사진!! by 동현///////////////////////////////////////////////
        m_uiLlyPhoto.removeAllViews();

        for (int i = 1; i < UserManager.getInstance().HealthBaseInfo.size(); i++) {
            final PhotoListLayout layout = new PhotoListLayout(this, i, UserManager.getInstance().arr_profile_photo_URL.get(i), m_listener, 1);
            layout.setLocalImage(false);
            layout.setFamName();
            m_uiLlyPhoto.addView(layout, i-1);
        }
        //////////////////////////////////////////////////////////////////////////////////
    }

    @Override
    public void updatePhotoListView(Integer famindex) {
        final PhotoListLayout layout = new PhotoListLayout(this, famindex, UserManager.getInstance().arr_profile_photo_URL.get(famindex), m_listener, 1);
        layout.setLocalImage(false);
        layout.setFamName();

        if(m_uiLlyPhoto.getChildCount() > famindex-1){
            m_uiLlyPhoto.removeViewAt(famindex-1);
            m_uiLlyPhoto.addView(layout);
        }else {
            while(m_uiLlyPhoto.getChildCount() < famindex-1){
                m_uiLlyPhoto.addView(layout);
            }
            m_uiLlyPhoto.addView(layout);
        }
    }

    View.OnClickListener m_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imv_listoffam:
                    index = (Integer) v.getTag();
                    if(UserManager.getInstance().arr_profile_photo_resID.get(index)!=-1)
                        Glide.with(mContext).load(resArr[UserManager.getInstance().arr_profile_photo_resID.get(index)]).into(m_imvImage);
                    else
                        Glide.with(mContext).load(UserManager.getInstance().arr_profile_photo_file.get(index).getPath()).into(m_imvImage);

                    updatHealthDataView();
                    setPhotoBorder(index);
                    break;
            }
        }
    };

    public void setPhotoBorder(int index){
        if(index==0){
            ((CircleImageView)findViewById(R.id.imv_me_photo_in_list)).setBorderWidth(3);
            ((CircleImageView)findViewById(R.id.imv_me_photo_in_list)).setBorderColor(ContextCompat.getColor(this, R.color.main_color_1));
            ((TextView)findViewById(R.id.tv_me_name_in_list)).setTypeface(null, Typeface.BOLD);
        }
        for(int i=1;i<UserManager.getInstance().HealthBaseInfo.size();i++){
            if(i==index){
                ((CircleImageView)m_uiLlyPhoto.getChildAt(i-1).findViewById(R.id.imv_listoffam)).setBorderWidth(3);
                ((CircleImageView)m_uiLlyPhoto.getChildAt(i-1).findViewById(R.id.imv_listoffam)).setBorderColor(ContextCompat.getColor(this, R.color.main_color_1));
                ((TextView)m_uiLlyPhoto.getChildAt(i-1).findViewById(R.id.tv_famname)).setTypeface(null, Typeface.BOLD);

                (m_imv_me_in_list).setBorderWidth(0);
                ((TextView)findViewById(R.id.tv_me_name_in_list)).setTypeface(null, Typeface.NORMAL);
            }else{
                ((CircleImageView)m_uiLlyPhoto.getChildAt(i-1).findViewById(R.id.imv_listoffam)).setBorderWidth(0);
                ((TextView)m_uiLlyPhoto.getChildAt(i-1).findViewById(R.id.tv_famname)).setTypeface(null, Typeface.NORMAL);
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.imv_back:
                intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.imv_me_photo_in_list:
                if(UserManager.getInstance().arr_profile_photo_resID.get(0)!=-1)
                    Glide.with(mContext).load(resArr[UserManager.getInstance().arr_profile_photo_resID.get(0)]).into(m_imvImage);
                else
                    Glide.with(mContext).load(UserManager.getInstance().arr_profile_photo_file.get(0).getPath()).into(m_imvImage);

                index = 0;
                updatHealthDataView();
                setPhotoBorder(index);
                break;
            case R.id.imv_add:
                m_userHealthBase = new UserHealthBase();
                m_userHealthBase.member_no = UserManager.getInstance().member_no;
                m_userHealthDetail = new UserHealthDetail();
                m_userHealthDetail.member_no = UserManager.getInstance().member_no;

                intent = new Intent(this, ManageProfileActivity.class);
                index = UserManager.getInstance().HealthBaseInfo.size();
                intent.putExtra("fam_index", index);
                intent.putExtra("HealthBase", m_userHealthBase);
                intent.putExtra("HealthDetail", m_userHealthDetail);
                intent.putExtra("isAdd", true);

                UserManager.getInstance().arr_profile_photo_URL.add(null);
                UserManager.getInstance().arr_profile_photo_file.add(null);
                UserManager.getInstance().arr_profile_photo_bitmap.add(null);
                UserManager.getInstance().arr_profile_photo_resID.add(-1);

                startActivityForResult(intent, 500);
                break;
            case R.id.imv_modify:
                m_userHealthBase = new UserHealthBase();
                m_userHealthBase.setUserHealthBase(UserManager.getInstance().HealthBaseInfo.get(index));
                m_userHealthDetail = new UserHealthDetail();
                m_userHealthDetail.setUserHealthDetail(UserManager.getInstance().HealthDetailInfo.get(index));

                intent = new Intent(this, ManageProfileActivity.class);
                intent.putExtra("fromReco", true);
                intent.putExtra("fam_index", index);
                intent.putExtra("HealthBase", m_userHealthBase);
                intent.putExtra("HealthDetail", m_userHealthDetail);
                startActivityForResult(intent, 100);
                break;
            case R.id.lly_pregnant:
                m_userHealthBase = new UserHealthBase();
                m_userHealthBase.setUserHealthBase(UserManager.getInstance().HealthBaseInfo.get(index));
                m_userHealthDetail = new UserHealthDetail();
                m_userHealthDetail.setUserHealthDetail(UserManager.getInstance().HealthDetailInfo.get(index));

                intent = new Intent(this, ManageProfileActivity.class);
                intent.putExtra("fromModi", true);
                intent.putExtra("fam_index", index);
                intent.putExtra("HealthBase", m_userHealthBase);
                intent.putExtra("HealthDetail", m_userHealthDetail);
                startActivityForResult(intent, 100);
                break;
            case R.id.lly_dwelling1:
                m_userHealthBase = new UserHealthBase();
                m_userHealthBase.setUserHealthBase(UserManager.getInstance().HealthBaseInfo.get(index));
                m_userHealthDetail = new UserHealthDetail();
                m_userHealthDetail.setUserHealthDetail(UserManager.getInstance().HealthDetailInfo.get(index));

                intent = new Intent(this, ManageLivingInfoActivity.class);
                intent.putExtra("fromModi", true);
                intent.putExtra("fam_index", index);
                intent.putExtra("HealthBase", m_userHealthBase);
                intent.putExtra("HealthDetail", m_userHealthDetail);
                startActivityForResult(intent, 40);
                break;
            case R.id.lly_disease1:
                intent = new Intent(this, DetailSurveyActivity.class);
                intent.putExtra("edit", true);
                intent.putExtra("type", "disease");
                intent.putExtra("disease", UserManager.getInstance().HealthBaseInfo.get(index).member_disease);
                minmax = new int[] {1,5};
                intent.putExtra("minmax", minmax);
                startActivityForResult(intent, 41);
                break;
            case R.id.lly_interest1:
                intent = new Intent(this, DetailSurveyActivity.class);
                intent.putExtra("edit", true);
                intent.putExtra("is_element_named_none", false);
                intent.putExtra("type", "interest");
                intent.putExtra("interest", UserManager.getInstance().HealthBaseInfo.get(index).member_interest_health);
                minmax = new int[] {1,5};
                intent.putExtra("minmax", minmax);
                startActivityForResult(intent, 42);
                break;
            case R.id.lly_prefer1:
                intent = new Intent(this, DetailSurveyActivity.class);
                intent.putExtra("edit", true);
                intent.putExtra("is_element_named_none", false);
                intent.putExtra("type", "prefer");
                intent.putExtra("prefer", UserManager.getInstance().HealthBaseInfo.get(index).member_prefer_healthfood);
                minmax = new int[] {1,5};
                intent.putExtra("minmax", minmax);
                startActivityForResult(intent, 43);
                break;
            case R.id.get_detail_health_info:
                /*
                intent = new Intent(this, RegisterBodyActivity.class);
                intent.putExtra("info_index", index+1);
                startActivity(intent);
                */
                intent = new Intent(this, ModifyDetailLifeActivity.class);
                intent.putExtra("fam_index", index);
                intent.putExtra("_userHealthDetail", UserManager.getInstance().HealthDetailInfo.get(index));
                startActivityForResult(intent, 300);
                break;
        }
    }

    @Override
    protected void onActivityResult(int p_requestCode,
                                    int p_resultCode,
                                    Intent p_intentActivity) {
        super.onActivityResult(p_requestCode, p_resultCode, p_intentActivity);
        if (p_resultCode == RESULT_OK) {        //p_resultCode = -1;
            switch (p_requestCode) {
                case 500:       //가족을 추가하는 액티비티 후의 requestMe code 임
                    updatePhotoListView();
                    if(UserManager.getInstance().HealthBaseInfo.size()-1==index)  {  //추가를 중도 취소하지 않고 완료한 경우 by 동현
                        updatHealthDataView();
                        setPhotoBorder(index);
                    } else {
                        index = 0;
                        setPhotoBorder(0);
                    }
                    break;
                case 100:       //ManageProfileActivity 액티비티 후의 requestMe code 임, 이름을 바꾸었을 수 있으므로 싹 다 새로 받아서 고치는 것으로.
                    if(index==0){
                        if(UserManager.getInstance().arr_profile_photo_resID.get(0)!=-1){
                            Glide.with(mContext).load(resArr[UserManager.getInstance().arr_profile_photo_resID.get(0)]).into(m_imvImage);
                            Glide.with(mContext).load(resArr[UserManager.getInstance().arr_profile_photo_resID.get(0)]).into(m_imv_me_in_list);
                        }else{
                            Glide.with(mContext).load(UserManager.getInstance().arr_profile_photo_file.get(0).getPath()).into(m_imvImage);
                            Glide.with(mContext).load(UserManager.getInstance().arr_profile_photo_file.get(0).getPath()).into(m_imv_me_in_list);
                        }
                    }else if(p_intentActivity!=null && p_intentActivity.getBooleanExtra("performDelete", false))
                        index=0;

                    updatePhotoListView();
                    updatHealthDataView();
                    setPhotoBorder(index);
                    break;
                case 40:       //주거정보 변경 by 동현
                    if (UserManager.getInstance().HealthBaseInfo.get(index).member_dwelling == 1){
                        m_tvDwelling.setText("1인 가구");
                    } else if (UserManager.getInstance().HealthBaseInfo.get(index).member_dwelling == 2){
                        m_tvDwelling.setText("2인 부부");
                    } else if (UserManager.getInstance().HealthBaseInfo.get(index).member_dwelling == 3){
                        m_tvDwelling.setText("3~4인 가구");
                    } else if (UserManager.getInstance().HealthBaseInfo.get(index).member_dwelling == 4){
                        m_tvDwelling.setText("5인 이상 가구");
                    } else{
                        m_tvDwelling.setText("선택 없음");
                    }
                    setPhotoBorder(index);
                    break;
                case 41:       //질환 by 동현
                    m_tvDisease.setText(p_intentActivity.getStringExtra("text"));
                    UserManager.getInstance().HealthBaseInfo.get(index).member_disease = p_intentActivity.getStringExtra("disease");
                    setPhotoBorder(index);

                    UserManager.getInstance().currentFamIndex = index;
                    m_nConnectType = NetUtil.apis_STORE_INFO_BODY_ALL;
                    mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, true);
                    break;
                case 42:       //관심 건강 분야 변경 by 동현
                    m_tvInterest.setText(p_intentActivity.getStringExtra("text"));
                    UserManager.getInstance().HealthBaseInfo.get(index).member_interest_health = p_intentActivity.getStringExtra("interest");
                    setPhotoBorder(index);

                    UserManager.getInstance().currentFamIndex = index;
                    m_nConnectType = NetUtil.apis_STORE_INFO_BODY_ALL;
                    mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, true);
                    break;
                case 43:       //선호 건강 제품 변경 by 동현
                    m_tvPrefer.setText(p_intentActivity.getStringExtra("text"));
                    UserManager.getInstance().HealthBaseInfo.get(index).member_prefer_healthfood = p_intentActivity.getStringExtra("prefer");
                    setPhotoBorder(index);

                    UserManager.getInstance().currentFamIndex = index;
                    m_nConnectType = NetUtil.apis_STORE_INFO_BODY_ALL;
                    mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, true);
                    break;
                case 300:       //상세 건강 정보의 변경 후의 requestMe code 임, 정보만 바뀌는 것으로.by 동현
                    setPhotoBorder(index);
                    break;
            }
        }else
            setPhotoBorder(index);
    }

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.imv_back));
    }
}
