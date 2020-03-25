package app.vitamiin.com.http;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import app.vitamiin.com.BaseActivity;
import app.vitamiin.com.common.UserManager;
import app.vitamiin.com.login.LoginActivity;

/**
 * HTTP방식으로 데이터를 주고 리퀘스트 받아오는 클래스 GET와 POST방식 두가지 다 사용 가능하다. 생성자 호출해서 클래스 인스턴스
 * 생성한 다음 requestMe ( URL주소, 방식(GET or POST), 변수명+변수값 ) ; 이 함수로 보내고 받을 수 있다.
 */
public class HttpRequester {

    private final int MAX_TIMEOUT = 15000;

    private final int FINAL_BUFFER_SINGLE_SIZE = 1024;

    private static HttpRequester httpRequester;

    private final String TAG = "HttpRequester";

    private Context mContext;

    // |||||||||| parse ||||||||||| //
    private OnParseJSonListener parseJSonListhener = null;

    /**
     * 서버연동 URL
     */
    private String netUrl = "";
    /**
     * 파라메터
     */
    private String[] netParamFields = null;
    /**
     * 파라메터 값
     */
    private String[] netParamValues = null;
    /**
     * 첨부파일 이름
     */
    private String[] netParamFileNames = null;
    /**
     * http 방식으로 연결을 유지할 커넥션
     */
    private HttpURLConnection m_httpURLconn;

    /**
     * 통신 결과 값
     */
    private int m_nResultCode = -1;
    /**
     * 통신 결과 메세지
     */
    private String m_strErrMsg = "";
    /**
     * Progress
     */
    private Dialog m_PrograssDlg;
    /**
     * Thread
     */
    private ProgressThread progThread = null;
    /**
     * handler
     */
    private Handler procHandler;
    /**
     * 프로그레스바의 메시지
     */
    private String m_strProgressMsg = "";
    private final int MAX_BUFF = 1024 * 100;

    /**
     * Context-Type = multipart/form-data
     */
    private boolean m_isMultiForm = false;

    /**
     * 생성자
     */
    public static HttpRequester getInstance() {
        if (httpRequester == null) {
            httpRequester = new HttpRequester();
        }
        return httpRequester;
    }

    private HttpRequester() {
        // TODO Auto-generated constructor stub
    }

    /**
     * 초기화
     *
     * @param url
     * @param params
     */
    public void init(Context context, Object _super, Handler handler,
                     String url, String[] params, String[] values, boolean isMultiForm) {
        mContext = context;
        procHandler = handler;
        setOnParseJSonListener(_super);
        netUrl = url;
        netParamFields = params;
        netParamValues = values;
        netParamFileNames = null;
        m_isMultiForm = isMultiForm;
    }

    /**
     * 초기화
     *
     * @param url
     * @param params
     */
    public void init(Context context, Object _super, Handler handler,
                     String url, String[] params, String[] values, String[] fileNames) {
        mContext = context;
        procHandler = handler;
        setOnParseJSonListener(_super);
        netUrl = url;
        netParamFields = params;
        netParamValues = values;
        netParamFileNames = fileNames;
        m_isMultiForm = true;
    }

    /**
     * 해제
     */
    public void destroy() {

        stopNetThread();

        mContext = null;
    }

    /**
     * 프로그레스바 윈도의 메시지 내용 설정
     */
    public void setProgressMessage(String msg) {
        m_strProgressMsg = msg;
    }

