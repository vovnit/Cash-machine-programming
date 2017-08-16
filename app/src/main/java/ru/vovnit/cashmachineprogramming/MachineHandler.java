package ru.vovnit.cashmachineprogramming;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by vovnit on 16/08/17.
 */

public class MachineHandler {
    String readFromTextFile(Intent resultData, Context context) {
        String res="";
        if (resultData != null) {
            Uri uri = resultData.getData();
            InputStream inputStream = null;
            try {
                inputStream = context.getContentResolver().openInputStream(uri);

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append("\n");
                }
                res=stringBuilder.toString();

                reader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }
}
