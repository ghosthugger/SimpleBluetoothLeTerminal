package de.kai_morich.simple_bluetooth_le_terminal;

import java.util.Random;

public class BatteryStatus {
    public double cell1Voltage;
    public double cell2Voltage;
    public double cell3Voltage;
    public double cell4Voltage;
    public double batteryVoltage;
    public double chargeState;
    public double currentFlow;

    public static double random(Random r, double rangeMin, double rangeMax) {
        double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        return randomValue;
    }

    public static BatteryStatus getRandomState() {
        BatteryStatus newInstance = new BatteryStatus();

        Random r = new Random();

        newInstance.cell1Voltage = random(r,2.0d,2.50d);
        newInstance.cell2Voltage = random(r,2.0d,2.50d);
        newInstance.cell3Voltage = random(r,2.0d,2.50d);
        newInstance.cell4Voltage = random(r,2.0d,2.50d);
        newInstance.batteryVoltage = newInstance.cell1Voltage+newInstance.cell2Voltage+newInstance.cell3Voltage+newInstance.cell4Voltage + random(r,0.0d,0.1d);
        newInstance.chargeState = random(r,0d,100d);
        newInstance.currentFlow = random(r,-100d,100d);

        return newInstance;
    }
}
