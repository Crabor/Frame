package common.struct;

public class SetState {
    public boolean state;

    public SetState() {
    }

    public SetState(boolean state) {
        this.state = state;
    }

    public boolean get() {
        return state;
    }

    public void set(boolean state) {
        this.state = state;
    }

    public static SetState fromString(String str) {
        return new SetState(Boolean.parseBoolean(str));
    }

    @Override
    public String toString() {
        return state ? "true" : "false";
    }
}
