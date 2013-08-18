package com.davidwilson.invoicemaker

import _root_.akka.actor.{ActorRef, ActorSystem}
import akka.pattern.AskSupport
import _root_.akka.util.Timeout
import scala.concurrent.duration._
import org.scalatra._
import scalate.ScalateSupport
import concurrent.{ExecutionContext, Future}
import java.io.File

class InvoiceServlet(system: ActorSystem, actor: ActorRef) extends InvoicemakerStack
  with AskSupport
  with FutureSupport {

  protected implicit def executor: ExecutionContext = system.dispatcher


  get("/") {
    contentType="text/html"

    jade("/invoice", "layout" -> "WEB-INF/layouts/app.jade", "title"-> "Invoice")
  }

  post("/invoice") {
    val session = request.getSession(true)
    val html = request.body
    val webappPath = servletContext.getRealPath("/");
    val order = PdfOrder(html, webappPath, session.id)

    new AsyncResult() {
      def is = ask(actor, order)(new Timeout(10 seconds))
    }
  }

  get("/invoice/:filename") {
    val session = request.getSession
    val expectedFilename = session.id + ".pdf"
    contentType = "application/octet-stream"
    response.setHeader("Content-Disposition", "attachment; filename=" + "invoice.pdf")
    val pdfPath = System.getProperty("user.home")+"/pdf/"
    new File(pdfPath+expectedFilename)
  }


}
