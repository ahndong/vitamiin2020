package app.vitamiin.com.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.vitamiin.com.Adapter.CommentListAdapter;
import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.ConfirmDialog;
import app.vitamiin.com.Model.CommentInfo;
import app.vitamiin.com.Model.GoodInfo;
import app.vitamiin.com.Model.ReviewInfo;
import app.vitamiin.com.R;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.common.Util;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;
import app.vitamiin.com.login.LoginActivity;
import app.vitamiin.com.setting.MyPageActivity;

public class DetailReviewActivity extends BaseActivity implements View.OnClickListener, OnParseJSonListener, AdapterView.OnItemClickListener {
    Context mContext = this;
    NetUtil mNetUtil;
    NetUtil.connectAndgetServer mNetUtilConnetServer;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    boolean isloggedIn = false;

    int CONNECT_TYPE_DETAIL_REVIEW = 13;
    int CONNECT_TYPE_LIKE_REVIEW = 14;
    int CONNECT_TYPE_FALLOW_USER = 15;
    int CONNECT_TYPE_REPORT_REVIEW = 17;
    int CONNECT_TYPE_DELTE_REVIEW = 20;

    int m_currentPage = 1;
    int m_nMaxPage = 1;
    boolean m_isModified = false;
    private int index=0;

    private ArrayList<Bitmap> m_btmIMGs = new ArrayList<>();
    int m_nResultType = 200;
    boolean m_bLockListView = false;
    int m_nConnectType = CONNECT_TYPE_DETAIL_REVIEW;
    ReviewInfo m_r_info;
    int mR_id;
    GoodInfo m_good = new GoodInfo();

    int m_nReportType = -1;
    View tempView;
    int scrollToHere = 0;

    ScrollView m_scvView;

    ImageView m_imvPhoto, m_imvGood;
    ImageView[] arr_mimvStars = new ImageView[5], arr_mimvStars1 = new ImageView[5], arr_mimvStars2 = new ImageView[5], arr_mimvStars3 = new ImageView[5], arr_mimvStars4 = new ImageView[5];

    TextView m_tvNick, m_tvRegdate, m_tvContent, m_tvContent2, m_tvContent3, m_tvCommentCnt, m_tvViewCnt,m_tvLikeCnt,m_tvCommentCnt2;
    TextView m_tvCategory, m_tvPerson, m_tvBuypath, m_tvTakenperiod, m_tvRebuy, m_tvFallow;
    EditText m_edtComment;
    LinearLayout m_llyPhotoView, m_llyShare;

    ImageView m_imvLike, m_imvKakao, m_imvFacebook;
    ListView m_lsvComment;
    CommentListAdapter m_comment_adapter;

