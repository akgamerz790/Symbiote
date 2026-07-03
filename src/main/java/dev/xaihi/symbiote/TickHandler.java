package dev.xaihi.symbiote;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class TickHandler {
    
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (MobTracker.isEnabled()) {
                MobTracker.tick();
            }
        });
    }
}