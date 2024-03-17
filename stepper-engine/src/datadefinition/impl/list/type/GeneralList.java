package datadefinition.impl.list.type;

import java.util.ArrayList;

public abstract class GeneralList<T> extends ArrayList<T> {
    protected  GeneralList() {super(); }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < this.size(); i++){
            builder.append(i + 1).append(": ").append(this.get(i).toString()).append(" ");
        }

        return builder.toString();
    }
}
