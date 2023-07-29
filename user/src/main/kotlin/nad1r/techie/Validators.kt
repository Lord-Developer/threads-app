package nad1r.techie

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext


class PasswordValidator : ConstraintValidator<ValidPassword, String> {

    override fun isValid(password: String?, context: ConstraintValidatorContext): Boolean {
        if (password == null) return false

        // Implement your password validation logic here
        // Example: Ensure password is at least 8 characters long and contains at least one uppercase letter and one digit
        val regex = "(?=.*[A-Z])(?=.*\\d).{8,}"
        return password.matches(Regex(regex))
    }
}