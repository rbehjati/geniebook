package io.rbehjati.dnf.v1;

import io.rbehjati.genie.CombinationGenerator;
import io.rbehjati.genie.model.Combination;
import io.rbehjati.genie.model.Factor;
import org.assertj.core.api.Assertions;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.rbehjati.dnf.v1.DisjunctiveNormalForm.*;
import static org.assertj.core.api.Assertions.assertThat;


public class DisjunctiveNormalFormTest {

    @Test
    public void simpleOr() {
        DisjunctiveNormalForm or = or(new Literal("a"), new Literal("b"));
        assertThat(or.toString()).isEqualTo("a + b");
    }


    @Test
    public void simpleAnd() {
        DisjunctiveNormalForm and = and(new Literal("a"), new Literal("b"));
        assertThat(and.toString()).isEqualTo("a.b");
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
            and(new Literal("a").negate(), new Literal("c"), new Literal("d")));
        assertThat(dnf.toString()).isEqualTo("a.b + !a.c.d");
    }

    @Test
    public void abOrnotCd_negated() {
        DisjunctiveNormalForm dnf = or(
            and(new Literal("a"), new Literal("b")),
            and(new Literal("c").negate(), new Literal("d"))).negate();
        assertThat(dnf.toString()).isEqualTo("!a.c + !a.!d + !b.c + !b.!d");
    }

    @Test
    @Ignore
    public void abOracd_negated() {
        DisjunctiveNormalForm dnf = or(
            and(new Literal("a"), new Literal("b")),
            and(new Literal("a"), new Literal("c"), new Literal("d"))).negate();
        // The resulting formula is "!a.!a + !a.!c + !a.!d + !a.!b + !b.!c + !b.!d", which can be simplified.
        assertThat(dnf.toString()).isEqualTo("!a + !b.!c + !b.!d");
    }

    static Factor lhs = new Factor("lhs", "a.b", "a + c");
    static Factor rhs = new Factor("rhs", "a.b", "a + c");
    static Factor lhsNeg = new Factor("lhsNeg", "yes", "no");


    @Test
    public void comboTest() {
        CombinationGenerator generator = new CombinationGenerator();
        List<Combination> combinations = generator.generateCombinations(2, Arrays.asList(lhs, rhs, lhsNeg));

        combinations.forEach(c -> System.out.println(comboToString(c) + ": " + expectedResult(c)));

        combinations.forEach(c -> {
            DisjunctiveNormalForm lhsVal = parse(c.getEquivalenceClass(lhs));
            DisjunctiveNormalForm rhsVal = parse(c.getEquivalenceClass(rhs));
            String lhsIsNeg = c.getEquivalenceClass(lhsNeg);
            if("yes".equals(lhsIsNeg)){
                lhsVal = lhsVal.negate();
            }

            DisjunctiveNormalForm implication = or(lhsVal.negate(), rhsVal);

            Assertions.assertThat(implication.toString()).isEqualTo(expectedResult(c));
        });
    }

    private String comboToString(Combination combo){
        return Stream.of(lhs, rhs, lhsNeg).map(combo::getEquivalenceClass).collect(Collectors.joining(", "));
    }

    private String expectedResult(Combination combination) {
        String lhsVal = combination.getEquivalenceClass(lhs);
        String rhsVal = combination.getEquivalenceClass(rhs);
        String lhsIsNeg = combination.getEquivalenceClass(lhsNeg);

        if (lhsVal.equals(rhsVal)) {
            if ("no".equals(lhsIsNeg)) {
                return ""; // a tautology
            } else {
                return rhsVal;
            }
        }

        if ("yes".equals(lhsIsNeg)) {
            return "a + c";
        }

        if("a.b".equals(lhsVal)){
            return "!b + c";
        }
        return "!a.!c + a.b";
    }

    private DisjunctiveNormalForm parse(String dnf) {
        if ("a.b".equals(dnf)) {
            return DisjunctiveNormalForm.and(new Literal("a"), new Literal("b"));
        } else if ("a + c".equals(dnf)) {
            return DisjunctiveNormalForm.or(new Literal("a"), new Literal("c"));
        } else {
            return DisjunctiveNormalForm.or(new Literal(dnf));
        }
    }

}