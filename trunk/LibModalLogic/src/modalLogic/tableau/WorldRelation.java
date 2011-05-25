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
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;
import util.Pair;

/**
 * Models a reachability relation between two worlds (see Kripke Models)
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class WorldRelation<P> extends HashSet<Pair<World<P>>> {
  private MultiMap<LabelledFormula<P>, Pair<World<P>>> resulting =
          new MultiHashMap<LabelledFormula<P>, Pair<World<P>>>();

  /**
   * Returns the map between labelled formulas and resulting relations.
   * @return
   */
  public MultiMap<LabelledFormula<P>, Pair<World<P>>> getResulting() {
    return resulting;
  }

  /**
   * Clear the relation.
   */
  @Override
  public void clear() {
    super.clear();
    resulting.clear();
  }

  /**
   * Add a relation between two worlds.
   *
   * @param w0 a world
   * @param w1 a world
   * @param reason the reason
   */
  public void add(World w0, World w1, LabelledFormula<P> reason) {
    Pair<World<P>> rel = new Pair<World<P>>(w0, w1);
    add(rel);
    /*Set<Pair<World<P>>> rs = resulting.get(reason);
    if(rs == null) {
      rs = new HashSet<Pair<World<P>>>();
      resulting.put(reason, rs);
    }
    rs.add(rel);*/

    resulting.put(reason, rel);
  }

  /**
   * Returns the reachable worlds for a given one.
   *
   * @param world the world
   * @return the reachable worlds
   */
  public Collection<World<P>> succ(World<P> world) {
    Collection<World<P>> succ = new ArrayList<World<P>>();

    for(Pair<World<P>> w : this) {
      if(w.getFirst().equals(world)) {
        succ.add(w.getSecond());
      }
    }
    return succ;
  }

  /**
   * Remove all relations caused by a given labelled formula.
   *
   * @param f the labelled formula
   */
  public void removeCausedBy(LabelledFormula<P> f) {
    Collection<Pair<World<P>>> res = resulting.get(f);
    if(res != null)
      removeAll(res);
    resulting.remove(f);
  }
}
