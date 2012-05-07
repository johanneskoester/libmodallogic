/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modalLogic.formula;

import java.util.Comparator;

/**
 *
 * @author johannes
 */
public class Constant extends Literal implements Cloneable {
  
  public Constant(boolean value) {
    super(null);
    type = Formula.CONSTANT;
    if(!value)
      negate();
  }

  @Override
  public String toString() {
    if(negation)
      return "F";
    return "T";
  }
  
  @Override
  public boolean isNegationOf(Formula f) {
    if(f.getType() == Formula.CONSTANT) {
      return ((negation && !f.isNegation()) || (!negation && f.isNegation()));
    }
    return false;
  }

  @Override
  public boolean isNegationOf(Formula f, Comparator propositionComparator) {
    return isNegationOf(f);
  }

  @Override
  public boolean isEquivalent(Formula f) {
    if(f instanceof Constant) {
      return negation == f.isNegation();
    }
    return false;
  }

  @Override
  public void setProposition(Object proposition) {
    new UnsupportedOperationException("Constants may not have a proposition.");
  }
}
