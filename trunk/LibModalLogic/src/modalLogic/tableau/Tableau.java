/* Copyright (c) 2010, Johannes Köster <johannes.koester@tu-dortmund.de>
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see "license.txt"
 * for a description.
 */

package modalLogic.tableau;

import java.util.ArrayList;
import modalLogic.tableau.comparators.EqualsComparator;
import modalLogic.formula.ParentDisj;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import modalLogic.formula.Formula;
import util.Pair;

/**
 * Implementation of the tableau algorithm.
 *
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class Tableau<P> {
  private TableauState state = TableauState.NOTINITIALIZED;
  private Formula<P> initialFormula;
  private Branch<P> branch;
  private ExpandedDisjunctions<P> expandedDisjunctions = new ExpandedDisjunctions<P>();
  private Worlds<P> worlds;
  private WorldRelation<P> rel = new WorldRelation<P>();
  private ParentDisj<P> parentDisj = new ParentDisj<P>();
  private Rules rules = new KRules();
  private DynamicBacktracking<P> dynBacktracking = new DynamicBacktracking<P>(this);
  private LabelledFormulas<P> labelledFormulas = new LabelledFormulas<P>(this);
  private Heuristics<P> heuristics;
  private UnitPropagation<P> unitPropagation;
  private Comparator<P> propositionComparator;
  private DisjunctSelector<P> disjunctSelector = new DisjunctSelector<P>(this);
  private boolean lazy;
  private PreBlocked<P> preBlocked = new PreBlocked<P>();

  /**
   * Constructor of class Tableau.
   */
  public Tableau() {
    this(new KRules<P>(), new EqualsComparator<P>(), true);
  }

  /**
   * Constructor of class Tableau.
   *
   * @param rules the expansion rules
   * @param lazy whether the tableau should be lazy (faster dynamic backtracking)
   */
  public Tableau(Rules<P> rules, boolean lazy) {
    this(rules, new EqualsComparator<P>(), lazy);
  }

  /**
   * Constructor of class Tableau.
   *
   * @param rules the expansion rules
   * @param propositionComparator a proposition comparator
   * @param lazy whether the tableau should be lazy (faster dynamic backtracking)
   */
  public Tableau(Rules<P> rules, Comparator<P> propositionComparator, boolean lazy) {
    this.rules = rules;
    this.heuristics = new DefaultHeuristics<P>(this);
    worlds = new Worlds<P>(this, propositionComparator);

    branch = new Branch<P>(this, propositionComparator);
    this.lazy = lazy;
    this.propositionComparator = propositionComparator;
    unitPropagation = new UnitPropagation<P>(propositionComparator);
  }

  /**
   * Returns the unit propagation implementation.
   *
   * @return the unit propagation implementation
   */
  public UnitPropagation<P> getUnitPropagation() {
    return unitPropagation;
  }

  /**
   * Define a subformula to be blocked prior to execution of tableau.
   *
   * @param w the world
   * @param f the subformula
   */
  public void block(World w, Formula<P> f) {
    preBlocked.addBlock(w, f);
  }

  /**
   * Undo blocking of a formula prior to execution of tableau.
   *
   * @param w the world
   * @param f the subformula
   */
  public void unblock(World w, Formula<P> f) {
    preBlocked.removeBlock(w, f);
  }

  /**
   * Returns true if tableau should be lazy.
   *
   * @return true if tableau should be lazy
   */
  public boolean isLazy() {
    return lazy;
  }

  /**
   * Returns all used labelled formulas.
   *
   * @return all used labelled formulas
   */
  public LabelledFormulas<P> getLabelledFormulas() {
    return labelledFormulas;
  }

  /**
   * Empty the tableau.
   */
  public void clear() {
    worlds.clear();
    branch.clear();
    labelledFormulas.clear();
    rel.clear();
    expandedDisjunctions.clear();
  }

  /**
   * Set a formula to be prooven.
   *
   * @param formula the formula in negation normal form
   */
  public void setFormula(Formula<P> formula) {
    initialFormula = formula;
    prepareFormula(formula);
  }

  /**
   * Prepare a formula for tableau execution.
   *
   * @param formula the formula
   */
  private void prepareFormula(Formula<P> formula) {
    formula.setPayload(new ArrayList(3));
    for(Formula<P> child : formula) {
      prepareFormula(child);
    }
  }

  /**
   * Append a new formula to current branch. Like dynamically adding a conjunct
   * to running tableau.
   *
   * @param formula the formula to add
   * @throws UnsupportedOperationException if current formula is not a conjunction
   */
  public void appendFormula(Formula<P> formula) {
    if(initialFormula.getType() == Formula.CONJUNCTION) {
      prepareFormula(formula);
      initialFormula.addChild(formula);
      branch.add(label(worlds.getStart(), formula));
    }
    else
      throw new UnsupportedOperationException("appending only allowed to conjunctive formulae for now.");
  }

  /**
   * Returns the current formula.
   *
   * @return the current formula
   */
  public Formula<P> getFormula() {
    return initialFormula;
  }

  /**
   * Returns the state of the tableau.
   *
   * @return the state
   */
  public TableauState getState() {
    return state;
  }

  /**
   * Returns the used heuristics.
   *
   * @return the used heuristics
   */
  public Heuristics<P> getHeuristics() {
    return heuristics;
  }

  /**
   * Sets the used heuristics.
   *
   * @param heuristics the heuristics
   */
  public void setHeuristics(Heuristics<P> heuristics) {
    this.heuristics = heuristics;
  }

  /**
   * Returns the disjunct selector.
   *
   * @return the disjunct selector
   */
  public DisjunctSelector<P> getDisjunctSelector() {
    return disjunctSelector;
  }

  /**
   * Returns the current branch.
   *
   * @return the current branch
   */
  public Branch<P> getBranch() {
    return branch;
  }

  /**
   * Search for a proof of a formula in negation normal form.
   * Undefined results for other formulae.
   * 
   * @return true if proof succesful
   */
  public boolean proofSearch() {

    if(getState() == TableauState.NOTINITIALIZED)
      branch.add(label(worlds.newWorld(), initialFormula));

    int count = 0;
    Iterator<Pair<LabelledFormula<P>>> clashes = branch.clashes();
    LabelledFormula<P> unexpanded = branch.unexpanded();
    while(clashes.hasNext() || unexpanded != null) {
      if(!clashes.hasNext()) {

        switch(unexpanded.getFormula().getType()) {
          case Formula.CONJUNCTION:
            rules.conjunction(unexpanded, this);
            break;
          case Formula.DISJUNCTION:
            rules.disjunction(unexpanded, this);
            break;
          case Formula.POSSIBILITY:
            rules.diamond(unexpanded, this);
            break;
          case Formula.NECESSITY:
            rules.box(unexpanded, this);
            break;
          case Formula.LITERAL:
            break;
          default:
            throw new UnsupportedOperationException("The found formula type is not accepted. Formula must be in negation normal form.");
        }
        setExpanded(unexpanded);
      }
      else {
        LabelledFormula<P> f = dynBacktracking.findBacktrackingPoint(clashes);
        if(f == null) {
          state = TableauState.UNSATISFIABLE;
          return false;
        }
        else {
          dynBacktracking.dynamicBacktrack(f);
        }
      }
      clashes = branch.clashes();
      unexpanded = branch.unexpanded();
    }
    state = TableauState.SATISFIABLE;
    return true;
  }

  /**
   * Add a labelled subformula to the current branch.
   *
   * @param f the labelled subformula
   * @param reason the subformula that marks the reason for this
   */
  public void addToBranch(LabelledFormula<P> f, LabelledFormula<P>... reason) {
    branch.add(f);
    for(LabelledFormula<P> r : reason) {
      r.addResultingFormula(f);
    }
  }

  /**
   * Set a labelled subformula to be expanded.
   *
   * @param f the subformula
   */
  public void setExpanded(LabelledFormula<P> f) {
    f.setExpanded();
  }

  /**
   * Record disjunction to be expanded.
   *
   * @param f the disjunction
   */
  public void setExpandedDisjunction(LabelledFormula<P> f) {
    expandedDisjunctions.add(f);
  }

  /**
   * Returns all expanded disjunctions.
   *
   * @return all expanded disjunctions
   */
  public ExpandedDisjunctions<P> getDisjunctions() {
    return expandedDisjunctions;
  }

  /**
   * Creates a new world.
   *
   * @return the new world
   */
  public World newWorld() {
    return worlds.newWorld();
  }

  /**
   * Relate two worlds.
   *
   * @param w0 a world
   * @param w1 a world
   * @param reason the subformula that caused this
   */
  public void relateWorlds(World w0, World w1, LabelledFormula<P> reason) {
    rel.add(w0, w1, reason);
  }

  /**
   * Returns the successing worlds of a given world.
   *
   * @param w a world
   * @return the successing worlds
   */
  public Collection<World<P>> getSucc(World<P> w) {
    return rel.succ(w);
  }

  /**
   * Returns the parent disjunctions of a subformula.
   *
   * @param f a subformula
   * @return the parent disjunctions
   */
  public Set<Formula<P>> getParentDisj(Formula<P> f) {
    return parentDisj.get(f);
  }

  /**
   * Return all labelled formulas the elimination explanation of which contains
   * a given formula f.
   *
   * @param f a subformula
   * @return the labelled formulas
   */
  public Collection<LabelledFormula<P>> getExplanationContains(Formula<P> f) {
    return labelledFormulas.getEliminationExplanationContains(f);
  }

  /**
   * Label a subformula.
   *
   * @param w the world
   * @param f the subformula
   * @return the labelled subformula
   */
  public LabelledFormula<P> label(World w, Formula<P> f) {
    return labelledFormulas.newLabelledFormula(w, f);
  }

  /**
   * Returns true if a subformula is blocked in a given world.
   *
   * @param w the world
   * @param f the subformula
   * @return true if the subformula is blocked
   */
  public boolean isBlocked(World w, Formula<P> f) {
    if(preBlocked.isBlocked(w, f))
      return true;
    LabelledFormula<P> lf = getLabelledFormula(w, f);
    if(lf!=null) {
      return lf.getState() == FormulaState.BLOCKED;
    }
    return false;
  }

  /**
   * Returns true if a subformula is neither expanded nor blocked.
   *
   * @param w the world
   * @param f the subformula
   * @return true if the subformula is neither expanded nor blocked
   */
  public boolean isUnknown(World w, Formula<P> f) {
    if(preBlocked.isBlocked(w, f))
      return false;
    LabelledFormula<P> lf = getLabelledFormula(w, f);
    if(lf!=null) {
      return lf.getState().equals(FormulaState.UNKNOWN);
    }
    return true;
  }

  /**
   * Returns the labelled formula for a given world and subformula.
   *
   * @param w the world
   * @param f the subformula
   * @return the labelled formula
   */
  public LabelledFormula<P> getLabelledFormula(World w, Formula<P> f) {
    for(LabelledFormula<P> lf : getLabelledFormulae(f)) {
      if(lf.getWorld() == w) {
        return lf;
      }
    }
    return null;
  }

  /**
   * Returns all labelled formulas for a given formula.
   *
   * @param f the formula
   * @return all labelled formulas
   */
  public Collection<LabelledFormula<P>> getLabelledFormulae(Formula<P> f) {
    return (Collection<LabelledFormula<P>>)f.getPayload();
  }

  /**
   * Returns true if a disjunction has an unknown disjunct.
   *
   * @param lf the disjunction
   * @return true if the subformula has an unknown disjunct
   */
  public boolean hasUnknownDisjunct(LabelledFormula<P> lf) {
    for(Formula<P> f1 : lf.getFormula()) {
      if(isUnknown(lf.getWorld(), f1)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the active disjunct of a disjunction.
   *
   * @param lf the disjunction
   * @return the active disjunct
   */
  public LabelledFormula<P> getActiveDisjunct(LabelledFormula<P> lf) {
    for(Formula<P> f1 : lf.getFormula()) {
      LabelledFormula<P> lf1 = getLabelledFormula(lf.getWorld(), f1);
      if(lf1 != null && lf1.getState().equals(FormulaState.ACTIVE))
        return getLabelledFormula(lf.getWorld(), f1);
    }
    return null; // this may not happen
  }

  /**
   * Removes the given subformulas from branch.
   *
   * @param lfs the subformulas
   */
  public void removeFromBranch(Collection<LabelledFormula<P>> lfs) {
    branch.removeAll(lfs);
  }

  /**
   * Removes the given subformulas from collection of expanded disjunctions.
   *
   * @param lfs the subformulas
   */
  public void removeFromExpandedDisjunctions(Collection<LabelledFormula<P>> lfs) {
    expandedDisjunctions.removeAll(lfs);
  }

  /**
   * Returns the resulting expanded subformulas for a given subformula.
   *
   * @param f the subformula
   * @return the resulting subformulas
   */
  public Collection<LabelledFormula<P>> getResultingFormulas(LabelledFormula<P> f) {
    Collection<LabelledFormula<P>> results = new ArrayList<LabelledFormula<P>>(f.getResultingFormulas());
    for(LabelledFormula<P> rlf : new ArrayList<LabelledFormula<P>>(results)) {
      results.addAll(getResultingFormulas(rlf));
    }
    return results;
  }

  /**
   * Returns the current worlds.
   *
   * @return the worlds
   */
  public Worlds<P> getWorlds() {
    return worlds;
  }

  /**
   * Removes all worlds caused by a given subformula.
   *
   * @param f the subformula
   */
  public void removeWorldsCausedBy(LabelledFormula<P> f) {
    worlds.removeCausedBy(f);
  }

  /**
   * Removes all relations between worlds caused by a given subformula.
   * @param f the subformula
   */
  public void removeRelationsCausedBy(LabelledFormula<P> f) {
    rel.removeCausedBy(f);
  }

  /**
   * Clone the tableau.
   * 
   * @return the cloned tableau
   */
  @Override
  public Tableau<P> clone() {
    // clone formula
    Map<Formula<P>, Formula<P>> formulaMap = initialFormula.cloneWithReference();

    Tableau<P> clone = new Tableau<P>(rules, propositionComparator, lazy);
    clone.state = state;

    // handle initial formula
    clone.setFormula(formulaMap.get(initialFormula));

    // handle worlds
    Map<World<P>, World<P>> worldMap = new HashMap<World<P>, World<P>>(worlds.size());
    clone.worlds.setCount(worlds.getCount());
    for(World<P> w : worlds) {
      World<P> v = new World<P>(w.getId(), propositionComparator);
      worldMap.put(w, v);
      clone.worlds.add(v);
    }

    // handle labelled formulae
    Map<LabelledFormula<P>, LabelledFormula<P>> labelledFormulaMap = new HashMap<LabelledFormula<P>, LabelledFormula<P>>(labelledFormulas.size());
    for(LabelledFormula<P> lf : labelledFormulas) {
      LabelledFormula<P> lf2 = clone.labelledFormulas.newLabelledFormula(
              worldMap.get(lf.getWorld()), formulaMap.get(lf.getFormula()));
      
      for(Formula<P> elim : lf.getEliminationExplanation()) {
        lf2.getEliminationExplanation().add(formulaMap.get(elim));
      }

      lf2.setState(lf.getState());
      lf2.setExpanded(lf.isExpanded());

      labelledFormulaMap.put(lf, lf2);
    }

    for(LabelledFormula<P> lf : labelledFormulas) {
      LabelledFormula<P> lf2 = labelledFormulaMap.get(lf);
      for(LabelledFormula<P> res : lf.getResultingFormulas()) {
        lf2.addResultingFormula(labelledFormulaMap.get(res));
      }
    }

    // handle literals and reasons stored in each world
    for(World<P> w : worlds) {
      World<P> v = worldMap.get(w);
      for(LabelledFormula<P> pos : w.getPositiveLiterals()) {
        v.addLiteral(labelledFormulaMap.get(pos));
      }
      for(LabelledFormula<P> neg : w.getNegativeLiterals()) {
        v.addLiteral(labelledFormulaMap.get(neg));
      }
      v.setReason(labelledFormulaMap.get(w.getReason()));
    }

    // handle world relation
    for(LabelledFormula<P> reason : rel.getResulting().keySet()) {
      Collection<Pair<World<P>>> res = rel.getResulting().get(reason);
      for(Pair<World<P>> r : res) {
        clone.rel.add(worldMap.get(r.getFirst()), worldMap.get(r.getSecond()),
                labelledFormulaMap.get(reason));
      }
    }

    // handle expanded disjunctions
    for(LabelledFormula<P> lf : expandedDisjunctions) {
      clone.expandedDisjunctions.add(labelledFormulaMap.get(lf));
    }

    // build branch
    for(LabelledFormula<P> lf : branch.getUnexpanded()) {
      clone.branch.getUnexpanded().add(labelledFormulaMap.get(lf));
    }

    return clone;
  }
}
