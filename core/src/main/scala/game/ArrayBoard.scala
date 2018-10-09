package game

private class ArrayBoard[T](val width:Int, val height:Int)(dataProvider: DataProvider[T]) extends Board[T] {

  private val length:Int = width * height
  val cells:Array[Cell] = init(length)
  private var zeroPos:Pos = findZeroPos()

  /**
    * Returns `true` in case puzzle was ordered as a reference sequence from data provider
    * @return `true` in case ordered puzzle otherwise `false`
    */
  def ordered():Boolean = cells.map(_.value).toSeq == dataProvider.reference()

  private def data(index:Int) : T = dataProvider.values(index)

  private def position(index:Int) : Pos = {
    val row = index / width
    Pos(row, index - row*height)
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
  def twoDimension() : Seq[Seq[T]] = {
    for (rowIndex <- 0 until width)
      yield
        for(colIndex <- 0 until height)
          yield cells(cellIndex(Pos(rowIndex, colIndex))).value
  }
  /**
    * Initialize game board with data and positions
    */
  private def init(length: Int): Array[Cell] = {
    val cells = new Array[Cell](length)
    for((index) <- 0 until length) {
      cells(index) = Cell(data(index), position(index))
    }
    cells
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

object ClassicArrayFifteenBoard {
  def apply: Board[Int] = new ArrayBoard[Int](4,4)(new FifteenDataProvider())
}