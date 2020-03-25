package app.vitamiin.com.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import app.vitamiin.com.Model.FilterInfo;
import app.vitamiin.com.R;
import app.vitamiin.com.http.Net;

/**
 * Created by Peter on 2015-08-20.
 */
public class FilterListAdapter extends ArrayAdapter<FilterInfo> {

    private Context m_context;
    private Boolean m_isLogo;

    public FilterListAdapter(Context context, ArrayList<FilterInfo> arrayItem, Boolean isLogo) {
        super(context, 0, arrayItem);
        m_context = context;
        m_isLogo = isLogo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder itemHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row_filter,
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

        private ImageView m_imvCheck, m_imvLogo, m_imvTemp;
        private TextView m_tvString;
        private LinearLayout m_llyRow;

        public ItemHolder(View v) {
            m_imvCheck = (ImageView) v.findViewById(R.id.imv_check);
            m_imvLogo = (ImageView) v.findViewById(R.id.imv_bussiness_logo);
            m_imvTemp = (ImageView) v.findViewById(R.id.imv_temp);
            m_tvString = (TextView) v.findViewById(R.id.tv_string);

            m_llyRow = (LinearLayout) v.findViewById(R.id.lly_row);
        }

        public void showInfo(final int position) {
            final FilterInfo info = getItem(position);

            if (m_isLogo) {
                m_imvLogo.setVisibility(View.VISIBLE);
                m_imvTemp.setVisibility(View.VISIBLE);

                if (info._imgPath.equals(""))
                    Glide.with(m_context).load(R.drawable.default_business_logo).into(m_imvLogo);
                else
                    Glide.with(m_context).load(Net.URL_SERVER1 + info._imgPath).into(m_imvLogo);

                m_tvString.setGravity(Gravity.CENTER);
                m_tvString.setText(info._string + "(" + info._good_cnt + ")");
            } else {
                m_imvLogo.setVisibility(View.GONE);
                m_imvTemp.setVisibility(View.GONE);

                m_tvString.setText(info._string);
            }
            if (info.isCheck) {
//                m_imvCheck.setSelected(true);
                m_llyRow.setBackgroundColor(Color.parseColor("#ffe7e7e7"));
            } else {
//                m_imvCheck.setSelected(false);
                m_llyRow.setBackgroundColor(Color.parseColor("#ffffffff"));
            }
            m_imvCheck.setVisibility(View.GONE);
        }
    }
}