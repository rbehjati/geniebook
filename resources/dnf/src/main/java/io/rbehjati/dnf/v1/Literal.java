package io.rbehjati.dnf.v1;

import java.util.Objects;

public class Literal implements Comparable<Literal>{

    public final String value;

    public  Literal(String value){
        this.value = value;
    }

    public Literal negate() {
        if(value.startsWith("!")){
            return new Literal(value.substring(1));
        }
        return new Literal("!" + value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int compareTo(Literal that) {
        String thisVar = value.startsWith("!") ? value.substring(1) : value;
        String thatVar = that.value.startsWith("!") ? that.value.substring(1) : that.value;

        return thisVar.equals(thatVar)
            ? this.value.compareTo(that.value)
            : thisVar.compareTo(thatVar);
    }
}
