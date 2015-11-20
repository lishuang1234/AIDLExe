// IOnNewBookArrivedListener.aidl
package com.baidu_lishuang10.aidlexe;

// Declare any non-default types here with import statements
import com.baidu_lishuang10.aidlexe.Book;

interface IOnNewBookArrivedListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    void OnNewBookArrived(in Book newBook);
}