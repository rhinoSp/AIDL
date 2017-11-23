// PersonManager.aidl
package com.rhino.aidlclient;


import com.rhino.aidlclient.Person;


/**
* 当使用PersonManager调用方法时，其实是调用了本地com.rhino.aidlclient.PersonManager.Stub.Proxy对象的方法，
* 从Proxy方法中可以看到，每个方法都执行了mRemote.transact(Stub.TRANSACTION_xxx, _data, _reply, 0);。
* 这一过程是把Client端的参数转换成Parcel（_data）传递到Server端，而在Server端又会把返回数据保存到_reply中，这就形成了一次交互。
*
* mRemote是远程对象，transact方法会执行onTransact方法
**/
interface PersonManager {

    /*
    * 接口的方法前面不能加public，private，protected等，也不能用final，static
    */
    List<Person> getPersons();
    int getPersonCount();

    /*
     * 传参时除了Java基本类型以及String，CharSequence之外的类型
     * 都需要在前面加上定向tag，具体加什么量需而定
     */
    void setPersonPrice(String name, int price);

    /*
     * "in" 服务端将会接收到一个那个对象的完整数据
     * 但是客户端的那个对象不会因为服务端对传参的修改而发生变动。
     */
    void addPersonIn(in Person person);

    /*
     * "out" 服务端将会接收到那个对象的参数为空的对象
     *  但是在服务端对接收到的空对象有任何修改之后客户端将会同步变动。
     */
    void addPersonOut(out Person person);

    /*
     * "inout" 服务端将会接收到客户端传来对象的完整信息
     * 并且客户端将会同步服务端对该对象的任何变动。（与java一样）
     */
    void addPersonInout(inout Person person);

}
