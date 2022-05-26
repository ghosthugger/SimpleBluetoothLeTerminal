package de.kai_morich.simple_bluetooth_le_terminal;

import static org.junit.Assert.*;

import org.junit.Test;

public class BMS4SUtilTest {

    @Test
    public void parseLine() {
        String input="U=3.368 3.353 3.347 3.356 13.424 I=2.093 SoC=68";

        double tiny = 0.00001D;
        BatteryStatus status = BMS4SUtil.parseLine(input);
        assertEquals(3.368D, status.cell1Voltage, tiny);
        assertEquals(3.353D, status.cell2Voltage, tiny);
        assertEquals(3.347D, status.cell3Voltage, tiny);
        assertEquals(3.356D, status.cell4Voltage, tiny);
        assertEquals(13.424D, status.batteryVoltage, tiny);
        assertEquals(2.093D, status.currentFlow, tiny);
        assertEquals(68D, status.chargeState, tiny);
    }
}