package ru.vovnit.cashmachineprogramming;

import android.support.annotation.NonNull;
import android.util.ArrayMap;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public class CashMachine {
    private String Name;
    private String Description;
    private int LineWidth;
    private ArrayMap<Character, Integer> CodeTable;

    CashMachine() {
        CodeTable.put('A', 0);
        CodeTable.put('B', 1);
    }
    String convert(String str) {
        StringBuilder res=new StringBuilder();
        for (char ch : str.toCharArray()) {
            res.append(CodeTable.get(ch));
        }
        return res.toString();
    }
    private String joinRest(ArrayList<?> keywords, int i) {
        StringBuilder sb = new StringBuilder();
        for (Object word : keywords.subList(i, keywords.size())) {
            sb.append(word);
        }
        return sb.toString();
    }
    void parseMachineFromTxt(@NonNull String line) {
        if (line.isEmpty()) {
            return;
        }
        ArrayList<String> keywords = new ArrayList<>(Arrays.asList(line.split(" ")));

        switch (keywords.get(0)) {
            case "n": //name
                Name = joinRest(keywords, 1);
                break;
            case "d": //description
                Description = joinRest(keywords, 1);
                break;
            case "w": //width of line
                LineWidth = Integer.parseInt(keywords.get(1));
                break;
            case "a": //alphabet
                for (String word : keywords) {
                    int num = Integer.parseInt(word.substring(2));
                    if (word.charAt(1)=='-') {
                        CodeTable.put(word.charAt(0), num);
                    }
                }
                break;
        }
    }
}