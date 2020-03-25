package app.vitamiin.com.home;

import android.content.Context;
import android.graphics.Bitmap;
//import android.support.v7.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import app.vitamiin.com.Model.GoodInfo;
import app.vitamiin.com.R;
import app.vitamiin.com.http.Net;

/**
 * Created by Peter on 2015-08-20.
 */
public class ProductRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //added view types
    private static final int TYPE_HEADER = 2;
    private static final int TYPE_ITEM = 1;

    private Context m_context;
    private boolean m_canSelect;

    private ArrayList<GoodInfo> mItemList;

    public ProductRecycleAdapter(Context context, ArrayList<GoodInfo> arrayItem, boolean canSelect) {
        m_context = context;
        mItemList = arrayItem;
        m_canSelect = canSelect;
    }

    public static class RecyclerItemViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout m_llyRow;
        public RelativeLayout m_rlyBadge;
        public ImageView m_imvPhoto, m_imvCheck;
        private ImageView[] arr_mimvStars = new ImageView[5];
        public TextView m_tvGood, m_tvBusiness, m_tvRate, m_tvCommentCnt, m_tvReviewCnt, m_tvLikeCnt, m_tvContent, m_tvNick;

        public RecyclerItemViewHolder(final View v) {
            super(v);

            m_rlyBadge = (RelativeLayout)v.findViewById(R.id.rly_badge);
            m_llyRow = (LinearLayout) v.findViewById(R.id.rly_row);
            m_imvPhoto = (ImageView) v.findViewById(R.id.imv_good);

            arr_mimvStars[0] = (ImageView) v.findViewById(R.id.imv_star1);
            arr_mimvStars[1] = (ImageView) v.findViewById(R.id.imv_star2);
            arr_mimvStars[2] = (ImageView) v.findViewById(R.id.imv_star3);
            arr_mimvStars[3] = (ImageView) v.findViewById(R.id.imv_star4);
            arr_mimvStars[4] = (ImageView) v.findViewById(R.id.imv_star5);

            m_tvBusiness = (TextView) v.findViewById(R.id.tv_business);
            m_tvGood = (TextView) v.findViewById(R.id.tv_title);
            m_tvRate = (TextView) v.findViewById(R.id.tv_rate);
            m_tvCommentCnt = (TextView) v.findViewById(R.id.tv_comment_cnt);
            m_tvReviewCnt = (TextView) v.findViewById(R.id.tv_review_cnt);
            m_tvLikeCnt = (TextView) v.findViewById(R.id.tv_like_cnt);
            m_imvCheck = (ImageView) v.findViewById(R.id.imv_check);
        }

        public static ProductRecycleAdapter.RecyclerItemViewHolder newInstance(View parent) {
            return new ProductRecycleAdapter.RecyclerItemViewHolder(parent);
        }
    }

    public class RecyclerHeaderViewHolder extends RecyclerView.ViewHolder {
        public RecyclerHeaderViewHolder(View itemView) {
            super(itemView);
        }
    }


    //modified creating viewholder, so it creates appropriate holder for a given viewType
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if (viewType == TYPE_ITEM) {
            final View view = LayoutInflater.from(context).inflate(R.layout.list_row_good, parent, false);
            return ProductRecycleAdapter.RecyclerItemViewHolder.newInstance(view);
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
            ProductRecycleAdapter.RecyclerItemViewHolder holder = (ProductRecycleAdapter.RecyclerItemViewHolder) viewHolder;

            final GoodInfo info = mItemList.get(position - 1);

            holder.m_rlyBadge.setVisibility(View.GONE);
            holder.m_llyRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((SearchInitActivity) m_context).gotoProductDetail(position - 1);
                }
            });

            if (info.isCheck) {
            } else {
                holder.m_imvCheck.setSelected(false);
            }

            if (m_canSelect){
                holder.m_imvCheck.setVisibility(View.VISIBLE);
                holder.m_imvCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public  void onClick(View v) {
                        if (info.isCheck){
                            v.setSelected(false);
                            info.isCheck = false;
                        }else {
                            int cnt = 0;
                            for (int i = 0; i < mItemList.size(); i++) {
                                if (mItemList.get(i).isCheck)
                                    cnt++;
                            }
                            if (cnt == 2) return;
                            v.setSelected(true);
                            info.isCheck = true;
                        }
                    }
                });
            }
            else{
                holder.m_imvCheck.setVisibility(View.GONE);
            }

            if (info._imagePath.length() == 0)
                Glide.with(m_context).load(R.drawable.default_review).into(holder.m_imvPhoto);
            else
                Glide.with(m_context).load(Net.URL_SERVER1 + info._imagePath).into(holder.m_imvPhoto);

            for(int i = 0; i < 5; i++)
                if(info._rate < i + 0.5)
                    Glide.with(m_context).load(R.drawable.ic_star_empty).into(holder.arr_mimvStars[i]);
                else if(info._rate < i+1)
                    Glide.with(m_context).load(R.drawable.ic_star_half).into(holder.arr_mimvStars[i]);
                else
                    Glide.with(m_context).load(R.drawable.ic_star_one).into(holder.arr_mimvStars[i]);

            holder.m_tvGood.setText(info._name);
            holder.m_tvBusiness.setText(info._business);
            holder.m_tvRate.setText("총 평점 " + info._rate);
            holder.m_tvReviewCnt.setText("" + info._review_cnt);
            holder.m_tvLikeCnt.setText("" + info._like_cnt);
        }
    }

    //our old getItemCount()
    public int getBasicItemCount() {
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