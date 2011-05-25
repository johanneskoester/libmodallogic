/* Copyright (c) 2010, Johannes Köster <johannes.koester@tu-dortmund.de>
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see "license.txt"
 * for a description.
 */

package modalLogic.tableau;

/**
 * Provides interface to exchangeable expansion rules.
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public interface Rules<P> {
  public void conjunction(LabelledFormula<P> f, Tableau<P> tableau);
  public void disjunction(LabelledFormula<P> f, Tableau<P> tableau);
  public void diamond(LabelledFormula<P> f, Tableau<P> tableau);
  public void box(LabelledFormula<P> f, Tableau<P> tableau);
}
