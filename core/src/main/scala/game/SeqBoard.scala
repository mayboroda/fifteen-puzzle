package game

private class SeqBoard[T](val width:Int, val height:Int)(dataProvider: DataProvider[T]) extends Board[T] {

  private val length:Int = width * height
  private var cells:Seq[T] = init(length)
  private var zeroIndex = findZero()

  override def ordered():Boolean = cells == dataProvider.reference()

  /**
    * In order to move elements in SeqBoard we have to generate another board based on the current one knowing
    * positions of all offset that we have to do in the board
    *
    * =Algorithm=
    * 1. We have to define whether we have intersection with zero element
    * 2. We have to get an offset step in order to get elements that needs to be moved
    * 3. We generate tuples of elements that we want to swap during board regeneration
    * 4. Construct a PartialFunction for each tuple and one function for rest elements
    * 5. Combine all functions in one with `orElse` method
    * 6. Map existing cells with final function
    *
    * @param pos position of a cell that should be moved
    * @return `true` in case cell was moved otherwise `false`
    */
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
      if (startIndex == zeroIndex) return 0
      if(startIndex/width - zeroIndex/width == 0)
        if(startIndex%width - zeroIndex%width < 0)  -1 else 1
      else if (startIndex%width - zeroIndex%width == 0)
        if(startIndex/width - zeroIndex/width < 0) -width else width
      else
        0
    }

    def tuplesToSwap(elems:Seq[T]) = for((el,index) <-elems.zipWithIndex)
      yield (el, elems(if(index+1>elems.length-1) 0 else index+1))

    def swapTupleFunction(t:(T,T)):PartialFunction[T,T] = { case d:T if d==t._1 => t._2 }
    def swapDefaultFunction(t:Seq[T]):PartialFunction[T,T] = { case d:T if !t.contains(d) => d }

    /* move method */
    val startIndex = cellIndex(pos)
    val step = stepOffset(startIndex)
    if (step !=0 ) {
      val tuples = tuplesToSwap(for (index <- zeroIndex to startIndex by step) yield cells(index))
      val swapper = (tuples.map(swapTupleFunction) ++ Seq(swapDefaultFunction(tuples.map(_._1)))).reduce(_ orElse _)
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
  def apply: Board[Int] = new SeqBoard[Int](4,4)(new FifteenDataProvider())
}