package com.hxsn.witwork;

import android.app.Application;
import android.content.Context;

import com.andbase.ssk.utils.LogUtil;
import com.hxsn.witwork.ui.Shared;
import com.hxsn.witwork.utils.Const;
import com.hxsn.witwork.utils.camera.Configura;
import com.senter.support.openapi.StUhf;


/**
 * 作者：jiely on 2017/5/17 10:54
 * 邮箱：songlj@fweb.cn
 */
public class TApplication extends Application{

    public static Context context;
    public static TApplication instance;
    public static int versionType = Const.TEST_VERTION;  //0-text 1-debug, 2-release
    public static String URL_STRING;//农事汇url,在另一个IP下

    /**
     * RFID初始化
     * */
    private static StUhf rfid;
    private static Configura mAppConfiguration;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Shared.init(this);
        switch (versionType) {
            case Const.TEST_VERTION:
                LogUtil.openLog();
                URL_STRING = "http://192.168.12.171:7480/sn_zhgl";

                break;
            case Const.TEST_VERTION1:
                URL_STRING = "http://60.10.69.100:8889/ssnc";

                break;
            case Const.DEBUG_VERTION:
                URL_STRING = "http://60.10.69.100:8889/ssnc";

                break;

            case Const.RELEASE_VERTION:
                LogUtil.closeLog();
                URL_STRING = "http://60.10.69.100:8889/ssnc";

                break;
        }
    }

    public static boolean stop() {
        if (rfid != null) {
            if (rfid.isFunctionSupported(StUhf.Function.StopOperation)) {
                for (int i = 0; i < 3; i++) {
                    if (rfid.stopOperation()) {
                        return true;
                    }
                }
                return false;
            }
        }
        return true;
    }

    private static final StUhf.InterrogatorModel getSavedModel() {
        String modelName=getConfiguration().getString("modelName", "");
        if (modelName.length()!=0)
        {
            return StUhf.InterrogatorModel.valueOf(modelName);
        }
        return null;
    }


    private static final Configura getConfiguration()
    {
        if (mAppConfiguration==null)
        {
            mAppConfiguration=new Configura(context, "settings", Context.MODE_PRIVATE);
        }
        return mAppConfiguration;
    }
    /**
     * 初始化时用以生成超高频对象，以后就可以直接用rfid()来调用了。
     */
    public static StUhf getRfid() {
        if (rfid == null) {
            StUhf rf = null;
            if (getSavedModel()==null) {
                try{
                    rf = StUhf.getUhfInstance();//InterrogatorModel.InterrogatorModelD2
                }catch(Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }else {
                rf=StUhf.getUhfInstance(getSavedModel());
            }

            if(rf == null) return null;
            boolean b = rf.init();
            if (b == false) {
                return null;
            }
            rfid = rf;
            StUhf.InterrogatorModel model=rfid.getInterrogatorModel();
            saveModelName(model);
        }
        return rfid;
    }

    private static final void saveModelName(StUhf.InterrogatorModel model) {
        if (model==null) {
            throw new NullPointerException();
        }
        getConfiguration().setString("modelName", model.name());
    }

}
