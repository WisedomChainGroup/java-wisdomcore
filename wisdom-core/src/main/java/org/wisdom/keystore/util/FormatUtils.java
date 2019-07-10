/*
 * Copyright (c) [2018]
 * This file is part of the java-wisdomcore
 *
 * The java-wisdomcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The java-wisdomcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the java-wisdomcore. If not, see <http://www.gnu.org/licenses/>.
 */

package org.wisdom.keystore.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;

public class FormatUtils {

    /**
     * Gets a default decimal format that should be used for formatting decimal values.
     *
     * @return A default decimal format.
     */
    public static DecimalFormat getDefaultDecimalFormat() {
        final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        final DecimalFormat format = new DecimalFormat("#0.000", decimalFormatSymbols);
        format.setGroupingUsed(false);
        return format;
    }

    /**
     * Gets a decimal format that with the desired number of decimal places.
     *
     * @param decimalPlaces The number of decimal places.
     * @return The desired decimal format.
     */
    public static DecimalFormat getDecimalFormat(final int decimalPlaces) {
        if (decimalPlaces < 0) {
            throw new IllegalArgumentException("decimalPlaces must be non-negative");
        }

        final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        final StringBuilder builder = new StringBuilder();
        builder.append("#0");

        if (decimalPlaces > 0) {
            builder.append('.');
            final char[] zeros = new char[decimalPlaces];
            Arrays.fill(zeros, '0');
            builder.append(zeros);
        }

        final DecimalFormat format = new DecimalFormat(builder.toString(), decimalFormatSymbols);
        format.setGroupingUsed(false);
        return format;
    }

    /**
     * Formats a double value with a given number of decimal places.
     *
     * @param value The value to format.
     * @param decimalPlaces The desired number of decimal places.
     * @return The formatted string.
     */
    public static String format(final double value, final int decimalPlaces) {
        final DecimalFormat formatter = getDecimalFormat(decimalPlaces);
        return formatter.format(value);
    }
}