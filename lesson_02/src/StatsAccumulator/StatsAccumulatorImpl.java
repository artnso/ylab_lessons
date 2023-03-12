package StatsAccumulator;

public class StatsAccumulatorImpl  implements StatsAccumulator{
    private int min;
    private int max;
    private int count;
    private int sum;

    public StatsAccumulatorImpl() {
        min = 0;
        max = 0;
        count = 0;
        sum = 0;
    }

    public StatsAccumulatorImpl(int value) {
        count = 1;
        min = value;
        max = value;
        sum = value;
    }

    @Override
    public void add(int value) {
        count++;
        sum += value;
        if (count == 1) {
            min = value;
            max = value;
            return;
        }
        if (min > value) {
            min = value;
        }

        if (max < value) {
            max = value;
        }
    }

    @Override
    public int getMin() {
        return min;
    }

    @Override
    public int getMax() {
        return max;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Double getAvg() {
        if (count != 0) {
            return (double)sum / count;
        }
        return null;
    }
}
