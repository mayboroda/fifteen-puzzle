package game

/**
  * Game board that holds numbers for further puzzle game
  */
// TODO: Define method that returns a matrix representation of a board. Just for external use.
// TODO: Define method that indicates that puzzle is completed

class Board[T](val width:Int, val height:Int)(dataProvider: DataProvider[T]) {

  val length:Int = width * height
  val cells:Array[Cell] = init()
  var zeroPos:Pos = findZeroPos()

  /**
    * Initialize game board with data and positions
    */
  def init(): Array[Cell] = {
    val cells = new Array[Cell](length)
    for((index) <- 0 until length) {
      cells(index) = Cell(data(index), position(index))
    }
    cells
  }

  private def data(index:Int) : T = dataProvider.value(index)

  private def position(index:Int) : Pos = {
    val row = index / width
    Pos(row, index - row*width)
  }

  def move(pos:Pos):Boolean = {
    /**
      * Swap sequence of cells with zero positioned cell
      *
      * @param list list of positions to swap
      * @return true in case there was a swap otherwise false
      */
    def swapCells(list:Seq[Pos]):Boolean = {
      var swapped = false
      for (pos <- list.reverse) {
        val zero = cells(cellIndex(zeroPos))
        cells(cellIndex(zeroPos)) = cells(cellIndex(pos))
        cells(cellIndex(pos)) = zero
        zeroPos = pos
        swapped = true
      }
      swapped
    }

    def moveDirection(start:Pos, end:Pos): Seq[Pos] = {
      start.directionTo(end) match {
        case dir @ (BottomToTop(_,_) | TopToBottom(_,_)) => for (i <- dir.from until dir.to by dir.step) yield Pos(i, start.col)
        case dir @ (LeftToRight(_,_) | RightToLeft(_,_)) => for (i <- dir.from until dir.to by dir.step) yield Pos(start.row, i)
      }
    }

    if (pos.intersect(zeroPos)) {
      val posList = moveDirection(pos, zeroPos)
      swapCells(posList)
    } else {
      false
    }
  }

  private def cellIndex(pos:Pos):Int = pos.row * width + pos.col

  // We assume that provider must return us a list with ZERO element
  private def findZeroPos():Pos = cells.filter(_.value == dataProvider.zero()).map(_.pos).head

  case class Cell(value:T, pos: Pos)
}

/**
  * Represent moving directions of cells on a board based on the start and end positions of a move.
  * Direction consists of `from` and `to` positions that define an amount of cells to move.
  * A `step` field defines direction based on vertical or horizontal position.
  * In case of horizontal movements from right to left `step` must be `-1` otherwise `1`. Same for vertical movements.
  *
  * @param from starting index of movement
  * @param to end index of movement
  * @param step amount of steps to move
  */
sealed abstract class MoveDirection(val from:Int, val to:Int, val step:Int)
case class LeftToRight(override val from:Int, override val to:Int) extends MoveDirection(from, to, 1)
case class RightToLeft(override val from:Int, override val to:Int) extends MoveDirection(from, to, -1)
case class TopToBottom(override val from:Int, override val to:Int) extends MoveDirection(from, to, 1)
case class BottomToTop(override val from:Int, override val to:Int) extends MoveDirection(from, to, -1)

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
