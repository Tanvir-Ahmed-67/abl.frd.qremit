package abl.frd.qremit.converter.helper;

public class NumberToWords {
    private static final String[] belowTwenty = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven",
            "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    };

    private static final String[] tens = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };

    private static final String[] bigNumbers = {
            "", "Thousand", "Lakh", "Crore"
    };

    public static String convertNumberToWords(long number) {
        if (number == 0) {
            return "Zero";
        }
        return convert(number);
    }

    private static String convert(long number) {
        String words = "";

        if (number < 20) {
            words = belowTwenty[(int) number];
        } else if (number < 100) {
            words = tens[(int) (number / 10)] + " " + belowTwenty[(int) (number % 10)];
        } else if (number < 1000) {
            words = belowTwenty[(int) (number / 100)] + " Hundred";
            if (number % 100 > 0) {
                words += " " + convert(number % 100);
            }
        } else {
            if (number >= 10000000) { // Crore
                words = convert(number / 10000000) + " Crore";
                if (number % 10000000 > 0) {
                    words += " " + convert(number % 10000000);
                }
            } else if (number >= 100000) { // Lakh
                words = convert(number / 100000) + " Lakh";
                if (number % 100000 > 0) {
                    words += " " + convert(number % 100000);
                }
            } else if (number >= 1000) { // Thousand
                words = convert(number / 1000) + " Thousand";
                if (number % 1000 > 0) {
                    words += " " + convert(number % 1000);
                }
            }
        }

        return words.trim();
    }

    public static String convertDoubleToWords(double amount) {
        // Extract the whole number part (Taka)
        long taka = (long) amount;

        // Extract the fractional part (Poysha) and round to 2 decimal places
        long poysha = Math.round((amount - taka) * 100);

        // Convert both parts to words
        String takaInWords = convertNumberToWords(taka);
        String poyshaInWords = poysha > 0 ? convertNumberToWords(poysha) : "";

        // Combine Taka and Poysha in the result
        String currencyInWords = takaInWords + " Taka";
        if (poysha > 0) {
            currencyInWords += " and " + poyshaInWords + " Poysha";
        }
        currencyInWords = currencyInWords+" Only.";

        return currencyInWords;
    }
}
