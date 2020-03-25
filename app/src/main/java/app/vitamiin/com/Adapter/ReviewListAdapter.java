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

import app.vitamiin.com.Model.ReviewInfo;
import app.vitamiin.com.R;
import app.vitamiin.com.http.Net;

/**
 * Created by Peter on 2015-08-20.
 */
public class ReviewListAdapter extends ArrayAdapter<ReviewInfo> {

    private Context m_context;
    private Boolean m_isVisibleGoodInfo;
    private int resArr[] = {R.drawable.ic_male_1, R.drawable.ic_male_2, R.drawable.ic_male_3, R.drawable.ic_male_4,
            R.drawable.ic_female_1, R.drawable.ic_female_2, R.drawable.ic_female_3, R.drawable.ic_female_4};


    public ReviewListAdapter(Context context, ArrayList<ReviewInfo> arrayItem, Boolean visibleGoodInfo) {
        super(context, 0, arrayItem);
        m_context = context;
        m_isVisibleGoodInfo = visibleGoodInfo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder itemHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row_review,
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
        LinearLayout m_llyRow, m_llyGoodInfo;
        ImageView m_imvPhoto, m_imvProfile;
        private ImageView[] arr_mimvStars = new ImageView[5];
        TextView m_tvGood, m_tvBusiness, m_tvRate, m_tvViewCnt, m_tvCommentCnt, m_tvLikeCnt, m_tvContent, m_tvNick, m_tvAge, m_tvSex, m_tvRegdate, m_tvPregnant, m_tvAage, m_tvAsex, m_tvAtype;

        public ItemHolder(View v) {
            m_llyRow = (LinearLayout) v.findViewById(R.id.rly_row);
            if(m_isVisibleGoodInfo)
                m_llyGoodInfo = (LinearLayout) v.findViewById(R.id.lly_good_info);
            else
                v.findViewById(R.id.lly_good_info).setVisibility(View.GONE);
            m_imvProfile = (ImageView) v.findViewById(R.id.imv_profile);
            m_imvPhoto = (ImageView) v.findViewById(R.id.imv_good);

            arr_mimvStars[0] = (ImageView) v.findViewById(R.id.imv_star1);
            arr_mimvStars[1] = (ImageView) v.findViewById(R.id.imv_star2);
            arr_mimvStars[2] = (ImageView) v.findViewById(R.id.imv_star3);
            arr_mimvStars[3] = (ImageView) v.findViewById(R.id.imv_star4);
            arr_mimvStars[4] = (ImageView) v.findViewById(R.id.imv_star5);

            m_tvGood = (TextView) v.findViewById(R.id.tv_name);
            m_tvBusiness = (TextView) v.findViewById(R.id.tv_business);
            m_tvRate = (TextView) v.findViewById(R.id.tv_rate);
            m_tvViewCnt = (TextView) v.findViewById(R.id.tv_view_cnt);
            m_tvCommentCnt = (TextView) v.findViewById(R.id.tv_comment_cnt);
            m_tvLikeCnt = (TextView) v.findViewById(R.id.tv_like_cnt);
            m_tvContent = (TextView) v.findViewById(R.id.tv_content);
            m_tvNick = (TextView) v.findViewById(R.id.tv_nick);
            m_tvRegdate = (TextView)v.findViewById(R.id.tv_regdate);

            m_tvSex = (TextView) v.findViewById(R.id.tv_sex);
            m_tvAge = (TextView) v.findViewById(R.id.tv_age);
            m_tvPregnant = (TextView) v.findViewById(R.id.tv_pregnant);
            m_tvAage = (TextView) v.findViewById(R.id.tv_author_age);
            m_tvAsex = (TextView) v.findViewById(R.id.tv_author_sex);
            m_tvAtype = (TextView) v.findViewById(R.id.tv_author_type);
        }

        public void showInfo(final int position) {
            final ReviewInfo info = getItem(position);

//            m_llyRow.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ((MainActivity) m_context).gotoReviewtabDetail(position);
//                }
//            });

            if(m_isVisibleGoodInfo){
                if (info.good_photo.length() == 0)
                    Glide.with(m_context).load(R.drawable.default_review).into(m_imvPhoto);
                else
                    Glide.with(m_context).load(Net.URL_SERVER1 + info.good_photo).into(m_imvPhoto);
                m_tvGood.setText(info.title);
                m_tvBusiness.setText(info.business);
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

            for(int i = 0; i < 5; i++)
                if(info.rate < i + 0.5)
                    Glide.with(m_context).load(R.drawable.ic_star_empty).into(arr_mimvStars[i]);
                else if(info.rate < i+1)
                    Glide.with(m_context).load(R.drawable.ic_star_half).into(arr_mimvStars[i]);
                else
                    Glide.with(m_context).load(R.drawable.ic_star_one).into(arr_mimvStars[i]);

            m_tvRate.setText("총 평점 " + info.rate);
            m_tvViewCnt.setText("" + info.view_cnt);
            m_tvCommentCnt.setText("" + info.comment_cnt);
            m_tvLikeCnt.setText("" + info.like_cnt);
            m_tvContent.setText(info.content);
            m_tvNick.setText(info._mb_nick);
            m_tvRegdate.setText(info.regdate);

            String strPerson="";
            int m_nPerson = info.person;

            if(m_nPerson<100){
                m_tvPregnant.setVisibility(View.GONE);
                m_tvSex.setVisibility(View.VISIBLE);
                m_tvAge.setVisibility(View.VISIBLE);
                if(m_nPerson>=10){
                    m_tvSex.setText("여성");
                    m_nPerson= m_nPerson - 10;
                }else{
                    m_tvSex.setText("남성");
                }
                switch (m_nPerson){
                    case 0:
                        m_tvAge.setText("영유아");
                        break;
                    case 1:
                        m_tvAge.setText("어린이");
                        break;
                    case 2:
                        m_tvAge.setText("청소년");
                        break;
                    case 3:
                        m_tvAge.setText("성인");
                        break;
                    case 4:
                        m_tvAge.setText("노인");
                        break;
                }
            }else{
                m_tvPregnant.setVisibility(View.VISIBLE);
                m_tvSex.setVisibility(View.GONE);
                m_tvAge.setVisibility(View.GONE);
                switch (m_nPerson){
                    case 100:
                        m_tvPregnant.setText("수험생");
                        break;
                    case 200:
                        m_tvPregnant.setText("임산부");
                        break;
                    case 300:
                        m_tvPregnant.setText("수유부");
                        break;
                    case 400:
                        m_tvPregnant.setText("갱년기");
                        break;
                }
            }
            int Nof10s = Math.round(info._mb_age/10)*10;
            String strAge = Nof10s==0? "어린이":Nof10s+ "대";
            m_tvAage.setText(strAge);
            m_tvAsex.setText(info._mb_sex==0 ? "남성" : "여성");
            if(info.Author_examinee==0)
                m_tvAtype.setText("수험생");
            else if(info.Author_pregnant==0)
                m_tvAtype.setText("임산부");
            else if(info.Author_lactating==0)
                m_tvAtype.setText("수유부");
            else if(info.Author_climacterium==0)
                m_tvAtype.setText("갱년기");
            else
                m_tvAtype.setVisibility(View.GONE);
        }
    }
}