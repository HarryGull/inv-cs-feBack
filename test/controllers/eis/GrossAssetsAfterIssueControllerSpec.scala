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

import auth.{MockAuthConnector, MockConfig}
import common.KeystoreKeys
import config.{FrontendAppConfig, FrontendAuthConnector}
import connectors.{EnrolmentConnector, S4LConnector, SubmissionConnector}
import controllers.helpers.BaseSpec
import models._
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.test.Helpers._
import uk.gov.hmrc.play.http.Upstream5xxResponse

import scala.concurrent.Future

class GrossAssetsAfterIssueControllerSpec extends BaseSpec {

  object TestController extends GrossAssetsAfterIssueController {
    override lazy val applicationConfig = MockConfig
    override lazy val authConnector = MockAuthConnector
    override lazy val s4lConnector = mockS4lConnector
    override lazy val submissionConnector = mockSubmissionConnector
    override lazy val enrolmentConnector = mockEnrolmentConnector
  }

  val grossAssetsAmount = 200000
  val failedResponse = Upstream5xxResponse("Error",INTERNAL_SERVER_ERROR,INTERNAL_SERVER_ERROR)

  "GrossAssetsController" should {
    "use the correct keystore connector" in {
      GrossAssetsAfterIssueController.s4lConnector shouldBe S4LConnector
    }
    "use the correct auth connector" in {
      GrossAssetsAfterIssueController.authConnector shouldBe FrontendAuthConnector
    }
    "use the correct enrolment connector" in {
      GrossAssetsAfterIssueController.enrolmentConnector shouldBe EnrolmentConnector
    }
    "use the correct submission connector" in {
      GrossAssetsAfterIssueController.submissionConnector shouldBe SubmissionConnector
    }
    "use the correct application config" in {
      GrossAssetsAfterIssueController.applicationConfig shouldBe FrontendAppConfig
    }
  }

  "Sending a GET request to GrossAssetsAfterIssueController when authenticated and enrolled" should {

    "return an OK when something is fetched from session" in {
      mockEnrolledRequest(eisSchemeTypesModel)
      when(mockS4lConnector.fetchAndGetFormData[GrossAssetsAfterIssueModel](Matchers.eq(KeystoreKeys.grossAssetsAfterIssue))
        (Matchers.any(), Matchers.any(), Matchers.any())).thenReturn(Future.successful(Option(GrossAssetsAfterIssueModel(grossAssetsAmount))))
      showWithSessionAndAuth(TestController.show)(
        result => status(result) shouldBe OK
      )
    }

    "return an OK when nothing is fetched from session" in {
      when(mockS4lConnector.fetchAndGetFormData[GrossAssetsAfterIssueModel](Matchers.eq(KeystoreKeys.grossAssetsAfterIssue))
        (Matchers.any(), Matchers.any(), Matchers.any())).thenReturn(Future.successful(None))
      mockEnrolledRequest(eisSchemeTypesModel)
      showWithSessionAndAuth(TestController.show)(
        result => status(result) shouldBe OK
      )
    }

  }

  "Sending a valid form submit to the GrossAssetsAfterIssueController" should {
    "redirect to the correct next page if the allowed amount is not exceeded from API" +
      "and the date of incorporation is less than 3 years ago" in {
      when(mockSubmissionConnector.checkGrossAssetsAfterIssueAmountExceeded(Matchers.any())
      (Matchers.any())).thenReturn(Future.successful(Option(false)))
      when(mockS4lConnector.fetchAndGetFormData[DateOfIncorporationModel](Matchers.eq(KeystoreKeys.dateOfIncorporation))
        (Matchers.any(), Matchers.any(),Matchers.any())).thenReturn(Future.successful(Some(dateOfIncorporationModelKI)))
      mockEnrolledRequest(eisSchemeTypesModel)
      submitWithSessionAndAuth(TestController.submit,
        "grossAmount" -> "200000")(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.FullTimeEmployeeCountController.show().url)
        }
      )
    }
  }

  "Sending a valid form submit to the GrossAssetsAfterIssueController" should {
    "redirect to the correct next page if the allowed amount is not exceeded from API" +
      "and the date of incorporation is more than 3 years ago" in {
      when(mockSubmissionConnector.checkGrossAssetsAfterIssueAmountExceeded(Matchers.any())
      (Matchers.any())).thenReturn(Future.successful(Option(false)))
      when(mockS4lConnector.fetchAndGetFormData[DateOfIncorporationModel](Matchers.eq(KeystoreKeys.dateOfIncorporation))
        (Matchers.any(), Matchers.any(),Matchers.any())).thenReturn(Future.successful(Some(dateOfIncorporationModel)))
      mockEnrolledRequest(eisSchemeTypesModel)
      submitWithSessionAndAuth(TestController.submit,
        "grossAmount" -> "200000")(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.IsCompanyKnowledgeIntensiveController.show().url)
        }
      )
    }
  }

  "Sending a valid form submit to the GrossAssetsAfterIssueController" should {
    "redirect to the correct next page if the allowed amount is exceeded from API" in {
      when(mockSubmissionConnector.checkGrossAssetsAfterIssueAmountExceeded(Matchers.any())
      (Matchers.any())).thenReturn(Future.successful(Option(true)))
      when(mockS4lConnector.fetchAndGetFormData[DateOfIncorporationModel](Matchers.eq(KeystoreKeys.dateOfIncorporation))
        (Matchers.any(), Matchers.any(),Matchers.any())).thenReturn(Future.successful(Some(dateOfIncorporationModel)))
      mockEnrolledRequest(eisSchemeTypesModel)
      submitWithSessionAndAuth(TestController.submit,
        "grossAmount" -> "20000000")(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.GrossAssetsAfterIssueErrorController.show().url)
        }
      )
    }
  }

  "Sending a valid form submit to the GrossAssetsAfterIssueController" should {
    "show the INTERNAL_SERVER_ERROR page if an exception occurs during the API call" in {
      when(mockSubmissionConnector.checkGrossAssetsAfterIssueAmountExceeded(Matchers.any())
      (Matchers.any())).thenReturn(Future.failed(failedResponse))
      mockEnrolledRequest(eisSchemeTypesModel)
      submitWithSessionAndAuth(TestController.submit,
        "grossAmount" -> "200001")(
        result => {
          status(result) shouldBe INTERNAL_SERVER_ERROR
        }
      )
    }
  }

  "Sending a valid form submit to the GrossAssetsAfterIssueController" should {
    "show the INTERNAL_SERVER_ERROR page if the API call returns an empty Option" in {
      when(mockSubmissionConnector.checkGrossAssetsAfterIssueAmountExceeded(Matchers.any())
      (Matchers.any())).thenReturn(Future.successful(None))
      mockEnrolledRequest(eisSchemeTypesModel)
      submitWithSessionAndAuth(TestController.submit,
        "grossAmount" -> "200001")(
        result => {
          status(result) shouldBe INTERNAL_SERVER_ERROR
        }
      )
    }
  }

  "Sending an invalid form submission with validation errors to the GrossAssetsAfterIssueController" should {
    "respond wih a bad request" in {
      mockEnrolledRequest(eisSchemeTypesModel)
      submitWithSessionAndAuth(TestController.submit,
        "grossAmount" -> "")(
        result => {
          status(result) shouldBe BAD_REQUEST
        }
      )
    }
  }

}

