package game

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

/**
  * Represents a game match that will be created as soon as a user ask to play.
  * This object should be responsible for many things like: make move, check status, count moves, count game duration time and many more.
  * Game also track it's state, so it should not be possible to move after the game is completed.
  *
  * In order to make it simple our main interface will have one method that will execute different commands in order to mutate the game.
  * And a second method to execute queries in order to get different game views for UI purpose
  *
  * As a result of execution both commands and queries we'll return an eventual response.
  *
  * @tparam T a game must be parametrized same as a Board object
  */
trait Game[T] {
  /**
    * Executes all available commands in order to mutate the game
    *
    * @param command  command that needs to be executed
    * @param executor command executor
    * @return Future of Seq[GameEvent] that happened after successful command execution
    */
  def execute(command: GameCommand)(implicit executor: ExecutionContext): Future[Seq[GameEvent]]

  /**
    * Executes queries over the game in order to get an actual state of the game
    * Query can't mutate game state, but only read its current position
    *
    * @param query query that needs to be executed
    * @param executor query executor
    * @return Future of [[GameView]]
    */
  def execute(query: Query)(implicit executor: ExecutionContext): Future[GameView]
}

class ClassicFifteenGame(private val board: Board[Int]) extends Game[Int] {

  private val startTime: Long = System.currentTimeMillis()
  private var moves: Seq[CellMoved] = Seq.empty
  private var state: GameStatus = Started

  override def execute(command: GameCommand)(implicit executor: ExecutionContext): Future[Seq[GameEvent]] = {
    (command match {
      case MoveCell(x, y) if state == Started => move(x, y)
      case Quit if state == Started => quit()
      case _ => Future.successful(Seq(WrongCommandTriggered))
    }).andThen {case Success(events) => effect(events)}
  }

  override def execute(query: Query)(implicit executor: ExecutionContext): Future[GameView] = {
    query match {
      case GetBoard => getBoard
      case GetStatus => status
    }
  }

  private def move(x: Int, y: Int)(implicit executor: ExecutionContext): Future[Seq[GameEvent]] = {
    Future(board.move(Pos(x, y)))
      .map { if (_) Seq(CellMoved(x, y)) else Seq(CellNotMoved) }
      .map { events => if (board.ordered()) events ++ Seq(GameCompleted) else events ++ Seq(GameNotCompleted) }
  }

  private def quit()(implicit executor: ExecutionContext): Future[Seq[GameEvent]] = {
    Future.successful(Seq(GameAborted))
  }

  private def status(implicit executor: ExecutionContext): Future[GameView] = {
    Future(state)
      .zipWith(duration)((_, _))
      .zipWith(getBoard)((_,_))
      .map(r => GameState(r._1._1, r._1._2, moves.length, r._2.board))
  }

  private def getBoard(implicit executor: ExecutionContext) = {
    Future {
      BoardView(board.twoDimension())
    }
  }

  private def duration(implicit executor: ExecutionContext) = {
    Future {
      System.currentTimeMillis() - startTime
    }
  }

  private def effect(events:Seq[GameEvent]):Unit = events.foreach(effectOne)
  /**
    * Method that will be called after event has happened in the game in order to effect on game status
    *
    * @param event happened event
    */
  private def effectOne(event: GameEvent): Unit = event match {
    case e@CellMoved(_, _) => recalculateMovesEffect(e)
    case GameCompleted => completeGame()
    case GameAborted => failGame()
    case _ => doNothingEffect()
  }

  /**
    * Effect that changes sequence on all proper moves in the game
    * @param event CellMoved event
    */
  private def recalculateMovesEffect(event: CellMoved): Unit = {
    moves = moves ++ Seq(event)
  }

  /**
    * Effect in case game completed to change internal game state into [[Completed]] state
    * In case game in [[Completed]] state you no longer can make moves
    */
  private def completeGame():Unit = {
    state = Completed
  }

  private def failGame():Unit = {
    state = Failed
  }

  private def doNothingEffect(): Unit = { /* stub method for effects that are not yet defined */}
}

/**
  * Game state representation. There are several states:
  * - Started - when game is created
  * - Completed - when game is completed
  * - Failed - when game was failed for any reason. For example user quit game
  */
sealed abstract class GameStatus
case object Started extends GameStatus
case object Completed extends GameStatus
case object Failed extends GameStatus

/**
  * Parent class for all game commands that can exists in a game.
  * See implementations below
  */
sealed abstract class GameCommand

case class MoveCell(x: Int, y: Int) extends GameCommand
case object Quit extends GameCommand

/**
  * Parent class for all queries that can be requested in a game.
  * See implementations below
  */
sealed abstract class Query

case object GetBoard extends Query
case object GetStatus extends Query



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
case object GameCompleted extends GameEvent
case object GameAborted extends GameEvent
case object GameNotCompleted extends GameEvent
case object WrongCommandTriggered extends GameEvent

sealed abstract class GameView
case class BoardView[T](board: Seq[Seq[T]]) extends GameView
case class GameState[T](status: GameStatus, duration: Long, moveAmount: Int, board: Seq[Seq[T]]) extends GameView

