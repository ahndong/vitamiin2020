package app.vitamiin.com.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;

import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.ConfirmDialog;
import app.vitamiin.com.R;
import app.vitamiin.com.common.CircleImageView;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.common.SelectPhotoManager;
import app.vitamiin.com.common.UserHealthBase;
import app.vitamiin.com.common.UserHealthDetail;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.common.Util;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;
import app.vitamiin.com.login.BirthdaySelectDialog;
import app.vitamiin.com.login.LoginActivity;
import app.vitamiin.com.login.PhotoSelectDialog;
import app.vitamiin.com.login.RegisterIDActivity;

public class ManageProfileActivity extends BaseActivity implements View.OnClickListener, OnParseJSonListener, Serializable, NetUtil.PhotoDownCompleteCallback{
    int m_nResultType = 200;
    Context m_context;
    NetUtil mNetUtil;
    NetUtil.connectAndgetServer mNetUtilConnetServer;

    EditText m_edtUserName, m_edtUserNick;
    TextView m_tvMale, m_tvFemale, m_tvBirth, m_tv_examinee, m_tv_pregnant, m_tv_lactating, m_tv_climacterium, m_tvCheckNick, m_edtUserId, m_tvCheckId, m_tvNext;
    boolean m_checkNick = false;
    boolean m_checkID = false;

    private Bitmap m_uploadBitmap = null;
    private File m_uploadImageFile = null;
    private int m_uploadImageResID = -1;

    public int m_nConnectType = 0;

    Intent m_intent;
    int index;
    UserHealthBase m_userHealthBase = new UserHealthBase();
    UserHealthDetail m_userHealthDetail = new UserHealthDetail();

