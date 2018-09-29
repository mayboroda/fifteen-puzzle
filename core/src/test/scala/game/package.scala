package object game {

  implicit class BoardDebug[T](board: Board[T]) {
    def printBoard() = {
      var coll = 0
      println("* * * * *")
      for ((cell, index) <- board.cells.zipWithIndex) {
        if (coll >= board.width-1) {
          println (s"${cell.value}")
          coll = 0
        } else {
          print(s"${cell.value} ")
          coll += 1
        }
      }
      println("* * * * *")
    }
  }

  class TestDataProvider(data:Array[Array[Int]]) extends DataProvider[Int] {

    val values:Seq[Int] = {
      for {
        (row, rowIndex) <- data.zipWithIndex
        (col, colIndex) <- row.zipWithIndex
      } yield data(rowIndex)(colIndex)
    }

    override def value(index: Int): Int = values(index)

    override def zero(): Int = 0
  }
}
