package RateLimitedPrinter;

public class RateLimitedPrinter {
    private final int interval;
    private long lastPrint;

    public RateLimitedPrinter(int interval) {
        this.interval = interval;
        lastPrint = 0;
    }

    public void print(String message) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPrint > interval){
            System.out.println(message);
            lastPrint = System.currentTimeMillis();
        }
    }
}
