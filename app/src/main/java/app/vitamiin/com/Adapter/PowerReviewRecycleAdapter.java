package app.vitamiin.com.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import app.vitamiin.com.Model.WikiInfo;
import app.vitamiin.com.R;
import app.vitamiin.com.setting.LikeActivity;
import app.vitamiin.com.home.MainActivity;
import app.vitamiin.com.http.Net;

public class PowerReviewRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //added view types
    private static final int TYPE_HEADER = 2;
    private static final int TYPE_ITEM = 1;

    private Context m_context;

    private int fromAcitivityIndex = 0;
    private ArrayList<WikiInfo> mItemList;
    private int mkind = -1;

    public PowerReviewRecycleAdapter(Context context, ArrayList<WikiInfo> arrayItem) {
        m_context = context;
        mItemList = arrayItem;
    }

    public PowerReviewRecycleAdapter(Context context, ArrayList<WikiInfo> arrayItem, int kind) {
        m_context = context;
        mItemList = arrayItem;
        mkind = kind;
    }

    private static class RecyclerItemViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout m_llyRow;
        private ImageView m_imvPhoto;
        private TextView m_tvName, m_tvViewCnt, m_tvCommentCnt, m_tvLikeCnt;

        private RecyclerItemViewHolder(final View v) {
            super(v);

            m_llyRow = (LinearLayout) v.findViewById(R.id.lly_row);
            m_imvPhoto = (ImageView) v.findViewById(R.id.imv_good);

            m_tvName = (TextView) v.findViewById(R.id.tv_name);
            m_tvViewCnt = (TextView) v.findViewById(R.id.tv_view_cnt);
            m_tvCommentCnt = (TextView) v.findViewById(R.id.tv_review_cnt);
            m_tvLikeCnt = (TextView) v.findViewById(R.id.tv_like_cnt);
        }

        private static RecyclerItemViewHolder newInstance(View parent) {
            return new RecyclerItemViewHolder(parent);
        }
    }

    private class RecyclerHeaderViewHolder extends RecyclerView.ViewHolder {
        private RecyclerHeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    //modified creating viewholder, so it creates appropriate holder for a given viewType
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if(!parent.getContext().getClass().toString().contains("LikeActivity")){
            fromAcitivityIndex = 1;        }
        else{
            fromAcitivityIndex = 2;        }
        if (viewType == TYPE_ITEM) {
            final View view1 = LayoutInflater.from(context).inflate(R.layout.list_row_event, parent, false);
            final View view2 = LayoutInflater.from(context).inflate(R.layout.list_row_wiki, parent, false);
            if(mkind==1){
                return RecyclerItemViewHolder.newInstance(view1);
            } else {
                return RecyclerItemViewHolder.newInstance(view2);
            }
        } else if (viewType == TYPE_HEADER) {
            final View view = LayoutInflater.from(context).inflate(R.layout.item_layout_empty_header, parent, false);
            return new RecyclerHeaderViewHolder(view);
        }
        throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types    correctly");
    }

    //modifed ViewHolder binding so it binds a correct View for the Adapter
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (!isPositionHeader(position)) {
            RecyclerItemViewHolder holder = (RecyclerItemViewHolder) viewHolder;
            final WikiInfo info = mItemList.get(position - 1);

            holder.m_llyRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mkind==1){
                        ((MainActivity) m_context).gotoDetailEvent(position - 1);
                    }
                    else if(fromAcitivityIndex==1){
                        ((MainActivity) m_context).gotoDetailFromWikiTab(position - 1);
                    }
//                    else if(fromAcitivityIndex==2){
//                        ((LikeActivity) m_context).gotoDetailFromLikeActivity(position - 1);
//                    }
                }
            });

            Glide.with(m_context).load(Net.URL_SERVER1 + info._imagePath).into(holder.m_imvPhoto);
            holder.m_tvName.setText(info.title);
            holder.m_tvViewCnt.setText(String.valueOf(info._view_cnt));
            holder.m_tvCommentCnt.setText(String.valueOf(info._comment_cnt));
            holder.m_tvLikeCnt.setText(String.valueOf(info._like_cnt));
        }
    }
    //our old getItemCount()
    private int getBasicItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }
    //our new getItemCount() that includes header View
    @Override
    public int getItemCount() {
        return getBasicItemCount() + 1; // header
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