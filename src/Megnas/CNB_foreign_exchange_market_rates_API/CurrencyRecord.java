package Megnas.CNB_foreign_exchange_market_rates_API;

public record CurrencyRecord(String country, String name, double quantity, String code, double value) {

    public double GetExchangeRate() {
        return value / quantity;
    }

    public String GetTextFormat() {
        return country + "|" + name + "|" + quantity + "|" + code + "|" + value;
    }

    @Override
    public String toString() {
        return "CurrencyRecord{" +
                "country='" + country + '\'' +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", code='" + code + '\'' +
                ", value=" + value +
                '}';
    }
}
