package net.sf.sockettest.swing;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Defines all possible charsets to choose from. Used for converting the raw bytes from the network socket to a String
 * when receiving data. Also used to convert the entered String to raw bytes when sending data.
 * 
 * @author Jochen Meierhofer
 */
public enum Encoding {

    US_ASCII(StandardCharsets.US_ASCII),
    UTF_8(StandardCharsets.UTF_8),

    UTF_16(StandardCharsets.UTF_16),
    UTF_16BE(StandardCharsets.UTF_16BE),
    UTF_16LE(StandardCharsets.UTF_16LE);

    private final Charset charset;

    private Encoding(Charset charset) {
        this.charset = charset;
    }

    public Charset getCharset() {
        return charset;
    }
}
