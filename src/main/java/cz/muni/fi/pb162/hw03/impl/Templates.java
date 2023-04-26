package cz.muni.fi.pb162.hw03.impl;

import cz.muni.fi.pb162.hw03.template.FSTemplateEngine;
import cz.muni.fi.pb162.hw03.template.model.TemplateModel;

import java.util.Map;

/**
 * Factory for template engine classes
 */
public final class Templates {
    private Templates() {
        // intentionally private
    }

    /**
     * Crates new {@link FSTemplateEngine} engine
     * @return template engine
     */
    public static FSTemplateEngine engine() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Crates new {@link TemplateModel} from given map
     * @param map data map
     * @return template model
     */
    public static TemplateModel modelOf(Map<String, Object> map) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
