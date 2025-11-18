package Components

fun isValidPassword(pw: String): Boolean {
    val hasUpper = pw.any { it.isUpperCase() }
    val hasLower = pw.any { it.isLowerCase() }
    val hasDigit = pw.any { it.isDigit() }
    val longEnough = pw.length >= 8

    return hasUpper && hasLower && hasDigit && longEnough
}