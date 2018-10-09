package game

import javafx.application.{Application, Platform}
import javafx.event.ActionEvent
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.{Alert, Button, ButtonType, MenuBar}
import javafx.scene.layout.{Background, GridPane, VBox}
import javafx.stage.Stage

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class Main extends Application {
  var game:Game[Int] = _
  var grid:GridPane = _

  override def start(primaryStage: Stage): Unit = {
    primaryStage.setTitle("Fifteen Puzzle")

    val root:VBox = FXMLLoader.load(getClass.getResource("/grid-table.fxml"))
    val scene = new Scene(root, 300, 250)

    grid = scene.lookup("#mainGrid").asInstanceOf[GridPane]

    val menu = scene.lookup("#menu").asInstanceOf[MenuBar]
    menu.getMenus.get(0).getItems.get(0).setOnAction(e=>startGame(grid))
    menu.getMenus.get(0).getItems.get(1).setOnAction(e=>primaryStage.close())

    primaryStage.setScene(scene)
    primaryStage.show()

  }

  def startGame(grid: GridPane):Unit = {
    val game = new ClassicFifteenGame(ClassicArrayFifteenBoard.apply)
    game.execute(GetBoard).onComplete {
      case Success(e) => drawGrid(game, e.asInstanceOf[BoardView[Int]].board)
      case Failure(_) => doNothing()
    }
  }

  private def drawGrid(game:Game[Int], board:Seq[Seq[Int]]):Unit = {
    Platform.runLater(() => {
      if (grid.getChildren.size() > 0) grid.getChildren.remove(0, 16)

      for ((row, i) <- board.zipWithIndex) {
        for ((col, j) <- row.zipWithIndex) {
          val zeroElem = board(i)(j) == 0
          val button = new Button(s"${if (zeroElem) "" else board(i)(j)}")
          if (zeroElem) button.setBackground(Background.EMPTY)
          button.setOnAction((event: ActionEvent) => {
            move(game, i, j)
          })
          button.setMaxSize(Double.MaxValue, Double.MaxValue)
          grid.add(button, j, i)
        }
      }
    })
  }

  private def move(game:Game[Int], x:Int, y:Int): Unit = {
    val moveFuture = game.execute(MoveCell(x, y))
    moveFuture.map { result:Seq[GameEvent] =>
      if (result.map(cellMoved).reduce(_||_) ) {
        true
      } else {
        false
      }
    }.andThen {case _ =>
      game.execute(GetStatus).onComplete {
        case Success(e) => e match {
          case e@GameState(Completed, _, _,board) => drawGrid(game, board.asInstanceOf[Seq[Seq[Int]]]); gameOver(e.asInstanceOf[GameState[Int]])
          case GameState(_, _,moves,board) => updateMoves(moves);drawGrid(game, board.asInstanceOf[Seq[Seq[Int]]])
        }
      }
    }
  }

  private def cellMoved(event:GameEvent): Boolean = event match {
    case CellMoved(_,_) => true
    case _ => false
  }

  private def gameOver(state:GameState[Int]): Unit = {
    Platform.runLater(() => {
      val alert = new Alert(AlertType.INFORMATION, s"Game Is Over. You did ${state.moveAmount} moves in ${state.duration}ms")
      alert.showAndWait
        .filter((resp) => resp == ButtonType.OK)
        .ifPresent(resp => println("Game Over"))
    })
  }

  private def updateMoves(moves:Int): Unit = {
    println(s"Moves updated: $moves")
  }

  private def doNothing(): Unit = { /* no action */ }
}

object Main  {

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[Main], args: _*)
  }
}
