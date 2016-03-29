/**
 * Copyright (C) 2015 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stratio.sparta.serving.core.models

import com.stratio.sparta.serving.core.exception.ServingCoreException
import com.stratio.sparta.serving.core.policy.status.PolicyStatusEnum

case class AggregationPoliciesModel(id: Option[String] = None,
                                    version: Option[Int] = None,
                                    storageLevel: Option[String] = AggregationPoliciesModel.storageDefaultValue,
                                    name: String,
                                    description: String = "default description",
                                    sparkStreamingWindow: String = AggregationPoliciesModel.sparkStreamingWindow,
                                    checkpointPath: String,
                                    rawData: RawDataModel,
                                    transformations: Seq[TransformationsModel],
                                    streamTriggers: Seq[TriggerModel],
                                    cubes: Seq[CubeModel],
                                    input: Option[PolicyElementModel] = None,
                                    outputs: Seq[PolicyElementModel],
                                    fragments: Seq[FragmentElementModel],
                                    userPluginsJars: Seq[String])

case object AggregationPoliciesModel {

  val sparkStreamingWindow = "2s"
  val storageDefaultValue = Some("MEMORY_AND_DISK_SER_2")

  def checkpointPath(policy: AggregationPoliciesModel): String =
    s"${policy.checkpointPath}/${policy.name}"
}

case class PolicyWithStatus(status: PolicyStatusEnum.Value,
                            policy: AggregationPoliciesModel)

case class PolicyResult(policyId: String, policyName: String)

object AggregationPoliciesValidator extends SpartaSerializer {

  def validateDto(aggregationPoliciesDto: AggregationPoliciesModel): Unit = {

    val outputsNames = aggregationPoliciesDto.outputs.map(_.name)

    val subErrorModels = List(
      (aggregationPoliciesDto.cubes.forall(cube => cube.name.nonEmpty),
       new ErrorModel(
         ErrorModel.ValidationError_There_is_at_least_one_cube_without_name,
         "There is at least one cube without name")),
      (aggregationPoliciesDto.cubes.forall(cube => cube.dimensions.nonEmpty),
       new ErrorModel(
         ErrorModel.ValidationError_There_is_at_least_one_cube_without_dimensions,
         "There is at least one cube without dimensions")),
      (aggregationPoliciesDto.outputs.nonEmpty,
        new ErrorModel(
          ErrorModel.ValidationError_The_policy_needs_at_least_one_output,
       "The policy needs at least one output")),
      (aggregationPoliciesDto.cubes.nonEmpty || aggregationPoliciesDto.streamTriggers.nonEmpty,
        new ErrorModel(
          ErrorModel.ValidationError_The_policy_needs_at_least_one_cube_or_one_trigger,
        "The policy needs at least one cube or one trigger")),
      (aggregationPoliciesDto.cubes.forall(cube =>
        cube.writer.outputs.forall(output =>
          outputsNames.contains(output))),
        new ErrorModel(
          ErrorModel.ValidationError_There_is_at_least_one_cube_with_a_bad_output,
       "There is at least one cube with a bad output")),
      (aggregationPoliciesDto.cubes.forall(cube =>
        cube.triggers.forall(trigger =>
          trigger.outputs.forall(outputName => outputsNames.contains(outputName)))),
        new ErrorModel(
          ErrorModel.ValidationError_There_is_at_least_one_cube_with_triggers_with_a_bad_output,
        "There is at least one cube with triggers that contains a bad output")),
      (aggregationPoliciesDto.streamTriggers.forall(trigger =>
      trigger.outputs.forall(outputName =>
        outputsNames.contains(outputName))),
        new ErrorModel(
          ErrorModel.ValidationError_There_is_at_least_one_stream_trigger_with_a_bad_output,
        "There is at least one stream trigger that contains a bad output"))
    ).filter(element => ! element._1)

    if(subErrorModels.nonEmpty) {
      throw new ServingCoreException(ErrorModel.toString(
        new ErrorModel(ErrorModel.ValidationError, "Policy validation error",
          Option(subErrorModels.map(element => element._2).toSeq)
      )))
    }
  }
}
