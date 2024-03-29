package com.bft.pos.dynamic.component.os;

import java.util.Vector;

import com.bft.pos.R;
import com.bft.pos.dynamic.component.Component;
import com.bft.pos.dynamic.core.ViewPage;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class DSpinner extends Spinner {
	
	private Context context;
	private String selectedIndexName;
	private String selectedValue;
	
	public DSpinner(Context context) {
		super(context);
		this.context = context;
	}

	public DSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	
	
	public String getSelectedIndexName() {
		return selectedIndexName;
	}

	public void setSelectedIndexName(String selectedIndexName) {
		this.selectedIndexName = selectedIndexName;
	}

	public String getSelectedValue() {
		return selectedValue;
	}

	public void setSelectedValue(String selectedValue) {
		this.selectedValue = selectedValue;
	}

	public void setItems(Vector<String>  items){
		ArrayAdapter<String> array_adapter = new ArrayAdapter <String> (context, R.layout.myspinner, items);
		array_adapter.setDropDownViewResource ( android.R.layout.simple_spinner_dropdown_item );        
		this.setAdapter (array_adapter);
	}
	
	public void setItems(String[] items){
		ArrayAdapter<String> array_adapter = new ArrayAdapter <String> (context, R.layout.myspinner, items);
		array_adapter.setDropDownViewResource ( android.R.layout.simple_spinner_dropdown_item );        
		this.setAdapter (array_adapter);
	}
}
