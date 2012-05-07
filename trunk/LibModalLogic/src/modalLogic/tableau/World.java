/* Copyright (c) 2010, Johannes Köster <johannes.koester@tu-dortmund.de>
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see "license.txt"
 * for a description.
 */
package modalLogic.tableau;

import java.util.*;
import modalLogic.formula.Constant;
import modalLogic.formula.Formula;
import modalLogic.tableau.clashes.ConcreteClashes;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;
import util.Pair;

/**
 * A world in the sense of modal logic (i.e. a state in a Kripke Model).
 *
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class World<P> {

  private int id;
  private MultiMap<P, LabelledFormula<P>> positive = new MultiHashMap<P, LabelledFormula<P>>();
  private MultiMap<P, LabelledFormula<P>> negative = new MultiHashMap<P, LabelledFormula<P>>();
  private MultiMap<Constant, LabelledFormula<P>> posconstants = new MultiHashMap<Constant, LabelledFormula<P>>();
  private MultiMap<Constant, LabelledFormula<P>> negconstants = new MultiHashMap<Constant, LabelledFormula<P>>();
  private ConcreteClashes<P> clashes = new ConcreteClashes<P>();
  private Comparator<P> propositionComparator;
  private LabelledFormula<P> reason;
  private Collection<Formula<P>> boxFormulae = new ArrayList<Formula<P>>();

  /**
   * Constructor of class world.
   *
   * @param id the id
   * @param comparator a proposition comparator
   */
  public World(int id, Comparator<P> comparator) {
    this.id = id;
    this.propositionComparator = comparator;
  }

  /**
   * Returns the id.
   *
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * Returns the necessity subformulas.
   *
   * @return the necessity subformulas
   */
  public Collection<Formula<P>> getBoxFormulae() {
    return boxFormulae;
  }

  /**
   * Returns the subformula the expansion of which caused this world to be
   * created.
   *
   * @return the causing subformula
   */
  public LabelledFormula<P> getReason() {
    return reason;
  }

  /**
   * Sets the reason for this world.
   *
   * @param reason the causing subformula
   */
  public void setReason(LabelledFormula<P> reason) {
    this.reason = reason;
  }

  /**
   * Returns a string representation.
   *
   * @return the string representation
   */
  @Override
  public String toString() {
    return "" + id;
  }

  /**
   * Returns the positive literals of this world.
   *
   * @return the positive literals of this world
   */
  public Collection<LabelledFormula<P>> getPositiveLiterals() {
    Collection<LabelledFormula<P>> pos = positive.values();
    pos.addAll(posconstants.values());
    return pos;
  }

  /**
   * Returns the negated literals of this world.
   *
   * @return the negated literals of this world
   */
  public Collection<LabelledFormula<P>> getNegativeLiterals() {
    Collection<LabelledFormula<P>> neg = negative.values();
    neg.addAll(negconstants.values());
    return neg;
  }

  /**
   * Add a literal.
   *
   * @param literal the literal
   */
  public void addLiteral(LabelledFormula<P> literal) {
    if (literal.getFormula().getType() == Formula.CONSTANT) {
      if (literal.getFormula().isNegation()) {
        negconstants.put((Constant) literal.getFormula(), literal);
        addClashes(literal, positive, posconstants); // search in positive literals for clash
      } else {
        posconstants.put((Constant) literal.getFormula(), literal);
        addClashes(literal, negative, negconstants);
      }
    } else {
      if (literal.getFormula().isNegation()) {
        negative.put(literal.getFormula().getProposition(), literal);
        addClashes(literal, positive, posconstants); // search in positive literals for clash
      } else {
        positive.put(literal.getFormula().getProposition(), literal);
        addClashes(literal, negative, negconstants);
      }
    }
  }

  /**
   * Remove a given literal.
   *
   * @param literal the literal
   */
  public void removeLiteral(LabelledFormula<P> literal) {
    P p = literal.getFormula().getProposition();
    if (positive.remove(p) == null) {
      negative.remove(p);
    }
    removeClashes(literal);
  }

  /**
   * Returns the clashes.
   *
   * @return the clashes
   */
  public Iterator<Pair<LabelledFormula<P>>> clashes() {
    if (clashes.isEmpty()) {
      return null;
    }
    return clashes.iterator();
  }

  /**
   * Returns true if world contains a clash.
   *
   * @return true if world contains a clash
   */
  public boolean isClashing() {
    return !clashes.isEmpty();
  }

  /**
   * Update clashes for an incoming literal.
   *
   * @param literal the literal
   * @param literals the other literals
   */
  private void addClashes(LabelledFormula<P> literal, MultiMap<P, LabelledFormula<P>> literals, MultiMap<Constant, LabelledFormula<P>> constants) {
    Collection<LabelledFormula<P>> clashing = literals.get(literal.getFormula().getProposition());
    if(clashing == null) {
      clashing = new ArrayList<LabelledFormula<P>>();
    }
    clashing.addAll(constants.values());
    
    if (!clashing.isEmpty()) {
      for (LabelledFormula<P> c : clashing) {
        clashes.add(literal, c);
      }
    }
  }

  /**
   * Remove clashes of a given literal.
   *
   * @param literal the literal
   */
  private void removeClashes(LabelledFormula<P> literal) {
    Iterator<Pair<LabelledFormula<P>>> clash = clashes.iterator();
    while (clash.hasNext()) {
      if (clash.next().contains(literal)) {
        clash.remove();
      }
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final World<P> other = (World<P>) obj;
    if (this.id != other.id) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return id;
  }
}
