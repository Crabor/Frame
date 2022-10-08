package platform.service.ctx.ctxChecker.middleware;

public class NotSupportedException extends Exception{
    public NotSupportedException() {
        super("It is not supported!");
    }

    public NotSupportedException(String message) {
        super(message);
    }
}
