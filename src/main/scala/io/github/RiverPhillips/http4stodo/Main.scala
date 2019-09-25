package io.github.RiverPhillips.http4stodo

import cats.effect.{Blocker, ExitCode, IO, IOApp, Resource}
import cats.implicits._
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import io.github.RiverPhillips.http4stodo.repository.TodoRepository
import io.github.RiverPhillips.http4stodo.service.TodoService
import org.http4s.server.blaze.BlazeServerBuilder


object Main extends IOApp {
  val transactor: Resource[IO, HikariTransactor[IO]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](32)
      be <- Blocker[IO]
      xa <- HikariTransactor.newHikariTransactor[IO](
        "org.postgresql.Driver",
        "jdbc:postgresql://localhost:5432/postgres",
        "postgres",
        "",
        ce,
        be
      )
    } yield xa

  override def run(args: List[String]): IO[ExitCode] =
    transactor.use { xa =>

      val todoRepository = new TodoRepository(xa)

      BlazeServerBuilder[IO]
        .bindHttp(8080, "localhost")
        .withHttpApp(new TodoService(todoRepository).service)
        .serve
        .compile
        .drain
        .as(ExitCode.Success)
    }

}