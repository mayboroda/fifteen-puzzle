package game

import scala.concurrent.{ExecutionContext, Future}

/**
  * Represents a game match that will be created as soon as a user ask to play.
  * This object should be responsible for many things like: make move, check status,
  * count moves, count game duration time and many more.
  * In order to make it simple our main interface will have one method that will execute different commands
  * As a result of execution we'll return an eventual response we an event that happened after command execution.
  * This is not an event-sourcing and CQRS design pattern, but much more simpler, for a sake of example.
  *
  * @tparam T a game must be parametrized same as a Board object
  */
trait Game[T] {

  /**
    * Main API method to execute all available commands
    *
    * @param command  command that needs to be executed
    * @param executor command executor
    * @return Future[GameEvent] that happened after successful command execution
    */
  def execute(command: GameCommand)(implicit executor: ExecutionContext): Future[GameEvent]
}

class ClassicFifteenGame(private val board: Board[Int]) extends Game[Int] {

  private val startTime: Long = System.currentTimeMillis()
  private var moves: Seq[CellMoved] = Seq.empty

  override def execute(event: GameCommand)(implicit executor: ExecutionContext): Future[GameEvent] = {
    val eventualEvent = event match {
      case MoveCell(x, y) => move(x, y)
      case GetDuration => duration
      case GetBoard => getBoard
      case GetStatus => status
      case _ => Future.successful(WrongCommandTriggered)
    }
    eventualEvent.onComplete(e => effect(e.get))
    eventualEvent
  }

  private def status(implicit executor: ExecutionContext): Future[GameEvent] = {
    val eventualGameStatus = duration.map { duration => GameStatus(duration.millis, moves.length) }
    Future(board.ordered())
      .zipWith(eventualGameStatus)((_, _))
      .map(r => if (r._1) GameCompleted(r._2) else r._2)
  }

  private def move(x: Int, y: Int)(implicit executor: ExecutionContext): Future[GameEvent] = {
    Future(board.move(Pos(x, y)))
      .map { result =>
        if (result) CellMoved(x, y) else CellNotMoved
      }
  }

  private def getBoard(implicit executor: ExecutionContext) = {
    Future {
      BoardState(board.twoDimension())
    }
  }

  private def duration(implicit executor: ExecutionContext) = {
    Future {
      GameLasts(System.currentTimeMillis() - startTime)
    }
  }

  /**
    * Method that will be called after event has happened in the game in order to effect on game status
    *
    * @param event happened event
    */
  private def effect(event: GameEvent): Unit = event match {
    case e@CellMoved(_, _) => recalculateMovesEffect(e)
    case _ => doNothingEffect()
  }

  /**
    * Effect that changes sequence on all proper moves in the game
    * @param event CellMoved event
    */
  private def recalculateMovesEffect(event: CellMoved): Unit = {
    moves = moves ++ Seq(event)
  }

  private def doNothingEffect(): Unit = { /* stub method for effects that are not yet defined */}
}

/**
  * Parent class for all game commands that can exists in a game.
  * See implementations below
  */
sealed abstract class GameCommand
/**
  * Game commands and queries
  * Currently we don't have queries explicitly, but we could have another abstract class with parent to GameCommand
  * Currently we could have a convention that all queries starts with `Get` or `Find`
  */
case class MoveCell(x: Int, y: Int) extends GameCommand

case object GetDuration extends GameCommand

case object GetBoard extends GameCommand

case object GetStatus extends GameCommand


/**
  * Parent class for all events that can happen during a game
  * Some of the events looks more like views and it might look weired, but this was done only for purpose of simplicity.
  */
sealed abstract class GameEvent
/**
  * Game events. These are object that should represent a happened action in the game.
  */
case class CellMoved(x: Int, y: Int) extends GameEvent

case object CellNotMoved extends GameEvent

case class GameLasts(millis: Long) extends GameEvent

case class BoardState[T](board: Seq[Seq[T]]) extends GameEvent

case class GameStatus(duration: Long, moveAmount: Int) extends GameEvent

case class GameCompleted(status: GameStatus) extends GameEvent

case object WrongCommandTriggered extends GameEvent