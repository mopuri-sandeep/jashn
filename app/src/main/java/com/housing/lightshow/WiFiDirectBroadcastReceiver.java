package com.housing.lightshow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;



/**
 * Created by Akshay on 16/10/15.
 */

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;
    WifiP2pManager.PeerListListener myPeerListListener;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       MainActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;


    }

    public void discoveringPeers(int x) {
        this.mManager.discoverPeers(this.mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                System.out.println("peer discovered successfull");
            }

            @Override
            public void onFailure(int reasonCode) {
                System.out.println("peer discovered unsuccessfull");
            }
        });
    }

    public void connectToPeer(){
        //obtain a peer from the WifiP2pDeviceList
        WifiP2pDevice device = new WifiP2pDevice();
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        this.mManager.connect(this.mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                //success logic
            }

            @Override
            public void onFailure(int reason) {
                //failure logic
            }
        });
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("laddha | setting up");
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                System.out.println("laddha | wifi direct enabled");
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

                this.mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        System.out.println("peer discovered successfull");

                            // request available peers from the wifi p2p manager. This is an
                            // asynchronous call and the calling activity is notified with a
                            // callback on PeerListListener.onPeersAvailable()

                            if (mManager != null) {
                                myPeerListListener = new WifiP2pManager.PeerListListener() {
                                    @Override
                                    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                                        System.out.println("getlist => " + wifiP2pDeviceList.getDeviceList().toString());
                                        System.out.println("printing device list => " + wifiP2pDeviceList.toString());
                                    }
                                };

                                mManager.requestPeers(mChannel, myPeerListListener);
                            }
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        System.out.println("peer discovered unsuccessfull");
                    }
                });



            } else {
                // Wi-Fi P2P is not enabled
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                System.out.println("laddha | wifi direct not enabled");
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            }
            // Check to see if Wi-Fi is enabled and notify appropriate activity
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }
}