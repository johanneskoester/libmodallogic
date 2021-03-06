/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modalLogic.tableau;

import java.util.ArrayList;
import java.util.Collection;
import modalLogic.formula.Formula;
import modalLogic.formula.factory.FormulaFactory;
import modalLogic.formula.io.StringPropositionMap;
import modalLogic.formula.io.TextReader;
import modalLogic.formula.io.TextWriter;
import static org.junit.Assert.*;

/**
 *
 * @author johannes
 */
public class TableauTest {

  public TableauTest() {
  }

  @org.junit.BeforeClass
  public static void setUpClass() throws Exception {
  }

  @org.junit.AfterClass
  public static void tearDownClass() throws Exception {
  }

  @org.junit.Before
  public void setUp() throws Exception {
  }

  @org.junit.After
  public void tearDown() throws Exception {
  }

  @org.junit.Test
  public void testTextReader() throws Exception {

    String test = "A AND B AND C";
    Formula<String> f = new TextReader<String>(test, new StringPropositionMap()).read();
    System.out.println(f);
    assert f.getChildCount() == 3;

    f = new TextReader<String>("A AND B pp C AND NOT ( C OR D )", new StringPropositionMap()).read();
    System.out.println(f);
    assert f.getChildCount() == 3;
  }

  @org.junit.Test
  public void testTextWriter() throws Exception {
    Formula<String> f = new TextReader<String>("A AND B pp C AND NOT ( C OR D )", new StringPropositionMap()).read();
    StringBuilder s = new StringBuilder();
    new TextWriter<String>(s).write(f);
    System.out.println("Output:");
    System.out.println(s);
  }

  /**
   * Test of proofSearch method for constants.
   */
  @org.junit.Test
  public void testConstants() {
    FormulaFactory<String> ff = new FormulaFactory<String>();
    ff.openConjunction();
    ff.constant(true);
    ff.constant(false);
    ff.close();
    Formula<String> f = ff.create();
    System.out.println(f);

    Tableau instance = new Tableau(new KRules(), false);
    instance.setFormula(f);
    assertEquals(false, instance.proofSearch());
  }

  /**
   * Test the creation of singleton formulas.
   */
  @org.junit.Test
  public void testSingletons() {
    FormulaFactory<String> ff = new FormulaFactory<String>();
    ff.constant(true);
    Formula<String> f = ff.create();
    System.out.println(f);

    try {
      ff = new FormulaFactory<String>();
      ff.literal("A");
      ff.constant(false);
      ff.create();
    } catch (UnsupportedOperationException e) {
      // this is the expected behaviour since there is no operator between the two literals.
    }
  }

  /**
   * Test of proofSearch method of class Tableau.
   */
  @org.junit.Test
  public void testProofSearch() {
    System.out.println("proofSearch");
    FormulaFactory<String> f1 = new FormulaFactory<String>();
    f1.openConjunction();
    f1.literal("Lt0x0y0");
    f1.literal("ESTt0");
    f1.literal("hasArrowt0");
    f1.negation();
    f1.literal("hasGoldt0");
    f1.negation();
    f1.literal("Wumpusx0y0");
    f1.negation();
    f1.literal("Wellx0y0");
    f1.close();

    FormulaFactory<String> wumpus1factory = new FormulaFactory<String>();
    wumpus1factory.openDisjunction();
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        wumpus1factory.literal("Wumpusx" + i + "y" + j);
      }
    }
    wumpus1factory.close();
    Formula<String> Wumpus1 = wumpus1factory.create();

    Collection<Formula<String>> fs = new ArrayList<Formula<String>>();
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        for (int k = 0; k < 5; k++) {
          for (int l = 0; l < 5; l++) {
            if (!(i == k && j == l)) {
              FormulaFactory<String> wumpus2factory = new FormulaFactory<String>();
              wumpus2factory.negation();
              wumpus2factory.openConjunction();
              wumpus2factory.literal("Wumpusx" + i + "y" + j);
              wumpus2factory.literal("Wumpusx" + k + "y" + l);
              wumpus2factory.close();
              Formula<String> Wumpus2 = wumpus2factory.create();
              fs.add(Wumpus2);
            }
          }
        }
      }
    }

    FormulaFactory<String> conj = new FormulaFactory<String>();
    conj.openConjunction();

    conj.subformula(f1.create());
    conj.subformula(Wumpus1);
    for (Formula<String> f : fs) {
      conj.subformula(f);
    }
    conj.close();


    Formula<String> toproof = conj.create();
    toproof.toNegationNormalForm();
    System.out.println(toproof);

    Tableau instance = new Tableau(new KRules(), false);
    instance.setFormula(toproof);


    boolean expResult = true;
    boolean result = instance.proofSearch();
    assertEquals(expResult, result);
  }

  @org.junit.Test
  public void testProofSearch2() {
    System.out.println("proofSearch");
    FormulaFactory<String> testfactory = new FormulaFactory<String>();
    testfactory.openConjunction(); //BUG: negation() OKt0x0y0 : eccezione
    testfactory.negation();
    testfactory.literal("OKt0x0y0");
    testfactory.close();
    Formula<String> test = testfactory.create();
    Tableau<String> tableau = new Tableau<String>();
    tableau.setFormula(test);
    boolean result = tableau.proofSearch();
    assertEquals(true, result);
  }

  @org.junit.Test
  public void testGooglecodeExample() {
    FormulaFactory<String> factory = new FormulaFactory<String>();

    factory.openDisjunction();
    factory.literal("A");

    factory.literal("B");

    factory.negation();
    factory.literal("C");
    factory.close();

    Formula<String> formula = factory.create();

    Tableau<String> tableau = new Tableau<String>();
    tableau.setFormula(formula);
    boolean satisfiable = tableau.proofSearch();
    System.out.println(tableau.getWorlds().get(0).getPositiveLiterals());
    assertEquals(true, satisfiable);
    
    World<String> world = tableau.getWorlds().get(0);
    Formula<String> toBlock = formula.getChild(0);
    tableau = new Tableau<String>();
    tableau.setFormula(formula);
    tableau.block(world, toBlock);
    satisfiable = tableau.proofSearch();
    System.out.println(tableau.getWorlds().get(0).getPositiveLiterals());
    assertEquals(true, satisfiable);
  }
}
