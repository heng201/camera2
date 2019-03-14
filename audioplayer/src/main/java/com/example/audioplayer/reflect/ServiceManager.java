package com.example.audioplayer.reflect;

import java.lang.reflect.Method;

/**
 * Created by wenrronglin on 12/5/17.
 */

public class ServiceManager {
    private static final Class cls = CompatUtils.getClass("android.os.ServiceManager");
    private static final Method getService = CompatUtils.getMethod(cls, "getService", String.class);

    public static Object getService(String name) {
        return CompatUtils.invoke(cls, null, getService, name);
    }
}
