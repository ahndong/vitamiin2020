package app.vitamiin.com.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import app.vitamiin.com.Model.GoodInfo;
import app.vitamiin.com.R;
import app.vitamiin.com.http.Net;

/**
 * Created by dong8 on 2016-12-31.
 */

public class SelectBrandAdapter extends ArrayAdapter<GoodInfo> {
    private Context m_context;

    int m_SelectedIndex = -1;
    private int m_sort_type = 1;

    public SelectBrandAdapter(Context context, int sort_type, ArrayList<GoodInfo> arrayItem) {
        super(context, 0, arrayItem);
        m_context = context;
        m_sort_type = sort_type;
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
            convertView = inflater.inflate(R.layout.list_row_brand,
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
        ImageView m_imvPhoto;
        TextView m_tvBusiness, m_tvProductCnt;
        LinearLayout m_llyStars;

        public ItemHolder(View v) {
            m_imvPhoto = (ImageView) v.findViewById(R.id.imv_good);
            m_tvBusiness = (TextView) v.findViewById(R.id.tv_business);
            m_tvProductCnt = (TextView) v.findViewById(R.id.tv_product_cnt);
            m_llyStars = (LinearLayout) v.findViewById(R.id.lly_stars);
        }

        public void showInfo(int position) {
            GoodInfo info = getItem(position);

            if (info._imagePath.equals("")) {
                Glide.with(m_context).load(R.drawable.default_review).into(m_imvPhoto);
            }
            else {
                Glide.with(m_context).load(Net.URL_SERVER1 + info._imagePath).into(m_imvPhoto);
            }
            m_tvBusiness.setText(info._business);
            m_tvProductCnt.setText(String.valueOf(info.like_good) + "개 제품");

            if (m_sort_type == 1){
                m_llyStars.setVisibility(View.VISIBLE);
                m_imvPhoto.setVisibility(View.VISIBLE);
            }else {
                m_llyStars.setVisibility(View.GONE);
                m_imvPhoto.setVisibility(View.GONE);
                m_tvBusiness.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                m_tvProductCnt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        }
    }
}
