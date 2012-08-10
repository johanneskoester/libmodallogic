/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modalLogic.formula;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

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
  protected void buildString(StringBuilder s) {
    if(negation)
      s.append('⊥');
    else
      s.append('⊤');
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
    throw new UnsupportedOperationException("Constants may not have a proposition.");
  }
  
  @Override
  public Literal clone() {
    return new Constant(!negation);
  }
  
  
  /**
   * Returns null, the common pseudo proposition of all constants.
   * 
   * @return null
   */
  @Override
  public Object getProposition() {
    return null;
  }

  /**
   * Returns an empty set.
   * @return empty set
   */
  @Override
  public Set getPropositions() {
    return new HashSet();
  }

  @Override
  public boolean contains(Object proposition) {
    return false;
  }
  
}
