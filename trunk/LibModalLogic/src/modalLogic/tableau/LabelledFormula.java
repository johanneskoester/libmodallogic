/* Copyright (c) 2010, Johannes Köster <johannes.koester@tu-dortmund.de>
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see "license.txt"
 * for a description.
 */

package modalLogic.tableau;

import java.util.ArrayList;
import java.util.Collection;
import modalLogic.formula.Formula;

/**
 * Container for subformulas together with world labels and meta information.
 * 
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class LabelledFormula<P> {
  private Formula<P> formula;
  private World world;
  private boolean expanded;
  private FormulaState state;
  private Collection<LabelledFormula<P>> resultingFormulae = new ArrayList<LabelledFormula<P>>();
  private Collection<Formula<P>> eliminationExplanation = new ArrayList<Formula<P>>();
  private String toString;

  /**
   * Constructor of class LabelledFormula.
   *
   * @param world the world
   * @param f the subformula
   * @param tableau the tableau
   */
  public LabelledFormula(World world, Formula<P> f, Tableau<P> tableau) {
    this.formula = f;
    this.world = world;
    expanded = f.getType() == Formula.LITERAL;
    ((Collection)f.getPayload()).add(this);
    System.out.println(((Collection)f.getPayload()));
  }

  /**
   * Returns the elimination explanation (blocking disjunctions; see dynamic backtracking)
   * @return the elimination explanation
   */
  public Collection<Formula<P>> getEliminationExplanation() {
    return eliminationExplanation;
  }

  /**
   * Sets the elimination explanation (blocking disjunctions; see dynamic backtracking)
   * @param formulas the elimination explanation
   */
  public void setEliminationExplanation(Collection<Formula<P>> formulas) {
    eliminationExplanation = formulas;
  }

  /**
   * Returns the state of the subformula
   *
   * @return the state
   */
  public FormulaState getState() {
    return state;
  }

  /**
   * Sets the state of the subformula
   *
   * @param state the state
   */
  public void setState(FormulaState state) {
    this.state = state;
  }

  /**
   * Report a subformula that results from the expansion of this one.
   *
   * @param f the resulting subformula
   */
  public void addResultingFormula(LabelledFormula<P> f) {
    resultingFormulae.add(f);
  }

  /**
   * Returns all subformulas that resulted from this.
   *
   * @return resulting subformulas
   */
  public Collection<LabelledFormula<P>> getResultingFormulas() {
    return resultingFormulae;
  }

  /**
   * Returns true if this subformula is expanded.
   *
   * @return true if this subformula is expanded
   */
  public boolean isExpanded() {
    return expanded;
  }

  /**
   * Report wether this subformula is expanded.
   *
   * @param expanded expansion state
   */
  public void setExpanded(boolean expanded) {
    this.expanded = expanded;
  }

  /**
   * Report this subformula as expanded.
   */
  public void setExpanded() {
    expanded = true;
  }

  /**
   * Returns the actual subformula.
   *
   * @return the actual subformula
   */
  public Formula<P> getFormula() {
    return formula;
  }

  /**
   * Returns the world in which this subformula is satisfied.
   *
   * @return the world
   */
  public World<P> getWorld() {
    return world;
  }

  /**
   * Returns the string representation of this subformula together with its world.
   *
   * @return the string representation
   */
  @Override
  public String toString() {
    if(toString == null)
      toString = "s" + world + ": " + formula;
    return toString;
  }
  
  
}
