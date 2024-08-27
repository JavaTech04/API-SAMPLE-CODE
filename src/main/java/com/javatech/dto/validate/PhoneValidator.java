package com.javatech.dto.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<PhoneNumber, String> {

    @Override
    public void initialize(PhoneNumber phoneNumber) {
    }

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext constraintValidatorContext) {
        if (phone == null) {
            return false;
        }

        if (phone.matches("\\d{10}")) { //validate phone numbers of format "0123456789"
            return true;
        } else if (phone.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) { //validating phone number with -, . or spaces: 012-345-6789
            return true;
        } else { //validating phone number with extension length from 3 to 5
            if (phone.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) { //return false if nothing matches the input
                return true;
            } else { //validating phone number where area code is in braces ()
                return phone.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}");
            }
        }
    }
}
