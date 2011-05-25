/* Copyright (c) 2010, Johannes Köster <johannes.koester@tu-dortmund.de>
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see "license.txt"
 * for a description.
 */

package modalLogic.formula.io;
import modalLogic.formula.Formula;
import org.apache.commons.collections15.Transformer;

/**
 * Provides an interface to tranform a Formula into a String.
 *
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public interface FormulaFormatter<P> extends Transformer<Formula<P>, String> {

}
