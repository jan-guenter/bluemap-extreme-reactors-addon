/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.bigreactors.profile;

import java.util.List;

/** Exact All the Mons 1.2.0 profile `bigreactors-1.21.1-2.4.28`. */
public final class BigReactors12112428Profile {

    public static final String PROFILE_ID = "bigreactors-1.21.1-2.4.28";
    public static final List<ArtifactPin> ARTIFACTS = List.of(
            new ArtifactPin(
                    "bigReactors",
                    "bigreactors",
                    "1.21.1-2.4.28",
                    "ExtremeReactors2-1.21.1-2.4.28.jar",
                    2_554_779L,
                    "74fdfdfc91c3c8e5a439d411e6f081d12193635fffe2c55142f2f28f75b9d621"
            ),
            new ArtifactPin(
                    "zeroCore",
                    "zerocore",
                    "1.21.1-2.4.21",
                    "ZeroCore2-1.21.1-2.4.21.jar",
                    1_551_013L,
                    "54ac755031b05c3a5b6ddfa22dabc45fb3775481041503dcecd75e5e86627779"
            )
    );

    private BigReactors12112428Profile() {
    }
}
