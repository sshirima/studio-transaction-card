package transactioncard.currency;

import com.example.transactioncard.object.Currencies;

public class CreateYahooQuery {
	
public static String URL_FORMAT_Brackets = "(%s)";
	
	private static String QUOTE = "%22";
	
	private static String SPACE = "%20";
	
	private static String CODE_USD = Currencies.CURRENCY_CODE_US;
	
	private static String URL_YAHOO_DOMAIN = "https://query.yahooapis.com/v1/public/yql?q="
            + "select%20*%20"
            + "from%20yahoo.finance.xchange%20"
            + "where%20pair%20in%20";
	private static String URL_FORMAT_CODE = "%s%s%s";
	
	private static String URL_YAHOO_QUERY_END ="&env=store://datatables.org/alltableswithkeys ";
	
	public CreateYahooQuery (){}
	
	public String getYahooQuery(String[] codeList){
		return URL_YAHOO_DOMAIN+getCurrencyCodes(codeList)+URL_YAHOO_QUERY_END;
	}
	
	private String getCurrencyCodes(String[] codeList){
		String currencyCode ="";
		for (int i = 0; i < codeList.length; i++) {
			if (i == 0){
				currencyCode+= getCode(codeList[i]);
			}else {
				currencyCode+=","+SPACE+getCode(codeList[i]);
			}
			
		}
		return String.format(URL_FORMAT_Brackets, currencyCode);
	}
	
	private static String getCode(String code){
		return String.format(URL_FORMAT_CODE, QUOTE,CODE_USD+code, QUOTE);
	}

}
