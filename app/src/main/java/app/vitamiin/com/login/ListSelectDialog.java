package app.vitamiin.com.login;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import app.vitamiin.com.R;
import app.vitamiin.com.setting.LikeActivity;
import app.vitamiin.com.setting.ModifyDetailLifeActivity;
import app.vitamiin.com.common.Util;
import app.vitamiin.com.home.MainActivity;
import app.vitamiin.com.home.ReviewWriteActivity;
import app.vitamiin.com.home.SearchInitActivity;
import app.vitamiin.com.home.ExpWriteActivity;

public class ListSelectDialog extends Dialog implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Context _context;

    ListView m_lsvLife;
    LifeAdapter m_Adapter;
    String _title;
    int m_nSelectedIndex = -1;
    ArrayList<String> m_selectedIndexes = new ArrayList<>();

    int _type = 0;
    int _value = -1;

    public ListSelectDialog(Context context, String title, String[] list, int type, int value) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        _context = context;
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if(type == 13 || type == 25 || type == 12 || type == 17
                      || type == 8 || type == 15 || type == 28
                      || type == 16 || type == 14 || type == 11){
            setContentView(R.layout.dlg_clickandclose);
        }
        else if(type == 1 || type == 2 || type == 3 || type == 4 || type == 5 ||
                type == 101 || type == 102 || type == 103 || type == 104 || type == 105){
            setContentView(R.layout.dlg_clickandclose);
            findViewById(R.id.tv_cancel).setVisibility(View.GONE);
        }else{
            setContentView(R.layout.dlg_listselect);
        }

        int lav_height = list.length>14? 420:list.length * 30;
        ListView llyView = (ListView) findViewById(R.id.lsv_life);
        ViewGroup.LayoutParams params = llyView.getLayoutParams();
        params.height = Util.dipToPixels(context, lav_height);
