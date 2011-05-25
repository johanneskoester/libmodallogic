/* Copyright (c) 2010, Johannes Köster <johannes.koester@tu-dortmund.de>
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see "license.txt"
 * for a description.
 */

package modalLogic.tableau;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import modalLogic.formula.Formula;
import modalLogic.tableau.clashes.ConcreteClashes;
import util.Pair;

/**
 * A world in the sense of modal logic (i.e. a state in a Kripke Model).
 *
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class World<P>{
  private int id;
  private Set<LabelledFormula<P>> positive = new HashSet<LabelledFormula<P>>();
  private Set<LabelledFormula<P>> negative = new HashSet<LabelledFormula<P>>();
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
   * Returns the subformula the expansion of which caused this world to be created.
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
    return positive;
  }

  /**
   * Returns the negated literals of this world.
   *
   * @return the negated literals of this world
   */
  public Collection<LabelledFormula<P>> getNegativeLiterals() {
    return negative;
  }

  /**
   * Add a literal.
   *
   * @param literal the literal
   */
  public void addLiteral(LabelledFormula<P> literal) {
    if(literal.getFormula().isNegation()) {
      negative.add(literal);
      addClashes(literal, positive); // search in positive literals for clash
    }
    else {
      positive.add(literal);
      addClashes(literal, negative);
    }
  }

  /**
   * Remove a given literal.
   *
   * @param literal the literal
   */
  public void removeLiteral(LabelledFormula<P> literal) {
    if(!positive.remove(literal))
      negative.remove(literal);
    removeClashes(literal);
  }

  /**
   * Returns the clashes.
   *
   * @return the clashes
   */
  public Iterator<Pair<LabelledFormula<P>>> clashes() {
    if(clashes.isEmpty())
      return null;
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
  private void addClashes(LabelledFormula<P> literal, Collection<LabelledFormula<P>> literals) {
    for(LabelledFormula<P> lf2 : literals) {
      if(propositionComparator.compare(literal.getFormula().getProposition(), lf2.getFormula().getProposition()) == 0) {
        clashes.add(literal,lf2);
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
    while(clash.hasNext()) {
      if(clash.next().contains(literal))
        clash.remove();
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
