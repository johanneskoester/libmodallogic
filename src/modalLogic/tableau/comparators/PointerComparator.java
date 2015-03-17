/* Copyright (c) 2010, Johannes Köster <johannes.koester@tu-dortmund.de>
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see "license.txt"
 * for a description.
 */

package modalLogic.tableau.comparators;

import java.util.Comparator;

/**
 * Comparator between propositons that uses the pointers of the objects.
 *
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class PointerComparator<P> implements Comparator<P> {

  @Override
  public int compare(P o1, P o2) {
    return (o1 == o2) ? 0 : -1;
  }

}
