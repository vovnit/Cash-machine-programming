package ru.vovnit.cashmachineprogramming;

import android.support.v4.util.ArrayMap;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class CashMachine {
    private String Name;
    private String Description;
    private String LineWidth;
    private ArrayMap<Character, String> CodeTable;

    CashMachine() {
        CodeTable = new ArrayMap<>();
    }

    String convert(String str) {
        StringBuilder res=new StringBuilder();
        for (char ch : str.toCharArray()) {
            if (ch=='\n') {
                res.append("\n");
                continue;
            }
            String add = CodeTable.get(ch);
            if (add!=null) {
                res.append(add);
            } else {
                res.append("-");
            }
            res.append(".");
        }
        return res.toString();
    }

    private String joinRest(ArrayList<?> keywords, int i) {
        StringBuilder sb = new StringBuilder();
        for (Object word : keywords.subList(i, keywords.size())) {
            sb.append(word);
            sb.append(" ");
        }
        return sb.toString();
    }

    void parseMachineFromTxt(String code) {
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(code.split("\n")));
        for (String line : lines) {
            ArrayList<String> keywords = new ArrayList<>(Arrays.asList(line.split(" ")));
            switch (keywords.get(0)) {
                case "n": //name
                    Name = joinRest(keywords, 1);
                    break;
                case "d": //description
                    Description = joinRest(keywords, 1);
                    break;
                case "w": //width of line
                    LineWidth = keywords.get(1);
                    break;
                case "a": //alphabet
                    boolean space = false;
                    for (String word : keywords.subList(1, keywords.size())) {
                        if (word.isEmpty()) {
                            space = true;
                            continue;
                        }
                        if (space) {
                            word = " " + word;
                        }
                        if (word.charAt(1) == '-') {
                            CodeTable.put(word.charAt(0), word.substring(2));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public String getName() {
        return Name;
    }

    public String getDescription() {
        return Description;
    }

    public String getLineWidth() {
        return LineWidth;
    }
}