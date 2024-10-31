package toolkit.patcher;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XposedHelpers;

public class MainHook implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    public static final String TAG = "MyPatch";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.panda.mouse")){

            Class<?> clazz = XposedHelper.findClass("com.chaozhuo.gameassistant.LicenseCheckerActivity$OooO0OO", lpparam.classLoader);
            XposedHelper.findAndHookMethod(clazz, "dontAllow", int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("Blocking dontAllow method call with argument: " + param.args[0]);
		        XposedHelpers.callMethod(param.thisObject, "allow", param.args[0]);
                param.setResult(null);
                }
            });
        }
    }

    @Override
    public void initZygote(StartupParam startupParam) {
        if (startupParam.startsSystemServer);
    }
}