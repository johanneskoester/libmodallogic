/* Copyright (c) 2010, Johannes Köster <johannes.koester@tu-dortmund.de>
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see "license.txt"
 * for a description.
 */

package modalLogic.formula;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Class Literal provides the leafs of the formula tree. Either a single proposition
 * or a negated single proposition.
 * 
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class Literal<T> extends FormulaImpl<T> implements Cloneable {
  
  private T proposition;

  /**
   * Constructor of class Literal.
   * 
   * @param proposition the proposition of the literal
   */
  public Literal(T proposition) {
    super(Formula.LITERAL);
    this.proposition = proposition;
  }

  @Override
  public T getProposition() {
    return proposition;
  }

  /**
   * Set the proposition.
   *
   * @param proposition a proposition
   */
  public void setProposition(T proposition) {
    this.proposition = proposition;
  }

  @Override
  public Set<T> getPropositions() {
    Set<T> propositions = new HashSet<T>();
    propositions.add(proposition);
    return propositions;
  }

  @Override
  public boolean contains(T proposition) {
    return this.proposition.equals(proposition);
  }

  @Override
  public boolean isNegationOf(Formula<T> f) {
    if(f.getType() == Formula.LITERAL) {
      return ((negation && !f.isNegation()) || (!negation && f.isNegation())) &&
              proposition.equals(f.getProposition());
    }
    return false;
  }

  @Override
  public boolean isNegationOf(Formula<T> f, Comparator<T> propositionComparator) {
    if(f.getType() == Formula.LITERAL) {
      return ((negation && !f.isNegation()) || (!negation && f.isNegation())) &&
              propositionComparator.compare(proposition, f.getProposition()) == 0;
    }
    return false;
  }

  @Override
  public boolean isEquivalent(Formula<T> f) {
    if(f instanceof Literal) {
      return negation == f.isNegation() && proposition.equals(f.getProposition());
    }
    return false;
  }

  @Override
  protected void buildString(StringBuilder s) {
    if(isNegation())
      s.append('¬');
    s.append(proposition.toString());
  }

  @Override
  public void toNegationNormalForm() {
    // literal is always in NNF
  }

  /**
   * A literal cannot change its type.
   *
   * @throws UnsupportedOperationException
   */
  @Override
  public void setType(short type) {
    throw new UnsupportedOperationException("Literals have fixed type: PROPOSITION");
  }

  /**
   * A literal cannot contain a child formula.
   *
   * @throws UnsupportedOperationException
   */
  @Override
  public void addChild(Formula<T> child) {
    throw new UnsupportedOperationException("Literals may not have child formulas");
  }

  /**
   * A Literal is not iterable.
   *
   * @throws UnsupportedOperationException
   */
  @Override
  public Iterator<Formula<T>> iterator() {
    return new Iterator<Formula<T>>() {

      @Override
      public boolean hasNext() {
        return false;
      }

      @Override
      public Formula<T> next() {
        throw new UnsupportedOperationException("Not supported.");
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException("Not supported.");
      }
    };
  }

  /**
   * Returns 0 as a literal has no further children in the formula tree.
   * 
   * @return 0
   */
  @Override
  public int getChildCount() {
    return 0;
  }

  @Override
  public Literal<T> clone() {
    Literal<T> clone = new Literal<T>(proposition);

    clone.negation = this.negation;

    return clone;
  }

  @Override
  public Literal<T> cloneWithReference(Map<Formula<T>, Formula<T>> reference) {
    Literal<T> clone = clone();

    reference.put(this, clone);

    return clone;
  }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Literal<T> other = (Literal<T>) obj;
        if(this.negation != other.negation)
            return false;
        if (this.proposition != other.proposition && (this.proposition == null || !this.proposition.equals(other.proposition))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.proposition != null ? this.proposition.hashCode() : 0);
        if(this.negation)
            hash = -hash;
        return hash;
    }

}
