/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modalLogic.formula.io;

import java.io.IOException;
import java.io.OutputStream;
import modalLogic.formula.Constant;
import modalLogic.formula.Formula;
import modalLogic.formula.Literal;

/**
 *
 * @author johannes
 */
public class TextWriter<P> implements FormulaWriter<P> {
  
  public static String AND = "AND";
  public static String OR = "OR";
  public static String IMPLIES = "IMPLIES";
  public static String NOT = "NOT";
  public static String NECESSARY = "NECESSARY";
  public static String POSSIBLE = "POSSIBLE";
  
  private OutputStream stream;
  private StringBuilder stringbuilder;
  boolean first = true;

  public TextWriter(OutputStream stream) {
    this.stream = stream;
  }
  
  public TextWriter(StringBuilder stringbuilder) {
    this.stringbuilder = stringbuilder;
  }
  
  @Override
  public void write(Formula<P> formula) throws Exception {
    if(formula.isNegation()) {
      append(NOT);
    }
    if(!(formula instanceof Literal)) {
        append("(");
      }

    if(formula instanceof Constant) {
      append(formula.toString());
    }
    else if (formula instanceof Literal) {
      append(formula.getProposition().toString());
    } else {
      switch (formula.getType()) {
        case Formula.CONJUNCTION:
          join(formula, AND);
          break;
        case Formula.DISJUNCTION:
          join(formula, OR);
          break;
        case Formula.IMPLICATION:
          join(formula, IMPLIES);
          break;
        case Formula.NECESSITY:
          join(formula, NECESSARY);
          break;
        case Formula.POSSIBILITY:
          join(formula, POSSIBLE);
          break;
        default:
          throw new UnsupportedOperationException("Unsupported operator.");
      }
    }
    if(!(formula instanceof Literal)) {
      append(")");
    }
  }
  
  private void join(Formula<P> formula, String operator) throws Exception {
    int i = 1;
    for(Formula<P> child : formula) {
      write(child);
      if(i < formula.getChildCount()) {
        append(operator);
      }
      i++;
    }
  }
  
  private void append(String s) throws IOException {
    if(first)
      first = false;
    else
      s = " " + s;
    if(stream != null)
      stream.write(s.getBytes());
    else
      stringbuilder.append(s);
  }
}
