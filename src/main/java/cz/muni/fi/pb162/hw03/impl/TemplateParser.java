package cz.muni.fi.pb162.hw03.impl;

import cz.muni.fi.pb162.hw03.impl.model.MapModel;
import cz.muni.fi.pb162.hw03.impl.parser.tokens.Token;
import cz.muni.fi.pb162.hw03.impl.parser.tokens.Tokenizer;
import cz.muni.fi.pb162.hw03.template.TemplateException;
import cz.muni.fi.pb162.hw03.template.model.ModelException;

/**
 * Template parser
 * @author Martin Drazkovec, 536686
 */
public class TemplateParser {
    /**
     * Parses template
     * @param template template to parse
     * @param model model to use
     * @return parsed template
     */
    public static String parseTemplate(Template template, MapModel model) {
        Tokenizer tokenizer = new Tokenizer(template.data());
        return parseFromTokenizer(tokenizer, model);
    }

    /**
     * Parses template from tokenizer
     * @param tokenizer tokenizer to parse from
     * @param model model to use
     * @return parsed template
     */
    private static String parseFromTokenizer(Tokenizer tokenizer, MapModel model) {
        StringBuilder stringBuilder = new StringBuilder();

        while (!tokenizer.done()) {
            Token token = tokenizer.consumeKeyword();
            if (token.getKind().equals(Token.Kind.TEXT)) {
                stringBuilder.append(token.text());
            }
            if (token.getKind().equals(Token.Kind.NAME)) {
                String tokenValue = model.getAsString(token.name());
                if (tokenValue == null) {
                    throw new ModelException("Name not found: " + token.name());
                }
                stringBuilder.append(tokenValue);
            }
            if (token.getKind().equals(Token.Kind.CMD)) {
                if (token.cmd().equals("done") || token.cmd().equals("else")) {
                    return stringBuilder.toString();
                }
                stringBuilder.append(parseCommand(tokenizer, model));
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Parses command
     * @param tokenizer tokenizer to parse from
     * @param model model to use
     * @return parsed command
     */
    private static String parseCommand(Tokenizer tokenizer, MapModel model) {
        if (tokenizer.getLastToken().cmd().equals("if")) {
            return parseIf(tokenizer, model);
        }
        if (tokenizer.getLastToken().cmd().equals("for")) {
            return parseFor(tokenizer, model);
        }
        throw new TemplateException("Invalid command: " + tokenizer.getLastToken().cmd());
    }

    /**
     * Parses if command
     * @param tokenizer tokenizer to parse from
     * @param model model to use
     * @return parsed if command
     */
    private static String parseIf(Tokenizer tokenizer, MapModel model) {
        String result = "";
        Token ifToken = tokenizer.consumeKeyword();
        if (model.getAsBoolean(ifToken.name())) {
            result = parseFromTokenizer(tokenizer, model);
        } else {
            parseFromTokenizer(tokenizer, model);
        }
        if (tokenizer.getLastTokenKind().equals(Token.Kind.CMD) && tokenizer.getLastToken().cmd().equals("else")) {
            if (!model.getAsBoolean(ifToken.name())) {
                result = parseFromTokenizer(tokenizer, model);
            } else {
                parseFromTokenizer(tokenizer, model);
            }
        }
        return result;
    }

    /**
     * Parses for command
     * @param tokenizer tokenizer to parse from
     * @param model model to use
     * @return parsed for command
     */
    private static String parseFor(Tokenizer tokenizer, MapModel model) {
        StringBuilder stringBuilder = new StringBuilder();
        String varName = tokenizer.consumeKeyword().name();
        Iterable iterable = model.getAsIterable(tokenizer.consumeKeyword().name());
        tokenizer.consume();
        for (var variable : iterable) {
            stringBuilder.append(parseFromTokenizer(tokenizer.copy(), (MapModel) model.extended(varName, variable)));
        }
        parseFromTokenizer(tokenizer, (MapModel) model.copy());
        return stringBuilder.toString();
    }
}
