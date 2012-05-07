/* Copyright (c) 2010, Johannes Köster <johannes.koester@tu-dortmund.de>
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see "license.txt"
 * for a description.
 */

package modalLogic.formula.io;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import modalLogic.formula.Constant;
import modalLogic.formula.Formula;
import modalLogic.formula.Literal;

/**
 * A FormulaWriter that writes to a MathML representation.
 *
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class MathMLWriter<E> implements FormulaWriter<E> {

  public static String APPLY = "apply";
  public static String IMPLIES = "implies";
  public static String FOR = "for";
  public static String NOT = "not";
  public static String OR = "or";
  public static String AND = "and";
  public static String CI = "ci";
  public static String CN = "cn";
  private XMLStreamWriter xmlw;

  /**
   * Constructor of class MathMLWriter.
   *
   * @param xmlw the XMLStreamWriter
   */
  public MathMLWriter(XMLStreamWriter xmlw) {
    this.xmlw = xmlw;
  }

  /**
   * Write a given formula.
   *
   * @param formula the formula to write
   * @throws XMLStreamException an exception that can occur during writing
   */
  @Override
  public void write(Formula<E> formula) throws XMLStreamException {
    if(formula.isNegation()) {
      xmlw.writeStartElement(APPLY);
      xmlw.writeEmptyElement(NOT);
    }

    if(formula instanceof Constant) {
      xmlw.writeStartElement(CI);
      xmlw.writeAttribute("type", "constant");
      xmlw.writeCData(formula.toString());
      xmlw.writeEndElement();
    }
    else if (formula instanceof Literal) {
      xmlw.writeStartElement(CI);
      xmlw.writeCData(((Literal)formula).getProposition().toString());
      xmlw.writeEndElement();
    } else {
      xmlw.writeStartElement(APPLY);
      switch (formula.getType()) {
        case Formula.CONJUNCTION:
          xmlw.writeEmptyElement(AND);
          break;
        case Formula.DISJUNCTION:
          xmlw.writeEmptyElement(OR);
          break;
        case Formula.IMPLICATION:
          xmlw.writeEmptyElement(IMPLIES);
          break;
        case Formula.NECESSITY:
        case Formula.POSSIBILITY:
          throw new UnsupportedOperationException("Modal logic not supported by mathml.");
        default:
          throw new UnsupportedOperationException("Unsupported operator.");
      }

      for (Formula<E> child : formula) {
        write(child);
      }
      xmlw.writeEndElement();
    }
    if(formula.isNegation()) {
      xmlw.writeEndElement();
    }
  }
}
