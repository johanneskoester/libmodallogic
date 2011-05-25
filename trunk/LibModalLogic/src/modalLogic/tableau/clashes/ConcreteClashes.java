/* Copyright (c) 2010, Johannes Köster <johannes.koester@tu-dortmund.de>
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see "license.txt"
 * for a description.
 */

package modalLogic.tableau.clashes;

import java.util.ArrayList;
import modalLogic.tableau.LabelledFormula;
import util.Pair;

/**
 * Container for pairs of clashing subformulas.
 *
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class ConcreteClashes<P> extends ArrayList<Pair<LabelledFormula<P>>> {

  /**
   * Add two clashing subformulas.
   *
   * @param f1 a subformula
   * @param f2 a subformula
   */
  public void add(LabelledFormula<P> f1, LabelledFormula<P> f2) {
    add(new Pair<LabelledFormula<P>>(f1, f2));
  }
}
