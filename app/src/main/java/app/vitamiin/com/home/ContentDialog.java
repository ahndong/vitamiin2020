package app.vitamiin.com.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;

import app.vitamiin.com.Adapter.ContentListAdapter;
import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.Model.ContentInfo;
import app.vitamiin.com.Model.GoodInfo;
import app.vitamiin.com.NoticeDialog;
import app.vitamiin.com.R;
import app.vitamiin.com.http.Net;

public class ContentDialog extends BaseActivity implements View.OnClickListener {
    Context m_context;

    GoodInfo m_info1;
    RelativeLayout m_rlyGood1;

    ImageLoader m_imageLoader = ImageLoader.getInstance();
    ArrayList<ContentInfo> m_arrContent1 = new ArrayList<>();
    ListView m_lsvContent;
    ContentListAdapter m_content_adapter;

//    public ContentDialog(Context context, GoodInfo goodInfo, ArrayList<ContentInfo> arrContent){
//        super(context);
//    _context = context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dlg_content);

        m_context = this;
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        setContentView(R.layout.dlg_content);

        m_info1 = (GoodInfo) getIntent().getSerializableExtra("info");
        m_arrContent1 = (ArrayList<ContentInfo>) getIntent().getSerializableExtra("ContentsInfo");
//        m_info1 = goodInfo;
//        m_arrContent1 = arrContent;

        m_rlyGood1 = (RelativeLayout) findViewById(R.id.ic_good_1);
        findViewById(R.id.tv_close).setOnClickListener(this);

        LoadView(m_rlyGood1, m_info1);

        m_lsvContent = (ListView) findViewById(R.id.lsv_chart);
        m_content_adapter = new ContentListAdapter(m_context, new ArrayList<ContentInfo>());
        m_lsvContent.setAdapter(m_content_adapter);

        fillTable();
    }

    private void LoadView(View v, GoodInfo info) {
        ((TextView) v.findViewById(R.id.tv_rate)).setText(String.valueOf(info._rate));
        ImageView m_imvImage = (ImageView) v.findViewById(R.id.imv_good);
        Glide.with(m_context).load(Net.URL_SERVER1 + info._imagePath).into(m_imvImage);

        ((TextView) v.findViewById(R.id.tv_name)).setText(info._name);
        ((TextView) v.findViewById(R.id.tv_business)).setText(info._business);

        ImageView[] arr_mimvStars = new ImageView[5];
        arr_mimvStars[0] = (ImageView) v.findViewById(R.id.imv_star1);
        arr_mimvStars[1] = (ImageView) v.findViewById(R.id.imv_star2);
        arr_mimvStars[2] = (ImageView) v.findViewById(R.id.imv_star3);
        arr_mimvStars[3] = (ImageView) v.findViewById(R.id.imv_star4);
        arr_mimvStars[4] = (ImageView) v.findViewById(R.id.imv_star5);

        for(int i = 0; i < 5; i++)
            if(info._rate < i + 0.5)
                Glide.with(m_context).load(R.drawable.ic_star_empty).into(arr_mimvStars[i]);
            else if(info._rate < i+1)
                Glide.with(m_context).load(R.drawable.ic_star_half).into(arr_mimvStars[i]);
            else
                Glide.with(m_context).load(R.drawable.ic_star_one).into(arr_mimvStars[i]);

        ((TextView)findViewById(R.id.tv_about_quantity)).setText("* " + info._intake + "(단위용량 : " + info._amount_per_intake + info._unit_amount_per_intake + ")");
        findViewById(R.id.tv_question).setOnClickListener(this);
    }

    private void fillTable() {
        for (int i = 0; i < m_arrContent1.size(); i++) {
            ContentInfo content1 = m_arrContent1.get(i);
            m_content_adapter.add(content1);
        }
        m_content_adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_close:
                finish();
                break;
            case R.id.tv_question:
                new NoticeDialog(this, "표 설명", "* 일일영양성분기준치\n" +
                        " - 1일 영양성분 기준치에 대한 비율은 1일 영양성분 기준치에 대해 해당 식품의 1회 제공량 혹은 1일 제공량 혹은 총 내용량 혹은 100 g/ml 가 제공하는 비율을 나타냅니다. 따라서 1일 영양성분 기준치에 대한 비율을 보면 식품이 하루에 섭취해야 할 영양성분의 몇 %를 함유하는지 알 수 있으며 해당 식품의 영양성분 함량이 높은 수준인지 낮은 수준인지 쉽게 이해할 수 있습니다.\n" +
                        "\n" +
                        "* 열량, 트랜스지방은 1일 영양성분 기준치에 대한 비율을 표시하지 않는다.\n" +
                        " - 열량, 트랜스지방은 1일 영양성분 기준치가 정해지지 않아 1일 영양성분 기준치에 대한 비율을 표시하지 않습니다.\n" +
                        "\n" +
                        "* 1일 영양성분 기준치에 대한 비율은 영양성분 함량 강조표시를 하는 기준\n" +
                        " - 1일 영양성분 기준치에 대한 비율은 영양성분함량 강조표시의 기준으로 활용되기도 합니다. 예를 들어 총 내용량당 일일 영양성분기준치의 10% 이상의 단백질을 포함하는 식품은 단백질을 ‘함유’한다거나 해당 식품이 단백질의 ‘급원’이라는 표현을 사용할 수 있습니다. 또한, 총 내용량 당 일일 영양성분기준치의 15% 이상의 비타민 또는 무기질을 포함하는 식품은 비타민 또는 무기질에 대하여 ‘함유’ 또는 ‘급원’이라는 표현을 사용할 수 있습니다.", "닫기", false).show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.tv_close));
    }
}
