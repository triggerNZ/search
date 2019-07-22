package triggernz.search

import japgolly.univeq.UnivEq

object Util {
  // O(n * m). Should not be used if v1 is large. But in the case of merging the results of
  // index lookups, we expect them to be small.
  def mergeSmallVectors[A : UnivEq](v1: Vector[A], v2: Vector[A]) =
    v1 ++ v2.filterNot(v1.contains(_))
}
