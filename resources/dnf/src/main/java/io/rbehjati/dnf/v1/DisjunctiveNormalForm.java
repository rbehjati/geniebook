package io.rbehjati.dnf.v1;

import java.util.*;
import java.util.stream.Collectors;

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
        conjunctions.add(conjunction);
        return new DisjunctiveNormalForm(conjunctions);
    }

    public static DisjunctiveNormalForm and(DisjunctiveNormalForm dnf1, DisjunctiveNormalForm dnf2) {
        List<Set<Literal>> clauses = new ArrayList<>();

        dnf1.clauses.forEach(conj1 -> dnf2.clauses.forEach(conj2 -> {
            Set<Literal> newConjunction = new HashSet<>(conj1);
            newConjunction.addAll(conj2);
            if(!clauses.contains(newConjunction)) {
                clauses.add(newConjunction);
            }
        }));
        return new DisjunctiveNormalForm(clauses);
    }

    public static DisjunctiveNormalForm or(Literal... literals) {
        List<Set<Literal>> clauses = new ArrayList<>();
        if (literals == null) {
            return new DisjunctiveNormalForm(clauses);
        }
        Arrays.asList(literals).forEach(literal -> {
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
        return new DisjunctiveNormalForm(clauses);
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

    @Override
    public String toString() {
        return clauses.stream()
            .map(conj -> conj.stream().sorted().map(Literal::toString).collect(Collectors.joining(".")))
            .collect(Collectors.joining(" + "));
    }
}
