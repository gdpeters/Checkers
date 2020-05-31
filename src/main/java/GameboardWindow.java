package checkers;

/** 
 * Designs the JFrame, JPanels, and the eventhandlers for
 * the GUI.
 */
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GameboardWindow extends JFrame
{
    private JFrame frame;
    private JLayeredPane pane;
    private GameBoard gameboard;
    private int xButton = 935, yButton = 325, yBShift = 50;
    private int bWidth = 60;
    private String buttonFile = "buttonBlue.png";
    private JButton multJumpButton, submitJumpButton, cancelJumpButton, skipTurnButton;
    private Trough p1Trough, p2Trough;
    private CurrentPlayer current;
    private boolean flip;
    private static final int FRAMEWIDTH = 1230, FRAMEHEIGHT = 860;
    private BufferedImage labelMultJump, labelSubmitJump, labelCancelJump, labelSkipTurn;
    private JComponent labelGraphics;
    private Rules rules;

    public GameboardWindow()
    {
        resetGameboard();

    }//END WindowFrame constructor
    
    /** Req 1.1.0 / 1.2.0 / 1.2.1 / 1.2.2
     * Adds panels, trough, and buttons to gameboard
     */
    private void resetGameboard()
    {
        rules = new Rules();
        flip = false;

        //Window frame
        frame = new JFrame("Checkers!");

        //Layered pane
        pane = new JLayeredPane();
        pane.setPreferredSize(new Dimension(FRAMEWIDTH, FRAMEHEIGHT));

        //Create components
        gameboard = new GameBoard();
        gameboard.setBounds(40, 90, 680, 680);
        p1Trough = new Trough(gameboard.plyr1Img, gameboard.plyr1NumPieces);
        p1Trough.setBounds(750, 90, 440, 160);
        p2Trough = new Trough(gameboard.plyr2Img, gameboard.plyr2NumPieces);
        p2Trough.setBounds(750, 860 - 90 - 160, 440, 160);
        current = new CurrentPlayer(gameboard.playercheck);
        current.setBounds(750, (860/2) - 115, 100, 200);
        this.labelGraphics();
        this.multJumpButton(xButton, yButton, bWidth, bWidth);
        this.submitJumpButton(xButton - 75, yButton, bWidth, bWidth);
        this.cancelJumpButton(xButton + 100, yButton, bWidth, bWidth);

        //Add components to pane
        pane.add(this.backgroundPanel(), new Integer(-1));
        pane.add(gameboard, new Integer(2));
        pane.add(p1Trough, new Integer(3));
        pane.add(p2Trough, new Integer(3));
        pane.add(multJumpButton, new Integer(4));
        pane.add(this.undoButton(xButton, yButton + 50, bWidth, bWidth), new Integer(4));
        pane.add(this.skipTurnButton(xButton, yButton + 100, bWidth, bWidth), new Integer(4));
        pane.add(this.rulesButton(xButton, yButton + 150, bWidth, bWidth), new Integer(4));
        pane.add(this.restartButton(xButton, yButton + 200, bWidth, bWidth), new Integer(4));
        pane.add(labelGraphics, new Integer(5));
        pane.add(current, new Integer(5));

        //Gameboard window action
        this.addMouseListener();//Method written below

        //frame properties
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(pane);
        frame.pack();
        // center the jframe, then make it visible
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public JPanel backgroundPanel()
    {
        JPanel p = new JPanel();
        try {
            final BufferedImage image = ImageIO.read(getClass().getClassLoader().getResource("chalkBoard.jpg"));

            p = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(image, 0, 0, null);
                }
            };
            p.setBounds(0,0,FRAMEWIDTH,FRAMEHEIGHT);
        } catch (IOException ex)
        {
            System.err.println(ex.getMessage());
        }

        return p;
    }//END backgroundPanel()

    /** Req 4.2.3
     * Defines multiple jump functionality
     * @param x x-coordinate of mouse click
     * @param y y-coordinate of mouse click
     * @param w width of square
     * @param h height of square
     */
    public void multJumpButton(int x, int y, int w, int h)
    {

        JButton button = null;
        BufferedImage img = null;
        try {
            img = ImageIO.read(getClass().getClassLoader().getResource(buttonFile));
            ImageIcon icon = new ImageIcon(img);
            button = new JButton(icon);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        button.setBounds(x, y, w, h);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        //When play game button is clicked, gameboard window opens
        button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    
                    if (!p1Trough.isLoser() && !p2Trough.isLoser())
                    {
                        flip = !flip;
                        pane.remove(multJumpButton);
                        pane.remove(labelGraphics);
                        pane.add(submitJumpButton);
                        pane.add(cancelJumpButton);
                        pane.add(labelGraphics);
                        gameboard.flipMultJump();
                    }

                }
            });
            multJumpButton = button;

    }//END multJumpButton()

    public void submitJumpButton(int x, int y, int w, int h)
    {
        JButton button = null;
        BufferedImage img = null;
        try {
            img = ImageIO.read(getClass().getClassLoader().getResource("buttonGreen.png"));
            ImageIcon icon = new ImageIcon(img);
            button = new JButton(icon);

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        button.setBounds(x, y, w, h);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    flip = !flip;
                    pane.remove(submitJumpButton);
                    pane.remove(cancelJumpButton);
                    pane.remove(labelGraphics);
                    pane.add(multJumpButton);
                    pane.add(labelGraphics);
                    gameboard.flipMultJump();
                    gameboard.repaint();
                    p1Trough.setTroughPieces(gameboard.plyr1NumPieces);
                    p2Trough.setTroughPieces(gameboard.plyr2NumPieces);
                    p1Trough.repaint();
                    p2Trough.repaint();
                    if (p1Trough.isLoser())
                        displayBanner(2);
                    else if (p2Trough.isLoser())
                        displayBanner(1);
                    else
                    {
                        current.setCurrent(gameboard.playercheck);
                        current.repaint();
                    }
                }
            });
        submitJumpButton = button;
    }//END submitJumpButton()

    public void displayBanner(int i)
    {
        JComponent jc = null;
        try {
            if (i == 1)
            {
                final BufferedImage img = ImageIO.read(getClass().getClassLoader().getResource("bannerPlyr1.png"));
                jc = new JComponent() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);

                        g.drawImage(img, 40, 250, null);
                        
                    }
                };
                jc.setBounds(0,0,FRAMEWIDTH,FRAMEHEIGHT);

                pane.add(jc, new Integer(6));
            }
            if (i == 2)
            {
                final BufferedImage img = ImageIO.read(getClass().getClassLoader().getResource("bannerPlyr2.png"));
                jc = new JComponent() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);

                        g.drawImage(img, 40, 250, null);

                    }
                };
                jc.setBounds(0,0,FRAMEWIDTH,FRAMEHEIGHT);
                pane.add(jc, new Integer(6));
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void cancelJumpButton(int x, int y, int w, int h)
    {
        JButton button = null;
        BufferedImage img = null;
        try {
            img = ImageIO.read(getClass().getClassLoader().getResource("buttonGreen.png"));
            ImageIcon icon = new ImageIcon(img);
            button = new JButton(icon);

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        button.setBounds(x, y, w, h);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flip = !flip;
                pane.remove(submitJumpButton);
                pane.remove(cancelJumpButton);
                pane.remove(labelGraphics);
                pane.add(multJumpButton);
                pane.add(labelGraphics);
                gameboard.cancelMultJump();
                gameboard.repaint();

            }
        });
        cancelJumpButton = button;
    }//END cancelJumpButton()

    /** Req 4.2.4
     * Button to undo as many plays that have been played
     * @param x x-coordinate of mouse click
     * @param y y-coordinate of mouse click
     * @param w width of square
     * @param h height of square
     * @return JButton to undo the previous play
     */
    public JButton undoButton(int x, int y, int w, int h)
    {
        JButton button = null;
        BufferedImage img = null;
        try {
            img = ImageIO.read(getClass().getClassLoader().getResource(buttonFile));
            ImageIcon icon = new ImageIcon(img);
            button = new JButton(icon);

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        button.setBounds(x, y, w, h);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                   boolean tf = gameboard.undo();

                    if (tf && !p1Trough.isLoser() && !p2Trough.isLoser())
                    {
                        gameboard.repaint();
                        p1Trough.setTroughPieces(gameboard.plyr1NumPieces);
                        p2Trough.setTroughPieces(gameboard.plyr2NumPieces);
                        p1Trough.repaint();
                        p2Trough.repaint();
                        current.setCurrent(gameboard.playercheck);
                        current.repaint();
                    }
                }
            });

        return button;
    }//END undoButton()

    /** Req. 4.2.5
     * Button to skip a player's turn
     * @param x x-coordinate of mouse click
     * @param y y-coordinate of mouse click
     * @param w width of square
     * @param h height of square
     * @return JButton to skip a player's turn
     */
    public JButton skipTurnButton(int x, int y, int w, int h)
    {
        JButton button = null;
        BufferedImage img = null;
        try {
            img = ImageIO.read(getClass().getClassLoader().getResource(buttonFile));
            ImageIcon icon = new ImageIcon(img);
            button = new JButton(icon);

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        button.setBounds(x, y, w, h);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if (!p1Trough.isLoser() && ! p2Trough.isLoser())
                {
                    gameboard.skipTurn();
                    gameboard.repaint();
                    current.setCurrent(gameboard.playercheck);
                    current.repaint();
                }
            }
        });
        return button;
    }//END skipTurnButton()

    /** Req 2.1.3 / 4.2.6
     * Button to view rules
     * @param x x-coordinate of mouse click
     * @param y y-coordinate of mouse click
     * @param w width of square
     * @param h height of square
     * @return JButton to view rules
     */
    public JButton rulesButton(int x, int y, int w, int h)
    {
        JButton button = null;
        BufferedImage img = null;
        try {
            img = ImageIO.read(getClass().getClassLoader().getResource(buttonFile));
            ImageIcon icon = new ImageIcon(img);
            button = new JButton(icon);

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        button.setBounds(x, y, w, h);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);


        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rules.viewFrame();

            }
        });
        return button;
    }//END rulesButton()

    /** Req 4.2.7
     * Button to restart the game
     * @param x x-coordinate of mouse click
     * @param y y-coordinate of mouse click
     * @param w width of square
     * @param h height of square
     * @return JButton to restart the game
     */
    public JButton restartButton(int x, int y, int w, int h)
    {
        JButton button = null;
        BufferedImage img = null;
        try {
            img = ImageIO.read(getClass().getClassLoader().getResource(buttonFile));
            ImageIcon icon = new ImageIcon(img);
            button = new JButton(icon);

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        button.setBounds(x, y, w, h);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                frame.dispose();
                resetGameboard();

            }
        });
        return button;
    }//END restartButton()

    public void labelGraphics()
    {
        JComponent jc = null;
        try {
            labelMultJump = ImageIO.read(getClass().getClassLoader().getResource("labelMultJump.png"));
            labelSubmitJump = ImageIO.read(getClass().getClassLoader().getResource("labelSubmit.png"));
            labelCancelJump = ImageIO.read(getClass().getClassLoader().getResource("labelCancel.png"));

            final BufferedImage img1 = ImageIO.read(getClass().getClassLoader().getResource("labelUndo.png"));
            final BufferedImage img2 = ImageIO.read(getClass().getClassLoader().getResource("labelSkipTurn.png"));
            final BufferedImage img3 = ImageIO.read(getClass().getClassLoader().getResource("labelRulesSmall.png"));
            final BufferedImage img4 = ImageIO.read(getClass().getClassLoader().getResource("labelRestart.png"));
            final BufferedImage img5 = ImageIO.read(getClass().getClassLoader().getResource("chalkBorder.png"));

            jc = new JComponent() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);

                    if (flip)
                    {
                        g.drawImage(labelSubmitJump, xButton - 25, yButton + 15, null);
                        g.drawImage(labelCancelJump, xButton + 150, yButton + 10, null);
                    }
                    else
                        g.drawImage(labelMultJump, xButton + 50, yButton + 15, null);

                    g.drawImage(img1, xButton + 50, yButton + 60, null);
                    g.drawImage(img2, xButton + 50, yButton + 115, null);
                    g.drawImage(img3, xButton + 50, yButton + 160, null);
                    g.drawImage(img4, xButton + 50, yButton + 210, null);
                    g.drawImage(img5, 22, 68, null);

                }
            };
            jc.setBounds(0,0,FRAMEWIDTH,FRAMEHEIGHT);

        } catch (IOException ex)
        {
            System.err.println(ex.getMessage());
        }

        labelGraphics = jc;
    }//END labelGraphics()


    /** Req. 4.2.1 / 4.2.2 / 4.2.8/ 4.3.0
     * Directs control flow for every mouse click.
     */
    private synchronized void addMouseListener()
    {
        //This method determines what happens when a player clicks the mouse
        gameboard.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                int x = e.getX();
                int y = e.getY();

                boolean update = gameboard.playerClicked(x, y);
                if (update)
                {
                    gameboard.repaint();

                    p1Trough.setTroughPieces(gameboard.plyr1NumPieces);
                    p2Trough.setTroughPieces(gameboard.plyr2NumPieces);

                    p1Trough.repaint();
                    p2Trough.repaint();

                    if (p1Trough.isLoser())
                        displayBanner(2);
                    else if (p2Trough.isLoser())
                        displayBanner(1);
                    else
                    {
                        current.setCurrent(gameboard.playercheck);
                        current.repaint();
                    }
                }
            }
        });
    }//END addMouseListener()

}//END class

