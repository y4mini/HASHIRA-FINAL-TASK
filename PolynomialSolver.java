import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.*;

public class PolynomialSolver {

    // Convert root value from given base to decimal
    private static BigInteger convertToDecimal(String value, int base) {
        return new BigInteger(value, base);
    }

    // Generate polynomial coefficients given roots
    // (monic polynomial: leading coefficient = 1)
    private static BigInteger[] generatePolynomial(BigInteger[] roots) {
        int degree = roots.length;
        BigInteger[] coeffs = new BigInteger[degree + 1];
        Arrays.fill(coeffs, BigInteger.ZERO);
        coeffs[0] = BigInteger.ONE; // x^m term

        for (BigInteger root : roots) {
            for (int i = degree; i >= 1; i--) {
                coeffs[i] = coeffs[i].subtract(coeffs[i - 1].multiply(root));
            }
        }
        return coeffs;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("⚠️ Usage: java PolynomialSolver <input.json>");
            System.exit(1);
        }

        String filePath = args[0];

        try (InputStream is = new FileInputStream(filePath)) {
            JSONObject obj = new JSONObject(new JSONTokener(is));
            JSONObject keys = obj.getJSONObject("keys");

            int n = keys.getInt("n"); // total roots
            int k = keys.getInt("k"); // minimum roots needed
            int m = k - 1;            // degree of polynomial

            // Collect roots
            List<BigInteger> rootsList = new ArrayList<>();

            // Keys may not be ordered; sort numerically
            List<String> numericKeys = new ArrayList<>(obj.keySet());
            numericKeys.remove("keys");
            numericKeys.sort(Comparator.comparingInt(Integer::parseInt));

            int count = 0;
            for (String key : numericKeys) {
                if (count >= m) break; // only take m roots
                JSONObject rootObj = obj.getJSONObject(key);
                int base = Integer.parseInt(rootObj.getString("base"));
                String value = rootObj.getString("value");
                rootsList.add(convertToDecimal(value, base));
                count++;
            }

            BigInteger[] roots = rootsList.toArray(new BigInteger[0]);

            // Generate polynomial coefficients
            BigInteger[] coeffs = generatePolynomial(roots);

            // Print result
            System.out.print("P(x) = ");
            for (int i = 0; i < coeffs.length; i++) {
                int power = coeffs.length - 1 - i;
                if (!coeffs[i].equals(BigInteger.ZERO)) {
                    if (coeffs[i].compareTo(BigInteger.ZERO) > 0 && i > 0) {
                        System.out.print("+");
                    }
                    System.out.print(coeffs[i] + "x^" + power + " ");
                }
            }
            System.out.println();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
