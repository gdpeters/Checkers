
package checkers;

/**
 * Trough consists of any player piece that was
 * removed from the gameboard.
 */
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class Trough extends JComponent {
    
    private BufferedImage piece, border;
    public int troughPieces;
    
    public Trough(BufferedImage img, int num)
    {
        piece = img;
        troughPieces = num;
    }
    
    public int getTroughPieces()
    {
        return troughPieces;
    }
    
    public void setTroughPieces(int num)
    {
        troughPieces = num;
    }
    
    public boolean isLoser()
    {
        return (troughPieces == 12);
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        try {
            border = ImageIO.read(getClass().getClassLoader().getResource("chalkBorderSmall.png"));
            g.drawImage(border, 0, 0, this);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        int shift = 70;//shift player piece image
        int x = 10;     
        int y = 15;     

        if (troughPieces > 0)
        {
            if (troughPieces < 6)
            {
                for (int i = 0; i < troughPieces; i++)
                {
                    g.drawImage(piece, x, y, this);
                    x += shift;
                }
            }
            else
            {
                for (int i = 0; i < 6; i++)
                {
                    g.drawImage(piece, x, y, this);
                    x += shift;
                }

                x = 10; //10    80  150 220 290 360
                y = 80; //80    80  80  80  80  80
                for (int i = 6; i < troughPieces; i++)
                {
                    g.drawImage(piece, x, y, this);
                    x += shift;
                }
            } 
        }
    }
}
