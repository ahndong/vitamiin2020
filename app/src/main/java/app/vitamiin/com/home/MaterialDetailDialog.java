package app.vitamiin.com.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import app.vitamiin.com.Adapter.MaterialListAdapter;
import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.DetailDialog;
import app.vitamiin.com.Model.GoodInfo;
import app.vitamiin.com.Model.IngredientInfo;
import app.vitamiin.com.R;
import app.vitamiin.com.setting.FaqNoticeActivity;

/**
 * Created by dong8 on 2017-03-17.
 */

public class MaterialDetailDialog extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener{
    Context m_context;

    GoodInfo m_info1;
    ArrayList<IngredientInfo> arrIngredientTotal = new ArrayList<>();
    ArrayList<IngredientInfo> arrIngredientOneElement = new ArrayList<>();
    ArrayList<ArrayList<IngredientInfo>> arrarrIngredient = new ArrayList<>();
    int m_nEWG12, m_nHarmful, m_nAdditive;

    TextView m_tvSource;
    ListView m_lsvMaterial;
    MaterialListAdapter m_material_adapter;
    LinearLayout m_llyBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dlg_material_detail);

        m_context = this;

        m_info1 = (GoodInfo) getIntent().getSerializableExtra("info");
        arrIngredientTotal = (ArrayList<IngredientInfo>) getIntent().getSerializableExtra("IngredientInfo");
        m_nEWG12 = getIntent().getIntExtra("EWG12",0);
        m_nHarmful = getIntent().getIntExtra("Harmful",0);
        m_nAdditive = getIntent().getIntExtra("Additive",0);

        initView();
    }

    private void initView() {
        findViewById(R.id.tv_close).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_ewg12)).setText(m_nEWG12 + "개");
        ((TextView) findViewById(R.id.tv_harmful)).setText(m_nHarmful + "개");
        ((TextView) findViewById(R.id.tv_additive)).setText(m_nAdditive + "개");
        findViewById(R.id.tv_more_material).setVisibility(View.GONE);

        m_tvSource = (TextView) findViewById(R.id.tv_source);
        m_tvSource.setOnClickListener(this);

        m_lsvMaterial = (ListView) findViewById(R.id.lsv_material);
        m_material_adapter = new MaterialListAdapter(this, new ArrayList<ArrayList<IngredientInfo>>());
        m_lsvMaterial.setAdapter(m_material_adapter);
        m_lsvMaterial.setOnItemClickListener(this);

        m_llyBar = (LinearLayout) findViewById(R.id.lly_bar);
        ViewTreeObserver vto = m_llyBar.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                m_llyBar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                m_nBarWidth = m_llyBar.getMeasuredWidth();
                if (m_material_adapter.getCount() > 0)
                    drawChart();
            }
        });

        int fore_mat_id = -1;
        for (int i=0; i < arrIngredientTotal.size(); i++) {
            if(i==0)
                fore_mat_id = arrIngredientTotal.get(i)._mat_id;

            if(fore_mat_id != arrIngredientTotal.get(i)._mat_id) {
                if(arrIngredientOneElement.size()!=0)
                    arrarrIngredient.add(arrIngredientOneElement);

                arrIngredientOneElement = new ArrayList<>();
                arrIngredientOneElement.add(arrIngredientTotal.get(i));
            }else{
                arrIngredientOneElement.add(arrIngredientTotal.get(i));
            }
            fore_mat_id = arrIngredientTotal.get(i)._mat_id;
        }
        if(arrIngredientOneElement.size()!=0)
            arrarrIngredient.add(arrIngredientOneElement);

        int refEWG = arrarrIngredient.get(0).get(0)._ewg;
        int refIval = 0;

        for(int j = 0; j < arrarrIngredient.size(); j++){
            for(int i = 0; i < arrarrIngredient.size(); i++) {
                if (arrarrIngredient.get(i).get(0)._ewg >= refEWG) {
                    refIval = i;
                    refEWG = arrarrIngredient.get(refIval).get(0)._ewg;
                }
            }

            m_material_adapter.add(arrarrIngredient.get(refIval));
            arrarrIngredient.remove(refIval);
            refIval = 0;
            if(arrarrIngredient.size()!=0)
                refEWG = arrarrIngredient.get(0).get(0)._ewg;
            j--;
        }

//                for(ArrayList<> arr: arrarrIngredient)
//                    m_material_adapter.add(arr);

        m_material_adapter.notifyDataSetChanged();
    }

    int m_nBarWidth = 0;

    private void drawChart() {
        int oneByOne = m_nBarWidth / m_material_adapter.getCount();

        int count1 = 0;
        int count2 = 0;
        int count3 = 0;
        int count4 = 0;
        int count5 = 0;

        for (int i = 0; i < m_material_adapter.getCount(); i++) {
            IngredientInfo m_IngredientInfo = m_material_adapter.getItem(i).get(0);

            if (m_IngredientInfo!=null){
                switch (m_IngredientInfo._ewg){
                    case 0:
                        count1++;
                        break;
                    case 1:
                        count2++;
                        break;
                    case 2:
                        count3++;
                        break;
                    case 3:
                        count4++;
                        break;
                    case 4:
                        count5++;
                        break;
                }
            }
        }

        View view05 = new View(this);
        view05.setLayoutParams(new LinearLayout.LayoutParams(count5 * oneByOne, LinearLayout.LayoutParams.MATCH_PARENT));
        view05.setBackgroundColor(Color.parseColor("#ff6666"));
        m_llyBar.addView(view05);

        View view04 = new View(this);
        view04.setLayoutParams(new LinearLayout.LayoutParams(count4 * oneByOne, LinearLayout.LayoutParams.MATCH_PARENT));
        view04.setBackgroundColor(Color.parseColor("#ffe066"));
        m_llyBar.addView(view04);

        View view03 = new View(this);
        view03.setLayoutParams(new LinearLayout.LayoutParams(count3 * oneByOne, LinearLayout.LayoutParams.MATCH_PARENT));
        view03.setBackgroundColor(Color.parseColor("#71da71"));
        m_llyBar.addView(view03);

        View view02 = new View(this);
        view02.setLayoutParams(new LinearLayout.LayoutParams(count2 * oneByOne, LinearLayout.LayoutParams.MATCH_PARENT));
        view02.setBackgroundColor(Color.parseColor("#80bfff"));
        m_llyBar.addView(view02);

        View view01 = new View(this);
        view01.setLayoutParams(new LinearLayout.LayoutParams(count1 * oneByOne, LinearLayout.LayoutParams.MATCH_PARENT));
        view01.setBackgroundColor(Color.parseColor("#cccccc"));
        m_llyBar.addView(view01);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_close:
                finish();
                break;
            case R.id.tv_source:
                Intent intent = new Intent(this, FaqNoticeActivity.class);
                intent.putExtra("from_source", true);
                intent.putExtra("from_content_dialog", true);
                intent.putExtra("type", 0);
                startActivity(intent);
                break;
//            case R.id.tv_question:
//                new NoticeDialog(this, "표 설명", "함량: -3- \n 비율(%): -3-","닫기", false).show();
//                break;
        }
    }

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.tv_close));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == m_lsvMaterial) {
            ArrayList<IngredientInfo> m_IngredientInfo = m_material_adapter.getItem(position);
            /*
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("");
            alert.setMessage(info.description);
            AlertDialog dialog = alert.show();
            TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
            messageText.setGravity(Gravity.CENTER);*/

            new DetailDialog(this, m_IngredientInfo).show();
        }
    }
}
