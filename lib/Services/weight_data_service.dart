// lib/services/weight_data_service.dart
import 'package:flutter/services.dart';

class WeightDataService {
  static const platform = MethodChannel('com.example.weight_machine/data');

  void initialize() {
    // Initialize if needed
  }

  void startWeightDataListener(Function(String) onDataReceived) {
    platform.setMethodCallHandler((call) async {
      if (call.method == "onWeightDataReceived") {
        final String data = call.arguments;
        onDataReceived(data);
      }
    });
  }

  Future<void> startScan() async {
    try {
      await platform.invokeMethod('startScan');
    } catch (e) {
      print("Error starting scan: $e");
    }
  }
}
