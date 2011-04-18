package bootstrap.liftweb


import _root_.net.liftweb.common._
import _root_.scala.xml.{Node, NodeSeq, Elem}
import _root_.net.liftweb.util._
import _root_.net.liftweb.util.Helpers._
import java.io.{Writer, InputStream}

import dispatch._

import net.liftweb._
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._
import mapper._

import code.model._

import net.liftweb.http._
import net.liftweb.http.rest._

object ApiProxy extends RestHelper {
    serve {
        case Req("api" :: "geocommits" :: Nil, _, GetRequest) => {
            var response: String = ""
            val http = new Http
            val req = :/("localhost", 3000) / "geocommits"
            http(req >- {response = _})
            new PlainTextResponse(response, List(), 200)
        }
    }
}

final case class GeocommitHtmlProperties(userAgent: Box[String]) extends HtmlProperties {
    def docType: Box[String] = Full("<!DOCTYPE html>")
    def encoding: Box[String] = Empty

    def contentType: Box[String] = {
        Full("text/html; charset=utf-8")
    }

    //def htmlParser: InputStream => Box[Elem] = Html5.parse _
    def htmlParser: InputStream => Box[NodeSeq] = PCDataXmlParser.apply _

    //def htmlWriter: (Node, Writer) => Unit = Html5.write(_, _, false)
    def htmlWriter: (Node, Writer) => Unit = (n: Node, w: Writer) => {
        val sb = new StringBuilder(64000)
        AltXML.toXML(n,
            _root_.scala.xml.TopScope,
            sb, false,
            !LiftRules.convertToEntity.vend,
            S.ieMode)
        w.append(sb)
        w.flush()
    }

    def htmlOutputHeader: Box[String] = docType.map(_ + "\n")

    val html5FormsSupport: Boolean = {
        val r = S.request openOr Req.nil
        r.isSafari5 || r.isFirefox36 || r.isFirefox40 ||
        r.isChrome5 || r.isChrome6
    }

    val maxOpenRequests: Int =
        LiftRules.maxConcurrentRequests.vend(S.request openOr Req.nil)
}


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
    def boot {
        if (!DB.jndiJdbcConnAvailable_?) {
            val vendor =
                new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
                    Props.get("db.url") openOr
                    "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
                    Props.get("db.user"), Props.get("db.password"))

            LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

            DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
        }

        // Use Lift's Mapper ORM to populate the database
        // you don't need to use Mapper to use Lift... use
        // any ORM you want
        Schemifier.schemify(true, Schemifier.infoF _, User)

        // where to search snippet
        LiftRules.addToPackages("code")

        // Build SiteMap
        def sitemap = SiteMap(
            Menu.i("Home") / "index" >> User.AddUserMenusAfter, // the simple way to declare a menu

            Menu.i("Full Map") / "full"//,

            // more complex because this menu allows anything in the
            // /static path to be visible
            //Menu(Loc("Static", Link(List("static"), true, "/static/index"),
            //  "Static Content")))
        )

        def sitemapMutators = User.sitemapMutator

        // set the sitemap.  Note if you don't want access control for
        // each page, just comment this line out.
        LiftRules.setSiteMapFunc(() => sitemapMutators(sitemap))

        LiftRules.statelessDispatchTable.append(ApiProxy)

        //Show the spinny image when an Ajax call starts
        LiftRules.ajaxStart =
            Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

        // Make the spinny image go away when it ends
        LiftRules.ajaxEnd =
            Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

        // Force the request to be UTF-8
        LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

        // What is the function to test if a user is logged in?
        LiftRules.loggedInTest = Full(() => User.loggedIn_?)

        // Use HTML5 for rendering
        //LiftRules.htmlProperties.default.set(
//            (r: Req) => new Html5Properties(r.userAgent))

        LiftRules.htmlProperties.default.set(
            (r: Req) => new GeocommitHtmlProperties(r.userAgent))

        // Use jQuery 1.4
        //LiftRules.jsArtifacts = net.liftweb.http.js.jquery.JQuery14Artifacts

        // Make a transaction span the whole HTTP request
        S.addAround(DB.buildLoanWrapper)
    }
}
