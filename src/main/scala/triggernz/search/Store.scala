package triggernz.search

import cats.{Id, Monad}
import cats.syntax.flatMap._
import cats.syntax.functor._
import japgolly.univeq.UnivEq

import scala.collection.immutable.HashMap
import scala.collection.mutable.{HashMap => MutHashMap}


abstract class Store[F[_], Query, T : UnivEq] {
  type DataLoc

  def lookupOne(q: Query): F[Vector[DataLoc]]
  def lookupMany(qs: Vector[Query]): F[Vector[DataLoc]]

  def retrieveOne(loc: DataLoc): F[Option[T]]
  def retrieveMany(locs: Vector[DataLoc]): F[Vector[T]]

  def lookupAndRetrieve(q: Query)(implicit M: Monad[F]) =
    for {
      lookupResult <- lookupOne(q)
      retrieveResult <- retrieveMany(lookupResult)
    } yield retrieveResult.distinct  // Distinct here is safe as the number of results is expected to be small

  def lookupAndRetrieveMany(qs: Vector[Query])(implicit M: Monad[F]) =
    for {
      lookupResults <- lookupMany(qs)
      retrieveResult <- retrieveMany(lookupResults)
    } yield retrieveResult.distinct // Distinct here is safe as the number of results is expected to be small
}


class VectorStore[T: UnivEq, Q: UnivEq] private (vec: Vector[T], index: HashMap[Q, Vector[Int]]) extends Store[Id, Q, T] {
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
  def apply[T: UnivEq, Q: UnivEq](v: Vector[T], queries: (T => Vector[Q])*): VectorStore[T, Q] =
    build(v, queries.toVector)

  def build[T: UnivEq, Q: UnivEq](v: Vector[T], queries: Vector[T => Vector[Q]]) = new VectorStore[T, Q](
    v,
    buildIndex(v, queries)
  )

  private def buildIndex[T, Q](v: Vector[T], queries: Vector[T => Vector[Q]]): HashMap[Q, Vector[Int]] = {
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