    PhotoSelectDialog m_selectPhotoDlg = null;
    BirthdaySelectDialog m_selectBirthDlg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_profile);
        m_context = this;
        mNetUtil = new NetUtil();
        mNetUtilConnetServer = mNetUtil.new connectAndgetServer(this);
        m_intent = getIntent();

        initView();
        updateInfo();
    }

    private void initView() {
        findViewById(R.id.tv_back).setOnClickListener(this);
        m_tvNext = (TextView) findViewById(R.id.tv_next);
        m_tvNext.setOnClickListener(this);
        findViewById(R.id.tv_delete).setOnClickListener(this);

        m_edtUserId = (EditText) findViewById(R.id.edt_userid);
        m_edtUserId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                m_tvCheckId.setSelected(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                m_checkID = false;
                m_tvCheckId.setSelected(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                m_tvCheckId.setSelected(false);
                checkNext();

            }
        });

        m_edtUserName = (EditText) findViewById(R.id.edt_name);
        m_edtUserName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                checkNext();
                return false;
            }
        });

        m_edtUserNick = (EditText) findViewById(R.id.edt_nick);
        m_edtUserNick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                m_checkNick = false;
                m_tvCheckNick.setSelected(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkNext();
            }
        });

        m_tvMale = (TextView) findViewById(R.id.tv_male);
        m_tvFemale = (TextView) findViewById(R.id.tv_female);
        m_tv_examinee = (TextView) findViewById(R.id.tv_examinee);
        m_tv_pregnant = (TextView) findViewById(R.id.tv_pregnant);
        m_tv_lactating = (TextView) findViewById(R.id.tv_lactating);
        m_tv_climacterium = (TextView) findViewById(R.id.tv_climacterium);
        m_tvCheckNick = (TextView) findViewById(R.id.tv_checknick);
        m_tvBirth = (TextView) findViewById(R.id.tv_birth);
        //m_tvPregnant = (TextView) findViewById(R.id.tv_pregnant);
        m_tvMale.setOnClickListener(this);
        m_tvFemale.setOnClickListener(this);
        m_tvBirth.setOnClickListener(this);

        m_tvMale.setSelected(false);
        m_tvFemale.setSelected(false);
        m_tv_examinee.setOnClickListener(this);
        m_tv_pregnant.setOnClickListener(this);
        m_tv_lactating.setOnClickListener(this);
        m_tv_climacterium.setOnClickListener(this);

        m_tvCheckNick = (TextView) findViewById(R.id.tv_checknick);
        m_tvCheckNick.setOnClickListener(this);
        m_tvCheckId = (TextView) findViewById(R.id.tv_checkid);
        m_tvCheckId.setOnClickListener(this);
        findViewById(R.id.imv_good).setOnClickListener(this);
    }

    private void updateInfo() {//수정이던 add이던 fam_index는 있다. by 동현
        index = m_intent.getIntExtra("fam_index",-1);
        m_userHealthBase = (UserHealthBase) m_intent.getSerializableExtra("HealthBase");
        m_userHealthDetail = (UserHealthDetail) m_intent.getSerializableExtra("HealthDetail");

        UserManager.getInstance().oldname = m_userHealthBase.member_name;
        m_edtUserId.setText(UserManager.getInstance().member_id);
        m_userHealthBase.member_sex = m_userHealthBase.member_sex == 0 ? 0:1;
        m_edtUserName.setText(m_userHealthBase.member_name);
        m_edtUserNick.setText(m_userHealthBase.member_nick_name);
        m_tvBirth.setText(m_userHealthBase.member_birth);
        m_tvMale.setSelected(m_userHealthBase.member_sex==0);
        m_tvFemale.setSelected(m_userHealthBase.member_sex==1);

        m_tv_examinee.setSelected(m_userHealthBase.member_examinee==0);
        m_tv_pregnant.setSelected(m_userHealthBase.member_pregnant==0);
        m_tv_lactating.setSelected(m_userHealthBase.member_lactating==0);
        m_tv_climacterium.setSelected(m_userHealthBase.member_climacterium==0);

        setProfile(index);

        if (m_intent.getBooleanExtra("registerEMAIL", false)) {
            m_tvCheckId.setSelected(true);
            m_checkID = true;
            m_edtUserId.setEnabled(false);
        } else if (m_intent.getBooleanExtra("registerSNS", false)) {
            mNetUtil.setPhotoDownCompleteCallback(this);
            mNetUtil.setProfilePhoto(this, 0, UserManager.getInstance().arr_profile_photo_URL.get(0));
            m_tvCheckId.setSelected(true);
            m_checkID = true;
//            if(UserManager.getInstance().member_type.equals("facebook")){}
//            else if(UserManager.getInstance().member_type.equals("kakao")){}
        } else if (m_intent.getBooleanExtra("isAdd", false)){
            ((TextView) findViewById(R.id.tv_title)).setText("가족 프로필 추가 - 1/3");
            findViewById(R.id.tv_delete).setVisibility(View.INVISIBLE);
            m_checkID = true;
        } else if (m_intent.getBooleanExtra("fromReco", false)){
            ((TextView) findViewById(R.id.tv_title)).setText("프로필 정보 수정 - 1/3");
            m_tvCheckId.setSelected(true);
            m_checkID = true;
            m_tvCheckNick.setSelected(true);
            m_checkNick = true;
            m_checkID = true;
        } else if (m_intent.getBooleanExtra("fromModi", false)){
            ((TextView) findViewById(R.id.tv_title)).setText("프로필 정보 수정");
            m_tvNext.setText("완료");
            m_tvCheckId.setSelected(true);
            m_checkID = true;
            m_tvCheckNick.setSelected(true);
            m_checkNick = true;
            m_checkID = true;
        }

        if(index==0){
            findViewById(R.id.fl_IDinput).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_delete).setVisibility(View.INVISIBLE);
        }

        if(m_intent.getBooleanExtra("turnBack", false)){
            m_tvCheckId.setSelected(true);
            m_checkID = true;

            m_tvCheckNick.setSelected(true);
            m_checkNick = true;
        }
        checkNext();
    }

    void setProfile(int index){
        m_uploadImageFile = UserManager.getInstance().arr_profile_photo_file.get(index);
        m_uploadBitmap = UserManager.getInstance().arr_profile_photo_bitmap.get(index);
        m_uploadImageResID = UserManager.getInstance().arr_profile_photo_resID.get(index);

        if (m_uploadImageResID != -1) {
            setSelectedImage(m_uploadImageResID);
        } else if (m_uploadBitmap != null || m_uploadImageFile != null) {
            setSelectedImage(m_uploadBitmap, m_uploadImageFile);
        } else {
            if(new Util().getAutoImageResID(index) != -1)
                setSelectedImage(new Util().getAutoImageResID(index));
        }
    }

    @Override
    public void updatePhotoListView(Integer index) {
        setProfile(index);
    }

    public boolean checkNext() {
        Boolean ret;
        m_tvNext.setSelected(false);
        if (m_edtUserName.getText().toString().length() == 0 || m_edtUserNick.getText().toString().length() == 0) {
            ret = false;
        }
        else if (m_tvBirth.getText().toString().length() == 0) {
            ret = false;
        }
        else if (!m_checkNick || !m_checkID) {
            ret = false;
        }
        else {
            m_tvNext.setSelected(true);
            ret = true;
        }
        return ret;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_back:
                if (m_intent.getBooleanExtra("registerEMAIL", false)) {    //이메일 회원 가입 중
                    UserManager.getInstance().member_nick_name = m_edtUserNick.getText().toString();
                    UserManager.getInstance().member_name = m_edtUserName.getText().toString();
                    UserManager.getInstance().member_sex = m_tvMale.isSelected()? 0:1;
                    UserManager.getInstance().member_birth = m_tvBirth.getText().toString();

                    m_userHealthBase.member_nick_name = m_edtUserNick.getText().toString();
                    m_userHealthBase.member_name = m_edtUserName.getText().toString();
                    m_userHealthBase.member_sex = m_tvMale.isSelected()? 0:1;
                    m_userHealthBase.member_birth = m_tvBirth.getText().toString();

                    m_userHealthBase.member_examinee = m_tv_examinee.isSelected()? 1:0;
                    m_userHealthBase.member_pregnant = m_tv_pregnant.isSelected()? 1:0;
                    m_userHealthBase.member_lactating = m_tv_lactating.isSelected()? 1:0;
                    m_userHealthBase.member_climacterium = m_tv_climacterium.isSelected()? 1:0;

//                    UserManager.getInstance().arr_profile_photo_file.set(index, null);
//                    if (m_uploadImageFile != null) {
//                        UserManager.getInstance().arr_profile_photo_file.set(index, m_uploadImageFile);
//                        UserManager.getInstance().arr_profile_photo_bitmap.set(index, m_uploadBitmap);
//                        UserManager.getInstance().arr_profile_photo_resID.set(index, -1);
//                    }
//                    if (m_uploadImageResID != -1) {
//                        UserManager.getInstance().arr_profile_photo_file.set(index, null);
//                        UserManager.getInstance().arr_profile_photo_bitmap.set(index , null);
//                        UserManager.getInstance().arr_profile_photo_resID.set(index, m_uploadImageResID);
//                    }

                    intent = new Intent(this, RegisterIDActivity.class);
                    intent.putExtra("fam_index", index);
                    intent.putExtra("HealthBase", m_userHealthBase);
                    intent.putExtra("HealthDetail", m_userHealthDetail);
                    intent.putExtra("turnBack", true);
                    startActivity(intent);
                    finish();
                } else if (m_intent.getBooleanExtra("registerSNS", false)) {
                    UserManager.getInstance().releaseUserData();
                    UserManager.getInstance().saveData(this,"","","","");
                    intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("turnBack", true);
                    startActivity(intent);
                    finish();
                } else if (m_intent.getBooleanExtra("isAdd", false)){
                    UserManager.getInstance().arr_profile_photo_URL.remove(index);
                    UserManager.getInstance().arr_profile_photo_file.remove(index);
                    UserManager.getInstance().arr_profile_photo_bitmap.remove(index);
                    UserManager.getInstance().arr_profile_photo_resID.remove(index);
                    setResult(RESULT_OK);
                    finish();
                } else if (m_intent.getBooleanExtra("fromReco", false)){
                    finish();
                } else if (m_intent.getBooleanExtra("fromModi", false)){
                    finish();
                }
                break;
            case R.id.tv_next:
                if(m_intent.getBooleanExtra("registerSNS", false)){
                    if (m_edtUserId.getText().toString().length() == 0) {
                        Toast.makeText(this, "이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }else if(m_edtUserId.getText().toString().length() > 50){
                        Toast.makeText(this, "이메일의 글자수가 50자 이하인 경우에만 가입이 가능합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!Util.isValidEmail(m_edtUserId.getText().toString())) {
                        Toast.makeText(this, "이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!m_checkID) {
                        Toast.makeText(this, "이메일 중복확인을 해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (m_edtUserName.getText().toString().length() == 0) {
                    Toast.makeText(this, "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }else if(m_edtUserName.getText().toString().length() > 20){
                    Toast.makeText(this, "이름의 글자수는 20자 이하로 줄여주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (m_edtUserNick.getText().toString().length() == 0) {
                    Toast.makeText(this, "닉네임을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }else if(m_edtUserNick.getText().toString().length() > 20){
                    Toast.makeText(this, "닉네임 글자수는 20자 이하로 줄여주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!m_checkNick) {
                    Toast.makeText(this, "닉네임 중복확인을 해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (m_tvBirth.getText().toString().length() == 0) {
                    Toast.makeText(this, "생년월일을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Util.hideKeyPad(this);

                if(index==0){
                    if(m_intent.getBooleanExtra("registerSNS", false))
                        UserManager.getInstance().member_id = m_edtUserId.getText().toString();

                    UserManager.getInstance().member_nick_name = m_edtUserNick.getText().toString();
                    UserManager.getInstance().member_name = m_edtUserName.getText().toString();
                    UserManager.getInstance().member_birth = m_tvBirth.getText().toString();
                    UserManager.getInstance().member_sex = m_tvMale.isSelected()? 0:m_tvFemale.isSelected()? 1:-1;
                }
                m_userHealthBase.member_nick_name = m_edtUserNick.getText().toString();
                m_userHealthDetail.member_nick_name = m_edtUserNick.getText().toString();
                m_userHealthBase.member_name = m_edtUserName.getText().toString();
                m_userHealthDetail.member_name = m_edtUserName.getText().toString();
                m_userHealthBase.member_birth = m_tvBirth.getText().toString();
                m_userHealthBase.member_sex = m_tvMale.isSelected()? 0:m_tvFemale.isSelected()? 1:-1;
                m_userHealthBase.member_examinee = m_tv_examinee.isSelected()? 0:1;
                m_userHealthBase.member_pregnant = m_tv_pregnant.isSelected()? 0:1;
                m_userHealthBase.member_lactating = m_tv_lactating.isSelected()? 0:1;
                m_userHealthBase.member_climacterium = m_tv_climacterium.isSelected()? 0:1;

                if (m_uploadImageResID != -1) {//resID가 -1이 아닌값으로 세팅이 되어 있다면 이미지 파일은 다 null로 하고 resID를 올린다.
                    UserManager.getInstance().arr_profile_photo_resID.set(index, m_uploadImageResID);
                    UserManager.getInstance().arr_profile_photo_URL.set(index, null);
                    UserManager.getInstance().arr_profile_photo_file.set(index, m_uploadImageFile);
                    UserManager.getInstance().arr_profile_photo_bitmap.set(index, m_uploadBitmap);
                }
                else {
                    if (m_uploadImageFile != null) {
                        UserManager.getInstance().arr_profile_photo_resID.set(index, -1);
                        UserManager.getInstance().arr_profile_photo_file.set(index,m_uploadImageFile);
                        UserManager.getInstance().arr_profile_photo_bitmap.set(index, m_uploadBitmap);
                    } else {
                        setSelectedImage(new Util().getAutoImageResID2(m_userHealthBase.member_birth, m_userHealthBase.member_sex));
                        UserManager.getInstance().arr_profile_photo_resID.set(index, new Util().getAutoImageResID2(m_userHealthBase.member_birth, m_userHealthBase.member_sex));
                        UserManager.getInstance().arr_profile_photo_URL.set(index, null);
                        UserManager.getInstance().arr_profile_photo_file.set(index, m_uploadImageFile);
                        UserManager.getInstance().arr_profile_photo_bitmap.set(index, m_uploadBitmap);
                    }
                }

                while(UserManager.getInstance().HealthBaseInfo.size() < index+1)
                    UserManager.getInstance().HealthBaseInfo.add(new UserHealthBase());
                while(UserManager.getInstance().HealthDetailInfo.size() < index+1)
                    UserManager.getInstance().HealthDetailInfo.add(new UserHealthDetail());

                UserManager.getInstance().currentFamIndex = index;
                UserManager.getInstance().HealthBaseInfo.get(index).setUserHealthBase(m_userHealthBase);
                UserManager.getInstance().HealthDetailInfo.get(index).setUserHealthDetail(m_userHealthDetail);

                if (m_intent.getBooleanExtra("fromModi", false)) {//건강 정보 수정을 종료하는 코드 by 동현
                    m_nConnectType = NetUtil.apis_UPDATE_PROFILE;
                    mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, true);
                } else {
                    intent = new Intent(this, ManageLivingInfoActivity.class);
                    intent.putExtra("fam_index", index);
                    intent.putExtra("HealthBase", m_userHealthBase);
                    intent.putExtra("HealthDetail", m_userHealthDetail);
                    intent.putExtra("registerEMAIL", getIntent().getBooleanExtra("registerEMAIL", false));
                    intent.putExtra("registerSNS", getIntent().getBooleanExtra("registerSNS", false));
                    intent.putExtra("isAdd", getIntent().getBooleanExtra("isAdd", false));
                    intent.putExtra("fromReco", getIntent().getBooleanExtra("fromReco", false));

                    startActivityForResult(intent, 141);
                }
                break;
            case R.id.tv_checknick:
                if(m_tvCheckNick.isSelected()){
                    Toast.makeText(this, "사용하셔도 되는 닉네임입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (m_edtUserNick.getText().toString().length() == 0) {
                    Toast.makeText(this, "닉네임을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (m_edtUserNick.getText().toString().length() > 9) {
                    Toast.makeText(this, "닉네임의 글자 수가 \n8글자가 넘지 않도록 해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Util.hideKeyPad(this);
                m_nConnectType = NetUtil.apis_CHECK_NICK;
                mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, m_edtUserNick.getText().toString());
                break;
            case R.id.tv_checkid:
                if(m_tvCheckId.isSelected()){
                    Toast.makeText(this, "사용하셔도 되는 메일 주소입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (m_edtUserId.getText().toString().length() == 0) {
                    Toast.makeText(this, "이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Util.isValidEmail(m_edtUserId.getText().toString())) {
                    Toast.makeText(this, "이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Util.hideKeyPad(this);
                m_nConnectType = NetUtil.apis_CHECK_ID;
                mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, m_edtUserId.getText().toString());
                break;
            case R.id.tv_delete:
                new ConfirmDialog(this, "가족 삭제", m_userHealthBase.member_name + "님을 가족에서 삭제하시겠습니까?", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserManager.getInstance().HealthBaseInfo.remove(index);
                        UserManager.getInstance().HealthDetailInfo.remove(index);
                        UserManager.getInstance().arr_profile_photo_URL.remove(index);
                        UserManager.getInstance().arr_profile_photo_file.remove(index);
                        UserManager.getInstance().arr_profile_photo_bitmap.remove(index);
                        UserManager.getInstance().arr_profile_photo_resID.remove(index);

                        m_nConnectType = NetUtil.apis_DELETE_FAMILY;
                        mNetUtilConnetServer.connectServer(m_context, handler, m_nConnectType, "");
                    }
                }).show();
                break;
            case R.id.imv_good:
                if (m_selectPhotoDlg == null)
                    m_selectPhotoDlg = new PhotoSelectDialog(this, 1);
                m_selectPhotoDlg.show();
                break;
            case R.id.tv_birth:
                if (m_selectBirthDlg == null)
                    m_selectBirthDlg = new BirthdaySelectDialog(this, 1);
                m_selectBirthDlg.show();
                break;
            case R.id.tv_male:
                if (m_tv_lactating.isSelected() || m_tv_pregnant.isSelected()){
                    Toast.makeText(this, "임산부 또는 수유부로 체크하시고, 성별을 남자로 선택하실 수는 없습니다. 가족 프로필 추가 등록 기능을 이용해주세요.", Toast.LENGTH_SHORT).show();
                    break;
                }
                Util.hideKeyPad(this);
                m_tvMale.setSelected(true);
                m_tvFemale.setSelected(false);
                break;
            case R.id.tv_female:
                Util.hideKeyPad(this);
                m_tvMale.setSelected(false);
                m_tvFemale.setSelected(true);
                break;
            //m_tv_examinee, m_tv_pregnant, m_tv_lactating, m_tv_climacterium;
            case R.id.tv_examinee:
                if (m_tv_examinee.isSelected()) {
                    m_tv_examinee.setSelected(false);
                } else {
                    m_tv_examinee.setSelected(true);
                    m_tv_pregnant.setSelected(false);
                    m_tv_lactating.setSelected(false);
                    m_tv_climacterium.setSelected(false);
                }
                break;
            case R.id.tv_pregnant:
                if (m_tvMale.isSelected()){
                    Toast.makeText(this, "성별을 남자로 선택하고, 임산부로 등록하실 수는 없습니다. 가족 프로필 추가 등록 기능을 이용해주세요.", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (m_tv_pregnant.isSelected()) {
                    m_tv_pregnant.setSelected(false);
                } else {
                    m_tv_examinee.setSelected(false);
                    m_tv_pregnant.setSelected(true);
                    m_tv_lactating.setSelected(false);
                    m_tv_climacterium.setSelected(false);
                }
                break;
            case R.id.tv_lactating:
                if (m_tvMale.isSelected()){
                    Toast.makeText(this, "성별을 남자로 선택하고, 수유부로 등록하실 수는 없습니다. 가족 프로필 추가 등록 기능을 이용해주세요.", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (m_tv_lactating.isSelected()) {
                    m_tv_lactating.setSelected(false);
                } else {
                    m_tv_examinee.setSelected(false);
                    m_tv_pregnant.setSelected(false);
                    m_tv_lactating.setSelected(true);
                    m_tv_climacterium.setSelected(false);
                }
                break;
            case R.id.tv_climacterium:
                if (m_tv_climacterium.isSelected()) {
                    m_tv_climacterium.setSelected(false);
                } else {
                    m_tv_examinee.setSelected(false);
                    m_tv_pregnant.setSelected(false);
                    m_tv_lactating.setSelected(false);
                    m_tv_climacterium.setSelected(true);
                }
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
        closeProgress();
        HttpRequester.getInstance().stopNetThread();

        int resultCode = HttpRequester.getInstance().getResultCode();
        String strMsg = HttpRequester.getInstance().getResultMsg();

        if (resultCode == Net.CONNECTION_SUCCSES) {
            if (m_nConnectType == NetUtil.apis_UPDATE_PROFILE){
                setResult(RESULT_OK);
                finish();
            } else if (m_nConnectType == NetUtil.apis_DELETE_FAMILY){
                Intent intent = new Intent();
                intent.putExtra("performDelete", true);
                setResult(RESULT_OK, intent);
                finish();
            } else if (m_nConnectType == NetUtil.apis_CHECK_ID || m_nConnectType == NetUtil.apis_CHECK_NICK){
                if (m_nResultType == 10) {                  // 이후 처리
                    Toast.makeText(this, "사용 가능한 이메일입니다.", Toast.LENGTH_SHORT).show();
                    m_tvCheckId.setSelected(true);
                    m_checkID = true;
                } else if(m_nResultType == 11) {
                    Toast.makeText(this, "사용 중인 이메일입니다.", Toast.LENGTH_SHORT).show();
                    m_tvCheckId.setSelected(false);
                    m_checkID = false;
                } else if (m_nResultType == 20) {
                    Toast.makeText(this, "사용 가능한 닉네임입니다.", Toast.LENGTH_SHORT).show();
                    m_tvCheckNick.setSelected(true);
                    m_checkNick = true;
                } else if (m_nResultType == 21) {
                    Toast.makeText(this, "사용 중인 닉네임입니다.", Toast.LENGTH_SHORT).show();
                    m_tvCheckNick.setSelected(false);
                    m_checkNick = false;
                }
                checkNext();
            }
        } else {
            if (!"".equals(strMsg)) {Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();}
        }
    }

    JSONObject _jResult;

    @Override
    public void onParseJSon(JSONObject j_source) {
        _jResult = j_source;
        try {
            JSONObject result = _jResult.getJSONObject(Net.NET_VALUE_RESULT);
            m_nResultType = result.getInt(Net.NET_VALUE_TYPE); //NET_VALUE_CODE
            if (m_nConnectType == NetUtil.apis_UPDATE_PROFILE)
                UserManager.getInstance().arr_profile_photo_URL.set(m_intent.getIntExtra("fam_index", -1), _jResult.getString("photoURL"));
        } catch (JSONException e) {e.printStackTrace();}
    }

    @Override
    protected void onActivityResult(int p_requestCode,
                                    int p_resultCode,
                                    Intent p_intentActivity) {
        super.onActivityResult(p_requestCode, p_resultCode, p_intentActivity);
        //intent를  100 이라는 request와 함께 묶어서 만들어낸 startActivity가 없으므로 이부분은 실행되지 않는 것 같다 왜 있는가. 추후 삭제 by 동현
        //p_resultCode = -1;
        if (p_resultCode == RESULT_OK) {
            switch (p_requestCode) {
                case 141:
                    setResult(RESULT_OK, p_intentActivity);
                    finish();
                    break;
                case SelectPhotoManager.CROP_FROM_CAMERA:
                case SelectPhotoManager.PICK_FROM_CAMERA:
                case SelectPhotoManager.PICK_FROM_FILE:
                    if (m_selectPhotoDlg != null) {
                        m_selectPhotoDlg.onActivityResult(p_requestCode, p_resultCode, p_intentActivity);
                    }
                    break;
            }
        }else if (p_resultCode == RESULT_CANCELED) {
            if(p_requestCode == SelectPhotoManager.CROP_FROM_CAMERA
                    || p_requestCode == SelectPhotoManager.PICK_FROM_CAMERA
                    || p_requestCode == SelectPhotoManager.PICK_FROM_FILE){
            }
            else{
                m_intent = p_intentActivity;
                updateInfo();
            }
        }
    }

    public void setBirthday(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        int iyear = calendar.get(Calendar.YEAR) - year;
        int imonth = month + 1;
        int iday = day + 1;

        String smonth = "" + imonth;
        if (imonth < 10)
            smonth = "0" + smonth;
        String sday = "" + iday;
        if (iday < 10)
            sday = "0" + sday;
        m_tvBirth.setText("" + iyear + "-" + smonth + "-" + sday);
        checkNext();
    }

    public void setSelectedImage(Bitmap b, File f) {
        if (b != null || f != null) {
            m_uploadImageResID = -1;
            m_uploadBitmap = b;
            m_uploadImageFile = f;

            if (m_uploadImageFile != null)
                Glide.with(m_context).load(m_uploadImageFile.getPath()).into((CircleImageView) findViewById(R.id.imv_good));
//            else if (m_uploadBitmap != null)
//                Glide.with(m_context).load(m_uploadBitmap).into((CircleImageView) findViewById(R.id.imv_photo));
        }
    }

    public void setSelectedImage(int resID) {
        m_uploadImageResID = resID;
        m_uploadBitmap = null;
        m_uploadImageFile = null;

        int resArr[] = {R.drawable.ic_male_1, R.drawable.ic_male_2, R.drawable.ic_male_3, R.drawable.ic_male_4,
                R.drawable.ic_female_1, R.drawable.ic_female_2, R.drawable.ic_female_3, R.drawable.ic_female_4};
        Glide.with(m_context).load(resArr[resID]).into(((CircleImageView) findViewById(R.id.imv_good)));

        String fileName_preset = Environment.getExternalStorageDirectory() + "/Vitamin/presetIMG_" + String.valueOf(resID) + ".png";
        m_uploadBitmap = BitmapFactory.decodeResource(this.getResources(), resArr[resID]);
        if (Util.saveBitmapToFileCache(m_uploadBitmap, fileName_preset, true))
            m_uploadImageFile = new File(fileName_preset);
//        if (resID < 4) {
//            m_tvMale.setSelected(true);
//            m_tvFemale.setSelected(false);
//        } else {
//            m_tvMale.setSelected(false);
//            m_tvFemale.setSelected(true);
//        }
    }

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.tv_back));
    }
}
