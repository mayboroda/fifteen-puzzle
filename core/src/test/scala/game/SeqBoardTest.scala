package game

class SeqBoardTest extends BoardTest {
  override def getBoard(provider:DataProvider[Int]=defaultProvider): Board[Int] = new SeqBoard(4, 4)(provider)
}