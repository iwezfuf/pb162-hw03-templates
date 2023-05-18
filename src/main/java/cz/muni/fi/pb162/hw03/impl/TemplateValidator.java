package cz.muni.fi.pb162.hw03.impl;
import cz.muni.fi.pb162.hw03.impl.parser.tokens.Token;
import cz.muni.fi.pb162.hw03.impl.parser.tokens.Tokenizer;
import java.util.Stack;

/**
 * Template validator
 * @author Martin Drazkovec, 536686
 */
public class TemplateValidator {
    private final Tokenizer tokenizer;

    /**
     * Constructor
     * @param template template to validate
     */
    public TemplateValidator(String template) {
        tokenizer = new Tokenizer(template);
    }

    /**
     * Checks if the next token is of given kind
     * @param tokenKind kind of token to check
     * @return true if the next token is of given kind, false otherwise
     */
    private boolean validateToken(Token.Kind tokenKind) {
        return tokenizer.consume().getKind().equals(tokenKind);
    }

    /**
     * Validates template
     * @return true if template is valid, false otherwise
     */
    public boolean validateTemplate() {
        return validate(0, 0, new Stack<>());
    }

    /**
     * Recursive validation function
     * @param allowedDoneCount number of allowed done commands
     * @param allowedElseCount number of allowed else commands
     * @param usesElse stack of booleans, true if else is used in the command that belongs to the next done,
     *                false otherwise
     * @return true if template is valid, false otherwise
     */
    private boolean validate(int allowedDoneCount, int allowedElseCount, Stack<Boolean> usesElse) {
        if (tokenizer.done()) {
            return allowedDoneCount == 0 && allowedElseCount == 0;
        }
        Token token = tokenizer.consume();
        if (token.getKind().equals(Token.Kind.TEXT)) {
            return validate(allowedDoneCount, allowedElseCount, usesElse);
        }
        if (token.getKind().equals(Token.Kind.OPEN)) {
            Token nestedToken = tokenizer.consume();
            if (nestedToken.getKind().equals(Token.Kind.NAME)) {
                return validateToken(Token.Kind.CLOSE) && validate(allowedDoneCount, allowedElseCount, usesElse);
            }
            if (!nestedToken.getKind().equals(Token.Kind.CMD)) {
                return false;
            }
            if (nestedToken.cmd().equals("if")) {
                return validateIf(allowedDoneCount, allowedElseCount, usesElse);
            }
            if (nestedToken.cmd().equals("for")) {
                return validateFor(allowedDoneCount, allowedElseCount, usesElse);
            }
            if (nestedToken.cmd().equals("done")) {
                if (!validateToken(Token.Kind.CLOSE) || allowedDoneCount <= 0) {
                    return false;
                }
                if (usesElse.pop()) {
                    return (validate(--allowedDoneCount, --allowedElseCount, usesElse));
                }
                return (validate(--allowedDoneCount, allowedElseCount, usesElse));
            }
            if (nestedToken.cmd().equals("else")) {
                if (!validateToken(Token.Kind.CLOSE) || allowedElseCount <= 0) {
                    return false;
                }
                return (validate(allowedDoneCount, allowedElseCount, usesElse));
            }
        }
        return false;
    }

    /**
     * Validates if command
     * @param allowedDoneCount number of allowed done commands
     * @param allowedElseCount number of allowed else commands
     * @param usesElse stack of booleans, true if else is used in the command that belongs to the next done,
     *                 false otherwise
     * @return true if template is valid, false otherwise
     */
    private boolean validateIf(int allowedDoneCount, int allowedElseCount, Stack<Boolean> usesElse) {
        if (!validateToken(Token.Kind.NAME)
                || !validateToken(Token.Kind.CLOSE)) {
            return false;
        }
        usesElse.push(true);
        return validate(++allowedDoneCount, ++allowedElseCount, usesElse);
    }

    /**
     * Validates for command
     * @param allowedDoneCount number of allowed done commands
     * @param allowedElseCount number of allowed else commands
     * @param usesElse stack of booleans, true if else is used in the command that belongs to the next done,
     *                 false otherwise
     * @return true if template is valid, false otherwise
     */
    private boolean validateFor(int allowedDoneCount, int allowedElseCount, Stack<Boolean> usesElse) {
        if ((!validateToken(Token.Kind.NAME))
                || (!validateToken(Token.Kind.IN))
                || (!validateToken(Token.Kind.NAME))
                || (!validateToken(Token.Kind.CLOSE))) {
            return false;
        }
        usesElse.push(false);
        return (validate(++allowedDoneCount, allowedElseCount, usesElse));
    }
}
