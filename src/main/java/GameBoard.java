
package checkers;

/** 
 * The GameBoard class defines all aspects of the piece movement
 * and graphics.
 */
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class GameBoard extends JComponent {

    private static final int SQ_WIDTH = 85;//width of each square
    private Square[][] boardMatrix;//represents gameboard
    public BufferedImage plyr1Img, plyr2Img, plyr1KImg, plyr2KImg, darkSqImg, selected;// = "selected.jpg";*/   
    private Square selectedSq;
    public boolean playercheck = true; // This boolean goes to check whether the first player or second player are moving, TRUE = P1 / FALSE = P2.
    private boolean isMultJump, submitMultJumps;
    private int clicks = 0;
    private Stack<ArrayList<Square>> undoStack;
    private Square eliminated, tempMultJump;
    public int plyr1NumPieces, plyr2NumPieces;
    private ArrayList<Square> multJumpList;
    
    public GameBoard()
    {
        //define variables
        isMultJump = false;
        multJumpList = new ArrayList();
        plyr1NumPieces = 0;
        plyr2NumPieces = 0;
        undoStack = new Stack<>();
        
        //Create the game matrix
        this.makeSqMatrix();
        
        //Import all graphics
        try
        { 
            darkSqImg = ImageIO.read(getClass().getClassLoader().getResource("chalkSquare.png"));//dark_wood.jpg
            plyr1Img = ImageIO.read(getClass().getClassLoader().getResource("blackPiece.png"));//darkPiece3.png
            plyr2Img = ImageIO.read(getClass().getClassLoader().getResource("redPiece.png"));//lightPiece8.png
            plyr1KImg = ImageIO.read(getClass().getClassLoader().getResource("blackKingPiece.png"));
            plyr2KImg = ImageIO.read(getClass().getClassLoader().getResource("redKingPiece.png"));
            selected = ImageIO.read(getClass().getClassLoader().getResource("selectedChalk.png"));

        }
        catch (IOException ex) 
        {
            System.err.println(ex.getMessage());
        }
          
    }//END GameBoard constructor
    
    public void makeSqMatrix()
    {
        boardMatrix = new Square[8][8];//64 total squares
        boolean isLight = true;//flip SquareType each square
        int currentX = 0;//X coordinate of top-left corner of square
        int currentY = 0;//Y coordinate of top-left corner of square
        
        //Set SquareType for each square
        for (int r = 0; r < 8; r++)//rows = y values
        {
            currentY = r * SQ_WIDTH;
            
            for (int c = 0; c < 8; c++)//columns = x values
            {
                currentX = c * SQ_WIDTH;
                Square sq = new Square();
                if (isLight)
                {
                    sq.setType(SquareType.LIGHT);
                }
                else
                {
                    //If first 3 rows, assign Player 1 pieces
                    if (r >= 0 && r <= 2)
                    {
                        sq.setType(SquareType.PLYR1);                        
                    }
                    //If last 3 rows, assign Player 2 pieces
                    else if (r >= 5 && r <= 7)
                    {
                        sq.setType(SquareType.PLYR2);
                    }
                    //Else set SquareType to dark empty square
                    else
                    {
                        sq.setType(SquareType.DARK);
                    }
                }       
                //Set x,y coordinates
                sq.setXY(currentX, currentY);
                sq.setIndex(c,r);
                
                //add square to matrix
                boardMatrix[c][r] = sq;
                
                //flip light/dark for next square in row
                isLight = !isLight;
            }
            //flip light/dark to start next row
            isLight = !isLight;
        }
    }//END makeSqMatrix()
    
    //returns the square object given the x,y coordinates
    public Square getSquare(int x, int y)
    {
        int col = x / SQ_WIDTH;    
        int row = y / SQ_WIDTH;    
        
        return boardMatrix[col][row];
    }//END getSquare()
 
    //Updates a square in the matrix typically when the SquareType has changed
    public void updateMatrix(Square sq)
    {
        boardMatrix[sq.getCol()][sq.getRow()] = sq;
    }
    
    //Moves a player's piece
    public void movePiece(Square moveFrom, Square moveTo)
    {
        moveFrom.deselect();
        moveTo.setType(moveFrom.getType());	//The moveTo SquareType is set to the current moveFrom SquareType
        moveFrom.setType(SquareType.DARK);	//The moveFrom square is now empty
        
        //Update the matrix
        updateMatrix(moveFrom);
        updateMatrix(moveTo);
        
    }//End movePiece()
    
    
    /** Req 2.1.0 / 2.1.2 / 2.3.0
     * A move is valid if the player's piece moves "forward" diagonally by 1 space
     * A move is valid if the player's king moves "forward" or backward diagonally
     * @param moveFrom selected player's square
     * @param moveTo square to move to
     * */
    public boolean isValid(Square moveFrom, Square moveTo)
    {
        if (!moveTo.getType().equals(SquareType.DARK))	//if moveTo square is NOT empty, return false
            return false;
        
        int rowDiff = moveTo.getRow() - moveFrom.getRow();	//the number of rows between squares
        int colDiff = moveTo.getCol() - moveFrom.getCol();	//the number of columns between squares
        
        //if Player1, piece can only move down gameboard
        switch(moveFrom.getType())
        {
            case SLCTD1:
                return (rowDiff == 1 && Math.abs(colDiff) == 1);
            case SLCTDK1:
                return (Math.abs(rowDiff) == 1 && Math.abs(colDiff) == 1);
            case SLCTDK2:
                return (Math.abs(rowDiff) == 1 && Math.abs(colDiff) == 1);
            case SLCTD2:
                return (rowDiff == -1 && Math.abs(colDiff) == 1);
            default:
                return false;
        }
    }//END isValid()

    /** Req 3.1.1 / 3.1.2 / 3.1.3 / 3.2.0
     * A jump is valid if the player's piece moves "forward" diagonally by 2 spaces and jumps over an opponent's piece
     * A jump is valid if the player's king moves "forward" or backward diagonally, jumping over an opponent's piece
     * @param moveFrom selected player's square
     * @param moveTo square to move to
     * @return true if valid jump
     */
    public boolean isValidJump(Square moveFrom, Square moveTo)
    {
        if (!moveTo.getType().equals(SquareType.DARK))	//if moveTo square is NOT empty, return false
        {
            return false;
        }
        int jumpcheck = moveTo.getCol() - moveFrom.getCol();
        if(Math.abs(moveTo.getRow() - moveFrom.getRow()) != 2)
        {
            return false;
        }
        switch(jumpcheck)
        {
            case 2:
                switch(moveFrom.getType())
                {
                    case SLCTD1:
                        if(moveTo.getRow() == moveFrom.getRow() + 2
                   && (getSquare((moveFrom.getCol() + 1)*SQ_WIDTH, (moveFrom.getRow() + 1)*SQ_WIDTH).getType().equals(SquareType.PLYR2)
                   || getSquare((moveFrom.getCol() + 1)*SQ_WIDTH, (moveFrom.getRow() + 1)*SQ_WIDTH).getType().equals(SquareType.KING2)))
                        {
                            eliminated = getSquare((moveFrom.getCol() + 1)*SQ_WIDTH, (moveFrom.getRow() + 1)*SQ_WIDTH);
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    case SLCTDK1:
                        if(moveTo.getRow() == moveFrom.getRow() + 2
                   && (getSquare((moveFrom.getCol() + 1)*SQ_WIDTH, (moveFrom.getRow() + 1)*SQ_WIDTH).getType().equals(SquareType.PLYR2)
                   || getSquare((moveFrom.getCol() + 1)*SQ_WIDTH, (moveFrom.getRow() + 1)*SQ_WIDTH).getType().equals(SquareType.KING2)))
                        {
                            eliminated = getSquare((moveFrom.getCol() + 1)*SQ_WIDTH, (moveFrom.getRow() + 1)*SQ_WIDTH);
                            return true;
                        }
                        else if(moveTo.getRow() == moveFrom.getRow() - 2
                    && (getSquare((moveFrom.getCol() + 1)*SQ_WIDTH, (moveFrom.getRow() - 1)*SQ_WIDTH).getType().equals(SquareType.PLYR2)
                    || getSquare((moveFrom.getCol() + 1)*SQ_WIDTH, (moveFrom.getRow() - 1)*SQ_WIDTH).getType().equals(SquareType.KING2)))
                        {
                            eliminated = getSquare((moveFrom.getCol() + 1)*SQ_WIDTH, (moveFrom.getRow() - 1)*SQ_WIDTH);
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    case SLCTD2:
                        if(moveTo.getRow() == moveFrom.getRow() - 2
                   && (getSquare((moveFrom.getCol() + 1)*SQ_WIDTH, (moveFrom.getRow() - 1)*SQ_WIDTH).getType().equals(SquareType.PLYR1)
                   || getSquare((moveFrom.getCol() + 1)*SQ_WIDTH, (moveFrom.getRow() - 1)*SQ_WIDTH).getType().equals(SquareType.KING1)))
                        {
                            eliminated = getSquare((moveFrom.getCol() + 1)*SQ_WIDTH, (moveFrom.getRow() - 1)*SQ_WIDTH);
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    case SLCTDK2:
                        if(moveTo.getRow() == moveFrom.getRow() - 2
                   && (getSquare((moveFrom.getCol() + 1)*SQ_WIDTH, (moveFrom.getRow() - 1)*SQ_WIDTH).getType().equals(SquareType.PLYR1)
                   || getSquare((moveFrom.getCol() + 1)*SQ_WIDTH, (moveFrom.getRow() - 1)*SQ_WIDTH).getType().equals(SquareType.KING1)))
                        {
                            eliminated = getSquare((moveFrom.getCol() + 1)*SQ_WIDTH, (moveFrom.getRow() - 1)*SQ_WIDTH);
                            return true;
                        }
                        else if(moveTo.getRow() == moveFrom.getRow() + 2
                   && (getSquare((moveFrom.getCol() + 1)*SQ_WIDTH, (moveFrom.getRow() + 1)*SQ_WIDTH).getType().equals(SquareType.PLYR1)
                   || getSquare((moveFrom.getCol() + 1)*SQ_WIDTH, (moveFrom.getRow() + 1)*SQ_WIDTH).getType().equals(SquareType.KING1)))
                        {
                            eliminated = getSquare((moveFrom.getCol() + 1)*SQ_WIDTH, (moveFrom.getRow() + 1)*SQ_WIDTH);
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    default:
                        return false;
                }
            case -2:
                switch(moveFrom.getType())
                {
                    case SLCTD1:
                        if (moveTo.getRow() == moveFrom.getRow() + 2
                        && (getSquare((moveFrom.getCol() - 1)*SQ_WIDTH, (moveFrom.getRow() + 1)*SQ_WIDTH).getType().equals(SquareType.PLYR2)
                        || getSquare((moveFrom.getCol() - 1)*SQ_WIDTH, (moveFrom.getRow() + 1)*SQ_WIDTH).getType().equals(SquareType.KING2)))
                        {
                            eliminated = getSquare((moveFrom.getCol() - 1)*SQ_WIDTH, (moveFrom.getRow() + 1)*SQ_WIDTH);
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    case SLCTDK1:
                        if (moveTo.getRow() == moveFrom.getRow() + 2
                        && (getSquare((moveFrom.getCol() - 1)*SQ_WIDTH, (moveFrom.getRow() + 1)*SQ_WIDTH).getType().equals(SquareType.PLYR2)
                        || getSquare((moveFrom.getCol() - 1)*SQ_WIDTH, (moveFrom.getRow() + 1)*SQ_WIDTH).getType().equals(SquareType.KING2)))
                        {
                            eliminated = getSquare((moveFrom.getCol() - 1)*SQ_WIDTH, (moveFrom.getRow() + 1)*SQ_WIDTH);
                            return true;
                        }
                        else if (moveTo.getRow() == moveFrom.getRow() - 2
                        && (getSquare((moveFrom.getCol() - 1)*SQ_WIDTH, (moveFrom.getRow() - 1)*SQ_WIDTH).getType().equals(SquareType.PLYR2)
                        || getSquare((moveFrom.getCol() - 1)*SQ_WIDTH, (moveFrom.getRow() - 1)*SQ_WIDTH).getType().equals(SquareType.KING2)))
                        {
                            eliminated = getSquare((moveFrom.getCol() - 1)*SQ_WIDTH, (moveFrom.getRow() - 1)*SQ_WIDTH);
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    case SLCTD2:
                        if (moveTo.getRow() == moveFrom.getRow() - 2
                        && (getSquare((moveFrom.getCol() - 1)*SQ_WIDTH, (moveFrom.getRow() - 1)*SQ_WIDTH).getType().equals(SquareType.PLYR1)
                        || getSquare((moveFrom.getCol() - 1)*SQ_WIDTH, (moveFrom.getRow() - 1)*SQ_WIDTH).getType().equals(SquareType.KING1)))
                        {
                            eliminated = getSquare((moveFrom.getCol() - 1)*SQ_WIDTH, (moveFrom.getRow() - 1)*SQ_WIDTH);
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    case SLCTDK2:
                        if (moveTo.getRow() == moveFrom.getRow() - 2
                        && (getSquare((moveFrom.getCol() - 1)*SQ_WIDTH, (moveFrom.getRow() - 1)*SQ_WIDTH).getType().equals(SquareType.PLYR1)
                        || getSquare((moveFrom.getCol() - 1)*SQ_WIDTH, (moveFrom.getRow() - 1)*SQ_WIDTH).getType().equals(SquareType.KING1)))
                        {
                            eliminated = getSquare((moveFrom.getCol() - 1)*SQ_WIDTH, (moveFrom.getRow() - 1)*SQ_WIDTH);
                            return true;
                        }
                        else if (moveTo.getRow() == moveFrom.getRow() + 2
                        && (getSquare((moveFrom.getCol() - 1)*SQ_WIDTH, (moveFrom.getRow() + 1)*SQ_WIDTH).getType().equals(SquareType.PLYR1)
                        || getSquare((moveFrom.getCol() - 1)*SQ_WIDTH, (moveFrom.getRow() + 1)*SQ_WIDTH).getType().equals(SquareType.KING1)))
                        {
                            eliminated = getSquare((moveFrom.getCol() - 1)*SQ_WIDTH, (moveFrom.getRow() + 1)*SQ_WIDTH);
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    default:
                        return false;
                }
            default:
                return false;
        }
    }

    /** Req 3.0.0
     * Multiple jump button
     */
    public void flipMultJump()
    {
        isMultJump = !isMultJump;
        if (!isMultJump)
        {
            submitMultJumps = true;
            playerClicked(-1,-1);
        }
    }
    
    public void cancelMultJump()
    {
        isMultJump = false;
        submitMultJumps = false;
        for (int i = 0; i < multJumpList.size(); i++)
        {
            Square temp = multJumpList.get(i);
            temp.deselect();
            updateMatrix(temp);
        }
        clicks = 0;
        selectedSq = null;
    }
    
    public void skipTurn()
    {
       
        if (selectedSq != null)
        {
            selectedSq.deselect();
            updateMatrix(selectedSq);
        }
        for (Square s : multJumpList)
        {
            s.deselect();
            updateMatrix(s);
        }
        
        multJumpList.clear();
        isMultJump = false;
        submitMultJumps = false;
        playercheck = !playercheck;
        clicks = 0;
    }
    
    /** Req 2.4.0 / 2.4.1
     * Determines whether the location of the mouse click is valid for the
     * given player. Square is highlighted if selected and valid.
     * @param x x-coordinate
     * @param y y-coordinate
     * @return true if valid mouse click
     */
    public boolean playerClicked(int x, int y) 
    {
        Square clickedSq = this.getSquare(x, y);
        clicks++;

        if (clicks == 1)
        {
            if ((playercheck && (clickedSq.getType().equals(SquareType.PLYR1) 
                    || clickedSq.getType().equals(SquareType.KING1)))
                    || (!playercheck && (clickedSq.getType().equals(SquareType.PLYR2) 
                    || clickedSq.getType().equals(SquareType.KING2))))
            {
                multJumpList.clear();
                clickedSq.select();
                selectedSq = clickedSq;
                this.updateMatrix(selectedSq);

                tempMultJump = selectedSq;
                multJumpList.add(tempMultJump);
                return true;
            }
            else
            {
                clicks = 0;
                selectedSq = null;
                return false;
            }
        }

        //else if it's the second click
        else if (clicks == 2)
        {
            if (clickedSq.isSelected() && clickedSq.equals(multJumpList.get(multJumpList.size() - 1)))    //if a selected square, then deselect it
            {
                if (isMultJump)
                {
                    int i = multJumpList.indexOf(clickedSq);
                    Square remove = multJumpList.remove(i);
                    remove.deselect();	//deselect the square
                    this.updateMatrix(remove);	//update the square
                    clicks--;	//reset the number of clicks
                }
                else
                {
                    clickedSq.deselect();	//deselect the square
                    this.updateMatrix(clickedSq);	//update the square
                    clicks = 0;	//reset the number of clicks
                    selectedSq = null;	//reset the selectedSq variable
                }
                return true;
            }
            if (isMultJump)
            {

                if ((multJumpList.size() == 1) && (this.isValidJump(selectedSq, clickedSq)))
                {
                    Square tempTo = clickedSq;
                    multJumpList.add(tempTo);
                    clickedSq.select();
                    this.updateMatrix(clickedSq);
                    clicks--;
                    return true;
                    
                }
                else if (multJumpList.size() > 1)
                {
                    Square startJump = multJumpList.get(multJumpList.size() - 1);
                    startJump.setType(selectedSq.getType());
                    
                    if (this.isValidJump(startJump, clickedSq))
                    {
                        multJumpList.add(clickedSq);
                        clickedSq.select();
                        this.updateMatrix(clickedSq);
                        clicks--;
                        
                        startJump.setType(SquareType.SLCTD);
                        return true;

                    }
                    
                    startJump.setType(SquareType.SLCTD);
                    clicks--;
                    return false;
                }
                else 
                {
                    clicks--;
                    return false;
                }  
            }
            else if (submitMultJumps && (x == -1) && (y == -1))
            {
                if (multJumpList.size() < 1)
                {
                    clicks = 0;
                    selectedSq = null;
                    return false;
                }
                if (multJumpList.size() == 1)
                {
                    multJumpList.get(0).deselect();
                    updateMatrix(multJumpList.get(0));
                    multJumpList.clear();
                    clicks = 0;
                    selectedSq = null;
                    return true;
                }
                Square from = multJumpList.get(0);

                //for undo list
                ArrayList<Square> undoMove = new ArrayList<>();
                SquareType fromType = from.getType();
                from.deselect();
                Square s1 = new Square(from.getType(), from.getX(), from.getY(), from.getCol(), from.getRow());
                undoMove.add(s1);
                from.select();
                int numEliminated = 0;

                for (int i = 1; i < multJumpList.size(); i++)
                {
                    
                    Square jumpTo = boardMatrix[multJumpList.get(i).getCol()][multJumpList.get(i).getRow()];
                    jumpTo.deselect();

                    if (this.isValidJump(from, jumpTo))
                    {
                        
                        //for undo button
                        Square s2 = new Square(SquareType.DARK, jumpTo.getX(), jumpTo.getY(), jumpTo.getCol(), jumpTo.getRow());//dark square
                        Square s3 = new Square(eliminated.getType(), eliminated.getX(), eliminated.getY(), eliminated.getCol(), eliminated.getRow());//opponent's piece
                        undoMove.add(s2);
                        undoMove.add(s3);

                        this.movePiece(from, jumpTo);
                        
                        eliminated.eliminated();
                        updateMatrix(eliminated);
                        numEliminated++;

                        if(jumpTo.kingCheck())
                        {
                            jumpTo.king();
                        }
                    }
                    
                    if (i < multJumpList.size() - 1 )
                    {
                        from = jumpTo;
                        from.select();
                    }
                }
                
                undoStack.add(undoMove);
                
                if (fromType.equals(SquareType.SLCTD1) || fromType.equals(SquareType.SLCTDK1))
                        plyr2NumPieces += numEliminated;
                if (fromType.equals(SquareType.SLCTD2) || fromType.equals(SquareType.SLCTDK2))
                        plyr1NumPieces += numEliminated;
                                
                clicks = 0;
                selectedSq = null;
                playercheck = !playercheck;

                return true;
            }
            else if (this.isValid(selectedSq, clickedSq))  //if it's a valid move, then move the piece
            {
                selectedSq.deselect();	//deselect the first square
                
                //for undo button
                ArrayList<Square> move = new ArrayList<>();
                Square s1 = new Square(selectedSq.getType(), selectedSq.getX(), selectedSq.getY(), selectedSq.getCol(), selectedSq.getRow());
                Square s2 = new Square(clickedSq.getType(), clickedSq.getX(), clickedSq.getY(), clickedSq.getCol(), clickedSq.getRow());
                move.add(s1);
                move.add(s2);
                undoStack.add(move);
                
                selectedSq.select();
                this.movePiece(selectedSq, clickedSq);	//move the piece
                if(clickedSq.kingCheck())
                {
                    clickedSq.king();
                }
                clicks = 0;	//reset the number of clicks
                selectedSq = null;	//reset the selectedSq variable
                playercheck = !playercheck; //The playercheck is only reset here since this is when the move is  valid

                return true;
            }
            else if (this.isValidJump(selectedSq, clickedSq))
            {
                selectedSq.deselect();
                
                //for undo button
                ArrayList<Square> move = new ArrayList<>();
                Square s1 = new Square(selectedSq.getType(), selectedSq.getX(), selectedSq.getY(), selectedSq.getCol(), selectedSq.getRow());
                Square s2 = new Square(clickedSq.getType(), clickedSq.getX(), clickedSq.getY(), clickedSq.getCol(), clickedSq.getRow());
                Square s3 = new Square(eliminated.getType(), eliminated.getX(), eliminated.getY(), eliminated.getCol(), eliminated.getRow());
                move.add(s1);
                move.add(s2);
                move.add(s3);
                undoStack.add(move);

                if (selectedSq.getType().equals(SquareType.PLYR1) || selectedSq.getType().equals(SquareType.KING1))
                    plyr2NumPieces++;
                if (selectedSq.getType().equals(SquareType.PLYR2) || selectedSq.getType().equals(SquareType.KING2))
                    plyr1NumPieces++;
                
                selectedSq.select();
                this.movePiece(selectedSq, clickedSq);
                eliminated.eliminated();
                updateMatrix(eliminated);
                if(clickedSq.kingCheck())
                {
                    clickedSq.king();
                }
                clicks = 0;
                selectedSq = null;
                tempMultJump = null;
                playercheck = !playercheck;
                
                return true;
            }
            else
            {
                clicks--;	//decrement clicks since it wasn't a valid one
                return false;
            }
        }
        else    //to catch errors
        {
            clicks = 0;
            selectedSq = null;
            return false;
        }                 
    }

    //Displays graphics to the window. This method is called automatically
    public void paintComponent(Graphics g)
    {
        //Draw each square onto gameboard
        int shift = 10; //shift player piece images
        for (int r = 0; r < 8; r++)
        {
            for (int c = 0; c < 8; c++)
            {
                Square sq = boardMatrix[c][r];
                SquareType st = sq.getType();
                
                //Based on SquareType, display its corresponding image
                switch (st)
                {
                    case DARK:
                        g.drawImage(darkSqImg, sq.getX(), sq.getY(), this);
                        break;
                    case LIGHT:
                       // g.drawImage(lightSqImg, sq.getX(), sq.getY(), this);
                        break;
                    case PLYR1:
                        g.drawImage(darkSqImg, sq.getX(), sq.getY(), this);
                        g.drawImage(plyr1Img, sq.getX() + shift, sq.getY() + shift, this);
                        break;
                    case PLYR2:
                        g.drawImage(darkSqImg, sq.getX(), sq.getY(), this);
                        g.drawImage(plyr2Img, sq.getX() + shift, sq.getY() + shift, this);
                        break;
                    case KING1:
                        g.drawImage(darkSqImg, sq.getX(), sq.getY(), this);
                        g.drawImage(plyr1KImg, sq.getX() + shift, sq.getY() + shift, this);
                        break;
                    case KING2:
                        g.drawImage(darkSqImg, sq.getX(), sq.getY(), this);
                        g.drawImage(plyr2KImg, sq.getX() + shift, sq.getY() + shift, this);
                        break;
                    case SLCTD1:
                        g.drawImage(selected, sq.getX(), sq.getY(), this);
                        g.drawImage(plyr1Img, sq.getX() + shift, sq.getY() + shift, this);
                        break;
                    case SLCTD2:
                        g.drawImage(selected, sq.getX(), sq.getY(), this);
                        g.drawImage(plyr2Img, sq.getX() + shift, sq.getY() + shift, this);
                        break;
                    case SLCTDK1:
                        g.drawImage(selected, sq.getX(), sq.getY(), this);
                        g.drawImage(plyr1KImg, sq.getX() + shift, sq.getY() + shift, this);
                        break;
                    case SLCTDK2:
                        g.drawImage(selected, sq.getX(), sq.getY(), this);
                        g.drawImage(plyr2KImg, sq.getX() + shift, sq.getY() + shift, this);
                        break;
                    case SLCTD:
                        g.drawImage(selected, sq.getX(), sq.getY(), this);
                        break;
                    default : 
                        break;
                }
            }
        }
    }//END paintComponent()

    public boolean undo()
    {
        if (selectedSq != null)
        {
            selectedSq.deselect();
            updateMatrix(selectedSq);
        }
        
        if (!undoStack.empty())
        {   
            int numEliminated = 0;
            ArrayList<Square> remove = undoStack.pop();
            for (Square s : remove)
            {
                this.updateMatrix(s);
            }
            numEliminated = (remove.size() - 1) / 2;
           
            if (remove.get(0).getType().equals(SquareType.PLYR1) || remove.get(0).getType().equals(SquareType.KING1))
                    plyr2NumPieces -= numEliminated;
            if (remove.get(0).getType().equals(SquareType.PLYR2) || remove.get(0).getType().equals(SquareType.KING2))
                    plyr1NumPieces -= numEliminated;
   
            playercheck = !playercheck;
        }
                    
        clicks = 0;
        
        return true;
    }//END undo()
    
}//END class



