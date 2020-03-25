package app.vitamiin.com.home;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;
import app.vitamiin.com.R;
import app.vitamiin.com.common.SelectPhotoManager;

public class SelectReviewPhotoDialog extends Dialog implements OnClickListener, SelectPhotoManager.PhotoSelectCallback {

    private Context _context;
    private View.OnClickListener m_clickListener;
    private int m_type;
    private SelectPhotoManager m_photoManger = null;

    public SelectReviewPhotoDialog(Context context, View.OnClickListener listener, int type) {
        super(context);
        _context = context;
        m_type = type;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dlg_review_photo);

        m_clickListener = listener;

        findViewById(R.id.tv_close).setOnClickListener(this);
        findViewById(R.id.tv_camera).setOnClickListener(this);
        findViewById(R.id.tv_gallery).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_close:
                dismiss();
                break;

            case R.id.tv_gallery:
                goToPhotoAlbum();
                dismiss();
                break;
            case R.id.tv_camera:
                doTakePhotoAction();
                dismiss();
                break;
            default:
                break;
        }
    }

    /**
     * 표준카메라 호출 하기
     */
    private void doTakePhotoAction() {

        if (m_photoManger == null) {
            m_photoManger = new SelectPhotoManager((AppCompatActivity) _context);
            m_photoManger.setPhotoSelectCallback(this);
        }
        m_photoManger.doTakePicture();
        dismiss();
    }

    /**
     * 사진앨범 액티비티로 이동
     */
    private void goToPhotoAlbum() {
        if (m_photoManger == null) {
            m_photoManger = new SelectPhotoManager((AppCompatActivity) _context);
            m_photoManger.setPhotoSelectCallback(this);
        }
        m_photoManger.doPickFromGallery();
        dismiss();
    }

    @Override
    public void onSelectImageDone(Bitmap image, File file) {
        if (image != null && file != null) {
            String strImagePath = file.getPath();
            if (m_type == 0)
                ((ReviewWriteActivity) _context).setSelectedImage(image, file);
            else if (m_type == 1)
                ((ExpWriteActivity) _context).setSelectedImage(image, file);
        }
    }

    @Override
    public void onFailedSelectImage(int errorCode, String err) {

    }

    @Override
    public void onDeleteImage() {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        m_photoManger.onActivityResult(requestCode, resultCode, data);
    }
}
