package mydragonsland.com.voicecontrol;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.widget.ListView;

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
public class VoiceControl extends Activity {
    private int mCurrentSystemVersion = 0; //当前系统版本
    private static final int SYSTEM_VERSION_ANDROID23 = 100;
    private static final int SYSTEM_VERSION_ANDROID40 = 101;
    private ListView mList;
    private static final String TAG = "VoiceControl";

    private int mBindFlag;
    private Messenger mServiceMessenger;

    /**
     * Called with the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weakUpScreen();

        // Inflate our UI from its XML layout description.
        setContentView(R.layout.activity_widgets);

        Intent service = new Intent(getApplicationContext(), VoiceCommandService.class);
        getApplicationContext().startService(service);
        mBindFlag = Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH ? 0 : Context.BIND_ABOVE_CLIENT;

//
//        //判断系统软件的版本，是Android2.3还是Android 4.x
//        String mSystemVersion = android.os.Build.VERSION.RELEASE;
//        Log.d(TAG, "---->>onCreate():info:" + android.os.Build.VERSION.SDK + "," + android.os.Build.VERSION.RELEASE);
//        //android.os.Build.VERSION_CODES;
//        if (mSystemVersion.contains("2.3")) {
//            mCurrentSystemVersion = SYSTEM_VERSION_ANDROID23;
//        } else if (mSystemVersion.contains("4.")) {
//            mCurrentSystemVersion = SYSTEM_VERSION_ANDROID40;
//        } else {
//            mCurrentSystemVersion = SYSTEM_VERSION_ANDROID40;
//        }
//
//        // Get display items for later interaction
//        Button speakButton = (Button) findViewById(R.id.btn_speak);
//
//        mList = (ListView) findViewById(R.id.list);
//
//        /**
//         * 下面是判断当前手机是否支持语音识别功能
//         */
//        // Check to see if a recognition activity is present
//        PackageManager pm = getPackageManager();
//        List<ResolveInfo> activities = pm.queryIntentActivities(
//                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0); //本地识别程序 ,通过全局包管理器及特定intent，查找系统是否有语音识别的服务程序
//        //new Intent(RecognizerIntent.ACTION_WEB_SEARCH), 0); // 网络识别程序
////        if (activities.size() != 0) {
////            speakButton.setOnClickListener(this);//如果存在该activity
////        } else {
////            speakButton.setEnabled(false); //否则将BUTTON显示值修改，并设置成不可选
////            speakButton.setText("Recognizer not present");
////        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        bindService(new Intent(this, VoiceCommandService.class), mServiceConnection, mBindFlag);
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        if (mServiceMessenger != null)
        {
            unbindService(mServiceConnection);
            mServiceMessenger = null;
        }
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {

            mServiceMessenger = new Messenger(service);
            Message msg = new Message();
            msg.what = VoiceCommandService.MSG_RECOGNIZER_START_LISTENING;

            try
            {
                mServiceMessenger.send(msg);
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            mServiceMessenger = null;
        }

    }; // mServiceConnection

    private void weakUpScreen() {
        PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wakeLock.acquire();
    }
}
