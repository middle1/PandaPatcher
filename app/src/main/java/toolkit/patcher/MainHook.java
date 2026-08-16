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
    private static final long EXPIRE_FAR_FUTURE = 4070908800000L;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.panda.mouse") && !lpparam.packageName.equals("com.panda.gamepad")) {
            return;
        }

        Class<?> mainClass = XposedHelpers.findClassIfExists("com.chaozhuo.gameassistant.XApp", lpparam.classLoader);

        hookStartupToast(mainClass);
        hookLicenseChecker(lpparam);
        hookSharedPreferences(lpparam);
    }

    private void hookStartupToast(Class<?> mainClass) {
        if (mainClass == null) return;

        XposedHelpers.findAndHookMethod(mainClass, "onCreate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Application app = (Application) param.thisObject;
                if (app != null) {
                    Toast.makeText(app, "Moded by @Middle1221\nFeatures: License bypass", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void hookLicenseChecker(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            Set<Class<?>> classes = XposedHelper.findClassesByPrefix("com.chaozhuo.gameassistant.LicenseCheckerActivity$", lpparam.classLoader);
            for (Class<?> clazz : classes) {
                XposedHelpers.findAndHookMethod(clazz, "dontAllow", int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedHelpers.callMethod(param.thisObject, "allow", param.args[0]);
                        param.setResult(null);
                    }
                });
            }
        } catch (Throwable ignored) {
        }
    }

    private void hookSharedPreferences(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            Class<?> spImplClass = XposedHelpers.findClassIfExists("android.app.SharedPreferencesImpl", lpparam.classLoader);
            if (spImplClass == null) {
                spImplClass = XposedHelpers.findClass("android.app.SharedPreferencesImpl", null);
            }

            XposedHelpers.findAndHookMethod(spImplClass, "getBoolean", String.class, boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    String key = (String) param.args[0];
                    if (key != null && (key.equals("subscription_active") || key.equals("KEY_MACRO_SETTLED"))) {
                        param.setResult(true);
                    }
                }
            });

            XposedHelpers.findAndHookMethod(spImplClass, "getLong", String.class, long.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    String key = (String) param.args[0];
                    if (key != null) {
                        if (key.equals("pro_plus_trial_expires_at")) {
                            param.setResult(EXPIRE_FAR_FUTURE);
                        } else if (key.equals("subscription_verified_at") || key.equals("KEY_MACRO_SETTLED_TIMESTAMP")) {
                            param.setResult(System.currentTimeMillis());
                        }
                    }
                }
            });

            Class<?> editorImplClass = XposedHelpers.findClassIfExists("android.app.SharedPreferencesImpl$EditorImpl", lpparam.classLoader);
            if (editorImplClass == null) {
                editorImplClass = XposedHelpers.findClassIfExists("android.app.SharedPreferencesImpl$EditorImpl", null);
            }

            if (editorImplClass != null) {
                XposedHelpers.findAndHookMethod(editorImplClass, "putBoolean", String.class, boolean.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String key = (String) param.args[0];
                        boolean value = (boolean) param.args[1];
                        if (!value && key != null && (key.equals("subscription_active") || key.equals("KEY_MACRO_SETTLED"))) {
                            param.args[1] = true;
                        }
                    }
                });
            }
        } catch (Throwable ignored) {
        }
    }

    @Override
    public void initZygote(StartupParam startupParam) {
    }
}