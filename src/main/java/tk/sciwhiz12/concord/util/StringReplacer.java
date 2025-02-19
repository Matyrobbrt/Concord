/*
 * Concord - Copyright (c) 2020-2022 SciWhiz12
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package tk.sciwhiz12.concord.util;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.ParametersAreNonnullByDefault;

public final class StringReplacer {

    private final Map<String, String> regex = new HashMap<>();

    public void add(String oldChar, String replacement) {
        this.regex.put(oldChar, replacement);
    }

    public void remove(String oldChar) {
        regex.remove(oldChar);
    }

    public String replace(String toFormat) {
        var str = toFormat;
        for (final var entry : regex.entrySet()) {
            if (str.contains(entry.getKey())) {
                str = fastReplace(str, entry.getKey(), entry.getValue());
            }
        }
        return str;
    }

    public String replace(CharSequence toFormat) {
        return replace(toFormat.toString());
    }

    @ParametersAreNonnullByDefault
    public static String fastReplace(final String str, final String searchChars, String replaceChars) {
        if ("".equals(str) || "".equals(searchChars) || searchChars.equals(replaceChars)) { return str; }
        final int strLength = str.length();
        final int searchCharsLength = searchChars.length();
        StringBuilder buf = new StringBuilder(str);
        boolean modified = false;
        for (int i = 0; i < strLength; i++) {
            int start = buf.indexOf(searchChars, i);

            if (start == -1) {
                if (i == 0) { return str; }
                return buf.toString();
            }
            buf = buf.replace(start, start + searchCharsLength, replaceChars);
            modified = true;

        }
        if (!modified) {
            return str;
        } else {
            return buf.toString();
        }
    }
}
