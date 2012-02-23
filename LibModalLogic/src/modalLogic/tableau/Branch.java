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
import java.util.Iterator;
import java.util.List;
import modalLogic.formula.Formula;
import modalLogic.formula.Literal;
import org.apache.commons.collections15.iterators.IteratorChain;
import util.Pair;

/**
 * The currently investigated branch of the tableau.
 * 
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class Branch<P> {

  private Tableau<P> tableau;
  private Comparator<P> propositionComparator;
  private List<LabelledFormula<P>> unexpanded;

  /**
   * Constructor of class Branch.
   *
   * @param tableau the tableau
   * @param propositionComparator a comparator for propositions
   */
  public Branch(Tableau<P> tableau, Comparator<P> propositionComparator) {
    this.tableau = tableau;
    this.propositionComparator = propositionComparator;
    unexpanded = new ArrayList<LabelledFormula<P>>();
  }

  /**
   * Returns a list of unexpanded subformulas.
   *
   * @return a list of unexpanded subformulas
   */
  public List<LabelledFormula<P>> getUnexpanded() {
    return unexpanded;
  }

  /**
   * Clears the list of unexpanded subformulas.
   */
  public void clear() {
    unexpanded.clear();
  }

  /**
   * Add a subformula to the branch.
   *
   * @param f the subformula
   */
  public void add(LabelledFormula<P> f) {
    if (f.getFormula().getType() == Formula.LITERAL) {
      f.getWorld().addLiteral(f);
    }
    unexpanded.add(f);
  }

  /**
   * Returns an iterator over all clashes in the branch.
   *
   * @return the iterator
   */
  public Iterator<Pair<LabelledFormula<P>>> clashes() {
    IteratorChain<Pair<LabelledFormula<P>>> ites = new IteratorChain<Pair<LabelledFormula<P>>>();
    for(World<P> w : tableau.getWorlds()) {
      if(w.isClashing())
        ites.addIterator(w.clashes());
    }
    return ites;
  }

  /**
   * Add subformula to the unexpanded ones.
   *
   * @param lf the subformula
   */
  public void unexpand(LabelledFormula<P> lf) {
    unexpanded.add(lf);
  }

  /**
   * Return the last unexpanded formula.
   * 
   * @return the last unexpanded formula
   */
  public LabelledFormula<P> unexpanded() {
    if(unexpanded.isEmpty()) {
      return null;
    }
    return unexpanded.remove(unexpanded.size()-1);
  }

  /**
   * Remove subformula from current branch and world.
   *
   * @param lf the subformula
   */
  public void remove(LabelledFormula<P> lf) {
    unexpanded.remove(lf);
    if(lf.getFormula() instanceof Literal)
      lf.getWorld().removeLiteral(lf);
  }

  /**
   * Remove all subformulas of given collection.
   *
   * @param lfs the subformulas
   */
  public void removeAll(Collection<LabelledFormula<P>> lfs) {
    for (LabelledFormula<P> lf : lfs) {
      remove(lf);
    }
  }

  /**
   * Return number of unexpanded subformulas.
   *
   * @return the number of unexpanded subformulas
   */
  public int unexpandedSize() {
    return unexpanded.size();
  }
}
