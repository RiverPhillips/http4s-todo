package io.github.RiverPhillips.http4stodo

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import io.github.RiverPhillips.http4stodo.repository.TodoRepository
import io.github.RiverPhillips.http4stodo.service.TodoService
import org.http4s.server.blaze.BlazeServerBuilder

object Main extends IOApp {

  val todoRepository =  new TodoRepository()

  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(new TodoService(todoRepository).service)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}