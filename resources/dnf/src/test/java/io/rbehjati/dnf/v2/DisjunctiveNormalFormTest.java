package io.rbehjati.dnf.v2;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static io.rbehjati.dnf.v2.DisjunctiveNormalForm.and;
import static io.rbehjati.dnf.v2.DisjunctiveNormalForm.or;
import static org.assertj.core.api.Assertions.assertThat;

public class DisjunctiveNormalFormTest {

    @Test
    public void simpleOr() {
        DisjunctiveNormalForm or = or(new Literal("a"), new Literal("b"));
        assertThat(or.toString()).isEqualTo("a + b");
    }

    @Test
    public void simpleImplication() {
        // a => a
        DisjunctiveNormalForm tautology = or(new Literal("a").negate(), new Literal("a"));
        assertThat(tautology.toString()).isEqualTo("");

        // !a => a
        DisjunctiveNormalForm a = or(new Literal("a"), new Literal("a"));
        assertThat(a.toString()).isEqualTo("a");
    }

    @Test
    public void simpleAnd() {
        DisjunctiveNormalForm and = and(new Literal("a"), new Literal("b"));
        assertThat(and.toString()).isEqualTo("a.b");
    }

    @Test
    public void aAndNotA() {
        DisjunctiveNormalForm and = and(new Literal("a"), new Literal("!a"));
        assertThat(and.toString()).isEqualTo("");
    }

    @Test
    public void aOrNotA() {
        DisjunctiveNormalForm and = or(new Literal("a"), new Literal("!a"));
        assertThat(and.toString()).isEqualTo("");
    }

    @Test
    public void aOrNotAOrb() {
        DisjunctiveNormalForm and = or(new Literal("a"), new Literal("!a"), new Literal("b"));
        assertThat(and.toString()).isEqualTo("b");
    }

    @Test
    public void simpleNegate() {
        DisjunctiveNormalForm and = and(new Literal("a"), new Literal("b"));
        assertThat(and.negate().toString()).isEqualTo("!a + !b");
    }

    @Test
    public void abOracd() {
        DisjunctiveNormalForm dnf = or(
            and(new Literal("a"), new Literal("b")),
            and(new Literal("a"), new Literal("c"), new Literal("d")));
        assertThat(dnf.toString()).isEqualTo("a.b + a.c.d");
    }

    @Test
    public void hasAsubsetIn() {
        Set<Literal> set1 = new HashSet<>(Collections.singletonList(new Literal("a")));
        Set<Literal> set2 = new HashSet<>(Arrays.asList(new Literal("a"), new Literal("b")));
        assertThat(DisjunctiveNormalForm.hasASubsetIn(set2, Collections.singletonList(set1))).isTrue();
    }

    @Test
    public void abOrnotCd_negated() {
        DisjunctiveNormalForm dnf = or(
            and(new Literal("a"), new Literal("b")),
            and(new Literal("c").negate(), new Literal("d"))).negate();
        assertThat(dnf.toString()).isEqualTo("!a.c + !a.!d + !b.c + !b.!d");
    }

    @Test
    public void abOracd_negated() {
        DisjunctiveNormalForm dnf = or(
            and(new Literal("a"), new Literal("b")),
            and(new Literal("a"), new Literal("c"), new Literal("d"))).negate();
        assertThat(dnf.toString()).isEqualTo("!a + !b.!c + !b.!d");
    }
}