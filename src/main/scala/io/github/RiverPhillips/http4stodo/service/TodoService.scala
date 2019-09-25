package io.github.RiverPhillips.http4stodo.service

import cats.effect.IO
import io.circe.generic.auto._
import io.circe.syntax._
import io.github.RiverPhillips.http4stodo.repository.TodoRepository
import org.http4s.{HttpRoutes, MediaType}
import org.http4s.circe._
import org.http4s.dsl._
import org.http4s.implicits._
import org.http4s.headers.`Content-Type`


class TodoService(todoRepository: TodoRepository) extends Http4sDsl[IO] {
  val service  = HttpRoutes.of[IO]{
    case GET -> Root / "todos" =>
      val todoStream = fs2.Stream("[") ++ todoRepository.getTodos.map(_.asJson.noSpaces).intersperse(",") ++ fs2.Stream("]")
      Ok(todoStream, `Content-Type`(new MediaType("application", "json")))

    case GET -> Root / "todos" / LongVar(id) =>
      for{
        getResult <- todoRepository.getTodoById(id)
        response <- getResult match {
          case Right(todo) => Ok(todo.asJson)
          case Left(_) => NotFound()
        }
      }yield response
  }.orNotFound
}
