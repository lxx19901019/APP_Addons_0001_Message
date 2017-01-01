package com.lxx.app_addons_0001_message;

import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.os.Handler;
import android.os.HandlerThread;
public class MainActivity extends AppCompatActivity {


    private Button mButton = null;
    private final String TAG = "MessageTest";
    private Thread myThread = null;
    private MyThread myThread2 = null;
    private Handler myHandler = null;
    private int ButtonCount = 0;
    private int msgCount = 0;
    private HandlerThread myThread3 = null;
    private Handler myHandler3 = null;

    class MyRunnable implements  Runnable {
        public  void run(){
            int count = 0;
            for (;;) {
                Log.d(TAG, "MyThread "+count);
                count++;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class MyThread extends Thread {
        private Looper mLoop = null;
        public void run() {
            super.run();
            Looper.prepare();
            synchronized (this) {
                mLoop = Looper.myLooper();
                notifyAll();
            }
            Looper.loop();
        }
        public Looper getLooper()
        {
            if(!isAlive()) {
                return null;
            }
            synchronized (this) {
                while(isAlive() && mLoop == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return mLoop;

            }

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button) findViewById(R.id.button);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Send Message "+ButtonCount);
                ButtonCount++;
                Message msg = new Message();

                myHandler.sendMessage(msg);

                myHandler3.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "get Message for Thread3 "+msgCount);
                        msgCount++;
                    }
                });
            }
        });

        myThread = new Thread(new MyRunnable());
        myThread.start();
        myThread2 = new MyThread();
        myThread2.start();

        myHandler = new Handler(myThread2.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Log.d(TAG, "get Message "+msgCount);
                msgCount++;
                return false;
            }
        });


        myThread3 = new HandlerThread("MessageTestThread3");
        myThread3.start();

        myHandler3 = new Handler(myThread3.getLooper());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
