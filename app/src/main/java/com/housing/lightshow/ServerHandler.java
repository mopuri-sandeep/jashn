package com.housing.lightshow;

import android.content.Context;
import android.os.AsyncTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by rohit on 10/16/15.
 */

//It will send the data

public class ServerHandler extends AsyncTask {

    Context context;
    String host;
    int port;
    int len;
    Socket socket;
    byte buf[] = new byte[1024];

    ServerHandler(String host) {

//        context = this.getApplicationContext();
        port = 8888;
        socket = new Socket();

    }


    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            /**
             * Create a client socket with the host,
             * port, and timeout information.
             */
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, port)), 500);

            /**
             * Create a byte stream from a JPEG file and pipe it to the output stream
             * of the socket. This data will be retrieved by the server device.
             */
            OutputStream outputStream = socket.getOutputStream();
            /*
            ContentResolver cr = context.getContentResolver();
            InputStream inputStream = null;
            inputStream = cr.openInputStream(Uri.parse("path/to/picture.jpg"));
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            inputStream.close();
            */
            outputStream.write("sample data".getBytes());
            outputStream.close();
            socket.close();

        } catch (FileNotFoundException e) {
            //catch logic
        } catch (IOException e) {
            //catch logic
        }

/**
 * Clean up any open sockets when done
 * transferring or if an exception occurred.
 */ finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        return null;
                        //catch logic
                    }
                }
            }
        }
        return true;
    }
}

