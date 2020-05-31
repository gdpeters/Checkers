
package checkers;

/**
 * Rules of the game viewable to players with the Rules button
 */
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public class Rules extends JFrame {

    private JFrame frame;
    
    public Rules()
    {
        makeFrame();
    }
  
    public void viewFrame()
    {
        frame.setVisible(true);
    }
    
    private void makeFrame()
    {
        JEditorPane jEditorPane = new JEditorPane();
        jEditorPane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(jEditorPane);

        // add an html editor kit
        HTMLEditorKit kit = new HTMLEditorKit();
        jEditorPane.setEditorKit(kit);

        // add html styles
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body {color:#000; font-family:times; margin: 4px; }");
        styleSheet.addRule("h1 {color: #343b45;}");
        styleSheet.addRule("h2 {color: #ff0000;}");
        styleSheet.addRule("h3 {color: #000;}");
        styleSheet.addRule("pre {font : 10px monaco; color : black; background-color : #fafafa; }");

        String rules = getHTML();

        // create document, add to JEditor pane, add html
        Document doc = kit.createDefaultDocument();
        jEditorPane.setDocument(doc);
        jEditorPane.setText(rules);

        frame = new JFrame("Checkers Rules");
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.setSize(new Dimension(400,600));

        // center the jframe, then make it visible
        frame.setLocationRelativeTo(null);
        frame.setVisible(false);
    }
  
  public String getHTML()
  {
      String rules = 
        "<html><body><h1>Checkers</h1><h2>Rules of the Game</h2><h3><br>OBJECT</h3>"
        + "<p>Eliminate all of your opponent’s checkers from the gameboard by capturing their pieces.</p>"
        + "<h3><br>HOW TO PLAY</h3>"
        + "<p>When the game starts, please select which player will go first.</p>"
        + "<p>On your turn, select any one of your checkers by the movement’s rules described below.</p>"
        + "<p>After selecting and moving one of your checker pieces, your turn is over.</p>"
        + "<p>The game will continue on with players alternating turns.</p>"
        + "<h3><br>MOVEMENT RULES</h3>"
        + "<p>• Always move your checker diagonally forward, toward your opponent’s side of the gameboard.</p>"
        + "<p>NOTE: After a checker becomes a “King,” it can move diagonally forward and backward.</p>"
        + "<p>• Move your checker one space diagonally, to an open adjacent square;</p>"
        + "<p>or jump one or more opponent’s checkers diagonally to an open square adjacent to the checker your jumped.</p>"
        + "<p>When you jump over an opponent’s checker, you capture it.</p>"
        + "<p>• If all squares adjacent to your checker are occupied, your checker is blocked and cannot move.</p>"
        + "<h3><br>CAPTURING AN OPPONENT'S CHECKER</h3>"
        + "<p>• If you jump an opponent’s checker, you capture it.</p>"
        + "<p>The checker piece will then be removed from the gameboard.</p>"
        + "<h3><br>BECOMING A KING</h3>"
        + "<p>When one of your checker pieces reaches the first-row on your opponent’s side of the gameboard, it will become a “King”.</p>"
        + "<p>The checker piece will change appearance, and can move forward or backward on the gameboard.</p>"
        + "<h3>HOW TO WIN</h3>"
        + "<p>The first player to capture all of your opponent’s checkers wins!</p>"
        + "</body>"
        + "</html>";

     return rules;
  }
}