// IFloatViewAidlInterface.aidl
package com.example.accessibility;

// Declare any non-default types here with import statements

interface IFloatViewAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void displayView();
    void dismissView();
    int getX();
    int getY();
}
