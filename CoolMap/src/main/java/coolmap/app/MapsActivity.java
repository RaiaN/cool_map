package coolmap.app;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class MapsActivity extends FragmentActivity {
    private GoogleMap mMap;
    private HashMap<LatLng, Note> notes = new HashMap<LatLng, Note>();
    private final DatabaseHandler handler = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_activity_layout);
        setUpMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMap();
    }

    private void setUpMap() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                restoreNotes();
                setOnMapLongClickListener();
                setOnMarkerClickListener();
            }
        }
    }

    private void restoreNotes() {
        SQLiteDatabase db = handler.getReadableDatabase();
        if( db == null ) {
            return;
        }
        handler.onCreate(db);

        String[] projection = {
                DatabaseHandler.ID_COLUMN,
                DatabaseHandler.NOTE_TEXT,
                DatabaseHandler.MARKER_POSITION
        };

        Cursor cursor = db.query(DatabaseHandler.TABLE_NAME, projection, null, null, null, null, null);
        if( cursor == null ) {
            return;
        }

        while( cursor.moveToNext() ) {
            String noteText = cursor.getString(cursor.getColumnIndex(DatabaseHandler.NOTE_TEXT));
            String coordinates = cursor.getString(cursor.getColumnIndex(DatabaseHandler.MARKER_POSITION));

            //parse coordinates
            String []cds = coordinates.split(";");
            Double latitude = Double.parseDouble(cds[0]);
            Double longitude = Double.parseDouble(cds[1]);

            //here we have latitude and longitude
            LatLng notePosition = new LatLng(latitude, longitude);
            Marker noteMarker = mMap.addMarker(new MarkerOptions().position(notePosition));

            Note note = new Note(noteText, noteMarker);
            notes.put(notePosition, note);
        }
        cursor.close();
        db.close();
    }

    private void storeNotes() {
        SQLiteDatabase db = handler.getWritableDatabase();
        if( db == null ) {
            return;
        }
        handler.dropTable(db);

        int noteId = 0;
        for( Note n: notes.values() ) {
            ContentValues values = new ContentValues();

            values.put(DatabaseHandler.ID_COLUMN, noteId);
            values.put(DatabaseHandler.NOTE_TEXT, n.getNote());

            Double latitude = n.getMarker().getPosition().latitude;
            Double longitude = n.getMarker().getPosition().longitude;
            String coordinates = latitude.toString() + ";" + longitude.toString();
            values.put(DatabaseHandler.MARKER_POSITION, coordinates);

            db.insert(DatabaseHandler.TABLE_NAME, null, values);

            ++noteId;
        }
        db.close();
    }

    private void setOnMapLongClickListener() {
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);

                builder.setTitle("Новая заметка");
                builder.setCancelable(true);

                final EditText noteEditText = new EditText(MapsActivity.this);
                noteEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                noteEditText.setTextSize(14);
                builder.setView(noteEditText);

                builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String noteText = noteEditText.getText().toString();
                        /*if( note.isEmpty() ) {
                            return; // sometimes empty note may be useful
                        }*/

                        Marker noteMarker = mMap.addMarker(new MarkerOptions().position(latLng));
                        Note note = new Note(noteText, noteMarker);
                        notes.put(noteMarker.getPosition(), note);

                        storeNotes();
                    }
                });
                builder.setNegativeButton("Отмена", null);

                builder.show();
            }
        });
    }

    private void setOnMarkerClickListener() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);

                builder.setTitle("Просмотр заметки");
                builder.setCancelable(true);

                final TextView input = new TextView(MapsActivity.this);
                input.setText(notes.get(marker.getPosition()).getNote());
                input.setTextSize(14);
                builder.setView(input);

                builder.setPositiveButton("Ок", null);
                builder.show();

                return false;
            }
        });

    }
}
