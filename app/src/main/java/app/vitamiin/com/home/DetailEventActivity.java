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
//import android.support.v4.content.ContextCompat;
import androidx.core.content.ContextCompat;
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
import android.widget.RelativeLayout;
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
import java.util.Map;

import app.vitamiin.com.Adapter.CommentListAdapter;
import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.Model.CommentInfo;
import app.vitamiin.com.Model.WikiInfo;
import app.vitamiin.com.R;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.common.Util;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;
import app.vitamiin.com.login.LoginActivity;
import app.vitamiin.com.setting.MyPageActivity;

/**
 * Created by dong8 on 2017-05-11.
 */

public class DetailEventActivity extends BaseActivity implements View.OnClickListener, OnParseJSonListener, AdapterView.OnItemClickListener {
    Context mContext = this;
    NetUtil mNetUtil;
    NetUtil.connectAndgetServer mNetUtilConnetServer;
    int m_nResultType = 200;

    int m_nConnectType = NetUtil.apis_GET_EVENT_BY_ID;

    CallbackManager callbackManager;
    ShareDialog shareDialog;
    boolean isloggedIn = false;

    int m_currentPage = 1, m_nMaxPage = 1, m_nTotalCount = 0;
    int index = 0;

    boolean m_isModified = false;
    boolean m_bLockListView = false;
    WikiInfo m_p_info;
    int mR_id;

    View tempView;
    int scrollToHere = 0;

