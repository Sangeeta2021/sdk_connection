import 'package:ble_sdk_connection/utils/colors.dart';
import 'package:ble_sdk_connection/utils/constants.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class Home2 extends StatefulWidget {
  const Home2({super.key});

  @override
  State<Home2> createState() => _Home2State();
}

class _Home2State extends State<Home2> {
  static const MethodChannel _channel = MethodChannel('com.example.ble_sdk_connection/ble');
  List<String> _devices = [];

  @override
  void initState() {
    super.initState();
    _startScan();
  }

  Future<void> _startScan() async {
    try {
      await _channel.invokeMethod('startScan');
      _channel.setMethodCallHandler((call) async {
        if (call.method == "onDeviceFound") {
          List<dynamic> deviceDetails = call.arguments;
          setState(() {
            _devices.add("${deviceDetails[0]} (${deviceDetails[1]})");
          });
        }
      });
    } on PlatformException catch (e) {
      print("Error: ${e.message}");
    }
  }

  Future<void> _connectToDevice(String deviceId) async {
    try {
      await _channel.invokeMethod('connectToDevice', {'deviceId': deviceId});
    } on PlatformException catch (e) {
      print("Error: ${e.message}");
    }
  }
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: themeColor,
        centerTitle: true,
        title: Text('BLE SDK Connection Method 1a',style: appBarTitleStyle,),
      ),
      body: ListView.builder(
        itemCount: _devices.length,
        itemBuilder: (context, index) {
          String deviceInfo = _devices[index];
          return ListTile(
            title: Text(deviceInfo),
            trailing: ElevatedButton(
              onPressed: () {
                _connectToDevice(deviceInfo.split(" ")[1].replaceAll("(", "").replaceAll(")", ""));
              },
              child: Text('Connect'),
            ),
          );
        },
      ),
    );
  }
}