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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.ConfirmDialog;
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
import app.vitamiin.com.setting.FaqNoticeActivity;


public class ExpWriteActivity extends BaseActivity implements View.OnClickListener, OnParseJSonListener {
    Context m_context = this;
    NetUtil mNetUtil;
    NetUtil.connectAndgetServer mNetUtilConnetServer;
    JSONObject _jResult;
    int m_nResultType = 200;

    int m_nConnectType = NetUtil.GET_PRODUCT_BY_ID;
    ScrollView m_scvView;

    GoodInfo m_good = new GoodInfo();
    private int good_index =0;
    private int index_knowhow;

    TextView m_tvGoodName, m_tvBusiness;
    ImageView m_imvPhoto;
    EditText m_edtTitle, m_edtContent, m_edt_knowhow;
    TextView m_tvCategory, m_tvPeriod, m_tvCount, m_tvHash;
    LinearLayout m_lly_knowhow_list;
    int m_nCategory = -1; //0~

    int m_nPeriod = -1;
    int m_nCount = -1;
    ArrayList<GoodInfo> m_arr_GoodInfo = new ArrayList<>();
    ReviewInfo m_r_info = new ReviewInfo();

    private LinearLayout m_uiLlyPhoto;
    ArrayList<String> oldphotoid = new ArrayList<String>();
    private LinearLayout m_uiLlyProd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_context = this;
        setContentView(R.layout.activity_exper_review_write);
        mNetUtil = new NetUtil();
        mNetUtilConnetServer = mNetUtil.new connectAndgetServer(this);

        initView();

        m_r_info.photos = new ArrayList<>();
        m_r_info.photos_id = new ArrayList<>();
        m_r_info.knowhow = new ArrayList<>();
        m_r_info._good_photo_urls = new ArrayList<>();
        m_r_info._good_id = new ArrayList<>();
        m_r_info._good_name = new ArrayList<>();
        m_r_info._good_business = new ArrayList<>();
        m_r_info._good_photo_files = new ArrayList<>();
        m_r_info.photosFile = new ArrayList<>();

