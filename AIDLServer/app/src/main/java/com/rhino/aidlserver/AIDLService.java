package com.rhino.aidlserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.rhino.aidlclient.Person;
import com.rhino.aidlclient.PersonManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LuoLin on 2017/10/16.
 **/
public class AIDLService extends Service {

    private static final String TAG = "AIDL_DEBUG";

    //包含Person对象的list
    private List<Person> mPersons = new ArrayList<>();

    //由AIDL文件生成的PersonManager
    private final PersonManager.Stub mPersonManager = new PersonManager.Stub() {
        @Override
        public List<Person> getPersons() throws RemoteException {
            synchronized (this) {
                Log.e(TAG, "invoking getPersons() method , now the list is : " + mPersons.toString());
                if (mPersons != null) {
                    return mPersons;
                }
                return new ArrayList<>();
            }
        }

        @Override
        public int getPersonCount() throws RemoteException {
            return null != mPersons ? mPersons.size() : 0;
        }

        @Override
        public void setPersonPrice(String name, int price) throws RemoteException {
            if(null != mPersons){
                for(Person person : mPersons){
                    if(name.equals(person.getName())){
                        person.setAge(price);
                        break;
                    }
                }
            }
        }

        @Override
        public void addPersonIn(Person person) throws RemoteException {
            synchronized (this) {
                if (person == null) {
                    Log.e(TAG, "Person is null in");
                    person = new Person();
                }
                person.setName("in我被服务端修改");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mPersons.add(person);
                //打印mPersons列表，观察客户端传过来的值
                Log.e(TAG, "invoking addPersonIn() method , now the list is : " + mPersons.toString());
            }
        }

        @Override
        public void addPersonOut(Person person) throws RemoteException {
            synchronized (this) {
                if (person == null) {
                    Log.e(TAG, "Person is null out");
                    person = new Person();
                }
                person.setName("out我被服务端修改");
                mPersons.add(person);
                //打印mPersons列表，观察客户端传过来的值
                Log.e(TAG, "invoking addPersonOut() method , now the list is : " + mPersons.toString());
            }
        }

        @Override
        public void addPersonInout(Person person) throws RemoteException {
            synchronized (this) {
                if (person == null) {
                    Log.e(TAG, "Person is null inout");
                    person = new Person();
                }
                person.setName("inout我被服务端修改");
                mPersons.add(person);
                //打印mPersons列表，观察客户端传过来的值
                Log.e(TAG, "invoking addPersonInout() method , now the list is : " + mPersons.toString());
            }
        }
    };

    @Override
    public void onCreate() {
        // 服务端创建时，添加一些数据
        mPersons.add(new Person(1, "Bob (服务端)", 60));
        mPersons.add(new Person(2, "Joy (服务端)", 100));
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, intent.toString());
        return mPersonManager;
    }
}
