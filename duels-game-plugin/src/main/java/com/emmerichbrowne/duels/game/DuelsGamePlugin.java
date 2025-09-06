package com.emmerichbrowne.duels.game;

import com.emmerichbrowne.duels.DuelsPlugin;

public final class DuelsGamePlugin extends DuelsPlugin {
	@Override
	public com.emmerichbrowne.duels.core.ServerRole getServerRole() {
		return com.emmerichbrowne.duels.core.ServerRole.GAME;
	}
}

