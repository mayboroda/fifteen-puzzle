package game

class ArrayBoardTest extends BoardTest {
  override def getBoard(provider: DataProvider[Int]): Board[Int] = new ArrayBoard[Int](4,4)(provider)
}
