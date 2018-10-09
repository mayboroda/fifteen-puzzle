package game

import scala.util.Random

/**
  * Abstract data provider for a puzzle game
  * It returns shuffled elements that must be sorted in order to reach the reference sequence
  * DataProvider contains reference sequence in order to identify what should be the final result
  * It also returns one element from shuffled collection by it's sequence number
  * DataProvider must have one zero element. Zero element must be specified in order for board to compare.
  *
  * @tparam T a type of elements to sort
  */
trait DataProvider[T] {

  /**
    * Returns a randomized values of a reference sequence
    * @return randomized reference sequence
    */
  def values : Seq[T]

  /**
    * Returns a zero element. The one that can be swapped with it's neighbors.
    *
    * @return element of T
    */
  def zero() : T

  /**
    * Reference sequence. The one that the game can check and identify whether the puzzle is complete or not.
    *
    * @return sequence of referenced elements of T
    */
  def reference() : Seq[T]
}

/**
  * FifteenDataProvider used for classic fifteen-puzzle game.
  * It has a constant reference sequence and a shuffled one.
  * Zero element is equals to `0`
  */
class FifteenDataProvider extends DataProvider[Int] {
  val reference = Seq(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,0)

  val values:Seq[Int] = randomize(reference)

  override def zero(): Int = 0

  private def randomize(seq:Seq[Int]):Seq[Int] = {
    val ints = Random.shuffle(seq)
    if (checkSum(ints, 0, 1) % 2 != 0) {
      randomize(seq)
    }
    else ints
  }

  /**
    * According to wikipedia https://ru.wikipedia.org/wiki/Игра_в_15#Математическое_описание
    * there are 16! combinations and half of them are unsolvable.
    * In order to check that randomized sequence is solvable we need to apply sum function.
    * In case a final sum is even puzzle is solvable otherwise it's not
    *
    * @param seq randomized sequence
    * @param i first index
    * @param j second index
    * @return sum of result
    */
  private def checkSum(seq:Seq[Int], i:Int, j:Int): Int = {
    def zeroElem(seq:Seq[Int], index:Int): Int = {
      if(seq(index) == 0) index/4 else 0
    }
    if (i >= seq.size) return 0
    if (j >= seq.size) return checkSum(seq, i+1, i+2) + zeroElem(seq, i)
    if (seq(i) > seq(j)) checkSum(seq, i, j+1) + 1 + zeroElem(seq, i)
    else checkSum(seq, i, j + 1) + zeroElem(seq, i)
  }
}
