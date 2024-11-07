
//********************************screen for android 12 & above*******************************/
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class Home3 extends StatefulWidget {
  const Home3({super.key});

  @override
  State<Home3> createState() => _Home3State();
}

class _Home3State extends State<Home3> {
  static const platform = MethodChannel('com.example.ble_sdk_connection/ble');
  List<DeviceDetails> devices = [];

  @override
  void initState() {
    super.initState();
    _startScan();
  }

  Future<void> _startScan() async {
    try {
      final List<dynamic> deviceList = await platform.invokeMethod('startScan');
      setState(() {
        devices = deviceList.map((device) => DeviceDetails.fromMap(device)).toList();
      });
    } on PlatformException catch (e) {
      print("Failed to start scan: ${e.message}");
    }
  }

  Future<void> _connectToDevice(String deviceAddress) async {
    try {
      await platform.invokeMethod('connectToDevice', {'deviceAddress': deviceAddress});
    } on PlatformException catch (e) {
      print("Failed to connect: ${e.message}");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Bluetooth Device Scanner'),
        centerTitle: true,
      ),
      body: ListView.builder(
        itemCount: devices.length,
        itemBuilder: (context, index) {
          return ListTile(
            title: Text(devices[index].name),
            subtitle: Text(devices[index].address),
            trailing: ElevatedButton(
              onPressed: () => _connectToDevice(devices[index].address),
              child: const Text("Connect"),
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

  factory DeviceDetails.fromMap(Map<String, dynamic> map) {
    return DeviceDetails(
      name: map['name'] as String,
      address: map['address'] as String,
    );
  }
}