        if (getIntent().getBooleanExtra("edit", false)) {
//            findViewById(R.id.tv_change).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.tv_title)).setText("노하우 공유 게시물 수정하기");

            m_r_info = (ReviewInfo) getIntent().getSerializableExtra("info");
            m_arr_GoodInfo = (ArrayList<GoodInfo>) getIntent().getSerializableExtra("arrGoodList");

            updateReview();

            for(int i = 0; i< m_r_info.knowhow.size(); i++){                addKnowToLLY(i);            }
            for(int i = 0; i< m_r_info._good_id.size(); i++){                addGoodToLLY(i);            }

            oldphotoid = (ArrayList<String>) m_r_info.photos_id.clone();
        }
    }

    private void initView() {
        if (getIntent().getIntExtra("review_type", 1) == 1)
            ((TextView) findViewById(R.id.tv_title)).setText("노하우 공유 리뷰 쓰기");
        else
            ((TextView) findViewById(R.id.tv_title)).setText("식단 리뷰 쓰기");
        findViewById(R.id.imv_back).setOnClickListener(this);

        m_edtTitle = (EditText) findViewById(R.id.edt_title);
        m_edtContent = (EditText) findViewById(R.id.edt_content);
        m_edt_knowhow = (EditText) findViewById(R.id.edt_knowhow);
        m_lly_knowhow_list = (LinearLayout) findViewById(R.id.lly_knowhow_list);
        m_lly_knowhow_list.removeAllViews();

        m_imvPhoto = (ImageView) findViewById(R.id.imv_good);
        m_tvGoodName = (TextView) findViewById(R.id.tv_good);
        m_tvBusiness = (TextView) findViewById(R.id.tv_business);

        m_uiLlyProd = (LinearLayout) findViewById(R.id.lly_prod);
        findViewById(R.id.tv_prod_add).setOnClickListener(this);
        //사진추가버튼
        findViewById(R.id.imv_add).setOnClickListener(this);
        findViewById(R.id.imv_add_btn).setOnClickListener(this);
        findViewById(R.id.tv_knowhow_add).setOnClickListener(this);

        findViewById(R.id.tv_finish).setOnClickListener(this);

        findViewById(R.id.tv_reg_hash).setOnClickListener(this);
        m_tvHash = (TextView) findViewById(R.id.tv_hash);

        m_uiLlyPhoto = (LinearLayout) findViewById(R.id.lly_photo);

        findViewById(R.id.lly_category).setOnClickListener(this);
        m_tvCategory = (TextView) findViewById(R.id.tv_category);

        findViewById(R.id.lly_period_review).setOnClickListener(this);
        m_tvPeriod = (TextView) findViewById(R.id.tv_period_review);

        m_tvCount = (TextView) findViewById(R.id.tv_taken_count);
        m_scvView = (ScrollView) findViewById(R.id.scv_view);
        m_scvView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                hideKeypad();
            }
        });
        m_scvView.scrollTo(0,0);
        m_tvHash.setText("");

        findViewById(R.id.tv_standard).setOnClickListener(this);
    }

    private void addGoodToLLY(int i) {
        ////////////////////////////////////////우선은 리셋!! by 동현///////////////////////////////////////////////
        final PhotoListLayout layout = new PhotoListLayout(this, i, Net.URL_SERVER1 + m_arr_GoodInfo.get(i)._imagePath, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.imv_delete:
                        good_index = Integer.parseInt(view.getTag().toString());
                        delete_product();
                        //onClick(findViewById(R.id.imv_invi));
                        break;
                    case R.id.imv_listoffam:
                        good_index = (Integer) view.getTag();
                        m_nConnectType = NetUtil.GET_PRODUCT_BY_ID;
                        mNetUtilConnetServer.connectServer(m_context, handler, m_nConnectType, m_arr_GoodInfo.get(good_index)._id);
                        break;
                }
            }
        }, 3);
        //layout.setLocalImage(m_arr_GoodInfo.get(i)._imagePath);
        layout.setLocalImage(false);
        layout.setName(m_r_info._good_name.get(i));
        //layout.setName(m_arr_GoodInfo.get(i)._name);
        layout.setBusiness(m_r_info._good_business.get(i));
        m_uiLlyProd.addView(layout, i);


            //File file = new File(Net.URL_SERVER1 + UserManager.getInstance().arr_profile_photo_URL.get(i));
            //UserManager.getInstance().arr_profile_photo_file.set(i,file);
            //m_uploadImageID.add(m_r_info.photos_id.get(i));
        //m_tvGoodName.setText(m_r_info._name);
        //m_tvBusiness.setText(m_r_info._business);
    }

    public void hideKeypad() {
        Util.hideKeyPad(this);
    }
    private void updateReview() {
        m_edtContent.setText(m_r_info.content);
        m_tvHash.setText(m_r_info.hash_tag);
        m_edtTitle.setText(m_r_info.title);

        m_nCategory = m_r_info.category;
        m_tvCategory.setText(getResources().getStringArray(R.array.exp_category_reg)[m_r_info.category]);
        m_nPeriod = m_r_info.period;
        m_tvPeriod.setText(getResources().getStringArray(R.array.taken_period)[m_r_info.period]);
        m_nCount = m_r_info.knowhow.size() + m_r_info._good_id.size();
        setCount(m_r_info.knowhow.size() + m_r_info._good_id.size());

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

    public void delete_product(){
        new ConfirmDialog(this, "제품 삭제", m_arr_GoodInfo.get(good_index)._name + "을(를) 제품 목록에서 삭제하시겠습니까?", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_uiLlyProd.removeViewAt(good_index);
                m_arr_GoodInfo.remove(good_index);
                m_r_info._good_id.remove(good_index);
                m_r_info._good_name.remove(good_index);
                m_r_info._good_business.remove(good_index);
                m_r_info._good_photo_urls.remove(good_index);
                m_r_info._good_photo_files.remove(good_index);

                //모든 child 요소들에 대하여 tag가 지운 요소와 같거나 크면 tag를 -1해준다.
                for (int i = good_index; i < m_uiLlyProd.getChildCount(); i++) {
                    if((Integer)m_uiLlyProd.getChildAt(i).getTag()> good_index){
                        View child = m_uiLlyProd.getChildAt(i);
                        child.setTag((Integer) child.getTag() - 1);
                    }
                }
                setCount(m_r_info.knowhow.size() + m_r_info._good_id.size());
            }
        }).show();
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
            case R.id.tv_reg_hash:
                intent = new Intent(this, HashListActivity.class);
                intent.putExtra("hash", m_tvHash.getText().toString());
                startActivityForResult(intent, 100);
                break;
            case R.id.imv_add:
                if (m_nAddImage == 5) {
                    Toast.makeText(this, "최대 5개까지 선택할 수 있습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                takePhoto();
                break;
            case R.id.imv_add_btn:
                if (m_nAddImage == 5) {
                    Toast.makeText(this, "최대 5개까지 선택할 수 있습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                takePhoto();
                break;
            case R.id.lly_category:
                new ListSelectDialog(this, "노하우 공유 카테고리 선택", getResources().getStringArray(R.array.exp_category_reg), 13, m_nCategory).show();
                break;
            case R.id.lly_period_review:
                new ListSelectDialog(this, "복용 기간", getResources().getStringArray(R.array.taken_period), 25, m_nPeriod).show();
                break;
            case R.id.tv_change:
            case R.id.tv_prod_add:
                intent = new Intent(this, SearchInitActivity.class);
                intent.putExtra("select_good", true);
                intent.putExtra("isInitSearch", true);
                startActivityForResult(intent, 200);
                break;
            case R.id.tv_knowhow_add:
                if(!m_edt_knowhow.getText().toString().equals("")){
                    //m_r_info 의 knowhow 요소에 추가
                    m_r_info.knowhow.add(m_edt_knowhow.getText().toString());
                    m_edt_knowhow.setText("");
                    //lsv_knowhow_list 의 요소에 추가
                    addKnowToLLY(m_r_info.knowhow.size()-1);
                }
                break;
        }
    }
    public void addKnowToLLY(int i){
        //m_lly_knowhow_list
        final PhotoListLayout layout = new PhotoListLayout(this, i, "", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.imv_delete:
                        index_knowhow = Integer.parseInt(view.getTag().toString());
                        delete_knowhow(index_knowhow);
                        //onClick(findViewById(R.id.imv_invi));
                        break;
                }
            }
        }, 2);
        //layout.setLocalImage(m_arr_GoodInfo.get(i)._imagePath);
        //layout.setLocalImage(true);
        layout.setName(m_r_info.knowhow.get(i));
        m_lly_knowhow_list.addView(layout, i);
        setCount(m_r_info.knowhow.size() + m_r_info._good_id.size());
    }

    public void delete_knowhow(int index_knowhow){
        for(int i=index_knowhow+1;i<m_lly_knowhow_list.getChildCount();i++){
            View child = m_lly_knowhow_list.getChildAt(i);
            child.setTag((Integer) child.getTag() -1);
        }
        m_lly_knowhow_list.removeViewAt(index_knowhow);
        m_r_info.knowhow.remove(index_knowhow);
        setCount(m_r_info.knowhow.size() + m_r_info._good_id.size());
    }

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.imv_back));
    }

    SelectReviewPhotoDialog m_selectPhotoDlg = null;

    private void takePhoto() {
        if (m_selectPhotoDlg == null)
            m_selectPhotoDlg = new SelectReviewPhotoDialog(this, null, 1);
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
            },0);
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
        if (m_edtTitle.getText().toString().length() == 0) {
            Toast.makeText(this, "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (m_edtContent.getText().toString().length() == 0) {
            Toast.makeText(this, "리뷰를 입력해 주세요.", Toast.LENGTH_SHORT).show();
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
        if (m_nPeriod == -1) {
            Toast.makeText(this, "복용기간을 선택해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
//        if (m_nCount == -1) {
//            Toast.makeText(this, "제품갯수를 선택해 주세요.", Toast.LENGTH_SHORT).show();
//            return;
//        }
        if (m_r_info.knowhow.size() + m_r_info._good_id.size() == 0) {
            Toast.makeText(this, "제품 또는 노하우를 1개 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        new NoticeDialog(this, "리뷰 등록 완료!", "신뢰도 있는 제품 리뷰와 평점을 제공하기 위하여, 저희 비타미인에서는 모든 리뷰를 직접 확인하고 있어요. 자체의 심의 기준과 맞지 않을 경우 게시되지 않을 수도 있음을 양해 부탁드려요. :D\n\n 리뷰 게시가 승인되기 전에도 마이페이지에서는 작성하신 리뷰를 확인하고, 수정할 수 있습니다.", "노하우공유 등록하기", false,
            new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    m_nConnectType = NetUtil.EXP_WRITEorUPDATE;
                    connectServer();
                }
            }).show();
    }

    private String[] m_strPhotoArray;

    private void connectServer() {
        showProgress();
        String[] paramFields = new String[]{Net.NET_POST_FIELD_ACT, Net.NET_POST_FIELD_REQUEST};
        String[] paramValues = null;

        // 파라메터 입력
        JSONObject w_objJSonData = new JSONObject();
        try {
            if (m_nConnectType == NetUtil.EXP_WRITEorUPDATE){
                w_objJSonData.put(Net.NET_VALUE_MEMBER_ID, UserManager.getInstance().member_id);
                w_objJSonData.put("f_no", UserManager.getInstance().member_no);
                w_objJSonData.put("title", m_edtTitle.getText().toString());
                w_objJSonData.put("content", m_edtContent.getText().toString());

                String m_strGoodIds = "";
                for (int i = 0; i < m_arr_GoodInfo.size(); i++)
                    m_strGoodIds = m_strGoodIds + m_arr_GoodInfo.get(i)._id + ",";

                if (m_strGoodIds.length()>1)
                m_strGoodIds = m_strGoodIds.substring(0, m_strGoodIds.length()-1);
                else
                m_strGoodIds = "";
                w_objJSonData.put("good_id", m_strGoodIds);
//            w_objJSonData.put("good_id", "" + m_r_info._id);

                JSONArray arr = new JSONArray();
                if (m_r_info.knowhow.size() != 0) {
                    for (int i = 0; i < m_r_info.knowhow.size(); i++) {
//                        JSONObject obj = new JSONObject();
//                        obj.put();
                        arr.put(m_r_info.knowhow.get(i));
                    }
                }
                w_objJSonData.put("knowhow", arr);

                if (getIntent().getIntExtra("review_type", 1) == 1)
                    w_objJSonData.put("review_type", "exper");
                else
                    w_objJSonData.put("review_type", "sikdan");
                w_objJSonData.put("hash_tag", m_tvHash.getText());
                w_objJSonData.put("category", "" + (m_nCategory + 1));
                w_objJSonData.put("period", m_nPeriod);

//            w_objJSonData.put("func_rate", "0");
//            w_objJSonData.put("price_rate", "0");
//            w_objJSonData.put("take_rate", "0");
//            w_objJSonData.put("taste_rate", "0");
//            w_objJSonData.put("rate", "0");
//            w_objJSonData.put("buy_path", "0");
//            w_objJSonData.put("price", "0");
//            w_objJSonData.put("retake", "0");

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
                m_r_info.title = m_edtTitle.getText().toString();
                m_r_info.content = m_edtContent.getText().toString();
                m_r_info.hash_tag = m_tvHash.getText().toString();
                m_r_info.category = m_nCategory;
                m_r_info.period = m_nPeriod;

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
                            Net.apis_UPDATE_EXP,
                            w_objJSonData.toString()};
                } else {
                    paramValues = new String[]{
                            Net.apis_WRITE_EXP,
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
            if (msg.what == Net.THREAD_REQUEST_END) {
                processForNetEnd();}
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
                if (m_nConnectType == NetUtil.GET_PRODUCT_BY_ID) {
                    Intent intent = new Intent(this, DetailGoodActivity.class);
                    intent.putExtra("info", m_good);
                    startActivity(intent);
                } else  if (m_nConnectType == NetUtil.EXP_WRITEorUPDATE){
                    Intent intent = new Intent();
                    if (getIntent().getBooleanExtra("edit", false)) {
                        intent.putExtra("info", m_r_info);
                        intent.putExtra("arrGoodList", m_arr_GoodInfo);
                    } else if (getIntent().getBooleanExtra("fromMypage", false)) {
                        m_r_info.f_photo = UserManager.getInstance().arr_profile_photo_URL.get(0);
                        m_r_info._mb_nick = UserManager.getInstance().member_nick_name;
                        intent.putExtra("info", m_r_info);
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        } else {
            if (!"".equals(strMsg)) {Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();}
        }
    }


    public void parseJSON() {
        try {
            m_nResultType = _jResult.getInt(Net.NET_VALUE_CODE);
            if (m_nConnectType == NetUtil.GET_PRODUCT_BY_ID) {
                JSONObject obj = _jResult.getJSONObject(Net.NET_VALUE_RESULT);
                m_good =  mNetUtil.parseGood(obj);
                m_arr_GoodInfo.set(good_index, m_good);
            } else if (m_nConnectType == NetUtil.EXP_WRITEorUPDATE){
            }
        } catch (JSONException e) {e.printStackTrace();}
    }

    @Override
    public void onParseJSon(JSONObject j_source) {
        _jResult = j_source;}

    public void setCategory(int index) {
        m_nCategory = index;
        m_tvCategory.setText(getResources().getStringArray(R.array.exp_category_reg)[index]);
    }
    public void setPeriod(int index) {
        m_nPeriod = index;
        m_tvPeriod.setText(getResources().getStringArray(R.array.taken_period)[index]);
    }
    public void setCount(int index) {
        m_nCount = index;
        if(index>10)    index = 10;
        m_tvCount.setText(getResources().getStringArray(R.array.taken_count)[index-1]);
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
                case 100:       //reg hash 에 대한 by 동현
                    m_tvHash.setText(data.getStringExtra("hash"));
                    break;
                case 200:       //add에 product에 대한. by 동현
                    if(m_r_info._good_id.contains(((GoodInfo) data.getSerializableExtra("info"))._id)){
                        new NoticeDialog(this, "제품이 중복되었어요!", "제품 목록에는 같은 제품이\n들어갈 수 없어요.","잠시 후에 자동으로 닫힙니다.", true).show();
                    }else{
                        if (data.getSerializableExtra("info") != null){
                            m_arr_GoodInfo.add((GoodInfo) data.getSerializableExtra("info"));
                            m_r_info._good_photo_urls.add(((GoodInfo) data.getSerializableExtra("info"))._imagePath);
                            m_r_info._good_id.add(((GoodInfo) data.getSerializableExtra("info"))._id);
                            m_r_info._good_name.add(((GoodInfo) data.getSerializableExtra("info"))._name);
                            m_r_info._good_business.add(((GoodInfo) data.getSerializableExtra("info"))._business);
                            m_r_info._good_photo_files.add(new File(""));
                        }
                        addGoodToLLY(m_r_info._good_id.size()-1);
                        setCount(m_r_info.knowhow.size() + m_r_info._good_id.size());
                    }
                    break;
                case 300:       //제품 리스트 클릭에 대한. by 동현
//                    if (data.getSerializableExtra("info") != null)
//                        m_arr_GoodInfo.set(good_index,(GoodInfo) data.getSerializableExtra("info"));
//                    updateGood();
                    break;
            }
        }
    }
}