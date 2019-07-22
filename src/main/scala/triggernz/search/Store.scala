package triggernz.search

import cats.{FlatMap, Id}
import cats.syntax.flatMap._
import japgolly.univeq.UnivEq
import monocle.Lens

import scala.collection.immutable.HashMap
import scala.collection.mutable.{HashMap => MutHashMap}


trait Store[F[_], Query, T] {
  type DataLoc

  def lookupOne(q: Query): F[Vector[DataLoc]]
  def lookupMany(qs: Vector[Query]): F[Vector[DataLoc]]

  def retrieveOne(loc: DataLoc): F[Option[T]]
  def retrieveMany(locs: Vector[DataLoc]): F[Vector[T]]

  def lookupAndRetrieve(q: Query)(implicit M: FlatMap[F]) =
    for {
      lookupResult <- lookupOne(q)
      retrieveResult <- retrieveMany(lookupResult)
    } yield retrieveResult
}

class VectorStore[T, Q: UnivEq] private (vec: Vector[T], index: HashMap[Q, Vector[Int]]) extends Store[Id, Q, T] {
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

object Store {
  def fromVector[T, Q](v: Vector[T], queries: Vector[Lens[T, Q]]) = new VectorStore[T, Q](
    v,
    buildIndex(v, queries)
  )

  private def buildIndex[T, Q](v: Vector[T], queries: Vector[Lens[T, Q]]): HashMap[Q, Vector[Int]] = {
    // Two traversals. One to build the map, one to make it immutable. We could do it with one
    // by passing the mutable map around but that breaks referential transparency.
    var mutableMap = MutHashMap.empty[Q, Vector[Int]]

    v.iterator.zipWithIndex.foreach { case (t, idx) =>
      val newQs: Vector[Q] = queries.map(_.get(t))
      newQs.foreach { q =>
        val oldIndices = mutableMap.getOrElse(q, Vector.empty)
        mutableMap.put(q, oldIndices :+ idx)
      }
    }

    (HashMap.newBuilder[Q, Vector[Int]] ++= mutableMap).result()
  }

}