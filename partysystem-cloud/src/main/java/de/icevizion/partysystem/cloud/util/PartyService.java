package de.icevizion.partysystem.cloud.util;

import de.icevizion.partysystem.cloud.PartyCloudPlugin;
import net.titan.manager.ClusterService;

/**
 * @author Patrick Zdarsky / Rxcki
 * @date 26/07/2020 21:02
 */

public class PartyService extends ClusterService {

    private final PartyCloudPlugin partyCloudPlugin;

    public PartyService(PartyCloudPlugin partyCloudPlugin) {
        super("Party-Watcher");
        this.partyCloudPlugin = partyCloudPlugin;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted() && !isStopping()) {
            update();

            if (isStopping()) return;
            serviceWait(5000);
        }
    }

    private void update() {

    }
}
