package game

/**
  * Abstract data provider for a puzzle game
  * It returns a sequence of elements that must be sorted based on the size of the board
  * DataProvider must have one zero element. Zero element must be specified in order for board to compare.
  *
  * @tparam T a type of elements to sort
  */
trait DataProvider[T] {

  def value(index:Int) : T

  def zero() : T
}

class RandomNumberDataProvider(val length:Int) extends DataProvider[Int] {

  val values = Seq(2,3,4,6,7,8,9,1,5,11,10,12,14,13,15,0)

  override def value(index:Int) : Int = values(index)

  override def zero(): Int = 0
}
