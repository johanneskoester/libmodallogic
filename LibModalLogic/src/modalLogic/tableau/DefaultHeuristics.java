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
 *
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class DefaultHeuristics<P> implements Heuristics<P> {
  protected Tableau<P> tableau;

  /**
   * Constructor of class DefaultHeuristics.
   *
   * @param tableau the tableau
   */
  public DefaultHeuristics(Tableau<P> tableau) {
    this.tableau = tableau;
  }

  /**
   * Selects permutation of disjuncts and returns an iterator.
   * @todo implement more intelligent heuristic
   *
   * @param disjunction the disjunction
   * @return Iterator of disjuncts
   */
  @Override
  public Iterator<Formula<P>> disjunctSelector(LabelledFormula<P> disjunction) {
    return disjunction.getFormula().iterator();
  }
}
