package cz.muni.fi.pb162.hw03.impl.engines;

import cz.muni.fi.pb162.hw03.impl.Parser;
import cz.muni.fi.pb162.hw03.impl.Template;
import cz.muni.fi.pb162.hw03.impl.model.MapModel;
import cz.muni.fi.pb162.hw03.impl.parser.tokens.Token;
import cz.muni.fi.pb162.hw03.impl.parser.tokens.Tokenizer;
import cz.muni.fi.pb162.hw03.template.FSTemplateEngine;
import cz.muni.fi.pb162.hw03.template.TemplateException;
import cz.muni.fi.pb162.hw03.template.model.ModelException;
import cz.muni.fi.pb162.hw03.template.model.TemplateModel;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FSTEngine implements FSTemplateEngine {
    private final Map<String, Template> templates = new HashMap<>();
    private MapModel mapModel = new MapModel(new HashMap<>());
    @Override
    public void loadTemplate(Path file, Charset cs, String ext) {

    }

    @Override
    public void loadTemplateDir(Path inDir, Charset cs, String ext) {

    }

    @Override
    public void writeTemplate(String name, TemplateModel model, Path file, Charset cs) {

    }

    @Override
    public void writeTemplates(TemplateModel model, Path outDir, Charset cs) {

    }

    @Override
    public void loadTemplate(String name, String text) {
        templates.put(name, new Template(name, text));
    }

    @Override
    public Collection<String> getTemplateNames() {
        return templates.keySet();
    }

    @Override
    public String evaluateTemplate(String name, TemplateModel model) {
        Template template = templates.get(name);
        if (template == null) {
            throw new TemplateException("Invalid template: " + name);
        }
        return new Parser((MapModel) model, template).parseTemplate(template, (MapModel) model);
    }
}
