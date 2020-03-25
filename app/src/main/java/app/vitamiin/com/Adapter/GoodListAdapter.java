package app.vitamiin.com.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import app.vitamiin.com.Model.GoodInfo;
import app.vitamiin.com.R;
import app.vitamiin.com.http.Net;

public class GoodListAdapter extends ArrayAdapter<GoodInfo> {
    private Context m_context;
    private Boolean _rank = false;

    public GoodListAdapter(Context context, ArrayList<GoodInfo> arrayItem, Boolean isRank) {
        super(context, 0, arrayItem);
        _rank = isRank;
        m_context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder itemHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row_good,
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
        private ImageView[] arr_mimvStars = new ImageView[5];
        private TextView m_tvBusiness, m_tvTitle, m_tvRate, m_tvViewCnt, m_tvReviewCnt, m_tvLikeCnt, m_tvBadge;
        private RelativeLayout m_rlyBadge, m_rlyCheck;

        public ItemHolder(View v) {
            m_rlyCheck = (RelativeLayout) v.findViewById(R.id.rly_check);
            m_imvPhoto = (ImageView) v.findViewById(R.id.imv_good);

            arr_mimvStars[0] = (ImageView) v.findViewById(R.id.imv_star1);
            arr_mimvStars[1] = (ImageView) v.findViewById(R.id.imv_star2);
            arr_mimvStars[2] = (ImageView) v.findViewById(R.id.imv_star3);
            arr_mimvStars[3] = (ImageView) v.findViewById(R.id.imv_star4);
            arr_mimvStars[4] = (ImageView) v.findViewById(R.id.imv_star5);

            m_tvBusiness = (TextView) v.findViewById(R.id.tv_business);
            m_tvTitle = (TextView) v.findViewById(R.id.tv_title);
            m_tvRate = (TextView) v.findViewById(R.id.tv_rate);
            m_tvViewCnt = (TextView) v.findViewById(R.id.tv_view_cnt);
            m_tvReviewCnt = (TextView) v.findViewById(R.id.tv_review_cnt);
            m_tvLikeCnt = (TextView) v.findViewById(R.id.tv_like_cnt);

            m_rlyBadge = (RelativeLayout) v.findViewById(R.id.rly_badge);
            m_tvBadge = (TextView) v.findViewById(R.id.tv_badge_num);
        }

        public void showInfo(final int position) {
            final GoodInfo info = getItem(position);

            m_rlyCheck.setVisibility(View.GONE);
            m_tvBusiness.setText(info._business);
            m_tvTitle.setText(info._name);
            m_tvRate.setText("" + info._rate);
            m_tvViewCnt.setText("" + info._view_cnt);
            m_tvReviewCnt.setText("" + info._review_cnt);
            m_tvLikeCnt.setText("" + info._like_cnt);

            m_tvBadge.setText("" + (position + 1));
            if (_rank)
                m_rlyBadge.setVisibility(View.VISIBLE);
            else
                m_rlyBadge.setVisibility(View.GONE);

            if (info._imagePath.equals(""))
                Glide.with(m_context).load(R.drawable.default_review).into(m_imvPhoto);
            else
                Glide.with(m_context).load(Net.URL_SERVER1 + info._imagePath).into(m_imvPhoto);

            for(int i = 0; i < 5; i++)
                if(info._rate < i + 0.5)
                    Glide.with(m_context).load(R.drawable.ic_star_empty).into(arr_mimvStars[i]);
                else if(info._rate < i+1)
                    Glide.with(m_context).load(R.drawable.ic_star_half).into(arr_mimvStars[i]);
                else
                    Glide.with(m_context).load(R.drawable.ic_star_one).into(arr_mimvStars[i]);
        }
    }
}