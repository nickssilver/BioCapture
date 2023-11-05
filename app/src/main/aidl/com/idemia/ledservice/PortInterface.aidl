package com.idemia.ledservice;

interface PortInterface {
	void setTorchLed(int upLedValue, int downLedValue) = 0;
	void setFlashLed(int upLedValue, int downLedValue) = 1;
	void setDefaultTorchLed(int upLedValue, int downLedValue) = 3;
}
