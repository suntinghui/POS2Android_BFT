package com.bft.pos.dynamic.component;

import com.bft.pos.dynamic.core.ViewPage;

public class Hidden extends Component {
	public Hidden(ViewPage viewPage) {
		this.viewPage = viewPage;
	}

	@Override
	protected Component construction(ViewPage viewPage) {
		return new Hidden(viewPage);
	}
}
