// package com.example.ble_sdk_connection;

// import android.Manifest;
// import android.bluetooth.BluetoothAdapter;
// import android.content.pm.PackageManager;
// import android.os.Build;
// import android.os.Bundle;
// import android.widget.Toast;

// import androidx.annotation.NonNull;
// import androidx.core.app.ActivityCompat;
// import androidx.core.content.ContextCompat;

// import io.flutter.embedding.android.FlutterActivity;
// import io.flutter.plugin.common.MethodChannel;

// import cn.icomon.sdk.ICDevice;
// import cn.icomon.sdk.ICDeviceManager;
// import cn.icomon.sdk.ICDeviceManagerConfig;
// import cn.icomon.sdk.ICDeviceManagerDelegate;
// import cn.icomon.sdk.ICScanDeviceDelegate;

// public class MainActivity extends FlutterActivity implements ICDeviceManagerDelegate, ICScanDeviceDelegate {

//     private static final String CHANNEL = "com.example.ble_sdk_connection/ble";
//     private static final int REQUEST_PERMISSIONS = 1;
//     private static final int REQUEST_ENABLE_BT = 2;
//     private ICDeviceManager deviceManager;

//     @Override
//     protected void onCreate(Bundle savedInstanceState) {
//         super.onCreate(savedInstanceState);

//         // Request permissions for Bluetooth and location access
//         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//             if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
//                     || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
//                     || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                     || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
//                     || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                 ActivityCompat.requestPermissions(this, new String[] {
//                         Manifest.permission.BLUETOOTH,
//                         Manifest.permission.BLUETOOTH_ADMIN,
//                         Manifest.permission.ACCESS_FINE_LOCATION,
//                         Manifest.permission.BLUETOOTH_SCAN,
//                         Manifest.permission.BLUETOOTH_CONNECT
//                 }, REQUEST_PERMISSIONS);
//             } else {
//                 initializeSDK();
//             }
//         } else {
//             initializeSDK();
//         }
//     }

//     private void initializeSDK() {
//         // Initialize ICDeviceManager and start scanning for devices
//         ICDeviceManagerConfig config = new ICDeviceManagerConfig(); // Initialize config
//         deviceManager = new ICDeviceManager(config);

//         // Set delegates for device manager
//         deviceManager.setDeviceManagerDelegate(this);
//         deviceManager.setScanDeviceDelegate(this);

//         // Start scanning for devices
//         startScanning();
//     }

//     private void startScanning() {
//         BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//         if (bluetoothAdapter == null) {
//             Toast.makeText(this, "Bluetooth is not supported on this device.", Toast.LENGTH_SHORT).show();
//         } else {
//             if (!bluetoothAdapter.isEnabled()) {
//                 Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                 startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//             } else {
//                 deviceManager.startScanDevice();
//             }
//         }
//     }

//     @Override
//     public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//         super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//         if (requestCode == REQUEST_PERMISSIONS) {
//             if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                 initializeSDK();
//             } else {
//                 Toast.makeText(this, "Permissions are required for BLE scanning", Toast.LENGTH_SHORT).show();
//             }
//         }
//     }

//     @Override
//     public void onDeviceConnected(ICDevice device) {
//         // Handle successful device connection
//         Toast.makeText(this, "Device connected: " + device.getDeviceName(), Toast.LENGTH_SHORT).show();
//     }

//     @Override
//     public void onDeviceDisconnected(ICDevice device) {
//         // Handle device disconnection
//         Toast.makeText(this, "Device disconnected: " + device.getDeviceName(), Toast.LENGTH_SHORT).show();
//     }

//     @Override
//     public void onScanDeviceFound(ICDevice device) {
//         // Handle found device (e.g., show it to the user)
//         Toast.makeText(this, "Found device: " + device.getDeviceName(), Toast.LENGTH_SHORT).show();
//     }

