import _root_.akka.actor.{Props, ActorSystem}
import com.davidwilson.invoicemaker._
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  val system = ActorSystem()
  val pdfActor = system.actorOf(Props[PdfActor])

  override def init(context: ServletContext) {
    context.mount(new InvoiceServlet(system, pdfActor), "/*")
  }

  override def destroy(context:ServletContext) {
    system.shutdown()
  }
}
