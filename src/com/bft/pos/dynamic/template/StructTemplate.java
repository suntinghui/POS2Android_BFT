/**
 * 
 */
package com.bft.pos.dynamic.template;

import java.util.Vector;

import android.view.View;

import com.bft.pos.dynamic.component.ViewException;
import com.bft.pos.dynamic.core.ViewContext;
import com.bft.pos.dynamic.core.ViewPage;
import com.bft.pos.dynamic.template.os.IStructTemplate;

/**
 * 结构模板父类
 * @author Xiaoping Dong
 *
 */
public abstract class StructTemplate extends Template implements IStructTemplate {
	private ViewPage currentPage;

	public StructTemplate(String id, String name) {
		super(id, name);
	}
	public void setCurrentPage(ViewPage currentPage) {
		this.currentPage = currentPage;
	} 
	public ViewPage getCurrentPage() {
		return this.currentPage;
	}
	public ViewPage getTemplatePage() {
		return null == this.getId() ? null : ViewContext.getInstance().getViewPage(this.getId());
	}
	@Override
	public Vector<View> rewind(ViewPage viewPage) throws ViewException {
		this.setCurrentPage(viewPage);
		this.dateInit();
		this.getCurrentPage().addAllComponents(this.getTemplatePage());
		this.getCurrentPage().addAllPageValues(this.getTemplatePage());
		this.getCurrentPage().addAllEvents(this.getTemplatePage());
		return this.excute();
	}
	public abstract void dateInit();
	public abstract Vector<View> excute();
}
