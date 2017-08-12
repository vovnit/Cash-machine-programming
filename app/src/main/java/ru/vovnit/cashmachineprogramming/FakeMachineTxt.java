package ru.vovnit.cashmachineprogramming;

import android.util.ArrayMap;

/**
 * Created by vovnit on 12/08/17.
 */

public class FakeMachineTxt {
    private String Name;
    private String Description;
    private String LineWidth;
    private String CodeTable;

    private CashMachine cashMachine;
    FakeMachineTxt() {
        Name="n ORION 100K";
        Description = "d Description";
        LineWidth = "w 16";
        CodeTable="a А-00 Б-01 В-02 Г-03 Д-04";

        cashMachine = new CashMachine();

        cashMachine.parseMachineFromTxt(Name);
        cashMachine.parseMachineFromTxt(Description);
        cashMachine.parseMachineFromTxt(LineWidth);
        cashMachine.parseMachineFromTxt(CodeTable);
    }

    public String getName() {
        return cashMachine.getName();
    }

    public String getDescription() {
        return cashMachine.getDescription();
    }

    public String getLineWidth() {
        return cashMachine.getLineWidth();
    }

    public String convertStr(String line) {
        return cashMachine.convert(line);
    }
}
