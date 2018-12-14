/**********************************************************\
|                                                          |
|                          hprose                          |
|                                                          |
| Official WebSite: http://www.hprose.com/                 |
|                   http://www.hprose.org/                 |
|                                                          |
\**********************************************************/
/**********************************************************\
 *                                                        *
 * ValueWriter.java                                       *
 *                                                        *
 * value writer class for Java.                           *
 *                                                        *
 * LastModified: Apr 26, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.serialize;

import hprose.io.HproseTags;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;

public final class ValueWriter implements HproseTags {
    private final static byte[] digits    = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    private final static byte[] DigitTens = { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '1', '1', '1', '1', '1',
            '1', '1', '1', '1', '2', '2', '2', '2', '2', '2', '2', '2', '2', '2', '3', '3', '3', '3', '3', '3', '3',
            '3', '3', '3', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '5', '5', '5', '5', '5', '5', '5', '5',
            '5', '5', '6', '6', '6', '6', '6', '6', '6', '6', '6', '6', '7', '7', '7', '7', '7', '7', '7', '7', '7',
            '7', '8', '8', '8', '8', '8', '8', '8', '8', '8', '8', '9', '9', '9', '9', '9', '9', '9', '9', '9', '9', };

    private final static byte[] DigitOnes = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', };

    private final static byte[] minIntBuf = new byte[] {'-','2','1','4','7','4','8','3','6','4','8'};
    private final static byte[] minLongBuf = new byte[] {'-','9','2','2','3','3','7','2','0','3','6','8','5','4','7','7','5','8','0','8'};

    private final static ThreadLocal<byte[]> buffer = new ThreadLocal<byte[]>() {
        @Override
        protected byte[] initialValue() {
            return new byte[20];
        }
    };
    
    public final static void writeInt(OutputStream stream, int i) throws IOException {
        if ((i >= 0) && (i <= 9)) {
            stream.write((byte)('0' + i));
        }
        else if (i == Integer.MIN_VALUE) {
            stream.write(minIntBuf);
        }
        else {
            byte[] buf = buffer.get();
            int off = 20;
            int q, r;
            byte sign = 0;
            if (i < 0) {
                sign = '-';
                i = -i;
            }
            while (i >= 65536) {
                q = i / 100;
                r = i - (q * 100);
                i = q;
                buf[--off] = DigitOnes[r];
                buf[--off] = DigitTens[r];
            }
            for (;;) {
                q = (i * 52429) >>> (16 + 3);
                r = i - (q * 10);
                buf[--off] = digits[r];
                i = q;
                if (i == 0) break;
            }
            if (sign != 0) {
                buf[--off] = sign;
            }
            stream.write(buf, off, 20 - off);
        }
    }

    public final static void writeInt(OutputStream stream, long i) throws IOException {
        if ((i >= 0) && (i <= 9)) {
            stream.write((byte)('0' + i));
        }
        else if (i == Long.MIN_VALUE) {
            stream.write(minLongBuf);
        }
        else {
            byte[] buf = buffer.get();
            long q;
            int off = 20;
            int q2, r;
            byte sign = 0;
            if (i < 0) {
                sign = '-';
                i = -i;
            }
            while (i >= 65536) {
                q = i / 100;
                r = (int)(i - (q * 100));
                i = q;
                buf[--off] = DigitOnes[r];
                buf[--off] = DigitTens[r];
            }
            int i2 = (int)i;
            while (i2 >= 65536) {
                q2 = i2 / 100;
                r = i2 - (q2 * 100);
                i2 = q2;
                buf[--off] = DigitOnes[r];
                buf[--off] = DigitTens[r];
            }
            for (;;) {
                q2 = (i2 * 52429) >>> (16 + 3);
                r = i2 - (q2 * 10);
                buf[--off] = digits[r];
                i2 = q2;
                if (i2 == 0) break;
            }
            if (sign != 0) {
                buf[--off] = sign;
            }
            stream.write(buf, off, 20 - off);
        }
    }

    public final static void write(OutputStream stream, int i) throws IOException {
        if (i >= 0 && i <= 9) {
            stream.write(i + '0');
        }
        else {
            stream.write(TagInteger);
            writeInt(stream, i);
            stream.write(TagSemicolon);
        }
    }
    
    public final static void write(OutputStream stream, long l) throws IOException {
            if (l >= 0 && l <= 9) {
            stream.write((int)l + '0');
        }
        else {
            stream.write(TagLong);
            writeInt(stream, l);
            stream.write(TagSemicolon);
        }
    }
    
    public final static void write(OutputStream stream, boolean b) throws IOException {
        stream.write(b ? TagTrue : TagFalse);
    }
    
    public final static void write(OutputStream stream, float f) throws IOException {
        if (Float.isNaN(f)) {
            stream.write(TagNaN);
        }
        else if (Float.isInfinite(f)) {
            stream.write(TagInfinity);
            stream.write(f > 0 ? TagPos : TagNeg);
        }
        else {
            stream.write(TagDouble);
            stream.write(getAscii(Float.toString(f)));
            stream.write(TagSemicolon);
        }
    }

    public final static void write(OutputStream stream, double d) throws IOException {
        if (Double.isNaN(d)) {
            stream.write(TagNaN);
        }
        else if (Double.isInfinite(d)) {
            stream.write(TagInfinity);
            stream.write(d > 0 ? TagPos : TagNeg);
        }
        else {
            stream.write(TagDouble);
            stream.write(getAscii(Double.toString(d)));
            stream.write(TagSemicolon);
        }
    }

    public final static void write(OutputStream stream, BigInteger bi) throws IOException {
        stream.write(TagLong);
        stream.write(getAscii(bi.toString()));
        stream.write(TagSemicolon);
    }

    public final static void write(OutputStream stream, BigDecimal bd) throws IOException {
        stream.write(TagDouble);
        stream.write(getAscii(bd.toString()));
        stream.write(TagSemicolon);
    }

    public final static void write(OutputStream stream, char c) throws IOException {
        stream.write(TagUTF8Char);
        if (c < 0x80) {
            stream.write(c);
        }
        else if (c < 0x800) {
            stream.write(0xc0 | (c >>> 6));
            stream.write(0x80 | (c & 0x3f));
        }
        else {
            stream.write(0xe0 | (c >>> 12));
            stream.write(0x80 | ((c >>> 6) & 0x3f));
            stream.write(0x80 | (c & 0x3f));
        }
    }

    final static void write(OutputStream stream, char[] s) throws IOException {
        int length = s.length;
        if (length > 0) {
            writeInt(stream, length);
        }
        stream.write(TagQuote);
        stream.write(new String(s).getBytes("UTF-8"));
/*
        byte[] b = new byte[length * 3];
        int n = 0;
        for (int i = 0; i < length; ++i) {
            int c = 0xffff & s[i];
            if (c < 0x80) {
                b[n++] = (byte)c;
            }
            else if (c < 0x800) {
                b[n++] = (byte)(0xc0 | (c >>> 6));
                b[n++] = (byte)(0x80 | (c & 0x3f));
            }
            else if (c < 0xd800 || c > 0xdfff) {
                b[n++] = (byte)(0xe0 | (c >>> 12));
                b[n++] = (byte)(0x80 | ((c >>> 6) & 0x3f));
                b[n++] = (byte)(0x80 | (c & 0x3f));
            }
            else {
                if (++i < length) {
                    int c2 = 0xffff & s[i];
                    if (c < 0xdc00 && 0xdc00 <= c2 && c2 <= 0xdfff) {
                        c = ((c & 0x03ff) << 10 | (c2 & 0x03ff)) + 0x010000;
                        b[n++] = (byte)(0xf0 | (c >>> 18));
                        b[n++] = (byte)(0x80 | ((c >>> 12) & 0x3f));
                        b[n++] = (byte)(0x80 | ((c >>> 6) & 0x3f));
                        b[n++] = (byte)(0x80 | (c & 0x3f));
                    }
                    else {
                        throw new HproseException("wrong unicode string");
                    }
                }
                else {
                    throw new HproseException("wrong unicode string");
                }
            }
        }
        stream.write(b, 0, n);
*/
        stream.write(TagQuote);
    }

    final static void write(OutputStream stream, String s) throws IOException {
        int length = s.length();
        if (length > 0) {
            writeInt(stream, length);
        }
        stream.write(TagQuote);
        stream.write(s.getBytes("UTF-8"));
/*
        byte[] b = new byte[length * 3];
        int n = 0;
        for (int i = 0; i < length; ++i) {
            int c = 0xffff & s.charAt(i);
            if (c < 0x80) {
                b[n++] = (byte)c;
            }
            else if (c < 0x800) {
                b[n++] = (byte)(0xc0 | (c >>> 6));
                b[n++] = (byte)(0x80 | (c & 0x3f));
            }
            else if (c < 0xd800 || c > 0xdfff) {
                b[n++] = (byte)(0xe0 | (c >>> 12));
                b[n++] = (byte)(0x80 | ((c >>> 6) & 0x3f));
                b[n++] = (byte)(0x80 | (c & 0x3f));
            }
            else {
                if (++i < length) {
                    int c2 = 0xffff & s.charAt(i);
                    if (c < 0xdc00 && 0xdc00 <= c2 && c2 <= 0xdfff) {
                        c = ((c & 0x03ff) << 10 | (c2 & 0x03ff)) + 0x010000;
                        b[n++] = (byte)(0xf0 | (c >>> 18));
                        b[n++] = (byte)(0x80 | ((c >>> 12) & 0x3f));
                        b[n++] = (byte)(0x80 | ((c >>> 6) & 0x3f));
                        b[n++] = (byte)(0x80 | (c & 0x3f));
                    }
                    else {
                        throw new HproseException("wrong unicode string");
                    }
                }
                else {
                    throw new HproseException("wrong unicode string");
                }
            }
        }
        stream.write(b, 0, n);
*/
        stream.write(TagQuote);
    }

    final static void writeDateOfCalendar(OutputStream stream, Calendar calendar) throws IOException {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        stream.write(TagDate);
        stream.write((byte) ('0' + (year / 1000 % 10)));
        stream.write((byte) ('0' + (year / 100 % 10)));
        stream.write((byte) ('0' + (year / 10 % 10)));
        stream.write((byte) ('0' + (year % 10)));
        stream.write((byte) ('0' + (month / 10 % 10)));
        stream.write((byte) ('0' + (month % 10)));
        stream.write((byte) ('0' + (day / 10 % 10)));
        stream.write((byte) ('0' + (day % 10)));
    }

    final static void writeTimeOfCalendar(OutputStream stream, Calendar calendar, boolean ignoreZero, boolean ignoreMillisecond) throws IOException {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int millisecond = calendar.get(Calendar.MILLISECOND);
        if (ignoreZero && hour == 0 && minute == 0 && second == 0 && millisecond == 0) {
            return;
        }
        stream.write(TagTime);
        stream.write((byte) ('0' + (hour / 10 % 10)));
        stream.write((byte) ('0' + (hour % 10)));
        stream.write((byte) ('0' + (minute / 10 % 10)));
        stream.write((byte) ('0' + (minute % 10)));
        stream.write((byte) ('0' + (second / 10 % 10)));
        stream.write((byte) ('0' + (second % 10)));
        if (!ignoreMillisecond && millisecond > 0) {
            stream.write(TagPoint);
            stream.write((byte) ('0' + (millisecond / 100 % 10)));
            stream.write((byte) ('0' + (millisecond / 10 % 10)));
            stream.write((byte) ('0' + (millisecond % 10)));
        }
    }

    final static byte[] getAscii(String s) {
        int size = s.length();
        byte[] b = new byte[size--];
        for (; size >= 0; --size) {
            b[size] = (byte) s.charAt(size);
        }
        return b;
    }
}
