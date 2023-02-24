package platform.app.struct;

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
}
