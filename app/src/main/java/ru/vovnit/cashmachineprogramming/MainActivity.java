package ru.vovnit.cashmachineprogramming;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.CashMachineContract;
import data.CashMachineContract.CashMachineEntry;
import data.MachineDbHelper;

public class MainActivity extends AppCompatActivity {

    CashMachine cashMachine;
    EditText editText;
    TextView formatedText;
    TextView descriptionText;
    boolean isUpper=false;
    Spinner spinner;
    MachineDbHelper dbHelper;
    ArrayAdapter<String> dataAdapter;
    List<String> names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(myToolbar);

        spinner=(Spinner)findViewById(R.id.ChooseSpinner);
        editText=(EditText)findViewById(R.id.OriginalEditText);
        formatedText =(TextView)findViewById(R.id.FormatedText);
        descriptionText = (TextView)findViewById(R.id.MachineDescription);
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
                    setTextFromCashMachine(expression);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        dbHelper=new MachineDbHelper(this);
        if (savedInstanceState!= null) {
            cashMachine = savedInstanceState.getParcelable("cashMachine");
            descriptionText.setText(savedInstanceState.getString("descriptionText"));
            if (!editText.getText().toString().isEmpty()) {
                setTextFromCashMachine(editText.getText().toString());
            }
        } else {
            cashMachine=new CashMachine();
        }

