
package checkers;

/**
 * Gameboard start window. When initially opened,
 * an automatically generated game begins to play until
 * players click Start.
 */
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JButton;

public class StartWindow extends JFrame
{
    private JFrame frame;
    private JLayeredPane pane;
    private GameBoard gmbrd;
    private int xButton = 800, yButton = 275, yBShift = 95;//button xy values
    private int xLabel = 900, yLabel = 285, yLShift = 100;//label xy values
    private static final int FRAMEWIDTH = 1230, FRAMEHEIGHT = 860;
    private boolean stop;
    private Rules rules;

    public StartWindow()
    {
        rules = new Rules();

        //Window frame
        frame = new JFrame("Checkers!");

        //Layered pane
        pane = new JLayeredPane();
        pane.setPreferredSize(new Dimension(FRAMEWIDTH, FRAMEHEIGHT));

        //Set up autoPlay gameboard
        gmbrd = new GameBoard();
        gmbrd.setBounds(40, 90, 680, 680);

        //Add components to pane
        pane.add(this.backgroundPanel(), new Integer(-1));//background chalk board
        pane.add(this.playButton(xButton, yButton, 100, 100), new Integer(0));//play game button
        pane.add(this.rulesButton(xButton, yButton + yBShift, 100, 100), new Integer(0));//rules button
        pane.add(gmbrd, new Integer(2));//autoPlay gameboard

        //Frame properties
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(pane);
        frame.pack();
        // center the jframe, then make it visible
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        stop = false;
        try {
            TimeUnit.MICROSECONDS.sleep(5000);
            this.autoPlay();
        } catch (InterruptedException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void autoPlay()
    {
        int[] xy = {5,2,4,3,
                    4,5,5,4,
                    7,2,6,3,
                    5,4,7,2,
                    3,2,2,3,
                    2,5,1,4,
                    6,1,5,2,
                    1,4,3,2,
                    4,1,2,3,
                    3,6,2,5,
                    2,3,3,4,
                    6,5,5,4,
                    4,3,6,5,
                    9,2,5,4,3,6,1,9,
                    7,0,5,2,
                    7,6,5,4,
                    1,2,2,3,
                    5,6,4,5,
                    5,2,4,3,
                    5,4,3,2,
                    2,1,4,3,
                    1,6,2,5,
                    0,1,1,2,
                    0,7,1,6,
                    2,3,1,4,
                    0,5,2,3,
                    3,0,4,1,
                    7,2,6,1,
                    5,0,7,2,
                    2,5,3,4,
                    9,4,3,2,5,0,7,9,
                    4,5,3,4,
                    4,1,3,2,
                    6,7,5,6,
                    7,2,6,3,
                    5,6,6,5,
                    6,3,5,4,
                    9,6,5,4,3,2,1,9,
                    9,1,0,3,2,1,4,9,
                    3,4,4,3,
                    1,4,2,5,
                    2,7,3,6,
                    2,5,1,6,
                    4,3,5,2,
                    1,6,2,7,
                    3,6,4,5,
                    0,7,1,6,
                    5,2,4,1,
                    1,6,2,5,
                    4,1,3,0,
                    2,7,3,6,
                    3,0,2,1,
                    3,6,5,4,
                    2,1,0,3,
                    2,5,3,4,
                    0,3,1,2,
                    5,4,4,5,
                    4,7,5,6,
                    4,5,6,7,
                    1,2,0,3,
                    3,4,2,5,
                    0,3,1,4,
                    2,5,0,3,
        };
        try {
            for (int i = 0; i < xy.length; i = i + 2)
            {
                if (stop)
                    throw new InterruptedException();

                if (xy[i] == 9)
                {
                    gmbrd.flipMultJump();
                    i++;

                    while (xy[i] != 9)
                    {
                        TimeUnit.MICROSECONDS.sleep(500000);
                        gmbrd.playerClicked(xy[i] * 85 + 15, xy[i + 1] * 85 + 15);
                        gmbrd.repaint();
                        i = i + 2;
                    }
                    i--;
                    TimeUnit.MICROSECONDS.sleep(500000);
                    gmbrd.flipMultJump();
                    gmbrd.repaint();
                }
                else
                {
                    TimeUnit.MICROSECONDS.sleep(500000);
                    gmbrd.playerClicked(xy[i] * 85 + 15, xy[i + 1] * 85 + 15);
                    gmbrd.repaint();
                }
            }

        } catch (InterruptedException ex) {
            System.err.println(ex.getMessage());
        }

        JComponent jc = null;
        try {
                final BufferedImage img = ImageIO.read(getClass().getClassLoader().getResource("bannerPlyr1.png"));
                jc = new JComponent() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);

                    g.drawImage(img, 40, 250, null);

                    }
                };
                jc.setBounds(0,0,FRAMEWIDTH,FRAMEHEIGHT);
                pane.add(jc, new Integer(3));


        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    //Creates all text graphics on the Start Window
    public JPanel backgroundPanel()
    {
        JPanel p = null;
        try {
            final BufferedImage img1 = ImageIO.read(getClass().getClassLoader().getResource("chalkBoard.jpg"));
            final BufferedImage img2 = ImageIO.read(getClass().getClassLoader().getResource("checkersTitle.png"));
            final BufferedImage img3 = ImageIO.read(getClass().getClassLoader().getResource("labelPlayGame.png"));
            final BufferedImage img4 = ImageIO.read(getClass().getClassLoader().getResource("labelRules.png"));
            final BufferedImage img5 = ImageIO.read(getClass().getClassLoader().getResource("chalkBorder.png"));

            p = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(img1, 0, 0, null);//background
                    g.drawImage(img2, 800, 100, null);//title
                    g.drawImage(img3, xLabel, yLabel, null);//label play game
                    g.drawImage(img4, xLabel, yLabel + yLShift, null);//label rules
                    g.drawImage(img5, 22, 68, null);//chalkboard border
                }
            };
            p.setBounds(0,0,FRAMEWIDTH,FRAMEHEIGHT);
        } catch (IOException ex)
        {
            System.err.println(ex.getMessage());
        }
        return p;
    }

    /**Start Game Button
	*Method puts a button that starts a game of checkers
	*(Req 4.1.1)
	*/
    public final JButton playButton(int x, int y, int w, int h)
    {
        JButton button = null;
        BufferedImage img = null;
        try {
            img = ImageIO.read(getClass().getClassLoader().getResource("menuButton.png"));
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

                        stop = true;
                        GameboardWindow gameWindow = new GameboardWindow();
                        frame.dispose();
                }
            });

        return button;
    }

    /**Rules
    *Button that opens a rules file for the user(s)
    *(Req 4.2.6)
    */
    public final JButton rulesButton(int x, int y, int w, int h)
    {
        JButton button = null;
        BufferedImage img = null;
        try {
            img = ImageIO.read(getClass().getClassLoader().getResource("menuButton.png"));
            ImageIcon icon = new ImageIcon(img);
            button = new JButton(icon);

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        button.setBounds(x, y, w, h);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        //When rules button is clicked, rules window is opened
        button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                   rules.viewFrame();

                }
            });

        return button;

    }
}

