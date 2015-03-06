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

import android.app.Notification;
import android.content.Context;

/**
 * Base class for notification preset generators.
 */
public abstract class NotificationPreset extends NamedPreset {
    public final int titleResId;
    public final int textResId;

    public NotificationPreset(int nameResId, int titleResId, int textResId) {
        super(nameResId);
        this.titleResId = titleResId;
        this.textResId = textResId;
    }

    public static class BuildOptions {
        public final CharSequence titlePreset;
        public final CharSequence textPreset;

        public BuildOptions(CharSequence titlePreset, CharSequence textPreset) {
            this.titlePreset = titlePreset;
            this.textPreset = textPreset;
        }
    }

    /** Build a notification with this preset and the provided options */
    public abstract Notification[] buildNotifications(Context context, BuildOptions options);

    /** Whether actions are required to use this preset. */
    public boolean actionsRequired() {
        return false;
    }

    /** Number of background pickers required */
    public int countBackgroundPickersRequired() {
        return 0;
    }
}
