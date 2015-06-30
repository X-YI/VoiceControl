package mydragonsland.com.voicecontrol;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用intent调用语音识别程序
 * 1.说明
 * 以下例程功能为：在应用程序中使用intent来调出语言识别界面，录音并识别后将识别的字串返回给应用程序。
 * 注意：使用前需要安装语音识别程序如语音搜索。
 * 2.本例参考自android例程：
 * development/samples/ApiDemos/src/com/example/android/apis/app/VoiceRecognition.java
 *
 * @author Administrator
 */
public class VoiceControl extends Activity implements OnClickListener {

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234; //startActivityForResult操作要求的标识码

    private ListView mList;
    private static final String TAG = "VoiceControl";
    private int mCurrentSystemVersion = 0; //当前系统版本
    private static final int SYSTEM_VERSION_ANDROID23 = 100;
    private static final int SYSTEM_VERSION_ANDROID40 = 101;

    private String[] mPackages23 = {"com.cooliris.media",//视频包名
            "com.cooliris.media",//相册应用包名
            "com.android.camera",
            "com.android.music",
            "com.android.deskclock",//桌面时钟包名
            "com.android.browser"};

    private String[] mActivitys23 = {"com.cooliris.media.VideoActivity",
            "com.cooliris.media.Gallery",
            "com.android.camera.Camera",
            "com.android.music.MusicBrowserActivity",
            "com.android.deskclock.DeskClock",
            "com.android.browser.BrowserActivity"};

    private String[] mPackages40 = {"com.android.gallery3d",
            "com.android.gallery3d",
            "com.android.camera",
            "com.android.music",
            "com.android.deskclock",
            "com.android.browser"};

    private String[] mActivitys40 = {"com.android.gallery3d.app.VideoActivity",
            "com.android.gallery3d.app.Gallery",
            "com.android.camera.Camera",
            "com.android.music.MusicBrowserActivity",
            "com.android.deskclock.DeskClock",
            "com.android.browser.BrowserActivity"};
    //预定义控制口令急
    private static final int ACTION_NULL = -1;
    private static final int ACTION_OPEN_VIDEO = 0;
    private static final int ACTION_OPEN_GALLERY = 1;
    private static final int ACTION_OPEN_CAMERA = 2;
    private static final int ACTION_OPEN_MUSIC = 3;
    private static final int ACTION_OPEN_TIME = 4;
    private static final int ACTION_OPEN_BROWSER = 5;

    public int[] mVideoStringId = {R.string.action_video_item1, R.string.action_video_item2,
            R.string.action_video_item3, R.string.action_video_item4};
    public int[] mGalleryStringId = {R.string.action_gallery_item1, R.string.action_gallery_item2,
            R.string.action_gallery_item3, R.string.action_gallery_item4};
    public int[] mCameraStringId = {R.string.action_camera_item1, R.string.action_camera_item2,
            R.string.action_camera_item3, R.string.action_camera_item4, R.string.action_camera_item5};
    public int[] mMusicStringId = {R.string.action_music_item1, R.string.action_music_item2,
            R.string.action_music_item3, R.string.action_music_item4};
    public int[] mTimeStringId = {R.string.action_desktime_item1, R.string.action_desktime_item2,
            R.string.action_desktime_item3, R.string.action_desktime_item4};
    public int[] mBrowserStringId = {R.string.action_browser_item1, R.string.action_browser_item2,
            R.string.action_browser_item3, R.string.action_browser_item4};

