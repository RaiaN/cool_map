package coolmap.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.text.InputType;
import android.widget.EditText;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap;
    private HashMap<LatLng, Note> notes = new HashMap<LatLng, Note>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_activity_layout);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                setOnMapLongClickListener();
                // setOnMarkerClickListener();
            }
        }
    }

    private void setOnMapLongClickListener() {
        // mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker")); bye-bye
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);

                builder.setTitle("Новая заметка");
                builder.setCancelable(true);

                final EditText noteEditText = new EditText(MapsActivity.this);
                noteEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                noteEditText.setTextSize(12);
                builder.setView(noteEditText);

                builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mMap.addMarker(new MarkerOptions().position(latLng));

                        notes.put(latLng, new Note(noteEditText.getText().toString()));
                    }
                });

                builder.show();
            }
        });
    }
}
