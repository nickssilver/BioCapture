package com.idemia.peripherals;

// Declare any non-default types here with import statements
interface PeripheralsPowerInterface {


    boolean setFingerPrintSwitch(boolean flag);
    boolean getFingerPrintSwitch();

    boolean setHostUsbPortSwitch(boolean flag);
    boolean getHostUsbPortSwitch();

    boolean setDockingStationUsbPortSwitch(boolean flag);
    boolean getDockingStationUsbPortSwitch();

    boolean setNfcSwitch(boolean flag);
    boolean getNfcSwitch();

    void setUSBRole(int role);
    int getUSBRole();
}
