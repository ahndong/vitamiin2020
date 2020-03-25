package app.vitamiin.com.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//import android.support.v4.app.Fragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.vitamiin.com.Adapter.PowerReviewRecycleAdapter;
import app.vitamiin.com.Model.WikiInfo;
import app.vitamiin.com.R;
import app.vitamiin.com.common.NetUtil;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;

/**
 * Created by dong8 on 2017-05-11.
 */

public class EventFragment extends Fragment implements View.OnClickListener, OnParseJSonListener {
    Fragment m_fragment =this;
    MainActivity m_MainAct;
    int m_nResultType = 200;
    NetUtil mNetUtil;
    NetUtil.connectAndgetServer mNetUtilConnetServer;
    Boolean m_isListLoaded = false;

    int m_nConnectType = NetUtil.apis_GET_EVENT_LIST;

    int m_currentPage = 1;
    int m_nMaxPage = 1;

    RecyclerView m_rcvEvent;
    PowerReviewRecycleAdapter m_EventAdapter;
    LinearLayoutManager mLayoutManager;

    ImageView m_imvPositionArrow;

    private ArrayList<WikiInfo> arrEventList = new ArrayList<>();

    public static EventFragment newInstance() {
        return new EventFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event, container, false);
        m_MainAct = (MainActivity) getActivity();
        mNetUtil = new NetUtil();
        mNetUtilConnetServer = mNetUtil.new connectAndgetServer(m_MainAct);

