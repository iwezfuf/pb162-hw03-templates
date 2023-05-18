package cz.muni.fi.pb162.hw03.impl.engines;
import cz.muni.fi.pb162.hw03.impl.TemplateParser;
import cz.muni.fi.pb162.hw03.impl.Template;
import cz.muni.fi.pb162.hw03.impl.TemplateValidator;
import cz.muni.fi.pb162.hw03.impl.model.MapModel;
import cz.muni.fi.pb162.hw03.template.FSTemplateEngine;
import cz.muni.fi.pb162.hw03.template.TemplateException;
import cz.muni.fi.pb162.hw03.template.model.TemplateModel;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FSTEngine implements FSTemplateEngine {
    private final Map<String, Template> templates = new HashMap<>();
    @Override
    public void loadTemplate(Path file, Charset cs, String ext) {
        Path path = Paths.get(file.toString());
        String content;
        try {
            content = Files.readString(path, cs);
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
        String fileName = file.getFileName().toString();
        String name = fileName.substring(0, fileName.length() - ext.length() - 1);
        loadTemplate(name, content);
    }

    @Override
    public void loadTemplateDir(Path inDir, Charset cs, String ext) {
        File directory = new File(inDir.toString());
        File[] files = directory.listFiles();
        if (files == null) {
            throw new IllegalArgumentException();
        }
        for (File file : files) {
            if (file.isFile()) {
                loadTemplate(Path.of(file.getPath()), cs, ext);
            }
        }
    }

    @Override
    public void writeTemplate(String name, TemplateModel model, Path file, Charset cs) {
        Path path = Paths.get(file.toString());
        try {
            Files.writeString(path, evaluateTemplate(name, model), cs);
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void writeTemplates(TemplateModel model, Path outDir, Charset cs) {
        for (Template template : templates.values()) {
            try {
                Files.createFile(Path.of(outDir.toString(), template.name()));
            } catch (IOException e) {
                throw new IllegalArgumentException("Cannot create file " + template.name() + " in dir " + outDir);
            }
            writeTemplate(template.name(), model, Path.of(outDir.toString(), template.name()), cs);
        }
    }

    @Override
    public void loadTemplate(String name, String text) {
        TemplateValidator templateValidator = new TemplateValidator(text);
        if (!templateValidator.validateTemplate()) {
            throw new TemplateException("Invalid template");
        }
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
        return TemplateParser.parseTemplate(template, (MapModel) model);
    }
}
