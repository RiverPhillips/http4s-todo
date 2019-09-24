package io.github.RiverPhillips.http4stodo.repository

import io.github.RiverPhillips.http4stodo.models.{Todo, TodoNotFoundError}

class TodoRepository {
  var todos: Seq[Todo] = Seq(
    Todo(Some(1), "test")
  )

  def getTodos(): Seq[Todo] = todos

  def getTodoById(id: Long): Either[TodoNotFoundError.type, Todo] = todos.find(x => x.id == Some(id)) match {
    case Some(todo) => Right(todo)
    case _ => Left(TodoNotFoundError)
  }

  def addTodo(todo: Todo): Seq[Todo] = {
    todos = todos :+ todo
    todos
  }
}
