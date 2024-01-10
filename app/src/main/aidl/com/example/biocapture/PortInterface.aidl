// PortInterface.aidl
package com.example.biocapture;

// Declare any non-default types here with import statements

interface PortInterface {
  void setTorchLed(int upLedValue, int downLedValue) = 0;
  	void setFlashLed(int upLedValue, int downLedValue) = 1;
  	void setDefaultTorchLed(int upLedValue, int downLedValue) = 3;
  }
