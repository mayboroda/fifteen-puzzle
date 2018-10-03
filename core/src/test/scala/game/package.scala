package object game {

  /**
    * Prints a board cells for debug purposes.
    */
  implicit class BoardDebug(board: Board[Int]) {
    def printBoard() = {
      println(" * * * * * * * * ")
      val seq = board.twoDimension()
      for(row <- seq) {
        for (col <- row) printCell(col)
        println()
      }
      println(" * * * * * * * * ")

      def printCell(num:Int):Unit = print(if(num > 9) s" $num " else s"  $num ")
    }
  }

  /**
    * Easy to setup a board with two-dimensional array of integers. It's more visible.
    * Used for test purpose only.
    *
    * @param data Test two dimensional array that emulate a real play-board
    */
  class TestDataProvider(data:Seq[Seq[Int]]) extends FifteenDataProvider {

    override val values:Seq[Int] = {
      for {
        (row, rowIndex) <- data.zipWithIndex
        (col, colIndex) <- row.zipWithIndex
      } yield data(rowIndex)(colIndex)
    }

    override def value(index: Int): Int = values(index)

    override def zero(): Int = 0
  }
}