//     @Override
//     public void onScanDeviceFailed(int errorCode) {
//         // Handle scanning failure
//         Toast.makeText(this, "Scan failed: " + errorCode, Toast.LENGTH_SHORT).show();
//     }

//     @Override
//     public void onScanDeviceFinished() {
//         // Handle scan finished
//         Toast.makeText(this, "Scan finished", Toast.LENGTH_SHORT).show();
//     }
// }


//************************************************Updated Code*************************************** */
// package com.example.ble_sdk_connection;

// import android.bluetooth.BluetoothAdapter;
// import android.bluetooth.BluetoothDevice;
// import android.content.BroadcastReceiver;
// import android.content.Context;
// import android.content.Intent;
// import android.content.IntentFilter;
// import android.os.Bundle;
// import androidx.annotation.NonNull;
// import io.flutter.embedding.android.FlutterActivity;
// import io.flutter.plugin.common.MethodChannel;
// import cn.icomon.icdemo.ICDeviceManager;  // Importing the SDK class for ICDeviceManager

// public class MainActivity extends FlutterActivity {
//     private static final String CHANNEL = "com.example.ble_sdk_connection/ble";
//     private BluetoothAdapter bluetoothAdapter;

//     @Override
//     public void configureFlutterEngine(@NonNull Context context) {
//         super.configureFlutterEngine(context);
        
//         bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
//         // Set up the method channel and handle method calls
//         new MethodChannel(getFlutterEngine().getDartExecutor(), CHANNEL).setMethodCallHandler(
//             (call, result) -> {
//                 if (call.method.equals("startScan")) {
//                     startScan(result);
//                 } else if (call.method.equals("connectDevice")) {
//                     String deviceAddress = call.argument("deviceAddress");
//                     connectDevice(deviceAddress, result);
//                 } else {
//                     result.notImplemented();
//                 }
//             }
//         );
//     }

//     // Start scanning for BLE devices
//     private void startScan(final MethodChannel.Result result) {
//         if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
//             result.error("BLUETOOTH_ERROR", "Bluetooth is not available or enabled", null);
//             return;
//         }

//         // Register a receiver to listen for found devices
//         IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//         registerReceiver(receiver, filter);

//         bluetoothAdapter.startDiscovery();
//         result.success("Scanning started");
//     }

//     // Receiver to capture found Bluetooth devices
//     private final BroadcastReceiver receiver = new BroadcastReceiver() {
//         @Override
//         public void onReceive(Context context, Intent intent) {
//             String action = intent.getAction();
//             if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                 BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                 String deviceName = device.getName();
//                 String deviceAddress = device.getAddress();

//                 // Send the found device details back to Flutter
//                 new MethodChannel(getFlutterEngine().getDartExecutor(), CHANNEL).invokeMethod("onDeviceFound", 
//                         new DeviceDetails(deviceName, deviceAddress));
//             }
//         }
//     };

//     // Connect to a selected device using its address
//     private void connectDevice(String deviceAddress, final MethodChannel.Result result) {
//         if (deviceAddress == null) {
//             result.error("DEVICE_ERROR", "Device address is null", null);
//             return;
//         }

//         BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
//         if (device != null) {
//             // Use ICDeviceManager to connect to the device
//             ICDeviceManager.getInstance().connectDevice(device, new ICDeviceManager.ICDeviceConnectCallback() {
//                 @Override
//                 public void onConnectSuccess() {
//                     result.success("Device connected");
//                     fetchWeightData(result);  // Fetch weight data once connected
//                 }

//                 @Override
//                 public void onConnectFailed(String errorMessage) {
//                     result.error("CONNECT_ERROR", "Failed to connect: " + errorMessage, null);
//                 }
//             });
//         } else {
//             result.error("DEVICE_NOT_FOUND", "Device not found", null);
//         }
//     }

//     // Fetch weight data from the connected device
//     private void fetchWeightData(final MethodChannel.Result result) {
//         ICDeviceManager.getInstance().getWeightData(new ICDeviceManager.ICDeviceWeightCallback() {
//             @Override
//             public void onWeightDataReceived(float weight) {
//                 result.success(weight); 
//             }

