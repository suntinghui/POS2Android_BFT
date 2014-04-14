package com.bft.pos.dynamic.template.os.view;

import java.util.List;
import java.util.Vector;

import com.bft.pos.dynamic.component.Component;
import com.bft.pos.dynamic.component.ViewException;
import com.bft.pos.dynamic.model.ViewGroupBean;

import android.content.Context;
import android.view.View;

public interface IViewGroupTemplate {
	
	public View rewind(List<ViewGroupBean> components) throws ViewException;

}
