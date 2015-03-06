/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sketchagram.chalmers.com.sketchagram;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;

/**
 * Collection of notification actions presets.
 */
public class ActionsPresets {
    public static final ActionsPreset NO_ACTIONS_PRESET = new NoActionsPreset();

    public static final ActionsPreset[] PRESETS = new ActionsPreset[] {
            NO_ACTIONS_PRESET
    };

    private static class NoActionsPreset extends ActionsPreset {
        public NoActionsPreset() {
            super(R.string.no_actions);
        }

        @Override
        public void apply(Context context, NotificationCompat.Builder builder,
                NotificationCompat.WearableExtender wearableOptions) {
        }
    }

}
