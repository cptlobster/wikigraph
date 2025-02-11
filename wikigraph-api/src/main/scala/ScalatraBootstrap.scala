import dev.cptlobster.wikigraph._
import org.scalatra._
import jakarta.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext): Unit = {
    context.mount(new WikigraphServlet, "/*")
  }
}