        names = dbHelper.getAllNames();
        // Creating adapter for spinner
        dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, names);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        SpinnerListener spinnerListener = new SpinnerListener(cashMachine, dbHelper,
                descriptionText, this, editText);
        spinner.setOnItemSelectedListener(spinnerListener);
    }

    void setTextFromCashMachine(String expression) {
        try {
            int lineWidth = Integer.parseInt(cashMachine.getLineWidth());
            expression = expression.replaceAll("(.{" + lineWidth + "})", "$1\n");
            formatedText.setText(cashMachine.convert(expression));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    private int REQUEST_CODE=42;
    private int REQUEST_LIST_CODE=103;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
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
                    break;
                }
            case R.id.action_list:
                Intent intent = new Intent(this, CashMachinesActivity.class);
                startActivityForResult(intent, REQUEST_LIST_CODE);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String code = readFromTextFile(resultData, this);
            //code=code.replaceAll("\\P{Print}","");
            final ArrayList<String> lines = cashMachine.parseMachineFromTxt(code);
            editText.setText(editText.getText());
            AsyncTask<Void, Void, Void> addToDatabase = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    for (String line : lines) {
                        switch (line.charAt(0)) {
                            case 'n':
                                cv.put(CashMachineEntry.COLUMN_NAME, line.substring(2));
                                break;
                            case 'd':
                                cv.put(CashMachineEntry.COLUMN_DESCRIPTION, line.substring(2));
                                break;
                            case 'w':
                                cv.put(CashMachineEntry.COLUMN_WIDTH, line.substring(2));
                                break;
                            case 'a':
                                cv.put(CashMachineEntry.COLUMN_ALPHABET, line.substring(2));
                                break;
                            default:
                                break;
                        }
                    }
                    boolean replaced=false;
                    Cursor cursor = db.query(CashMachineContract.CashMachineEntry.TABLE_NAME,
                            null, null, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        int idColIndex = cursor.getColumnIndex(CashMachineContract.CashMachineEntry._ID);
                        int nameColIndex = cursor.getColumnIndex(CashMachineContract.CashMachineEntry.COLUMN_NAME);
                        do {
                            if (cursor.getString(nameColIndex)
                                    .equals(cv.getAsString(CashMachineEntry.COLUMN_NAME))) {
                                db.update(CashMachineEntry.TABLE_NAME, cv,
                                        "_id="+cursor.getString(idColIndex), null);
                                replaced=true;
                                break;
                            }
                        } while (cursor.moveToNext());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (descriptionText!=null) {
                                    descriptionText.setText(cashMachine.getDescription() +
                                            getResources().getString(R.string.width_of_line) + " " +
                                            cashMachine.getLineWidth());
                                }
                            }
                        });
                    }
                    if (!replaced) {
                        db.insert(CashMachineEntry.TABLE_NAME, null, cv);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            names.clear();
                            names.addAll(dbHelper.getAllNames());
                            ((BaseAdapter)spinner.getAdapter()).notifyDataSetChanged();
                            cashMachine.clear();
                            if (names.isEmpty()) {
                                descriptionText.setText(getString(R.string.cash_machine_description));
                                formatedText.setText(getString(R.string.cash_machine_description));
                            } else {
                                getFirstMachine();
                                editText.setText(editText.getText());
                            }
                        }
                    });

                    cursor.close();
                    return null;
                }
            }.execute();
        } else if(requestCode == REQUEST_LIST_CODE) {
            names.clear();
            names.addAll(dbHelper.getAllNames());
            ((BaseAdapter)spinner.getAdapter()).notifyDataSetChanged();
            if (names.isEmpty()) {
                cashMachine.clear();
                descriptionText.setText(getString(R.string.cash_machine_description));
                formatedText.setText(getString(R.string.edited_text));
            } else {
                getFirstMachine();
                editText.setText(editText.getText());
            }
        }
    }
    void getFirstMachine() {
        cashMachine.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(CashMachineContract.CashMachineEntry.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idColIndex = cursor.getColumnIndex(CashMachineContract.CashMachineEntry._ID);
            int nameColIndex = cursor.getColumnIndex(CashMachineContract.CashMachineEntry.COLUMN_NAME);
            int descColIndex = cursor.getColumnIndex(CashMachineContract.CashMachineEntry.COLUMN_DESCRIPTION);
            int widthColIndex = cursor.getColumnIndex(CashMachineContract.CashMachineEntry.COLUMN_WIDTH);
            int alphabetColIndex = cursor.getColumnIndex(CashMachineContract.CashMachineEntry.COLUMN_ALPHABET);
            cashMachine.parseMachineFromTxt("n " + cursor.getString(nameColIndex));
            cashMachine.parseMachineFromTxt("d " + cursor.getString(descColIndex));
            cashMachine.parseMachineFromTxt("w " + cursor.getString(widthColIndex));
            cashMachine.parseMachineFromTxt("a " + cursor.getString(alphabetColIndex));
            if (descriptionText != null) {
                descriptionText.setText(cashMachine.getDescription() +
                        getResources().getString(R.string.width_of_line) + " " +
                        cashMachine.getLineWidth());
            }
        } else {
            cashMachine.clear();
        }
    }

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("cashMachine", cashMachine);
        outState.putString("descriptionText", descriptionText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    public void onButtonsCLick(View view) {
        switch (view.getId()) {
            case R.id.buttonCenter:
                if (!cashMachine.isEmpty()) {
                    int width = Integer.parseInt(cashMachine.getLineWidth());
                    ArrayList<String> lines = new ArrayList<>();
                    for (String line : editText.getText().toString().split("\n")) {
                        if (line.length() > width) {
                            line = line.replaceAll("(.{" + width + "})", "$1\n");
                            lines.addAll(Arrays.asList(line.split("\n")));
                        } else {
                            lines.add(line);
                        }
                    }
                    StringBuilder res = new StringBuilder();
                    for (String line : lines) {
                        int length = line.length();
                        if (length < width) {
                            int p = (width - length) / 2;
                            while (p > 0) {
                                res.append(" ");
                                --p;
                            }
                            res.append(line);
                            res.append("\n");
                        } else {
                            res.append(line);
                            res.append("\n");
                        }
                    }
                    editText.setText(res.toString());
                } else {
                    Snackbar.make( findViewById(R.id.mainLayout),
                            getString(R.string.no_cr),
                            Snackbar.LENGTH_LONG).show();
                }
                break;
            case R.id.buttonUpper:
                if (editText.getText().toString().isEmpty()) {
                    Snackbar.make( findViewById(R.id.mainLayout),
                            getString(R.string.no_text),
                            Snackbar.LENGTH_LONG).show();
                } else {
                    if (!isUpper) {
                        editText.setText(editText.getText().toString().toUpperCase());
                        isUpper = true;
                    } else {
                        editText.setText(editText.getText().toString().toLowerCase());
                        isUpper = false;
                    }
                }
                break;
            default:
                break;
        }
    }
}
