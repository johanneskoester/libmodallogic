/* Copyright (c) 2010, Johannes Köster <johannes.koester@tu-dortmund.de>
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see "license.txt"
 * for a description.
 */

package modalLogic.tableau;

import modalLogic.formula.Formula;

/**
 * Expansion rules for modal logic K (contains propositional logic).
 *
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class KRules<P> implements Rules<P> {

  /**
   * Expands a conjunction.
   *
   * @param f the conjunction to expand
   * @param tableau the tableau
   */
  @Override
  public void conjunction(LabelledFormula<P> f, Tableau<P> tableau) {
    for(Formula<P> f1 : f.getFormula()) {
      LabelledFormula<P> lf1 = tableau.label(f.getWorld(), f1);
      tableau.addToBranch(lf1, f);
      lf1.setState(FormulaState.ACTIVE);
    }
  }

  /**
   * Disjunction expansion. Uses disjunct selection heuristic.
   *
   * @param f the disjunction to expand
   * @param tableau the tableau
   */
  @Override
  public void disjunction(LabelledFormula<P> f, Tableau<P> tableau) {

    Formula<P> f1 = tableau.getDisjunctSelector().selectNotBlockedDisjunct(f,
            tableau.getHeuristics().disjunctSelector(f));

    LabelledFormula<P> lf1 = tableau.label(f.getWorld(), f1);
    tableau.addToBranch(lf1, f);
    tableau.setExpandedDisjunction(f);
    lf1.setState(FormulaState.ACTIVE);
  }

  /**
   * Expands a possibility subformula (see modal logic)
   *
   * @param f the subformula
   * @param tableau the tableau
   */
  @Override
  public void diamond(LabelledFormula<P> f, Tableau<P> tableau) {
    Formula<P> f1 = f.getFormula().getChild();
    
    World<P> w0 = f.getWorld();
    World<P> w1 = tableau.newWorld();
    w1.setReason(f);
    tableau.relateWorlds(w0, w1, f);

    LabelledFormula<P> lf1 = tableau.label(w1, f1);
    tableau.addToBranch(lf1, f);
    lf1.setState(FormulaState.ACTIVE);

    for(Formula<P> f2 : w0.getBoxFormulae()) {
      LabelledFormula<P> lf2 = tableau.label(w1, f2);
      // third argument adds it as a result of the box formula, second as a result of the diamond formula
      tableau.addToBranch(lf2, f, tableau.getLabelledFormula(w0, f2.getParent()));
      lf2.setState(FormulaState.ACTIVE);
    }
  }

  /**
   * Expands a necessity subformual (see modal logic).
   *
   * @param f the subformula
   * @param tableau the tableau
   */
  @Override
  public void box(LabelledFormula<P> f, Tableau<P> tableau) {
    Formula<P> f1 = f.getFormula().getChild();

    World w0 = f.getWorld();
    for(World w1 : tableau.getSucc(w0)) {
      LabelledFormula<P> lf1 = tableau.label(w1, f1);
      tableau.addToBranch(lf1, f);
      lf1.setState(FormulaState.ACTIVE);
    }
    w0.getBoxFormulae().add(f1);
  }
}
