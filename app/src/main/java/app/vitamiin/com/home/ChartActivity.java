package app.vitamiin.com.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.vitamiin.com.Adapter.ChartListAdapter;
import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.Model.ChartInfo;
import app.vitamiin.com.Model.ContentInfo;
import app.vitamiin.com.Model.GoodInfo;
import app.vitamiin.com.R;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.http.HttpRequester;
import app.vitamiin.com.http.Net;
import app.vitamiin.com.http.OnParseJSonListener;

public class ChartActivity extends BaseActivity implements View.OnClickListener, OnParseJSonListener {
    int m_nResultType = 200;
    int CONNECT_TYPE_VIEW_GOOD1 = 1;
    int CONNECT_TYPE_VIEW_GOOD2 = 2;
    int m_nConnectType = CONNECT_TYPE_VIEW_GOOD1;

    Context m_context;
    GoodInfo m_info1, m_info2;

    RelativeLayout m_rlyGood1, m_rlyGood2;

    ArrayList<ContentInfo> m_material1 = new ArrayList<>();
    ArrayList<ContentInfo> m_material2 = new ArrayList<>();
    ListView m_lsvMaterial;
    ChartListAdapter m_chart_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        m_context = this;

        m_info1 = (GoodInfo) getIntent().getSerializableExtra("good1");
        m_info2 = (GoodInfo) getIntent().getSerializableExtra("good2");
        m_rlyGood1 = (RelativeLayout) findViewById(R.id.ic_good_1);
        m_rlyGood2 = (RelativeLayout) findViewById(R.id.ic_good_2);
        findViewById(R.id.imv_back).setOnClickListener(this);

        LoadView(m_rlyGood1, m_info1);
        LoadView(m_rlyGood2, m_info2);

        m_lsvMaterial = (ListView) findViewById(R.id.lsv_chart);
        m_chart_adapter = new ChartListAdapter(this, new ArrayList<ChartInfo>());
        m_lsvMaterial.setAdapter(m_chart_adapter);

        connectServer(CONNECT_TYPE_VIEW_GOOD1);
    }

    private void LoadView(View v, GoodInfo info) {
        v.setVisibility(View.VISIBLE);

        ((TextView) v.findViewById(R.id.tv_rate)).setText("" + info._rate);
        ImageView m_imvImage = (ImageView) v.findViewById(R.id.imv_good);
        Glide.with(m_context).load(Net.URL_SERVER1 + info._imagePath).into(m_imvImage);

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
                Glide.with(m_context).load(R.drawable.ic_star_empty).into(arr_mimvStars[i]);
            else if(info._rate < i+1)
                Glide.with(m_context).load(R.drawable.ic_star_half).into(arr_mimvStars[i]);
            else
                Glide.with(m_context).load(R.drawable.ic_star_one).into(arr_mimvStars[i]);
    }

    private void drawChart() {
        ContentInfo content_1;
        ContentInfo content_2;
        ChartInfo c_info;
        for (int i = 0; i < m_material1.size(); i++) {
            content_1 = m_material1.get(i);
            c_info = new ChartInfo();

            c_info._f_name = content_1._f_name;
            c_info._per_day_01 = content_1._per_day;
            c_info._content_unit_01 = content_1._content_unit;
            c_info._ppds_01 = content_1._ppds;
            for (int j = 0; j < m_material2.size(); j++) {
                content_2 = m_material2.get(j);
                c_info._per_day_02="x";
                if (content_2._f_name.equals(content_1._f_name)) {
                    c_info._per_day_02 = content_2._per_day;
                    c_info._content_unit_02 = content_2._content_unit;
                    c_info._ppds_02 = content_2._ppds;
                    m_material2.remove(j);
                    break;
                }
            }
            m_chart_adapter.add(c_info);
        }
        for (int j = 0; j < m_material2.size(); j++) {
            content_2 = m_material2.get(j);
            c_info = new ChartInfo();

            c_info._f_name = content_2._f_name;
            c_info._per_day_02 = content_2._per_day;
            c_info._per_day_01 ="x";
            c_info._content_unit_02 = content_2._content_unit;
            c_info._ppds_02 = content_2._ppds;
            m_chart_adapter.add(c_info);
        }
        m_chart_adapter.notifyDataSetChanged();
    }

    private void connectServer(int strAct) {
        m_nConnectType = strAct;

        showProgress();
        String[] paramFields = new String[]{Net.NET_POST_FIELD_ACT,
                Net.NET_POST_FIELD_REQUEST};
        String[] paramValues = null;

        JSONObject w_objJSonData = new JSONObject();
        try {
            w_objJSonData.put(Net.NET_VALUE_MEMBER_ID, UserManager.getInstance().member_id);
            if (m_nConnectType == CONNECT_TYPE_VIEW_GOOD1) {
                w_objJSonData.put("good_id", "" + m_info1._id);
                w_objJSonData.put("good_id2", "" + m_info2._id);

                paramValues = new String[]{
                        Net.POST_FIELD_ACT_VIEWGOOD_FOR_CHART,
                        w_objJSonData.toString()};
            }
        } catch (JSONException e) { e.printStackTrace(); }

        String netUrl = Net.URL_SERVER + Net.URL_SERVER_API;
        HttpRequester.getInstance().init(this, this, handler, netUrl,
                paramFields, paramValues, false);

        HttpRequester.getInstance().setProgressMessage(
                Net.NET_COMMON_STRING_WAITING);
        HttpRequester.getInstance().startNetThread();
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Net.THREAD_REQUEST_END) {
                processForNetEnd();            }
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
                if (m_nConnectType == CONNECT_TYPE_VIEW_GOOD1) {
                    drawChart();
                }
            }
        } else {
            if (!"".equals(strMsg)) {
                Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    JSONObject _jResult;

    public void parseJSON() {
        try {
            m_nResultType = _jResult.getInt(Net.NET_VALUE_CODE);
            JSONObject result = _jResult.getJSONObject(Net.NET_VALUE_RESULT);

            JSONArray arr;
            arr = result.getJSONArray("content");
            for(int i=0; i<arr.length();i++){
                JSONObject obj = arr.getJSONObject(i);
                ContentInfo m_ContentInfo = new ContentInfo();
                m_ContentInfo._content_id = obj.getInt("content_id");
                m_ContentInfo._content_unit = obj.getString("content_unit");
                m_ContentInfo._per_day = obj.getString("per_day");
                m_ContentInfo._ppds = obj.getString("ppds");
                m_ContentInfo._f_name = obj.getString("f_name");
                m_ContentInfo._f_description = obj.getString("f_description");

                m_material1.add(m_ContentInfo);
            }
            arr = result.getJSONArray("content2");
            for(int i=0; i<arr.length();i++){
                JSONObject obj = arr.getJSONObject(i);
                ContentInfo m_ContentInfo = new ContentInfo();
                m_ContentInfo._content_id = obj.getInt("content_id");
                m_ContentInfo._content_unit = obj.getString("content_unit");
                m_ContentInfo._per_day = obj.getString("per_day");
                m_ContentInfo._ppds = obj.getString("ppds");
                m_ContentInfo._f_name = obj.getString("f_name");
                m_ContentInfo._f_description = obj.getString("f_description");

                m_material2.add(m_ContentInfo);
            }
        } catch (JSONException e) {e.printStackTrace();}
    }

    @Override
    public void onParseJSon(JSONObject j_source) {
        _jResult = j_source;}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imv_back:
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.imv_back));
    }
}
