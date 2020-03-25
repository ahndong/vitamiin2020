package app.vitamiin.com.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import app.vitamiin.com.Adapter.HashListAdapter;
import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.R;

public class HashListActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    ListView m_lsvList1, m_lsvList2;
    HashListAdapter m_adapter1, m_adapter2;

    public ArrayList<String> m_selected = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hash_list);

        initView();
    }

    private void initView() {

        findViewById(R.id.imv_back).setOnClickListener(this);
        findViewById(R.id.tv_ok).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);

        m_lsvList1 = (ListView) findViewById(R.id.lsv_list1);
        m_lsvList2 = (ListView) findViewById(R.id.lsv_list2);
        m_adapter1 = new HashListAdapter(this, new ArrayList<String>(), 1);
        m_adapter2 = new HashListAdapter(this, new ArrayList<String>(), 1);

        String[] list = getResources().getStringArray(R.array.hash_tag);
        int startIndexOfAdventage = 8;

        for (int i = 0; i < startIndexOfAdventage; i++)
            m_adapter1.add(list[i]);
        for (int i = startIndexOfAdventage; i < list.length; i++)
            m_adapter2.add(list[i]);

        m_lsvList1.setAdapter(m_adapter1);
        m_lsvList1.setOnItemClickListener(this);
        m_lsvList2.setAdapter(m_adapter2);
        m_lsvList2.setOnItemClickListener(this);

        String str = getIntent().getStringExtra("hash");
        String[] hashList = str.split("#");
        for (int i = 0; i < hashList.length; i++) {
            if(hashList[i].length()>1){
                hashList[i] = "#" + hashList[i].trim();
                m_selected.add(hashList[i]);
            }
        }
        m_adapter1.notifyDataSetChanged();
        m_adapter2.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imv_back:
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_ok:
                clickOK();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.imv_back));
    }

    private void clickOK() {
        if (m_selected.size() == 0) {
            Toast.makeText(this, "해시태그를 1개 이상 선택해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        String str = "";
        for (int i = 0; i < m_selected.size(); i++) {
            str += m_selected.get(i) + " ";
        }
        str = str.substring(0, str.length() - 1);
        Intent intent = new Intent();
        intent.putExtra("hash", str);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String info="";
        if(parent == m_lsvList1){
            info= m_adapter1.getItem(position);
        }if(parent == m_lsvList2){
            info= m_adapter2.getItem(position);
        }
        if (m_selected.contains(info))
            m_selected.remove(info);
        else {
            if (m_selected.size() < 4)
                m_selected.add(info);
        }
        m_adapter1.notifyDataSetChanged();
        m_adapter2.notifyDataSetChanged();
    }
}
