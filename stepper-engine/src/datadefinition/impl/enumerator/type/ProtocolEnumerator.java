package datadefinition.impl.enumerator.type;

public enum ProtocolEnumerator {
    HTTP, HTTPS;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
