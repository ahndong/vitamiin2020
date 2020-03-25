package app.vitamiin.com.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import app.vitamiin.com.Model.ContentInfo;
import app.vitamiin.com.R;

/**
 * Created by dong8 on 2017-02-21.
 */
public class ContentListAdapter extends ArrayAdapter<ContentInfo> {
    private Context m_context;

    public ContentListAdapter(Context context, ArrayList<ContentInfo> arrayItem) {
        super(context, 0, arrayItem);
        m_context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder itemHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row_content,
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
        TextView m_tvName, m_tvPerDay, m_tvPPDS;

        public ItemHolder(View v) {
            m_tvName = (TextView) v.findViewById(R.id.tv_cont_name);
            m_tvPerDay = (TextView) v.findViewById(R.id.tv_cont_per_day);
            m_tvPPDS = (TextView) v.findViewById(R.id.tv_cont_ppds);
        }

        public void showInfo(final int position) {
            final ContentInfo info = getItem(position);

            m_tvName.setText(info._f_name);
            m_tvPerDay.setText(info._per_day + info._content_unit);
            m_tvPPDS.setText(info._ppds + " %");
        }
    }
}
