package app.vitamiin.com.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import app.vitamiin.com.Model.ReviewInfo;
import app.vitamiin.com.R;
import app.vitamiin.com.setting.MyPageActivity;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.common.Util;
import app.vitamiin.com.home.MainActivity;
import app.vitamiin.com.http.Net;

public class ReviewExperRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //added view types
    private static final int TYPE_HEADER = 2;
    private static final int TYPE_ITEM = 1;

    private Context m_context;
    private int resArr[] = {R.drawable.ic_male_1, R.drawable.ic_male_2, R.drawable.ic_male_3, R.drawable.ic_male_4,
            R.drawable.ic_female_1, R.drawable.ic_female_2, R.drawable.ic_female_3, R.drawable.ic_female_4};
    private int backColors[] = {Color.parseColor("#debee8"),Color.parseColor("#ffe1bf"),Color.parseColor("#a6bd84"),Color.parseColor("#f9ffb3"),Color.parseColor("#80d2ff"),Color.parseColor("#82ffb4")};
    private int backCol_index = 0;
    private ArrayList<ReviewInfo> mItemList;

    private int fromAcitivityIndex = 0;
    private View.OnClickListener m_listener;


    public ReviewExperRecycleAdapter(Context context, ArrayList<ReviewInfo> arrayItem) {
        m_context = context;
        mItemList = arrayItem;
    }

    public ReviewExperRecycleAdapter(Context context, ArrayList<ReviewInfo> arrayItem, View.OnClickListener listener) {
        m_context = context;
        mItemList = arrayItem;
        m_listener = listener;
    }

    private static class RecyclerItemViewHolder extends RecyclerView.ViewHolder {
        LinearLayout m_llyRow;
        ImageView m_imvPhoto, m_imvProfile;
        TextView m_tvNick, m_tvRegdate, m_tvContent, m_tvTitle, m_tvViewCnt, m_tvCommentCnt, m_tvLikeCnt, m_tvRepresent;

        private RecyclerItemViewHolder(final View v) {
            super(v);
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
        private static RecyclerItemViewHolder newInstance(View parent) {
            return new RecyclerItemViewHolder(parent);
        }
    }

    private static class RecyclerHeaderViewHolder extends RecyclerView.ViewHolder {
        private ImageView m_imv_back, m_imv_edit, m_imv_family, m_imv_my, mImvProfile;
        private TextView m_tvSort1, m_tvSort2, m_tvFallow, m_tvFallowing, m_tvLike, m_tv_nickname;
        private TextView m_tvFallowUser;
        private LinearLayout m_lly_fallow, m_lly_fallowing, m_lly_like, m_imv_dummy;
        private RecyclerHeaderViewHolder(final View v) {
            super(v);
            m_imv_back = (ImageView) v.findViewById(R.id.imv_back);
            m_tvFallowing = (TextView) v.findViewById(R.id.tv_fallowing_cnt);
            m_tvFallow = (TextView) v.findViewById(R.id.tv_fallow_cnt);
            m_tvLike = (TextView) v.findViewById(R.id.tv_like_cnt);
            m_tvSort1 = (TextView) v.findViewById(R.id.tv_review);
            m_tvSort2 = (TextView) v.findViewById(R.id.tv_exper);
            m_tvFallowUser = (TextView) v.findViewById(R.id.tv_fallow_user);
            m_tv_nickname = (TextView) v.findViewById(R.id.tv_nickname);
            mImvProfile = (ImageView) v.findViewById(R.id.imv_profile);
            m_imv_dummy = (LinearLayout) v.findViewById(R.id.imv_dummy);
            m_imv_family = (ImageView) v.findViewById(R.id.imv_family);
            m_imv_my = (ImageView) v.findViewById(R.id.imv_my);
            m_imv_edit = (ImageView) v.findViewById(R.id.imv_edit);
            m_lly_fallowing = (LinearLayout) v.findViewById(R.id.lly_fallowing);
            m_lly_fallow = (LinearLayout) v.findViewById(R.id.lly_fallow);
            m_lly_like = (LinearLayout) v.findViewById(R.id.lly_like);
        }
        private static RecyclerHeaderViewHolder newInstance(View parent) {
            return new RecyclerHeaderViewHolder(parent);
        }
    }
    //modified creating viewholder, so it creates appropriate holder for a given viewType
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if (viewType == TYPE_ITEM) {
            final View view = LayoutInflater.from(context).inflate(R.layout.list_row_review_exp, parent, false);
            return RecyclerItemViewHolder.newInstance(view);
        } else if (viewType == TYPE_HEADER) {
            if(!parent.getContext().getClass().toString().split("M")[1].equals("yPageActivity")){
                fromAcitivityIndex = 1;
                final View view = LayoutInflater.from(context).inflate(R.layout.item_layout_empty_header, parent, false);
                return RecyclerHeaderViewHolder.newInstance(view);
            }else{
                fromAcitivityIndex = 2;
                final View view = LayoutInflater.from(context).inflate(R.layout.item_layout_header_mypage, parent, false);
                return RecyclerHeaderViewHolder.newInstance(view);
            }
        }
        throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types    correctly");
    }

    //modifed ViewHolder binding so it binds a correct View for the Adapter
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (!isPositionHeader(position)) {
            RecyclerItemViewHolder holder = (RecyclerItemViewHolder) viewHolder;
            final ReviewInfo info = mItemList.get(position);

            holder.m_llyRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(fromAcitivityIndex==1){
                        ((MainActivity) m_context).gotoReviewtabDetail(position);
                    }else if(fromAcitivityIndex==2){
                        ((MyPageActivity) m_context).gotoMyPageActivityDetail(position);
                    }
                }
            });

            if (info.photos.size() > 0 && info.photos.get(0).length() > 0) {
                holder.m_imvPhoto.setVisibility(View.VISIBLE);
                holder.m_tvRepresent.setVisibility(View.GONE);
                if(info.photos_id.get(0).equals("NA"))
                    Glide.with(m_context).load(new File(info.photos.get(0)).getPath()).into(holder.m_imvPhoto);
                else
                    Glide.with(m_context).load(Net.URL_SERVER1 + info.photos.get(0)).into(holder.m_imvPhoto);
            } else {
                if(info._good_photo_urls.size() > 0 && info._good_photo_urls.get(0).length() > 0) {
                    holder.m_imvPhoto.setVisibility(View.VISIBLE);
                    holder.m_tvRepresent.setVisibility(View.GONE);
                    Glide.with(m_context).load(Net.URL_SERVER1 + info._good_photo_urls.get(0)).into(holder.m_imvPhoto);
                } else {
                    if(info.knowhow.size() > 0 && info.knowhow.get(0).length() > 0) {
                        holder.m_imvPhoto.setVisibility(View.GONE);
                        holder.m_tvRepresent.setVisibility(View.VISIBLE);
                        backCol_index = backCol_index == 5 ? 0 : backCol_index+1;
                        holder.m_tvRepresent.setBackgroundColor(backColors[backCol_index]);
                        holder.m_tvRepresent.setBackgroundColor(backColors[backCol_index]);
                        holder.m_tvRepresent.setText(info.knowhow.get(0));
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
                    Glide.with(m_context).load(resArr[resID]).into(holder.m_imvProfile);
                } else {
                    Glide.with(m_context).load(Net.URL_SERVER1 + info.f_photo).into(holder.m_imvProfile);
                }
            }else{
                Glide.with(m_context).load(R.drawable.ic_female_3).into(holder.m_imvProfile);
            }

            holder.m_tvNick.setText(info._mb_nick);
            holder.m_tvRegdate.setText(info.regdate);
            holder.m_tvContent.setText(info.content);
            holder.m_tvTitle.setText(info.title);

            holder.m_tvViewCnt.setText(String.valueOf(info.view_cnt));
            holder.m_tvCommentCnt.setText(String.valueOf(info.comment_cnt));
            holder.m_tvLikeCnt.setText(String.valueOf(info.like_cnt));
        }else{
            if(fromAcitivityIndex==2){
                RecyclerHeaderViewHolder holder = (RecyclerHeaderViewHolder) viewHolder;
                holder.m_imv_back.setOnClickListener(m_listener);
                holder.m_lly_fallow.setOnClickListener(m_listener);
                holder.m_lly_fallowing.setOnClickListener(m_listener);
                holder.m_lly_like.setOnClickListener(m_listener);
                holder.mImvProfile.setOnClickListener(m_listener);
                holder.m_imv_dummy.setOnClickListener(m_listener);
                holder.m_tvSort1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(fromAcitivityIndex==2)
                            ((MyPageActivity) m_context).switchReviewTab();
                    }
                });
                holder.m_tvSort2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(fromAcitivityIndex==2)
                            ((MyPageActivity) m_context).switchExpTab();
                    }
                });
                holder.m_tvSort1.setSelected(false);
                holder.m_tvSort2.setSelected(true);

                final ReviewInfo info = mItemList.get(0);

                if(info.price==1){  //headerinfo 가 세팅이 되어 있는지? 확인해서 아니면 그냥 pass
                    holder.m_tvFallowing.setText(String.valueOf(info.comment_cnt));
                    holder.m_tvFallow.setText(String.valueOf(info.like_cnt));
                    holder.m_tvLike.setText(String.valueOf(info.view_cnt));
                    holder.m_tvSort1.setText("리뷰 (" + info.period + ")");
                    holder.m_tvSort2.setText("노하우 공유 (" + info.person + ")");
                    holder.m_tv_nickname.setText(info._mb_nick);

                    if(info._mb_sex==1){    //본인인 경우
                        holder.m_imv_family.setVisibility(View.VISIBLE);
                        holder.m_imv_family.setOnClickListener(m_listener);
                        holder.m_imv_my.setVisibility(View.VISIBLE);
                        holder.m_imv_my.setOnClickListener(m_listener);
                        holder.m_imv_edit.setVisibility(View.VISIBLE);
                        holder.m_imv_edit.setOnClickListener(m_listener);
                        holder.m_tvFallowUser.setVisibility(View.GONE);

                        if(UserManager.getInstance().arr_profile_photo_resID.get(0)!=-1){
                            Glide.with(m_context).load(resArr[UserManager.getInstance().arr_profile_photo_resID.get(0)]).into(holder.mImvProfile);
                        }else{
                            if(UserManager.getInstance().arr_profile_photo_bitmap.get(0)!=null){
                                Glide.with(m_context).load(UserManager.getInstance().arr_profile_photo_bitmap.get(0)).into(holder.mImvProfile);
                            } else if(UserManager.getInstance().arr_profile_photo_file.get(0)!=null){
                                Glide.with(m_context).load(UserManager.getInstance().arr_profile_photo_file.get(0).getPath()).into(holder.mImvProfile);
                            } else {
                                UserManager.getInstance().arr_profile_photo_resID.set(0,new Util().getAutoImageResID(0));
                                Glide.with(m_context).load(resArr[UserManager.getInstance().arr_profile_photo_resID.get(0)]).into(holder.mImvProfile);
                            }
                        }
                    }else if(info._mb_sex==0){                  //본인이 아닌 경우
                        holder.m_tvFallowUser.setSelected(info.fallow_user==1);
                        holder.m_imv_family.setVisibility(View.GONE);
                        holder.m_imv_my.setVisibility(View.GONE);
                        holder.m_imv_edit.setVisibility(View.GONE);
                        holder.m_tvFallowUser.setVisibility(View.VISIBLE);
                        holder.m_tvFallowUser.setOnClickListener(m_listener);

                        if(info._good_photo_urls==null || info._good_photo_urls.get(0).length()<3)
                            Glide.with(m_context).load(resArr[0]).into(holder.mImvProfile);
                        else
                            Glide.with(m_context).load(Net.URL_SERVER1 + info._good_photo_urls.get(0)).into(holder.mImvProfile);
                    }
                }
            }
        }
    }
    //our old getItemCount()
    private int getBasicItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }
    //our new getItemCount() that includes header View
    @Override
    public int getItemCount() {
        return getBasicItemCount(); // header
    }
    //added a method that returns viewType for a given position
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    //added a method to check if given position is a header
    private boolean isPositionHeader(int position) {
        return position == 0;
    }
}