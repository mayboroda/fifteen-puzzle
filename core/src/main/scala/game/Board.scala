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

private class BoardImp[T](val width:Int, val height:Int)(dataProvider: DataProvider[T]) extends Board[T] {

  private val length:Int = width * height
  private var cells:Seq[T] = init(length)
  private var zeroIndex = findZero()

  override def ordered():Boolean = cells == dataProvider.reference()

  override def move(pos:Pos):Boolean = {

    /**
      * Returns an step size of an offset in case move is legal.
      * Move is legal in case it's on position is on the same row or on the same column as a zero element
      * In case move is legal there are 4 step variations:
      *
      * - -1 (Right) in case position is on the left side from zero element and we need to move it from left to right
      * - 1 (Left) in case position is on the right from zero element and we need to move it from right to left
      * - -4 (Top) position is in the bottom as to zero and we need to move it from bottom to top
      * - 4 (Bottom) position is on the top as to zero and we need to move it from top to bottom
      * - 0 (None) in this case there is not orthogonal intersection between position and zero element and we don't need to move it
      *
      * @param startIndex index of a start position
      * @return Integer representation of offset size
      */
    def stepOffset(startIndex:Int): Int = {
      if(startIndex/width - zeroIndex/width == 0)
        if(startIndex%width - zeroIndex%width < 0)  1 else -1
      else if (startIndex%width - zeroIndex%width == 0)
        if(startIndex/width - zeroIndex/width < 0) width else -width
      else
        0
    }
    
    def swapTuples(elems:Seq[T]) = for((el,index) <-elems.zipWithIndex)
      yield (el, elems(if(index+1>elems.length-1) 0 else index+1))

    def swapFun(t:(T,T)):PartialFunction[T,T] = { case d:T if d==t._1 => t._2 }
    def swapDefault(t:Seq[T]):PartialFunction[T,T] = { case d:T if !t.contains(d) => d }

    /* move method */
    val startIndex = cellIndex(pos)
    val step = stepOffset(startIndex)
    if (step !=0 ) {
      val elements = for (index <- startIndex to zeroIndex by step) yield cells(index)
      val tuples = swapTuples(elements.reverse)
      val swapper = (tuples.map(swapFun) ++ Seq(swapDefault(tuples.map(_._1)))).reduce(_ orElse _)
      cells = cells.map(swapper)
      return true
    }
    false
  }

  override def twoDimension() : Seq[Seq[T]] = {
    for (rowIndex <- 0 until width)
      yield
        for(colIndex <- 0 until height)
          yield cells(cellIndex(Pos(rowIndex, colIndex)))
  }

  /**
    * Initialize game board with data and positions
    */
  private def init(length: Int): Seq[T] = {
    for((index) <- 0 until length)
      yield data(index)
  }

  private def data(index:Int) : T = dataProvider.value(index)

  private def cellIndex(pos:Pos):Int = pos.row * width + pos.col

  // We assume that provider must return us a list with ZERO element
  private def findZero():Int = cells.zipWithIndex.filter(_._1 == dataProvider.zero()).map(_._2).head
}

object ClassicFifteenBoard {
  def apply: Board[Int] = new BoardImp[Int](4,4)(new FifteenDataProvider())
}

/**
  * Position of a cell on the board that is defined as row and column numbers.
  *
  * @param row row index of a position
  * @param col column index of a position
  */
case class Pos(row:Int, col:Int)
