package game

/**
  * Game board that operates with cells and moving logic of a puzzle game.
  * Also this class provide a method that can distinguish whether or not a puzzle is ordered or not.
  */
trait Board[T] {

  /**
    * Method responsible for moving cells on a board according to board rules
    * Cells can move only vertically or horizontally.
    * We can move one or more cells at the same time if they are located on (intersect with) same horizontal or
    * vertical coordinate as a zero cell.
    *
    * @param pos position of a cell that should be moved
    * @return `true` in case cell was moved otherwise `false`
    */
  def move(pos:Pos):Boolean

  /**
    * Returns `true` in case puzzle was ordered as a reference sequence from data provider
    * @return `true` in case ordered puzzle otherwise `false`
    */
  def ordered():Boolean

  /**
    * Returns a two-dimensional representation of a board.
    * Result of this method must be used in UI clients
    * @return two-dimensional array
    */
  def twoDimension() : Seq[Seq[T]]

  def width:Int
  def height:Int
}

/**
  * Position of a cell on the board that is defined as row and column numbers.
  *
  * @param row row index of a position
  * @param col column index of a position
  */
case class Pos(row:Int, col:Int) {
  def intersect(pos:Pos): Boolean = sameRow(pos) || sameColumn(pos)

  private def sameRow(pos: Pos) = row == pos.row

  private def sameColumn(pos: Pos) = col == pos.col

  def directionTo(end:Pos):MoveDirection = {
    if (sameColumn(end)) {
      if (isBottomTo(end)) BottomToTop(row, end.row) else TopToBottom(row, end.row)
    } else {
      if (isRightTo(end)) RightToLeft(col, end.col) else LeftToRight(col, end.col)
    }
  }

  private def isRightTo(pos: Pos) = col > pos.col

  private def isBottomTo(pos: Pos) = row > pos.row
}