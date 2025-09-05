package toolkit.patcher;

import android.app.Application;
import android.widget.Toast;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XposedHelpers;
import java.util.Set;

public class MainHook implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    public static final String TAG = "MyPatch";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.panda.mouse")){
            XposedBridge.log(TAG + " Inited. Package found: "+lpparam.packageName);
            Class<?> main_klass = XposedHelpers.findClassIfExists("com.chaozhuo.gameassistant.XApp", lpparam.classLoader);
            if(main_klass != null){
                XposedHelpers.findAndHookMethod(main_klass, "onCreate", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Application currentApp = (Application) param.thisObject;
                        if(currentApp != null){
                            Toast.makeText(currentApp, "Moded by @Middle1221\nFeatures: License bypass", 1).show();
                        }
                    }
                });
            }
            Set<Class<?>> classes = XposedHelper.findClassesByPrefix("com.chaozhuo.gameassistant.LicenseCheckerActivity$", lpparam.classLoader);
            for (Class<?> clazz : classes) {
                XposedHelper.findAndHookMethod(clazz, "dontAllow", int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log(TAG + " Blocking dontAllow method call with argument: " + param.args[0]);
                        XposedHelpers.callMethod(param.thisObject, "allow", param.args[0]);
                        param.setResult(null);
                    }
                });
            }
        }
    }

    @Override
    public void initZygote(StartupParam startupParam) {
        if (startupParam.startsSystemServer);
    }
}
