/* Copyright (c) 2010, Johannes Köster <johannes.koester@tu-dortmund.de>
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see "license.txt"
 * for a description.
 */

package modalLogic.tableau;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import modalLogic.formula.Formula;

/**
 * Manager of all labelled formulas.
 *
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class LabelledFormulas<P> implements Iterable<LabelledFormula<P>> {

  private ArrayList<LabelledFormula<P>> formulae =
          new ArrayList<LabelledFormula<P>>();
  private Tableau<P> tableau;

  /**
   * Constructor of class LabelledFormulas
   * @param tableau the tableau
   */
  public LabelledFormulas(Tableau<P> tableau) {
    this.tableau = tableau;
  }

  /**
   * Clear the formulas.
   */
  public void clear() {
    formulae.clear();
  }

  /**
   * Create new labelled formula.
   *
   * @param w the world
   * @param f the subformula
   * @return the labelled formula
   */
  public LabelledFormula<P> newLabelledFormula(World w, Formula<P> f) {
    LabelledFormula<P> lf = new LabelledFormula<P>(w, f, tableau);

    formulae.add(lf);
    
    return lf;
  }

  /**
   * Return all labelled formulas the elimination explanation of which contains
   * a given formula f.
   *
   * @param f the formula
   * @return the labelled formulas
   */
  public Collection<LabelledFormula<P>> getEliminationExplanationContains(Formula<P> f) {
    Collection<LabelledFormula<P>> lfs = new ArrayList<LabelledFormula<P>>();
    for(LabelledFormula<P> lf : this) {
      if(lf.getEliminationExplanation().contains(f))
        lfs.add(lf);
    }

    return lfs;
  }

  /**
   * Iterator over all labelled formulas.
   *
   * @return the iterator
   */
  @Override
  public Iterator<LabelledFormula<P>> iterator() {
    return formulae.iterator();
  }

  /**
   * Returns the number of labelled formulas.
   *
   * @return the number of labelled formulas
   */
  public int size() {
    return formulae.size();
  }
}
