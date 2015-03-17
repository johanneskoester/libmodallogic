/* Copyright (c) 2010, Johannes Köster <johannes.koester@tu-dortmund.de>
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see "license.txt"
 * for a description.
 */

package modalLogic.tableau;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Management of all created world in the tableau.
 *
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class Worlds<P> extends ArrayList<World<P>> {
  private int count = 0;
  private World<P> start;
  private Comparator<P> propositionComparator;
  private Tableau<P> tableau;

  /**
   * Constructor of class Worlds.
   *
   * @param tableau the tableau
   * @param propositionComparator a proposition comparator
   */
  public Worlds(Tableau<P> tableau, Comparator<P> propositionComparator) {
    this.propositionComparator = propositionComparator;
    this.tableau = tableau;
  }

  /**
   * Return the number of managed worlds.
   *
   * @return the number of managed worlds
   */
  public int getCount() {
    return count;
  }

  /**
   * Set the number of managed worlds.
   *
   * @param count the number of managed worlds
   */
  public void setCount(int count) {
    this.count = count;
  }

  /**
   * Clear the managed worlds.
   */
  @Override
  public void clear() {
    super.clear();
    count = 0;
  }

  /**
   * Get initial world.
   *
   * @return the initial world
   */
  public World<P> getStart() {
    if(size() > 0)
      return get(0);
    return null;
  }

  /**
   * Create a new world.
   *
   * @return the new world
   */
  public World newWorld() {
    World<P> w = new World<P>(count++, propositionComparator);
    add(w);
    return w;
  }

  /**
   * Remove all worlds caused by a given labelled formula.
   * 
   * @param lf the labelled formula
   */
  public void removeCausedBy(LabelledFormula<P> lf) {
    Iterator<World<P>> worlds = iterator();
    while(worlds.hasNext()) {
      World<P> w = worlds.next();
      if(w.getReason() == lf) {
        worlds.remove();
      }
    }
  }
}
