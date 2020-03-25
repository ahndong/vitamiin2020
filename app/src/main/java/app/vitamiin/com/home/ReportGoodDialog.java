package app.vitamiin.com.home;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import app.vitamiin.com.R;

public class ReportGoodDialog extends Dialog implements View.OnClickListener {
    private Context _context;

    int m_nSelectedIndex = -1;
    TextView m_tvReport1, m_tvReport2, m_tvReport3, m_tvReport4;
    EditText m_edtContent;

    public ReportGoodDialog(Context context) {
        super(context);
        _context = context;
        // TODO Auto-generated constructor stub
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dlg_report_good);

        findViewById(R.id.tv_ok).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
        m_tvReport1 = (TextView) findViewById(R.id.tv_report_1);
        m_tvReport2 = (TextView) findViewById(R.id.tv_report_2);
        m_tvReport3 = (TextView) findViewById(R.id.tv_report_3);
        m_tvReport4 = (TextView) findViewById(R.id.tv_report_4);
        m_tvReport1.setOnClickListener(this);
        m_tvReport2.setOnClickListener(this);
        m_tvReport3.setOnClickListener(this);
        m_tvReport4.setOnClickListener(this);
        m_edtContent = (EditText) findViewById(R.id.edt_content);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ok:
                if (m_nSelectedIndex == -1) {
                    Toast.makeText(_context, "항목을 선택해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (m_edtContent.getText().toString().length() == 0) {
                    Toast.makeText(_context, "내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                ((DetailGoodActivity) _context).setReport(m_nSelectedIndex, m_edtContent.getText().toString());
                dismiss();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_report_1:
                m_nSelectedIndex = 0;
                m_tvReport1.setSelected(true);
                m_tvReport2.setSelected(false);
                m_tvReport3.setSelected(false);
                m_tvReport4.setSelected(false);
                break;
            case R.id.tv_report_2:
                m_nSelectedIndex = 1;
                m_tvReport2.setSelected(true);
                m_tvReport1.setSelected(false);
                m_tvReport3.setSelected(false);
                m_tvReport4.setSelected(false);
                break;
            case R.id.tv_report_3:
                m_nSelectedIndex = 2;
                m_tvReport3.setSelected(true);
                m_tvReport2.setSelected(false);
                m_tvReport1.setSelected(false);
                m_tvReport4.setSelected(false);
                break;
            case R.id.tv_report_4:
                m_nSelectedIndex = 3;
                m_tvReport4.setSelected(true);
                m_tvReport2.setSelected(false);
                m_tvReport3.setSelected(false);
                m_tvReport1.setSelected(false);
                break;
        }
    }
}
