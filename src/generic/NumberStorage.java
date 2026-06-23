package generic;

public class NumberStorage<T extends Number> {

    private T value;

    public NumberStorage(T value) {
        this.value = value;
    }

    public double square() {
        return value.doubleValue()*value.doubleValue();
    }
}