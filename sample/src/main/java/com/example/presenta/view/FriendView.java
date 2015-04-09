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

package com.example.presenta.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.presenta.R;
import com.example.presenta.screen.FriendScreen;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.techery.presenta.mortar.DaggerService;

public class FriendView extends FrameLayout {

  @Inject
  FriendScreen.Presenter presenter;

  @InjectView(R.id.friend_info)
  TextView friendInfo;

  public FriendView(Context context, AttributeSet attrs) {
    super(context, attrs);
    DaggerService.<FriendScreen.Component>getDaggerComponent(context).inject(this);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.inject(this);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    presenter.takeView(this);
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    presenter.dropView(this);
  }

  public void setFriend(String name) {
    friendInfo.setText(name);
  }
}