//             @Override
//             public void onError(String errorMessage) {
//                 result.error("WEIGHT_ERROR", "Failed to fetch weight: " + errorMessage, null);
//             }
//         });
//     }

//     // Clean up receiver when done
//     @Override
//     protected void onDestroy() {
//         super.onDestroy();
//         unregisterReceiver(receiver);  // Unregister the receiver to avoid memory leaks
//     }

//     // A simple class to encapsulate the device details for Flutter
//     public static class DeviceDetails {
//         private final String name;
//         private final String address;

//         public DeviceDetails(String name, String address) {
//             this.name = name;
//             this.address = address;
//         }

//         public String getName() {
//             return name;
//         }

//         public String getAddress() {
//             return address;
//         }
//     }
// }





//*******************************************************Code 2.1*********************************** */
// package com.example.ble_sdk_connection;

// import android.bluetooth.BluetoothAdapter;
// import android.bluetooth.BluetoothDevice;
// import android.content.BroadcastReceiver;
// import android.content.Context;
// import android.content.Intent;
// import android.content.IntentFilter;
// import androidx.annotation.NonNull;
// import io.flutter.embedding.android.FlutterActivity;
// import io.flutter.plugin.common.MethodChannel;
// import cn.icomon.icdemo.ICDeviceManager; // Importing the SDK class for ICDeviceManager

// import java.util.ArrayList;
// import java.util.List;

// public class MainActivity extends FlutterActivity {
//     private static final String CHANNEL = "com.example.ble_sdk_connection/ble";
//     private BluetoothAdapter bluetoothAdapter;
//     private List<DeviceDetails> deviceList = new ArrayList<>();

//     @Override
//     public void configureFlutterEngine(@NonNull Context context) {
//         super.configureFlutterEngine(context);

//         bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

//         // Set up the method channel and handle method calls
//         new MethodChannel(getFlutterEngine().getDartExecutor(), CHANNEL).setMethodCallHandler(
//             (call, result) -> {
//                 if (call.method.equals("startScan")) {
//                     startScan(result);
//                 } else if (call.method.equals("connectDevice")) {
//                     String deviceAddress = call.argument("deviceAddress");
//                     connectDevice(deviceAddress, result);
//                 } else {
//                     result.notImplemented();
//                 }
//             }
//         );
//     }

//     // Start scanning for BLE and classic Bluetooth devices
//     private void startScan(final MethodChannel.Result result) {
//         if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
//             result.error("BLUETOOTH_ERROR", "Bluetooth is not available or enabled", null);
//             return;
//         }

//         // Register a receiver to listen for found devices
//         IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//         registerReceiver(receiver, filter);

//         // Start both BLE and classic Bluetooth scans
//         bluetoothAdapter.startDiscovery();
//         result.success("Scanning started");
//     }

//     // Receiver to capture found Bluetooth devices
//     private final BroadcastReceiver receiver = new BroadcastReceiver() {
//         @Override
//         public void onReceive(Context context, Intent intent) {
//             String action = intent.getAction();
//             if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                 BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                 String deviceName = device.getName();
//                 String deviceAddress = device.getAddress();

//                 if (deviceName != null) {
//                     DeviceDetails deviceDetails = new DeviceDetails(deviceName, deviceAddress);
//                     if (!deviceList.contains(deviceDetails)) {
//                         deviceList.add(deviceDetails);
//                         // Send the found device list to Flutter
//                         new MethodChannel(getFlutterEngine().getDartExecutor(), CHANNEL)
//                             .invokeMethod("onDeviceFound", deviceList);
//                     }
//                 }
//             }
//         }
//     };

//     // Connect to a selected device using its address
//     private void connectDevice(String deviceAddress, final MethodChannel.Result result) {
//         if (deviceAddress == null) {
//             result.error("DEVICE_ERROR", "Device address is null", null);
//             return;
//         }

