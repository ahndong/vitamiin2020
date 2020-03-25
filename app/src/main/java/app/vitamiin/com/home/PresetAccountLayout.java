package app.vitamiin.com.home;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.vitamiin.com.R;

/**
 * Created by dong8 on 2017-07-25.
 */

public class PresetAccountLayout extends LinearLayout {
    private String m_id, m_pw, m_name, m_nick;
    private EditText m_etv_id, m_etv_pw;
    private TextView m_tv_name, m_tv_nick, m_tv_login;

    public PresetAccountLayout(Context context, int index, String id, String pw, String name, String nick, OnClickListener listener, OnFocusChangeListener listener2) {
        super(context);
        m_id = id;
        m_pw = pw;
        m_name = name;
        m_nick = nick;

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.setTag(index);

        View view = inflater.inflate(R.layout.layout_preset_account, this, true);
        initViews(view, listener, listener2);
    }

    private void initViews(View v, final OnClickListener listener, final OnFocusChangeListener listener2) {
        m_etv_id = (EditText) v.findViewById(R.id.edt_id);
        m_etv_id.setText(m_id);
        m_etv_id.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                view.setTag(PresetAccountLayout.this.getTag());
                listener2.onFocusChange(view, false);
            }
        });

        m_etv_pw = (EditText) v.findViewById(R.id.edt_pw);
        m_etv_pw.setText(m_pw);
        m_etv_pw.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                view.setTag(PresetAccountLayout.this.getTag());
                listener2.onFocusChange(view, false);
            }
        });

        m_tv_name = (TextView) v.findViewById(R.id.tv_name);
        m_tv_name.setText(m_name);
        m_tv_nick = (TextView) v.findViewById(R.id.tv_nick);
        m_tv_nick.setText(m_nick);

        m_tv_login = (TextView) v.findViewById(R.id.tv_login_preset);
        m_tv_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setTag(PresetAccountLayout.this.getTag());
                listener.onClick(view);
            }
        });
    }

    public void setID(String id) {
        m_etv_id.setText(id);
    }
    public void setPW(String pw) {
        m_etv_pw.setText(pw);
    }
}