    /**
     * Called with the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weakUpScreen();

        // Inflate our UI from its XML layout description.
        setContentView(R.layout.activity_voice_control);
        //判断系统软件的版本，是Android2.3还是Android 4.x
        String mSystemVersion = android.os.Build.VERSION.RELEASE;
        Log.d(TAG, "---->>onCreate():info:" + android.os.Build.VERSION.SDK + "," + android.os.Build.VERSION.RELEASE);
        //android.os.Build.VERSION_CODES;
        if (mSystemVersion.contains("2.3")) {
            mCurrentSystemVersion = SYSTEM_VERSION_ANDROID23;
        } else if (mSystemVersion.contains("4.")) {
            mCurrentSystemVersion = SYSTEM_VERSION_ANDROID40;
        } else {
            mCurrentSystemVersion = SYSTEM_VERSION_ANDROID40;
        }

        // Get display items for later interaction
        Button speakButton = (Button) findViewById(R.id.btn_speak);

        mList = (ListView) findViewById(R.id.list);

        /**
         * 下面是判断当前手机是否支持语音识别功能
         */
        // Check to see if a recognition activity is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0); //本地识别程序 ,通过全局包管理器及特定intent，查找系统是否有语音识别的服务程序
        //new Intent(RecognizerIntent.ACTION_WEB_SEARCH), 0); // 网络识别程序
        if (activities.size() != 0) {
            speakButton.setOnClickListener(this);//如果存在该activity
        } else {
            speakButton.setEnabled(false); //否则将BUTTON显示值修改，并设置成不可选
            speakButton.setText("Recognizer not present");
        }
    }

    /**
     * Handle the click on the start recognition button.
     */
    public void onClick(View v) {
        if (v.getId() == R.id.btn_speak) {
            startVoiceRecognitionActivity(); //OnClickListener中的要override的函数
        }
    }

    /**
     * Fire an intent to start the speech recognition activity.
     */
    private void startVoiceRecognitionActivity() {
        /**
         * 启动手机内置的语言识别功能
         */
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM); //设置为当前手机的语言类型
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition and simple control");//设置语音识别Intent调用的特定属性参数 ,;//出现语言识别界面上面需要显示的提示
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE); //启动一个要求有返回值的activity调用
    }

    /**
     * Handle the results from the recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//该函数非接口内也非抽象函数，为何会Override？
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it could have heard
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            //解析存储识别返回的结果
            mList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, matches));
            //在listview中显示结果
            startSelectApp(recognitionResult(matches));

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private int recognitionResult(ArrayList<String> voiceArrayList) {
        for (String result : voiceArrayList) {
            Log.d(TAG, "---->>result=" + result);
            if (recognitionVedio(result)) {
                return ACTION_OPEN_VIDEO;
            } else if (recognitionGallery(result)) {
                return ACTION_OPEN_GALLERY;
            } else if (recognitionCamera(result)) {
                return ACTION_OPEN_CAMERA;
            } else if (recognitionMusic(result)) {
                return ACTION_OPEN_MUSIC;
            } else if (recognitionTime(result)) {
                return ACTION_OPEN_TIME;
            } else if (recognitionBrowser(result)) {
                return ACTION_OPEN_BROWSER;
            }
        }
        return ACTION_NULL;
    }

    private boolean recognitionVedio(String recognitionString) {
        Log.d(TAG, "---->>heww recognitionVedio():recognitionString=" + recognitionString);
        for (int i = 0; i < mVideoStringId.length; i++) {
            String temp = getResources().getString(mVideoStringId[i]);
            Log.d(TAG, "---->>recognitionVedio():temp=" + temp);
            if (recognitionString.contains(temp)) {
                return true;
            }
        }
        return false;
    }

    private boolean recognitionGallery(String recognitionString) {
        for (int i = 0; i < mGalleryStringId.length; i++) {
            if (recognitionString.contains(getResources().getString(mGalleryStringId[i]))) {
                return true;
            }
        }
        return false;
    }

    private boolean recognitionCamera(String recognitionString) {
        for (int i = 0; i < mCameraStringId.length; i++) {
            if (recognitionString.contains(getResources().getString(mCameraStringId[i]))) {
                return true;
            }
        }
        return false;
    }

    private boolean recognitionMusic(String recognitionString) {
        for (int i = 0; i < mMusicStringId.length; i++) {
            if (recognitionString.contains(getResources().getString(mMusicStringId[i]))) {
                return true;
            }
        }
        return false;
    }

    private boolean recognitionTime(String recognitionString) {
        for (int i = 0; i < mTimeStringId.length; i++) {
            if (recognitionString.contains(getResources().getString(mTimeStringId[i]))) {
                return true;
            }
        }
        return false;
    }

    private boolean recognitionBrowser(String recognitionString) {
        for (int i = 0; i < mBrowserStringId.length; i++) {
            if (recognitionString.contains(getResources().getString(mBrowserStringId[i]))) {
                return true;
            }
        }
        return false;
    }

    public void startSelectApp(int position) {
        Log.d(TAG, "---->>heww position=" + position);
        if (position < 0 || position > 6) {
            return;
        }
        Intent intent = new Intent();
        if (mCurrentSystemVersion == SYSTEM_VERSION_ANDROID23) {
            intent.setClassName(mPackages23[position], mActivitys23[position]);
        } else {
            intent.setClassName(mPackages40[position], mActivitys40[position]);
        }
        startActivity(intent);
    }


    private void weakUpScreen() {
        PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wakeLock.acquire();
    }
}
