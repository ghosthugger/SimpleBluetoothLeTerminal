package de.kai_morich.simple_bluetooth_le_terminal;

import java.util.Arrays;
import java.util.List;

public final class BMS4SUtil {
    public static BatteryStatus parseLine(String line) {
        List<String> splited = Arrays.asList(line.split("\\s+"));
        try {
            if (splited.size() == 7
                && splited.get(0).substring(0,2).equals("U=")
                && splited.get(5).substring(0,2).equals("I=")
                && splited.get(6).substring(0,4).equals("SoC=")) {
                float c1v = Float.parseFloat(splited.get(0).substring(2, splited.get(0).length()));
                float c2v = Float.parseFloat(splited.get(1));
                float c3v = Float.parseFloat(splited.get(2));
                float c4v = Float.parseFloat(splited.get(3));
                float batteryVoltage = Float.parseFloat(splited.get(4));
                float currentFlow = Float.parseFloat(splited.get(5).substring(2, splited.get(5).length()));
                float soc = Float.parseFloat(splited.get(6).substring(4, splited.get(6).length()));

                return new BatteryStatus(c1v, c2v, c3v, c4v, batteryVoltage, currentFlow, soc);
            }
        }
        catch(NumberFormatException e) {

        }
        return null;
    }
}
