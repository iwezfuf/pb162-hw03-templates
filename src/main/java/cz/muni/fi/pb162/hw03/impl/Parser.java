package cz.muni.fi.pb162.hw03.impl;

import cz.muni.fi.pb162.hw03.impl.model.MapModel;
import cz.muni.fi.pb162.hw03.impl.parser.tokens.Token;
import cz.muni.fi.pb162.hw03.impl.parser.tokens.Tokenizer;
import cz.muni.fi.pb162.hw03.template.TemplateException;
import cz.muni.fi.pb162.hw03.template.model.ModelException;


public class Parser {
    MapModel model;
    Template template;

    public Parser(MapModel model, Template template) {
        this.model = model;
        this.template = template;
    }

    public String parseTemplate(Template template, MapModel model) {
        Tokenizer tokenizer = new Tokenizer(template.getData());
        return parseFromTokenizer(tokenizer, model);
    }

    public String parseFromTokenizer(Tokenizer tokenizer, MapModel model) {
        StringBuilder stringBuilder = new StringBuilder();

        while (!tokenizer.done()) {
//            System.out.println(stringBuilder.toString());
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

    public String parseCommand(Tokenizer tokenizer, MapModel model) {
        if (tokenizer.getLastToken().cmd().equals("if")) {
            return parseIf(tokenizer, model);
        }
        if (tokenizer.getLastToken().cmd().equals("for")) {
            return parseFor(tokenizer, model);
        }
        System.out.println(tokenizer.getLastToken().cmd());
        throw new TemplateException("Invalid command: " + tokenizer.getLastTokenKind().name());
    }

    public String parseIf(Tokenizer tokenizer, MapModel model) {
        String result = "";
        Token ifToken = tokenizer.consumeKeyword();
        if (model.getAsBoolean(ifToken.name())) {
            tokenizer.consumeKeyword();
            result = parseFromTokenizer(tokenizer, model);
        } else {
            tokenizer.consumeKeyword();
            tokenizer.consumeKeyword();
//            tokenizer.consumeKeyword();
            System.out.println("grrve" + tokenizer.getLastTokenKind());
        }
        tokenizer.consumeKeyword();
        tokenizer.consumeKeyword();
//        System.out.println("aaa" + tokenizer.consumeKeyword().text());
//        System.out.println("a" + tokenizer.consumeKeyword().getKind());
//
//        if (tokenizer.consumeKeyword().getKind().equals(Token.Kind.CMD)) {
//            System.out.println("Here:" + tokenizer.getLastToken().cmd());
//        }
        if (tokenizer.getLastToken().getKind().equals(Token.Kind.CMD) && tokenizer.getLastToken().cmd().equals("else")) {
            System.out.println("oinside else");
            if (!model.getAsBoolean(ifToken.name())) {
                tokenizer.consumeKeyword();
//                tokenizer.consumeKeyword();
//                System.out.println(tokenizer.getLastToken().text());
                result = parseFromTokenizer(tokenizer, model);
            } else {
                tokenizer.consumeKeyword();
                tokenizer.consumeKeyword();
                tokenizer.consumeKeyword();
                tokenizer.consumeKeyword();

            }
        }
        Token token = tokenizer.consumeKeyword();
        while (!token.getKind().equals(Token.Kind.CMD) || !token.cmd().equals("done")) {
            token = tokenizer.consumeKeyword();
        }
        System.out.println("result: " + result);
        return result;
    }

    public String parseFor(Tokenizer tokenizer, MapModel model) {
        return "";
    }
}
