package app.vitamiin.com.Adapter;

import android.content.Context;
//import android.support.v4.content.ContextCompat;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import app.vitamiin.com.Model.ChartInfo;
import app.vitamiin.com.R;

/**
 * Created by Peter on 2015-08-20.
 */
public class ChartListAdapter extends ArrayAdapter<ChartInfo> {
    private Context m_context;

    public ChartListAdapter(Context context, ArrayList<ChartInfo> arrayItem) {
        super(context, 0, arrayItem);
        m_context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder itemHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row_chart,
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
        TextView m_tvFirstP, m_tvFirstC, m_tvMaterial, m_tvSecondC, m_tvSecondP;

        public ItemHolder(View v) {
            m_tvFirstP = (TextView) v.findViewById(R.id.tv_first_percentage);
            m_tvFirstC = (TextView) v.findViewById(R.id.tv_first_content);
            m_tvMaterial = (TextView) v.findViewById(R.id.tv_material);
            m_tvSecondC = (TextView) v.findViewById(R.id.tv_second_content);
            m_tvSecondP = (TextView) v.findViewById(R.id.tv_second_percentage);
        }

        public void showInfo(final int position) {
            final ChartInfo info = getItem(position);

            m_tvMaterial.setText(info._f_name);

            if(info._per_day_01.equals("x")){
                m_tvFirstC.setBackgroundColor(ContextCompat.getColor(m_context, R.color.bg_grey));
                m_tvFirstP.setBackgroundColor(ContextCompat.getColor(m_context, R.color.bg_grey));
                m_tvFirstC.setText("");
                m_tvFirstP.setText("");
            }else{
                m_tvFirstC.setBackgroundColor(ContextCompat.getColor(m_context, R.color.white_color));
                m_tvFirstP.setBackgroundColor(ContextCompat.getColor(m_context, R.color.white_color));
                if(info._per_day_01.equals("") || info._per_day_01.equals("-") || info._per_day_01.equals("0"))
                    info._per_day_01 = "0";
                if(info._per_day_01.length()==0)
                    info._ppds_01 = "-";
                m_tvFirstC.setText(info._per_day_01 + " " + info._content_unit_01);
                m_tvFirstP.setText(info._ppds_01 + " %");
            }

            if(info._per_day_02.equals("x")){
                m_tvSecondC.setBackgroundColor(ContextCompat.getColor(m_context, R.color.bg_grey));
                m_tvSecondP.setBackgroundColor(ContextCompat.getColor(m_context, R.color.bg_grey));
                m_tvSecondC.setText("");
                m_tvSecondP.setText("");
            }else{
                m_tvSecondC.setBackgroundColor(ContextCompat.getColor(m_context, R.color.white_color));
                m_tvSecondP.setBackgroundColor(ContextCompat.getColor(m_context, R.color.white_color));
                if(info._per_day_02.equals("") || info._per_day_02.length()==0 || info._per_day_02.equals("-") || info._per_day_02.equals("0"))
                    info._per_day_02 = "0";
                if(info._per_day_02.length()==0)
                    info._ppds_02 = "-";
                m_tvSecondC.setText(info._per_day_02 + " " + info._content_unit_02);
                m_tvSecondP.setText(info._ppds_02 + " %");
            }
        }
    }
}