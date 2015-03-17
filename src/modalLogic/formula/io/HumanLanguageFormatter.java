/* Copyright (c) 2010, Johannes Köster <johannes.koester@tu-dortmund.de>
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see "license.txt"
 * for a description.
 */

package modalLogic.formula.io;

import java.util.Iterator;
import modalLogic.formula.Formula;
import modalLogic.formula.Literal;

/**
 *
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class HumanLanguageFormatter<P> implements FormulaFormatter<P> {
  boolean literal = false;

  /**
   * Transform a formula into a string.
   *
   * @param f the formula
   * @return the string
   */
  @Override
  public String transform(Formula<P> f) {
    String s = "";
    switch (f.getType()) {
      case Formula.LITERAL:
        s = ((Literal)f).getProposition().toString();
        literal = true;
        break;
      case Formula.CONJUNCTION:
        s = naryOperatorTransform(" and ", f);
        break;
      case Formula.DISJUNCTION:
        s = naryOperatorTransform(" or ", f);
        break;
      case Formula.IMPLICATION:
        s = naryOperatorTransform(" if ", f);
        break;
      case Formula.NECESSITY:
        s = " it is neccessary, that " + transform(f.getChild());
        break;
      case Formula.POSSIBILITY:
        s = " it is possible, that " + transform(f.getChild());
        break;
      case Formula.CONSTANT:
        s = f.toString();
        literal = true;
        break;
    }
    if(!literal)
      s = "(" + s + ")";

    if (f.isNegation()) {
      s = "not " + s;
    }
    
    return s;
  }

  /**
   * Helper method to transform an nary operator into a string.
   *
   * @param operator the operator
   * @param f for subformula
   * @return the String
   */
  private String naryOperatorTransform(String operator, Formula<P> f) {
    String s = "";
    Iterator<Formula<P>> child = f.iterator();

    while (child.hasNext()) {
      s += transform(child.next());
      if (child.hasNext()) {
        s += operator;
      }
    }
    return s;
  }
}
