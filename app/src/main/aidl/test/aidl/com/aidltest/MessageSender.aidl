// MessageSender.aidl
package test.aidl.com.aidltest;

import test.aidl.com.aidltest.data.MessageModel;
import test.aidl.com.aidltest.MessageReceiver;
interface MessageSender{
  void sendMessage(in MessageModel messageModel);

  void registerReceiveListener(MessageReceiver messageReceiver);

   void unregisterReceiveListener(MessageReceiver messageReceiver);
}
