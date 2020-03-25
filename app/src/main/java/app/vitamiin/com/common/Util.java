package app.vitamiin.com.common;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;

public class Util {
    public static final String KT = "KT";
    public static final String LGT = "LGT";
    public static final String SKT = "SKT";

    public static void Log(String paramString) {
    }

    public static final String convertDate(String paramString1,
                                           String paramString2) {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
                paramString2, Locale.getDefault());
        try {
            String str = localSimpleDateFormat.format(localSimpleDateFormat
                    .parse(paramString1));
            return str;
        } catch (ParseException localParseException) {
            localParseException.printStackTrace();
        }
        return "";
    }

    public static String getCurrentOSVersion(Context paramContext) {
        try {
            String str = Build.VERSION.RELEASE;
            return str;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return "0.0";
    }

    public static int getCurrentSDKVersion(Context paramContext) {
        try {
            int i = Build.VERSION.SDK_INT;
            return i;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return 0;
    }

    public static String getCurrentVersion(Context paramContext) {
        try {
            String str = paramContext.getPackageManager().getPackageInfo(
                    paramContext.getPackageName(), 0).versionName;
            return str;
        } catch (NameNotFoundException localNameNotFoundException) {
            localNameNotFoundException.printStackTrace();
        }
        return "";
    }

    public static int[] getDeviceSize(Context paramContext) {
        Display localDisplay = ((WindowManager) paramContext
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return new int[]{localDisplay.getWidth(), localDisplay.getHeight()};
    }

    public static String getModelName(Context paramContext) {
        try {
            String str = Build.MODEL;
            return str;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return "ModelName";
    }

    public static String getPhonenum(Context paramContext) {
        String str1 = ((TelephonyManager) paramContext
                .getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
        if ((str1 != null) && (str1.length() > 0)) {
            String str2 = str1.trim().replace("-", "");
            if (str2.indexOf("+8210") == 0)
                str2 = str2.replace("+8210", "010");
            if (str2.indexOf("8210") == 0)
                str2 = str2.replaceFirst("8210", "010");
            if (str2.indexOf("82") == 0)
                str2 = str2.replaceFirst("82", "");
            return str2.replace("+", "0");
        }
        return "";
    }

    public static String getTelecom(Context paramContext) {
        String str = ((TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE))
                .getNetworkOperatorName();
        if ("SKTelecom".equalsIgnoreCase(str))
            return "SKT";
        if ("olleh".equalsIgnoreCase(str))
            return "KT";
        if ("LG U+".equalsIgnoreCase(str))
            return "LGT";
        return "";
    }

    public static void hideKeyPad(AppCompatActivity _activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) _activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(_activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static boolean isAppInstall(Context paramContext, String paramString) {
        PackageManager localPackageManager = paramContext.getPackageManager();
        try {
            localPackageManager.getPackageInfo(paramString, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException localNameNotFoundException) {
        }
        return false;
    }

    public static boolean isAppInstall2(Context paramContext, String paramString) {
        try {
            ApplicationInfo localApplicationInfo = paramContext
                    .getPackageManager().getApplicationInfo(paramString, 0);
            String str = (String) paramContext.getPackageManager()
                    .getApplicationLabel(localApplicationInfo);
            boolean bool = TextUtils.isEmpty(str);
            boolean i = false;
            if (!bool) {
                Log(String.format("getApplicationLabel : %s",
                        new Object[]{str}));
                i = true;
            }
            return i;
        } catch (NameNotFoundException localNameNotFoundException) {
            localNameNotFoundException.printStackTrace();
        }
        return false;
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isCheckEmailCorrect(String paramString) {
        if (TextUtils.isEmpty(paramString)) {
            return false;
        }

        if (!Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE).matcher(paramString)
                .matches()) {
            return false;
        }
        return true;
    }

    public static boolean isConnect(Context paramContext) {
        NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return (localNetworkInfo != null) && (localNetworkInfo.isConnected());
    }

    public static boolean isUsim(Context paramContext) {
        boolean i = true;
        if (((TelephonyManager) paramContext
                .getSystemService(Service.TELEPHONY_SERVICE)).getSimState() == 1) {
            i = false;
        }
        return i;
    }

    public static final Intent setRunApplication(Context paramContext,
                                                 Intent paramIntent, int paramInt) {
        Iterator<ResolveInfo> localIterator = paramContext.getPackageManager()
                .queryIntentActivities(paramIntent, 0).iterator();
        while (true) {
            if (!localIterator.hasNext())
                return paramIntent;
            ResolveInfo localResolveInfo = (ResolveInfo) localIterator.next();
            Object[] arrayOfObject = new Object[2];
            arrayOfObject[0] = localResolveInfo.activityInfo.packageName;
            arrayOfObject[1] = localResolveInfo.activityInfo.name;
            Log(String.format("activityInfo.packageName : %s, %s",
                    arrayOfObject));
            String str = localResolveInfo.activityInfo.packageName;
            switch (paramInt) {
                default:
                    if ((str.indexOf("com.android.browser") < 0)
                            && (str.indexOf("com.android.chrome") < 0)
                            && (str.indexOf("com.sec.android.app.sbrowser") < 0))
                        continue;
                    paramIntent.setClassName(
                            localResolveInfo.activityInfo.packageName,
                            localResolveInfo.activityInfo.name);
                    break;
                case 0:
                    if ((str.indexOf("com.android.browser") < 0)
                            && (str.indexOf("com.android.chrome") < 0)
                            && (str.indexOf("com.sec.android.app.sbrowser") < 0))
                        continue;
                    paramIntent.setClassName(
                            localResolveInfo.activityInfo.packageName,
                            localResolveInfo.activityInfo.name);
                    break;
                case 1:
                    if (str.indexOf("com.google.android.apps.maps") < 0)
                        continue;
                    paramIntent.setClassName(
                            localResolveInfo.activityInfo.packageName,
                            localResolveInfo.activityInfo.name);
            }
        }
    }

    public static void deleteDir(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            File[] childFileList = dir.listFiles();
            for (File childFile : childFileList) {
                if (childFile.isDirectory()) {
                    deleteDir(childFile.getAbsolutePath()); // 하위 디렉토리 루프
                } else {
                    childFile.delete(); // 하위 파일삭제
                }
            }
            dir.delete(); // root 삭제
        }
    }

    public static void deleteFiles(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            File[] childFileList = dir.listFiles();
            for (File childFile : childFileList) {
                if (childFile.isDirectory()) {
                    continue;
                } else {
                    childFile.delete(); // 하위 파일삭제
                }
            }
        }
    }

    /**
     * web-url 로부터 이미지 불러온다.
     *
     * @param url
     * @param inSampleSize
     * @return
     */
    public static Bitmap loadImageFromWebUrl(String url, int inSampleSize) {
        // NOTE : 스레드 안에 넣어야 한다. 메인스레드에서 호출되면 오류
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPurgeable = true; // declare as purgeable to disk
            options.inSampleSize = inSampleSize;
            InputStream is = (InputStream) new URL(url).getContent();
            Bitmap b = BitmapFactory.decodeStream(is, null, options);
            return b;
        } catch (Exception e) {
            System.out.println("Exc2=" + e);
            return null;
        }
    }

    /**
     * bitmap 를 jpeg 파일로 저장; 출처 : http://snowbora.com/418
     */
    public static boolean saveBitmapToFileCache(Bitmap bitmap,
                                                String strFilePath, boolean bPNG) {

        if (bitmap == null) {
            return false;
        }

        boolean bRet;

        bRet = false;

        File fileItem = new File(strFilePath);
        OutputStream out = null;

        try {
            fileItem.getParentFile().mkdirs();
            fileItem.createNewFile();
            out = new FileOutputStream(fileItem);
            if (bPNG) {
                bitmap.compress(CompressFormat.PNG, 100, out);
            } else {
                bitmap.compress(CompressFormat.JPEG, 100, out);
            }
            out.flush();
            bRet = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bRet;
    }

    public static int getAppVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static void getDeviceInfo(Context m_context, String[] strOSVer, String[] strModel, String[] deviceId) {
        try {
            strOSVer[0] = Build.VERSION.RELEASE;
            strModel[0] = Build.MODEL;
            String serial = "empty";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//                serial = Build.SERIAL;
//                serial = InstanceID.getInstance(m_context).getId();
//                serial = AdvertisingIdClient.getAdvertisingIdInfo(m_context).getId();
                serial = android.os.Build.class.getField("SERIAL").get(null).toString();
            }
            deviceId[0] = Build.MODEL + "_" + serial;
        } catch (Exception e) {
            e.printStackTrace();}
    }

    private static boolean isSDCARDMounted() {
        String status = Environment.getExternalStorageState();

        if (status.equals(Environment.MEDIA_MOUNTED))
            return true;
        return false;
    }

    public static boolean isStorage(boolean requireWriteAccess) {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else if (!requireWriteAccess
                && Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static long getExternalMemorySize() {
        if (isStorage(true) == true) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return -1;
        }
    }

    /**
     * Crop된 이미지가 저장될 파일을 만든다.
     *
     * @return Uri
     */
    public static File createSaveCropFile(Context context, String saveFolder) {
        // Uri uri;
        // String url = "tmp_" + String.valueOf(System.currentTimeMillis())
        // + ".jpg";
        // uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory()
        // + File.separator + sdcardFolder, url));
        // return uri;

        String url = "tmp_" + String.valueOf(System.currentTimeMillis())
                + ".jpg";
        File crop_file;
        if (isSDCARDMounted() && getExternalMemorySize() > 0) {
            crop_file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + saveFolder, url);
        } else {
            File directory = context.getFilesDir();
            crop_file = new File(directory, url);
            try {
                crop_file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                FileOutputStream fos = context.openFileOutput(url,
                        Context.MODE_WORLD_WRITEABLE);
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return crop_file;
    }

    /**
     * Crop된 이미지가 저장될 파일을 만든다.
     *
     * @return Uri
     */
    public static File createFile(Context context, String saveFolder,
                                  String fileName) {
        // Uri uri;
        // String url = "tmp_" + String.valueOf(System.currentTimeMillis())
        // + ".jpg";
        // uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory()
        // + File.separator + sdcardFolder, url));
        // return uri;

        File file;
        if (isSDCARDMounted() && getExternalMemorySize() > 0) {
            file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + saveFolder, fileName);
        } else {
            File directory = context.getFilesDir();
            file = new File(directory, fileName);
            if (file.exists()) {
                return file;
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                FileOutputStream fos = context.openFileOutput(fileName,
                        Context.MODE_WORLD_WRITEABLE);
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * bitmap 를 jpeg 파일로 저장; 출처 : http://snowbora.com/418
     */
    public static boolean SaveBitmapToFileCache(Bitmap bitmap,
                                                String strFilePath, boolean bPNG) {

        if (bitmap == null) {
            return false;
        }

        boolean bRet;

        bRet = false;

        File fileItem = new File(strFilePath);
        OutputStream out = null;

        try {
            fileItem.getParentFile().mkdirs();
            fileItem.createNewFile();
            out = new FileOutputStream(fileItem);
            if (bPNG) {
                bitmap.compress(CompressFormat.PNG, 100, out);
            } else {
                bitmap.compress(CompressFormat.JPEG, 100, out);
            }
            out.flush();
            bRet = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bRet;
    }

    // 텍스트에 각이한 스타일 지정하기
    public static SpannableStringBuilder setTextStyle(String szTxt, int nStyle,
                                                      int nColor, int nSize) {

        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.clear();

        if (szTxt == null || szTxt.equals("")) {
            return ssb;
        }

        ssb.append(szTxt); // 주의 : 본문에 "."이 들어가면 행바꾸기됩니다.
        try {
            ssb.setSpan(new StyleSpan(nStyle), 0, szTxt.length(),
                    Spannable.SPAN_COMPOSING); // nStyle : Typeface.BOLD_ITALIC
            ssb.setSpan(new ForegroundColorSpan(nColor), 0, szTxt.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(new AbsoluteSizeSpan(nSize), 0, szTxt.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        } catch (Exception e) {
            Log.d("", "setTextStyle() -->  " + e.getMessage());
        }

        return ssb;
        // textView.append(setTextStyle("테스트.스타일", Typeface.BOLD_ITALIC,
        // Color.RED, 22));
    }

    public static boolean isServiceRunning(Context ctx, String s_service_name) {
        ActivityManager manager = (ActivityManager) ctx
                .getSystemService(AppCompatActivity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (s_service_name.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;

    }

    /**
     * 금액(double)을 금액표시타입(소숫점2자리)으로 변환한다.
     *
     * @param moneyString 금액(double 형)
     * @return 변경된 금액 문자렬
     */
    public static String makeMoneyType(String moneyString) {
        String format = "#,###"/* "#.##0.00" */;
        DecimalFormat df = new DecimalFormat(format);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();

        dfs.setGroupingSeparator(','); //
        df.setGroupingSize(3); //
        df.setDecimalFormatSymbols(dfs);

        try {
            return (df.format(Float.parseFloat(moneyString))).toString();
        } catch (Exception e) {
            return moneyString;
        }
    }

    public static int dipToPixels(Context context, int dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public boolean is_family_info_loaded(){
        for(int i=0;i<UserManager.getInstance().HealthBaseInfo.size();i++){
            if(UserManager.getInstance().arr_profile_photo_file.get(i)==null && UserManager.getInstance().arr_profile_photo_resID.get(i)==-1){
                UserManager.getInstance().is_familyinfo_loaded = false;
                return false;
            }
        }
        UserManager.getInstance().is_familyinfo_loaded = true;
        return true;
    }

    public int getAutoImageResID2(String birth, int sex) {
        int res = -1;
        try{
            if(birth != null){
                if(birth.length()>3) {
                    int yearOfBirth = Integer.parseInt(birth.substring(0, 4));
                    int yearOfCurrent = Calendar.getInstance().get(Calendar.YEAR);
                    int age = yearOfCurrent - yearOfBirth;
                    if (age <= 10)
                        res = 0;
                    else if (age <= 19)
                        res = 1;
                    else if (age <= 50)
                        res = 2;
                    else
                        res = 3;

                    if (sex == 1)
                        res = res + 4;
                }else
                    res = -1;
            }else
                res = -1;
            return res;
        }
        catch (Exception e) {
            e.printStackTrace();
            return res;
        }
    }

    public int getAutoImageResID(int index) {
        int res = -1;
        try{
            if(UserManager.getInstance().HealthBaseInfo.get(index).member_birth != null){
                if(UserManager.getInstance().HealthBaseInfo.get(index).member_birth.length()>3) {
                    int yearOfBirth = Integer.parseInt(UserManager.getInstance().HealthBaseInfo.get(index).member_birth.substring(0, 4));
                    int yearOfCurrent = Calendar.getInstance().get(Calendar.YEAR);
                    int age = yearOfCurrent - yearOfBirth;
                    if (age <= 10)
                        res = 0;
                    else if (age <= 19)
                        res = 1;
                    else if (age <= 50)
                        res = 2;
                    else
                        res = 3;

                    if (UserManager.getInstance().HealthBaseInfo.get(index).member_sex == 1)
                        res = res + 4;
                }else
                    res = -1;
            }else
                res = -1;

            UserManager.getInstance().arr_profile_photo_resID.set(index, res);
            return res;
        }
        catch (Exception e) {
            e.printStackTrace();
            return res;
        }
    }

    public class ImgDownTask extends AsyncTask<String, Void, Integer> {
        public static final String TAG = "ImgDownTask";
        public String fileName = "";
        public Context ctx;
        public int asyncindex;
        public Bitmap mbitmap = null;

        public  ImgDownTask(Context context){
            super();
            this.ctx = context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                String url = params[0];
                fileName = params[1];
                asyncindex = Integer.parseInt(params[2]);
                if (url != null && !url.equals("null") && url.length() > 0) {
                    try {
                        if(!new File(fileName).exists()){
                            URL imageurl = new URL(url);
                            HttpGet httpRequest = new HttpGet(imageurl.toURI());
                            //HttpClient httpclient = HttpClientBuilder.create().build();
                            HttpClient httpclient = new DefaultHttpClient();
                            HttpResponse response = httpclient.execute(httpRequest);
                            HttpEntity entity = response.getEntity();
                            BufferedHttpEntity b_entity = new BufferedHttpEntity(entity);
                            InputStream input = b_entity.getContent();
                            ///위 7행과 아래 한 줄은 같은 것. by 동현
//                            InputStream input = new BufferedHttpEntity(new DefaultHttpClient().execute(new HttpGet(url)).getEntity()).getContent();

                            mbitmap = BitmapFactory.decodeStream(input);

                            if(Util.saveBitmapToFileCache(mbitmap, fileName, true)){
                                UserManager.getInstance().arr_profile_photo_bitmap.set(asyncindex, mbitmap);
                                UserManager.getInstance().arr_profile_photo_file.set(asyncindex, new File(fileName));
                                UserManager.getInstance().arr_profile_photo_resID.set(asyncindex, -1);
                            }
                        } else{
                            UserManager.getInstance().arr_profile_photo_file.set(asyncindex, new File(fileName));
                        }
                        return asyncindex;
                    }
                    catch (MalformedURLException e) {e.printStackTrace();}
                } else {
                    fileName = "";
                    return asyncindex;
                }
            } catch (Exception e) {Log.w(TAG, e);}
            return asyncindex;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            //boolean ret = (Boolean) result;
            if (result>-1) {
                //UserManager.getInstance().arr_profile_photo_bitmap.set(index, BitmapFactory.decodeFile(UserManager.getInstance().arr_profile_photo_file.get(0).getPath()));
                //Toast.makeText(ctx, "down user photo: "+ String.valueOf(asyncindex), Toast.LENGTH_LONG).show();

                //sns_fileName = fileName;
                //connectServer(m_strUserType, m_strUserid, m_strUserName, fileName);
            } else {
                //Toast.makeText(cont,
                //        "fetch error user photo", Toast.LENGTH_LONG).show();
            }
        }
    }
}