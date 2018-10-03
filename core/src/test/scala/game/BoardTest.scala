package game

import org.scalatest.FunSuite

abstract class BoardTest extends FunSuite {

  def getBoard(provider:DataProvider[Int]=defaultProvider):Board[Int]

  test("Board 4x4 should be initialized") {
    val board = getBoard()
    val gameBoard = board.twoDimension()
    assert(gameBoard.length == board.width)

    for (i <- 0 until board.width) {
      for (j <- 0 until board.height) {
        val index = i * board.width + j
        assert(data(i)(j) == gameBoard(i)(j))
      }
    }
  }

  test("Should not move a cell in case it's not on the same row and column as a zero-cell") {
    val board:Board[Int] = getBoard()
    val desiredPos = Pos(0, 0)

    assert(!board.move(desiredPos))
  }

  test("Should not move in case it's a zero element selected") {
    val board:Board[Int] = getBoard()
    val desiredPos = Pos(1,3)

    assert(!board.move(desiredPos))
  }

  test("Should move one cell from left to right on a third row") {
    val board:Board[Int] = getBoard()
    val startPost = Pos(2,0)

    assert(board.move(startPost))
    assert(board.twoDimension()(2)(0) == 0)
  }

  test("Should move two cells from right to left on third row") {
    val board:Board[Int] = getBoard()
    val startPos = Pos(2,3)

    assert(board.move(startPos))
    assert(board.twoDimension()(2)(3) == 0)
  }

  test("Should move one cell from bottom to top in third column") {
    val board:Board[Int] = getBoard()
    val start = Pos(3,1)

    assert(board.move(start))
    assert(board.twoDimension()(3)(1) == 0)
  }

  test("Should move two cells from top to bottom") {
    val board:Board[Int] = getBoard()
    val start = Pos(0,1)

    assert(board.move(start))
    assert(board.twoDimension()(0)(1) == 0)
  }

  test("Should be ordered with ordered cells") {
    val board:Board[Int] = getBoard(ordered)
    assert(board.ordered())
  }

  test("Should not be ordered with unordered cells") {
    val board:Board[Int] = getBoard()
    assert(!board.ordered())

    board.printBoard()
  }

  private val data = Seq(
    Seq(1, 2, 3, 4),
    Seq(5, 6, 7, 8),
    Seq(9, 0, 10, 11),
    Seq(12, 13, 14, 15),
  )

  def defaultProvider = new TestDataProvider(data)

  def ordered = new TestDataProvider(Seq(
    Seq(1,  2,  3,  4),
    Seq(5,  6,  7,  8),
    Seq(9,  10, 11, 12),
    Seq(13, 14, 15, 0),
  ))
}

