package game

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Success

object Main {
  var game:Game[Int] = null
  var board:Seq[Seq[Int]] = null
  var duration: Long = 0
  var moveAmount: Int = 0
  var state:State = NotPlaying

  def main(args:Array[String]): Unit = {
    var command: String = "1"
    val Input = "(^[0-3]) ([0-3]$)".r

    do {
      state match {
        case NotPlaying => menu()
        case Playing => render()
        case GameOver => gameOver()
      }

      command = scala.io.StdIn.readLine() match {
        case "1" if state == NotPlaying => start(); "1"
        case "2" if state == NotPlaying => infoMenu(); "2"
        case "0" if state == NotPlaying => "0"
        case "q" if state == Playing => quitGame(); "q"
        case Input(x, y) if state == Playing => move(x.toInt,y.toInt); "i"
        case _ => "_"
      }

    } while (command != "0")
  }

  def menu() = {
    println("1. Play")
    println("2. Info")
    println("0. Quit")
  }

  def infoMenu() = {
    println("Developed as a test task by Dmitriy Mayboroda. Enjoy.")
  }

  def start() = {
    game = new ClassicFifteenGame(ClassicArrayFifteenBoard.apply)
    state = Playing
  }

  def move(x:Int, y:Int) = {
    val future = game.execute(MoveCell(x, y))
      .flatMap {
        case CellMoved(_,_) => game.execute(GetStatus)
        case CellNotMoved => game.execute(GetStatus)
      }

    Await.result(future, 100 millis) match {
      case GameStatus(d,a) => duration = d; moveAmount = a;
      case GameCompleted(s) => duration = s.duration; moveAmount = s.moveAmount; state = GameOver
    }

  }

  def quitGame() = state = NotPlaying

  def gameOver() = println(s"Congratulation you ordered puzzle with $moveAmount moves in $duration ms")


  private def getBoardUpdate = {
    val future = game.execute(GetBoard)
    future.onComplete { case Success(v) => board = v.asInstanceOf[BoardState[Int]].board }
    Await.result(future, 1 millis)
  }

  def render(): Unit = {
    getBoardUpdate
    println(" * * * * * * * * ")
    for(row <- board) {
      for (col <- row) print(if(col > 9) s" $col " else s"  $col "  )
      println()
    }
    println(" * * * * * * * * ")
    println(s"Moves: $moveAmount ::: In game $duration ms")
    println("Press `q` to quit the game and return to menu")
  }
}

sealed abstract class State
case object NotPlaying extends State
case object Playing extends State
case object GameOver extends State