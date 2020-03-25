package app.vitamiin.com.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.R;

public class DetailSurveyActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    Context m_context;

    GridView m_grvView;
    CategoryAdapter m_adapter;
    Boolean is_element_named_none = true;
    int minmax[];

    ArrayList<String> m_selected;
    ArrayList<String> m_selectedText;
    String[] list = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_survey);
        m_context = this;

        initView();
    }

    private void initView() {
        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_next).setOnClickListener(this);

        m_grvView = (GridView) findViewById(R.id.grv_list);
        m_grvView.setOnItemClickListener(this);
        m_adapter = new CategoryAdapter(this, new ArrayList<String>());

        is_element_named_none = getIntent().getBooleanExtra("is_element_named_none", true);
        String strTitle = "";
        list = null;
        minmax = getIntent().getIntArrayExtra("minmax");

        if (getIntent().getStringExtra("type").equals("disease")) {
            strTitle = "질환 정보 ";
            list = getResources().getStringArray(R.array.array_disease);
            ((TextView) findViewById(R.id.tv_sub_title)).setText("해당하는 질병을 겪은 적이 있으시면 체크해주세요.");
            ((TextView) findViewById(R.id.tv_guide)).setText("질환 정보는 최대 5개 까지 선택 가능합니다.");
        } else if (getIntent().getStringExtra("type").equals("interest")) {
            strTitle = "관심 건강분야 정보 ";
            list = getResources().getStringArray(R.array.array_interest_health);
            ((TextView) findViewById(R.id.tv_sub_title)).setText("관심이 있는 건강 분야를 선택해주세요.");
            ((TextView) findViewById(R.id.tv_guide)).setText("관심 건강 분야는 최대 5개 까지 선택 가능합니다.");
        } else if (getIntent().getStringExtra("type").equals("prefer")) {
            strTitle = "선호 건강식품군 ";
            list = getResources().getStringArray(R.array.array_prefer_healthfood);
            ((TextView) findViewById(R.id.tv_sub_title)).setText("선호하는 건강기능 식품군을 선택해주세요.");
            ((TextView) findViewById(R.id.tv_guide)).setText("건강 제품 군은 최대 5개 까지 선택 가능합니다.");
        }
        else if (getIntent().getStringExtra("type").equals("allergy_sm")) {
            strTitle = "알레르기 증상 정보 ";
            list = getResources().getStringArray(R.array.life_allergy_symptom);
            ((TextView) findViewById(R.id.tv_sub_title)).setText("어떤 알레르기 증상들이 있으신지요?");
            ((TextView) findViewById(R.id.tv_guide)).setText("알레르기에 대한 선택은 최대 5개까지 가능합니다.");
        } else if (getIntent().getStringExtra("type").equals("pee_sm")) {
            strTitle = "소변 상태 정보 ";
            list = getResources().getStringArray(R.array.life_pee_symptom);
            ((TextView) findViewById(R.id.tv_sub_title)).setText("요즘 소변의 상태는 어떠하신지요?");
            ((TextView) findViewById(R.id.tv_guide)).setText("소변의 상태에 대한 선택은 최대 5개까지 가능합니다.");
        } else if (getIntent().getStringExtra("type").equals("dung_sm")) {
            strTitle = "대변 상태 정보 ";
            list = getResources().getStringArray(R.array.life_dung_symptom);
            ((TextView) findViewById(R.id.tv_sub_title)).setText("요즘 대변의 상태는 어떠하신지요?");
            ((TextView) findViewById(R.id.tv_guide)).setText("대변의 상태에 대한 선택은 최대 5개까지 가능합니다.");
        } else if (getIntent().getStringExtra("type").equals("life_pt")) {
            strTitle = "생활 패턴 정보 ";
            list = getResources().getStringArray(R.array.life_life_pattern);
            ((TextView) findViewById(R.id.tv_sub_title)).setText("요즘 생활은 어떻게 하고 계신지요?");
            ((TextView) findViewById(R.id.tv_guide)).setText("생활 패턴에 대한 선택은 최대 5개까지 가능합니다.");
        } else if (getIntent().getStringExtra("type").equals("eat_pt")) {
            strTitle = "식사 패턴 정보 ";
            list = getResources().getStringArray(R.array.life_eat_pattern);
            ((TextView) findViewById(R.id.tv_sub_title)).setText("요즘 식사는 어떻게 하시는지요?");
            ((TextView) findViewById(R.id.tv_guide)).setText("식사 패턴에 대한 선택은 최대 5개까지 가능합니다.");
        }
        else if (getIntent().getStringExtra("type").equals("allergy_fd")) {
            strTitle = "알레르기 정보 ";
            list = getResources().getStringArray(R.array.array_allergy);
            ((TextView) findViewById(R.id.tv_sub_title)).setText("어떤식품에 알레르기가 있나요?");
            ((TextView) findViewById(R.id.tv_guide)).setText("알레르기 항목 선택은 최대 5개까지 가능합니다.");
        } else if (getIntent().getStringExtra("type").equals("drug")) {
            strTitle = "최근 복약 정보 ";
            list = getResources().getStringArray(R.array.array_eat_drug);
            ((TextView) findViewById(R.id.tv_sub_title)).setText("최근에 복용하시는 약이 있으신지요?");
            ((TextView) findViewById(R.id.tv_guide)).setText("약 종류의 선택은 최대 5개까지 가능합니다.");
        } else if (getIntent().getStringExtra("type").equals("health_fd")) {
            strTitle = "최근 섭취 건강기능식품 정보 ";
            list = getResources().getStringArray(R.array.array_eat_healthfood);
            ((TextView) findViewById(R.id.tv_sub_title)).setText("최근에 드시는 건강기능 식품은 있으신지요?");
            ((TextView) findViewById(R.id.tv_guide)).setText("건강기능식품 선택은 최대 5개까지 가능합니다.");
        } else if (getIntent().getStringExtra("type").equals("health_stt")) {
            strTitle = "최근 건강 상태 정보 ";
            list = getResources().getStringArray(R.array.array_health_state);
            ((TextView) findViewById(R.id.tv_sub_title)).setText("최근의 건강상태는 어떠신지요?");
            ((TextView) findViewById(R.id.tv_guide)).setText("건강상태에 대한 선택은 최대 3개까지 가능합니다.");
        }

        for(String strOne:list) m_adapter.add(strOne);
        m_grvView.setAdapter(m_adapter);

        m_selected = new ArrayList<>();
        m_selectedText = new ArrayList<>();

        if (getIntent().getStringExtra(getIntent().getStringExtra("type")) != null){
            if(getIntent().getStringExtra(getIntent().getStringExtra("type")).length() > 0) {
                String[] arr = getIntent().getStringExtra(getIntent().getStringExtra("type")).split(",");
                for(String strOne:arr){
                    m_selected.add(strOne);
                    m_selectedText.add(list[Integer.parseInt(strOne)]);
                }
                checkNext();
                m_adapter.notifyDataSetChanged();
            }
        }
        if (getIntent().getBooleanExtra("edit", false))
            ((TextView) findViewById(R.id.tv_title)).setText(strTitle + "수정");
        else
            ((TextView) findViewById(R.id.tv_title)).setText(strTitle + "입력");
    }

    public boolean checkNext() {
        if (m_selected.size() < minmax[0]) {
            findViewById(R.id.tv_next).setSelected(false);
            return false;
        }
        findViewById(R.id.tv_next).setSelected(true);
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_back:
                intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
            case R.id.tv_next:
                if (m_selected.size() < minmax[0]) {
                    Toast.makeText(this, "항목을 더 선택해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String strIll = "";
                for (int i = 0; i < m_selected.size(); i++)
                    strIll += m_selected.get(i) + ",";
                if (strIll.length() > 0)
                    strIll = strIll.substring(0, strIll.length() - 1);

                String text = "";
                for (int i = 0; i < m_selectedText.size(); i++)
                    text += m_selectedText.get(i) + ",";
                if (text.length() > 0)
                    text = text.substring(0, text.length() - 1);
                text = text.replace("\n", "");

                intent = new Intent();
                intent.putExtra(getIntent().getStringExtra("type"), strIll);
                intent.putExtra("text", text);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.tv_back));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == list.length - 1 && is_element_named_none) {
            m_selected.clear();
            m_selectedText.clear();
            if(m_selected.contains("" + (list.length-1))) {
                m_selected.add("" + (list.length - 1));
                m_selectedText.add(m_adapter.getItem(list.length - 1));
            }
        }

        if (m_selected.contains("" + position))
            m_selected.remove("" + position);
        else {
            if (m_selected.size() < minmax[1]) {
                m_selected.add("" + position);
                if(position != list.length-1)
                m_selected.remove("" + (list.length - 1));
            }
        }

        if (m_selectedText.contains(m_adapter.getItem(position)))
            m_selectedText.remove(m_adapter.getItem(position));
        else {
            if (m_selectedText.size() < minmax[1]) {
                m_selectedText.add(m_adapter.getItem(position));
                if(position != list.length-1)
                    m_selectedText.remove(m_adapter.getItem(list.length - 1));
            }
        }

        m_adapter.notifyDataSetChanged();
        checkNext();
    }

    public class CategoryAdapter extends ArrayAdapter<String> {
        private Context m_context;

        public CategoryAdapter(Context context, ArrayList<String> arrayItem) {
            super(context, 0, arrayItem);
            m_context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemHolder itemHolder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_row_simple_height65dp,
                        parent, false);
                itemHolder = new ItemHolder(convertView);
                convertView.setTag(itemHolder);
            } else {
                itemHolder = (ItemHolder) convertView.getTag();
            }

            itemHolder.showInfo(position);
            return convertView;
        }

        public class ItemHolder {
            TextView m_uiTvItem;

            public ItemHolder(View v) {
                m_uiTvItem = (TextView) v.findViewById(R.id.tv_item);
            }

            public void showInfo(int position) {
                String category = getItem(position);
                m_uiTvItem.setText(category);

                if (m_selected.contains("" + position)) {
                    int div = position % 8;
                    if (div == 1 || div == 3 || div == 4 || div == 6) {
                        m_uiTvItem.setBackgroundColor(ContextCompat.getColor(m_context, R.color.main_color_1));
                    } else {
                        m_uiTvItem.setBackgroundColor(ContextCompat.getColor(m_context, R.color.main_color_2));
                    }
                    m_uiTvItem.setTextColor(ContextCompat.getColor(m_context, R.color.white_color));
                } else {
                    if (m_selected.size() == minmax[1])
                        m_uiTvItem.setTextColor(ContextCompat.getColor(m_context, R.color.main_grey_1));
                    else
                        m_uiTvItem.setTextColor(ContextCompat.getColor(m_context, R.color.black_color));
                    int div = position % 8;
                    if (div == 1 || div == 3 || div == 4 || div == 6) {
                        m_uiTvItem.setBackgroundColor(ContextCompat.getColor(m_context, R.color.bg_grey));
                    } else {
                        m_uiTvItem.setBackgroundColor(ContextCompat.getColor(m_context, R.color.bg_grey_white));
                    }
                }
            }
        }
    }
}
