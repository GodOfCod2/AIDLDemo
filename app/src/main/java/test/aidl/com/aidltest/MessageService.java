package test.aidl.com.aidltest;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import test.aidl.com.aidltest.data.MessageModel;

/**
 * Created by sk on 2017/8/15.
 */

public class MessageService extends Service {
    private static final String TAG = "MessageService";
    private AtomicBoolean serviceStop = new AtomicBoolean(false);
    private RemoteCallbackList<MessageReceiver> listenerList = new RemoteCallbackList<>();
    public MessageService(){

    }

    @Override
    public IBinder onBind(Intent intent) {
        if(checkCallingOrSelfPermission("com.example.aidl.permission.REMOTE_SERVICE_PERMISSION")== PackageManager.PERMISSION_DENIED){
            return null;
        }
        return messageSender;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new FakeTCPTack()).start();
    }

    @Override
    public void onDestroy() {
        serviceStop.set(true);
        super.onDestroy();
    }

    private class FakeTCPTack implements Runnable{
        @Override
        public void run() {
            while (!serviceStop.get()){
                try {
                    Thread.sleep(5000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                MessageModel messageModel = new MessageModel();
                messageModel.setFrom("Service");
                messageModel.setTo("Client");
                messageModel.setContent(String.valueOf(System.currentTimeMillis()));

                final int listenerCount = listenerList.beginBroadcast();
                Log.d(TAG,"listererCount == " + listenerCount);
                for(int i = 0;i<listenerCount;i++){
                    MessageReceiver messageReceiver = listenerList.getBroadcastItem(i);
                    if(messageReceiver!=null){
                        try {
                            messageReceiver.onMessageReceived(messageModel);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
                listenerList.finishBroadcast();
            }
        }
    }

    IBinder messageSender = new MessageSender.Stub(){
        @Override
        public void sendMessage(MessageModel messageModel) throws RemoteException {
            Log.d(TAG,messageModel.getContent());
        }

        @Override
        public void registerReceiveListener(MessageReceiver messageReceiver) throws RemoteException {
            listenerList.register(messageReceiver);
        }

        @Override
        public void unregisterReceiveListener(MessageReceiver messageReceiver) throws RemoteException {
            listenerList.unregister(messageReceiver);
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String packageName = null;
            String packagers[] = getPackageManager().getPackagesForUid(getCallingUid());
            if(packagers!=null&&packagers.length>0){
                packageName = packagers[0];
            }
            Log.d(TAG,packageName);
            if(packageName == null ||!packageName.startsWith("test.aidl.com.aidltest")){
                Log.d(TAG,"拒绝调用");
                return false;
            }
            return super.onTransact(code, data, reply, flags);
        }
    };
}