    TextView m_tvCommentCnt;
    ImageView m_imvLike, m_imvPhoto, m_imvKakao, m_imvFacebook;
    RelativeLayout m_rlyBack;
    EditText m_edtComment;
    ScrollView m_scvView;
    ListView m_lsvComment;
    CommentListAdapter m_comment_adapter;
    LinearLayout m_llyShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);
        mNetUtil = new NetUtil();
        mNetUtilConnetServer = mNetUtil.new connectAndgetServer(this);
        Intent intent = getIntent();

        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            try{
                if(uri.toString().contains("fb.me")){       //페북에서 옴//페북에서 올때 _id찾기
                    mR_id = Integer.parseInt((uri.toString().split("_id%3D"))[uri.toString().split("_id%3D").length-1].substring(1));
                }else{                          //카톡에서 옴
                    mR_id = Integer.valueOf(uri.getQueryParameter("_id"));
                }
            } catch (Exception e) {            e.printStackTrace();        }
            SharedPreferences pref = getSharedPreferences("Vitamiin", 0);
            if(UserManager.getInstance().member_type.equals("") && !pref.getString("type", "").equals("")){
                showProgress();
                m_nConnectType = NetUtil.apis_LOGIN;
                isloggedIn = mNetUtil.LoginInUtil(this, mContext, handler, pref.getString("type", ""),pref.getString("email", ""),pref.getString("id", ""),pref.getString("pass", ""));
            }else{
                showProgress();
                m_nConnectType = NetUtil.apis_GET_EVENT_BY_ID;
                mNetUtilConnetServer.connectServer(mContext, handler, m_nConnectType, new int[] {m_p_info._id, m_currentPage});
            }
        } else {
            m_p_info = (WikiInfo) intent.getSerializableExtra("info");

            initView();
            m_nConnectType = NetUtil.apis_GET_EVENT_DETAIL;
            mNetUtilConnetServer.connectServer(mContext, handler, m_nConnectType, new int[] {m_p_info._id, m_currentPage});
        }

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(mContext, "onSuccess", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(mContext, "onCancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(mContext, "onError: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        findViewById(R.id.imv_back).setOnClickListener(this);
        findViewById(R.id.rly_bg).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_title)).setText(m_p_info.title);
        findViewById(R.id.tv_content).setVisibility(View.GONE);
        m_imvPhoto = (ImageView) findViewById(R.id.imv_good);
        m_rlyBack = (RelativeLayout) findViewById(R.id.rly_back);

        if (m_p_info._content.length() == 0)
            Glide.with(mContext).load(R.drawable.default_review).into(m_imvPhoto);
        else
            Glide.with(mContext).load(Net.URL_SERVER1 + m_p_info._content).into(m_imvPhoto);
        findViewById(R.id.tv_share).setOnClickListener(this);

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
                if (scrollY ==0){
                    m_rlyBack.setBackgroundColor(Color.parseColor("#bbffffff"));
                }else{
                    m_rlyBack.setBackgroundColor(ContextCompat.getColor(mContext, R.color.trans_main_color_1));
                }
                if (scrollY + height > totalHeight - 20) {
                    if(m_currentPage < m_nMaxPage && !m_bLockListView){
                        m_bLockListView = true;
                        m_currentPage++;
                        m_nConnectType = NetUtil.apis_GET_EVENT_DETAIL;
                        mNetUtilConnetServer.connectServer(mContext, handler, m_nConnectType, new int[] {m_p_info._id, m_currentPage});
                    }
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

        m_comment_adapter = new CommentListAdapter(this, new ArrayList<CommentInfo>(), 4);
        m_lsvComment.setAdapter(m_comment_adapter);
        m_lsvComment.setOnItemClickListener(this);
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
            case R.id.tv_share:
                showShareLly();
                break;
            case R.id.imv_kakao:
                shareToKakaoTalk();
                hideShareLly();
                break;
            case R.id.imv_facebook:
                shareToFacebook();
                hideShareLly();
                break;
            case R.id.tv_input:
                m_isModified = true;
                m_edtComment.clearFocus();
                commitComment();
                break;
        }
    }

    private void shareToFacebook() {
        String title = null;
        title = "[비타미인 이벤트 공유] " + m_p_info.title + " by. Vitamiin";

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            try {
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setImageUrl(Uri.parse(Net.URL_SERVER2 + m_p_info._content))
                        .setContentTitle(title)
                        .setContentDescription("솔직한 후기만 있는 비타미인에서 이벤트를 합니다!")
                        .setContentUrl(Uri.parse("https://fb.me/1131037673668165?_id=" + m_p_info._id))
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
            properties.put("_id", String.valueOf(m_p_info._id));
            properties.put("_activity", "ev");

            final KakaoLink kakaoLink = KakaoLink.getKakaoLink(this);
            final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink
                    .createKakaoTalkLinkMessageBuilder();

            String title = null;
            title = "[비타미인 이벤트 공유] " + m_p_info.title + " by. Vitamiin";

            kakaoTalkLinkMessageBuilder.addText(title + "\n " + "솔직한 후기만 있는 비타미인에서 이벤트를 합니다!")
                    .addImage(Net.URL_SERVER1+ m_p_info._content, 100, 100)
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

    private void gotoBack() {
        Intent intent;
        if(isTaskRoot()){
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            intent = new Intent();
//            if (m_isModified) {
                intent.putExtra("view_cnt", m_p_info._view_cnt);
                intent.putExtra("comment_cnt", m_p_info._comment_cnt);
//            }
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void commitComment() {
        Util.hideKeyPad(this);
        if (m_edtComment.getText().toString().length() == 0) {
            Toast.makeText(this, "댓글을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        m_nConnectType = NetUtil.apis_REG_COMMENT_REV_AND_EXP_AND_EVENT;
        mNetUtilConnetServer.connectServer(mContext, handler, m_nConnectType, new String[] {String.valueOf(m_p_info._id),
                                                                                        m_edtComment.getText().toString(),
                                                                                        "event"});
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
                    m_nConnectType = NetUtil.apis_GET_EVENT_BY_ID;
                    mNetUtilConnetServer.connectServer(mContext, handler, m_nConnectType, new int[] {m_p_info._id, m_currentPage});
                }
                else if (m_nConnectType == NetUtil.apis_GET_EVENT_BY_ID) {
                    initView();
                    m_nConnectType = NetUtil.apis_GET_EVENT_DETAIL;
                    mNetUtilConnetServer.connectServer(mContext, handler, m_nConnectType, new int[] {m_p_info._id, m_currentPage});
                }
                else if (m_nConnectType == NetUtil.apis_GET_EVENT_DETAIL) {
                    m_bLockListView = false;
                    m_tvCommentCnt.setText("댓글 (" + m_p_info._comment_cnt + ")");
                    m_p_info._view_cnt++;

                    m_comment_adapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(m_lsvComment);

//                    ViewTreeObserver vto = m_lsvComment.getViewTreeObserver();
//
//                    if(vto.isAlive()){
//                        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                            @Override
//                            public void onGlobalLayout(){
//                                setListViewHeightBasedOnChildren(m_lsvComment);
//
////                                ViewTreeObserver vto = m_lsvComment.getViewTreeObserver();
////                                if ( Build.VERSION.SDK_INT >= 16 ) // or Build.VERSION_CODES.JELLY_BEAN
////                                    vto.removeOnGlobalLayoutListener( this );
////                                else
////                                    vto.removeGlobalOnLayoutListener( this );
//                            }
//                        });
//                    }

                    if (m_currentPage == 1)
                        m_scvView.setScrollY(0);

                    if(scrollToHere!=0) scrollToView();
                }
                else if (m_nConnectType == NetUtil.apis_REG_COMMENT_REV_AND_EXP_AND_EVENT) {
                    m_tvCommentCnt.setText("댓글 (" + ++m_p_info._comment_cnt + ")");
                    m_edtComment.setText("");

                    m_comment_adapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(m_lsvComment);

                    if(scrollToHere!=0) scrollToView();
                } else if (m_nConnectType == NetUtil.apis_DEL_COMMENT_REV_AND_EXP_AND_EVENT) {
                    m_tvCommentCnt.setText("댓글 (" + --m_p_info._comment_cnt + ")");
                    m_comment_adapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(m_lsvComment);

                    if(scrollToHere!=0) scrollToView();
                }
            }
        } else {
            if (!"".equals(strMsg)) {Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();}
        }
    }

    public void parseJSON() {
        try {
            m_nResultType = _jResult.getInt(Net.NET_VALUE_CODE); //
            JSONObject obj = _jResult.getJSONObject(Net.NET_VALUE_RESULT);

            if (m_nResultType == 200) {
                if (m_nConnectType == NetUtil.apis_LOGIN) {
                    mNetUtil.transProfile_ObjToUM(this,0,obj);
                    UserManager.getInstance().member_type = getSharedPreferences("Vitamiin", 0).getString("type", "");
                    UserManager.getInstance().event_num = _jResult.getJSONObject("result").getInt("event_num");
                }else if (m_nConnectType == NetUtil.apis_GET_EVENT_DETAIL) {
                    m_p_info._comment_cnt = obj.getInt(Net.NET_VALUE_TOTAL_COUNT);
                    m_nMaxPage = obj.getInt(Net.NET_VALUE_TOTAL_PAGE);

                    JSONArray arr = obj.getJSONArray("comment");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj_cmt = arr.getJSONObject(i);
                        CommentInfo info = new CommentInfo();
                        info._id = obj_cmt.getInt("id");
                        info._mb_id = obj_cmt.getString("mb_id");
                        info._review_id = obj_cmt.getInt("event_id");
                        info.content = obj_cmt.getString("content");
                        info.regdate = obj_cmt.getString("rdate");
                        info._mb_nick = obj_cmt.getString("mb_nick");
                        info.f_photo = obj_cmt.getString("f_photo");
                        m_comment_adapter.add(info);
                    }
                } else if (m_nConnectType == NetUtil.apis_REG_COMMENT_REV_AND_EXP_AND_EVENT) {
                    scrollToHere = findViewById(R.id.tv_comment_cnt).getTop()-250;

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
            }
            else if (m_nConnectType == NetUtil.apis_GET_EVENT_BY_ID) {
            }
        } catch (JSONException e) {e.printStackTrace();}
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

//        totalHeight = m_comment_adapter.getTotalHeight();

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
        mNetUtilConnetServer.connectServer(mContext, handler, m_nConnectType
                                        , new String[] {String.valueOf(m_selectedComment._id)
                                        , "event"});
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
