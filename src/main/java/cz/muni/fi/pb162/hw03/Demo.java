package cz.muni.fi.pb162.hw03;

import com.beust.jcommander.converters.CommaParameterSplitter;
import cz.muni.fi.pb162.hw03.impl.engines.FSTEngine;
import cz.muni.fi.pb162.hw03.impl.model.MapModel;
import cz.muni.fi.pb162.hw03.impl.parser.tokens.Tokenizer;

public class Demo {
    public static void main(String[] args) {
        FSTEngine fstEngine = new FSTEngine();
        fstEngine.loadTemplate("test", "test");
    }
}
