
package checkers;

/** Gameboard square types include:
 * empty (DARK, LIGHT)
 * player (PLYR1, PLYR2),
 * selected player (SLCTD1, SLCTD2)
 * selected empty (SLCTD)
 * king (KING1, KING2)
 * selected king (SLCTDK1, SLCTDK2)
 */
public enum SquareType {
    
    DARK, LIGHT, PLYR1, PLYR2, SLCTD1, SLCTD2, SLCTD, KING1, KING2, SLCTDK1, SLCTDK2
}
