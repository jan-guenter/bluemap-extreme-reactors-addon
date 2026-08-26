/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.bigreactors.adapter.bluemap522;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExactRotorResourceContractTest {

    @Test
    void exactCandidateProvidesAllContextualRotorModels() throws IOException {
        String configured = System.getProperty("bigReactorsJar");
        Assumptions.assumeTrue(configured != null && !configured.isBlank());

        try (ZipFile jar = new ZipFile(Path.of(configured).toFile())) {
            for (String blockId : RotorCatalog.BLOCK_IDS) {
                String blockPath = blockId.substring(blockId.indexOf(':') + 1);
                JsonObject variants = json(
                        jar,
                        "assets/bigreactors/blockstates/" + blockPath + ".json"
                ).getAsJsonObject("variants");
                Set<String> expectedStates = RotorCatalog.states(blockId).stream()
                        .map(state -> "state=" + state)
                        .collect(Collectors.toUnmodifiableSet());
                assertEquals(expectedStates, variants.keySet());
                for (Map.Entry<String, com.google.gson.JsonElement> entry
                        : variants.entrySet()) {
                    JsonObject variant = entry.getValue().getAsJsonObject();
                    String model = variant.get("model").getAsString();
                    assertTrue(model.startsWith("bigreactors:block/"));
                    assertModelExists(jar, model);
                }
            }

            Set<String> visibleModels = Set.of(
                    "rotorshaft_z_0c",
                    "rotorshaft_z_2c",
                    "rotorshaft_z_2cy",
                    "rotorshaft_z_4c",
                    "rotorblade_z",
                    "rotorblade_zy"
            );
            for (String tier : Set.of("basic", "reinforced")) {
                for (String model : visibleModels) {
                    assertNotNull(jar.getEntry(
                            "assets/bigreactors/models/block/turbine/" + tier
                                    + '/' + model + ".json"
                    ));
                }
            }
        }
    }

    @Test
    void exactCandidateProvidesAllConnectedGlassModels() throws IOException {
        String configured = System.getProperty("bigReactorsJar");
        Assumptions.assumeTrue(configured != null && !configured.isBlank());

        try (ZipFile jar = new ZipFile(Path.of(configured).toFile())) {
            for (String blockId : GlassCatalog.BLOCK_IDS) {
                String blockPath = blockId.substring(blockId.indexOf(':') + 1);
                JsonObject variants = json(
                        jar,
                        "assets/bigreactors/blockstates/" + blockPath + ".json"
                ).getAsJsonObject("variants");
                Set<String> expectedStates = GlassCatalog.STATES.stream()
                        .map(state -> "facings=" + state)
                        .collect(Collectors.toUnmodifiableSet());
                assertEquals(expectedStates, variants.keySet());
                for (Map.Entry<String, com.google.gson.JsonElement> entry
                        : variants.entrySet()) {
                    String model = entry.getValue().getAsJsonObject()
                            .get("model").getAsString();
                    assertTrue(model.startsWith(
                            "bigreactors:block/turbine/"
                    ));
                    assertModelExists(jar, model);
                }
            }
        }
    }

    private static void assertModelExists(ZipFile jar, String model) {
        String namespaced = model.substring("bigreactors:".length());
        assertNotNull(jar.getEntry(
                "assets/bigreactors/models/" + namespaced + ".json"
        ));
    }

    private static JsonObject json(ZipFile jar, String path) throws IOException {
        ZipEntry entry = jar.getEntry(path);
        assertNotNull(entry, path);
        try (InputStreamReader reader = new InputStreamReader(
                jar.getInputStream(entry), StandardCharsets.UTF_8
        )) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }
}
