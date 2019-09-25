package io.github.RiverPhillips.http4stodo.repository

import cats.effect.IO
import doobie.implicits._
import doobie.util.transactor.Transactor
import io.github.RiverPhillips.http4stodo.models.{Todo, TodoNotFoundError}

class TodoRepository(transactor: Transactor[IO]) {
  var todos: Seq[Todo] = Seq(
    Todo(Some(1), "test")
  )

  def getTodos(): fs2.Stream[IO, Todo] =
    sql"SELECT id, description FROM todo".query[Todo].stream.transact(transactor)

  def getTodoById(id: Long): IO[Either[TodoNotFoundError.type, Todo]] = {
    sql"SELECT id, description FROM todo WHERE id = $id".query[Todo].option.transact(transactor).map{
      case Some(todo) => Right(todo)
      case _ => Left(TodoNotFoundError)
    }
  }
}
