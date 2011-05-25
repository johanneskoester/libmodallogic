/* Copyright (c) 2010, Johannes Köster <johannes.koester@tu-dortmund.de>
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see "license.txt"
 * for a description.
 */
package modalLogic.tableau;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import modalLogic.formula.Formula;

/**
 * Implementation of unit propagation to predict the best expansion due to 
 * current branch.
 *
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class UnitPropagation<P> {

  private Comparator<P> propositionComparator;

  /**
   * Constructor of class UnitPropagation.
   *
   * @param propositionComparator a comparator for propositions
   */
  public UnitPropagation(Comparator<P> propositionComparator) {
    this.propositionComparator = propositionComparator;
  }

  /**
   * Selects a disjunct of a given disjunction.
   *
   * @param disjunction the disjunction
   * @return the selected disjunct
   */
  public Iterator<Formula<P>> selectDisjunct(LabelledFormula<P> disjunction) {
    Collection<Formula<P>> disjuncts = new ArrayList<Formula<P>>();
    for(Formula<P> d : disjunction.getFormula()) {
      disjuncts.add(d);
    }

    removeClashing(true, disjunction.getWorld().getPositiveLiterals(), disjuncts);
    removeClashing(false, disjunction.getWorld().getNegativeLiterals(), disjuncts);

    return disjuncts.iterator();
  }

  /**
   * Helper method to remove potentially clashing disjuncts when looking at
   * current branch.
   *
   * @param positive whether literals are not negated
   * @param literals literals of the current world
   * @param disjuncts disjunct to select from
   */
  private void removeClashing(boolean positive, Iterable<LabelledFormula<P>> literals, Iterable<Formula<P>> disjuncts) {
    for(LabelledFormula<P> l : literals) {
      Iterator<Formula<P>> ite = disjuncts.iterator();
      while(ite.hasNext()) {
        Formula<P> d = ite.next();
        if(d.getType() == Formula.LITERAL && propositionComparator.compare(l.getFormula().getProposition(), d.getProposition()) == 0)
          if(d.isNegation() == positive) {
            ite.remove();
          }
      }
    }
  }
}