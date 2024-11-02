// // import 'package:flutter/material.dart';
// // import 'package:flutter/services.dart';

// // class HomeScreen extends StatefulWidget {
// //   @override
// //   _HomeScreenState createState() => _HomeScreenState();
// // }

// // class _HomeScreenState extends State<HomeScreen> {
// //   static const platform = MethodChannel('ble_device_channel');
// //   String weightData = 'No data';

// //   @override
// //   void initState() {
// //     super.initState();
// //     initializeBLE();
// //   }

// //   Future<void> initializeBLE() async {
// //     try {
// //       await platform.invokeMethod('initialize');
// //     } on PlatformException catch (e) {
// //       print("Failed to initialize BLE: '${e.message}'.");
// //     }
// //   }

// //   Future<void> startScan() async {
// //     try {
// //       await platform.invokeMethod('startScan');
// //     } on PlatformException catch (e) {
// //       print("Failed to start scan: '${e.message}'.");
// //     }
// //   }

// //   Future<void> connectToDevice(String macAddress) async {
// //     try {
// //       await platform.invokeMethod('connectDevice', {"macAddress": macAddress});
// //     } on PlatformException catch (e) {
// //       print("Failed to connect: '${e.message}'.");
// //     }
// //   }

// //   @override
// //   Widget build(BuildContext context) {
// //     return Scaffold(
// //       appBar: AppBar(title: Text('BLE Weight Machine')),
// //       body: Center(
// //         child: Column(
// //           mainAxisAlignment: MainAxisAlignment.center,
// //           children: <Widget>[
// //             Text('Weight Data: $weightData kg'),
// //             SizedBox(height: 20),
// //             ElevatedButton(
// //               onPressed: startScan,
// //               child: Text('Scan for Devices'),
// //             ),
// //             ElevatedButton(
// //               onPressed: () => connectToDevice('00:11:22:AA:BB:CC'), // Example MAC
// //               child: Text('Connect Device'),
// //             ),
// //           ],
// //         ),
// //       ),
// //     );
// //   }
// // }


// // lib/main_screen.dart
// import 'package:flutter/material.dart';

// import '../Services/weight_data_service.dart';

// class MainScreen extends StatefulWidget {
//   @override
//   _MainScreenState createState() => _MainScreenState();
// }

// class _MainScreenState extends State<MainScreen> {
//   final WeightDataService _weightDataService = WeightDataService();
//   String weightData = "Awaiting data...";

//   @override
//   void initState() {
//     super.initState();
//     _weightDataService.initialize();
//     _weightDataService.startWeightDataListener((data) {
//       setState(() {
//         weightData = data;
//       });
//     });
//     _weightDataService.startScan();
//   }

//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       appBar: AppBar(
//         title: Text('Weight Data'),
//       ),
//       body: Center(
//         child: Text(
//           weightData,
//           style: TextStyle(fontSize: 24),
//         ),
//       ),
//     );
//   }
// }


import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class MainScreen extends StatefulWidget {
  @override
  _MainScreenState createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  static const platform = MethodChannel("com.example.ble_sdk_connection/ble");
  String _bleData = "No data";
  List<String> _devices = [];

  @override
  void initState() {
    super.initState();
    platform.setMethodCallHandler(_methodCallHandler);
    _initializeSDK();
  }

  Future<void> _methodCallHandler(MethodCall call) async {
    switch (call.method) {
      case "onWeightDataReceived":
        setState(() {
          _bleData = call.arguments;
        });
        break;
      case "onDeviceFound":
        setState(() {
          _devices.add(call.arguments);
        });
        break;
    }
  }

  Future<void> _initializeSDK() async {
    try {
      await platform.invokeMethod("initializeSDK");
    } catch (e) {
      print("Failed to initialize SDK: $e");
    }
  }

  Future<void> _startScan() async {
    try {
      await platform.invokeMethod("startScan");
    } catch (e) {
      print("Failed to start scan: $e");
    }
  }

  Future<void> _connectToDevice(String macAddress) async {
    try {
      await platform.invokeMethod("connectDevice", {"macAddress": macAddress});
    } catch (e) {
      print("Failed to connect: $e");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text("BLE Weight Machine")),
      body: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: <Widget>[
          Text("Weight Data: $_bleData"),
          ElevatedButton(
            onPressed: _startScan,
            child: Text("Start Scan"),
          ),
          ..._devices.map((device) => ListTile(
                title: Text(device),
                trailing: ElevatedButton(
                  onPressed: () => _connectToDevice(device),
                  child: Text("Connect"),
                ),
              )),
        ],
      ),
    );
  }
}
