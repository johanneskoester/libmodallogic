/* Copyright (c) 2010, Johannes Köster <johannes.koester@tu-dortmund.de>
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see "license.txt"
 * for a description.
 */
package modalLogic.formula;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;

/**
 * Abstract class Formula, represents a (modal) logic formula (if root node) or
 * subformula (if inner node) as a tree.
 * 
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public abstract class Formula<T> implements Iterable<Formula<T>>, Cloneable {

  public static final short CONJUNCTION = 0;
  public static final short DISJUNCTION = 1;
  public static final short NECESSITY = 2;
  public static final short POSSIBILITY = 3;
  public static final short LITERAL = 4;
  public static final short IMPLICATION = 5;
  public static final short UNDEFINED = 6;
  public static final short CONSTANT = 7;

  /**
   * Sets the type of the Formula.
   *
   * @param type the type
   */
  public abstract void setType(short type);

  /**
   * Returns the type of the Formula.
   *
   * @return the type
   */
  public abstract short getType();

  /**
   * Returns true if Formula is negated.
   *
   * @return true if negated
   */
  public abstract boolean isNegation();

  /**
   * Negates the Formula.
   */
  public abstract void negate();

  /**
   * Returns true if formula is a negation of a given formula.
   *
   * @param formula a formula
   * @return true if current formula is negation of given formula.
   */
  public abstract boolean isNegationOf(Formula<T> formula);

  /**
   * Returns true if formula is a negation of a given formula.
   *
   * @param formula a formula
   * @param propositionComparator a Comparator for propositions,
   * overriding the default comparator.
   * @return true if current formula is negation of given formula.
   */
  public abstract boolean isNegationOf(Formula<T> formula, Comparator<T> propositionComparator);

  /**
   * Converts formula into negation normal form.
   */
  public abstract void toNegationNormalForm();

  /**
   * Add a child formula to current formula.
   *
   * @param formula the new child formula
   */
  public abstract void addChild(Formula<T> formula);

  /**
   * Returns the ith child.
   * 
   * @param index the index of the child
   * @return return the child
   */
  public abstract Formula<T> getChild(int index);

  /**
   * Returns the first child.
   *
   * @return the child
   */
  public abstract Formula<T> getChild();

  /**
   * Returns the number of child formulas.
   *
   * @return the number of child formulas
   */
  public abstract int getChildCount();

  /**
   * Returns the parent formula of the current one.
   * 
   * @return the parent formula of the current one
   */
  public abstract Formula<T> getParent();

  /**
   * Sets the parent formula for the current one.
   *
   * @param formula the new parent formula
   */
  public abstract void setParent(Formula<T> formula);

  /**
   * Returns the proposition of the current formula.
   *
   * @return the proposition
   */
  public abstract T getProposition();

  /**
   * Returns the set of all propositions.
   *
   * @return the propositions
   */
  public abstract Set<T> getPropositions();

  /**
   * Returns true if formula contains a given proposition.
   *
   * @param proposition a proposition
   * @return true if formula contains the given proposition
   */
  public abstract boolean contains(T proposition);

  /**
   * Returns the index of a given child formula.
   *
   * @param child the child formula
   * @return the index of the given child formula
   */
  public abstract int indexOf(Formula<T> child);

  /**
   * Removes a given child formula.
   *
   * @param child a child formula
   */
  public abstract void removeChild(Formula<T> child);

  /**
   * Sets a payload for the given formula. May be used to host meta information.
   *
   * @param payload the payload
   */
  public abstract void setPayload(Object payload);

  /**
   * Returns the payload.
   * @return the payload
   */
  public abstract Object getPayload();

  /**
   * Returns a string representation of the formula.
   * @return a string representation of the formula
   */
  @Override
  public abstract String toString();

  /**
   * Clones the whole formula tree.
   *
   * @return a clone of the whole formula tree
   */
  @Override
  public abstract Formula<T> clone();

  /**
   * Clones the whole formula tree while maintaining a map between original and 
   * cloned subformulas.
   *
   * @return a map between original and cloned subformulas of the whole
   * formula tree
   */
  public abstract Map<Formula<T>, Formula<T>> cloneWithReference();

  /**
   * Clones the whole formula tree while maintaining a map between original and
   * cloned subformulas.
   *
   * @param reference the map between the original and cloned subformulas
   * @return a clone of the whole formula tree
   */
  public abstract Formula<T> cloneWithReference(Map<Formula<T>, Formula<T>> reference);
}
