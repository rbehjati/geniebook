package io.rbehjati.dnf.v2;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Literal literal = (Literal) o;
        return Objects.equals(value, literal.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
