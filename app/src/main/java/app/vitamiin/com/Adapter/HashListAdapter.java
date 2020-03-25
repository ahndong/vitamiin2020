package app.vitamiin.com.Adapter;

import android.content.Context;
//import android.support.v4.content.ContextCompat;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import app.vitamiin.com.R;
import app.vitamiin.com.home.HashListActivity;

/**
 * Created by Peter on 2015-08-20.
 */
public class HashListAdapter extends ArrayAdapter<String> {

    private Context m_context;
    private int mkind = -1;

    public HashListAdapter(Context context, ArrayList<String> arrayItem, int kind) {
        super(context, 0, arrayItem);
        m_context = context;
        mkind = kind;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder itemHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row_hash,
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

        public void showInfo(final int position) {
            final String info = getItem(position);

            m_uiTvItem.setText(info);

            //home.SearchInitActivity 이거로 체크해서......getLocalClassName()
            if(mkind == 1){
                if (((HashListActivity) m_context).m_selected.contains(info)) {
                    m_uiTvItem.setBackgroundColor(ContextCompat.getColor(m_context, R.color.main_color_1));
                    m_uiTvItem.setTextColor(ContextCompat.getColor(m_context, R.color.white_color));
                } else {
                    m_uiTvItem.setTextColor(ContextCompat.getColor(m_context, R.color.black_color));
                    if (position % 2 == 0) {
                        m_uiTvItem.setBackgroundColor(ContextCompat.getColor(m_context, R.color.bg_grey));
                    } else {
                        m_uiTvItem.setBackgroundColor(ContextCompat.getColor(m_context, R.color.bg_grey_white));
                    }
                }
            }else{
                m_uiTvItem.setTextColor(ContextCompat.getColor(m_context, R.color.black_color));
            }
        }
    }
}