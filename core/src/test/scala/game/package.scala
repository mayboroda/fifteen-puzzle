package object game {

  implicit class BoardDebug(board: Board) {
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

}
