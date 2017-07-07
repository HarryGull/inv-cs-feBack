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

package forms

import models.NumberOfSharesModel
import play.api.data.Form
import play.api.data.Forms._
import utils.Validation._


object NumberOfSharesForm {

  val formValueMessageKey = "numberOfShares"
  val minimumValue = 1
  val numberOfSharesForm = Form(
    mapping(
      "numberOfShares" -> nonEmptyText
        .verifying(genericDecimalCheck(formValueMessageKey, minimumValue))
        .transform[BigDecimal](input => BigDecimal(input).setScale(5, BigDecimal.RoundingMode.HALF_UP), _.toString)
    )(NumberOfSharesModel.apply)(NumberOfSharesModel.unapply)
  )
}