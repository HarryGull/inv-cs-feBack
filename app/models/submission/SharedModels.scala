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

package models.submission

import play.api.libs.json.Json

case class KiModel(
                    skilledEmployeesConditionMet: Boolean,
                    innovationConditionMet: Option[String],
                    kiConditionMet: Option[Boolean]
                  )

case class CostModel(
                      amount : String,
                      currency: String = "GBP"
                    )

case class AnnualCostModel(
                     periodEnding: String,
                     operatingCost: CostModel,
                     researchAndDevelopmentCost: CostModel
                     )

case class TurnoverCostModel(
                            periodEnding: String,
                            turnover: CostModel
                          )

case class UnitIssueModel(
                           description: String,
                           dateOfIssue: String,
                           unitType: String, // Mandatory as per schema
                           nominalValue: CostModel,
                           numberUnitsIssued: BigDecimal,
                           totalAmount: CostModel
                         )

object SharedImplicits {

  implicit val formatSubmitKiModel = Json.format[KiModel]
  implicit val formatSubmitCostModel = Json.format[CostModel]
  implicit val formatSubmitAnnualCostModel = Json.format[AnnualCostModel]
  implicit val formatSubmitTurnoverCostModel = Json.format[TurnoverCostModel]
  implicit val formatSubmitUnitIssueModel = Json.format[UnitIssueModel]
}
