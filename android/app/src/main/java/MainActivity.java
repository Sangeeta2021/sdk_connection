
//***************************************************Handle permission for Android 12 & above*************************/
// package com.example.ble_sdk_connection;

// import androidx.annotation.NonNull;
// import io.flutter.embedding.android.FlutterActivity;
// import io.flutter.embedding.engine.FlutterEngine;
// import io.flutter.plugin.common.MethodChannel;
// import android.bluetooth.BluetoothAdapter;
// import android.bluetooth.BluetoothDevice;
// import android.content.Intent;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// public class MainActivity extends FlutterActivity {
//     private static final String CHANNEL = "com.example.ble_sdk_connection/ble";
//     private BluetoothAdapter bluetoothAdapter;

//     @Override
//     public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
//         super.configureFlutterEngine(flutterEngine);

//         bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

//         new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
//             .setMethodCallHandler((call, result) -> {
//                 if (call.method.equals("startScan")) {
//                     startBluetoothScan(result);
//                 } else if (call.method.equals("connectToDevice")) {
//                     String deviceAddress = call.argument("deviceAddress");
//                     connectToDevice(deviceAddress, result);
//                 } else {
//                     result.notImplemented();
//                 }
//             });
//     }

//     private void startBluetoothScan(MethodChannel.Result result) {
//         if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
//             result.error("UNAVAILABLE", "Bluetooth is not available or enabled.", null);
//             return;
//         }

//         List<Map<String, String>> devicesList = new ArrayList<>();
//         for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
//             Map<String, String> deviceInfo = new HashMap<>();
//             deviceInfo.put("name", device.getName());
//             deviceInfo.put("address", device.getAddress());
//             devicesList.add(deviceInfo);
//         }
//         result.success(devicesList);
//     }

//     private void connectToDevice(String deviceAddress, MethodChannel.Result result) {
//         BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
//         if (device != null) {
//             // Implement your BLE connection logic here if required.
//             result.success("Connected to " + device.getName());
//         } else {
//             result.error("ERROR", "Device not found.", null);
//         }
//     }
// }

//************************************Updated code with no device found logic**************************/
package com.example.ble_sdk_connection;

import androidx.annotation.NonNull;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "com.example.ble_sdk_connection/ble";
    private BluetoothAdapter bluetoothAdapter;

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
            .setMethodCallHandler((call, result) -> {
                if (call.method.equals("startScan")) {
                    startBluetoothScan(result);
                } else if (call.method.equals("connectToDevice")) {
                    String deviceAddress = call.argument("deviceAddress");
                    connectToDevice(deviceAddress, result);
                } else {
                    result.notImplemented();
                }
            });
    }

    private void startBluetoothScan(MethodChannel.Result result) {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            result.error("UNAVAILABLE", "Bluetooth is not available or enabled.", null);
            return;
        }
        List<Map<String, String>> devicesList = new ArrayList<>();
        for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
            Map<String, String> deviceInfo = new HashMap<>();
            deviceInfo.put("name", device.getName());
            deviceInfo.put("address", device.getAddress());
            devicesList.add(deviceInfo);
        }
        result.success(devicesList);
    }

    private void connectToDevice(String deviceAddress, MethodChannel.Result result) {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        if (device != null) {
            result.success("Connected to " + device.getName());
        } else {
            result.error("ERROR", "Device not found.", null);
        }
    }
}
