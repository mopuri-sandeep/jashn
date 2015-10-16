package com.housing.lightshow;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by rohit on 10/16/15.
 */
public class FileServerAsyncTask extends AsyncTask {

    private Context context;
    private TextView statusText;
    String host;

//    public FileServerAsyncTask(Contedxt context, View statusText) {
    public FileServerAsyncTask() {
//        this.context = context;
//        this.statusText = (TextView) statusText;
        host = "10.1.2.60";
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {

            /**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */
            ServerSocket serverSocket = new ServerSocket(8888);
            Socket client = serverSocket.accept();

            System.out.println("laddha >>>>>>>>>>>>>>>>>> " + client.getInetAddress().toString());

            /**
             * If this code is reached, a server has connected and transferred data
             * Save the input stream from the server as a JPEG file
             */
            /*
            final File f = new File(Environment.getExternalStorageDirectory() + "/"
                    + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                    + ".jpg");

            File dirs = new File(f.getParent());
            if (!dirs.exists())
                dirs.mkdirs();
            f.createNewFile();
            InputStream inputstream = client.getInputStream();

            FileOutputStream os = new FileOutputStream(f);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputstream.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.close();
//            copyFile(inputstream, new FileOutputStream(f));
             */

            InputStream inputstream = client.getInputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputstream.read(buffer)) > 0) {
                System.out.println("laddha >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> hi");
                System.out.println(buffer.toString());
            }
            serverSocket.close();
            return true;
//            return f.getAbsolutePath();
        } catch (IOException e) {
            Log.e("WiFiDirectActivity", e.getMessage());
            return null;
        }




        /*try {

            *//**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             *//*
            ServerSocket serverSocket = new ServerSocket(8888);
            Socket client = null;
            try {
                client = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
            OutputStream osx;
            try {
                osx = client.getOutputStream();
                osx.write("sample Data".getBytes());
                osx.close();
            } catch (NullPointerException ex){
            } catch (Exception e){
                e.printStackTrace();
            }


            *//**
             * If this code is reached, a client has connected and transferred data
             * Save the input stream from the client as a JPEG file
             *//*
            final File f = new File(Environment.getExternalStorageDirectory() + "/"
                    + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                    + ".jpg");

            File dirs = new File(f.getParent());
            if (!dirs.exists())
                dirs.mkdirs();
            f.createNewFile();
            InputStream inputstream = client.getInputStream();
//            copyFile(inputstream, new FileOutputStream(f));

            FileOutputStream os = new FileOutputStream(f);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputstream.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            serverSocket.close();
            return f.getAbsolutePath();
        } catch (IOException e) {
            Log.e("WiFiDirectActivity", e.getMessage());
            return null;
        }*/
    }

//    @Override
//    protected void onPostExecute(Result result) {
//        throw new RuntimeException("Stub!");
//    }


    protected void onPostExecute(String result) {
        if (result != null) {
//            statusText.setText("File copied - " + result);
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file://" + result), "image/*");
            context.startActivity(intent);
        }
    }
}