//         BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
//         if (device != null) {
//             // Use ICDeviceManager to connect to BLE devices or classic Bluetooth devices
//             ICDeviceManager.getInstance().connectDevice(device, new ICDeviceManager.ICDeviceConnectCallback() {
//                 @Override
//                 public void onConnectSuccess() {
//                     result.success("Device connected");
//                     fetchWeightData(result);  // Fetch weight data once connected
//                 }

//                 @Override
//                 public void onConnectFailed(String errorMessage) {
//                     result.error("CONNECT_ERROR", "Failed to connect: " + errorMessage, null);
//                 }
//             });
//         } else {
//             result.error("DEVICE_NOT_FOUND", "Device not found", null);
//         }
//     }

//     // Fetch weight data from the connected device
//     private void fetchWeightData(final MethodChannel.Result result) {
//         ICDeviceManager.getInstance().getWeightData(new ICDeviceManager.ICDeviceWeightCallback() {
//             @Override
//             public void onWeightDataReceived(float weight) {
//                 result.success(weight);  // Return the weight data to Flutter
//             }

//             @Override
//             public void onError(String errorMessage) {
//                 result.error("WEIGHT_ERROR", "Failed to fetch weight: " + errorMessage, null);
//             }
//         });
//     }

//     // Clean up receiver when done
//     @Override
//     protected void onDestroy() {
//         super.onDestroy();
//         unregisterReceiver(receiver);  // Unregister the receiver to avoid memory leaks
//     }

//     // A simple class to encapsulate the device details for Flutter
//     public static class DeviceDetails {
//         private final String name;
//         private final String address;

//         public DeviceDetails(String name, String address) {
//             this.name = name;
//             this.address = address;
//         }

//         public String getName() {
//             return name;
//         }

//         public String getAddress() {
//             return address;
//         }

//         @Override
//         public boolean equals(Object obj) {
//             if (this == obj) return true;
//             if (obj == null || getClass() != obj.getClass()) return false;
//             DeviceDetails that = (DeviceDetails) obj;
//             return address.equals(that.address);
//         }

//         @Override
//         public int hashCode() {
//             return address.hashCode();
//         }
//     }
// }

//****************************************************Code 2.2*********************************************** */
// package com.example.ble_sdk_connection;

// import android.bluetooth.BluetoothAdapter;
// import android.bluetooth.BluetoothDevice;
// import android.bluetooth.BluetoothLeScanner;
// import android.bluetooth.BluetoothManager;
// import android.content.Context;
// import android.os.Bundle;
// import android.os.ParcelUuid;

// import androidx.annotation.NonNull;

// import io.flutter.embedding.android.FlutterActivity;
// import io.flutter.embedding.engine.FlutterEngine;
// import io.flutter.plugin.common.MethodChannel;
// import io.flutter.plugins.GeneratedPluginRegistrant;

// import java.util.ArrayList;
// import java.util.List;

// public class MainActivity extends FlutterActivity {
//     private static final String CHANNEL = "com.example.ble_sdk_connection/ble";
//     private BluetoothAdapter bluetoothAdapter;
//     private BluetoothLeScanner bluetoothLeScanner;
//     private MethodChannel.Result scanResult;

//     @Override
//     public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
//         super.configureFlutterEngine(flutterEngine);
//         GeneratedPluginRegistrant.registerWith(flutterEngine);

//         BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//         bluetoothAdapter = bluetoothManager.getAdapter();

//         new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL).setMethodCallHandler(
//             (call, result) -> {
//                 switch (call.method) {
//                     case "startScan":
//                         startScan(result);
//                         break;
//                     case "stopScan":
//                         stopScan(result);
//                         break;
//                     case "connectToDevice":
//                         String deviceId = call.argument("deviceId");
//                         connectToDevice(deviceId, result);
//                         break;
//                     default:
//                         result.notImplemented();
//                         break;
//                 }
//             }
//         );
//     }

