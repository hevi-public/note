package hu.hevi.note.shell;

// https://stackoverflow.com/questions/17006239/whats-the-best-way-to-implement-next-and-previous-on-an-enum-type
public enum State {

    ADD, TAG_CURRENT_NOTE, FIND, COMMAND;

    private static State[] vals = values();

    public State next() {
        return vals[(this.ordinal()+1) % vals.length];
    }
}
