package data;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import data.CashMachineContract.CashMachineEntry;
import ru.vovnit.cashmachineprogramming.CashMachine;

/**
 * Created by vovnit on 16/08/17.
 */

public class MachineDbHelper extends SQLiteOpenHelper{

    public static final String LOG_TAG = MachineDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "hotel.db";

    private static final int DATABASE_VERSION = 1;

    public MachineDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_GUESTS_TABLE = "CREATE TABLE " + CashMachineEntry.TABLE_NAME + " ("
                + CashMachineEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CashMachineEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + CashMachineEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                + CashMachineEntry.COLUMN_WIDTH + " TEXT NOT NULL, "
                + CashMachineEntry.COLUMN_ALPHABET + " TEXT NOT NULL);";


        sqLiteDatabase.execSQL(SQL_CREATE_GUESTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF IT EXISTS " + CashMachineEntry.TABLE_NAME);
        // Создаём новую таблицу
        onCreate(sqLiteDatabase);
    }

    public List<String> getAllNames(){
        List<String> labels = new ArrayList<String>();
        // Select All Query

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(CashMachineEntry.TABLE_NAME, null, null, null, null, null, null);



        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(CashMachineEntry.COLUMN_NAME);
            do {
                labels.add(cursor.getString(nameColumnIndex));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }
}
