package game

import org.scalatest.FunSuite

class BoardTest extends FunSuite {

  test("Board 4x4 should be initialized") {
    val board = new BoardImp(4,4)(leftToRight)
    val gameBoard = board.twoDimension()
    assert(gameBoard.length == board.width)

    for (i <- 0 until board.width) {
      for (j <- 0 until board.height) {
        val index = i * board.width + j
        assert(leftToRightData(i)(j) == gameBoard(i)(j))
      }
    }
  }

  test("Should not move a cell in case it's not on the same row and column as a zero-cell") {
    val board:Board[Int] = new BoardImp(4,4)(leftToRight)
    val desiredPos = Pos(0, 0)

    assert(!board.move(desiredPos))
  }

  test("Should move three cells from left to right on a forth row") {
    val board:Board[Int] = new BoardImp(4,4)(leftToRight)
    val startPost = Pos(1,0)

    assert(board.move(startPost))
    assert(board.twoDimension()(1)(0) == 0)
  }

  test("Should move two cells from right to left on third row") {
    val board:Board[Int] = new BoardImp(4,4)(rightToLeft)
    val startPos = Pos(2,3)

    assert(board.move(startPos))
    assert(board.twoDimension()(2)(3) == 0)
  }

  test("Should move two cells from bottom to top in fourth column") {
    val board:Board[Int] = new BoardImp(4,4)(leftToRight)
    val start = Pos(3,3)

    assert(board.move(start))
    assert(board.twoDimension()(3)(3) == 0)
  }

  test("Should move two cells from top to bottom") {
    val board:Board[Int] = new BoardImp(4,4)(rightToLeft)
    val start = Pos(0,1)

    assert(board.move(start))
    assert(board.twoDimension()(0)(1) == 0)
  }

  test("Should be ordered with ordered cells") {
    val board:Board[Int] = new BoardImp(4,4)(ordered)
    assert(board.ordered())
  }

  test("Should not be ordered with unordered cells") {
    val board:Board[Int] = new BoardImp(4,4)(leftToRight)
    assert(!board.ordered())

    board.printBoard()
  }

  private val leftToRightData = Array(
    Array(1,  2,  3,  4),
    Array(5,  6,  7,  0),
    Array(9,  10, 11, 8),
    Array(12, 13, 14, 15),
  )

  def leftToRight = new TestDataProvider(leftToRightData)

  def rightToLeft = new TestDataProvider(Array(
    Array(1,2,3,4),
    Array(5,6,7,8),
    Array(9,0,10,11),
    Array(12,13,14,15),
  ))

  def ordered = new TestDataProvider(Array(
    Array(1,  2,  3,  4),
    Array(5,  6,  7,  8),
    Array(9,  10, 11, 12),
    Array(13, 14, 15, 0),
  ))
}

