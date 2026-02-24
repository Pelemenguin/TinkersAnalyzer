package pelemenguin.tinkersanalyzer.client.util;

import net.minecraft.util.Mth;

/**
 * A util class for defining how numbers are displayed in Analyzer Graphs
 * @param number The {@code float} number to display
 * @return       A {@link String}ified number 
 */
@FunctionalInterface
public interface NumberFormatter {

    public String formatNumber(float number);

    public static final NumberFormatter PLAIN = (f) -> Float.toString(f);
    public static final NumberFormatter FLUID_IN_BUCKET = (f) -> {
        if (f < 10_000) {
            return Mth.floor(f) + "mB";
        } else if (f < 10_000_000) {
            return "%.5gB".formatted(f / 1_000);
        } else {
            return "%.5gKB".formatted(f / 1_000_000);
        }
    };

    public static NumberFormatter integer(int threshold) {
        int precision = Integer.toString(threshold).length();
        String formatter = "%%.%de".formatted(precision - 4);
        return (f) -> f < threshold ? Integer.toString((int) f) : formatter.formatted(f);
    }

}
