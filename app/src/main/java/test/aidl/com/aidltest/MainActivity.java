package test.aidl.com.aidltest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import test.aidl.com.aidltest.data.MessageModel;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private MessageSender messageSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupService();
    }

    /**
     * bindSErvice & startService
     */
    private void setupService() {
        Intent intent = new Intent(this,MessageService.class);
        bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        if(messageSender!=null&&messageSender.asBinder().isBinderAlive()){
            try {
                messageSender.unregisterReceiveListener(messageReceiver);
            }catch (RemoteException e){
                e.printStackTrace();
            }
        }
        unbindService(serviceConnection);
        super.onDestroy();
    }
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"onServiceConnected");
            messageSender = MessageSender.Stub.asInterface(service);
            MessageModel messageModel = new MessageModel();
            messageModel.setFrom("client user id");
            messageModel.setTo("receiver user id");
            messageModel.setContent("This is message content");

            try {
                messageSender.registerReceiveListener(messageReceiver);
                messageSender.sendMessage(messageModel);
                messageSender.asBinder().linkToDeath(deathRecipient,0);
            }catch (RemoteException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG,"onServiceDisconnected");
        }
    };
    private MessageReceiver messageReceiver = new MessageReceiver.Stub() {
        @Override
        public void onMessageReceived(MessageModel receiveMessage) throws RemoteException {
            Log.d(TAG,"onMessageReceived"+receiveMessage.getContent());
        }
    };

    IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient(){

        @Override
        public void binderDied() {
            Log.d(TAG,"binderDied");
            if(messageSender != null){
                messageSender.asBinder().unlinkToDeath(this,0);
                messageSender = null;
            }
            setupService();
        }
    };
}
