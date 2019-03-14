package com.example.audioplayer.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by tanfujun on 15-10-19.
 */
public class ClassInfo {

    Class<?> mClass;
    HashMap<String, Method> methods;
    HashMap<String, Field> fields;

    public ClassInfo(Class<?> clazz, String className) {
        methods = new HashMap<String, Method>();
        fields = new HashMap<String, Field>();
        mClass = clazz;
    }

    public void addCachedMethod(String key, Method method) {
        methods.put(key, method);
    }

    public Method getCachedMethod(String key) {
        return methods.get(key);
    }

    public void addCachedField(String key, Field field) {
        fields.put(key, field);
    }

    public Field getCachedField(String key) {
        return fields.get(key);
    }
}
