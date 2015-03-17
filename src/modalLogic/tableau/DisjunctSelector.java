/* Copyright (c) 2010, Johannes Köster <johannes.koester@tu-dortmund.de>
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see "license.txt"
 * for a description.
 */

package modalLogic.tableau;

import java.util.Iterator;
import modalLogic.formula.Formula;

/**
 * Provides methods to select disjuncts.
 *
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class DisjunctSelector<P> {
  private Tableau<P> tableau;

  public DisjunctSelector(Tableau<P> tableau) {
    this.tableau = tableau;
  }

  /**
   * Selects the first disjunct due to heuristics.
   *
   * @param f a disjunction
   * @return the disjunct
   */
  public Formula<P> selectDisjunct(LabelledFormula<P> f) {
    return tableau.getHeuristics().disjunctSelector(f).next(); 
  }

  /**
   * Selects the first not blocked disjunct.
   *
   * @param f a disjunction
   * @param disjuncts an iterator over the heuristically ordered disjuncts
   * @return the not blocked disjunct
   */
  public Formula<P> selectNotBlockedDisjunct(LabelledFormula<P> f, Iterator<Formula<P>> disjuncts) {
    while(disjuncts.hasNext()) {
      Formula<P> f1 = disjuncts.next();
      if(!tableau.isBlocked(f.getWorld(), f1)) {
        return f1;
      }
    }
    return null;
  }
}