//        switch (type){
//            case 14:
//                params.height = Util.dipToPixels(context, lav_height);
//                break;
//            case 16:
//                params.height = Util.dipToPixels(context, lav_height);
//                break;
//            case 3:
//            case 103:
//                params.height = Util.dipToPixels(context, lav_height);
//                break;
//            case 5:
//            case 105:
//            case 11://리뷰 탭 : 경험공유 카테고리 선택
//                params.height = Util.dipToPixels(context, lav_height);
//                break;
//            case 12://리뷰 작성: 카테고리 선택
//                params.height = Util.dipToPixels(context, lav_height);
//                break;
//            case 17://리뷰 작성: 복용기간 선택5
//            case 25://노하우 공유 작성: 복용기간 선택5
//                params.height = Util.dipToPixels(context, lav_height);
//            break;
//            case 8://리뷰 작성 : 구매경로 선택11
//                params.height = Util.dipToPixels(context, lav_height);
//            break;
//            case 13://노하우 공유 작성: 구매경로 선택
//                params.height = Util.dipToPixels(context, lav_height);
//            break;
//        }
        llyView.setLayoutParams(params);

        if(findViewById(R.id.tv_ok)!=null)
            findViewById(R.id.tv_ok).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);

        _title = title;
        _type = type;
        _value = value;
        ((TextView) findViewById(R.id.tv_title)).setText(title);

        m_Adapter = new LifeAdapter(_context, new ArrayList<String>());

        for (String strOne:list)
            m_Adapter.add(strOne);

        m_lsvLife = (ListView) findViewById(R.id.lsv_life);
        m_lsvLife.setAdapter(m_Adapter);
        m_lsvLife.setOnItemClickListener(this);

        if (_value > -1) {
            m_nSelectedIndex = _value;
            m_Adapter.setPos(m_nSelectedIndex);
            m_Adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ok:
                if (m_nSelectedIndex == -1) {
                    Toast.makeText(_context, _title + "을 선택해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (_type == 8) {
                    ((ReviewWriteActivity) _context).setBuyPath(m_nSelectedIndex);
                } else if (_type == 12) {
                    ((ReviewWriteActivity) _context).setCategory(m_nSelectedIndex);
                } else if (_type == 17) {
                    ((ReviewWriteActivity) _context).setPeroid(m_nSelectedIndex);
                } else if (_type == 13) {
                    ((ExpWriteActivity) _context).setCategory(m_nSelectedIndex);
                } else if (_type == 25) {
                    ((ExpWriteActivity) _context).setPeriod(m_nSelectedIndex);
                }
                else if (_type == 9) {
                }else if (_type == 11) {
                    ((MainActivity) _context).setReviewCategory(m_nSelectedIndex);
                } else if (_type == 15) {
                    ((LikeActivity) _context).setSort(m_nSelectedIndex);
                } else if (_type == 1 || _type == 2 || _type == 3 || _type == 4 || _type == 5){
                    ((DetailLife1of3Activity) _context).setLife(_type, m_nSelectedIndex);
                } else if (_type == 101 || _type == 102 || _type == 103 || _type == 104 || _type == 105){
                    ((ModifyDetailLifeActivity) _context).setLife(_type, m_nSelectedIndex);
                } else if (_type == 26) {
                    ((ExpWriteActivity) _context).setCount(m_nSelectedIndex);
                }
                dismiss();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        m_nSelectedIndex = position;
        m_Adapter.setPos(m_nSelectedIndex);

        switch (_type) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                ((DetailLife1of3Activity) _context).setLife(_type, m_nSelectedIndex);
                dismiss();
                break;
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
                ((ModifyDetailLifeActivity) _context).setLife(_type, m_nSelectedIndex);
                dismiss();
                break;
            case 8:
                ((ReviewWriteActivity) _context).setBuyPath(m_nSelectedIndex);
                dismiss();
                break;
            case 11:
                ((MainActivity) _context).setReviewCategory(m_nSelectedIndex);
                dismiss();
                break;
            case 12:
                ((ReviewWriteActivity) _context).setCategory(m_nSelectedIndex);
                dismiss();
                break;
            case 13:
                ((ExpWriteActivity) _context).setCategory(m_nSelectedIndex);
                dismiss();
                break;
            case 15:
                ((LikeActivity) _context).setSort(m_nSelectedIndex);
                dismiss();
                break;
            case 17:
                ((ReviewWriteActivity) _context).setPeroid(m_nSelectedIndex);
                dismiss();
                break;
            case 25:
                ((ExpWriteActivity) _context).setPeriod(m_nSelectedIndex);
                dismiss();
                break;
            case 28:
                ((SearchInitActivity) _context).setPerson(String.valueOf(m_nSelectedIndex));
                dismiss();
                break;
        }
        m_Adapter.notifyDataSetChanged();
        ((TextView)view.findViewById(R.id.tv_date)).setTextColor(ContextCompat.getColor(_context, R.color.main_color_1));
    }

    public class LifeAdapter extends ArrayAdapter<String> {

        private Context m_context;
        int m_SelectedIndex = -1;

        public LifeAdapter(Context context, ArrayList<String> arrayItem) {
            super(context, 0, arrayItem);
            m_context = context;
        }

        public void setPos(int index) {
            m_SelectedIndex = index;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemHolder itemHolder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_row_simple_height30dp,
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
            TextView m_tvTitle;

            public ItemHolder(View v) {
                m_tvTitle = (TextView) v.findViewById(R.id.tv_date);
            }

            public void showInfo(int position) {
                String info = getItem(position);
                m_tvTitle.setText(info);

                if(m_nSelectedIndex != -1){
                    if (m_SelectedIndex == position)
                        m_tvTitle.setTextColor(ContextCompat.getColor(m_context, R.color.main_color_1));
                    else
                        m_tvTitle.setTextColor(ContextCompat.getColor(m_context, R.color.main_grey));
                } else {
                    if (m_selectedIndexes.contains("" + position))
                        m_tvTitle.setTextColor(ContextCompat.getColor(m_context, R.color.main_color_1));
                    else
                        m_tvTitle.setTextColor(ContextCompat.getColor(m_context, R.color.main_grey));
                }
            }
        }
    }
}
