 
package checkers;

public class Square {
    
    //x,y are coordinates of square on gameboard
    //col,row are indices of square in boardMatrix
    private int x, y, col, row;
    private SquareType type;
    
    public Square()
    {
        type = null;
 
    }
    
    public Square(SquareType st, int x, int y, int col, int row)
    {
        type = st;
        this.x = x;
        this.y = y;
        this.col = col;
        this.row = row;
    }
    
    public void setType(SquareType st)
    {
        type = st;
    }
    
    //Select a square by changing its SquareType accordingly
    public void select()
    {
        switch (type) 
        {
            case PLYR1:
                type = SquareType.SLCTD1;
                break;
            case KING1:
                type = SquareType.SLCTDK1;
                break;
            case PLYR2:
                type = SquareType.SLCTD2;
                break;
            case KING2:
                type = SquareType.SLCTDK2;
                break;
            case DARK:
                type = SquareType.SLCTD;
            default:
                break;
        }
    }//END select()
    
    //Deselect a square
    public void deselect()
    {        
        switch (type)
        {
            case SLCTD1:
                type = SquareType.PLYR1;
                break;
            case SLCTD2:
                type = SquareType.PLYR2;
                break;
            case SLCTDK1:
                type = SquareType.KING1;
                break;
            case SLCTDK2:
                type = SquareType.KING2;
                break;
            case SLCTD:
                type = SquareType.DARK;
            default:
                break;
        }
    }//END deselect()
    
    public void eliminated()
    {
        type = SquareType.DARK;
    }
    
    public void king()
    {
        switch(type)
        {
            case PLYR1:
                type = SquareType.KING1;
                break;
            case PLYR2:
                type = SquareType.KING2;
                break;
            default:
                break;
        }
    }
    
    public boolean kingCheck()
    {
        return (row == 0 || row == 7);
    }
    
    public boolean isKing()
    {
        return (type.equals(SquareType.SLCTDK1) || type.equals(SquareType.SLCTDK2));
    }
    
    //returns true if SquareType is Slctd1 or Slctd2
    public boolean isSelected()
    {
        return (type.equals(SquareType.SLCTD1) || type.equals(SquareType.SLCTD2) 
                || type.equals(SquareType.SLCTDK1) || type.equals(SquareType.SLCTDK2)
                || type.equals(SquareType.SLCTD));
    }
    
    public SquareType getType()
    {
        return type;
    }
    
    public void setXY(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setIndex(int col, int row)
    {
        this.col = col;
        this.row = row;
    }
    
    public int getCol()
    {
        return col;
    }
    
    public int getRow()
    {
        return row;
    }
    
    
}