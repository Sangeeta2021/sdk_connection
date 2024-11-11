// import 'package:ble_sdk_connection/utils/colors.dart';
// import 'package:ble_sdk_connection/utils/constants.dart';
// import 'package:flutter_blue/flutter_blue.dart';
// import 'package:flutter/material.dart';
//
// class Home4 extends StatefulWidget {
//   const Home4({super.key});
//
//   @override
//   State<Home4> createState() => _Home4State();
// }
//
// class _Home4State extends State<Home4> {
//   FlutterBlue flutterBlue = FlutterBlue.instance;
//   List<BluetoothDevice> devicesList = [];
//   BluetoothDevice? connectedDevice;
//
//   @override
//   void initState() {
//     super.initState();
//     _startScan();
//   }
//
//   // Start scanning for devices
//   void _startScan() {
//     flutterBlue.startScan(timeout: Duration(seconds: 4));
//
//     flutterBlue.scanResults.listen((results) {
//       // Update the devices list with the results
//       for (ScanResult result in results) {
//         setState(() {
//           if (!devicesList.contains(result.device)) {
//             devicesList.add(result.device);
//           }
//         });
//       }
//     });
//
//     flutterBlue.isScanning.listen((isScanning) {
//       if (isScanning) {
//         print("Scanning for devices...");
//       } else {
//         print("Scan stopped.");
//       }
//     });
//   }
//
//   // Connect to a selected device and discover its services
//   Future<void> _connectToDevice(BluetoothDevice device) async {
//     await device.connect();
//     setState(() {
//       connectedDevice = device;
//     });
//
//     // Discover services and characteristics
//     List<BluetoothService> services = await device.discoverServices();
//
//     // Iterate through the discovered services and their characteristics
//     for (BluetoothService service in services) {
//       print('Service UUID: ${service.uuid}');
//       for (BluetoothCharacteristic characteristic in service.characteristics) {
//         print('Characteristic UUID: ${characteristic.uuid}');
//
//         // If the device has a weight characteristic, subscribe to it (you may need to adjust this)
//         await characteristic.setNotifyValue(true);
//         characteristic.value.listen((value) {
//           // Handle the value, for example, print or process the weight
//           print("Weight Data: $value");
//         });
//       }
//     }
//   }
//
//   // Stop the scan and disconnect from the device
//   void _disconnectDevice() {
//     connectedDevice?.disconnect();
//     setState(() {
//       connectedDevice = null;
//     });
//   }
//
//   // UI to display available devices
//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       appBar: AppBar(
//         centerTitle: true,
//         backgroundColor: themeColor,
//         title: Text('BLE SDK Connection Method 1C',
//         style: appBarTitleStyle,),
//       ),
//       body: Column(
//         children: [
//           Expanded(
//             child: ListView.builder(
//               itemCount: devicesList.length,
//               itemBuilder: (context, index) {
//                 return ListTile(
//                   tileColor: themeColor,
//                   shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8),),
//                   title: Text(devicesList[index].name, style: blackTitleStyle,),
//                   subtitle: Text(devicesList[index].id.toString(), style: blackContenteStyle,),
//                   onTap: () {
//                     _connectToDevice(devicesList[index]);
//                   },
//                 );
//               },
//             ),
//           ),
//           if (connectedDevice != null)
//             ElevatedButton(
//               onPressed: _disconnectDevice,
//               child: Text('Disconnect from ${connectedDevice!.name}'),
//             ),
//         ],
//       ),
//     );
//   }
// }


//*********************Updated code with location permission***************************
import 'package:flutter/material.dart';
import 'package:flutter_blue/flutter_blue.dart';
import 'package:permission_handler/permission_handler.dart';

import '../utils/colors.dart';
import '../utils/constants.dart';

class HomeScreen4 extends StatefulWidget {
  const HomeScreen4({super.key});

  @override
  State<HomeScreen4> createState() => _HomeScreen4State();
}

class _HomeScreen4State extends State<HomeScreen4> {
  FlutterBlue flutterBlue = FlutterBlue.instance;
  List<BluetoothDevice> devicesList = [];
  BluetoothDevice? connectedDevice;

  @override
  void initState() {
    super.initState();
    _requestPermissions(); // Requesting permissions at the start
  }

  // Request location and Bluetooth permissions
  Future<void> _requestPermissions() async {
    Map<Permission, PermissionStatus> statuses = await [
      Permission.location,
      Permission.bluetooth,
      Permission.bluetoothScan,
      Permission.bluetoothConnect,
    ].request();

    bool allGranted = statuses.values.every((status) => status.isGranted);

    if (allGranted) {
      _startScan();
    } else {
      print("Permissions not granted");
    }
  }

  // Start scanning for devices
  void _startScan() {
    flutterBlue.startScan(timeout: Duration(seconds: 4));

    flutterBlue.scanResults.listen((results) {
      // Update the devices list with the results
      for (ScanResult result in results) {
        setState(() {
          if (!devicesList.contains(result.device)) {
            devicesList.add(result.device);
          }
        });
      }
    });

    flutterBlue.isScanning.listen((isScanning) {
      if (isScanning) {
        print("Scanning for devices...");
      } else {
        print("Scan stopped.");
      }
    });
  }

  // Connect to a selected device and discover its services
  Future<void> _connectToDevice(BluetoothDevice device) async {
    await device.connect();
    setState(() {
      connectedDevice = device;
    });

    // Discover services and characteristics
    List<BluetoothService> services = await device.discoverServices();

    // Iterate through the discovered services and their characteristics
    for (BluetoothService service in services) {
      print('Service UUID: ${service.uuid}');
      for (BluetoothCharacteristic characteristic in service.characteristics) {
        print('Characteristic UUID: ${characteristic.uuid}');

        // If the device has a weight characteristic, subscribe to it (you may need to adjust this)
        await characteristic.setNotifyValue(true);
        characteristic.value.listen((value) {
          // Handle the value, for example, print or process the weight
          print("Weight Data: $value");
        });
      }
    }
  }

  // Stop the scan and disconnect from the device
  void _disconnectDevice() {
    connectedDevice?.disconnect();
    setState(() {
      connectedDevice = null;
    });
  }
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        centerTitle: true,
        backgroundColor: themeColor,
        title: Text('BLE SDK Connection Method 1C', style: appBarTitleStyle),
      ),
      body: Column(
        children: [
          Expanded(
            child: ListView.builder(
              itemCount: devicesList.length,
              itemBuilder: (context, index) {
                return ListTile(
                  tileColor: themeColor,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                  title: Text(devicesList[index].name, style: blackTitleStyle),
                  subtitle: Text(devicesList[index].id.toString(), style: blackContenteStyle),
                  onTap: () {
                    _connectToDevice(devicesList[index]);
                  },
                );
              },
            ),
          ),
          if (connectedDevice != null)
            ElevatedButton(
              onPressed: _disconnectDevice,
              child: Text('Disconnect from ${connectedDevice!.name}'),
            ),
        ],
      ),
    );
  }
}
