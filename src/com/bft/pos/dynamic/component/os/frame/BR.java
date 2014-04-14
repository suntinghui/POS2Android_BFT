/**
 * 
 */
package com.bft.pos.dynamic.component.os.frame;

import java.util.Vector;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.bft.pos.activity.BaseActivity;
import com.bft.pos.dynamic.component.Component;
import com.bft.pos.dynamic.component.ViewException;
import com.bft.pos.dynamic.component.os.StructComponent;
import com.bft.pos.dynamic.core.ViewContext;
import com.bft.pos.dynamic.core.ViewPage;

/**
 * @author STH
 *
 */
public class BR extends FrameComponent {
	public BR(ViewPage viewPage) {
		super(viewPage);
		this.setId("_System_Frame_BR_");
	}
	
	@Override
	public View toOSFrame(Vector<Component> components) throws ViewException {
		LinearLayout layout = new LinearLayout(BaseActivity.getTopActivity());   //线性布局方式  
        layout.setOrientation(LinearLayout.HORIZONTAL ); //控件对其方式为水平排列
        layout.setGravity(Gravity.LEFT);
        layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		for (Component component:components) {
			if (component instanceof StructComponent) {
				((StructComponent)component).toComponent().setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));
				layout.addView(ViewContext.getInstance().cssRewind(((StructComponent)component).toComponent(), component.getTemplate()));
			}
		}
		return layout;
	}
	
	@Override
	protected Component construction(ViewPage viewPage) {
		return new BR(viewPage);
	}
}