        initView(rootView);
        m_isListLoaded = false;
        m_MainAct.m_LockView = false;
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getView() != null) {
                if(m_isListLoaded){
                    m_MainAct.m_LockView = false;
                }else{
                    m_MainAct.showProgress();
                    m_MainAct.m_LockView = true;
                    m_rcvEvent.setVisibility(View.VISIBLE);
                    m_currentPage = 1;
                    m_nMaxPage = 1;

                    arrEventList.clear();
                    m_rcvEvent.setAdapter(m_EventAdapter);

                    m_nConnectType = NetUtil.apis_GET_EVENT_LIST;
                    mNetUtilConnetServer.connectServer(m_fragment, handler, m_nConnectType, String.valueOf(m_currentPage));
                }
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
            if (msg.what == 0) {
                if(m_isListLoaded){
                    m_MainAct.m_LockView = false;
                }else {
                    m_MainAct.showProgress();
                    m_MainAct.m_LockView = true;
                    m_rcvEvent.setVisibility(View.VISIBLE);
                    m_currentPage = 1;
                    m_nMaxPage = 1;

                    arrEventList.clear();
                    m_rcvEvent.setAdapter(m_EventAdapter);

                    m_nConnectType = NetUtil.apis_GET_EVENT_LIST;
                    mNetUtilConnetServer.connectServer(m_fragment, handler, m_nConnectType, String.valueOf(m_currentPage));
                }
            }
        }
    };

    private void initView(View v) {
        m_currentPage = 1;
        m_nMaxPage = 1;

        m_imvPositionArrow = (ImageView)v.findViewById(R.id.imv_position_arrow);
        m_imvPositionArrow.setOnClickListener(this);

        m_rcvEvent = (RecyclerView) v.findViewById(R.id.rcv_event);
        mLayoutManager = new LinearLayoutManager(m_MainAct);
        m_rcvEvent.setLayoutManager(mLayoutManager);
        m_rcvEvent.setOnScrollListener(m_hidingScrollListener);

        m_EventAdapter = new PowerReviewRecycleAdapter(m_MainAct, arrEventList, 1);
        m_rcvEvent.setAdapter(m_EventAdapter);
    }

    HidingScrollListener m_hidingScrollListener = new HidingScrollListener() {
        @Override
        public void onHide() {
            hideViews();
        }

        @Override
        public void onShow() {
            showViews();
        }
    };

    private void hideViews() {}
    private void showViews() {}


    private abstract class HidingScrollListener extends RecyclerView.OnScrollListener {
        private static final int HIDE_THRESHOLD = 30;
        private int scrolledDistance = 0;

        private boolean controlsVisible = true;

        int pastVisiblesItems, visibleItemCount, totalItemCount;
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                onHide();
            } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                onShow();
            }

            if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
                scrolledDistance += dy;
            }

            visibleItemCount = mLayoutManager.getChildCount();
            totalItemCount = mLayoutManager.getItemCount();
            pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

            int count = totalItemCount - visibleItemCount;

            if(pastVisiblesItems>4)
                viewPositionTextView();
            else
                hidePositionTextView();

            if (pastVisiblesItems >= count && totalItemCount != 1
                    && m_currentPage < m_nMaxPage && !m_MainAct.m_LockView) {
                m_MainAct.showProgress();
                m_MainAct.m_LockView = true;
                m_currentPage++;
                mNetUtilConnetServer.connectServer(m_fragment, handler, m_nConnectType, "");
            }
        }
        public abstract void onHide();
        public abstract void onShow();

    }

    public void viewPositionTextView(){
        m_imvPositionArrow.setVisibility(View.VISIBLE);
        m_imvPositionArrow.animate().alpha(0.8f);
    }

    public void hidePositionTextView(){
        m_imvPositionArrow.animate().alpha(0f);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imv_position_arrow:
                break;
        }
    }

    private void processForNetEnd() {
        parseJSON();
        m_MainAct.closeProgress();
        HttpRequester.getInstance().stopNetThread();

        int resultCode = HttpRequester.getInstance().getResultCode();
        String strMsg = HttpRequester.getInstance().getResultMsg();

        if (resultCode == Net.CONNECTION_SUCCSES) {
            if (m_nResultType == 200) {
                m_isListLoaded = true;
                if (m_nConnectType == NetUtil.apis_GET_EVENT_LIST){
                    m_EventAdapter.notifyDataSetChanged();
                }
            }
        } else {
            if (!"".equals(strMsg))
                Toast.makeText(m_MainAct, strMsg, Toast.LENGTH_SHORT).show();
        }
        m_MainAct.m_LockView = false;
    }

    public void parseJSON() {
        try {
            if(_jResult==null) return;
            m_nResultType = _jResult.getInt(Net.NET_VALUE_CODE); //
            JSONObject obj = _jResult.getJSONObject(Net.NET_VALUE_RESULT);

            JSONArray arr = obj.getJSONArray("event");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj_evt = arr.getJSONObject(i);
                WikiInfo info = new NetUtil().parseWiki(obj_evt);
                if (m_nConnectType == NetUtil.apis_GET_EVENT_LIST){
                    m_nMaxPage = obj.getInt(Net.NET_VALUE_TOTAL_PAGE);
                    arrEventList.add(info);
                }
            }
        } catch (JSONException e) {            e.printStackTrace();        }
    }

    int m_nSeletedPos = -1;

    public void gotoDetailEvent(int pos) {
        m_nSeletedPos = pos;
        if (m_nConnectType == NetUtil.apis_GET_EVENT_LIST){
            WikiInfo info = arrEventList.get(pos);

            Intent intent = new Intent(getActivity(), DetailEventActivity.class);
            intent.putExtra("info", info);
            m_MainAct.startActivityForResult(intent, 801);
        }
    }

    public void updateEventItem(int view_cnt, int like_cnt, int comment_cnt) {
        if (m_nSeletedPos == -1)
            return;
        if (m_nConnectType == NetUtil.apis_GET_EVENT_LIST){
            arrEventList.get(m_nSeletedPos)._view_cnt = view_cnt;
            arrEventList.get(m_nSeletedPos)._like_cnt = like_cnt;
            arrEventList.get(m_nSeletedPos)._comment_cnt = comment_cnt;
            m_EventAdapter.notifyDataSetChanged();
        }
    }

    JSONObject _jResult;
    @Override
    public void onParseJSon(JSONObject j_source) {
        _jResult = j_source;
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Net.THREAD_REQUEST_END) {
                processForNetEnd();}
        }
    };
}
