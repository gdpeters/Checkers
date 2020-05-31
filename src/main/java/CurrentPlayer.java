
package checkers;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class CurrentPlayer extends JComponent {
    
    private BufferedImage plyr1Img, plyr2Img, highlight;
    private boolean player1;
    
    public CurrentPlayer(boolean current)
    {
        player1 = current;
    }
    
    public void setCurrent(boolean current)
    {
        player1 = current;
    }

    public void paintComponent(Graphics g)
    {
        int x = 17;     
        int y = 25;    
        int shiftX = 10;
        int shiftY = 100; //shift player piece image

        try {
            highlight = ImageIO.read(getClass().getClassLoader().getResource("highlight.png"));
            plyr1Img = ImageIO.read(getClass().getClassLoader().getResource("blackPiece.png"));
            plyr2Img = ImageIO.read(getClass().getClassLoader().getResource("redPiece.png"));
            g.drawImage(plyr1Img, x + shiftX, y + shiftX, this);
            g.drawImage(plyr2Img, x + shiftX, y + shiftY, this);
            
            if (player1)
            {
                g.drawImage(highlight, x + 3, y + 3, this);
            }
            else
            {
                g.drawImage(highlight, x + 3, y + shiftY - 7, this);
            }

        }
        catch (IOException ex)
        {
            System.err.println(ex.getMessage());
        }
    }//END paintComponent()
    
}//END CurrentPlayer class
