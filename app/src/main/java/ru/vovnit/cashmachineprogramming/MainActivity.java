package ru.vovnit.cashmachineprogramming;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    CashMachine cashMachine;
    EditText editText;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);

        editText=(EditText)findViewById(R.id.OriginalEditText);
        textView=(TextView)findViewById(R.id.FormatedText);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            private String insert(String text, String insert, int period) {
                Pattern p = Pattern.compile("(.{" + period + "})", Pattern.DOTALL);
                Matcher m = p.matcher(text);
                return m.replaceAll("$1" + insert);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (cashMachine!=null) {
                    String expression = charSequence.toString();
                    int lineWidth= Integer.parseInt(cashMachine.getLineWidth());
                    expression=expression.replaceAll("(.{" + lineWidth + "})", "$1\n");
                    textView.setText(cashMachine.convert(expression));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    private int REQUEST_CODE=42;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                cashMachine=new CashMachine();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    Intent intent = null;
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("text/plain");
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
                    Snackbar.make( findViewById(R.id.mainLayout),
                            getString(R.string.low_android_version),
                            Snackbar.LENGTH_LONG).show();
                }
                break;
            case R.id.action_rotate:
                if (getResources().getConfiguration().orientation ==
                        Configuration.ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            MachineHandler machineHandler = new MachineHandler();
            String code = machineHandler.readFromTextFile(resultData, this);
            //code=code.replaceAll("\\P{Print}","");
            cashMachine.parseMachineFromTxt(code);
        }
    }

}
