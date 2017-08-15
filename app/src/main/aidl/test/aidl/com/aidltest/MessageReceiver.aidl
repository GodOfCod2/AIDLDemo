// MessageReceiver.aidl
package test.aidl.com.aidltest;

// Declare any non-default types here with import statements
import test.aidl.com.aidltest.data.MessageModel;
interface MessageReceiver {
    void onMessageReceived(in MessageModel receiveMessage);
}
