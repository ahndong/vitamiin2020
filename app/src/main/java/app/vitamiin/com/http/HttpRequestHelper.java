package app.vitamiin.com.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.HttpEntity;
import org.apache.http.client.params.ClientPNames;

public class HttpRequestHelper extends AsyncHttpClient {

    public static final int TIME_OUT = 180000; // 단위는 미리 초
    public static AsyncHttpClient client = new AsyncHttpClient();
    public static RequestHandle request = null;

    private static boolean m_bLoading = false;
    private static ProgressDialog m_dlgProgress = null;

    public static boolean isNetworkAvaliable(Context ctx) {
        ConnectivityManager connMgr = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean wifiConnected = false;
        boolean mobileConnected = false;

        // Checks the user prefs and the network connection. Based on the
        // result, decides
        // whether
        // to refresh the display or keep the current display.
        // If the userpref is Wi-Fi only, checks to see if the device has a
        // Wi-Fi connection.
        if (networkInfo != null && networkInfo.isConnected()) {
            // If device has its Wi-Fi connection, sets refreshDisplay
            // to true. This causes the display to be refreshed when the user
            // returns to the app.
            wifiConnected = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            // Otherwise, the app can't download content--either because there
            // is no network
            // connection (mobile or Wi-Fi), or because the pref setting is
            // WIFI, and there
            // is no Wi-Fi connection.
            // Sets refreshDisplay to false.
            wifiConnected = false;
            mobileConnected = false;
        }

        return (mobileConnected || wifiConnected);
    }

    public static AsyncHttpClient getClient() {
        return client;
    }

    public static void get(String url, RequestParams params,
                           AsyncHttpResponseHandler responseHandler) {
        if (params == null)
            Log.i("HTTP GET >>>", getAbsoluteUrl(url));
        else
            Log.i("HTTP GET >>>", getAbsoluteUrl(url) + "?" + params.toString());
        client.getHttpClient().getParams()
                .setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        client.setTimeout(TIME_OUT);
        request = client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params,
                            AsyncHttpResponseHandler responseHandler) {

        if (params == null)
            Log.i("HTTP POST >>>", getAbsoluteUrl(url));
        else
            Log.i("HTTP POST >>>",
                    getAbsoluteUrl(url) + "\ndata: " + params.toString());
        client.getHttpClient().getParams()
                .setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        client.setTimeout(TIME_OUT * 1000);
        request = client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(Context context, String url, HttpEntity entity,
                            AsyncHttpResponseHandler responseHandler) {

        if (entity == null)
            Log.i("HTTP POST >>>", getAbsoluteUrl(url));
        else
            Log.i("HTTP POST >>>",
                    getAbsoluteUrl(url) + "\ndata: " + entity.toString());
        client.getHttpClient().getParams()
                .setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        client.setTimeout(TIME_OUT * 1000);
        // requestMe = client.post(getAbsoluteUrl(url), params, responseHandler);
        request = client.post(context, url, entity, "multipart/form-data",
                responseHandler);
    }

    public static void put(String url, RequestParams params,
                           AsyncHttpResponseHandler responseHandler) {

        if (params == null)
            Log.i("HTTP POST >>>", getAbsoluteUrl(url));
        else
            Log.i("HTTP POST >>>",
                    getAbsoluteUrl(url) + "\ndata: " + params.toString());
        request = client.put(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return "" + relativeUrl;
    }

    public static void getFromFullService(String url, RequestParams params,
                                          AsyncHttpResponseHandler responseHandler) {
        if (params == null)
            Log.i("HTTP GET >>>", url);
        else
            Log.i("HTTP GET >>>", url + "?" + params.toString());
        client.get(url, params, responseHandler);
    }

    // Show loading progress bar from server
    public static void showProgress(Context ctx, String title, String msg) {
        if (isShowProgress())
            return;

        m_bLoading = true;
        m_dlgProgress = ProgressDialog.show(ctx, "",
                msg, true);

//        m_dlgProgress = new ProgressDialog(ctx);
//        m_dlgProgress.setTitle(title);
//        m_dlgProgress.setMessage(msg);
//        m_dlgProgress.setCancelable(true);
//        m_dlgProgress.setCanceledOnTouchOutside(false);
//        m_dlgProgress.setIndeterminate(false);
//        m_dlgProgress.setOnCancelListener(new OnCancelListener() {
//
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                // TODO Auto-generated method stub
//                cancelProgress();
//            }
//        });
        m_dlgProgress.show();
//        m_dlgProgress.setContentView(R.layout.layout_loading);
//        ImageView iv = (ImageView) m_dlgProgress.findViewById(R.id.iv_loading);
//        AnimationDrawable animDrawable = (AnimationDrawable) iv.getDrawable();
//        animDrawable.start();
    }

    // Whether there is data loading state
    public static boolean isShowProgress() {
        return m_bLoading;
    }

    public static void cancelProgress() {
        m_bLoading = false;
        if (request != null) {
            request.cancel(true);
        }
    }

    // Hide loading progress bar
    public static void hideProgress() {
        m_bLoading = false;
        if (m_dlgProgress != null && m_dlgProgress.isShowing())
            m_dlgProgress.dismiss();
    }
}
