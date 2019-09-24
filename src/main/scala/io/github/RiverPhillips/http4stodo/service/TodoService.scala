package io.github.RiverPhillips.http4stodo.service

import cats.effect.IO
import io.circe.generic.auto._
import io.circe.syntax._
import io.github.RiverPhillips.http4stodo.models._
import io.github.RiverPhillips.http4stodo.repository.TodoRepository
import org.http4s.{HttpRoutes, HttpService}
import org.http4s.circe._
import org.http4s.dsl._
import org.http4s.implicits._


class TodoService(todoRepository: TodoRepository) extends Http4sDsl[IO] {
  val service  = HttpRoutes.of[IO]{
    case GET -> Root / "todos" =>
      Ok(todoRepository.getTodos.asJson)
    case GET -> Root / "todos" / LongVar(id) =>
      todoRepository.getTodoById(id) match {
        case Left(_) => NotFound()
        case Right(todo) =>Ok(todo.asJson)
      }
  }.orNotFound
}
