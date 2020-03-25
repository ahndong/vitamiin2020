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

import java.io.Serializable;
import java.util.ArrayList;

import app.vitamiin.com.ConfirmDialog;
import app.vitamiin.com.Model.CommentInfo;
import app.vitamiin.com.R;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.home.DetailEventActivity;
import app.vitamiin.com.home.DetailExpActivity;
import app.vitamiin.com.home.DetailReviewActivity;
import app.vitamiin.com.home.DetailPowerActivity;
import app.vitamiin.com.http.Net;

/**
 * Created by Peter on 2015-08-20.
 */
public class CommentListAdapter extends ArrayAdapter<CommentInfo> {

    private Context m_context;
    private int m_type;
    private View m_convertView;
    private ArrayList<Integer> m_arrH = new ArrayList<>();

    public CommentListAdapter(Context context, ArrayList<CommentInfo> arrayItem, int type) {
        super(context, 0, arrayItem);
        m_context = context;
        m_type = type;
    }

    public void setAllHeight(int index, int toAddH){
        while(m_arrH.size()<index+1)
            m_arrH.add(toAddH);

        m_arrH.set(index,toAddH);
    }

    public int getTotalHeight(){
        int totalHeight = 0;
        for (int one: m_arrH)
            totalHeight += one;
        return totalHeight;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder itemHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row_comment, parent, false);
            itemHolder = new ItemHolder(convertView);
            convertView.setTag(itemHolder);
            m_convertView = itemHolder.showInfo(position);
        } else {
            itemHolder = (ItemHolder) convertView.getTag();
            m_convertView = itemHolder.showInfo(position);
//            setAllHeight(position, m_convertView.getMeasuredHeight());
        }
//        itemHolder.m_tvNick.requestLayout();
//        itemHolder.m_tvRegdate.requestLayout();
//        itemHolder.m_tvContent.requestLayout();
        m_convertView.requestLayout();

        return convertView;
    }

    public class ItemHolder {
        LinearLayout m_llyRoot;
        ImageView m_imvPhoto, m_imvDelete;
        TextView m_tvNick, m_tvRegdate, m_tvContent;

        public ItemHolder(View v) {
            m_convertView = v;
            m_llyRoot = (LinearLayout) m_convertView.findViewById(R.id.lly_row);
            m_imvPhoto = (ImageView) m_convertView.findViewById(R.id.imv_good);
            m_tvNick = (TextView) m_convertView.findViewById(R.id.tv_nick);
            m_tvRegdate = (TextView) m_convertView.findViewById(R.id.tv_regdate);
            m_tvContent = (TextView) m_convertView.findViewById(R.id.tv_content);
            m_imvDelete = (ImageView) m_convertView.findViewById(R.id.imv_delete);
        }

        public View showInfo(final int position) {
            final CommentInfo info = getItem(position);
            if (info.f_photo.equals(""))
                Glide.with(m_context).load(R.drawable.ic_female_3).into(m_imvPhoto);
            else
                Glide.with(m_context).load(Net.URL_SERVER1 + info.f_photo).into(m_imvPhoto);

            m_tvContent.requestLayout();
            m_tvNick.requestLayout();
            m_tvRegdate.requestLayout();

            m_tvContent.setText(info.content);
            m_tvNick.setText(info._mb_nick);
            m_tvRegdate.setText(info.regdate);

            m_llyRoot.requestLayout();

//            if (info._mb_id.equals(UserManager.getInstance().member_id))
                m_imvDelete.setVisibility(View.VISIBLE);
//            else
//                m_imvDelete.setVisibility(View.GONE);

            m_imvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ConfirmDialog(m_context, "댓글 삭제", info._mb_nick + "님, 댓글을 삭제 하시겠습니까?", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (m_type == 1) {
                                ((DetailReviewActivity) m_context).deleteComment(info, v);
                            } else if (m_type == 2) {
                                ((DetailPowerActivity) m_context).deleteComment(info, v);
                            } else if (m_type == 3) {
                                ((DetailExpActivity) m_context).deleteComment(info, v);
                            } else if (m_type == 4) {
                                ((DetailEventActivity) m_context).deleteComment(info, v);
                            }
                        }
                    }).show();
                }
            });
            return m_convertView;
        }
    }
}