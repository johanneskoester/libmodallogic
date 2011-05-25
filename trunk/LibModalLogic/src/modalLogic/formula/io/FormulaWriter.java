/* Copyright (c) 2010, Johannes Köster <johannes.koester@tu-dortmund.de>
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see "license.txt"
 * for a description.
 */

package modalLogic.formula.io;

import modalLogic.formula.Formula;

/**
 *
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public interface FormulaWriter<E> {

  /**
   * Write a given formula.
   *
   * @param formula the formula to write
   * @throws Exception any exception that occurs during writing
   */
  public void write(Formula<E> formula) throws Exception;
}
