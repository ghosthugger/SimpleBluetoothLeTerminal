package de.kai_morich.simple_bluetooth_le_terminal;

import java.util.Random;

public class BatteryStatus {
    public double cell1Voltage;
    public double cell2Voltage;
    public double cell3Voltage;
    public double cell4Voltage;
    public double batteryVoltage;
    public double currentFlow;
    public double chargeState;

    public BatteryStatus(double cell1Voltage,
            double cell2Voltage,
            double cell3Voltage,
            double cell4Voltage,
            double batteryVoltage,
            double currentFlow,
            double chargeState
            ) {
        this.cell1Voltage = cell1Voltage;
        this.cell2Voltage = cell2Voltage;
        this.cell3Voltage = cell3Voltage;
        this.cell4Voltage = cell4Voltage;
        this.batteryVoltage = batteryVoltage;
        this.currentFlow = currentFlow;
        this.chargeState = chargeState;
    }

    public static double random(Random r, double rangeMin, double rangeMax) {
        double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        return randomValue;
    }

    public static BatteryStatus getRandomState() {
        Random r = new Random();

        double cell1Voltage = random(r,2.0d,2.50d);
        double cell2Voltage = random(r,2.0d,2.50d);
        double cell3Voltage = random(r,2.0d,2.50d);
        double cell4Voltage = random(r,2.0d,2.50d);
        double batteryVoltage = cell1Voltage+cell2Voltage+cell3Voltage+cell4Voltage + random(r,0.0d,0.1d);
        double currentFlow = random(r,-100d,100d);
        double chargeState = random(r,0d,100d);

        BatteryStatus newInstance = new BatteryStatus(cell1Voltage,cell2Voltage,cell3Voltage,cell4Voltage,batteryVoltage,currentFlow,chargeState);
        return newInstance;
    }
}
