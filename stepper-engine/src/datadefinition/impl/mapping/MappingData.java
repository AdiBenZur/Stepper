package datadefinition.impl.mapping;

public class MappingData<T1,T2> {

    private T1 car;
    private T2 cdr;

    public MappingData(T1 car, T2 cdr) {
        this.car = car;
        this.cdr = cdr;
    }

    public T1 getCar() { return car; }

    public T2 getCdr() { return cdr; }

    public void setCar(T1 car) { this.car = car; }

    public void setCdr(T2 cdr) { this.cdr = cdr; }

    @Override
    public String toString() {
        return "(" + car + ", " + cdr + ")";
    }
}
