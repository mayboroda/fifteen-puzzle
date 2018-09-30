package game

import org.scalatest.FunSuite

class FifteenDataProviderTest extends FunSuite {

  test("Should have a reference of 16 sorted elements that ends with 0") {
    val dataProvider = new FifteenDataProvider()

    assert(dataProvider.reference == Seq(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,0))
  }

  test("Should have `0` as zero element") {
    val dataProvider = new FifteenDataProvider()

    assert(dataProvider.zero() == 0)
  }
}
