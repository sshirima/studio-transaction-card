package com.example.transactioncard.object;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import com.example.transactioncard.ImportAndExport;
import com.example.transactioncard.Settings;
import com.example.transactioncard.database.TransactionTable;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class DataTransfer {

	private Context context;
	public DataTransfer(Context context){
		this.context = context;
	}
	
	public static final File DIRECTORY = new File(
			Environment.getExternalStorageDirectory(), "TransactionCard");
	
	public static String getFileDirectory(){
		outFile = new File(DIRECTORY, ImportAndExport.fileName);
		return 	outFile.getAbsolutePath();
	}
	
	private static File outFile ;
	public boolean exportDataToCSV(String filename){
		boolean returnCode = false;
		String csvHeader = "";
		String csvValues = "";
		try {
			TransactionTable transactionTable = new TransactionTable(context);
			transactionTable.open();
			
			if (!DIRECTORY.exists()) {
				DIRECTORY.mkdirs();
			}
			
			outFile = new File(DIRECTORY, filename);
			FileWriter fileWriter = new FileWriter(outFile);
			BufferedWriter out = new BufferedWriter(fileWriter);
			
			ArrayList<Transaction> transactionList = transactionTable.getTransactionsAll();
			int listSize = transactionList.size();
			csvHeader += "\"" + "Id" + "\",";
			csvHeader += "\"" + "Amount" + "\",";
			csvHeader += "\"" + "CurrencyCashFlow" + "\",";
			csvHeader += "\"" + "Description" + "\",";
			csvHeader += "\"" + "Account name" + "\",";
			csvHeader += "\"" + "Category" + "\",";
			csvHeader += "\"" + "Date" + "\",";
			csvHeader += "\"" + "Time" + "\",";
			csvHeader += "\n";
			
			if (transactionList != null) {
				out.write(csvHeader);
				for (int i = 0; i < listSize; i++) {
					Transaction transaction = transactionList.get(i);
					String id = Long.toString(transaction.getTransactionId());
					String amount = Double.toString(transaction.getAmountInDefaultCurrency(context));
					String currency = Settings.getDefaultCurrency(context);
					String description = transaction.getDescriptionName();
					String accountName = transaction.getAccountName();
					String category = transaction.getCategory();
					String date = transaction.getDate();
					String time = transaction.getTime();
					
					csvValues =  id + ","; //id
					csvValues += amount + ","; // amount
					csvValues += currency + ","; // currency
					csvValues += description + ","; // description
					csvValues += accountName + ","; // accountName
					csvValues += category + ","; // category
					csvValues += date + ","; // date
					csvValues += time + ",\n"; // time
					
					out.write(csvValues);
				}
			}
			out.close();
			transactionTable.close();
			returnCode = true;
		} catch (Exception e) {
			// TODO: handle exception
			returnCode = false;
			Log.e("Exception", e.getMessage());
		}
		return returnCode;
				
	}
}
