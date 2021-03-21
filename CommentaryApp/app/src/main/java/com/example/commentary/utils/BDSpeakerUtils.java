package com.example.commentary.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.example.commentary.R;
import com.example.commentary.control.InitConfig;
import com.example.commentary.listener.UiMessageListener;
import com.example.commentary.util.Auth;
import com.example.commentary.util.AutoCheck;
import com.example.commentary.util.FileUtil;
import com.example.commentary.util.IOfflineResourceConst;

import java.util.HashMap;
import java.util.Map;

import static com.example.commentary.util.IOfflineResourceConst.DEFAULT_SDK_TTS_MODE;
import static com.example.commentary.util.IOfflineResourceConst.PARAM_SN_NAME;
import static com.example.commentary.util.IOfflineResourceConst.TEXT_MODEL;
import static com.example.commentary.util.IOfflineResourceConst.VOICE_MALE_MODEL;

public class BDSpeakerUtils {
    // ================== 精简版初始化参数设置开始 ==========================
    /**
     * 发布时请替换成自己申请的appId appKey 和 secretKey。注意如果需要离线合成功能,请在您申请的应用中填写包名。
     * 本demo的包名是com.baidu.tts.sample，定义在build.gradle中。
     */
    protected String appId;

    protected String appKey;

    protected String secretKey;

    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    private TtsMode ttsMode = DEFAULT_SDK_TTS_MODE;

    private boolean isOnlineSDK = TtsMode.ONLINE.equals(DEFAULT_SDK_TTS_MODE);
    // ================ 纯离线sdk或者选择TtsMode.ONLINE  以下参数无用;
    private static final String TEMP_DIR = "/sdcard/baiduTTS"; // 重要！请手动将assets目录下的3个dat 文件复制到该目录

    // 请确保该PATH下有这个文件
    private static final String TEXT_FILENAME = TEMP_DIR + "/" + TEXT_MODEL;

    // 请确保该PATH下有这个文件 ，m15是离线男声
    private static final String MODEL_FILENAME = TEMP_DIR + "/" + VOICE_MALE_MODEL;

    // ===============初始化参数设置完毕，更多合成参数请至getParams()方法中设置 =================

    protected SpeechSynthesizer mSpeechSynthesizer;

    // =========== 以下为UI部分 ==================================================

    protected Handler mainHandler;

    private String desc; // 说明文件


    private static final String TAG = "MiniActivity";

    protected Context context;


    public BDSpeakerUtils(Context context){
        this.context = context;
        //初始化配置
        initConfig();
        initTTs();
    }

    public void initConfig(){
        appId = Auth.getInstance(context).getAppId();
        appKey = Auth.getInstance(context).getAppKey();
        secretKey = Auth.getInstance(context).getSecretKey();
        desc = FileUtil.getResourceText(context, R.raw.mini_activity_description);
        mainHandler = new Handler() {
            /*
             * @param msg
             */
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.obj != null) {
                    print(msg.obj.toString());
                }
            }
        };
    }

