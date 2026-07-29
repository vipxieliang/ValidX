/*
 * Copyright 2025-2025 vipxieliang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.vipxieliang.validx.chain.vehicle;

import io.github.vipxieliang.validx.validator.vehicle.VehicleEngineValidator;
import io.github.vipxieliang.validx.validator.vehicle.VINValidator;
import io.github.vipxieliang.validx.i18n.MessageManager;

import java.util.List;
import java.util.Locale;

public class VehicleValidation {

    public void validateVIN(Object value, List<String> errors, Locale locale) {
        VINValidator validator = new VINValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.vin", locale));
        }
    }
    
    public void validateVehicleEngine(Object value, List<String> errors, Locale locale) {
        VehicleEngineValidator validator = new VehicleEngineValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.vehicle.engine", locale));
        }
    }
}