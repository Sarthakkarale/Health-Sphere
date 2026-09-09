package com.healthsphere.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class RazorpayService {

    private static final String BASE_URL = "https://api.razorpay.com/v1";
    private static final String KEY_ID = "rzp_test_TXgBBan9AQFSyf";
    private static final String KEY_SECRET = "Gbmwc3PLNjX0hjlBqd9TX46o";
    private static final String X_ACCOUNT_NUMBER = "233445566778899";

    private final HttpClient http = com.healthsphere.util.SharedHttpClient.getInstance();

    /**
     * Basics auth.
     *
     * @return the string value
     */
    private String basicAuth() {
        String creds = "rzp_test_TXgBBan9AQFSyf" + ":" + "Gbmwc3PLNjX0hjlBqd9TX46o";
        return "Basic " + Base64.getEncoder().encodeToString(creds.getBytes());
    }

    /**
     * Step 1: Create an order — amountRupees is in ₹ (e.g. 99), converts to paise
     */
    
    public JsonObject createOrder(int amountRupees, String receipt) throws Exception {

        int amountPaise = amountRupees * 100;

        String body = """
                {
                  "amount": %d,
                  "currency": "INR",
                  "receipt": "%s",
                  "notes": { "plan": "Premium" }
                }
                """.formatted(amountPaise, receipt);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/orders"))
                .header("Authorization", basicAuth())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("Create Order response: " + res.body()); // debug
        return JsonParser.parseString(res.body()).getAsJsonObject();
    }


    /**
     * Creates qr code.
     *
     * @param orderId the order ID
     * @param amountPaise the amount in paise
     * @return the JsonObject object
     */
    public JsonObject createQrCode(String orderId, int amountPaise) throws Exception {
        String body = """
                {
                  "type": "upi_qr",
                  "name": "Payment QR",
                  "usage": "single_use",
                  "fixed_amount": true,
                  "payment_amount": %d,
                  "description": "Payment for order %s",
                  "close_by": %d
                }
                """.formatted(amountPaise, orderId,
                (System.currentTimeMillis() / 1000) + 1800); // 15 min expiry

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/payments/qr_codes"))
                .header("Authorization", basicAuth())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("Create QR response: " + res.body()); // debug
        return JsonParser.parseString(res.body()).getAsJsonObject();
    }


    /**
     * Gets qr payment status.
     *
     * @param qrCodeId the QR code ID
     * @return the string value
     */
    public String getQrPaymentStatus(String qrCodeId) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/payments/qr_codes/" + qrCodeId + "/payments"))
                .header("Authorization", basicAuth())
                .GET()
                .build();

        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("QR poll response: " + res.body()); // debug

        JsonObject json = JsonParser.parseString(res.body()).getAsJsonObject();
        int count = json.get("count").getAsInt();

        if (count > 0) {
            JsonObject payment = json.getAsJsonArray("items")
                    .get(0).getAsJsonObject();
            return payment.get("status").getAsString(); // "captured" = success
        }
        return "pending";
    }


    /**
     * Creates payment link.
     *
     * @param orderId the order ID
     * @param amountPaise the amount in paise
     * @param description the description
     * @return the string value
     */
    public String createPaymentLink(String orderId, int amountPaise, String description) throws Exception {
        String body = """
                {
                  "amount": %d,
                  "currency": "INR",
                  "description": "%s",
                  "reference_id": "%s",
                  "expire_by": %d,
                  "reminder_enable": false,
                  "notify": { "sms": false, "email": false }
                }
                """.formatted(amountPaise, description, orderId,
                (System.currentTimeMillis() / 1000) + 1200); // 20 min expiry to avoid clock drift issues

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/payment_links"))
                .header("Authorization", basicAuth())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("Payment Link response: " + res.body());

        JsonObject json = JsonParser.parseString(res.body()).getAsJsonObject();
        if (json.has("short_url")) {
            return json.get("short_url").getAsString(); // e.g. https://rzp.io/i/xxxx
        }
        throw new RuntimeException("Payment link creation failed: " + res.body());
    }

    /**
     * Gets payment link status.
     *
     * @param paymentLinkId the payment link ID
     * @return the string value
     */
    public String getPaymentLinkStatus(String paymentLinkId) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/payment_links/" + paymentLinkId))
                .header("Authorization", basicAuth())
                .GET()
                .build();

        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        JsonObject json = JsonParser.parseString(res.body()).getAsJsonObject();
        System.out.println("Payment link poll: " + res.body());
        return json.get("status").getAsString(); // "created" | "paid"
    }


    /**
     * Creates payment link full.
     *
     * @param orderId the order ID
     * @param amountPaise the amount in paise
     * @param description the description
     * @return the JsonObject object
     */
    public JsonObject createPaymentLinkFull(String orderId, int amountPaise, String description) throws Exception {
        String body = """
                {
                  "amount": %d,
                  "currency": "INR",
                  "description": "%s",
                  "reference_id": "%s",
                  "expire_by": %d,
                  "reminder_enable": false,
                  "notify": { "sms": false, "email": false }
                }
                """.formatted(amountPaise, description, orderId,
                (System.currentTimeMillis() / 1000) + 1200); // 20 min expiry to avoid clock drift issues

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/payment_links"))
                .header("Authorization", basicAuth())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("Payment Link full response: " + res.body());
        return JsonParser.parseString(res.body()).getAsJsonObject();
    }

    /**
     * Creates a payout using RazorpayX API.
     *
     * @param amountRupees the amount in rupees
     * @param mode either "UPI" or "IMPS"
     * @param name beneficiary name
     * @param info the payment details (UPI ID or Account Number)
     * @param ifsc the IFSC code (only for bank account)
     * @return the JsonObject response
     */
    public JsonObject createPayout(int amountRupees, String mode, String name, String info, String ifsc) throws Exception {
        int amountPaise = amountRupees * 100;
        String idempotencyKey = java.util.UUID.randomUUID().toString();

        String fundAccountJson;
        if ("UPI".equalsIgnoreCase(mode)) {
            fundAccountJson = """
                "fund_account": {
                  "account_type": "vpa",
                  "vpa": {
                    "address": "%s"
                  }
                }
                """.formatted(info);
        } else {
            fundAccountJson = """
                "fund_account": {
                  "account_type": "bank_account",
                  "bank_account": {
                    "name": "%s",
                    "ifsc": "%s",
                    "account_number": "%s"
                  }
                }
                """.formatted(name, ifsc, info);
        }

        String sourceAccount = X_ACCOUNT_NUMBER;

        String body = """
                {
                  "account_number": "%s",
                  "amount": %d,
                  "currency": "INR",
                  "mode": "%s",
                  "purpose": "payout",
                  %s
                }
                """.formatted(sourceAccount, amountPaise, "UPI".equalsIgnoreCase(mode) ? "UPI" : "IMPS", fundAccountJson);

        System.out.println("Creating payout body: " + body);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://api.razorpay.com/v1/payouts"))
                .header("Authorization", basicAuth())
                .header("Content-Type", "application/json")
                .header("X-Payout-Idempotency", idempotencyKey)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("Create Payout response: " + res.body());
        return JsonParser.parseString(res.body()).getAsJsonObject();
    }

}