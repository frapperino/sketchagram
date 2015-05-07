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

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.wearable.view.FragmentGridPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Constructs fragments as requested by the GridViewPager. For each row a different background is
 * provided.
 * <p>
 * Always avoid loading resources from the main thread. In this sample, the background images are
 * loaded from an background task and then updated using {@link #notifyRowBackgroundChanged(int)}
 * and {@link #notifyPageBackgroundChanged(int, int)}.
 */
public class ConversationViewAdapter extends FragmentGridPagerAdapter {
    private static final int TRANSITION_DURATION_MILLIS = 100;

    private final Context mContext;
    private List<Row> mRows;

    public ConversationViewAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        mContext = ctx;

        mRows = new ArrayList<Row>();

        loadFragments();
    }

    /** A convenient container for a row of fragments. */
    private class Row {
        Fragment fragment;

        public Row(Fragment fragment) {
            this.fragment = fragment;
        }

        Fragment getFragment() {
            return fragment;
        }
    }

    @Override
    public Fragment getFragment(int row, int col) {
        Row adapterRow = mRows.get(row);
        return adapterRow.getFragment();
    }

    @Override
    public int getRowCount() {
        return mRows.size();
    }

    @Override
    public int getColumnCount(int i) {
        return 1;
    }

    public void loadFragments(){
        int messageAmount = MessageHolder.getInstance().getDrawingsAmount();

        for(int i = messageAmount-1; i >= 0; i--) {
            ConversationViewFragment fragment = new ConversationViewFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("id", i);
            fragment.setArguments(bundle);
            mRows.add(new Row(fragment));
        }

    }
}
