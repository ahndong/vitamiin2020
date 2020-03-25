package app.vitamiin.com.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;

import app.vitamiin.com.Model.WikiInfo;
import app.vitamiin.com.R;
import app.vitamiin.com.http.Net;

public class ExpertColumnListAdapter extends ArrayAdapter<WikiInfo> {
    private Context m_context;
    private ImageLoader m_imageLoader = ImageLoader.getInstance();

    private DisplayImageOptions optionsImage = new DisplayImageOptions.Builder()
            .cacheInMemory()
            .cacheOnDisc()
            .showImageForEmptyUri(R.drawable.default_business_logo)
            .showImageOnFail(R.drawable.default_business_logo)
            .showStubImage(R.drawable.default_business_logo)
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .build();

    public ExpertColumnListAdapter(Context context, ArrayList<WikiInfo> arrayItem) {
        super(context, 0, arrayItem);
        m_context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder itemHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row_wiki,
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
        TextView m_tvName, m_tvViewCnt, m_tvReviewCnt, m_tvLikeCnt;

        public ItemHolder(View v) {
            m_imvPhoto = (ImageView) v.findViewById(R.id.imv_good);
            m_tvName = (TextView) v.findViewById(R.id.tv_name);
            m_tvViewCnt = (TextView) v.findViewById(R.id.tv_view_cnt);
            m_tvReviewCnt = (TextView) v.findViewById(R.id.tv_review_cnt);
            m_tvLikeCnt = (TextView) v.findViewById(R.id.tv_like_cnt);
        }

        public void showInfo(final int position) {
            final WikiInfo info = getItem(position);
            if (info._imagePath.equals(""))
                Glide.with(m_context).load(R.drawable.default_review).into(m_imvPhoto);
            else
                Glide.with(m_context).load(Net.URL_SERVER1 + info._imagePath).into(m_imvPhoto);
//                m_imageLoader.displayImage(Net.URL_SERVER1 + info._imagePath, m_imvPhoto, optionsImage);

            m_tvName.setText(info.title);
            m_tvViewCnt.setText("" + info._view_cnt);
            m_tvReviewCnt.setText("" + info._comment_cnt);
            m_tvLikeCnt.setText("" + info._like_cnt);
        }
    }
}