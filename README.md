# AIDLDemo
一个关于自己对aidl的理解用于解决内存和进程间通讯问题

## 简单的实现了android应用中的跨进程通讯


#### 多进程app可以在系统中申请多份内存，但应合理使用，建议把一些高消耗但不常用的模块放到独立的进程，不使用的进程可及时手动关闭；

#### 实现多进程的方式有多种：四大组件传递Bundle、Messenger、AIDL等，选择适合自己的使用场景；

#### Android中实现多进程通讯，建议使用系统提供的Binder类，该类已经实现了多进程通讯而不需要我们做底层工作；

#### 多进程应用，Application将会被创建多次；

