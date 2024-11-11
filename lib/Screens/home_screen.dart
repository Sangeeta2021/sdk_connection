import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class HomeScreen extends StatefulWidget {
  @override
  _HomeScreenState createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  static const platform = MethodChannel('ble_device_channel');
  String weightData = 'No data';

  @override
  void initState() {
    super.initState();
    initializeBLE();
  }

  Future<void> initializeBLE() async {
    try {
      await platform.invokeMethod('initialize');
    } on PlatformException catch (e) {
      print("Failed to initialize BLE: '${e.message}'.");
    }
  }

  Future<void> startScan() async {
    try {
      await platform.invokeMethod('startScan');
    } on PlatformException catch (e) {
      print("Failed to start scan: '${e.message}'.");
    }
  }

  Future<void> connectToDevice(String macAddress) async {
    try {
      await platform.invokeMethod('connectDevice', {"macAddress": macAddress});
    } on PlatformException catch (e) {
      print("Failed to connect: '${e.message}'.");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('BLE Weight Machine')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text('Weight Data: $weightData kg'),
            SizedBox(height: 20),
            ElevatedButton(
              onPressed: startScan,
              child: Text('Scan for Devices'),
            ),
            ElevatedButton(
              onPressed: () => connectToDevice('B4:56:5D:5E:82:C8'), // device MAC
              child: Text('Connect Device'),
            ),
          ],
        ),
      ),
    );
  }
}


