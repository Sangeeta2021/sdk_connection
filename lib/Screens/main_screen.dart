

// import 'package:flutter/material.dart';
// import 'package:flutter/services.dart';

// class MainScreen extends StatefulWidget {
//   @override
//   _MainScreenState createState() => _MainScreenState();
// }

// class _MainScreenState extends State<MainScreen> {
//   static const platform = MethodChannel("com.example.ble_sdk_connection/ble");
//   String _bleData = "No data";
//   List<String> _devices = [];

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
//           _bleData = call.arguments;
//         });
//         break;
//       case "onDeviceFound":
//         setState(() {
//           _devices.add(call.arguments);
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
//       appBar: AppBar(title: Text("BLE Weight Machine")),
//       body: Column(
//         mainAxisAlignment: MainAxisAlignment.center,
//         children: <Widget>[
//           Text("Weight Data: $_bleData"),
//           ElevatedButton(
//             onPressed: _startScan,
//             child: Text("Start Scan"),
//           ),
//           ..._devices.map((device) => ListTile(
//                 title: Text(device),
//                 trailing: ElevatedButton(
//                   onPressed: () => _connectToDevice(device),
//                   child: Text("Connect"),
//                 ),
//               )),
//         ],
//       ),
//     );
//   }
// }


import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class MainScreen extends StatefulWidget {
  const MainScreen({super.key});

  @override
  _MainScreenState createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  static const platform = MethodChannel("com.example.ble_sdk_connection/ble");
  String _weightData = "No data";
  List<String> _availableDevices = [];

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
          _weightData = call.arguments;
        });
        break;
      case "onDeviceFound":
        setState(() {
          _availableDevices.add(call.arguments);
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
      _availableDevices.clear();
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
          Text("Weight Data: $_weightData", style: TextStyle(fontSize: 24)),
          ElevatedButton(
            onPressed: _startScan,
            child: Text("Start Scan"),
          ),
          Expanded(
            child: ListView.builder(
              itemCount: _availableDevices.length,
              itemBuilder: (context, index) {
                return ListTile(
                  title: Text(_availableDevices[index]),
                  trailing: ElevatedButton(
                    onPressed: () => _connectToDevice(_availableDevices[index]),
                    child: Text("Connect"),
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}
