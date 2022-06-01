package com.github.sebastian4j.spaceship.utils;

import java.io.*;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public final class BytesUtils {
    static byte[] dec(byte[] input) throws DataFormatException {
        var dec = new Inflater();
        dec.setInput(input);
        var baos = new ByteArrayOutputStream();
        var buff = new byte[1024];
        while (!dec.finished()) {
            var rc = dec.inflate(buff);
            if (rc > 0) {
                baos.write(buff, 0, rc);
            }
        }
        dec.end();
        return baos.toByteArray();
    }

    static ByteArrayOutputStream comp(ByteArrayOutputStream baos) {
        var def = new Deflater(Deflater.BEST_COMPRESSION);
        def.setInput(baos.toByteArray());
        def.finish();
        var bao = new ByteArrayOutputStream();
        var buff = new byte[1024];
        while (!def.finished()) {
            int cs = def.deflate(buff);
            if (cs > 0) {
                bao.write(buff, 0, cs);
            }
        }
        def.end();
        return bao;
    }

    public static byte[] bytes(Object datos) throws IOException {
        var baos = new ByteArrayOutputStream();
        var o = new ObjectOutputStream(baos);
        o.writeObject(datos);
        return comp(baos).toByteArray();
    }

    public static Object object(byte[] recuperados) throws IOException, ClassNotFoundException, DataFormatException {
        var bais = new ByteArrayInputStream(dec(recuperados));
        var ois = new ObjectInputStream(bais);
        return ois.readObject();
    }
}
