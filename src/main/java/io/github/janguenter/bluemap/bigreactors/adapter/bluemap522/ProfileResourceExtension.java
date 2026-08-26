/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.bigreactors.adapter.bluemap522;

import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePackExtension;
import de.bluecolored.bluemap.core.world.BlockProperties;
import de.bluecolored.bluemap.core.world.BlockState;
import io.github.janguenter.bluemap.bigreactors.activation.AddonRuntime;
import io.github.janguenter.bluemap.bigreactors.profile.ExactArtifactDetector;
import io.github.janguenter.bluemap.bigreactors.profile.BigReactors12112428Profile;

import java.nio.file.Path;

/** Exact-artifact admission and atomic installed rotor-resource routing. */
final class ProfileResourceExtension implements ResourcePackExtension {

    private final ResourcePack resourcePack;
    private final BlockRendererType renderer;
    private final AddonRuntime runtime;
    private InstalledRotorResources.Admission admission;

    ProfileResourceExtension(
            ResourcePack resourcePack,
            BlockRendererType renderer,
            AddonRuntime runtime
    ) {
        this.resourcePack = resourcePack;
        this.renderer = renderer;
        this.runtime = runtime;
    }

    @Override
    public void loadResources(Iterable<Path> roots) {
        if (Boolean.getBoolean("bluemap.bigreactors.disabled")) {
            runtime.inactive("operator-disabled");
            return;
        }
        if (!ExactArtifactDetector.matchesAll(roots, BigReactors12112428Profile.ARTIFACTS)) {
            runtime.inactive("exact-artifact-missing-or-duplicate");
            return;
        }

        admission = InstalledRotorResources.inspect(resourcePack);
        if (admission == null) {
            runtime.inactive("installed-rotor-schema-invalid");
        }
    }

    @Override
    public void bake() {
        if (admission == null) {
            return;
        }
        if (!InstalledRotorResources.bakedModelsValid(resourcePack, admission)) {
            admission = null;
            runtime.inactive("installed-rotor-model-invalid");
            return;
        }
        if (!admission.route(renderer)) {
            admission = null;
            runtime.inactive("rotor-routing-collision");
            return;
        }
        runtime.activate();
        System.out.println("BlueMap Extreme Reactors add-on active: 4 rotor blocks.");
    }

    @Override
    public void getBlockProperties(
            BlockState blockState,
            BlockProperties.Builder builder
    ) {
        if (runtime.active()
                && RotorCatalog.owns(blockState.getId().getFormatted())) {
            builder.culling(false).occluding(false).cullingIdentical(false);
        }
    }
}
