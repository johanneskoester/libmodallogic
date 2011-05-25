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
 * Provides interface of replacable heuristics used by tableau algorithm.
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public interface Heuristics<P> {

  /**
   * Selects permutation of disjuncts and returns an iterator.
   *
   * @param disjunction the disjunction
   * @return Iterator of disjuncts
   */
  public Iterator<Formula<P>> disjunctSelector(LabelledFormula<P> disjunction);
}
