/* Copyright (c) 2010, Johannes Köster <johannes.koester@tu-dortmund.de>
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see "license.txt"
 * for a description.
 */

package modalLogic.formula.factory;

import modalLogic.formula.Constant;
import modalLogic.formula.Formula;
import modalLogic.formula.FormulaImpl;
import modalLogic.formula.Literal;
import org.apache.commons.collections15.Factory;

/**
 * Class FormulaFactory allows to build up a formula in a state-machine like way
 * similar to OpenGL.
 *
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class FormulaFactory<P> implements Factory<Formula<P>> {

  private Formula<P> formula;
  private Formula<P> current;
  private boolean negateNext = false;

  /**
   * Reset the current formula tree to be empty.
   */
  public void reset() {
    formula = null;
    current = null;
    negateNext = false;
  }

  /**
   * Returns true if the next inserted subformula will be the root of the built
   * formula tree.
   *
   * @return true if the next inserted subformula will be the root of the built
   * formula tree
   */
  public boolean isRoot() {
    return current == null;
  }

  /**
   * Returns the current subformula
   *
   * @return the last inserted subformula
   */
  public Formula<P> getCurrent() {
    return current;
  }

  /**
   * Set the type of the current subformula.
   *
   * @param type the type
   */
  public void setCurrentType(short type) {
    current.setType(type);
  }
  
  public short getCurrentType() {
    return current.getType();
  }

  /**
   * Opens formula with undefined type. Attention, type must be set
   * afterwards or formula will be invalid.
   */
  public void openFormula() {
    openFormula((short)-1);
  }

  /**
   * Opens a conjunction.
   */
  public void openConjunction() {
    openFormula(Formula.CONJUNCTION);
  }

  /**
   * Opens a disjunction.
   */
  public void openDisjunction() {
    openFormula(Formula.DISJUNCTION);
  }

  /**
   * Opens an implication.
   */
  public void openImplication() {
    openFormula(Formula.IMPLICATION);
  }

  /**
   * Opens a necessity (see Modal Logic).
   */
  public void necessity() {
    openFormula(Formula.NECESSITY);
  }

  /**
   * Opens a possibility (see Modal Logic).
   */
  public void possibility() {
    openFormula(Formula.POSSIBILITY);
  }

  /**
   * Inserts a literal.
   *
   * @param proposition the proposition
   */
  public void literal(P proposition) {
    Literal<P> l = new Literal<P>(proposition);
    handleNegation(l);
    addToCurrent(l);

    if(!isSingleton() && currentIsUnary())
      close();
  }
  
  /**
   * Inserts a constant (T or F).
   *
   * @param proposition the value
   */
  public void constant(boolean value) {
    Constant c = new Constant(value);
    handleNegation(c);
    addToCurrent(c);
    
    if(!isSingleton() && currentIsUnary())
      close();
  }

  /**
   * Inserts a whole subformula.
   *
   * @param formula the subformula
   */
  public void subformula(Formula<P> formula) {
    addToCurrent(formula);

    if(!isSingleton() && currentIsUnary())
      close();
  }

  /**
   * Negates the next inserted subformula.
   */
  public void negation() {
    negateNext = true;
  }

  /**
   * Closes the current subformula.
   */
  public void close() {
    if(current == null) {
      throw new UnsupportedOperationException("No formula was opened before.");
    }
    current = current.getParent();
    while(current != null && currentIsUnary()) {
      current = current.getParent();
    }
  }

  /**
   * Returns the formula tree.
   * @return the formula tree
   */
  @Override
  public Formula<P> create() {
    return formula;
  }
  
  public boolean isUndefined() {
    return current.getType() == -1;
  }

  /**
   * Returns true if current subformula is unary.
   *
   * @return true if current subformula is unary
   */
  private boolean currentIsUnary() {
    return current.getType() == Formula.NECESSITY || current.getType() == Formula.POSSIBILITY;
  }
  
  private boolean isSingleton() {
    return formula instanceof Literal || formula instanceof Constant;
  }

  /**
   * Helper method to open a subformula of a given type.
   *
   * @param type the type
   */
  private void openFormula(short type) {
    Formula<P> f = new FormulaImpl<P>(type);
    handleNegation(f);
    
    addToCurrent(f);
    current = f;
  }
  
  private void addToCurrent(Formula<P> f) {
    if(current != null) {
      current.addChild(f);
    }
    else {
      formula = f;
    }
  }

  /**
   * Helper method to handle wether a subformula has to be negated.
   *
   * @param f a subformula
   */
  private void handleNegation(Formula<P> f) {
    if(negateNext) {
      f.negate();
      negateNext = false;
    }
  }
}
