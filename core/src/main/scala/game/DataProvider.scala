package game
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
    * Returns one element from shuffled collection by it's index
    *
    * @param index index of a shuffled element
    * @return element of T
    */
  def value(index:Int) : T

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

  val values:Seq[Int] = scala.util.Random.shuffle(reference)

  override def value(index:Int) : Int = values(index)

  override def zero(): Int = 0
}
