package com.baidu_lishuang10.aidlexe;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by baidu_lishuang10 on 15/11/20.
 */
public class BookManagerService extends Service {
    private static final String TAG = "BMS";
    private AtomicBoolean mIsServiceDestroyed = new AtomicBoolean(false);
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();//自动进程同步处理
    private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<>();//专门用于删除跨进程接口的类


    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(0, "Android"));
        mBookList.add(new Book(1, "iOS"));
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (!mIsServiceDestroyed.get()) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int bookId = mBookList.size();
                    Book newBook = new Book(bookId, "Service:#new Book#" + bookId);
                    onNewBookArrived(newBook);
                }
            }
        }.start();


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    private Binder mBinder = new IBookManager.Stub() {

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.register(listener);
        }

        @Override
        public void unRegisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.unregister(listener);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsServiceDestroyed.set(true);
    }


    private void onNewBookArrived(Book newBook) {
        mBookList.add(newBook);
        Log.i(TAG, "Service:onNewBookArrived beginBroadcast.");
        final int N = mListenerList.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IOnNewBookArrivedListener listener = mListenerList.getBroadcastItem(i);
            try {
                if (listener != null)
                    listener.OnNewBookArrived(newBook);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mListenerList.finishBroadcast();
    }
}
