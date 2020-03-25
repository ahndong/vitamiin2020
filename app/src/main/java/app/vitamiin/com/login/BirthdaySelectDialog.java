package app.vitamiin.com.login;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import app.vitamiin.com.R;
import app.vitamiin.com.home.ManageProfileActivity;

public class BirthdaySelectDialog extends Dialog implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Context _context;

    private ListView m_lsvYear, m_lsvMonth, m_lsvDay;
    private CalendarAdapter m_yAdapter, m_mAdapter, m_dAdapter;

    private int m_nSelectedYear = 31, m_nSelectedMonth = 0, m_nSelectedDay = 0;
    private int m_type = 0;

    public BirthdaySelectDialog(Context context, int type) {
        super(context);
        _context = context;
        m_type = type;
        // TODO Auto-generated constructor stub
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dlg_birthday);

        findViewById(R.id.tv_ok).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        m_yAdapter = new CalendarAdapter(_context, new ArrayList<String>());
        for (int i = year; i >= 1940; i--) {
            m_yAdapter.add("" + i);
        }
        m_mAdapter = new CalendarAdapter(_context, new ArrayList<String>());
        for (int i = 1; i < 13; i++) {
            if (i > 9)
                m_mAdapter.add(i + "월");
            else
                m_mAdapter.add("0" + i + "월");
        }
        m_dAdapter = new CalendarAdapter(_context, new ArrayList<String>());
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= daysInMonth; i++) {
            if (i > 9)
                m_dAdapter.add(i + "일");
            else
                m_dAdapter.add("0" + i + "일");
        }

        m_lsvYear = (ListView) findViewById(R.id.lsv_year);
        m_lsvYear.setAdapter(m_yAdapter);
        m_lsvYear.setOnItemClickListener(this);
        m_lsvMonth = (ListView) findViewById(R.id.lsv_month);
        m_lsvMonth.setAdapter(m_mAdapter);
        m_lsvMonth.setOnItemClickListener(this);
        m_lsvDay = (ListView) findViewById(R.id.lsv_day);
        m_lsvDay.setAdapter(m_dAdapter);
        m_lsvDay.setOnItemClickListener(this);

        m_yAdapter.setPos(m_nSelectedYear);
        m_mAdapter.setPos(m_nSelectedMonth);
        m_dAdapter.setPos(m_nSelectedDay);
        m_yAdapter.notifyDataSetChanged();
        m_mAdapter.notifyDataSetChanged();
        m_dAdapter.notifyDataSetChanged();

        m_lsvYear.setSelection(m_nSelectedYear - 2);
        m_lsvMonth.setSelection(m_nSelectedMonth);
        m_lsvDay.setSelection(m_nSelectedDay);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ok:
                if (m_nSelectedYear == -1 || m_nSelectedMonth == -1 || m_nSelectedDay == -1) {
                    Toast.makeText(_context, "생년월일을 선택해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (m_type == 1)
                    ((ManageProfileActivity) _context).setBirthday(m_nSelectedYear, m_nSelectedMonth, m_nSelectedDay);
                else if (m_type == 3)
                    ((FindPWActivity) _context).setBirthday1(m_nSelectedYear, m_nSelectedMonth, m_nSelectedDay);
                dismiss();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == m_lsvYear) {
            m_nSelectedYear = position;
            m_nSelectedMonth = -1;
            m_nSelectedDay = -1;
            m_yAdapter.setPos(m_nSelectedYear);
            m_mAdapter.setPos(m_nSelectedMonth);
            m_dAdapter.setPos(m_nSelectedDay);
            m_yAdapter.notifyDataSetChanged();
            m_mAdapter.notifyDataSetChanged();
            m_dAdapter.notifyDataSetChanged();
        } else if (parent == m_lsvMonth) {
            m_nSelectedMonth = position;
            m_nSelectedDay = -1;

            m_mAdapter.setPos(m_nSelectedMonth);
            m_dAdapter.setPos(m_nSelectedDay);

            m_mAdapter.notifyDataSetChanged();

            m_dAdapter.clear();

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR) - m_nSelectedYear;
            int month = m_nSelectedMonth;
            Calendar mycal = new GregorianCalendar(year, month, 1);
            int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int i = 1; i <= daysInMonth; i++) {
                if (i > 9)
                    m_dAdapter.add(i + "일");
                else
                    m_dAdapter.add("0" + i + "일");
            }
            m_dAdapter.notifyDataSetChanged();
        } else if (parent == m_lsvDay) {
            m_nSelectedDay = position;
            m_dAdapter.setPos(m_nSelectedDay);
            m_dAdapter.notifyDataSetChanged();
        }
    }

    public class CalendarAdapter extends ArrayAdapter<String> {

        private Context m_context;
        int m_SelectedIndex = -1;

        public CalendarAdapter(Context context, ArrayList<String> arrayItem) {
            super(context, 0, arrayItem);
            m_context = context;
        }

        public void setPos(int index) {
            m_SelectedIndex = index;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemHolder itemHolder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_row_simple_height30dp,
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

            TextView m_tvTitle;

            public ItemHolder(View v) {
                m_tvTitle = (TextView) v.findViewById(R.id.tv_date);
            }

            public void showInfo(int position) {

                String info = getItem(position);
                m_tvTitle.setText(info);

                if (m_SelectedIndex == position) {
                    m_tvTitle.setTextColor(ContextCompat.getColor(_context, R.color.main_color_1));
                } else {
                    m_tvTitle.setTextColor(ContextCompat.getColor(_context, R.color.main_grey));
                }
            }
        }
    }
}
