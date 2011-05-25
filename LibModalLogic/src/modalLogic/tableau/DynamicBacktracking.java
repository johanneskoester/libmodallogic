/* Copyright (c) 2010, Johannes Köster <johannes.koester@tu-dortmund.de>
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see "license.txt"
 * for a description.
 */

package modalLogic.tableau;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import modalLogic.formula.Formula;
import util.DescendingIterator;
import util.Pair;

/**
 * Performs dynamic backtracking when clash occurs on current branch.
 * 
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class DynamicBacktracking<P> {
  private Tableau<P> tableau;

  /**
   * Constructor of class DynamicBacktracking.
   *
   * @param tableau the tableau
   */
  public DynamicBacktracking(Tableau<P> tableau) {
    this.tableau = tableau;
  }

  /**
   * Find the disjunction were backtracking takes place.
   * 
   * @param clashes clashing pairs of subformulas
   * @return the selected disjunction to backtrack
   */
  public LabelledFormula<P> findBacktrackingPoint(Iterator<Pair<LabelledFormula<P>>> clashes) {
    Set<Formula<P>> tempDisj = new HashSet<Formula<P>>();
    while(clashes.hasNext()) {
      for(LabelledFormula<P> f : clashes.next()) {
        tempDisj.addAll(tableau.getParentDisj(f.getFormula()));
      }
    }

    Iterator<LabelledFormula<P>> disj = new DescendingIterator<LabelledFormula<P>>(tableau.getDisjunctions());
    while(disj.hasNext()) {
      LabelledFormula<P> lf = disj.next();
      Formula<P> f = lf.getFormula();
      if(tempDisj.contains(f)) {
        if(tableau.hasUnknownDisjunct(lf)) { // suspicious
          LabelledFormula<P> activeDisjunct = tableau.getActiveDisjunct(lf);
          tempDisj.remove(f);
          activeDisjunct.setEliminationExplanation(tempDisj);
          return lf;
        }

        for(Formula<P> f1 : f) {
          tempDisj.addAll(tableau.getLabelledFormula(lf.getWorld(), f1).getEliminationExplanation());
        }

        tempDisj.addAll(tableau.getParentDisj(f));
        
        World w = lf.getWorld();
        LabelledFormula<P> reason = w.getReason();
        if(reason != null)
          tempDisj.addAll(tableau.getParentDisj(reason.getFormula()));
        
        tempDisj.remove(lf.getFormula());
      }
    }
    return null;
  }

  /**
   * Perform actual backtracking. The next possible disjunct will be selected
   * upon the applied heuristic.
   *
   * @param ldisjunction the labelled disjunction
   */
  public void dynamicBacktrack(LabelledFormula<P> ldisjunction) {
    World w = ldisjunction.getWorld();
    LabelledFormula<P> lclashed = tableau.getActiveDisjunct(ldisjunction);

    lclashed.setState(FormulaState.BLOCKED);

    for(LabelledFormula<P> lf1 : tableau.getExplanationContains(ldisjunction.getFormula())) {
      if(tableau.isLazy()) {
        LabelledFormula<P> par = tableau.getLabelledFormula(lf1.getWorld(), lf1.getFormula().getParent());
        undoFormula(lf1);
        lf1.setState(FormulaState.UNKNOWN);
        nextDisjunct(par);
      }
      else {
        lf1.setState(FormulaState.UNKNOWN);
        lf1.getEliminationExplanation().clear();
      }
    }

    undoFormula(lclashed);
    nextDisjunct(ldisjunction);
  }

  /**
   * Helper method to branch to the next not blocked disjunct.
   *
   * @param ldisjunction the disjunction
   */
  private void nextDisjunct(LabelledFormula<P> ldisjunction) {
    // use selection heuristic to choose disjunct
    Formula<P> disjunct = tableau.getDisjunctSelector().selectNotBlockedDisjunct(ldisjunction, tableau.getHeuristics().disjunctSelector(ldisjunction));

    LabelledFormula<P> ldisjunct = tableau.label(ldisjunction.getWorld(), disjunct);
    ldisjunct.setState(FormulaState.ACTIVE);
    tableau.addToBranch(ldisjunct, ldisjunction);
  }

  /**
   * Helper method to remove a subformula and its resulting expansions from branch.
   *
   * @param lf the subformula
   */
  public void undoFormula(LabelledFormula<P> lf) {
    Collection<LabelledFormula<P>> fs =
            new HashSet<LabelledFormula<P>>(tableau.getResultingFormulas(lf));
    tableau.getBranch().remove(lf);

    tableau.removeFromBranch(fs);

    lf.setExpanded(false);

    tableau.removeFromExpandedDisjunctions(fs);

    tableau.removeWorldsCausedBy(lf);
    tableau.removeRelationsCausedBy(lf);

    for(LabelledFormula<P> lf2 : fs) {
      ((ArrayList<LabelledFormula<P>>)lf2.getFormula().getPayload()).remove(lf2);
    }
  }
}
