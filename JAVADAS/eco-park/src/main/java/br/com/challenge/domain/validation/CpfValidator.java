package br.com.challenge.domain.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<ValidCpf, String> {

    @Override
    public void initialize(ValidCpf constraintAnnotation) {
    }

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return false;
        }

        cpf = cpf.replaceAll("\\D", "");

        if (cpf.length() != 11) {
            return false;
        }

        return true;
    }
}