package com.example.jianxu.bluetoothwear;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Set;
import com.example.jianxu.commonlib.BluetoothChatService;
import com.example.jianxu.commonlib.Constants;

public class MainActivity extends AppCompatActivity {
    public final String TAG = "MainActivity";
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothChatService mBluetoothService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetoothService = new BluetoothChatService(this, mHandler);
        mBluetoothService.start();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.w(TAG, "Default Bluetooth Adapter is null.");
        }

        Log.i(TAG, "Default Bluetooth Adapter is set");
        if (mBluetoothAdapter.isEnabled()) {
            Log.i(TAG, "Bluetooth is enabled");
            String addr = mBluetoothAdapter.getAddress();
            String deviceName = mBluetoothAdapter.getName();
            Log.i(TAG, "Bluetooth is enabled, " + deviceName + ":" + addr);

        } else {
            Log.w(TAG, "Bluetooth is not Enabled");
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {

            for (BluetoothDevice device : pairedDevices) {

                Log.i(TAG, "Found device, " + device.getName() + ":" + device.getAddress());
            }

        } else {
            Log.i(TAG, "No devices have been paired yet.");
        }

        Button connectBtn = (Button)findViewById(R.id.connectWatch);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Connect the smartwatch
                connectDevice("44:D4:E0:F9:AF:D1");
            }
        });

        Button sendBtn = (Button)findViewById(R.id.sendMsg);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Hello, I'm Nexus 6P " + System.currentTimeMillis() + " .\n");
            }
        });

        // Sending 1KB data
        Button send1KBtn = (Button)findViewById(R.id.send1KB);
        send1KBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Should fillin 1K/2 chars
                int length = 1024;
                String sendStr = new String("");
                for (int i = 0; i < length; ++i) {
                    sendStr += "a";
                }
                sendMessage(sendStr);
            }
        });

        // Sending 5KB data
        Button send5KBtn = (Button)findViewById(R.id.send5KB);
        send5KBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int length = 5*1024;
                String sendStr = new String("");
                for (int i = 0; i < 1024; ++i) {
                    sendStr += "a";
                }
                for (int i = 0; i < 1024; ++i) {
                    sendStr += "b";
                }
                for (int i = 0; i < 1024; ++i) {
                    sendStr += "c";
                }
                for (int i = 0; i < 1024; ++i) {
                    sendStr += "d";
                }
                for (int i = 0; i < 1024; ++i) {
                    sendStr += "e";
                }
                sendMessage(sendStr);
            }
        });

        // Sending 10KB data
        Button send10KBtn = (Button)findViewById(R.id.send10KB);
        send10KBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int length = 10*1024;
                String sendStr = new String("");
                for (int i = 0; i < 1024; ++i) {
                    sendStr += "a";
                }
                for (int i = 0; i < 1024; ++i) {
                    sendStr += "b";
                }
                for (int i = 0; i < 1024; ++i) {
                    sendStr += "c";
                }
                for (int i = 0; i < 1024; ++i) {
                    sendStr += "d";
                }
                for (int i = 0; i < 1024; ++i) {
                    sendStr += "e";
                }
                for (int i = 0; i < 1024; ++i) {
                    sendStr += "a";
                }
                for (int i = 0; i < 1024; ++i) {
                    sendStr += "b";
                }
                for (int i = 0; i < 1024; ++i) {
                    sendStr += "c";
                }
                for (int i = 0; i < 1024; ++i) {
                    sendStr += "d";
                }
                for (int i = 0; i < 1024; ++i) {
                    sendStr += "e";
                }

                sendMessage(sendStr);
            }
        });

    }


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //Log.d(TAG, "Received message: " + msg.what);
            try {
                switch (msg.what) {
                    case Constants.MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;
                        String readStr = new String(readBuf);
                        Log.i(TAG, "For script: received timestamp= " + System.currentTimeMillis());
                        Log.i(TAG, "Received packet, " + readStr);
                        break;
                    case Constants.MESSAGE_WRITE:
                        byte[] writeBuf = (byte[]) msg.obj;
                        String sentStr = new String(writeBuf);
                        Log.i(TAG, "Sent packet, " + sentStr);
                        Log.i(TAG, "For script: sent packet size= " + writeBuf.length
                                    + " ,timestamp= " + System.currentTimeMillis());
                        break;
                }
            } catch (Exception e) {
                Log.w(TAG, "Exception caught " + e.toString());
            }
        }
    };

    private void sendMessage(String message) {
        if (mBluetoothService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, "You are not connected to a device", Toast.LENGTH_SHORT).show();
        }

        try {
            if (message.length() > 0) {
                byte[] send = message.getBytes();
                mBluetoothService.write(send);

            }
        } catch (Exception e) {
            Log.w(TAG, "Exception catched " + e.toString());
        }
    }

    private void connectDevice(String addr) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(addr);
        mBluetoothService.connect(device, false);
    }
}
