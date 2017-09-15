/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers.eis

import auth.{AuthorisedAndEnrolledForTAVC, EIS, VCT}
import common.KeystoreKeys
import config.{FrontendAppConfig, FrontendAuthConnector}
import connectors.{EnrolmentConnector, S4LConnector}
import forms.AmountSharesRepaymentForm._
import models.repayments.AmountSharesRepaymentModel
import uk.gov.hmrc.play.frontend.controller.FrontendController
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import views.html.eis.investors.AmountSharesRepayment

import scala.concurrent.Future
import play.api.mvc.{Action, AnyContent, Result}
import play.api.data.Form


object AmountSharesRepaymentController extends AmountSharesRepaymentController{
  override lazy val s4lConnector = S4LConnector
  override lazy val applicationConfig = FrontendAppConfig
  override lazy val authConnector = FrontendAuthConnector
  override lazy val enrolmentConnector = EnrolmentConnector
}

trait AmountSharesRepaymentController extends FrontendController with AuthorisedAndEnrolledForTAVC {

  override val acceptedFlows = Seq(Seq(EIS))

  val show = AuthorisedAndEnrolled.async { implicit user => implicit request =>
    s4lConnector.fetchAndGetFormData[AmountSharesRepaymentModel](KeystoreKeys.amountSharesRepayment).map {
      case Some(data) => Ok(AmountSharesRepayment(amountSharesRepaymentForm.fill(data)))
      case None => Ok(AmountSharesRepayment(amountSharesRepaymentForm))
    }
  }

  val submit: Action[AnyContent] = AuthorisedAndEnrolled.async { implicit user => implicit request =>
    val success: AmountSharesRepaymentModel => Future[Result] = { model =>
      s4lConnector.saveFormData(KeystoreKeys.amountSharesRepayment, model).map(_ =>
        //TODO: Route to next page (review) when available
        Redirect(routes.AmountSharesRepaymentController.show())
      )
    }

    val failure: Form[AmountSharesRepaymentModel] => Future[Result] = { form =>
      Future.successful(BadRequest(AmountSharesRepayment(form)))
    }

    amountSharesRepaymentForm.bindFromRequest().fold(failure, success)
  }

}

