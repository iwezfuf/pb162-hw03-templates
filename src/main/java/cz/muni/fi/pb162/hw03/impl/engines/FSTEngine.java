package cz.muni.fi.pb162.hw03.impl.engines;

import cz.muni.fi.pb162.hw03.impl.Parser;
import cz.muni.fi.pb162.hw03.impl.Template;
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
            System.out.println(path);
            content = Files.readString(path);
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
        String name = file.getFileName().toString().substring(0, file.getFileName().toString().length() - ext.length() - 1);
        templates.put(name, new Template(name, content));
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
        loadTemplate(file, cs, "");
        String result = evaluateTemplate(file.toString(), model);
        Path path = Paths.get(file.toString());
        try {
            Files.writeString(path, result);
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void writeTemplates(TemplateModel model, Path outDir, Charset cs) {
        File directory = new File(outDir.toString());
        File[] files = directory.listFiles();
        if (files == null) {
            throw new IllegalArgumentException();
        }
        for (File file : files) {
            if (file.isFile()) {
                writeTemplate(file.getName(), model,  Path.of(file.getPath()), cs);
            }
        }
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
