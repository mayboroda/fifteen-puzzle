package game

/**
  * Game board that holds numbers for further puzzle game
  */
class Board(val width:Int, val height:Int) {

  val length = width * height
  val values:Seq[Int] = generateValues()
  val cells:Array[Cell] = init()
  var zeroPos:Pos = Pos(width-1, height-1)

  /**
    * Initialize game board with random numbers
    */
  def init(): Array[Cell] = {
    val cells = new Array[Cell](length)
    for((index) <- 0 until length) {
      cells(index) = Cell(getNumber(index), position(index))
    }
    cells
  }

  def getNumber(index:Int) : Int = values(index)

  def position(index:Int) : Pos = {
    val row = index / width
    Pos(row, index - row*width)
  }

  def generateValues():Seq[Int] = {
    for (i <- 1 to length) yield if (i == 16) 0 else i
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

  private def move(list:Seq[Pos]) = {
    for (pos <- list.reverse) {
      val zero = cells(cellIndex(zeroPos))
      cells(cellIndex(zeroPos)) = cells(cellIndex(pos))
      cells(cellIndex(pos)) = zero
      zeroPos = pos
    }
  }

  private def cellIndex(pos:Pos):Int = pos.row * width + pos.col

  private def moveCandidates(start: Pos, end: Pos): Seq[Pos] = {
    if (start.col == end.col) {
      for (i <- start.row until end.row) yield Pos(i, start.col)
    } else {
      for (i <-start.col until end.col) yield Pos(start.row, i)
    }
  }

  case class Cell(value:Int, pos: Pos)
}

case class Pos(row:Int, col:Int) {
  def intersect(pos:Pos): Boolean = (row == pos.row) || (col == pos.col)
}
