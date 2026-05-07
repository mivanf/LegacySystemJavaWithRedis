package com.qris.payment.infrastructure.qris;

import com.qris.payment.exception.InvalidQrisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Parser for QRIS (Quick Response Code Indonesian Standard) payloads.
 * QRIS follows the EMVCo QR Code Specification using TLV (Tag-Length-Value) format.
 *
 * Common QRIS Tags:
 * - 00: Payload Format Indicator
 * - 01: Point of Initiation Method
 * - 26-51: Merchant Account Information
 * - 52: Merchant Category Code (MCC)
 * - 53: Transaction Currency
 * - 54: Transaction Amount
 * - 58: Country Code
 * - 59: Merchant Name
 * - 60: Merchant City
 * - 61: Postal Code
 * - 62: Additional Data Field
 * - 63: CRC
 */
@Component
public class QrisParser {

    private static final Logger log = LoggerFactory.getLogger(QrisParser.class);

    /**
     * Parse a QRIS TLV payload string into a map of tag -> value.
     */
    public Map<String, String> parse(String qrisPayload) {
        if (qrisPayload == null || qrisPayload.isBlank()) {
            throw new InvalidQrisException("QRIS payload is empty");
        }

        // URL decode if needed
        String decoded;
        try {
            decoded = URLDecoder.decode(qrisPayload.trim(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            decoded = qrisPayload.trim();
        }

        Map<String, String> result = new HashMap<>();
        int index = 0;

        try {
            while (index < decoded.length()) {
                if (index + 4 > decoded.length()) break;

                String tag = decoded.substring(index, index + 2);
                index += 2;

                int length = Integer.parseInt(decoded.substring(index, index + 2));
                index += 2;

                if (index + length > decoded.length()) {
                    throw new InvalidQrisException("Invalid TLV: length exceeds payload for tag " + tag);
                }

                String value = decoded.substring(index, index + length);
                index += length;

                result.put(tag, value);
            }
        } catch (NumberFormatException e) {
            throw new InvalidQrisException("Invalid QRIS payload format: cannot parse TLV length");
        }

        if (result.isEmpty()) {
            throw new InvalidQrisException("No valid TLV tags found in QRIS payload");
        }

        log.debug("Parsed QRIS payload with {} tags", result.size());
        return result;
    }

    /**
     * Extract merchant ID from parsed QRIS data.
     * Looks in merchant account information tags (26-51).
     */
    public String extractMerchantId(Map<String, String> parsed) {
        // Check tags 26-51 for merchant account info
        for (int i = 26; i <= 51; i++) {
            String tag = String.format("%02d", i);
            if (parsed.containsKey(tag)) {
                String subData = parsed.get(tag);
                // Parse sub-TLV to find merchant ID (sub-tag 02 or 03)
                Map<String, String> subParsed = parseSubTlv(subData);
                if (subParsed.containsKey("02")) {
                    return subParsed.get("02");
                }
                if (subParsed.containsKey("03")) {
                    return subParsed.get("03");
                }
                // If no sub-tag found, use the whole value as merchant ID
                return subData.length() > 20 ? subData.substring(0, 20) : subData;
            }
        }
        return "UNKNOWN_MERCHANT";
    }

    /**
     * Extract terminal ID from merchant account sub-TLV.
     */
    public String extractTerminalId(Map<String, String> parsed) {
        for (int i = 26; i <= 51; i++) {
            String tag = String.format("%02d", i);
            if (parsed.containsKey(tag)) {
                Map<String, String> subParsed = parseSubTlv(parsed.get(tag));
                if (subParsed.containsKey("07")) {
                    return subParsed.get("07");
                }
            }
        }
        return null;
    }

    /**
     * Extract merchant name from tag 59.
     */
    public String extractMerchantName(Map<String, String> parsed) {
        return parsed.getOrDefault("59", "Unknown Merchant");
    }

    /**
     * Extract city from tag 60.
     */
    public String extractCity(Map<String, String> parsed) {
        return parsed.getOrDefault("60", "Unknown City");
    }

    /**
     * Extract MCC (Merchant Category Code) from tag 52.
     */
    public String extractMcc(Map<String, String> parsed) {
        return parsed.getOrDefault("52", "0000");
    }

    /**
     * Extract fixed amount from tag 54 (if present, means static QR).
     */
    public BigDecimal extractAmount(Map<String, String> parsed) {
        String amount = parsed.get("54");
        if (amount != null && !amount.isBlank()) {
            try {
                return new BigDecimal(amount);
            } catch (NumberFormatException e) {
                return BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Parse sub-TLV data within a tag's value.
     */
    private Map<String, String> parseSubTlv(String data) {
        Map<String, String> result = new HashMap<>();
        int index = 0;

        try {
            while (index < data.length() && index + 4 <= data.length()) {
                String tag = data.substring(index, index + 2);
                index += 2;

                int length = Integer.parseInt(data.substring(index, index + 2));
                index += 2;

                if (index + length > data.length()) break;

                String value = data.substring(index, index + length);
                index += length;

                result.put(tag, value);
            }
        } catch (NumberFormatException e) {
            // Sub-TLV parsing failed, return what we have
        }

        return result;
    }
}
