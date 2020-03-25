package app.vitamiin.com.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import app.vitamiin.com.Model.FoodInfoDeux;
import app.vitamiin.com.R;
import app.vitamiin.com.http.Net;

public class FoodListAdapter extends ArrayAdapter<FoodInfoDeux> {
    private Context m_context;

    public FoodListAdapter(Context context, ArrayList<FoodInfoDeux> arrayItem) {
        super(context, 0, arrayItem);
        m_context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder itemHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row_food,
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
        LinearLayout m_llyRoot;
        ImageView m_imvPhoto,m_imvPhoto2;
        TextView m_tvName,m_tvName2, m_tvViewCnt, m_tvViewCnt2, m_tvLikeCnt, m_tvLikeCnt2;
        RelativeLayout m_rlyONE, m_rlyTWO;
//m_rlyONE.setHeight(m_rlyONE.getMeasuredWidth);
        public ItemHolder(View v) {
            m_llyRoot = (LinearLayout) v.findViewById(R.id.lly_root);
            m_imvPhoto = (ImageView) v.findViewById(R.id.imv_good);
            m_imvPhoto2 = (ImageView) v.findViewById(R.id.imv_photo2);
            m_tvName = (TextView) v.findViewById(R.id.tv_name);
            m_tvName2 = (TextView) v.findViewById(R.id.tv_name2);
            m_tvViewCnt = (TextView) v.findViewById(R.id.tv_view_cnt);
            m_tvViewCnt2 = (TextView) v.findViewById(R.id.tv_view_cnt2);
            m_tvLikeCnt = (TextView) v.findViewById(R.id.tv_like_cnt);
            m_tvLikeCnt2 = (TextView) v.findViewById(R.id.tv_like_cnt2);
            m_rlyONE = (RelativeLayout) v.findViewById(R.id.rly_ONE);
            m_rlyTWO = (RelativeLayout) v.findViewById(R.id.rly_TWO);
//            ViewTreeObserver vto = m_rlyONE.getViewTreeObserver();
//            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    m_rlyONE.set(m_rlyONE.getMeasuredWidth());
//                }
//            });
//            ViewTreeObserver vto2 = m_rlyTWO.getViewTreeObserver();
//            vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    m_rlyTWO.setMinimumHeight(m_rlyTWO.getMeasuredWidth());
//                }
//            });
        }

        public void showInfo(final int position) {
            final FoodInfoDeux info = getItem(position);
            if (info._imagePath.equals(""))
                Glide.with(m_context).load(R.drawable.default_review).into(m_imvPhoto);
            else
                Glide.with(m_context).load(Net.URL_SERVER1 + info._imagePath).into(m_imvPhoto);
            m_tvName.setText(info._name);
            m_tvViewCnt.setText(String.valueOf(info._view_cnt));
            m_tvLikeCnt.setText(String.valueOf(info._like_cnt));

            if(info._imagePath2 == null) {
                m_rlyTWO.setVisibility(View.INVISIBLE);
            }else{
                if (info._imagePath2.equals(""))
                    Glide.with(m_context).load(R.drawable.default_review).into(m_imvPhoto2);
                else
                    Glide.with(m_context).load(Net.URL_SERVER1 + info._imagePath2).into(m_imvPhoto2);
                m_tvName2.setText(info._name2);
                m_tvViewCnt2.setText(String.valueOf(info._view_cnt2));
                m_tvLikeCnt2.setText(String.valueOf(info._like_cnt2));
            }

            if(m_llyRoot.getViewTreeObserver().isAlive()){
                m_llyRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout(){
                        ViewGroup.LayoutParams params1 = m_rlyONE.getLayoutParams();
//                        params1.height = ((View)m_rlyONE.getParent()).getMeasuredWidth()/2;
                        params1.height = m_rlyONE.getMeasuredWidth();
                        m_rlyONE.setLayoutParams(params1);
                        m_rlyTWO.setLayoutParams(params1);
                        m_llyRoot.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                });
            }
        }
    }
}