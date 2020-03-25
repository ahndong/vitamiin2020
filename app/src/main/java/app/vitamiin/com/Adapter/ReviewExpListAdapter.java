package app.vitamiin.com.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import app.vitamiin.com.Model.ReviewInfo;
import app.vitamiin.com.R;
import app.vitamiin.com.http.Net;

public class ReviewExpListAdapter extends ArrayAdapter<ReviewInfo> {
    private Context m_context;
    private int resArr[] = {R.drawable.ic_male_1, R.drawable.ic_male_2, R.drawable.ic_male_3, R.drawable.ic_male_4,
            R.drawable.ic_female_1, R.drawable.ic_female_2, R.drawable.ic_female_3, R.drawable.ic_female_4};
    private int backColors[] = {Color.parseColor("#debee8"),Color.parseColor("#ffe1bf"),Color.parseColor("#a6bd84"),Color.parseColor("#f9ffb3"),Color.parseColor("#80d2ff"),Color.parseColor("#82ffb4")};
    private int backCol_index = 0;

    public ReviewExpListAdapter(Context context, ArrayList<ReviewInfo> arrayItem) {
        super(context, 0, arrayItem);
        m_context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder itemHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row_review_exp,
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
        private LinearLayout m_llyRow;
        private ImageView m_imvPhoto, m_imvProfile;
        private TextView m_tvNick, m_tvRegdate, m_tvContent, m_tvTitle, m_tvViewCnt, m_tvCommentCnt, m_tvLikeCnt, m_tvRepresent;

        public ItemHolder(View v) {
            m_llyRow = (LinearLayout) v.findViewById(R.id.lly_row);
            m_imvProfile = (ImageView) v.findViewById(R.id.imv_profile);
            m_imvPhoto = (ImageView) v.findViewById(R.id.imv_good);
            m_tvRepresent = (TextView) v.findViewById(R.id.tv_represent);
            m_tvNick = (TextView) v.findViewById(R.id.tv_nick);
            m_tvRegdate = (TextView) v.findViewById(R.id.tv_regdate);
            m_tvContent = (TextView) v.findViewById(R.id.tv_content);
            m_tvTitle = (TextView) v.findViewById(R.id.tv_title);
            m_tvViewCnt = (TextView) v.findViewById(R.id.tv_view_cnt);
            m_tvCommentCnt = (TextView) v.findViewById(R.id.tv_comment_cnt);
            m_tvLikeCnt = (TextView) v.findViewById(R.id.tv_like_cnt);
        }

        public void showInfo(final int position) {
            final ReviewInfo info = getItem(position);

//            m_llyRow.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ((MainActivity) m_context).gotoReviewtabDetail(position);
//                }
//            });

            if (info.photos.size() > 0 && info.photos.get(0).length() > 0) {
                m_imvPhoto.setVisibility(View.VISIBLE);
                m_tvRepresent.setVisibility(View.GONE);
                if(info.photos_id.get(0).equals("NA"))
                    Glide.with(m_context).load(new File(info.photos.get(0)).getPath()).into(m_imvPhoto);
                else
                    Glide.with(m_context).load(Net.URL_SERVER1 + info.photos.get(0)).into(m_imvPhoto);
            } else {
                if(info._good_photo_urls.size() > 0 && info._good_photo_urls.get(0).length() > 0) {
                    m_imvPhoto.setVisibility(View.VISIBLE);
                    m_tvRepresent.setVisibility(View.GONE);
                    Glide.with(m_context).load(Net.URL_SERVER1 + info._good_photo_urls.get(0)).into(m_imvPhoto);
                } else {
                    if(info.knowhow.size() > 0 && info.knowhow.get(0).length() > 0) {
                        m_imvPhoto.setVisibility(View.GONE);
                        m_tvRepresent.setVisibility(View.VISIBLE);
                        backCol_index = backCol_index == 5 ? 0 : backCol_index+1;
                        m_tvRepresent.setBackgroundColor(backColors[backCol_index]);
                        m_tvRepresent.setText(info.knowhow.get(0));
                    }
                }
            }

            int resID;
            if (info.f_photo.length() > 1){
                if(info.f_photo.contains("ic_")) {
                    String[] arr_str = info.f_photo.split("male_");
                    resID = Integer.parseInt(String.valueOf(arr_str[arr_str.length-1].charAt(0)))-1;
                    if(info.f_photo.contains("female_"))
                        resID=resID+4;
                    Glide.with(m_context).load(resArr[resID]).into(m_imvProfile);
                } else {
                    Glide.with(m_context).load(Net.URL_SERVER1 + info.f_photo).into(m_imvProfile);
                }
            }else{
                Glide.with(m_context).load(R.drawable.ic_female_3).into(m_imvProfile);
            }

            m_tvNick.setText(info._mb_nick);
            m_tvRegdate.setText(info.regdate);
            m_tvContent.setText(info.content);
            m_tvTitle.setText(info.title);

            m_tvViewCnt.setText(String.valueOf(info.view_cnt));
            m_tvCommentCnt.setText(String.valueOf(info.comment_cnt));
            m_tvLikeCnt.setText(String.valueOf(info.like_cnt));
        }
    }
}