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
package io.techery.presenta.addition;

import android.content.Context;
import android.util.AttributeSet;

import flow.path.Path;
import io.techery.presenta.addition.flow.container.SimplePathContainer;
import io.techery.presenta.mortarflow.MortarContextFactory;

public class MortarFramePathContainerView extends FramePathContainerView {
  public MortarFramePathContainerView(Context context, AttributeSet attrs) {
    super(context, attrs, new SimplePathContainer(R.id.mortar_screen_switcher_tag, Path.contextFactory(new MortarContextFactory())));
  }
}
