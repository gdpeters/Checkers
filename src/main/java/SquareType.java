
package checkers;

/** Squares can be one of 11 types but their type can change during a game
 * Empty dark square, empty light square
 * Player 1 piece, player 2 piece
 * Player 1 selected piece, player 2 selected piece, empty selected square
 * Player 1 king piece, player 2 king piece
 * Player 1 selected king piece, player 2 selected king piece
 */
public enum SquareType {
    
    DARK, LIGHT, PLYR1, PLYR2, SLCTD1, SLCTD2, SLCTD, KING1, KING2, SLCTDK1, SLCTDK2
}
