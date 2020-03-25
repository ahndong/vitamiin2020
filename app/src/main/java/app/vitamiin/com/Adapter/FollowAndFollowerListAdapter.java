package app.vitamiin.com.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import app.vitamiin.com.Model.FallowInfo;
import app.vitamiin.com.R;
import app.vitamiin.com.setting.FollowingAndFollowerActivity;
import app.vitamiin.com.common.CircleImageView;
import app.vitamiin.com.http.Net;

public class FollowAndFollowerListAdapter extends ArrayAdapter<FallowInfo> {

    private Context m_context;

    private int f_type; //1:fallowing, 2:fallower
    boolean isMine=false;

    public FollowAndFollowerListAdapter(Context context, ArrayList<FallowInfo> arrayItem, int type, boolean mine) {
        super(context, 0, arrayItem);
        m_context = context;
        f_type = type;
        isMine = mine;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder itemHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row_follow,
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

        CircleImageView m_imvPhoto;
        TextView m_tvNick, m_tvBtn;

        public ItemHolder(View v) {
            m_imvPhoto = (CircleImageView) v.findViewById(R.id.imv_good);
            m_tvNick = (TextView) v.findViewById(R.id.tv_nickname);
            m_tvBtn = (TextView) v.findViewById(R.id.tv_fallow);
        }

        public void showInfo(final int position) {
            final FallowInfo info = getItem(position);

            if (info.imagePath.equals(""))
                Glide.with(m_context).load(R.drawable.ic_female_3).into(m_imvPhoto);
            else
                Glide.with(m_context).load(Net.URL_SERVER1 + info.imagePath).into(m_imvPhoto);

            m_imvPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((FollowingAndFollowerActivity) m_context).gotoUserProfile(info);
                }
            });

            m_tvNick.setText(info._nick);
            m_tvNick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((FollowingAndFollowerActivity) m_context).gotoUserProfile(info);
                }
            });
            
            m_tvBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (f_type == 1)
                        ((FollowingAndFollowerActivity) m_context).disableFallowing(info);
                }
            });

            if (f_type == 1) {
                m_tvBtn.setText("팔로잉");
                if(isMine)
                    m_tvBtn.setVisibility(View.VISIBLE);
                else
                    m_tvBtn.setVisibility(View.GONE);
            } else if (f_type == 2) {
                m_tvBtn.setText("팔로워");
                m_tvBtn.setVisibility(View.GONE);
            }
        }
    }
}