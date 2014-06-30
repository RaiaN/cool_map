package coolmap.app;

import com.google.android.gms.maps.model.Marker;

public class Note {
    private String note;
    private Marker marker;

    Note(String note, Marker marker) {
        super();
        this.note = note;
        this.marker = marker;
    }

    public String getNote() {
        return note;
    }

    public Marker getMarker() {
        return marker;
    }
}
