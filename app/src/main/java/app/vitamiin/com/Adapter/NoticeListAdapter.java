package app.vitamiin.com.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
//import android.support.v4.content.ContextCompat;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import app.vitamiin.com.Model.NoticeInfo;
import app.vitamiin.com.R;

/**
 * Created by Peter on 2015-08-20.
 */
public class NoticeListAdapter extends ArrayAdapter<NoticeInfo> {
    private Context m_context;

    public NoticeListAdapter(Context context, ArrayList<NoticeInfo> arrayItem) {
        super(context, 0, arrayItem);
        m_context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder itemHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row_faq,
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

        TextView m_tvTitle, m_tvContent, m_tvRegdate;
        RelativeLayout m_rlyContent;

        public ItemHolder(View v) {
            m_tvTitle = (TextView) v.findViewById(R.id.tv_title);
            m_tvContent = (TextView) v.findViewById(R.id.tv_content);
            m_tvRegdate = (TextView) v.findViewById(R.id.tv_regdate);
            m_rlyContent = (RelativeLayout) v.findViewById(R.id.Rly_content);
        }

        public void showInfo(final int position) {
            final NoticeInfo info = getItem(position);
            m_tvTitle.setText(info._subject);
            m_tvContent.setText(info._content);
            m_tvRegdate.setText(info.regdate);

            if (!info.view){
                m_tvTitle.setTextColor(ContextCompat.getColor(m_context, R.color.main_grey));
                m_rlyContent.setVisibility(View.GONE);
            }
            else{
                m_tvTitle.setTextColor(ContextCompat.getColor(m_context, R.color.main_color_1));
                m_rlyContent.setVisibility(View.VISIBLE);
            }
        }
    }
}