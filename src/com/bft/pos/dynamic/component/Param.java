package com.bft.pos.dynamic.component;

import com.bft.pos.dynamic.core.ViewPage;

public class Param extends ParamComponent {
	
	public Param(ViewPage viewPage) {
		super(viewPage);
	}

	public Param(ViewPage viewPage, String paramId) {
		super(viewPage, paramId);
	}

	@Override
	protected Component construction(ViewPage viewPage) {
		return new Param(viewPage);
	}
}
