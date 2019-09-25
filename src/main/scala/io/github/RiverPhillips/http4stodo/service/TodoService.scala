package io.github.RiverPhillips.http4stodo.service

import cats.effect.IO
import io.circe.generic.auto._
import io.circe.syntax._
import io.github.RiverPhillips.http4stodo.models.Todo
import io.github.RiverPhillips.http4stodo.repository.TodoRepository
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe._
import org.http4s.dsl._
import org.http4s.headers.`Content-Type`
import org.http4s.implicits._
import org.http4s.{HttpRoutes, MediaType}


class TodoService(todoRepository: TodoRepository) extends Http4sDsl[IO] {
  val service = HttpRoutes.of[IO] {
    case GET -> Root / "todos" =>
      val todoStream = fs2.Stream("[") ++ todoRepository.getTodos.map(_.asJson.noSpaces).intersperse(",") ++ fs2.Stream("]")
      Ok(todoStream, `Content-Type`(new MediaType("application", "json")))

    case GET -> Root / "todos" / LongVar(id) =>
      for {
        result <- todoRepository.getTodoById(id)
        response <- result match {
          case Right(todo) => Ok(todo.asJson)
          case Left(_) => NotFound()
        }
      } yield response

    case req@POST -> Root / "todos" =>
      for {
        todo <- req.as[Todo]
        result <- todoRepository.createTodo(todo)
        response <- Ok(result.asJson)
      } yield response

    case req@PUT -> Root / "todos" / LongVar(id) =>
      for {
        todo <- req.as[Todo]
        result <- todoRepository.updateTodo(id, todo)
        response <- result match {
          case Right(todo) => Ok(todo.asJson)
          case Left(_) => NotFound()
        }
      } yield response

    case DELETE -> Root / "todos" / LongVar(id) =>
      for {
        result <- todoRepository.deleteTodo(id)
        response <- result match {
          case Right(_) => NoContent()
          case Left(_) => NotFound()
        }
      } yield response

  }.orNotFound
}