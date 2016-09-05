package com.example.transactioncard.object;


import java.util.ArrayList;
import java.util.List;

import com.example.transactioncard.database.DescriptionTable;

import android.content.Context;

public class CacheDescription {
	
	private Context context;
	
	public CacheDescription(Context context){
		this.context = context;
	}
	
	public List<Description>getDescriptionList(){
		List<Description> returnList = new ArrayList<Description>();
		DescriptionTable descriptionTable = new DescriptionTable(context);
		descriptionTable.open();
		returnList = descriptionTable.getDescriptionsAll();
		descriptionTable.close();
		return returnList;
	}
	
	public String[] getDescriptionNames(){
		List<Description> descList = getDescriptionList();
		String[] returnList = new String[descList.size()];
		for (int i = 0; i < returnList.length; i++) {
			returnList[i] = descList.get(i).getDescription();
		}
		return returnList;
	}
	
	public void deleteDescription(Description description){
		DescriptionTable descriptionTable = new DescriptionTable(context);
		descriptionTable.open();
		descriptionTable.deleteDescriptionSqliteDB(description);
		descriptionTable.close();
	}
	
	private boolean isDescriptionCached(String descriptionName){
		Description description = queryDescriptionByName(descriptionName);
		return !(description == null)? true : false;
	}

	private Description queryDescriptionByName(String descriptionName) {
		DescriptionTable descriptionTable = new DescriptionTable(context);
		descriptionTable.open();
		Description description = descriptionTable.getDescriptionByName(descriptionName);
		descriptionTable.close();
		return description;
	}
	
	public Description getDescription(String descriptionName){
		return saveNewDescription(descriptionName);
	}
	
	public Description saveNewDescription(String descriptionName){
		Description returnDescription = null;
		boolean descriptionIsSaved = isDescriptionCached(descriptionName);
		if (!descriptionIsSaved) {
			Description desc = createNewDescription(descriptionName);
			Description savedDesc = saveNewDescription(desc);
			if (!(savedDesc == null)) {
				returnDescription = savedDesc;
			}
		}else {
			returnDescription = queryDescriptionByName(descriptionName);
		}
		return returnDescription;
	}
	
	public Description getDescription(long id){
		DescriptionTable descriptionTable = new DescriptionTable(context);
		descriptionTable.open();
		Description description = descriptionTable.getDescriptionById(id);
		descriptionTable.close();
		return description;
	}
	
	private Description createNewDescription(String description) {
		Description desc = new Description();
		desc.setDescription(description);
		return desc;
	}
	
	private Description saveNewDescription(Description desc) {
		DescriptionTable descriptionTable = new DescriptionTable(context);
		descriptionTable.open();
		Description savedDesc = descriptionTable.insertDescriptionSqliteDB(desc);
		descriptionTable.close();
		return savedDesc;
	}
	
	
}

