package com.jetdrone.vertx.mods.stomp;

import org.junit.Assert;
import org.junit.Test;

/**
 * User: plenderyou
 * Date: 26/11/2014
 * Time: 17:47
 */
public class FrameEncoderTest {
    static String testString;
    static {
        StringBuilder sb = new StringBuilder();
        sb.append(':').append('\r').append('\n').append('\\');
        testString = sb.toString();

    }

    @Test
    public void testEncoder() throws Exception {


        String s = Frame.escape(testString);
        final byte[] bytes = s.getBytes();

        int i=0;
        Assert.assertEquals( (byte)92, bytes[i++]);
        Assert.assertEquals((byte)99, bytes[i++]);
        Assert.assertEquals((byte)92, bytes[i++]);
        Assert.assertEquals((byte)114, bytes[i++]);
        Assert.assertEquals((byte)92, bytes[i++]);
        Assert.assertEquals((byte)110, bytes[i++]);
        Assert.assertEquals((byte)92, bytes[i++]);
        Assert.assertEquals((byte)92, bytes[i++]);

        Assert.assertEquals(8, bytes.length);

    }

    @Test
    public void testDecoder() throws Exception {
        // Assumption that the encoder works
        String s = Frame.escape(testString);

        final String unescape = Frame.unescape(s);

        System.out.printf(s);
        Assert.assertEquals(testString, unescape);

    }
}
