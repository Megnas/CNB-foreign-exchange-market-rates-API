package Megnas.CNB_foreign_exchange_market_rates_API;

public record ExchangeDate(int day, int month, int year) {
    public String GetTextFormat() {
        return String.format("%02d", day) + "." + String.format("%02d", month) + "." + String.format("%04d", year);
    }

    public String GetAsParameters() {
        return "?date=" + GetTextFormat();
    }
}