    /**
     * Start Thread
     */
    public void startNetThread() {
        // 네트상태를 검사한다.
        if (!checkNetworkAvailable()) {
            Toast.makeText(mContext, "ERROR : NETWORK!", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        try {
            JSONObject obj = new JSONObject(netParamValues[1]);
            String mb_id = obj.isNull(Net.NET_VALUE_MEMBER_ID)? "":obj.getString(Net.NET_VALUE_MEMBER_ID);
            if (mb_id.length() == 9990) {
                UserManager.getInstance().saveData(mContext,  "", "", "", "");
                UserManager.getInstance().releaseUserData();
                ((BaseActivity) mContext).closeProgress();

                Toast.makeText(mContext, "로그인을 다시 해주세요!\nfrom " + mContext.getClass().toString().split("com")[1], Toast.LENGTH_LONG)
                        .show();
                Intent intent = new Intent(mContext, LoginActivity.class);
                mContext.startActivity(intent);
                ((BaseActivity) mContext).finish();
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (progThread == null) {
            progThread = new ProgressThread(procHandler);
            progThread.start();
        }

        if ("".equals(m_strProgressMsg)) {
            return;
        }

//        m_PrograssDlg = ProgressDialog.show(mContext, "",
//                m_strProgressMsg, true);
    }

    /**
     * Stop Thread for Animation
     */
    public void stopNetThread() {

//        if (m_PrograssDlg != null) {
//            m_PrograssDlg.dismiss();
//            m_PrograssDlg = null;
//        }

        if (progThread != null) {
            if (progThread.isAlive()) {
                progThread.interrupt();
            }
            progThread = null;
        }
    }

    /**
     * 결과메쎄지 얻기
     */
    public String getResultMsg() {
        return m_strErrMsg;
    }

    /**
     * 결과코드 얻기
     *
     * @return
     */
    public int getResultCode() {
        return m_nResultCode;
    }

    /**
     * 서버와의 통신이 성공한 경우 서버메시지를, 실패한 경우 실패한 메시지를 내려보낸다.
     */
    public void showNetErrMessage(String msg) {

        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 네트워크 상태 정보 체크
     */
    private boolean checkNetworkAvailable() {

        boolean isNetworkAvailable = false;
        ConnectivityManager conn_manager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        try {
            NetworkInfo network_info = conn_manager.getActiveNetworkInfo();
            if (network_info != null && network_info.isConnected()) {

                if (network_info.getType() == ConnectivityManager.TYPE_WIFI) {
                    // do some staff with WiFi connection
                    isNetworkAvailable = true;

                } else if (network_info.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // do something with Carrier connection
                    isNetworkAvailable = true;

                } else if (network_info.getType() == ConnectivityManager.TYPE_WIMAX) {
                    // do something with Wibro/Wimax connection
                    isNetworkAvailable = true;
                }
            }
        } catch (Exception e) {
            Log.d("HttpRequester", e.getMessage());
        }

        return isNetworkAvailable;
    }

    /**
     * 리퀘스트 받아오는 함수 ( URL주소, 방식(GET or POST), 변수명+변수값 ) ;
     */
    private int request(String method) {

        int resultCode = Net.CONNECTION_NONE;
        final String boundary = "*****";
        final String twoHyphens = "--";
        m_strErrMsg = "";

        // / 받아올 인풋스트림
        // / POST방식일 경우 데이터를 전송할 아웃풋 스트림
        InputStream in = null;
        OutputStream out = null;
        DataOutputStream dos = null;

        try {
            URL url = new URL(netUrl);

            // / 연결하고 메소드 셋팅함
            m_httpURLconn = (HttpURLConnection) url.openConnection();
            m_httpURLconn.setUseCaches(false);
            m_httpURLconn.setRequestMethod("POST");

            m_httpURLconn.setDoInput(true);
            m_httpURLconn.setDoOutput(true);
            m_httpURLconn.setInstanceFollowRedirects(false);

            if (!m_isMultiForm) {
                // / 인코딩 정의 HTTP방식으로 전송할때는 urlencoded방식으로 인코딩해서 전송해야한다.
                m_httpURLconn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
            } else {
                m_httpURLconn.setRequestProperty("Connection", "Keep-Alive");
                m_httpURLconn.setRequestProperty("Content-Type",
                        "multipart/form-data, boundary=" + boundary);
            }

            m_httpURLconn.setReadTimeout(MAX_TIMEOUT);
            m_httpURLconn.setConnectTimeout(MAX_TIMEOUT);

            // 포스트방식일 경우 변수를 outputStream생성해서 서버로 전송
            out = m_httpURLconn.getOutputStream(); // 아웃풋 스트림 생성
            dos = new DataOutputStream(out);

            if (netParamFields == null || netParamValues == null) {
                // LogCat.write(TAG, "Not  parameters");
            } else if (netParamFields.length != netParamValues.length) {
                resultCode = Net.BAD_REQUEST_PARAMS;
                m_strErrMsg = "Bad requestMe parameters";
                // LogCat.write(TAG, m_strErrMsg);
            } else {

                if (!m_isMultiForm) {
                    for (int i = 0; i < netParamFields.length; i++) {
                        dos.writeBytes(netParamFields[i]);
                        dos.writeBytes("=");
                        if (netParamValues[i] == null) {
                            netParamValues[i] = "";
                        }
                        dos.write(netParamValues[i].getBytes("UTF-8"));
                        if (i != netParamFields.length - 1)
                            dos.writeBytes("&");
                    }

                    dos.flush();
                } else {

                    String lineEnd = "\r\n";

                    int cnt = netParamFields.length;
                    for (int i = 0; i < cnt; i++) {
                        if (netParamFields[i] == null)
                            break;
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\""
                                + netParamFields[i] + "\"" + lineEnd + lineEnd);
                        if (netParamValues[i] == null)
                            netParamValues[i] = "";
                        dos.write(netParamValues[i].getBytes("UTF-8"));
                        dos.writeBytes(lineEnd);
                    }
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    if (netParamFileNames != null && m_isMultiForm) {
                        for (int i = 0; i < netParamFileNames.length; i++) {
                            String strAttachedFile = netParamFileNames[i];
                            if (strAttachedFile != null
                                    && strAttachedFile.length() > 0) {
                                // 데이터
                                String fileName = System.currentTimeMillis()
                                        + strAttachedFile
                                        .substring(strAttachedFile
                                                .length() - 4);
                                dos.writeBytes(twoHyphens + boundary + lineEnd);
                                dos.writeBytes("Content-Disposition: form-data; name=\""
                                        + Net.NET_VALUE_UPLOAD_FILES
                                        + "\";filename=\""
                                        + fileName
                                        + "\""
                                        + lineEnd);
                                dos.writeBytes(lineEnd);

                                InputStream mFileInputStream = new FileInputStream(
                                        strAttachedFile);
                                int bytesAvailable = mFileInputStream
                                        .available();
                                int maxBufferSize = FINAL_BUFFER_SINGLE_SIZE;
                                int bufferSize = Math.min(bytesAvailable,
                                        maxBufferSize);

                                byte[] buffer = new byte[bufferSize];
                                int bytesRead = mFileInputStream.read(buffer,
                                        0, bufferSize);

                                // read image
                                while (bytesRead > 0) {
                                    dos.write(buffer, 0, bufferSize);
                                    bytesAvailable = mFileInputStream
                                            .available();
                                    bufferSize = Math.min(bytesAvailable,
                                            maxBufferSize);
                                    bytesRead = mFileInputStream.read(buffer,
                                            0, bufferSize);
                                }

                                dos.writeBytes(lineEnd);
                                dos.writeBytes(twoHyphens + boundary + lineEnd);
                                // close streams
                                mFileInputStream.close();
                            }
                        }
                        dos.writeBytes(twoHyphens + boundary + twoHyphens
                                + lineEnd);
                    }
                    dos.flush();
                }
            }

            // LogCat.write(TAG, "success requestMe to server.");

            // ------------------------ 응답 받는 부분
            // --------------------------------
            in = m_httpURLconn.getInputStream(); // / 인풋스트림 생성
            InputStreamReader isr = null;
            BufferedReader br = null;

            String resultData = "";

            try {
                isr = new InputStreamReader(in, "UTF-8");
                br = new BufferedReader(isr, MAX_BUFF);

                StringBuilder sb = new StringBuilder();
                while ((resultData = br.readLine()) != null) {
                    sb.append(resultData);
                }
                resultData = sb.toString();
                sb = null;
                // parse
                resultCode = parse(resultData);
            } catch (IOException e) {

                resultCode = Net.BAD_STREAM;
                m_strErrMsg = e.getMessage();

            } catch (JSONException e1) {

                resultCode = Net.JSON_PARSER_FAILD;
                m_strErrMsg = e1.getMessage();

            } finally {
                if (in != null)
                    in.close();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            resultCode = Net.CONNECTION_ERROR;
            m_strErrMsg = e.getMessage();
        } catch (IOException e) {
            resultCode = Net.BAD_STREAM;
            m_strErrMsg = e.getMessage();
        } finally {
            if (dos != null) {

                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if (m_httpURLconn != null)
                m_httpURLconn.disconnect();
        }

        return resultCode;
    }

    public static String buildParameters(Map<String, Object> params)
            throws IOException {
        return "";
    }

    private class ProgressThread extends Thread {

        private Handler mHandler;

        private ProgressThread(Handler handler) {
            this.mHandler = handler;
        }

        @Override
        public void run() {

            m_nResultCode = request("POST");

            // -----------------------------
            if (m_PrograssDlg != null) {
                m_PrograssDlg.dismiss(); // close Loading Dialog
            }

            mHandler.sendEmptyMessage(Net.THREAD_REQUEST_END);
        }
    }

    /**
     * 결과자료 파저
     *
     * @param resultData
     * @return
     * @throws JSONException
     */
    public int parse(String resultData) throws JSONException {
        int resultCode = Net.CONNECTION_SUCCSES;
        // ---
        JSONObject j_source = new JSONObject(resultData);

        int code = j_source.getInt(Net.NET_VALUE_CODE);
        if (code == 200)
            resultCode = Net.CONNECTION_SUCCSES;
        else
            resultCode = Net.CONNECTION_FAILD;
        m_strErrMsg = j_source.getString(Net.NET_VALUE_MSG);

        parseJSon(j_source);
        return resultCode;
    }

    /**
     * ParseJSonListener 설정
     *
     * @param content
     */
    public void setOnParseJSonListener(Object content) {
        parseJSonListhener = (OnParseJSonListener) content;
    }

    /**
     * JSON 파저하기
     */
    private void parseJSon(JSONObject j_source) {
        parseJSonListhener.onParseJSon(j_source);
    }
}
