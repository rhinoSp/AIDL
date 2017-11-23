package com.rhino.aidlclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by LuoLin on 2017/10/16.
 **/
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AIDL_DEBUG";

    /** 由AIDL文件生成的Java类 **/
    private PersonManager mPersonManager = null;
    /** 标志当前与服务端连接状况的布尔值，false为未连接，true为连接中 **/
    private boolean mIsBound = false;

    private TextView mTvLog;


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            putLog("MainActivity：Service connected");
            // 将服务端的Binder对象转换为客户端所需要的接口对象，
            // 该过程区分进程，如果进程一样，就返回服务端Stub对象本身，否则呢就返回封装后的Stub.Proxy对象。
            mPersonManager = PersonManager.Stub.asInterface(service);
            mIsBound = true;
            // 连接服务成功，打印服务端数据
            printServerData();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            putLog("MainActivity：Service disconnected");
            mIsBound = false;
        }
    };

    /**
     * 尝试与服务端建立连接
     */
    private void attemptToBindService() {
        Intent intent = new Intent();
        intent.setAction("com.rhino.aidl.test");
        intent.setPackage("com.rhino.aidlserver");
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvLog = (TextView) findViewById(R.id.log);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mIsBound) {
            attemptToBindService();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mIsBound) {
            unbindService(mServiceConnection);
            mIsBound = false;
        }
    }

    public void addPersonIn(View view) {
        //如果与服务端的连接处于未连接状态，则尝试连接
        if (!mIsBound) {
            attemptToBindService();
            Toast.makeText(this, "当前与服务端处于未连接状态，正在尝试重连，请稍后再试", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Person person = new Person(111, "张三 in(客户端)", 31);
            mPersonManager.addPersonIn(person);
            putLog("-----------客户端添加-----------\n" + person.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addPersonOut(View view) {
        //如果与服务端的连接处于未连接状态，则尝试连接
        if (!mIsBound) {
            attemptToBindService();
            Toast.makeText(this, "当前与服务端处于未连接状态，正在尝试重连，请稍后再试", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Person person = new Person(222, "李四 out(客户端)", 28);
            mPersonManager.addPersonOut(person);
            putLog("-----------客户端添加-----------\n" + person.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addPersonInOut(View view) {
        //如果与服务端的连接处于未连接状态，则尝试连接
        if (!mIsBound) {
            attemptToBindService();
            Toast.makeText(this, "当前与服务端处于未连接状态，正在尝试重连，请稍后再试", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Person person = new Person(333, "王五 inout(客户端)", 26);
            mPersonManager.addPersonInout(person);
            putLog("-----------客户端添加-----------\n" + person.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPerson(View view) {
        //如果与服务端的连接处于未连接状态，则尝试连接
        if (!mIsBound) {
            attemptToBindService();
            Toast.makeText(this, "当前与服务端处于未连接状态，正在尝试重连，请稍后再试", Toast.LENGTH_SHORT).show();
            return;
        }
        printServerData();
    }

    private void putLog(String log){
        if(null != mTvLog){
            mTvLog.setText(mTvLog.getText().toString()+"\n"+log);
        }
        Log.e(TAG, log);
    }

    private void printServerData(){
        if (mPersonManager != null) {
            try {
                List<Person> mPersons = mPersonManager.getPersons();
                StringBuilder sb = new StringBuilder();
                for(Person b : mPersons){
                    if(!sb.toString().isEmpty()){
                        sb.append("\n");
                    }
                    sb.append(b.toString());
                }
                sb.insert(0, "-----------服务端数据-----------\n");
                putLog(sb.toString());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
