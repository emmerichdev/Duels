package com.emmerichbrowne.duels.game;

import com.emmerichbrowne.duels.DuelsPlugin;
import com.emmerichbrowne.duels.ServerRole;

public final class DuelsGamePlugin extends DuelsPlugin {
	@Override
	public ServerRole getServerRole() {
		return ServerRole.GAME;
	}
}

