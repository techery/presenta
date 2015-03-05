/*
 * Copyright 2013 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mortar.flow;

import com.example.mortar.R;

import flow.HasParent;
import flow.Layout;
import flow.Path;

public final class Paths {

  @Layout(R.layout.no_details)
  public static class NoDetails extends Path {
  }

  /**
   * Identifies screens in a master / detail relationship. Both master and detail screens
   * extend this class.
   * <p>
   * Not a lot of thought has been put into making a decent master / detail modeling here. Rather
   * this is an excuse to show off using Flow to build a responsive layout. See {@link
   * com.example.flow.view.TabletMasterDetailRoot}.
   */
  public abstract static class MasterDetailPath extends Path {
    /**
     * Returns the screen that shows the master list for this type of screen.
     * If this screen is the master, returns self.
     * <p>
     * For example, the {@link Conversation} and {@link Message} screens are both
     * "under" the master {@link ConversationList} screen. All three of these
     * screens return a {@link Conversation} from this method.
     */
    public abstract MasterDetailPath getMaster();

    public final boolean isMaster() {
      return equals(getMaster());
    }
  }

  private Paths() {
  }
}
