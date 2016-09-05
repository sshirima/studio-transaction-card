package transactioncard.currency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.example.transactioncard.database.ConstsDatabase;

public class YahooCurrencyConvertor implements YahooConvertorInterface {

	private final String STR_Classname = YahooCurrencyConvertor.class.getName();

	@Override
	public float getCurrencyRates(String currencyFrom, String currencyTo)
			throws Exception {
		// TODO Auto-generated method stub
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet("http://quote.yahoo.com/d/quotes.csv?s="
				+ currencyFrom + currencyTo + "=X&f=l1&e=.csv");
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String responseBody = httpclient.execute(httpGet, responseHandler);
		httpclient.getConnectionManager().shutdown();
		return Float.parseFloat(responseBody);
	}

	@Override
	public String getCurrencyRates(String currencyFrom, String[] currencyTo)
			throws Exception {
		// TODO Auto-generated method stub
		String methodName = "";
		String operation = "";
		ConstsDatabase.logINFO(STR_Classname, methodName, operation);
		/*
		 * Convert yahoo query
		 */
		CreateYahooQuery createYahooQuery = new CreateYahooQuery();

		String yahooQuery = createYahooQuery.getYahooQuery(currencyTo);
		/*
		 * Create httpclient instance for server connection
		 */
		String responseBody = getXMLresultFromURL(yahooQuery);

		return responseBody;
	}

	private String getXMLresultFromURL(String url) throws Exception {

        String returnXML = null;

        try {
            URL yahoooURL = new URL(url);
            URLConnection yahooURLConnection = yahoooURL.openConnection();
            yahooURLConnection.connect();
            /*
             * Check connection to the server
             */
            int readTimeout = yahooURLConnection.getReadTimeout();
            int connectionTimeout = yahooURLConnection.getConnectTimeout();
            System.out.println("Read time out: " + readTimeout);
            System.out.println("Connection time out: " + connectionTimeout);

            StringBuilder response;
            
            InputStreamReader inputStreamReader = new InputStreamReader( yahooURLConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            
           
                String inputLine;
                response = new StringBuilder();
                while ((inputLine = bufferedReader.readLine()) != null) {
                    System.out.println(inputLine);
                    response.append(inputLine);
                }
                returnXML = response.toString();
        } catch (MalformedURLException malformedURLException) {
            System.out.println(" MalformedURLException");
            throw malformedURLException;
        } catch (IOException IOException) {
            System.out.println(" Open connection failed!!!");
            throw IOException;
        }
        return returnXML;
    }
}
