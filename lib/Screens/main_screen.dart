import 'package:flutter/material.dart';
import 'package:flutter/services.dart';


class MainScreen extends StatefulWidget {
  const MainScreen({super.key});

  @override
  _MainScreenState createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  static const platform = MethodChannel("cn.icomon.icdemo/ble");
  String _bleData = "No data";

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
          ElevatedButton(
            onPressed: () => _connectToDevice("B4:56:5D:5E:82:C8"),
            child: Text("Connect to Device"),
          ),
        ],
      ),
    );
  }
}
