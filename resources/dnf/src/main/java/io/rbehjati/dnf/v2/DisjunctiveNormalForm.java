package io.rbehjati.dnf.v2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DisjunctiveNormalForm {

    private List<Set<Literal>> clauses;

    private DisjunctiveNormalForm(List<Set<Literal>> clauses) {
        this.clauses = clauses;
    }

    public static DisjunctiveNormalForm and(Literal... literals) {
        List<Set<Literal>> conjunctions = new ArrayList<>();
        if (literals == null) {
            return new DisjunctiveNormalForm(conjunctions);
        }
        Set<Literal> conjunction = new HashSet<>(Arrays.asList(literals));
        Set<Literal> negates = conjunction.stream().map(Literal::negate).collect(Collectors.toSet());
        if (haveNonEmptyIntersection(conjunction, negates)) {
            return new DisjunctiveNormalForm(Collections.emptyList());
        }
        conjunctions.add(conjunction);
        return new DisjunctiveNormalForm(conjunctions);
    }

    public static DisjunctiveNormalForm and(DisjunctiveNormalForm dnf1, DisjunctiveNormalForm dnf2) {
        List<Set<Literal>> clauses = new ArrayList<>();

        dnf1.clauses.forEach(conj1 -> dnf2.clauses.forEach(conj2 -> {
            Literal[] literals = Stream.of(conj1, conj2).flatMap(Collection::stream).toArray(Literal[]::new);
            DisjunctiveNormalForm dnf = and(literals);
            if (!dnf.clauses.isEmpty()) {
                Set<Literal> newConjunction = dnf.clauses.get(0);
                if (!clauses.contains(newConjunction)) {
                    clauses.add(newConjunction);
                }
            }
        }));
        minimize(clauses);
        return new DisjunctiveNormalForm(clauses);
    }

    public static DisjunctiveNormalForm or(Literal... literals) {
        List<Set<Literal>> clauses = new ArrayList<>();
        if (literals == null) {
            return new DisjunctiveNormalForm(clauses);
        }
        Set<Literal> input = new HashSet<>(Arrays.asList(literals));
        Set<Literal> negates = input.stream().map(Literal::negate).collect(Collectors.toSet());
        input.removeAll(negates);
        input.forEach(literal -> {
            Set<Literal> clause = new HashSet<>();
            clause.add(literal);
            clauses.add(clause);
        });
        return new DisjunctiveNormalForm(clauses);
    }

    public static DisjunctiveNormalForm or(DisjunctiveNormalForm dnf1, DisjunctiveNormalForm dnf2) {
        List<Set<Literal>> clauses = new ArrayList<>();
        clauses.addAll(dnf1.clauses);
        clauses.addAll(dnf2.clauses);
        minimize(clauses);
        return new DisjunctiveNormalForm(clauses);
    }

    private static void minimize(List<Set<Literal>> clauses) {
        List<Set<Literal>> toRemove = new ArrayList<>();
        for (Set<Literal> clause: clauses) {
            Set<Literal> copy = new HashSet<>(clause);
            if(hasASubsetIn(copy, clauses)){
                toRemove.add(clause);
            }
        }
        clauses.removeAll(toRemove);
    }

    static boolean hasASubsetIn(Set<Literal> set, List<Set<Literal>> clauses) {
        return clauses.stream().filter(s -> !s.equals(set)).anyMatch(set::containsAll);
    }

    public DisjunctiveNormalForm negate() {
        return clauses.stream()
            .map(this::negateClause)
            .reduce(DisjunctiveNormalForm::and)
            .orElse(null);
    }

    private DisjunctiveNormalForm negateClause(Set<Literal> clause) {
        List<Set<Literal>> newClauses = new ArrayList<>();
        clause.stream().sorted().forEach(literal -> {
            Set<Literal> newClause = new HashSet<>();
            newClause.add(literal.negate());
            newClauses.add(newClause);
        });
        return new DisjunctiveNormalForm(newClauses);
    }

    private static boolean haveNonEmptyIntersection(Set<Literal> conjunction, Set<Literal> negates) {
        negates.retainAll(conjunction);
        return !negates.isEmpty();
    }

    @Override
    public String toString() {
        return clauses.stream()
            .map(conj -> conj.stream().sorted().map(Literal::toString).collect(Collectors.joining(".")))
            .collect(Collectors.joining(" + "));
    }
}
