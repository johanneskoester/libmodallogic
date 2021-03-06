# LibModalLogic - A JAVA implementation of Modal Logic K, Propositional Logic and the tableau algorithm

LibModalLogic provides a JAVA implementation of Modal Logic K and Propositional Logic. Logic formulas can be build in memory, saved to and read from MathML and formatted human readable. Reasoning is implemented by the (modal) logic tableau algorithm, including dynamic backtracking for maximum performance.

## Examples

Construction of formulas works similar to the usage of the OpenGL state machine, and combined with a factory design pattern. E.g. a disjunction of String literals (e.g. A∨B∨¬C) is created in the following way:

```java
FormulaFactory<String> factory = new FormulaFactory<String>();

factory.openDisjunction();
  factory.literal("A");
  
  factory.literal("B");

  factory.negation();
  factory.literal("C");
factory.close();

Formula<String> formula = factory.create();
```

For reasoning, the tableau algorithm can be used to find a satisfying model (i.e. a world):

```java
Tableau<String> tableau = new Tableau<String>();
tableau.setFormula(formula);
boolean satisfiable = tableau.proofSearch();
Worlds<String> worlds = tableau.getWorlds();
```

In the case of a propositional logic formula, this will yield a single world that represents one satisfying model.

To explore alternative models, a mechanism to pre-block subformulas is provided:

```java
World<String> world = worlds.get(0);
Formula<String> toBlock = formula.getChild(0);
tableau = new Tableau<String>();
tableau.setFormula(formula);
tableau.block(world, toBlock);
tableau.proofSearch();
```

## Citation

This library was developed as part of the following diploma thesis

Johannes Köster, "Propagating Interaction Logic Toward Predicitve Protein Hypernetworks", Algorithm Engineering Report TR11-4-002 (ISSN 1864-4503), TU Dortmund
