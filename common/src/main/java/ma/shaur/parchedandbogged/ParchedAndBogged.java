package ma.shaur.parchedandbogged;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import ma.shaur.parchedandbogged.entity.Entities;

public final class ParchedAndBogged
{
	public static final String MOD_ID = "parchedandbogged";

	public static void init()
	{
		Entities.register();
		
		if(Platform.getEnvironment() == Env.CLIENT) ClientLifecycleEvent.CLIENT_STARTED.register(listener ->
		{
			Entities.initEntityRendering();
		});
	}
}
