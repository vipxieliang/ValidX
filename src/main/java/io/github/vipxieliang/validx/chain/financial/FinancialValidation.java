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

package io.github.vipxieliang.validx.chain.financial;

import io.github.vipxieliang.validx.annotations.SWIFT;
import io.github.vipxieliang.validx.annotations.StockCode;
import io.github.vipxieliang.validx.annotations.FinancialProductCode;
import io.github.vipxieliang.validx.annotations.TradeOrderNumber;
import io.github.vipxieliang.validx.validator.financial.BankCardValidator;
import io.github.vipxieliang.validx.validator.financial.CVVValidator;
import io.github.vipxieliang.validx.validator.financial.IBANValidator;
import io.github.vipxieliang.validx.validator.financial.SWIFTValidator;
import io.github.vipxieliang.validx.validator.financial.StockCodeValidator;
import io.github.vipxieliang.validx.validator.financial.FinancialProductCodeValidator;
import io.github.vipxieliang.validx.validator.financial.TradeOrderNumberValidator;
import io.github.vipxieliang.validx.i18n.MessageManager;

import javax.validation.Payload;
import java.util.List;
import java.util.Locale;

public class FinancialValidation {
    
    public void validateBankCard(Object value, List<String> errors, Locale locale) {
        BankCardValidator validator = new BankCardValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.bank.card", locale));
        }
    }
    
    public void validateCVV(Object value, List<String> errors, Locale locale) {
        CVVValidator validator = new CVVValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.cvv", locale));
        }
    }
    
    public void validateIBAN(Object value, List<String> errors, Locale locale) {
        IBANValidator validator = new IBANValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.iban", locale));
        }
    }
    
    public void validateSWIFT(Object value, List<String> errors, Locale locale) {
        SWIFTValidator validator = new SWIFTValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.swift", locale));
        }
    }
    
    public void validateStockCode(Object value, StockCode.Exchange[] exchanges, List<String> errors, Locale locale) {
        StockCodeValidator validator = new StockCodeValidator();
        validator.initialize(exchanges != null ? exchanges : new StockCode.Exchange[0]);
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.stock.code", locale));
        }
    }
    
    public void validateFinancialProductCode(Object value, FinancialProductCode.ProductType[] productTypes, List<String> errors, Locale locale) {
        FinancialProductCodeValidator validator = new FinancialProductCodeValidator();
        validator.initialize(productTypes != null ? productTypes :
            new FinancialProductCode.ProductType[]{FinancialProductCode.ProductType.FUND, FinancialProductCode.ProductType.BOND});
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.financial.product.code", locale));
        }
    }
    
    public void validateTradeOrderNumber(Object value, List<String> errors, Locale locale) {
        TradeOrderNumberValidator validator = new TradeOrderNumberValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.trade.order.number", locale));
        }
    }
}