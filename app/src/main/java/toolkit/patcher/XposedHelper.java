package toolkit.patcher;

import android.util.Log;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import dalvik.system.DexFile;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class XposedHelper {

    public static void findAndHookMethod(String className, ClassLoader classLoader, String methodName, Object... parameterTypesAndCallback) {
        try {
            if (findClass(className, classLoader) != null) {
                XposedHelpers.findAndHookMethod(className, classLoader, methodName, parameterTypesAndCallback);
            }
        } catch (Throwable e) {
            if (BuildConfig.DEBUG)
                XposedBridge.log("E/" + MainHook.TAG + " " + Log.getStackTraceString(e));
        }
    }

    public static void findAndHookMethod(Class<?> clazz, String methodName, Object... parameterTypesAndCallback) {
        try {
            if (clazz != null) {
                XposedHelpers.findAndHookMethod(clazz, methodName, parameterTypesAndCallback);
            }
        } catch (Throwable e) {
            if (BuildConfig.DEBUG)
                XposedBridge.log("E/" + MainHook.TAG + " " + Log.getStackTraceString(e));
        }
    }

    public static void hookAllMethods(String className, ClassLoader classLoader, String methodName, XC_MethodHook callback) {
        try {
            Class<?> packageParser = findClass(className, classLoader);
            XposedBridge.hookAllMethods(packageParser, methodName, callback);
        } catch (Throwable e) {
            if (BuildConfig.DEBUG)
                XposedBridge.log("E/" + MainHook.TAG + " " + Log.getStackTraceString(e));
        }
    }

    public void hookAllMethods(Class<?> hookClass, String methodName, XC_MethodHook callback) {
        try {
            XposedBridge.hookAllMethods(hookClass, methodName, callback);
        } catch (Throwable e) {
            if (BuildConfig.DEBUG)
                XposedBridge.log("E/" + MainHook.TAG + " " + Log.getStackTraceString(e));
        }
    }

    public static Class<?> findClass(String className, ClassLoader classLoader) {
        try {
            return Class.forName(className, false, classLoader);
        } catch (Throwable e) {
            if (BuildConfig.DEBUG)
                XposedBridge.log("E/" + MainHook.TAG + " " + Log.getStackTraceString(e));
        }
        return null;
    }

    public static void hookAllConstructors(String className, XC_MethodHook callback) {
        try {
            Class<?> packageParser = findClass(className, null);
            XposedBridge.hookAllConstructors(packageParser, callback);
        } catch (Throwable e) {
            if (BuildConfig.DEBUG)
                XposedBridge.log("E/" + MainHook.TAG + " " + Log.getStackTraceString(e));
        }
    }
    public static Set<Class<?>> findClassesByPrefix(String prefix, ClassLoader classLoader) {
        Set<Class<?>> classes = new HashSet<>();
        try {
            Object pathList = XposedHelpers.getObjectField(classLoader, "pathList");
            Object[] dexElements = (Object[]) XposedHelpers.getObjectField(pathList, "dexElements");
            for (Object dexElement : dexElements) {
                DexFile dexFile = (DexFile) XposedHelpers.getObjectField(dexElement, "dexFile");
                Enumeration<String> entries = dexFile.entries();
                while (entries.hasMoreElements()) {
                    String className = entries.nextElement();
                    if (className.startsWith(prefix)) {
                        classes.add(Class.forName(className, false, classLoader));
                    }
                }
            }
        } catch (Throwable e) {
            if (BuildConfig.DEBUG)
                XposedBridge.log("E/" + MainHook.TAG + " " + Log.getStackTraceString(e));
        }
        return classes;
    }
}