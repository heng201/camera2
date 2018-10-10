// IMyAidlInterface.aidl
package com.example.aidlservice;

// Declare any non-default types here with import statements

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
    /**
     * 自己添加的方法
     */
    int add(int value1,int value2);
}
