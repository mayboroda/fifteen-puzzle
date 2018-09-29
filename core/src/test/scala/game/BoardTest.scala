package game

import org.scalatest.FunSuite

class BoardTest extends FunSuite {

  test("Board 4x4 should be initialized") {
    val board = new Board(4, 4)
    assert(board.cells.length == 16)

    for (i <- 0 until board.width) {
      for (j <- 0 until board.height) {
        val index = i * board.width + j
        assert(board.cells(index).pos.row == i)
        assert(board.cells(index).pos.col == j)
        assert(board.cells(index).value != null)
      }
    }
  }

  test("Should not move a cell in case it's not on the same row and column as a zero-cell") {
    val board = new Board(4,4)
    val desiredPos = Pos(0, 0)
    assert(board.zeroPos != desiredPos)

    assert(!board.move(desiredPos))
  }

  test("Should move three cells horizontally on a forth row") {
    val board = new Board(4,4)
    val desiredPos = Pos(3,0)

    assert(board.move(desiredPos))
    assert(board.cells(12).value == 0)

    board.printBoard()
  }
}