    //아래는 파이어베이스 딥크 관련 코드임. 이곳이 아니라 다른 딥링크로 시작되는 곳에 있어야 할 듯 by 동현
//        // Build GoogleApiClient with AppInvite API for receiving deep links
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, this)
//                .addApi(AppInvite.API)
//                .build();
//
// Check if this app was launched from a deep link. Setting autoLaunchDeepLink to true
// would automatically launch the deep link if one is found.
//        boolean autoLaunchDeepLink = false;
//        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
//                .setResultCallback(
//                        new ResultCallback<AppInviteInvitationResult>() {
//                            @Override
//                            public void onResult(@NonNull AppInviteInvitationResult result) {
//                                if (result.getStatus().isSuccess()) {
//                                    // Extract deep link from Intent
//                                    Intent intent = result.getInvitationIntent();
//                                    String deepLink = AppInviteReferral.getDeepLink(intent);
//
//                                    // Handle the deep link. For example, open the linked
//                                    // content, or apply promotional credit to the user's
//                                    // account.
//
//                                    // ...
//                                } else {
//                                    Log.d(TAG, "getInvitation: no deep link found.");
//                                }
//                            }
//                        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_review);
        mNetUtil = new NetUtil();
        mNetUtilConnetServer = mNetUtil.new connectAndgetServer(this);
        Intent intent = getIntent();

        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            try{
                if(uri.toString().contains("fb.me")){       //페북에서 옴//페북에서 올때 _id찾기
                    mR_id = Integer.valueOf((String.valueOf(uri).split("_id%3D"))[String.valueOf(uri).split("_id%3D").length-1]);
                }else{                          //카톡에서 옴
                    mR_id = Integer.valueOf(uri.getQueryParameter("_id"));
                }
            } catch (Exception e) {            e.printStackTrace();        }
            SharedPreferences pref = getSharedPreferences("Vitamiin", 0);
            if(UserManager.getInstance().member_type.equals("") && !pref.getString("type", "").equals("")){
                showProgress();
                m_nConnectType = NetUtil.apis_LOGIN;
                isloggedIn = mNetUtil.LoginInUtil(this, this, handler, pref.getString("type", ""),pref.getString("email", ""),pref.getString("id", ""),pref.getString("pass", ""));
            }else{
                showProgress();
                m_nConnectType = NetUtil.GET_REVIEW_BYID;
                mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, mR_id);
            }
        }else{
            m_r_info = (ReviewInfo) getIntent().getSerializableExtra("info");

            initView();
            updateInfo();
            m_nConnectType = CONNECT_TYPE_DETAIL_REVIEW;
            connectServer(m_nConnectType);
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
        if (m_r_info._mb_id.equals(UserManager.getInstance().member_id)) {
            findViewById(R.id.tv_delete).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_modify).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_delete).setOnClickListener(this);
            findViewById(R.id.tv_modify).setOnClickListener(this);
        }

        m_imvGood = (ImageView) findViewById(R.id.imv_good);
        m_imvLike = (ImageView) findViewById(R.id.imv_like);
        m_imvPhoto = (ImageView) findViewById(R.id.imv_photo);
        arr_mimvStars[0] = (ImageView) findViewById(R.id.imv_star1);
        arr_mimvStars[1] = (ImageView) findViewById(R.id.imv_star2);
        arr_mimvStars[2] = (ImageView) findViewById(R.id.imv_star3);
        arr_mimvStars[3] = (ImageView) findViewById(R.id.imv_star4);
        arr_mimvStars[4] = (ImageView) findViewById(R.id.imv_star5);

        m_tvNick = (TextView) findViewById(R.id.tv_nick);
        m_tvNick.setOnClickListener(this);
        m_tvRegdate = (TextView) findViewById(R.id.tv_regdate);
        m_tvContent = (TextView) findViewById(R.id.tv_content);
        m_tvContent2 = (TextView) findViewById(R.id.tv_content2);
        m_tvContent3 = (TextView) findViewById(R.id.tv_content3);
        m_tvCommentCnt2 = (TextView) findViewById(R.id.tv_comment_cnt2);
        m_tvViewCnt = (TextView) findViewById(R.id.tv_view_cnt);
        m_tvLikeCnt = (TextView) findViewById(R.id.tv_like_cnt);
        m_tvCategory = (TextView) findViewById(R.id.tv_category);
        m_tvPerson = (TextView) findViewById(R.id.tv_person);
        m_tvBuypath = (TextView) findViewById(R.id.tv_buy_path);
        m_tvTakenperiod = (TextView) findViewById(R.id.tv_taken_period);
        m_tvRebuy = (TextView) findViewById(R.id.tv_rebuy);

        arr_mimvStars1[0] = (ImageView) findViewById(R.id.imv_star11);
        arr_mimvStars1[1] = (ImageView) findViewById(R.id.imv_star12);
        arr_mimvStars1[2] = (ImageView) findViewById(R.id.imv_star13);
        arr_mimvStars1[3] = (ImageView) findViewById(R.id.imv_star14);
        arr_mimvStars1[4] = (ImageView) findViewById(R.id.imv_star15);
        arr_mimvStars2[0] = (ImageView) findViewById(R.id.imv_star21);
        arr_mimvStars2[1] = (ImageView) findViewById(R.id.imv_star22);
        arr_mimvStars2[2] = (ImageView) findViewById(R.id.imv_star23);
        arr_mimvStars2[3] = (ImageView) findViewById(R.id.imv_star24);
        arr_mimvStars2[4] = (ImageView) findViewById(R.id.imv_star25);
        arr_mimvStars3[0] = (ImageView) findViewById(R.id.imv_star31);
        arr_mimvStars3[1] = (ImageView) findViewById(R.id.imv_star32);
        arr_mimvStars3[2] = (ImageView) findViewById(R.id.imv_star33);
        arr_mimvStars3[3] = (ImageView) findViewById(R.id.imv_star34);
        arr_mimvStars3[4] = (ImageView) findViewById(R.id.imv_star35);
        arr_mimvStars4[0] = (ImageView) findViewById(R.id.imv_star41);
        arr_mimvStars4[1] = (ImageView) findViewById(R.id.imv_star42);
        arr_mimvStars4[2] = (ImageView) findViewById(R.id.imv_star43);
        arr_mimvStars4[3] = (ImageView) findViewById(R.id.imv_star44);
        arr_mimvStars4[4] = (ImageView) findViewById(R.id.imv_star45);

        m_tvFallow = (TextView) findViewById(R.id.tv_fallow);
        m_tvFallow.setOnClickListener(this);

        m_llyPhotoView = (LinearLayout) findViewById(R.id.lly_view);

        findViewById(R.id.imv_like).setOnClickListener(this);
        findViewById(R.id.imv_share).setOnClickListener(this);
        findViewById(R.id.imv_report).setOnClickListener(this);
        findViewById(R.id.lly_like).setOnClickListener(this);
        findViewById(R.id.lly_shareBtn).setOnClickListener(this);
        findViewById(R.id.lly_report).setOnClickListener(this);

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
        m_scvView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {
                int totalHeight = m_scvView.getChildAt(0).getHeight();
                int scrollY = m_scvView.getScrollY();
                int height = m_scvView.getHeight();

                if (scrollY + height > totalHeight - 30 && m_currentPage < m_nMaxPage && !m_bLockListView) {
                    m_currentPage++;
                    connectServer(CONNECT_TYPE_DETAIL_REVIEW);
                }
            }
        });

        m_llyShare = (LinearLayout) findViewById(R.id.lly_share);
        m_llyShare.setOnClickListener(this);
        m_imvKakao = (ImageView) findViewById(R.id.imv_kakao);
        m_imvKakao.setOnClickListener(this);
        m_imvFacebook = (ImageView) findViewById(R.id.imv_facebook);
        m_imvFacebook.setOnClickListener(this);

        m_llyShare.setVisibility(View.GONE);

        m_tvCommentCnt = (TextView) findViewById(R.id.tv_comment_cnt);
        m_edtComment = (EditText) findViewById(R.id.edt_comment);
        findViewById(R.id.tv_input).setOnClickListener(this);

        m_lsvComment = (ListView) findViewById(R.id.lsv_comment);
        m_comment_adapter = new CommentListAdapter(this, new ArrayList<CommentInfo>(), 1);
        m_lsvComment.setAdapter(m_comment_adapter);
        m_lsvComment.setOnItemClickListener(this);
    }

    private void updateInfo() {
        m_tvCategory.setText("카테고리: " + getResources().getStringArray(R.array.review_category_reg)[m_r_info.category]);

        String strPerson="";
        if(m_r_info.person>99){
            switch (m_r_info.person){
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
        }else{
            switch(m_r_info.person%10){
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
            if(m_r_info.person>=10){
                strPerson = strPerson + ",여성";
            }else{
                strPerson = strPerson + ",남성";
            }
        }
        m_tvPerson.setText("복용대상: " + strPerson);

        m_tvBuypath.setText("구입경로: " +getResources().getStringArray(R.array.buy_path)[m_r_info.buy_path]);
        m_tvTakenperiod.setText("복용기간: " +getResources().getStringArray(R.array.taken_period)[m_r_info.period]);

        m_tvCommentCnt.setText("댓글 (" + m_r_info.comment_cnt + ")");
        m_tvContent.setText(m_r_info.content);
        m_tvContent2.setText(m_r_info.content2);
        m_tvContent3.setText(m_r_info.content3);
        m_tvViewCnt.setText(String.valueOf(m_r_info.view_cnt));
        m_tvCommentCnt2.setText(String.valueOf(m_r_info.comment_cnt));
        m_tvLikeCnt.setText(String.valueOf(m_r_info.like_cnt));


        if(m_r_info.retake == 0) {
            m_tvRebuy.setText("재구매 했어요!");
        } else {
            m_tvRebuy.setText("재구매 안 했어요!");
        }

        ((TextView) findViewById(R.id.tv_title)).setText("제품 리뷰");

        ((TextView) findViewById(R.id.tv_good)).setText(m_r_info.title);
        ((TextView) findViewById(R.id.tv_business)).setText(m_r_info.business);

        if (m_r_info._good_photo_urls.get(0).equals(""))
            Glide.with(mContext).load(R.drawable.default_review).into(m_imvGood);
        else
            Glide.with(mContext).load(Net.URL_SERVER1 + m_r_info._good_photo_urls.get(0)).into(m_imvGood);

        findViewById(R.id.lly_good).setOnClickListener(this);

        if (m_r_info.f_photo.equals(""))
            Glide.with(mContext).load(R.drawable.ic_female_3).into(m_imvPhoto);
        else
            Glide.with(mContext).load(Net.URL_SERVER1 + m_r_info.f_photo).into(m_imvPhoto);

        for(int i = 0; i < 5; i++)
            if(m_r_info.rate < i + 0.5)
                Glide.with(mContext).load(R.drawable.ic_star_empty).into(arr_mimvStars[i]);
            else if(m_r_info.rate < i+1)
                Glide.with(mContext).load(R.drawable.ic_star_half).into(arr_mimvStars[i]);
            else
                Glide.with(mContext).load(R.drawable.ic_star_one).into(arr_mimvStars[i]);

        m_tvNick.setText(m_r_info._mb_nick);
        m_tvRegdate.setText(m_r_info.regdate);
        m_tvCommentCnt.setText("댓글 (" + m_r_info.comment_cnt + ")");
        m_tvContent.setText(m_r_info.content);
        m_tvContent2.setText(m_r_info.content2);
        m_tvContent3.setText(m_r_info.content3);
        m_tvViewCnt.setText(String.valueOf(m_r_info.view_cnt));
        m_tvCommentCnt2.setText(String.valueOf(m_r_info.comment_cnt));
        m_tvLikeCnt.setText(String.valueOf(m_r_info.like_cnt));

        for(int i = 0; i < 5; i++)
            if(m_r_info.func_rate < i + 0.5)
                Glide.with(mContext).load(R.drawable.ic_star_empty).into(arr_mimvStars1[i]);
            else if(m_r_info.func_rate < i+1)
                Glide.with(mContext).load(R.drawable.ic_star_half).into(arr_mimvStars1[i]);
            else
                Glide.with(mContext).load(R.drawable.ic_star_one).into(arr_mimvStars1[i]);

        for(int i = 0; i < 5; i++)
            if(m_r_info.price_rate < i + 0.5)
                Glide.with(mContext).load(R.drawable.ic_star_empty).into(arr_mimvStars2[i]);
            else if(m_r_info.price_rate < i+1)
                Glide.with(mContext).load(R.drawable.ic_star_half).into(arr_mimvStars2[i]);
            else
                Glide.with(mContext).load(R.drawable.ic_star_one).into(arr_mimvStars2[i]);

        for(int i = 0; i < 5; i++)
            if(m_r_info.take_rate < i + 0.5)
                Glide.with(mContext).load(R.drawable.ic_star_empty).into(arr_mimvStars3[i]);
            else if(m_r_info.take_rate < i+1)
                Glide.with(mContext).load(R.drawable.ic_star_half).into(arr_mimvStars3[i]);
            else
                Glide.with(mContext).load(R.drawable.ic_star_one).into(arr_mimvStars3[i]);

        for(int i = 0; i < 5; i++)
            if(m_r_info.taste_rate < i + 0.5)
                Glide.with(mContext).load(R.drawable.ic_star_empty).into(arr_mimvStars4[i]);
            else if(m_r_info.taste_rate < i+1)
                Glide.with(mContext).load(R.drawable.ic_star_half).into(arr_mimvStars4[i]);
            else
                Glide.with(mContext).load(R.drawable.ic_star_one).into(arr_mimvStars4[i]);

        ((TextView) findViewById(R.id.tv_hash)).setText(m_r_info.hash_tag);

        if (m_r_info.photos.size() == 0)
            m_llyPhotoView.setVisibility(View.GONE);
        else
            updatePhotoListView();
    }

    private void updatePhotoListView() {
        m_llyPhotoView.setVisibility(View.VISIBLE);
        m_llyPhotoView.removeAllViews();
        String photoUrl = null, photoUrl2 = null;

        for(int j = 0; j<=(m_r_info.photos.size()-1)/2; j++) {
            photoUrl = m_r_info.photos.get(j * 2);
            photoUrl2 = "";
            if (j * 2 + 1 < m_r_info.photos.size()) {
                photoUrl2 = m_r_info.photos.get(j * 2 + 1);
            }
            final PhotoListLayout layout = new PhotoListLayout(this, j, photoUrl, photoUrl2, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.imv_photo1:
                            index = (Integer) view.getTag();
                            ShowContentPhotoViewer(index);
                            break;
                        case R.id.imv_photo2:
                            index = (Integer) view.getTag();
                            ShowContentPhotoViewer(index);
                            break;
                    }
                }
            }, 4);
            m_llyPhotoView.addView(layout, j);
        }
        for(int j = 0; j<(m_r_info.photos.size()); j++){
            Util.ImgDownTask mImgDownTask = new Util().new ImgDownTask(this){
                @Override
                protected Integer doInBackground(String... params) {
                    try {
                        String url = params[0];
                        asyncindex = Integer.parseInt(params[1]);
                        if (url != null && !url.equals("null") && url.length() > 0) {
                            try {
                                int url_length = url.length();
                                fileName = Environment.getExternalStorageDirectory()
                                        + "/Vitamin/" + url.substring(url_length-15, url_length-4) + ".png";
                                if(!new File(fileName).exists()){
                                    URL imageurl = new URL(url);
                                    HttpGet httpRequest = null;

                                    httpRequest = new HttpGet(imageurl.toURI());

                                    HttpClient httpclient = new DefaultHttpClient();
                                    HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);

                                    HttpEntity entity = response.getEntity();
                                    BufferedHttpEntity b_entity = new BufferedHttpEntity(entity);
                                    InputStream input = b_entity.getContent();

                                    mbitmap = BitmapFactory.decodeStream(input);

                                    if(Util.saveBitmapToFileCache(mbitmap, fileName, true)){
                                        m_r_info.photosFile.set(asyncindex, new File(fileName));
                                        //UserManager.getInstance().arr_profile_photo_file.set(asyncindex, new File(fileName_preset));
                                    }
                                }else{
                                    m_r_info.photosFile.set(asyncindex, new File(fileName));
                                    //UserManager.getInstance().arr_profile_photo_file.set(asyncindex, new File(fileName_preset));
                                }
                                return asyncindex;
                            } catch (MalformedURLException e) {e.printStackTrace();}
                        } else {
                            fileName = "";
                            return asyncindex;
                        }
                    } catch (Exception e) {Log.w(TAG, e);}

                    return asyncindex;
                }
                @Override
                public void onPostExecute(Integer result)
                {
                    setLocalImageOfPhotoListLayout(result);
                }
            };
            //세번째 파라메터가 "0"이면 프로파일 이미지 다운이고, "1"이면 리뷰 콘텐츠 이미지 다운임 by 동현
            mImgDownTask.execute(Net.URL_SERVER2 + m_r_info.photos.get(j), String.valueOf(j));
        }
    }

    private void setLocalImageOfPhotoListLayout(int result){
        int j = Math.round(result/2);
        if(m_r_info.photos.size()-1<(j*2)+1)
            ((PhotoListLayout) m_llyPhotoView.getChildAt(j)).setLocalImage(m_r_info.photos_id.get(j*2).equals("NA"), null);
        else if((result == m_r_info.photos.size()-1 && Math.round(result/2)!=result/2)
                || (m_r_info.photosFile.get(j*2)!=null && m_r_info.photosFile.get(j*2+1)!=null))
            ((PhotoListLayout) m_llyPhotoView.getChildAt(j)).setLocalImage(m_r_info.photos_id.get(j*2).equals("NA")
                    , m_r_info.photos_id.get(j*2+1).equals("NA"));
    }

    private void ShowContentPhotoViewer(int index){
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 1;
        m_btmIMGs.clear();
        for(File file : m_r_info.photosFile)  m_btmIMGs.add(BitmapFactory.decodeFile(file.getPath(), opts));
//        new ContentPhotoViewerDialog(this, m_btmIMGs, index, 1).show();

        final ContentPhotoViewerDialog contacts_dialog = new ContentPhotoViewerDialog(this, m_btmIMGs, index, 1);
        contacts_dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        contacts_dialog.show();
    }

    public void setReport(int type) {//type: 0~6
        m_nReportType = type;
        connectServer(CONNECT_TYPE_REPORT_REVIEW);
    }

    private void hideShareLly() {
        if(m_llyShare.getVisibility()==View.VISIBLE){
            m_llyShare.animate().translationY(m_llyShare.getHeight()*3).setInterpolator(new DecelerateInterpolator(1));
            m_imvKakao.animate().translationY(m_imvKakao.getHeight()*3).setInterpolator(new DecelerateInterpolator(3));
            m_imvFacebook.animate().translationY(m_imvFacebook.getHeight()*3).setInterpolator(new DecelerateInterpolator(3));
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
                }else {
                    gotoBack();
                }
                break;
            case R.id.rly_bg:
                hideShareLly();
                break;
            case R.id.tv_nick:
                intent = new Intent(this, MyPageActivity.class);
                intent.putExtra("mb_id", m_r_info._mb_id);
                intent.putExtra("mb_nick", m_r_info._mb_nick);
                intent.putExtra("mb_photo", m_r_info.f_photo);
                startActivityForResult(intent, 200);
                break;
            case R.id.imv_like:
            case R.id.lly_like:
                m_isModified = true;
                like();
                break;
            case R.id.imv_share:
            case R.id.lly_shareBtn:
                showShareLly();
                break;
            case R.id.imv_report:
            case R.id.lly_report:
                if (m_r_info._mb_id.equals(UserManager.getInstance().member_id)) {
                    Toast.makeText(this, "본인의 리뷰입니다.", Toast.LENGTH_SHORT).show();
                }else
                    new ReportReviewDialog(this, 0).show();
                break;
            case R.id.imv_kakao:
                shareToKakaoTalk();
                hideShareLly();
                break;
            case R.id.imv_facebook:
                shareToFacebook();
                hideShareLly();
                break;
            case R.id.tv_fallow:
                fallow();
                break;
            case R.id.tv_input:
                m_isModified = true;
                m_edtComment.clearFocus();
                commitComment();
                break;
            case R.id.lly_good:
                m_nConnectType = NetUtil.GET_PRODUCT_BY_ID;
                mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, m_r_info._good_id.get(0));
                break;
            case R.id.tv_delete:
                new ConfirmDialog(this, "리뷰 게시글 삭제", "이 리뷰를 정말 삭제하시겠습니까?", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        connectServer(CONNECT_TYPE_DELTE_REVIEW);
                    }
                }).show();
                break;
            case R.id.tv_modify:
                m_isModified = true;
                intent = new Intent(this, ReviewWriteActivity.class);
                intent.putExtra("edit", true);
                intent.putExtra("info", m_r_info);
                startActivityForResult(intent, 100);
                break;
        }
    }

    private void shareToFacebook() {
        String title = "[제품리뷰] " + m_r_info.title + " by. " + m_r_info._mb_nick+ "님";
        String strImg;
        if(m_r_info.photos.size() != 0)
            strImg = Net.URL_SERVER2 + m_r_info.photos.get(0);
        else
            strImg = Net.URL_SERVER2 + m_r_info._good_photo_urls.get(0);

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            try {
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setImageUrl(Uri.parse(strImg))
                        .setContentTitle(title)
                        .setContentDescription("솔직한 후기만 있는 비타미인에서 제품 리뷰를 확인해보세요!")
                        .setContentUrl(Uri.parse("https://fb.me/1089182657853667?_id=" + String.valueOf(m_r_info._id)))
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
            properties.put("_id", String.valueOf(m_r_info._id));
            properties.put("_activity", "rv");

            final KakaoLink kakaoLink = KakaoLink.getKakaoLink(this);
            final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink
                    .createKakaoTalkLinkMessageBuilder();

            String title = "[제품리뷰] " + m_r_info.title + " by. " + m_r_info._mb_nick+ "님";
            String strImg;
            if(m_r_info.photos.size() != 0)
                strImg = Net.URL_SERVER2 + m_r_info.photos.get(0);
            else
                strImg = Net.URL_SERVER2 + m_r_info._good_photo_urls.get(0);

            kakaoTalkLinkMessageBuilder.addText(title + "\n " + "솔직한 후기만 있는 비타미인에서 제품 리뷰를 확인해보세요!")
                    .addImage(strImg, 100, 100)
                    .addAppButton("앱으로 이동",
                            new AppActionBuilder()
                                    .addActionInfo(AppActionInfoBuilder
                                            .createAndroidActionInfoBuilder()
                                            .setMarketParam("referrer=kakaotalklink")
                                            .setExecuteParam(properties)
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

    private void gotoBack() {
        Intent intent;
        if(isTaskRoot()){
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            intent = new Intent();
//            if (m_isModified) {
            intent.putExtra("editedReview", m_r_info);
            intent.putExtra("view_cnt", m_r_info.view_cnt + 1);
            intent.putExtra("like_cnt", m_r_info.like_cnt);
            intent.putExtra("comment_cnt", m_r_info.comment_cnt);
            intent.putExtra("content", m_r_info.content);
            intent.putExtra("content2", m_r_info.content2);
            intent.putExtra("content3", m_r_info.content3);
            intent.putExtra("rate", m_r_info.rate);
//            }
            if(!m_imvLike.isSelected())
                intent.putExtra("unlike", true);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void fallow() {
        if (m_r_info._mb_id.equals(UserManager.getInstance().member_id)) {
            Toast.makeText(this, "본인은 팔로잉 할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        connectServer(CONNECT_TYPE_FALLOW_USER);
    }

    private void like() {
        if (m_r_info._mb_id.equals(UserManager.getInstance().member_id)) {
            Toast.makeText(this, "본인의 리뷰입니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        m_nConnectType = CONNECT_TYPE_LIKE_REVIEW;
        connectServer(m_nConnectType);
    }

    private void commitComment() {
        Util.hideKeyPad(this);
        if (m_edtComment.getText().toString().length() == 0) {
            Toast.makeText(this, "댓글을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        m_nConnectType = NetUtil.apis_REG_COMMENT_REV_AND_EXP_AND_EVENT;
        mNetUtilConnetServer.connectServer(mContext, handler, m_nConnectType, new String[] {String.valueOf(m_r_info._id),
                m_edtComment.getText().toString(),
                "review"});
    }

    private void connectServer(int strAct) {
        m_bLockListView = true;
        m_nConnectType = strAct;

        showProgress();
        String[] paramFields = new String[]{Net.NET_POST_FIELD_ACT,
                Net.NET_POST_FIELD_REQUEST};
        String[] paramValues = null;

        // 파라메터 입력
        JSONObject w_objJSonData = new JSONObject();
        try {
            w_objJSonData.put(Net.NET_VALUE_MEMBER_ID, UserManager.getInstance().member_id);
            if (m_nConnectType == CONNECT_TYPE_DETAIL_REVIEW) {
                w_objJSonData.put("maker_id", m_r_info._mb_id);
                w_objJSonData.put("review_id", "" + m_r_info._id);
                w_objJSonData.put(Net.NET_VALUE_PAGE, m_currentPage);

                paramValues = new String[]{
                        Net.apis_DETAIL_REVIEW,
                        w_objJSonData.toString()};
            } else if (m_nConnectType == CONNECT_TYPE_LIKE_REVIEW) {
                w_objJSonData.put("review_id", "" + m_r_info._id);
                w_objJSonData.put("author_id", "" + m_r_info._mb_id);
                w_objJSonData.put("like", "" + m_r_info.like_review);
                w_objJSonData.put("type", 1);

                paramValues = new String[]{
                        Net.apis_LIKE_REVIEW,
                        w_objJSonData.toString()};
            } else if (m_nConnectType == CONNECT_TYPE_FALLOW_USER) {
                w_objJSonData.put("fallow_id", m_r_info._mb_id);
                w_objJSonData.put("fallow", "" + m_r_info.fallow_user);

                paramValues = new String[]{
                        Net.POST_FIELD_ACT_FALLOW_USER,
                        w_objJSonData.toString()};
            }else if (m_nConnectType == CONNECT_TYPE_REPORT_REVIEW) {
                w_objJSonData.put("review_id", "" + m_r_info._id);
                w_objJSonData.put("report_type", "" + m_nReportType);

                paramValues = new String[]{
                        Net.apis_REPORT_REVIEW,
                        w_objJSonData.toString()};
            }else if (m_nConnectType == CONNECT_TYPE_DELTE_REVIEW) {
                w_objJSonData.put("review_id", "" + m_r_info._id);
                paramValues = new String[]{
                        Net.apis_DELETE_REVIEW,
                        w_objJSonData.toString()};
            }
        } catch (Exception e) {e.printStackTrace();}

        String netUrl = Net.URL_SERVER + Net.URL_SERVER_API;
        HttpRequester.getInstance().init(this, this, handler, netUrl,
                paramFields, paramValues, false);

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
                if (m_nConnectType == NetUtil.apis_LOGIN) {
                    showProgress();
                    m_nConnectType = NetUtil.GET_REVIEW_BYID;
                    mNetUtilConnetServer.connectServer(this, handler, m_nConnectType, mR_id);
                }
                else if (m_nConnectType == NetUtil.GET_REVIEW_BYID) {
                    initView();
                    updateInfo();
                    m_nConnectType = CONNECT_TYPE_DETAIL_REVIEW;
                    connectServer(m_nConnectType);
                }
                else if (m_nConnectType == CONNECT_TYPE_DETAIL_REVIEW) {
                    m_tvCommentCnt.setText("댓글 (" + m_r_info.comment_cnt + ")");
                    m_tvCommentCnt2.setText(String.valueOf(m_r_info.comment_cnt));
                    m_imvLike.setSelected(m_r_info.like_review == 1);
                    m_tvFallow.setSelected(m_r_info.fallow_user == 1);

                    m_comment_adapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(m_lsvComment);
                    if (m_currentPage == 1)
                        m_scvView.setScrollY(0);

                    if(scrollToHere!=0) scrollToView();
                }
                else if (m_nConnectType == CONNECT_TYPE_LIKE_REVIEW) {
                    if (m_r_info.like_review == 1) {
                        m_imvLike.setSelected(true);
                        m_r_info.like_cnt++;
                    } else {
                        m_imvLike.setSelected(false);
                        m_r_info.like_cnt--;
                    }
                    m_tvLikeCnt.setText(String.valueOf(m_r_info.like_cnt));
                }
                else if (m_nConnectType == CONNECT_TYPE_FALLOW_USER) {
                    m_tvFallow.setSelected(m_r_info.fallow_user == 1);
                }
                else if (m_nConnectType == NetUtil.apis_REG_COMMENT_REV_AND_EXP_AND_EVENT) {
                    m_tvCommentCnt.setText("댓글 (" + ++m_r_info.comment_cnt + ")");
                    m_edtComment.setText("");

                    m_comment_adapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(m_lsvComment);

                    if(scrollToHere!=0) scrollToView();
                }
                else if (m_nConnectType == NetUtil.apis_DEL_COMMENT_REV_AND_EXP_AND_EVENT) {
                    m_tvCommentCnt.setText("댓글 (" + --m_r_info.comment_cnt + ")");
                    m_comment_adapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(m_lsvComment);

                    if(scrollToHere!=0) scrollToView();
                }
                else if (m_nConnectType == CONNECT_TYPE_REPORT_REVIEW) {
                    Toast.makeText(this, "해당 리뷰가 신고 되었습니다.", Toast.LENGTH_SHORT).show();
                }
                else if (m_nConnectType == NetUtil.GET_PRODUCT_BY_ID) {
                    Intent intent = new Intent(this, DetailGoodActivity.class);
                    intent.putExtra("info", m_good);
                    startActivity(intent);
                }
                else if (m_nConnectType == CONNECT_TYPE_DELTE_REVIEW) {
                    Intent intent = new Intent();
                    intent.putExtra("delete", true);
                    setResult(RESULT_OK, intent);
                    finish();
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
                if (m_nResultType == 200) {
                    mNetUtil.transProfile_ObjToUM(this,0,obj);
                    UserManager.getInstance().member_type = getSharedPreferences("Vitamiin", 0).getString("type", "");
                    UserManager.getInstance().event_num = _jResult.getJSONObject("result").getInt("event_num");
                }
            }
            else if (m_nConnectType == NetUtil.GET_REVIEW_BYID) {
                JSONObject obj_rev = obj.getJSONObject("review");
                m_r_info =  mNetUtil.parseReview(obj_rev, true);
            }
            else if (m_nConnectType == CONNECT_TYPE_DETAIL_REVIEW) {
                m_r_info.like_review = obj.getInt("like");
                m_r_info.fallow_user = obj.getInt("fallow");
                m_r_info.comment_cnt = obj.getInt(Net.NET_VALUE_TOTAL_COUNT);
                m_nMaxPage = obj.getInt(Net.NET_VALUE_TOTAL_PAGE);

                JSONArray arr = obj.getJSONArray("comment");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj_cmt = arr.getJSONObject(i);
                    CommentInfo info = new CommentInfo();
                    info._id = obj_cmt.getInt("id");
                    info._mb_id = obj_cmt.getString("mb_id");
                    info._review_id = obj_cmt.getInt("review_id");
                    info.content = obj_cmt.getString("content");
                    info.regdate = obj_cmt.getString("rdate");
                    info._mb_nick = obj_cmt.getString("mb_nick");
                    info.f_photo = obj_cmt.getString("f_photo");
                    m_comment_adapter.add(info);
                }
            } else if (m_nConnectType == CONNECT_TYPE_LIKE_REVIEW) {
                m_r_info.like_review = obj.getInt("like");
            } else if (m_nConnectType == CONNECT_TYPE_FALLOW_USER) {
                m_r_info.fallow_user = obj.getInt("fallow");
            } else if (m_nConnectType == NetUtil.GET_PRODUCT_BY_ID) {
                m_good =  mNetUtil.parseGood(obj);
            } else if (m_nConnectType == CONNECT_TYPE_DELTE_REVIEW) {
            }
            else if (m_nConnectType == NetUtil.apis_REG_COMMENT_REV_AND_EXP_AND_EVENT) {
//                scrollToHere = ((View)findViewById(R.id.tv_comment_cnt).getParent()).getTop()-250;
                TextView tv = (TextView)findViewById(R.id.tv_comment_cnt);
                scrollToHere = ((View)tv.getParent()).getTop()-250;

                CommentInfo info = new CommentInfo();
                info._id = obj.getInt("comment_id");
                info._mb_id = UserManager.getInstance().member_id;
//                    info._review_id = obj_cmt.getInt("wiki_id");
                info.content = m_edtComment.getText().toString();
                info.regdate = "방금";
                info._mb_nick = UserManager.getInstance().member_nick_name;
                info.f_photo = UserManager.getInstance().arr_profile_photo_URL.get(0);
                m_comment_adapter.insert(info,0);
            } else if (m_nConnectType == NetUtil.apis_DEL_COMMENT_REV_AND_EXP_AND_EVENT) {
                scrollToHere = ((View) tempView.getParent().getParent()).getTop()-250;

                m_comment_adapter.remove(m_selectedComment);
            }
        } catch (JSONException e) {e.printStackTrace();}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 100:       //수정에 대한 return by 동현
                    m_r_info = (ReviewInfo) data.getSerializableExtra("info");
                    updateInfo();
                    break;
                case 200:
                    m_r_info.fallow_user = data.getIntExtra("fallow_user", 0);
                    if (m_r_info.fallow_user == 1)
                        m_tvFallow.setSelected(true);
                    else
                        m_tvFallow.setSelected(false);
                    break;
            }
        }
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        if (m_comment_adapter == null)
            return;

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.AT_MOST);
        for (int i = 0; i < m_comment_adapter.getCount(); i++) {
            View listItem = m_comment_adapter.getView(i, null, listView);
            listItem.measure(0, 0);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (m_comment_adapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public  void scrollToView(){
        m_scvView.scrollTo(0, scrollToHere);
        scrollToHere = 0;
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Net.THREAD_REQUEST_END)
                processForNetEnd();
        }
    };

    JSONObject _jResult;

    @Override
    public void onParseJSon(JSONObject j_source) {
        _jResult = j_source;
    }

    CommentInfo m_selectedComment;

    public void deleteComment(CommentInfo info, View v) {
        m_selectedComment = info;
        tempView = v;
        m_isModified = true;
        m_nConnectType = NetUtil.apis_DEL_COMMENT_REV_AND_EXP_AND_EVENT;
        mNetUtilConnetServer.connectServer(mContext, handler, m_nConnectType, new String[] {String.valueOf(m_selectedComment._id),
                "review"});
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        CommentInfo c_info = m_comment_adapter.getItem(pos);
        if (c_info == null)                return;

        Intent intent = new Intent(this, MyPageActivity.class);
        intent.putExtra("mb_id", c_info._mb_id);
        intent.putExtra("mb_nick", c_info._mb_nick);
        intent.putExtra("mb_photo", c_info.f_photo);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if(m_llyShare.getVisibility()==View.VISIBLE){
            hideShareLly();
        } else {
            onClick(findViewById(R.id.imv_back));
        }
    }
}
