package app.vitamiin.com;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class NoticeDialog extends Dialog implements View.OnClickListener {
    private Context _context;
    View.OnClickListener m_listener = null;
    String m_title, m_content, m_ButtonText;
    Boolean m_AutoClose;

    public NoticeDialog(Context context, String title, String content, String ButtonText, Boolean AutoClose) {
        super(context);
        _context = context;
        m_title = title;
        m_content = content;
        m_ButtonText = ButtonText;
        m_AutoClose = AutoClose;

        initView();
    }

    public NoticeDialog(Context context, String title, String content, String ButtonText, Boolean AutoClose, View.OnClickListener listener) {
        super(context);
        _context = context;
        m_listener = listener;
        m_title = title;
        m_content = content;
        m_ButtonText = ButtonText;
        m_AutoClose = AutoClose;

        initView();
    }

    public void initView(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dlg_notice);

        findViewById(R.id.tv_ok).setOnClickListener(this);

        ((TextView) findViewById(R.id.tv_title)).setText(m_title);
        ((TextView) findViewById(R.id.tv_content)).setText(m_content);
        if(!m_ButtonText.equals("")){
            findViewById(R.id.lly_ok).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.tv_ok)).setText(m_ButtonText);
        }
        if(m_AutoClose){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    onClick(findViewById(R.id.tv_ok));
                }
            }, 3200);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ok:
                if(m_listener!=null)
                    m_listener.onClick(v);
                dismiss();
                break;
        }
    }
}
