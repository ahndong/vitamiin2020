package app.vitamiin.com.home;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import app.vitamiin.com.R;

/**
 * Created by dong8 on 2016-12-28.
 */

public class SearchSelectConditionDialog extends Dialog implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Context m_context;

    private GridView m_grvView;
    private TextView m_edtSearch;
    private app.vitamiin.com.home.SearchSelectConditionDialog.CategoryAdapter m_adapter, m_adapterToShow;
    private int limit_no = 1;
    private int _type;
    private String[] m_list = null;

    private ArrayList<String> m_selected;
    private ArrayList<String> m_selectedText;
    int m_currentPage = 1;
    int m_nMaxPage = 1;

    public SearchSelectConditionDialog(Context context, String title, int type, String[] arrStr, String value) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        m_context = context;
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dlg_search_select_condition);
        _type = type;

        ((TextView)findViewById(R.id.tv_title)).setText(title);
        findViewById(R.id.tv_ok).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);

        m_edtSearch = ((TextView)findViewById(R.id.edt_search));
        m_edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideSoftKeyBoard();
                    //Util.hideKeyPad((Activity) getContext());
                    //((Activity) getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    return true;
                }
                return false;
            }
        });

        m_edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to Filter
//                m_adapter.getFilter().filter(s.toString());
                m_adapterToShow.clear();
                m_currentPage = 1;
                m_nMaxPage = 1;
                for (int i = 0; i < m_adapter.getCount(); i++) {
                    if(m_adapter.getItem(i).contains(s.toString()))
                        m_adapterToShow.add(m_adapter.getItem(i));
                }
                m_grvView.setAdapter(m_adapterToShow);
                m_adapterToShow.notifyDataSetChanged();
            }
            @Override            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }
            @Override            public void afterTextChanged(Editable s) {            }
        });

        m_grvView = (GridView) findViewById(R.id.grv_list);
        m_grvView.setOnItemClickListener(this);
        m_adapter = new app.vitamiin.com.home.SearchSelectConditionDialog.CategoryAdapter(m_context, new ArrayList<String>());
        m_adapterToShow = new app.vitamiin.com.home.SearchSelectConditionDialog.CategoryAdapter(m_context, new ArrayList<String>());

        m_list = arrStr;

        if (_type==0) {
            for (int i = 0; i < m_list.length-1; i++)
                m_adapter.add(m_list[i]);
        } else if (_type==1) {
            for (String One:m_list)
                m_adapter.add(One);
            findViewById(R.id.lly_search).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.tv_guide)).setText("효능 기능에 대한 선택은 1~5개까지 가능합니다.");
        }
        m_grvView.setAdapter(m_adapter);

        m_selected = new ArrayList<>();
        m_selectedText = new ArrayList<>();

        if (((AppCompatActivity)m_context).getIntent().getStringExtra(((AppCompatActivity)m_context).getIntent().getStringExtra("type")) != null){
            if(((AppCompatActivity)m_context).getIntent().getStringExtra(((AppCompatActivity)m_context).getIntent().getStringExtra("type")).length() > 0) {
                String[] arr = ((AppCompatActivity)m_context).getIntent().getStringExtra(((AppCompatActivity)m_context).getIntent().getStringExtra("type")).split(",");
                for (String One:arr) {
                    m_selected.add(One);
                    m_selectedText.add(m_list[Integer.parseInt(One)]);
                }
                checkNext();
                m_adapter.notifyDataSetChanged();
            }
        }
    }

    private void hideSoftKeyBoard() {
        try {
            // hides the soft keyboard when the drawer opens
            InputMethodManager inputManager = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean checkNext() {
//        findViewById(R.id.tv_ok).setSelected(false);
//        if (m_selected.size() == 0) {
//            return false;
//        }
//        findViewById(R.id.tv_ok).setSelected(true);
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_ok:
                if(m_selected.size()==0){
                    dismiss();
                    break;
                }
                /*
                if (m_selected.size() == 0) {
                    Toast.makeText(this, "1개 이상의 항목을 선택해 주십시오.", Toast.LENGTH_SHORT).show();
                    return;
                }*/
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

                int i;
                for(i = 0; i < m_list.length; i++)
                    if(m_list[i].equals(text)) break;
//
//                intent = new Intent();
//                intent.putExtra(getIntent().getStringExtra("type"), strIll);
//                intent.putExtra("text", text);
//                setResult(RESULT_OK, intent);
//                finish();

                if(_type==0){
                    ((SearchInitActivity) m_context).setProductGroup(String.valueOf(i));
                } else if (_type==1) {
                    ((SearchInitActivity) m_context).setFunctionality(strIll);
                }
                dismiss();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.tv_cancel));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        if (m_selected.contains("" + position))
//            m_selected.remove("" + position);
//        else {
//            if (m_selected.size() < limit_no)
//                m_selected.add("" + position);
//        }

        if (m_selectedText.contains(((CategoryAdapter)m_grvView.getAdapter()).getItem(position))){
            m_selectedText.remove(((CategoryAdapter)m_grvView.getAdapter()).getItem(position));
            m_selected.remove("" + position);
        }else {
            if (m_selectedText.size() < limit_no){
                m_selectedText.add(((CategoryAdapter)m_grvView.getAdapter()).getItem(position));
                m_selected.add("" + position);
            }
        }

        ((CategoryAdapter)m_grvView.getAdapter()).notifyDataSetChanged();
        //m_adapter.notifyDataSetChanged();
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
            app.vitamiin.com.home.SearchSelectConditionDialog.CategoryAdapter.ItemHolder itemHolder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_row_simple_height65dp,
                        parent, false);
                itemHolder = new app.vitamiin.com.home.SearchSelectConditionDialog.CategoryAdapter.ItemHolder(convertView);
                convertView.setTag(itemHolder);
            } else {
                itemHolder = (app.vitamiin.com.home.SearchSelectConditionDialog.CategoryAdapter.ItemHolder) convertView.getTag();
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

                if (m_selectedText.contains(((CategoryAdapter)m_grvView.getAdapter()).getItem(position))){
                    int div = position % 8;
                    if (div == 1 || div == 3 || div == 4 || div == 6) {
                        m_uiTvItem.setBackgroundColor(ContextCompat.getColor(m_context, R.color.main_color_1));
                    } else {
                        m_uiTvItem.setBackgroundColor(ContextCompat.getColor(m_context, R.color.main_color_2));
                    }
                    m_uiTvItem.setTextColor(ContextCompat.getColor(m_context, R.color.white_color));
                }else {
                    if (m_selected.size() == limit_no)
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
