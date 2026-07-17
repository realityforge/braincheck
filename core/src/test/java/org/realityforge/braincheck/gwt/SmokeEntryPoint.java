package org.realityforge.braincheck.gwt;

import com.google.gwt.core.client.EntryPoint;
import org.realityforge.braincheck.BrainCheckConfig;

public final class SmokeEntryPoint implements EntryPoint {
    @Override
    public void onModuleLoad() {
        BrainCheckConfig.checkInvariants();
    }
}
