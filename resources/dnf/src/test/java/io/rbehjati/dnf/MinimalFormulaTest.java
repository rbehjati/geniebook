package io.rbehjati.dnf;

import io.rbehjati.dnf.v2.DisjunctiveNormalForm;
import io.rbehjati.dnf.v2.Literal;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MinimalFormulaTest {

    @Test
    public void negateOfABigFormula() {

        Map<String, Literal> literals = Stream
            .of("d1w", "d1m", "d2w", "d2m", "p1n", "p2n", "i1a", "i2a")
            .map(Literal::new)
            .collect(Collectors.toMap(l -> l.value, l -> l));

        // (d1w & d1m) | (d2w & d2m)
        DisjunctiveNormalForm biClause = DisjunctiveNormalForm.or(
            DisjunctiveNormalForm.and(literals.get("d1w"), literals.get("d1m")),
            DisjunctiveNormalForm.and(literals.get("d2w"), literals.get("d2m")));

        // (p1n | d1w | i1a) & (p2n | d2w | i2a)
        DisjunctiveNormalForm conjunction =
            DisjunctiveNormalForm.and(
                DisjunctiveNormalForm.or(literals.get("p1n"), literals.get("d1w"), literals.get("i1a")),
                DisjunctiveNormalForm.or(literals.get("p2n"), literals.get("d2w"), literals.get("i2a")));

        DisjunctiveNormalForm conjunctionNegated = DisjunctiveNormalForm.and(biClause, conjunction).negate();
        String formula = conjunctionNegated.toString();
        Assertions.assertThat(formula).isEqualTo("!d1m.!d2m + !d1m.!d2w + !d1w.!d2m + !d1w.!d2w + !d1w.!i1a.!p1n + !d2w.!i2a.!p2n");
    }
}
