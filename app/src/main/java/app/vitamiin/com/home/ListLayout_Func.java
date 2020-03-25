package app.vitamiin.com.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.vitamiin.com.R;

/**
 * Created by dong8 on 2017-04-18.
 */

public class ListLayout_Func extends LinearLayout {
    private TextView m_tvName, m_tvDescription;

    public ListLayout_Func(Context context, int index, OnClickListener listener){
        super(context);

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.setTag(index);

        View view = inflater.inflate(R.layout.list_row_func, this, true);
//        initViews_funclist(view, listener);
        initViews_funclist(view, listener);
    }

    private void initViews_funclist(View v, final OnClickListener listener) {
        m_tvName = (TextView) v.findViewById(R.id.tv_name);
        m_tvName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setTag(ListLayout_Func.this.getTag());
                listener.onClick(view);
            }
        });
        m_tvDescription = (TextView) v.findViewById(R.id.tv_desc);
    }

    public void setFuncContent(String[] oneFunc) {
        int i = (Integer)this.getTag();
        m_tvName.setText(oneFunc[0]);
        m_tvDescription.setText(oneFunc[1]);
    }
}
