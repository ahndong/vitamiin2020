package app.vitamiin.com.home;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import app.vitamiin.com.R;

public class ReportReviewDialog extends Dialog implements View.OnClickListener {
    private Context _context;

    private int m_nSelectedIndex = -1;
    private int m_flag;
    private TextView m_tvReport1, m_tvReport2, m_tvReport3, m_tvReport4, m_tvReport5, m_tvReport6, m_tvReport7;

    public ReportReviewDialog(Context context,int flag) {
        super(context);
        _context = context;
        m_flag =flag;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dlg_report_review);

        findViewById(R.id.tv_ok).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
        m_tvReport1 = (TextView) findViewById(R.id.tv_report_1);
        m_tvReport2 = (TextView) findViewById(R.id.tv_report_2);
        m_tvReport3 = (TextView) findViewById(R.id.tv_report_3);
        m_tvReport4 = (TextView) findViewById(R.id.tv_report_4);
        m_tvReport5 = (TextView) findViewById(R.id.tv_report_5);
        m_tvReport6 = (TextView) findViewById(R.id.tv_report_6);
        m_tvReport7 = (TextView) findViewById(R.id.tv_report_7);
        m_tvReport1.setOnClickListener(this);
        m_tvReport2.setOnClickListener(this);
        m_tvReport3.setOnClickListener(this);
        m_tvReport4.setOnClickListener(this);
        m_tvReport5.setOnClickListener(this);
        m_tvReport6.setOnClickListener(this);
        m_tvReport7.setOnClickListener(this);
    }

    private void clearCheck() {
        m_tvReport1.setSelected(false);
        m_tvReport2.setSelected(false);
        m_tvReport3.setSelected(false);
        m_tvReport4.setSelected(false);
        m_tvReport5.setSelected(false);
        m_tvReport6.setSelected(false);
        m_tvReport7.setSelected(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ok:
                if (m_nSelectedIndex == -1) {
                    Toast.makeText(_context, "항목을 선택해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(m_flag == 0)
                    ((DetailReviewActivity) _context).setReport(m_nSelectedIndex);
                else if(m_flag ==1)
                    ((DetailExpActivity) _context).setReport(m_nSelectedIndex);
                dismiss();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_report_1:
                m_nSelectedIndex = 0;
                clearCheck();
                m_tvReport1.setSelected(true);
                break;
            case R.id.tv_report_2:
                m_nSelectedIndex = 1;
                clearCheck();
                m_tvReport2.setSelected(true);
                break;
            case R.id.tv_report_3:
                m_nSelectedIndex = 2;
                clearCheck();
                m_tvReport3.setSelected(true);
                break;
            case R.id.tv_report_4:
                m_nSelectedIndex = 3;
                clearCheck();
                m_tvReport4.setSelected(true);
                break;
            case R.id.tv_report_5:
                m_nSelectedIndex = 4;
                clearCheck();
                m_tvReport5.setSelected(true);
                break;
            case R.id.tv_report_6:
                m_nSelectedIndex = 5;
                clearCheck();
                m_tvReport6.setSelected(true);
                break;
            case R.id.tv_report_7:
                m_nSelectedIndex = 6;
                clearCheck();
                m_tvReport7.setSelected(true);
                break;
        }
    }
}
