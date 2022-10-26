package com.github.leeonky.dal.extensions.basic;

import com.github.leeonky.dal.DAL;
import com.github.leeonky.dal.runtime.ConditionalChecker;
import com.github.leeonky.dal.runtime.ExpectActual;
import com.github.leeonky.dal.runtime.Extension;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static com.github.leeonky.dal.extensions.basic.BinaryExtension.readAll;
import static com.github.leeonky.dal.extensions.basic.FileGroup.register;
import static com.github.leeonky.dal.extensions.basic.StringExtension.StaticMethods.string;
import static com.github.leeonky.dal.runtime.ConditionalChecker.matchTypeChecker;
import static java.util.Arrays.asList;

public class StringExtension implements Extension {
    private static final List<String> SPLITTERS = asList("\r\n", "\n\r", "\n", "\r");

    @Override
    public void extend(DAL dal) {
        dal.getRuntimeContextBuilder()
                .registerStaticMethodExtension(StaticMethods.class)
                .registerEqualsChecker(CharSequence.class, new CharSequenceChecker())
                .registerMatchesChecker(CharSequence.class, matchTypeChecker(Number.class, String.class)
                        .and(matchTypeChecker(Boolean.class, String.class))
                        .and(new CharSequenceMatcherChecker()))
        ;

        register("txt", inputStream -> string(readAll(inputStream)));
        register("TXT", inputStream -> string(readAll(inputStream)));
    }

    public static class StaticMethods {
        public static String string(byte[] data) {
            return new String(data);
        }

        public static List<String> lines(byte[] content) {
            return lines(new String(content));
        }

        public static List<String> lines(String content) {
            return lines(content, new ArrayList<>());
        }

        private static List<String> lines(String content, List<String> list) {
            for (String str : SPLITTERS) {
                int index = content.indexOf(str);
                if (index != -1) {
                    lines(content.substring(0, index), list);
                    return lines(content.substring(index + str.length()), list);
                }
            }
            list.add(content);
            return list;
        }

        public static byte[] encode(String content, String encoder) throws UnsupportedEncodingException {
            return content.getBytes(encoder);
        }

        public static byte[] utf8(String content) {
            return content.getBytes(StandardCharsets.UTF_8);
        }

        public static byte[] base64(String encoded) {
            return Base64.getDecoder().decode(encoded);
        }

        public static byte[] ascii(String content) {
            return content.getBytes(StandardCharsets.US_ASCII);
        }

        public static byte[] iso8859_1(String content) {
            return content.getBytes(StandardCharsets.ISO_8859_1);
        }

        public static byte[] gbk(String content) throws UnsupportedEncodingException {
            return encode(content, "gbk");
        }
    }

    private static class CharSequenceChecker implements ConditionalChecker {

        @Override
        public boolean failed(ExpectActual expectActual) {
            return !convertToString(expectActual.getExpected().getInstance())
                    .equals(convertToString(expectActual.getActual().getInstance()));
        }

        @Override
        public String message(ExpectActual expectActual) {
            String message = expectActual.verificationMessage(getPrefix());
            if (expectActual.getActual().isNull())
                return message;
            String detail = new Diff(convertToString(expectActual.getExpected().getInstance()),
                    convertToString(expectActual.getActual().getInstance())).detail();
            return detail.isEmpty() ? message : message + "\n\n" + detail;
        }

        protected String getPrefix() {
            return "Expected to be equal to: ";
        }

        protected String convertToString(Object object) {
            return object == null ? null : ((CharSequence) object).toString();
        }
    }

    private static class CharSequenceMatcherChecker extends CharSequenceChecker {

        @Override
        public boolean failed(ExpectActual expectActual) {
            return !convertToString(expectActual.getExpected().getInstance())
                    .equals(convertToString(expectActual.getActual().convert(String.class).getInstance()));
        }

        @Override
        protected String getPrefix() {
            return "Expected to match: ";
        }

        @Override
        protected String convertToString(Object object) {
            return object == null ? null : object.toString();
        }
    }
}