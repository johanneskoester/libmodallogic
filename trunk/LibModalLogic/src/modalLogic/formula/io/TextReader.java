/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modalLogic.formula.io;

import java.io.InputStream;
import java.util.Scanner;
import modalLogic.formula.Formula;
import modalLogic.formula.factory.FormulaFactory;

/**
 *
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class TextReader<P> implements FormulaReader<P> {

  private Scanner scanner;
  private PropositionMap<P> propositionMap;

  public TextReader(InputStream stream, PropositionMap<P> propositionMap) {
    scanner = new Scanner(stream);
    this.propositionMap = propositionMap;
  }

  public TextReader(String text, PropositionMap<P> propositionMap) {
    this.scanner = new Scanner(text);
    this.propositionMap = propositionMap;
  }

  @Override
  public Formula<P> read() throws Exception {
    FormulaFactory<P> formula = new FormulaFactory<P>();
    String proposition = "";

    formula.openFormula();
    while (scanner.hasNext()) {
      if (scanner.hasNext("\\(")) {
        if (!proposition.isEmpty()) {
          throw new InvalidFormulaException();
        }
        scanner.next();
        formula.subformula(read());
      } else if (scanner.hasNext("\\)")) {
        scanner.next();
        break;
      } else {
        String token = scanner.next();
        if (token.equals(TextWriter.AND)) {
          setOperator(formula, Formula.CONJUNCTION);
          addProposition(formula, proposition);
          proposition = "";
        } else if (token.equals(TextWriter.OR)) {
          setOperator(formula, Formula.DISJUNCTION);
          addProposition(formula, proposition);
          proposition = "";
        } else if (token.equals(TextWriter.IMPLIES)) {
          setOperator(formula, Formula.IMPLICATION);
          addProposition(formula, proposition);
          proposition = "";
        } else if (token.equals(TextWriter.NOT)) {
          if(!proposition.isEmpty())
            throw new InvalidFormulaException();
          formula.negation();
        } else if (token.equals(TextWriter.NECESSARY)) {
          setOperator(formula, Formula.NECESSITY);
          addProposition(formula, proposition);
          proposition = "";
        } else if (token.equals(TextWriter.POSSIBLE)) {
          setOperator(formula, Formula.POSSIBILITY);
          addProposition(formula, proposition);
          proposition = "";
        } else {
          if(!proposition.isEmpty())
            proposition += " ";
          proposition += token;
        }
      }
    }
    if(!proposition.isEmpty()) {
      addProposition(formula, proposition);
    }
    formula.close();
    
    Formula<P> f = formula.create();
    if (f.getChildCount() == 1 && f.getType() != Formula.NECESSITY && f.getType() != Formula.POSSIBILITY) {
      return f.getChild();
    }
    return f;
  }

  private void addProposition(FormulaFactory<P> formula, String proposition) throws InvalidFormulaException {
    if (proposition.isEmpty()) {
      throw new InvalidFormulaException();
    }
    formula.literal(propositionMap.get(proposition));
  }

  private void setOperator(FormulaFactory<P> formula, short type) throws InvalidFormulaException {
    if (formula.isUndefined()) {
      formula.setCurrentType(type);
    } else if(formula.getCurrentType() != type) {
      throw new InvalidFormulaException();
    }
  }

  private static class InvalidFormulaException extends Exception {
  }
}
