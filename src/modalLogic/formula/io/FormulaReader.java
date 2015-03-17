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
public interface FormulaReader<E> {
  /**
   * Read a formula.
   *
   * @return the read formula
   * @throws Exception any Exception that occurs while reading.
   */
  public Formula<E> read() throws Exception;
}
