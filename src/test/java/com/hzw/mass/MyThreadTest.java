package com.hzw.mass;

/**
 * Copyright@www.localhost.com.
 * Author:H.zw
 * Date:2018/4/11 18:32
 * Description:
 */
public class MyThreadTest {

    public static void main(String[] args){

        System.out.println("主线程Id" + Thread.currentThread().getId());
        MyThread myThread = new MyThread("thread1");
        myThread.start();
        MyThread myThread2 = new MyThread("thread2");
        myThread2.run();
        Thread myThread3 = new Thread(new MyRunnable("myRunnable"));
        myThread3.start();
    }
}
class MyThread extends Thread{

    private String name;

    public MyThread(String name){
        this.name = name;
    }

    @Override
    public void run(){
        System.out.println("name:" + name + " child thread ID:" + Thread.currentThread() .getId());
    }
}

class MyRunnable implements Runnable{

    private String name;

    public MyRunnable(String name){
        this.name = name;
    }

    @Override
    public void run() {
        System.out.println("name:" + name + " ID:" + Thread.currentThread().getId());
    }
}
