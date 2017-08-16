package data;

import android.provider.BaseColumns;

/**
 * Created by vovnit on 16/08/17.
 */

public final class CashMachineContract {
    private CashMachineContract() {}

    public static final class CashMachineEntry implements BaseColumns {
        public final static String TABLE_NAME = "machines";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_DESCRIPTION = "description";
        public final static String COLUMN_WIDTH = "width";
        public final static String COLUMN_ALPHABET = "alphabet";
    }
}
