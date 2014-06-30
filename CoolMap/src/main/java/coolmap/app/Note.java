package coolmap.app;

public class Note {
    private String note;

    Note(String note) {
        super();
        this.note = note;
    }

    public void modifyNote(String modifiedNote) {
        this.note = modifiedNote;
    }

    public String getNote() {
        return note;
    }
}
