package game

import org.scalatest.FunSuite

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class ClassicFifteenGameTest extends FunSuite {

  test("Should count time of playing game after it's creation") {
    val game = new ClassicFifteenGame(getBoard)

    val future = game.execute(GetDuration)
    val event = Await.result(future, 1 millis)

    assert(event.asInstanceOf[GameLasts].millis > 0)
  }

  test("Should execute move command") {
    val game = new ClassicFifteenGame(getBoard)

    val future = game.execute(MoveCell(1,1))

    val event = Await.result(future, 2 millis)

    assert(event == CellMoved(1,1))
  }

  test("Should not move a cell in case of a wrong position") {
    val game = new ClassicFifteenGame(getBoard)

    val future = game.execute(MoveCell(0,0))

    val event = Await.result(future, 2 millis)

    assert(event == CellNotMoved)
  }

  test("Should get game status") {
    val game = new ClassicFifteenGame(getBoard)

    Thread.sleep(1)
    val future = game.execute(GetStatus)

    Await.result(future, 1 millis) match {
      case GameStatus(duration, moves) => assert(moves == 0); assert(duration > 0)
      case _ => fail("Wrong event happened")
    }
  }

  test("Should count only moves of the game") {
    val game = new ClassicFifteenGame(getBoard)

    val futures = Seq(
      game.execute(MoveCell(1,1)),
      game.execute(MoveCell(3,3)),
      game.execute(GetStatus),
      game.execute(GetBoard),
      game.execute(GetDuration)
    )
    val moves = Future.sequence(futures)
    val events = Await.result(moves, 1 millis)

    Await.result(game.execute(MoveCell(0,1)), 1 millis)

    val status = Await.result(game.execute(GetStatus), 1 millis)
    assert(status.asInstanceOf[GameStatus].moveAmount == 2)
  }

  test("Should complete game in two moves") {
    val game = new ClassicFifteenGame(getBoard)

    val futures = Seq(
      game.execute(MoveCell(3,1)),
      game.execute(MoveCell(3,2)),
      game.execute(MoveCell(3,3))
    )

    Await.result(Future.sequence(futures), 1 millis)

    val status = Await.result(game.execute(GetStatus), 1 millis)
    assert(status.asInstanceOf[GameCompleted].status != null)
  }

  private def getBoard = {
    new ArrayBoard[Int](4, 4)(disordered)
  }

  val disordered = new TestDataProvider(Seq(
    Seq(1,  2,  3,  4),
    Seq(5,  6,  7,  8),
    Seq(9,  0,  11, 12),
    Seq(13, 10, 14, 15)
  ))
}
