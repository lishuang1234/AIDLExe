package com.baidu_lishuang10.aidlexe;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    public static final int MSG_NEW_BOOK = 1;

    public static final String TAG = "MainActivity";
    private IBookManager mRemote;
    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_NEW_BOOK:
                    Log.i(TAG, "Client:new book is : " + msg.obj);
                    break;
            }
        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IBookManager bookManager = IBookManager.Stub.asInterface(service);
            mRemote = bookManager;
            try {
                List<Book> bookList = bookManager.getBookList();
                Log.i(TAG, "Client:query book list is : " + bookList);
                bookManager.registerListener(listener);//注册接口
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private IOnNewBookArrivedListener listener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void OnNewBookArrived(Book newBook) throws RemoteException {
            mHandler.obtainMessage(MSG_NEW_BOOK, newBook).sendToTarget();//到UI线程中处理
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindService(new Intent(this, BookManagerService.class), connection, BIND_AUTO_CREATE);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        if (mRemote != null && mRemote.asBinder().isBinderAlive()) {
            try {
                mRemote.unRegisterListener(listener);//注销接口,按正常注销会出现问题，无法注销
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