/*    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
        appId = Auth.getInstance(this).getAppId();
        appKey = Auth.getInstance(this).getAppKey();
        secretKey = Auth.getInstance(this).getSecretKey();
//        sn = Auth.getInstance(this).getSn(); // 纯离线合成必须有此参数；离在线合成SDK没有此参数
        desc = FileUtil.getResourceText(this, R.raw.mini_activity_description);
//        setContentView(R.layout.activity_mini);
//        initView();
//        initPermission();
        initTTs();
    }*/

    /**
     * 注意此处为了说明流程，故意在UI线程中调用。
     * 实际集成中，该方法一定在新线程中调用，并且该线程不能结束。具体可以参考NonBlockSyntherizer的写法
     */
    public void initTTs() {
        /*HandlerThread handlerThread = new HandlerThread("BDSpeaker-thread");
        handlerThread.start();
        Handler tHandler = new Handler(handlerThread.getLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

            }
        };*/
        new Thread(()->{

            LoggerProxy.printable(true); // 日志打印在logcat中

            // 日志更新在UI中，可以换成MessageListener，在logcat中查看日志
            SpeechSynthesizerListener listener = new UiMessageListener(mainHandler);

            // 1. 获取实例
            mSpeechSynthesizer = SpeechSynthesizer.getInstance();
            mSpeechSynthesizer.setContext(context);

            // 2. 设置listener
            mSpeechSynthesizer.setSpeechSynthesizerListener(listener);

            // 3. 设置appId，appKey.secretKey
            int result = mSpeechSynthesizer.setAppId(appId);
            checkResult(result, "setAppId");
            result = mSpeechSynthesizer.setApiKey(appKey, secretKey);
            checkResult(result, "setApiKey");

            // 4. 如果是纯离线SDK需要离线功能的话
        /*if (!isOnlineSDK) {
            // 文本模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TEXT_FILENAME);
            // 声学模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, MODEL_FILENAME);

            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
            // 该参数设置为TtsMode.MIX生效。
            // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
            // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
            // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
            // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线



        }*/

            // 5. 以下setParam 参数选填。不填写则默认值生效
            // 设置在线发声音人： 0 普通女声（默认） 1 普通男声  3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
            // 设置合成的音量，0-15 ，默认 5
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "9");
            // 设置合成的语速，0-15 ，默认 5
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
            // 设置合成的语调，0-15 ，默认 5
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");

            // mSpeechSynthesizer.setAudioStreamType(AudioManager.MODE_IN_CALL); // 调整音频输出

            // x. 额外 ： 自动so文件是否复制正确及上面设置的参数
            /*Map<String, String> params = new HashMap<>();
            // 复制下上面的 mSpeechSynthesizer.setParam参数
            // 上线时请删除AutoCheck的调用
            if (!isOnlineSDK) {
                params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TEXT_FILENAME);
                params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, MODEL_FILENAME);
            }

            // 检测参数，通过一次后可以去除，出问题再打开debug
            InitConfig initConfig = new InitConfig(appId, appKey, secretKey, ttsMode, params, listener);
            AutoCheck.getInstance(context).check(initConfig, new Handler() {
                @Override
                *//**
                 * 开新线程检查，成功后回调
                 *//*
                public void handleMessage(Message msg) {
                    if (msg.what == 100) {
                        AutoCheck autoCheck = (AutoCheck) msg.obj;
                        synchronized (autoCheck) {
                            String message = autoCheck.obtainDebugMessage();
                            print(message); // 可以用下面一行替代，在logcat中查看代码
                            // Log.w("AutoCheckMessage", message);
                        }
                    }
                }

            });
*/
            // 6. 初始化
            result = mSpeechSynthesizer.initTts(ttsMode);
            checkResult(result, "initTts");
        }).start();
    }

    public void speak(String RequestSpeaker) {
        /* 以下参数每次合成时都可以修改
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
         *  设置在线发声音人： 0 普通女声（默认） 1 普通男声  3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "5"); 设置合成的音量，0-9 ，默认 5
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5"); 设置合成的语速，0-9 ，默认 5
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5"); 设置合成的语调，0-9 ，默认 5
         *
         */

        if (mSpeechSynthesizer == null) {
            print("[ERROR], 初始化失败");
            return;
        }
        int result = mSpeechSynthesizer.speak(RequestSpeaker);
        print("合成并播放 按钮已经点击");
        checkResult(result, "speak");
    }

    public void stop() {
        print("停止合成引擎 按钮已经点击");
        int result = mSpeechSynthesizer.stop();
        checkResult(result, "stop");
    }

    private void print(String message) {
        Log.i(TAG, message);
    }

    public void onDestroy() {
        if (mSpeechSynthesizer != null) {
            mSpeechSynthesizer.stop();
            mSpeechSynthesizer.release();
            mSpeechSynthesizer = null;
            print("释放资源成功");
        }
    }

    private void checkResult(int result, String method) {
        if (result != 0) {
            print("error code :" + result + " method:" + method);
        }
    }
}
