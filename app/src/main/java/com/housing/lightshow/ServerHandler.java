package com.housing.lightshow;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        port = 8889;
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
            socket.connect((new InetSocketAddress("192.168.49.1", port)), 1500);

            /**
             * Create a byte stream from a JPEG file and pipe it to the output stream
             * of the socket. This data will be retrieved by the server device.
             */
            OutputStream outputStream = socket.getOutputStream();
            InputStream iStream = socket.getInputStream();
            /*
            ContentResolver cr = context.getContentResolver();
            InputStream inputStream = null;
            inputStream = cr.openInputStream(Uri.parse("path/to/picture.jpg"));
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            inputStream.close();
            */
            String result = getStringFromInputStream(iStream);
            System.out.println("hahahahahahaha3");
            iStream.close();

            outputStream.write("sample data >>>DKFJDKJFKDJFKDJFKDJF>>>>>".getBytes());
            outputStream.close();
//            outputStream.close();
            System.out.println("hahahahahahaha1");
//            InputStream iStream = socket.getInputStream();
            System.out.println("hahahahahahaha2");


            System.out.println("hahahahahahaha4");
            System.out.println("the output from server is: " + result);
//            socket.close();

        } catch (FileNotFoundException e) {
            System.out.println("Error in file");
        } catch (IOException e) {
            //catch logic
            System.out.println("Error in IO " + e.toString());
        }

/**
 * Clean up any open sockets when done
 * transferring or if an exception occurred.
 */ finally {
            if (socket != null) {
//                if (socket.isConnected()) {
//                    try {
////                        socket.close();
//                    } catch (IOException e) {
//                        return null;
//                        //catch logic
//                    }
//                }
            }
        }
        return true;
    }

    // convert InputStream to String
    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                System.out.println("tahahahahahadkfjdkjfdkfjd " + line);
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }
}

