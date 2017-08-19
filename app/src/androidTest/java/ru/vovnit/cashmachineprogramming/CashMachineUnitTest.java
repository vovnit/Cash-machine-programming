package ru.vovnit.cashmachineprogramming;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
/**
 * Created by vovnit on 16/08/17.
 */

@RunWith(AndroidJUnit4.class)
public class CashMachineUnitTest {
    FakeMachineTxt fakeMachineTxt = new FakeMachineTxt();
    CashMachine cashMachine = fakeMachineTxt.getCashMachine();
    @Test
    public void simpleCheck() {
        assertEquals("15.16.14.01.05.11.", cashMachine.convert("ПРОБЕЛ"));
    }

    @Test
    public void checkWithSpaces() {
        assertEquals("19.33.14.10.13.00.33.05.17.18.28.33.", cashMachine.convert("У ОКНА ЕСТЬ "));
    }

    @Test
    public void checkDash() {
        assertEquals("34.", cashMachine.convert("-"));
    }

    private class FakeMachineTxt {

        private CashMachine cashMachine;
        FakeMachineTxt() {
            String name = "n ORION 100K";
            String description = "d Description";
            String lineWidth = "w 16";
            String codeTable = "a А-00 Б-01 В-02 Г-03 Д-04 Е-05 Ж-06 З-07 И-08 Й-09 К-10 Л-11 " +
                    "М-12 Н-13 О-14 П-15 " +
                    "Р-16 С-17 Т-18 У-19 Ф-20 Х-21 Ц-22 Ч-23 Ш-24 Щ-25 Ъ-26 Ы-27 Ь-28 Э-29 Ю-30 " +
                    "Я-31 №-32  -33 --34";

            cashMachine = new CashMachine();

            cashMachine.parseMachineFromTxt(name);
            cashMachine.parseMachineFromTxt(description);
            cashMachine.parseMachineFromTxt(lineWidth);
            cashMachine.parseMachineFromTxt(codeTable);
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

        public CashMachine getCashMachine() {
            return cashMachine;
        }
    }
}
