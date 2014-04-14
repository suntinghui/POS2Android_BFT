/**
 * 
 */
package com.bft.pos.dynamic.template.os;

import java.util.Vector;

import com.bft.pos.dynamic.component.ViewException;
import com.bft.pos.dynamic.core.ViewPage;

import android.view.View;

/**
 * @author DongXiaoping
 *
 */
public interface IStructTemplate {
	public Vector<View> rewind(ViewPage viewPage) throws ViewException;
}
