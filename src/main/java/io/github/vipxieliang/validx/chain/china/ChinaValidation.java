package io.github.vipxieliang.validx.chain.china;

import io.github.vipxieliang.validx.validator.china.*;
import io.github.vipxieliang.validx.validator.china.DrugCodeValidator;
import io.github.vipxieliang.validx.validator.china.SoftwareCopyrightValidator;
import io.github.vipxieliang.validx.validator.china.WorkCopyrightValidator;
import io.github.vipxieliang.validx.validator.china.LawyerValidator;
import io.github.vipxieliang.validx.validator.china.MedicalDeviceRegistrationValidator;
import io.github.vipxieliang.validx.validator.network.WeChatValidator;
import io.github.vipxieliang.validx.i18n.MessageManager;

import java.util.List;
import java.util.Locale;

public class ChinaValidation {
    
    public void validateIdCard(Object value, List<String> errors, Locale locale) {
        ChineseIdCardValidator validator = new ChineseIdCardValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.chinese.idcard", locale));
        }
    }
    
    public void validateLicensePlate(Object value, List<String> errors, Locale locale) {
        ChineseLicensePlateValidator validator = new ChineseLicensePlateValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.chinese.license.plate", locale));
        }
    }
    
    public void validateQQ(Object value, List<String> errors, Locale locale) {
        QQValidator validator = new QQValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.qq", locale));
        }
    }
    
    public void validateMilitaryOfficer(Object value, List<String> errors, Locale locale) {
        ChineseMilitaryOfficerValidator validator = new ChineseMilitaryOfficerValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.chinese.military.officer", locale));
        }
    }
    
    public void validatePassport(Object value, List<String> errors, Locale locale) {
        ChinesePassportValidator validator = new ChinesePassportValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.chinese.passport", locale));
        }
    }
    
    public void validateSoldier(Object value, List<String> errors, Locale locale) {
        ChineseSoldierValidator validator = new ChineseSoldierValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.chinese.soldier", locale));
        }
    }
    
    public void validateForeignerPermanentResidenceIdentity(Object value, List<String> errors, Locale locale) {
        ForeignerPermanentResidenceIdentityValidator validator = new ForeignerPermanentResidenceIdentityValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.foreigner.permanent.residence", locale));
        }
    }
    
    public void validateHKMacauPass(Object value, List<String> errors, Locale locale) {
        HKMacauPassValidator validator = new HKMacauPassValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.hk.macau.pass", locale));
        }
    }
    
    public void validateHKMacauResidence(Object value, List<String> errors, Locale locale) {
        HKMacauResidenceValidator validator = new HKMacauResidenceValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.hk.macau.residence", locale));
        }
    }
    
    public void validateTaiwanPass(Object value, List<String> errors, Locale locale) {
        TaiwanPassValidator validator = new TaiwanPassValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.taiwan.pass", locale));
        }
    }
    
    public void validateTaiwanResidence(Object value, List<String> errors, Locale locale) {
        TaiwanResidenceValidator validator = new TaiwanResidenceValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.taiwan.residence", locale));
        }
    }
    
    public void validateUnifiedSocialCreditCode(Object value, List<String> errors, Locale locale) {
        UnifiedSocialCreditCodeValidator validator = new UnifiedSocialCreditCodeValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.unified.social.credit.code", locale));
        }
    }
    
    public void validateZipCode(Object value, List<String> errors, Locale locale) {
        ChineseZipCodeValidator validator = new ChineseZipCodeValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.chinese.zip.code", locale));
        }
    }
    
    public void validatePatent(Object value, List<String> errors, Locale locale) {
        ChinesePatentValidator validator = new ChinesePatentValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.chinese.patent", locale));
        }
    }
    
    public void validateTrademark(Object value, List<String> errors, Locale locale) {
        ChineseTrademarkValidator validator = new ChineseTrademarkValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.chinese.trademark", locale));
        }
    }
    
    public void validatePhone(Object value, List<String> errors, Locale locale) {
        ChinesePhoneValidator validator = new ChinesePhoneValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.chinese.phone", locale));
        }
    }

    public void validatePhoneOrLandline(Object value, List<String> errors, Locale locale) {
        ChinesePhoneOrLandlineValidator validator = new ChinesePhoneOrLandlineValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.chinese.phone.or.landline", locale));
        }
    }

    public void validateLandline(Object value, List<String> errors, Locale locale) {
        ChineseLandlineValidator validator = new ChineseLandlineValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.chinese.landline", locale));
        }
    }
    
    public void validateDrugApproval(Object value, List<String> errors, Locale locale) {
        DrugApprovalValidator validator = new DrugApprovalValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.drug.approval", locale));
        }
    }
    
    public void validateDrugCode(Object value, List<String> errors, Locale locale) {
        DrugCodeValidator validator = new DrugCodeValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.drug.code", locale));
        }
    }
    
    // 从ChinaValidation类合并的方法
    
    public void validateSoftwareCopyright(Object value, List<String> errors, Locale locale) {
        SoftwareCopyrightValidator validator = new SoftwareCopyrightValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.software.copyright", locale));
        }
    }
    
    public void validateWorkCopyright(Object value, List<String> errors, Locale locale) {
        WorkCopyrightValidator validator = new WorkCopyrightValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.work.copyright", locale));
        }
    }

    public void validateLawyer(Object value, List<String> errors, Locale locale) {
        LawyerValidator validator = new LawyerValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.lawyer", locale));
        }
    }
    
    public void validateWeChat(Object value, List<String> errors, Locale locale) {
        WeChatValidator validator = new WeChatValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.wechat", locale));
        }
    }
    
    public void validateMedicalDeviceRegistration(Object value, List<String> errors, Locale locale) {
        MedicalDeviceRegistrationValidator validator = new MedicalDeviceRegistrationValidator();
        if (!validator.isValid((String) value, null)) {
            errors.add(MessageManager.getMessage("io.github.vipxieliang.validx.annotation.medical.device.registration", locale));
        }
    }
}