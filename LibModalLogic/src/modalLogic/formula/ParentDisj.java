/* Copyright (c) 2010, Johannes Köster <johannes.koester@tu-dortmund.de>
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see "license.txt"
 * for a description.
 */

package modalLogic.formula;

import java.util.HashSet;
import java.util.Set;

/**
 * Class ParentDisj provides methods to traverse a formulas parent disjunctions.
 * 
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class ParentDisj<P>{

  /**
   * Returns the next parent disjuction.
   *
   * @param f the subformula
   * @return the parent disjunction
   */
  public Formula<P> getNearest(Formula<P> f) {
    Formula<P> parent = f.getParent();
    if(parent != null) {
      if(parent.getType() == Formula.DISJUNCTION) {
        return parent;
      }
      return getNearest(parent);
    }
    return null;
  }

  /**
   * Returns the set of all parent disjunctions.
   * 
   * @param f the subformula
   * @return the parent disjunctions
   */
  public Set<Formula<P>> get(Formula<P> f) {
    Set<Formula<P>> disjunctions = new HashSet<Formula<P>>();
    collectDisjunctions(f, disjunctions);
    return disjunctions;
  }

  /**
   * Helper method to collect parent disjunctions.
   * 
   * @param f the subformula
   * @param disjunctions the set to store parent disjunctions in.
   */
  private void collectDisjunctions(Formula<P> f, Set<Formula<P>> disjunctions) {
    Formula<P> parent = f.getParent();
    if(parent != null) {
      if(parent.getType() == Formula.DISJUNCTION)
        disjunctions.add(parent);

      collectDisjunctions(parent, disjunctions);
    }
  }
}
