/* Copyright (c) 2010, Johannes Köster <johannes.koester@tu-dortmund.de>
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see "license.txt"
 * for a description.
 */
package modalLogic.formula.io;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import modalLogic.formula.Formula;
import modalLogic.formula.factory.FormulaFactory;

/**
 * A FormulaReader that reads from a MathML representation.
 * 
 * @author Johannes Köster <johannes.koester@tu-dortmund.de>
 */
public class MathMLReader<E> implements FormulaReader<E> {

  private XMLEventReader xmlr;
  private PropositionMap<E> propositionMap;

  /**
   * Constructor of class MathMLReader.
   *
   * @param xmlr instance of the XMLEventReader
   * @param propositionMap map of propositions.
   */
  public MathMLReader(XMLEventReader xmlr, PropositionMap<E> propositionMap) {
    this.xmlr = xmlr;
    this.propositionMap = propositionMap;
  }

  /**
   * Read a formula.
   *
   * @return the read formula
   * @throws XMLStreamException can occur while reading.
   */
  @Override
  public Formula<E> read() throws XMLStreamException {
    FormulaFactory<E> formula = new FormulaFactory<E>();

    boolean ignoreNextEnd = false;

    try {
      while (xmlr.hasNext()) {
        XMLEvent e = xmlr.nextEvent();
        if (e.isStartElement()) {
          StartElement s = e.asStartElement();
          String tag = s.getName().getLocalPart();

          if (tag.equals(MathMLWriter.AND)) {
            formula.openConjunction();
          } else if (tag.equals(MathMLWriter.OR)) {
            formula.openDisjunction();
          } else if (tag.equals(MathMLWriter.IMPLIES)) {
            formula.openImplication();
          } else if (tag.equals(MathMLWriter.NOT)) {
            formula.negation();
            ignoreNextEnd = true;
          } else if (tag.equals(MathMLWriter.CI)) {
            formula.literal(propositionMap.get(xmlr.getElementText()));
          }
        } else if (e.isEndElement()) {

          EndElement f = e.asEndElement();
          String tag = f.getName().getLocalPart();
          if (tag.equals(MathMLWriter.APPLY)) {
            if (!ignoreNextEnd) {
              formula.close();
              if (formula.isRoot()) {
                break; // stop in case of multiple apply tags as siblings of a parent tag that wasn't read here
              }
            } else {
              ignoreNextEnd = false;
            }
          }

        }
      }
    } catch (UnsupportedOperationException ex) {
      System.out.println(formula.create().toString());
      throw ex;
    }

    return formula.create();
  }
}
