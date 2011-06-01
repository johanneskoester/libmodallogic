/* Copyright (c) 2010, Johannes Köster <johannes.koester@tu-dortmund.de>
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see "license.txt"
 * for a description.
 */

package modalLogic.tableau;

import modalLogic.formula.Formula;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;

/**
 * Manage blocked formulas before execution of tableau.
 *
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class PreBlocked<P> {
  private MultiMap<World, Formula<P>> map = new MultiHashMap<World, Formula<P>>();

  /**
   * Select a subformula to be blocked.
   *
   * @param w the world
   * @param f the subformula
   */
  public void addBlock(World w, Formula<P> f) {
    map.put(w, f);
  }

  /**
   * Remove a formula from being blocked.
   *
   * @param w the world
   * @param f the subformula
   */
  public void removeBlock(World w, Formula<P> f) {
    map.remove(w, f);
  }

  /**
   * Return true if a subformula was defined to be blocked.
   *
   * @param w the world
   * @param f the subformula
   * @return true if the subformula was defined to be blocked
   */
  public boolean isBlocked(World w, Formula<P> f) {
    if(map.containsKey(w)) {
      return map.get(w).contains(f);
    }
    return false;
  }
}
