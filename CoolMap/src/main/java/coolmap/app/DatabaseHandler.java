package coolmap.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "stored_notes_db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "notes";
    public static final String ID_COLUMN = "note_id";
    public static final String NOTE_TEXT = "note";
    public static final String MARKER_POSITION = "position";

    private final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            ID_COLUMN + " INTEGER PRIMARY KEY," +
            NOTE_TEXT + " TEXT," +
            MARKER_POSITION + " TEXT)";

    DatabaseHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {

    }

    public void dropTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME );
        db.execSQL(SQL_CREATE_TABLE);
    }
}
