/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.bigreactors.adapter.bluemap523;

import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.util.Key;
import io.github.janguenter.bluemap.addon.adapter.api.bluemap523.RegistryGuard;
import io.github.janguenter.bluemap.addon.adapter.api.bluemap523.ResourceExtensionType;
import io.github.janguenter.bluemap.bigreactors.activation.AddonRuntime;

/** Exact BlueMap 5.23 feature-backport registration boundary. */
public final class BlueMap523Adapter {

    private static final AddonRuntime RUNTIME = AddonRuntime.INSTANCE;
    private static final Key EXTENSION_KEY =
            Key.parse("bluemap_bigreactors:exact_profile");
    private static final BlockRendererType RENDERER = new BlockRendererType.Impl(
            Key.parse("bluemap_bigreactors:turbine_context"),
            (pack, gallery, settings) ->
                    new TurbineRotorRenderer(pack, gallery, settings, RUNTIME)
    );
    private static final ResourcePack.Extension<ProfileResourceExtension> EXTENSION =
            new ResourceExtensionType<>(
                    EXTENSION_KEY,
                    pack -> new ProfileResourceExtension(pack, RENDERER, RUNTIME)
            );

    private BlueMap523Adapter() {
    }

    /** Registers the renderer and exact-profile resource extension atomically. */
    public static synchronized boolean install() {
        if (!RegistryGuard.canRegister(BlockRendererType.REGISTRY, RENDERER)
                || !RegistryGuard.canRegister(
                        ResourcePack.Extension.REGISTRY, EXTENSION
                )) {
            RUNTIME.fail("registry-collision");
            return false;
        }
        if (!RegistryGuard.register(BlockRendererType.REGISTRY, RENDERER)
                || !RegistryGuard.register(
                        ResourcePack.Extension.REGISTRY, EXTENSION
                )) {
            RUNTIME.fail("registry-registration-failed");
            return false;
        }
        return true;
    }

    static BlockRendererType renderer() {
        return RENDERER;
    }

    static ResourcePack.Extension<ProfileResourceExtension> extension() {
        return EXTENSION;
    }
}
