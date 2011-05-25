/* Copyright (c) 2010, Johannes Köster <johannes.koester@tu-dortmund.de>
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see "license.txt"
 * for a description.
 */

package modalLogic.tableau.comparators;

import java.util.Comparator;

/**
 * Comparator between propositions that uses the equals method.
 *
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class EqualsComparator<P> implements Comparator<P> {
  
  @Override
  public int compare(P o1, P o2) {
    return (o1.equals(o2)) ? 0 : -1;
  }

}
