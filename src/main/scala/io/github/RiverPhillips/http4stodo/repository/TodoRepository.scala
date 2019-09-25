package io.github.RiverPhillips.http4stodo.repository

import cats.effect.IO
import doobie.implicits._
import doobie.util.transactor.Transactor
import io.github.RiverPhillips.http4stodo.models.{Todo, TodoNotFoundError}

class TodoRepository(transactor: Transactor[IO]) {
  var todos: Seq[Todo] = Seq(
    Todo(Some(1), "test")
  )

  def getTodos: fs2.Stream[IO, Todo] =
    sql"SELECT id, description FROM todo".query[Todo].stream.transact(transactor)

  def getTodoById(id: Long): IO[Either[TodoNotFoundError.type, Todo]] = {
    sql"SELECT id, description FROM todo WHERE id = $id".query[Todo].option.transact(transactor).map{
      case Some(todo) => Right(todo)
      case _ => Left(TodoNotFoundError)
    }
  }

  def createTodo(todo: Todo): IO[Todo] = {
    sql"INSERT INTO todo (description) VALUES (${todo.description})".update.withUniqueGeneratedKeys[Long]("id").transact(transactor).map{id =>
      todo.copy(id = Some(id))
    }
  }

  def updateTodo(id: Long, todo: Todo): IO[Either[TodoNotFoundError.type, Todo]] = {
    sql"UPDATE todo SET description = ${todo.description} WHERE  id = $id".update.run.transact(transactor).map{affectedRows =>
      if(affectedRows == 1){
        Right(todo.copy(id = Some(id)))
      }else{
        Left(TodoNotFoundError)
      }
    }
  }

  def deleteTodo(id: Long): IO[Either[TodoNotFoundError.type, Unit]] = {
    sql"DELETE FROM todo WHERE id = $id".update.run.transact(transactor).map {affectedRows =>
      if(affectedRows  == 1)
        Right(())
      else
        Left(TodoNotFoundError)
    }
  }
}
