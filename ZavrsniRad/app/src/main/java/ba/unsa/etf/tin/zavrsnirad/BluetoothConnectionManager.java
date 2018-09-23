package ba.unsa.etf.tin.zavrsnirad;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * Created by Tin on 18.07.2018..
 */

public class BluetoothConnectionManager {

    private static final UUID MY_UUID = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //private static final UUID MY_UUID = java.util.UUID.fromString("3c825bff-2545-4ac4-b8c0-2834acb4acd2"); // randomly generisan koristeci https://www.uuidgenerator.net/
    private static final String NAME = "HC-05";
    private static final String TAG = "TAG";
    Context mContext;
    BluetoothAdapter mBluetoothAdapter;
    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    private BluetoothDevice mmDevice;
    private UUID deviceUUID;
    private String mIncomingMessage;
    private Activity mActivity;



    ConnectedThread mConnectedThread;


    private Handler mHandler; // handler that gets info from Bluetooth service

    public BluetoothConnectionManager(Context mContext, Activity mActivity) {

        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        start();
    }

    // ACCEPT THREAD
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            Log.d(TAG, "run: AcceptThread Running.");
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    Log.d(TAG, "run : RFCOM server socket start");
                    socket = mmServerSocket.accept();
                    Log.d(TAG, "dobio socket");

                } catch (IOException e) {
                    Log.e(TAG, "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.

                    manageMyConnectedSocket(socket, mmDevice);

                    try
                    {
                        mmServerSocket.close();
                    } catch (IOException e)
                    {
                        Log.e(TAG, "Socket's close() method failed", e);
                        break;
                    }

                    break;
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }
    // ACCEPT THREAD

    //CONNECT THREAD

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        //private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;
            Log.d(TAG, "run : Connect Thread started");

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                Log.d(TAG, "I ovo se desilo");
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                Log.d(TAG, "Tu smo " + mmDevice.getName());
                mmSocket.connect();

                Log.d(TAG, "Connect Thread connected");
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            manageMyConnectedSocket(mmSocket, mmDevice);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }


    //CONNECT THREAD


    // Defines several constants used when transmitting messages between the
    // service and the UI.
    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;


    }

    //CONNECTED THREAD

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            //ShareSettingsActivity.mProgressBar.setVisibility(ProgressBar.VISIBLE);
            //Toast.makeText(mContext, "Konfiguracija poslana", Toast.LENGTH_LONG).show();


            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    //Message readMsg = mHandler.obtainMessage(
                            //MessageConstants.MESSAGE_READ, numBytes, -1,
                            //mmBuffer);
                    //readMsg.sendToTarget();
                    String incomingMessage = new String(mmBuffer, 0, numBytes);
                    Log.d(TAG, "InputStream: " + incomingMessage);
                    //da li prima od tastature ili konfiguraciju simbola
                    if(incomingMessage.equals("1") || incomingMessage.equals("2") || incomingMessage.equals("3") || incomingMessage.equals("4") || incomingMessage.equals("5"))
                    {

                        MainActivity.izgovoriSimbolSaTastature(incomingMessage);
                    }
                    else
                    {
                        ShareSettingsActivity.postaviKonfiguraciju(incomingMessage);
                    }

                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {

            String pendingMessage = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: Writing to outputstream: " + pendingMessage);
            
            try {

                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                //Message writtenMsg = mHandler.obtainMessage(
                        //MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                //writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                mHandler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    //CONNECTED THREAD

    // method to initiate accept thread
    public synchronized void start()
    {
        Log.d(TAG, "start");

        if(mConnectThread != null)
        {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if(mInsecureAcceptThread == null)
        {
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }
    }

    //method to initiate connect thread
    public void startClient(BluetoothDevice device, UUID uuid)
    {
        Log.d(TAG, "startClient: Started");

        //ShareSettingsActivity.mProgressBar.setVisibility(ProgressBar.VISIBLE);
        //Toast.makeText(mContext, "Šaljem konfiguraciju", Toast.LENGTH_LONG).show();

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
    }

    private void manageMyConnectedSocket(final BluetoothSocket mmSocket, final BluetoothDevice mmDevice) {
        Log.d(TAG, "connected: Starting.");

        Looper.prepare();
        Toast.makeText(mContext, "Jezik nije podrzan", Toast.LENGTH_LONG).show();
        mConnectedThread = new BluetoothConnectionManager.ConnectedThread(mmSocket);
        mConnectedThread.start();
    }


    public void write(byte[] out)
    {


        Log.d(TAG, "write: Write Called.");
        mConnectedThread.write(out);

    }

}
