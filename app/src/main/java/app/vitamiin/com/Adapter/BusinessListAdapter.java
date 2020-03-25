package app.vitamiin.com.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import java.util.ArrayList;

import app.vitamiin.com.Model.BusinessInfo;
import app.vitamiin.com.R;
import app.vitamiin.com.http.Net;

/**
 * Created by Peter on 2015-08-20.
 */
public class BusinessListAdapter extends ArrayAdapter<BusinessInfo> {
    private Context m_context;

    public BusinessListAdapter(Context context, ArrayList<BusinessInfo> arrayItem) {
        super(context, 0, arrayItem);
        m_context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder itemHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row_business,
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

        private ImageView m_imvPhoto;
        private TextView m_tvBusiness;
        private LinearLayout m_llyRow;

        public ItemHolder(View v) {
            m_imvPhoto = (ImageView) v.findViewById(R.id.imv_good);
            m_tvBusiness = (TextView) v.findViewById(R.id.tv_business);
            m_llyRow = (LinearLayout) v.findViewById(R.id.lly_row);
        }

        public void showInfo(final int position) {
            final BusinessInfo info = getItem(position);

            m_tvBusiness.setText(info._business + " (" + info._good_cnt + ")");

            if (info._imagePath.equals(""))
                Glide.with(m_context).load(R.drawable.default_business_logo).into(m_imvPhoto);
            else
                Glide.with(m_context).load(Net.URL_SERVER1 + info._imagePath).into(m_imvPhoto);

            if (position % 2 == 0)
                m_llyRow.setBackgroundColor(Color.parseColor("#ffe7e7e7"));
            else
                m_llyRow.setBackgroundColor(Color.parseColor("#fff2f2f2"));
        }
    }
}