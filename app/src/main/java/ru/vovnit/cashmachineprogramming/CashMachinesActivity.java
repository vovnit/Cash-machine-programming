package ru.vovnit.cashmachineprogramming;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import data.CashMachineContract;
import data.MachineDbHelper;

public class CashMachinesActivity extends AppCompatActivity {
    ListView machinesList;
    List<String> names;
    MachineDbHelper dbHelper;
    ArrayAdapter<String> adapter;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_machines);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle(getResources().getString(R.string.item_delete));
        setSupportActionBar(myToolbar);

        machinesList = (ListView) findViewById(R.id.machinesListView);

        MachineDbHelper db = new MachineDbHelper(this);
        names = db.getAllNames();

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, names);
        dbHelper = new MachineDbHelper(this);
        machinesList.setAdapter(adapter);
        machinesList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        context = this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_cash_machines, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final SparseBooleanArray checked = machinesList.getCheckedItemPositions();
        int size = checked.size();
        final ArrayList<Integer> trues = new ArrayList<>();
        for (int i = 0; i < names.size(); ++i) {
            if (checked.get(i)) {
                int key = checked.keyAt(i);
                trues.add(i);
            }
        }
        switch (item.getItemId()) {
            case R.id.action_delete:
                if (trues.isEmpty()) {
                    Snackbar.make(findViewById(R.id.machinesListView),
                            getString(R.string.no_selection),
                            Snackbar.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(getResources().getString(R.string.sure_question));
                    builder.setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ArrayList<String> forDeleting = new ArrayList<String>();
                            for (int tru : trues) {
                                forDeleting.add(names.get(tru));
                            }
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            for (String el : forDeleting) {
                                String[] elem = {el};
                                db.delete(CashMachineContract.CashMachineEntry.TABLE_NAME,
                                        CashMachineContract.CashMachineEntry.COLUMN_NAME + "=?",
                                        elem);
                            }

                            names.clear();
                            names.addAll(dbHelper.getAllNames());
                            ((BaseAdapter) machinesList.getAdapter()).notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.setCancelable(true);
                    builder.show();
                }
        }
        return super.onOptionsItemSelected(item);
    }

}
