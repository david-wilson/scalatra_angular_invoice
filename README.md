# Scalatra/AngularJS Invoice Creator #
A single page WYSIWYG invoice editing app using AngularJS. Uses a simple Scalatra/Akka backed to  asyncronously handle the generation of downloadable PDFs using wkhtmltopdf. This is a toy/learning application, and is not designed with security in mind.

##Dependencies and Initial Configuration##
Requires wkhtmltopdf installed. By default, this will be installed at /usr/bin/wkhmtltopdf, but this can be changed by modifying the WKHTML_PATH costant in PdfActor.scala. 

/home/[user]/pdf is the default location for the generated PDFs, but this can be changed via the PDF_PATH constant in PdfActor.scala.


## Build & Run ##

```sh
$ cd invoicemaker
$ ./sbt
> container:start
> browse
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.