//     private void startScan(MethodChannel.Result result) {
//         bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
//         if (bluetoothLeScanner != null) {
//             bluetoothLeScanner.startScan((device, rssi, scanRecord) -> {
//                 String deviceName = device.getName() != null ? device.getName() : "Unknown";
//                 String deviceAddress = device.getAddress();
//                 List<String> deviceDetails = new ArrayList<>();
//                 deviceDetails.add(deviceName);
//                 deviceDetails.add(deviceAddress);

//                 new MethodChannel(getFlutterEngine().getDartExecutor().getBinaryMessenger(), CHANNEL)
//                     .invokeMethod("onDeviceFound", deviceDetails);
//             });
//             result.success("Scanning started");
//         } else {
//             result.error("NO_SCANNER", "BluetoothLeScanner not available", null);
//         }
//     }

//     private void stopScan(MethodChannel.Result result) {
//         if (bluetoothLeScanner != null) {
//             bluetoothLeScanner.stopScan(null);
//             result.success("Scan stopped");
//         } else {
//             result.error("NO_SCANNER", "BluetoothLeScanner not available", null);
//         }
//     }

//     private void connectToDevice(String deviceId, MethodChannel.Result result) {
//         BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceId);
//         if (device != null) {
//             device.connectGatt(this, false, new android.bluetooth.BluetoothGattCallback() {
//                 @Override
//                 public void onConnectionStateChange(android.bluetooth.BluetoothGatt gatt, int status, int newState) {
//                     if (newState == android.bluetooth.BluetoothProfile.STATE_CONNECTED) {
//                         result.success("Connected to device: " + deviceId);
//                     } else {
//                         result.error("ERROR", "Failed to connect", null);
//                     }
//                 }
//             });
//         } else {
//             result.error("ERROR", "Device not found", null);
//         }
//     }
// }


//********************************************Code 2.3************************/
// package com.example.ble_sdk_connection;

// import androidx.annotation.NonNull;
// import io.flutter.embedding.android.FlutterActivity;
// import io.flutter.embedding.engine.FlutterEngine;
// import io.flutter.plugin.common.MethodChannel;
// import android.bluetooth.BluetoothAdapter;
// import android.bluetooth.BluetoothDevice;
// import android.content.Intent;
// import java.util.Set;

// public class MainActivity extends FlutterActivity {
//     private static final String CHANNEL = "com.example.ble_sdk_connection/ble";
//     private BluetoothAdapter bluetoothAdapter;

//     @Override
//     public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
//         super.configureFlutterEngine(flutterEngine);
        
//         bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

//         new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
//             .setMethodCallHandler(
//                 (call, result) -> {
//                     if (call.method.equals("startScan")) {
//                         startBluetoothScan(result);
//                     } else if (call.method.equals("connectToDevice")) {
//                         String deviceAddress = call.argument("address");
//                         connectToDevice(deviceAddress, result);
//                     } else {
//                         result.notImplemented();
//                     }
//                 }
//             );
//     }

//     private void startBluetoothScan(MethodChannel.Result result) {
//         if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
//             result.error("UNAVAILABLE", "Bluetooth is not available or enabled.", null);
//             return;
//         }
        
//         Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
//         StringBuilder devicesList = new StringBuilder();
//         for (BluetoothDevice device : pairedDevices) {
//             devicesList.append(device.getName()).append(" (").append(device.getAddress()).append(")\n");
//         }
//         result.success(devicesList.toString());
//     }

//     private void connectToDevice(String deviceAddress, MethodChannel.Result result) {
//         BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
//         if (device != null) {
//             // Implement connection logic for BLE or Bluetooth classic device here
//             result.success("Connected to " + device.getName());
//         } else {
//             result.error("ERROR", "Device not found.", null);
//         }
//     }
// }



//***************************************************Handle permission for Android 12 & above*************************/
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
            // Implement your BLE connection logic here if required.
            result.success("Connected to " + device.getName());
        } else {
            result.error("ERROR", "Device not found.", null);
        }
    }
}
