package com.davidwilson.invoicemaker

import akka.actor.Actor
import scala.xml.Unparsed
import java.io.{PrintWriter, File}
import java.util.{GregorianCalendar, Date}
import sun.util.calendar.Gregorian
import sys.process.Process
import java.io.File
import javax.servlet.ServletContext
import org.slf4j.LoggerFactory

case class PdfOrder(html: String, assetPath: String, id: String)

class PdfActor extends Actor {
  val PDF_PATH = "/pdf/"
  val WKHTML_PATH = "/usr/bin/wkhtmltopdf"

  val pdfPath = System.getProperty("user.home")+"/pdf/"
  val logger =  LoggerFactory.getLogger(getClass)

  def receive = {
    case o: PdfOrder => sender ! genPdf(o)
  }

  def genPdf(order: PdfOrder): String = {
    logger.info("Generating PDF")
    logger.info(pdfPath)

    val bootstrapPath = order.assetPath+"/css/bootstrap.min.css"
    val mainCssPath = order.assetPath+"/css/main.css"
    val pdfCssPath = order.assetPath+"/css/pdf.css"
    println(order.assetPath)
    logger.info((bootstrapPath));
    val fullHtml =
      <html>
        <head>
            <link rel="stylesheet" type="text/css" href={bootstrapPath}/>
            <link rel="stylesheet" type="text/css" href={mainCssPath}/>
            <link rel="stylesheet" type="text/css" href={pdfCssPath}/>
        </head>
        <body>
          {new xml.Unparsed(order.html)}
        </body>
      </html>

    val tmpFile = File.createTempFile(order.id, ".html");
    val pw = new PrintWriter(tmpFile)
    pw.print(fullHtml)
    pw.close

    val outFile = new File(pdfPath+order.id+".pdf")

    val pb = Process(WKHTML_PATH + " --zoom .8  " + tmpFile.getCanonicalPath + " " + outFile.getAbsolutePath)
    println(outFile.getAbsolutePath)
    val returnCode = pb.!
    tmpFile.delete()
    return "invoice.pdf"
  }
}
