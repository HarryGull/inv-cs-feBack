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

package views.seis

import org.jsoup.Jsoup
import play.api.test.FakeRequest
import views.helpers.ViewSpec
import views.html.seis.shareDetails.TotalAmountSpent
import forms.TotalAmountSpentForm._
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.test.Helpers._


class TotalAmountSpentSpec extends ViewSpec {

  "TotalAmountSpent view" when {
    implicit lazy val request = FakeRequest("GET", "")

    "not supplied with form errors" should {
      lazy val document: Document = {
        val result = TotalAmountSpent(totalAmountSpentForm)
        Jsoup.parse(contentAsString(result))
      }

      "have the correct title" in {
        document.title() shouldBe Messages("page.shareDetails.totalAmountSpent.title")
      }

      "have the correct back link text" in {
        document.select("a.back-link").text() shouldBe Messages("common.button.back")
      }

      "have the correct back link url" in {
        document.select("a.back-link").attr("href") shouldBe "/investment-tax-relief-cs/seis/total-amount-raised"
      }

      "have the progress details" in {
        document.select("article span").first().text shouldBe Messages("common.section.progress.details.three")
      }

      "have the correct heading" in {
        document.select("h1").text() shouldBe Messages("page.shareDetails.totalAmountSpent.heading")
      }

      "have a form posting to the correct route" in {
        document.select("form").attr("action") shouldBe controllers.seis.routes.TotalAmountSpentController.submit().url
      }

      "have the correct question in a label" in {
        document.select("fieldset label").text() shouldBe Messages("page.shareDetails.totalAmountSpent.heading")
      }

      "have a next button" in {
        document.select("button").text() shouldBe Messages("common.button.snc")
      }
    }

    "supplied with form errors" should {
      lazy val document: Document = {
        val map = Map("employeeCount" -> "")
        val result = TotalAmountSpent(totalAmountSpentForm.bind(map))
        Jsoup.parse(contentAsString(result))
      }

      "have an error summary" in {
        document.getElementById("error-summary-display").hasClass("error-summary--show")
      }

      "have the correct title" in {
        document.title() shouldBe Messages("page.shareDetails.totalAmountSpent.title")
      }

      "have the correct back link text" in {
        document.select("a.back-link").text() shouldBe Messages("common.button.back")
      }

      "have the correct back link url" in {
        document.select("a.back-link").attr("href") shouldBe "/investment-tax-relief-cs/seis/total-amount-raised"
      }

      "have the progress details" in {
        document.select("article span").first().text shouldBe Messages("common.section.progress.details.three")
      }

      "have the correct heading" in {
        document.select("h1").text() shouldBe Messages("page.shareDetails.totalAmountSpent.heading")
      }

      "have a form posting to the correct route" in {
        document.select("form").attr("action") shouldBe controllers.seis.routes.TotalAmountSpentController.submit().url
      }

      "have a next button" in {
        document.select("button").text() shouldBe Messages("common.button.snc")
      }
    }
  }
}
