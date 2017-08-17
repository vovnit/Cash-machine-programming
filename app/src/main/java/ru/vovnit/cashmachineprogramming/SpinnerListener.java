package ru.vovnit.cashmachineprogramming;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import data.CashMachineContract;
import data.MachineDbHelper;

/**
 * Created by vovnit on 16/08/17.
 */

public class SpinnerListener implements AdapterView.OnItemSelectedListener {
    private CashMachine cashMachine;
    private MachineDbHelper dbHelper;
    private Context context;
    private TextView descriptionText;

    SpinnerListener(CashMachine cashMachine, MachineDbHelper machineDbHelper,
                    TextView descriptionText, Context context) {
        this.cashMachine=cashMachine;
        this.dbHelper=machineDbHelper;
        this.descriptionText=descriptionText;
        this.context=context;
    }
    @Override
    public void onItemSelected(final AdapterView<?> adapterView, View view, int i, long l) {
        cashMachine.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(CashMachineContract.CashMachineEntry.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idColIndex = cursor.getColumnIndex(CashMachineContract.CashMachineEntry._ID);
            int nameColIndex = cursor.getColumnIndex(CashMachineContract.CashMachineEntry.COLUMN_NAME);
            int descColIndex = cursor.getColumnIndex(CashMachineContract.CashMachineEntry.COLUMN_DESCRIPTION);
            int widthColIndex = cursor.getColumnIndex(CashMachineContract.CashMachineEntry.COLUMN_WIDTH);
            int alphabetColIndex = cursor.getColumnIndex(CashMachineContract.CashMachineEntry.COLUMN_ALPHABET);
            do {
                if (cursor.getString(nameColIndex)
                        .equals(adapterView.getItemAtPosition(i).toString())) {
                    cashMachine.parseMachineFromTxt("n " + cursor.getString(nameColIndex));
                    cashMachine.parseMachineFromTxt("d " + cursor.getString(descColIndex));
                    cashMachine.parseMachineFromTxt("w " + cursor.getString(widthColIndex));
                    cashMachine.parseMachineFromTxt("a " + cursor.getString(alphabetColIndex));
                    break;
                }
            } while (cursor.moveToNext());
            if (descriptionText!=null) {
                descriptionText.setText(cashMachine.getDescription() +
                        context.getResources().getString(R.string.width_of_line) + " " +
                        cashMachine.getLineWidth());
            }
        }

        cursor.close();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    static void loadSpinnerData(Context context, Spinner spinner) {
        // database handler
        MachineDbHelper db = new MachineDbHelper(context);

        // Spinner Drop down elements
        List<String> lables = db.getAllNames();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }
}
