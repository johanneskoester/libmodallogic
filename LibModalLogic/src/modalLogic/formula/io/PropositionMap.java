/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modalLogic.formula.io;

/**
 * Provides interface for a map that returns a Proposition Object of type E for
 * a given String.
 *
 * @param <E> the type of proposition objects
 */
public interface PropositionMap<E> {

  /**
   * Returns a Proposition Object of type E for a given String.
   *
   * @param s the string
   * @return the propositon object
   */
  public E get(String s);
}