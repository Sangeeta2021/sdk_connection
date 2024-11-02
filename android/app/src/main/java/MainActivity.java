// /// import android.os.Bundle;
// // import androidx.annotation.NonNull;
// // import io.flutter.embedding.android.FlutterActivity;
// // import io.flutter.embedding.android.FlutterActivity;

// // import io.flutter.embedding.engine.FlutterEngine;
// // import io.flutter.plugin.common.MethodChannel;
// // import com.icomon.ICDeviceManager;
// // import com.icomon.ICDeviceManagerCallback;
// // import com.icomon.ICDeviceData;

// // public class MainActivity extends FlutterActivity {
// //     private static final String CHANNEL = "ble_weight_machine";
// //     private ICDeviceManager deviceManager;

// //     @Override
// //     public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
// //         super.configureFlutterEngine(flutterEngine);
// //         new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
// //             .setMethodCallHandler((call, result) -> {
// //                 if (call.method.equals("connectToDevice")) {
// //                     connectToDevice(result);
// //                 } else if (call.method.equals("disconnectDevice")) {
// //                     disconnectDevice(result);
// //                 } else {
// //                     result.notImplemented();
// //                 }
// //             });
// //     }

// //     private void connectToDevice(MethodChannel.Result result) {
// //         deviceManager = new ICDeviceManager(this);
// //         deviceManager.setCallback(new ICDeviceManagerCallback() {
// //             @Override
// //             public void onDeviceConnected() {
// //                 result.success("Device connected successfully");
// //             }

// //             @Override
// //             public void onDeviceDisconnected() {
// //                 result.error("DISCONNECTED", "Device disconnected", null);
// //             }

// //             @Override
// //             public void onDataReceived(ICDeviceData data) {
// //                 double weight = data.getWeight();
// //                 new MethodChannel(getFlutterEngine().getDartExecutor().getBinaryMessenger(), CHANNEL)
// //                     .invokeMethod("onWeightData", weight);
// //             }
// //         });
// //         deviceManager.connect();
// //     }

// //     private void disconnectDevice(MethodChannel.Result result) {
// //         if (deviceManager != null) {
// //             deviceManager.disconnect();
// //             result.success("Device disconnected");
// //         } else {
// //             result.error("NO_DEVICE", "No device to disconnect", null);
// //         }
// //     }

// //     @Override
// //     protected void onDestroy() {
// //         super.onDestroy();
// //         if (deviceManager != null) {
// //             deviceManager.disconnect();
// //         }
// //     }
// // }
// import cn.icomon.icdemo;
// import androidx.annotation.NonNull;
// import io.flutter.embedding.android.FlutterActivity;
// import io.flutter.embedding.engine.FlutterEngine;
// import io.flutter.plugin.common.MethodChannel;

// import cn.icomon.icdevicemanager.ICDeviceManager;
// import cn.icomon.icdevicemanager.ICDeviceManagerDelegate;
// import cn.icomon.icdevicemanager.callback.ICScanDeviceDelegate;
// import cn.icomon.icdevicemanager.model.data.ICWeightData;
// import cn.icomon.icdevicemanager.model.device.ICDevice;
// import cn.icomon.icdevicemanager.model.device.ICScanDeviceInfo;
// import cn.icomon.icdevicemanager.model.other.ICConstant;

// import android.os.Bundle;
// import android.util.Log;

// public class MainActivity extends FlutterActivity implements ICDeviceManagerDelegate, ICScanDeviceDelegate {
//     private static final String CHANNEL = "com.example.ble_sdk_connection/ble";
//     private ICDeviceManager deviceManager;
//     private ICDevice connectedDevice;

//     @Override
//     public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
//         super.configureFlutterEngine(flutterEngine);
//         new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
//             .setMethodCallHandler((call, result) -> {
//                 switch (call.method) {
//                     case "initializeSDK":
//                         initializeSDK();
//                         result.success(null);
//                         break;
//                     case "startScan":
//                         startScan();
//                         result.success(null);
//                         break;
//                     case "stopScan":
//                         stopScan();
//                         result.success(null);
//                         break;
//                     case "connectDevice":
//                         String macAddress = call.argument("macAddress");
//                         connectToDevice(macAddress);
//                         result.success(null);
//                         break;
//                     case "disconnectDevice":
//                         disconnectDevice();
//                         result.success(null);
//                         break;
//                     default:
//                         result.notImplemented();
//                         break;
//                 }
//             });
//     }

//     private void initializeSDK() {
//         deviceManager = ICDeviceManager.shared();
//         deviceManager.setDelegate(this);
//         deviceManager.initMgrWithConfig(new ICDeviceManagerConfig(getApplicationContext()));
//         Log.d("MainActivity", "SDK Initialized");
//     }

//     private void startScan() {
//         deviceManager.startScanDevice();
//         Log.d("MainActivity", "Scanning started");
//     }

//     private void stopScan() {
//         deviceManager.stopScanDevice();
//         Log.d("MainActivity", "Scanning stopped");
//     }

//     private void connectToDevice(String macAddress) {
//         if (connectedDevice == null) {
//             connectedDevice = new ICDevice();
//             connectedDevice.setMacAddr(macAddress);
//             deviceManager.addDevice(connectedDevice, code -> {
//                 Log.d("MainActivity", "Device connection status: " + code);
//             });
//         }
//     }

//     private void disconnectDevice() {
//         if (connectedDevice != null) {
//             deviceManager.removeDevice(connectedDevice, code -> {
//                 Log.d("MainActivity", "Device disconnected with status: " + code);
//             });
//             connectedDevice = null;
//         }
//     }

//     @Override
//     public void onReceiveWeightData(ICDevice device, ICWeightData data) {
//         if (data.isStabilized) {
//             String weightData = "Weight: " + data.weight_kg + " kg";
//             new MethodChannel(getFlutterEngine().getDartExecutor().getBinaryMessenger(), CHANNEL)
//                 .invokeMethod("onWeightDataReceived", weightData);
//         }
//     }

//     @Override
//     public void onScanResult(ICScanDeviceInfo deviceInfo) {
//         Log.d("MainActivity", "Scan Result: " + deviceInfo.getMacAddr());
//         // Optionally send scan results to Flutter
//         new MethodChannel(getFlutterEngine().getDartExecutor().getBinaryMessenger(), CHANNEL)
//             .invokeMethod("onDeviceFound", deviceInfo.getMacAddr());
//     }

//     @Override
//     public void onDeviceConnectionChanged(ICDevice device, ICConstant.ICDeviceConnectState state) {
//         Log.d("MainActivity", "Connection state changed: " + state);
//         // Optionally send connection state to Flutter
//     }

//     @Override
//     public void onInitFinish(boolean isSuccess) {
//         Log.d("MainActivity", "SDK Initialization finished: " + isSuccess);
//     }
// }


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
        deviceManager.initMgrWithConfig(new ICDeviceManagerConfig(getApplicationContext()));
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
            deviceManager.addDevice(connectedDevice, code -> {
                Log.d("MainActivity", "Device connection status: " + code);
            });
        }
    }

    private void disconnectDevice() {
        if (connectedDevice != null) {
            deviceManager.removeDevice(connectedDevice, code -> {
                Log.d("MainActivity", "Device disconnected with status: " + code);
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
}
