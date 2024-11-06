// import 'package:ble_sdk_connection/utils/colors.dart';
// import 'package:ble_sdk_connection/utils/constants.dart';
// import 'package:flutter/material.dart';
// import 'package:flutter/services.dart';

// class MainScreen extends StatefulWidget {
//   const MainScreen({super.key});

//   @override
//   _MainScreenState createState() => _MainScreenState();
// }

// class _MainScreenState extends State<MainScreen> {
//   static const platform = MethodChannel("com.example.ble_sdk_connection/ble");
//   String _weightData = "No data";
//   List<String> _availableDevices = [];

//   @override
//   void initState() {
//     super.initState();
//     platform.setMethodCallHandler(_methodCallHandler);
//     _initializeSDK();
//   }

//   Future<void> _methodCallHandler(MethodCall call) async {
//     switch (call.method) {
//       case "onWeightDataReceived":
//         setState(() {
//           _weightData = call.arguments;
//         });
//         break;
//       case "onDeviceFound":
//         setState(() {
//           _availableDevices.add(call.arguments);
//         });
//         break;
//     }
//   }

//   Future<void> _initializeSDK() async {
//     try {
//       await platform.invokeMethod("initializeSDK");
//     } catch (e) {
//       print("Failed to initialize SDK: $e");
//     }
//   }

//   Future<void> _startScan() async {
//     try {
//       _availableDevices.clear();
//       await platform.invokeMethod("startScan");
//     } catch (e) {
//       print("Failed to start scan: $e");
//     }
//   }

//   Future<void> _connectToDevice(String macAddress) async {
//     try {
//       await platform.invokeMethod("connectDevice", {"macAddress": macAddress});
//     } catch (e) {
//       print("Failed to connect: $e");
//     }
//   }

//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       backgroundColor: bgColor,
//       appBar: AppBar(
//         centerTitle: true,
//         backgroundColor: themeColor,

//         title: Text("BLE Weight Tracker", style: appBarTitleStyle,)),
//       body: Padding(
//         padding: const EdgeInsets.symmetric(vertical:20,horizontal: 16 ),
//         child: Column(
//           mainAxisAlignment: MainAxisAlignment.center,
//           children: <Widget>[
//             Text("Weight Data: $_weightData", style: blackTitleStyle),
//             SizedBox(height: 20,),
//             ElevatedButton(
//               style: ElevatedButton.styleFrom(
//                 backgroundColor: themeColor
//               ),
//               onPressed: _startScan,
//               child: Padding(
//                 padding: const EdgeInsets.symmetric(vertical: 4,horizontal: 12),
//                 child: Text("Start Scan", style: buttonTextStyle,),
//               ),
//             ),
//             Expanded(
//               child: ListView.builder(
//                 itemCount: _availableDevices.length,
//                 itemBuilder: (context, index) {
//                   return Padding(
//                     padding: const EdgeInsets.only(bottom: 8),
//                     child: ListTile(
//                       shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12),),
//                       tileColor: wColor,
//                       title: Text(_availableDevices[index]),
//                       trailing: ElevatedButton(
//                         style: ElevatedButton.styleFrom(backgroundColor: themeColor),
//                         onPressed: () => _connectToDevice(_availableDevices[index]),
//                         child: Text("Connect", style: buttonTextStyle,),
//                       ),
//                     ),
//                   );
//                 },
//               ),
//             ),
//           ],
//         ),
//       ),
//     );
//   }
// }

//**********************************Updated Code********************************* */
import 'package:ble_sdk_connection/utils/colors.dart';
import 'package:ble_sdk_connection/utils/constants.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class MainScreen extends StatefulWidget {
  const MainScreen({super.key});

  @override
  State<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  static const platform = MethodChannel('com.example.ble_sdk_connection/ble');
  List<DeviceDetails> devices = [];

  @override
  void initState() {
    super.initState();
    _startScan();
  }

  // Start scanning for devices
  Future<void> _startScan() async {
    try {
      await platform.invokeMethod('startScan');
      platform.setMethodCallHandler((call) async {
        if (call.method == "onDeviceFound") {
          final List<dynamic> deviceList = call.arguments;
          setState(() {
            devices = deviceList
                .map((device) => DeviceDetails.fromMap(device))
                .toList();
          });
        }
      });
    } on PlatformException catch (e) {
      print("Failed to start scan: ${e.message}");
    }
  }

  // Connect to a selected device
  Future<void> _connectToDevice(String deviceAddress) async {
    try {
      await platform
          .invokeMethod('connectDevice', {'deviceAddress': deviceAddress});
    } on PlatformException catch (e) {
      print("Failed to connect: ${e.message}");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: bgColor,
      appBar: AppBar(
        backgroundColor: themeColor,
        centerTitle: true,
        title: Text(
          'Bluetooth Device Scanner',
          style: appBarTitleStyle,
        ),
      ),
      body: ListView.builder(
        itemCount: devices.length,
        itemBuilder: (context, index) {
          return ListTile(
            tileColor: themeColor,
            title: Text(devices[index].name, style: blackTitleStyle,),
            subtitle: Text(devices[index].address, style: blackContenteStyle,),
            trailing: ElevatedButton(
              onPressed: () {
                _connectToDevice(devices[index].address);
              },
              child: Text("Connect",style: blackTitleStyle,),
            ),
          );
        },
      ),
    );
  }
}

class DeviceDetails {
  final String name;
  final String address;

  DeviceDetails({required this.name, required this.address});

  // Convert from map to DeviceDetails object
  factory DeviceDetails.fromMap(Map<String, dynamic> map) {
    return DeviceDetails(
      name: map['name'],
      address: map['address'],
    );
  }
}
