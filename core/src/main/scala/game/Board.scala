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
    * Initialize game board with random numbers
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
    if (pos.intersect(zeroPos)) {
      val posList = moveCandidates(pos, zeroPos)
      move(posList)
      true
    } else {
      false
    }
  }

  private def move(list:Seq[Pos]):Unit = {
    for (pos <- list.reverse) {
      val zero = cells(cellIndex(zeroPos))
      cells(cellIndex(zeroPos)) = cells(cellIndex(pos))
      cells(cellIndex(pos)) = zero
      zeroPos = pos
    }
  }

  private def cellIndex(pos:Pos):Int = pos.row * width + pos.col

  // Refactor this if-hell
  private def moveCandidates(start: Pos, end: Pos): Seq[Pos] = {
    if (start.col == end.col) {
      if (start.row > end.row) { // from bottom to top
        for (i <- start.row until end.row by -1) yield Pos(i, start.col)
      } else { // from top to bottom
        for (i <- start.row until end.row) yield Pos(i, start.col)
      }
    } else {
      if (start.col > end.col) { // from right to left
        for (i <-start.col until end.col by -1) yield Pos(start.row, i)
      } else { // from left to right
        for (i <-start.col until end.col) yield Pos(start.row, i)
      }
    }
  }

  // We assume that provider must return us a list with ZERO element
  private def findZeroPos():Pos = cells.filter(_.value == dataProvider.zero()).map(_.pos).head

  case class Cell(value:T, pos: Pos)
}

case class Pos(row:Int, col:Int) {
  def intersect(pos:Pos): Boolean = (row == pos.row) || (col == pos.col)
}
