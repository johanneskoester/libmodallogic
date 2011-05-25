/* Copyright (c) 2010, Johannes Köster <johannes.koester@tu-dortmund.de>
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see "license.txt"
 * for a description.
 */

package util;

import java.util.Iterator;
import java.util.List;

/**
 * A descending iterator.
 * 
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class DescendingIterator<T> implements Iterator<T> {
  
  private List<T> list;
  private int current;

  /**
   * Constructor of class DescendingIterator.
   *
   * @param list the list
   */
  public DescendingIterator(List<T> list) {
    this.list = list;
    current = list.size()-1;
  }

  @Override
  public boolean hasNext() {
    return current >= 0;
  }

  @Override
  public T next() {
    return list.get(current--);
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
