package triggernz.search

import cats.{Id, Monad}
import cats.syntax.flatMap._
import cats.syntax.functor._
import japgolly.univeq.UnivEq

import scala.collection.immutable.HashMap
import scala.collection.mutable.{HashMap => MutHashMap}


abstract class Store[Query, T : UnivEq] {
  type DataLoc

  def lookupOne(q: Query): Vector[DataLoc]
  def lookupMany(qs: Vector[Query]): Vector[DataLoc]

  def retrieveOne(loc: DataLoc): Option[T]
  def retrieveMany(locs: Vector[DataLoc]): Vector[T]

  def lookupAndRetrieve(q: Query) =
    retrieveMany(lookupOne(q)).distinct  // Distinct here is safe as the number of results is expected to be small

  def lookupAndRetrieveMany(qs: Vector[Query]) =
    retrieveMany(lookupMany(qs)).distinct

}

object Store {
}

class VectorStore[T: UnivEq, Q: UnivEq] private (vec: Vector[T], index: HashMap[Q, Vector[Int]]) extends Store[Q, T] {
  type DataLoc = Int

  def lookupOne(q: Q) = index.getOrElse(q, Vector.empty)
  def lookupMany(qs: Vector[Q]) = qs.flatMap(lookupOne)

  def retrieveOne(loc: Int) =
    if (loc >= 0 && loc < vec.length)
      Some(vec(loc))
    else
      None

  def retrieveMany(locs: Vector[DataLoc]) =
    locs.flatMap(retrieveOne)
}

object VectorStore {
  // Convenience method from tests
  def apply[T: UnivEq, Q: UnivEq](v: Vector[T], queries: IndexGen[T, Q]*): VectorStore[T, Q] =
    build(v, queries.toVector)

  def build[T: UnivEq, Q: UnivEq](v: Vector[T], queries: Vector[IndexGen[T, Q]]) = new VectorStore[T, Q](
    v,
    buildIndex(v, queries)
  )

  private def buildIndex[T, Q](v: Vector[T], queries: Vector[IndexGen[T, Q]]): HashMap[Q, Vector[Int]] = {
    // Two traversals. One to build the map, one to make it immutable. We could do it with one
    // by passing the mutable map around but that breaks referential transparency.
    var mutableMap = MutHashMap.empty[Q, Vector[Int]]

    v.iterator.zipWithIndex.foreach { case (t, idx) =>
      val newQs: Vector[Q] = queries.flatMap(_(t))
      newQs.foreach { q =>
        val oldIndices = mutableMap.getOrElse(q, Vector.empty)
        val updatedIndices = if (oldIndices.contains(idx)) oldIndices else oldIndices :+ idx
        mutableMap.put(q, updatedIndices)
      }
    }

    (HashMap.newBuilder[Q, Vector[Int]] ++= mutableMap).result()
  }
}

sealed trait IndexGen[T, Q] {
  def apply(t: T): Vector[Q]
}
object IndexGen {
  def many[T, Q](getter: T => Vector[Q]): IndexGen[T, Q] =
    Direct[T, Q](getter)

  def apply[T, Q](getter: T => Q): IndexGen[T, Q] =
    Direct[T, Q](t => Vector(getter(t)))

  case class Direct[T, Q](getter: T => Vector[Q]) extends IndexGen[T, Q] {
    def apply(t: T) = getter(t)
  }
  case class Join[T, S, Q, PQ](parentGetter: T => Vector[PQ], childGetter: S => Vector[Q], childStore: Store[PQ, S])
    extends IndexGen[T, Q]{
    def apply(t: T) = {
      val parentQs: Vector[PQ] = parentGetter(t)
      val children = childStore.lookupAndRetrieveMany(parentQs)
      children.flatMap(childGetter)
    }

  }
}