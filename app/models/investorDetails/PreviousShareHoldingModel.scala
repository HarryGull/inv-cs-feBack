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

package models.investorDetails

import play.api.libs.json.Json

case class PreviousShareHoldingModel(investorShareIssueDateModel: Option[InvestorShareIssueDateModel] = None,
                                     numberOfPreviouslyIssuedSharesModel: Option[NumberOfPreviouslyIssuedSharesModel] = None,
                                     previousShareHoldingNominalValueModel: Option[PreviousShareHoldingNominalValueModel] = None,
                                     previousShareHoldingDescriptionModel: Option[PreviousShareHoldingDescriptionModel] = None,
                                     processingId: Option[Int] = None, investorProcessingId: Option[Int] = None){

  /** Validates that all PreviousShareHolding fields exist**/
  def validate: Boolean = investorShareIssueDateModel.isDefined && numberOfPreviouslyIssuedSharesModel.isDefined &&
    previousShareHoldingNominalValueModel.isDefined  && previousShareHoldingDescriptionModel.isDefined
}

object PreviousShareHoldingModel{
  implicit val formats = Json.format[PreviousShareHoldingModel]
}
