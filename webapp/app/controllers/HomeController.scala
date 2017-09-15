package controllers

import javax.inject.Inject

import models._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n._
import play.api.mvc._
import play.api.i18n.{MessagesApi, Messages}
import play.api.i18n.Messages.Implicits._
import views._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Manage a database of computers
  */
class HomeController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  private val logger = play.api.Logger(this.getClass)


  def index = Action {
    Ok("It works!")
  }


}

//class HomeController @Inject()(cc: MessagesControllerComponents)(implicit ec: ExecutionContext)
//  extends MessagesAbstractController(cc) {
//
//  private val logger = play.api.Logger(this.getClass)
//
//
//  def index = Action {
//    Ok(views.html.index())
//  }
//
//
//}

//class Application @Inject()
//(cc: ControllerComponents)
//  extends AbstractController(cc) {
//
//  def index = Action {
//    Ok("It works!")
//  }
//
//}
            
