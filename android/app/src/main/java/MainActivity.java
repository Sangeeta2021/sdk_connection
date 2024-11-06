package com.example.ble_sdk_connection;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

import cn.icomon.icdevicemanager.ICDeviceManager;
import cn.icomon.icdevicemanager.ICDeviceManagerDelegate;
import cn.icomon.icdevicemanager.callback.ICScanDeviceDelegate;
import cn.icomon.icdevicemanager.model.data.ICWeightData;
import cn.icomon.icdevicemanager.model.device.ICDevice;
import cn.icomon.icdevicemanager.model.device.ICScanDeviceInfo;
import cn.icomon.icdevicemanager.model.other.ICConstant;
import cn.icomon.icdevicemanager.model.other.ICDeviceManagerConfig; // Make sure this import is correct

public class MainActivity extends FlutterActivity implements ICDeviceManagerDelegate, ICScanDeviceDelegate {
    private static final String CHANNEL = "com.example.ble_sdk_connection/ble";
    private ICDeviceManager deviceManager;
    private ICDevice connectedDevice;

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler((call, result) -> {
                    switch (call.method) {
                        case "initializeSDK":
                            initializeSDK();
                            result.success(null);
                            break;
                        case "startScan":
                            startScan();
                            result.success(null);
                            break;
                        case "stopScan":
                            stopScan();
                            result.success(null);
                            break;
                        case "connectDevice":
                            String macAddress = call.argument("macAddress");
                            connectToDevice(macAddress);
                            result.success(null);
                            break;
                        case "disconnectDevice":
                            disconnectDevice();
                            result.success(null);
                            break;
                        default:
                            result.notImplemented();
                            break;
                    }
                });
    }

    private void initializeSDK() {
        deviceManager = ICDeviceManager.shared();
        deviceManager.setDelegate(this);

        // Ensure ICDeviceManagerConfig is recognized and correct
        ICDeviceManagerConfig config = new ICDeviceManagerConfig(getApplicationContext());
        deviceManager.initMgrWithConfig(config);

        Log.d("MainActivity", "SDK Initialized");
    }

    private void startScan() {
        deviceManager.startScanDevice();
        Log.d("MainActivity", "Scanning started");
    }

    private void stopScan() {
        deviceManager.stopScanDevice();
        Log.d("MainActivity", "Scanning stopped");
    }

    private void connectToDevice(String macAddress) {
        if (connectedDevice == null) {
            connectedDevice = new ICDevice();
            connectedDevice.setMacAddr(macAddress);

            deviceManager.addDevice(connectedDevice, new ICDeviceManager.Callback() {
                @Override
                public void onResponse(int code) {
                    Log.d("MainActivity", "Device connection status: " + code);
                }
            });
        }
    }

    private void disconnectDevice() {
        if (connectedDevice != null) {
            deviceManager.removeDevice(connectedDevice, new ICDeviceManager.Callback() {
                @Override
                public void onResponse(int code) {
                    Log.d("MainActivity", "Device disconnected with status: " + code);
                }
            });
            connectedDevice = null;
        }
    }

    @Override
    public void onReceiveWeightData(ICDevice device, ICWeightData data) {
        if (data.isStabilized) {
            String weightData = "Weight: " + data.weight_kg + " kg";
            new MethodChannel(getFlutterEngine().getDartExecutor().getBinaryMessenger(), CHANNEL)
                    .invokeMethod("onWeightDataReceived", weightData);
        }
    }

    @Override
    public void onScanResult(ICScanDeviceInfo deviceInfo) {
        Log.d("MainActivity", "Scan Result: " + deviceInfo.getMacAddr());
        new MethodChannel(getFlutterEngine().getDartExecutor().getBinaryMessenger(), CHANNEL)
                .invokeMethod("onDeviceFound", deviceInfo.getMacAddr());
    }

    @Override
    public void onDeviceConnectionChanged(ICDevice device, ICConstant.ICDeviceConnectState state) {
        Log.d("MainActivity", "Connection state changed: " + state);
    }

    @Override
    public void onInitFinish(boolean isSuccess) {
        Log.d("MainActivity", "SDK Initialization finished: " + isSuccess);
    }

    @Override
    public void onReceiveRSSI(ICDevice device, int rssi) {
        Log.d("MainActivity", "Received RSSI: " + rssi + " for device: " + device.getMacAddr());
    }
}
