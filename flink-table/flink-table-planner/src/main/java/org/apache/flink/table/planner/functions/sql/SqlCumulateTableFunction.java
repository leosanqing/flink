/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.table.planner.functions.sql;

import org.apache.flink.table.planner.functions.utils.SqlValidatorUtils;

import org.apache.flink.shaded.guava33.com.google.common.collect.ImmutableList;

import org.apache.calcite.sql.SqlCallBinding;
import org.apache.calcite.sql.SqlOperator;

/**
 * SqlCumulateTableFunction implements an operator for cumulative.
 *
 * <p>It allows four parameters:
 *
 * <ol>
 *   <li>a table
 *   <li>a descriptor to provide a time attribute column name from the input table
 *   <li>an interval parameter to specify the window size to increase.
 *   <li>an interval parameter to specify the max length of window size
 * </ol>
 */
public class SqlCumulateTableFunction extends SqlWindowTableFunction {

    public SqlCumulateTableFunction() {
        super("CUMULATE", new OperandMetadataImpl());
    }

    /** Operand type checker for CUMULATE. */
    private static class OperandMetadataImpl extends AbstractOperandMetadata {
        OperandMetadataImpl() {
            super(
                    ImmutableList.of(
                            PARAM_DATA, PARAM_TIMECOL, PARAM_STEP, PARAM_SIZE, PARAM_OFFSET),
                    4);
        }

        @Override
        public boolean checkOperandTypes(SqlCallBinding callBinding, boolean throwOnFailure) {
            if (!SqlValidatorUtils.checkTableAndDescriptorOperands(callBinding, 1)) {
                return SqlValidatorUtils.throwValidationSignatureErrorOrReturnFalse(
                        callBinding, throwOnFailure);
            }
            if (!checkIntervalOperands(callBinding, 2)) {
                return SqlValidatorUtils.throwValidationSignatureErrorOrReturnFalse(
                        callBinding, throwOnFailure);
            }
            // check time attribute
            return SqlValidatorUtils.throwExceptionOrReturnFalse(
                    checkTimeColumnDescriptorOperand(callBinding, 1), throwOnFailure);
        }

        @Override
        public String getAllowedSignatures(SqlOperator op, String opName) {
            return opName
                    + "(TABLE table_name, DESCRIPTOR(timecol), "
                    + "datetime interval, datetime interval[, datetime interval])";
        }
    }
}
