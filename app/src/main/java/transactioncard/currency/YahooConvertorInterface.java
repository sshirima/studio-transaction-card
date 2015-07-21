package transactioncard.currency;

public interface YahooConvertorInterface {
	public float getCurrencyRates(String currencyFrom, String currencyTo) throws Exception;
	
	public String getCurrencyRates(String currencyFrom, String[] currencyTo) throws Exception;
}
