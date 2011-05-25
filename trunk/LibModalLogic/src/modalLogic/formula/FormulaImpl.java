package modalLogic.formula;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class FormulaImpl provides an implementation of the inner nodes of the formula
 * tree.
 * 
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class FormulaImpl<T> extends Formula<T> {

  protected short type;
  protected boolean negation = false;
  private List<Formula<T>> children = new ArrayList<Formula<T>>();
  private Formula<T> parent;
  private Object payload;

  /**
   * Constructor of class FormulaImpl
   *
   * @param type the type of the node (see class Formula)
   */
  public FormulaImpl(short type) {
    this.type = type;
  }

  @Override
  public Formula<T> getChild(int index) {
    return children.get(index);
  }

  @Override
  public Formula<T> getParent() {
    return parent;
  }

  @Override
  public void setParent(Formula<T> formula) {
    parent = formula;
  }

  @Override
  public void setType(short type) {
    this.type = type;
  }

  @Override
  public short getType() {
    return type;
  }

  @Override
  public void negate() {
    negation = !negation;
  }

  @Override
  public boolean isNegation() {
    return negation;
  }

  @Override
  public boolean isNegationOf(Formula<T> f) {
    return false;
  }

  @Override
  public boolean isNegationOf(Formula<T> formula, Comparator<T> propositionComparator) {
    return false;
  }

  public boolean isEquivalent(Formula<T> f) {
    return false;
  }

  /**
   * Add a child formula to current formula.
   *
   * @param child the new child formula
   * @throws UnsupportedOperationException if multiple children are added to
   * unary operator.
   */
  @Override
  public void addChild(Formula<T> child) {
    if ((type == NECESSITY || type == POSSIBILITY) && !children.isEmpty()) {
      throw new UnsupportedOperationException("You may not add multiple children to a unary operator");
    } else if (type == IMPLICATION && children.size() == 2) {
      throw new UnsupportedOperationException("You may not add more than two children to an implication");
    } else {
      children.add(child);
      child.setParent(this);
    }
  }

  @Override
  public Iterator<Formula<T>> iterator() {
    return children.iterator();
  }

  @Override
  public Formula<T> getChild() {
    return children.get(0);
  }

  @Override
  public String toString() {
    String s = "";
    switch (type) {
      case FormulaImpl.CONJUNCTION:
        s = naryOperatorToString("∧");
        break;
      case FormulaImpl.DISJUNCTION:
        s = naryOperatorToString("∨");
        break;
      case FormulaImpl.IMPLICATION:
        s = naryOperatorToString("⇒");
        break;
      case FormulaImpl.NECESSITY:
        s = "□" + getChild();
        break;
      case FormulaImpl.POSSIBILITY:
        s = "◊" + getChild();
        break;
    }
    s = "(" + s + ")";

    if (negation) {
      s = "¬" + s;
    }

    return s;
  }

  /**
   * Helper method to return a string representation for a given n-ary operator.
   *
   * @param operator the operator
   * @return the string representation for an n-ary operator
   */
  private String naryOperatorToString(String operator) {
    String s = "";
    Iterator<Formula<T>> child = iterator();

    while (child.hasNext()) {
      s += child.next().toString();
      if (child.hasNext()) {
        s += operator;
      }
    }
    return s;
  }

  @Override
  public void toNegationNormalForm() {
    if (type == IMPLICATION) {
      // translate implication to disjunction
      getChild(0).negate();
      type = DISJUNCTION;
    }
    if (isNegation()) {
      switch (type) {
        case CONJUNCTION:
          type = DISJUNCTION;
          negateDeep();
          break;
        case DISJUNCTION:
          type = CONJUNCTION;
          negateDeep();
          break;
        case NECESSITY:
          type = POSSIBILITY;
          negateDeep();
          break;
        case POSSIBILITY:
          type = NECESSITY;
          negateDeep();
          break;
      }
    }
    for (Formula<T> child : this) {
      child.toNegationNormalForm();
    }
  }

  /**
   * Negate this and all child nodes.
   */
  private void negateDeep() {
    negate();

    for (Formula<T> child : this) {
      child.negate();
    }
  }

  @Override
  public boolean contains(T proposition) {
    for (Formula child : children) {
      if (child.contains(proposition)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Set<T> getPropositions() {
    Set<T> propositions = new HashSet<T>();
    for (Formula child : children) {
      propositions.addAll(child.getPropositions());
    }
    return propositions;
  }

  /**
   * Non-literal formulas cannot have a single proposition.
   * 
   * @throws UnsupportedOperationException
   */
  @Override
  public T getProposition() {
    throw new UnsupportedOperationException("Not supported for formulas.");
  }

  @Override
  public int indexOf(Formula<T> child) {
    return children.indexOf(child);
  }

  @Override
  public int getChildCount() {
    return children.size();
  }

  @Override
  public void removeChild(Formula<T> child) {
    children.remove(child);
  }

  @Override
  public void setPayload(Object payload) {
    this.payload = payload;
  }

  @Override
  public Object getPayload() {
    return payload;
  }

  @Override
  public FormulaImpl<T> clone() {
    FormulaImpl<T> clone = new FormulaImpl<T>(type);
    clone.negation = negation;

    for (Formula<T> child : this) {
      clone.addChild(child.clone());
    }

    return clone;
  }

  @Override
  public Map<Formula<T>, Formula<T>> cloneWithReference() {
    Map<Formula<T>, Formula<T>> reference = new HashMap<Formula<T>, Formula<T>>();
    cloneWithReference(reference);
    return reference;
  }

  @Override
  public FormulaImpl<T> cloneWithReference(Map<Formula<T>, Formula<T>> reference) {
    FormulaImpl<T> clone = new FormulaImpl<T>(type);
    clone.negation = negation;

    for (Formula<T> child : this) {
      clone.addChild(child.cloneWithReference(reference));
    }

    reference.put(this, clone);

    return clone;
  }
}
