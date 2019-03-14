package com.example.audioplayer.reflect;

import java.lang.reflect.Method;

/**
 * Created by wenrronglin on 12/5/17.
 */

public class SystemProperties {
    private static final Class cls = CompatUtils.getClass("android.os.SystemProperties");
    private static final Method get = CompatUtils.getMethod(cls, "get", String.class);
    private static final Method get2 = CompatUtils.getMethod(cls, "get", String.class, String.class);
    private static final Method getBoolean = CompatUtils.getMethod(cls, "getBoolean", String.class, boolean.class);
    private static final Method getInt = CompatUtils.getMethod(cls, "getInt", String.class, int.class);
    private static final Method set = CompatUtils.getMethod(cls, "set", String.class, String.class);

    public static String get(String key) {
        return (String) CompatUtils.invoke(cls, "", get, key);
    }

    public static String get(String key, String def) {
        return (String) CompatUtils.invoke(cls, def, get2, key, def);
    }

    public static int getInt(String key, int def) {
        return (int) CompatUtils.invoke(cls, def, getInt, key);
    }

    public static boolean getBoolean(String key, boolean def) {
        return (boolean) CompatUtils.invoke(cls, def, getBoolean, key);
    }

    public static void set(String key, String val) {
        CompatUtils.invoke(cls, null, set, key, val);
    }
}
