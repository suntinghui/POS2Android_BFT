package com.bft.pos.dynamic.component.os;

import com.bft.pos.activity.view.TopInfoView;
import com.bft.pos.dynamic.component.Component;
import com.bft.pos.dynamic.component.ViewException;
import com.bft.pos.dynamic.core.ViewPage;

public class OSTopInfo extends StructComponent {
	
	public OSTopInfo(ViewPage viewPage) {
		super(viewPage);
	}

	@Override
	public TopInfoView toOSComponent() throws ViewException {
		TopInfoView topInfo = new TopInfoView(this.getContext());
		topInfo.setTag(this.getId());
		return topInfo;
	}

	@Override
	protected Component construction(ViewPage viewPage) {
		return new OSTopInfo(viewPage);
	}
}
