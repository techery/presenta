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
package com.example.presenta;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.presenta.di.ScreenComponent;
import com.example.presenta.screen.ChatListScreen;
import com.example.presenta.screen.FriendListScreen;
import com.google.gson.Gson;

import javax.inject.Inject;

import flow.ActivityFlowSupport;
import flow.Backstack;
import flow.Flow;
import flow.Path;
import flow.PathContainerView;
import io.techery.presenta.addition.ActionBarOwner;
import io.techery.presenta.addition.flow.util.GsonParceler;
import io.techery.presenta.di.ScreenScope;
import io.techery.presenta.mortar.DaggerService;
import mortar.MortarScope;
import mortar.MortarScopeDevHelper;
import mortar.bundler.BundleServiceRunner;

import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;
import static io.techery.presenta.addition.flow.util.BackSupport.HandlesBack;
import static mortar.bundler.BundleServiceRunner.getBundleServiceRunner;

/**
 * A well intentioned but overly complex sample that demonstrates
 * the use of Mortar, Flow and Dagger in a single app.
 */
public class MortarDemoActivity extends ActionBarActivity
    implements ActionBarOwner.Activity, Flow.Dispatcher {

  @ScreenScope(MortarDemoActivity.class)
  @dagger.Component(dependencies = MortarDemoApplication.AppComponent.class)
  public interface Component extends MortarDemoApplication.AppComponent, ScreenComponent {
    void inject(MortarDemoActivity activity);
  }

  private MortarScope activityScope;
  private ActionBarOwner.MenuAction actionBarMenuAction;

  @Inject
  ActionBarOwner actionBarOwner;

  private PathContainerView container;
  private HandlesBack containerAsHandlesBack;
  private ActivityFlowSupport flowSupport;

  @Override
  public Context getContext() {
    return this;
  }

  @Override
  public void dispatch(Flow.Traversal traversal, final Flow.TraversalCallback callback) {
    Path path = traversal.destination.current();
    setTitle(path.getClass().getSimpleName());
    boolean canGoBack = traversal.destination.size() > 1;
    String title = path.getClass().getSimpleName();
    ActionBarOwner.MenuAction menu = canGoBack ? null : new ActionBarOwner.MenuAction("Friends", new Runnable() {
          @Override
          public void run() {
            Flow.get(MortarDemoActivity.this).goTo(new FriendListScreen());
          }
        });
    actionBarOwner.setConfig(new ActionBarOwner.Config(false, canGoBack, title, menu));
    container.dispatch(traversal, new Flow.TraversalCallback() {
      @Override
      public void onTraversalCompleted() {
        invalidateOptionsMenu();
        callback.onTraversalCompleted();
      }
    });
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ActivityFlowSupport.NonConfigurationInstance nonConfigurationInstance = (ActivityFlowSupport.NonConfigurationInstance) getLastCustomNonConfigurationInstance();
    Backstack backstack = Backstack.single(new ChatListScreen());
    flowSupport = ActivityFlowSupport.onCreate(nonConfigurationInstance, savedInstanceState, new GsonParceler(new Gson()), backstack);

    Object appComponent = DaggerService.getDaggerComponent(MortarDemoApplication.instance());
    Component component = DaggerService.createComponent(Component.class, appComponent);
    component.inject(this);

    String scopeName = getLocalClassName() + "-task-" + getTaskId();
    MortarScope parentScope = MortarScope.getScope(getApplication());
    activityScope = parentScope.findChild(scopeName);
    if (activityScope == null) {
      activityScope = parentScope.buildChild()
          .withService(BundleServiceRunner.SERVICE_NAME, new BundleServiceRunner())
          .withService(DaggerService.SERVICE_NAME, component)
          .build(scopeName);
    }
    BundleServiceRunner.getBundleServiceRunner(activityScope).onCreate(savedInstanceState);

    actionBarOwner.takeView(this);

    setContentView(R.layout.root_layout);
    container = (PathContainerView) findViewById(R.id.container);
    containerAsHandlesBack = (HandlesBack) container;
  }

  @Override
  protected void onResume() {
    super.onResume();
    flowSupport.onResume(this);
  }

  @Override
  protected void onPause() {
    flowSupport.onPause();
    super.onPause();
  }

  @Override
  public Object getSystemService(String name) {
    Object flow = null;
    if (flowSupport != null) flow = flowSupport.getSystemService(name);
    return flow != null ? flow : (activityScope != null && activityScope.hasService(name)) ? activityScope.getService(name) : super.getSystemService(name);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    flowSupport.onSaveInstanceState(outState, container.getCurrentChild());
    getBundleServiceRunner(this).onSaveInstanceState(outState);
  }


  @Override
  public Object onRetainCustomNonConfigurationInstance() {
    return flowSupport.onRetainNonConfigurationInstance();
  }

  /**
   * Inform the view about back events.
   */
  @Override
  public void onBackPressed() {
    if (!containerAsHandlesBack.onBackPressed() && !flowSupport.onBackPressed()) super.onBackPressed();
  }

  /**
   * Inform the view about up events.
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /**
   * Configure the action bar menu as required by {@link ActionBarOwner.Activity}.
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if (actionBarMenuAction != null) {
      menu.add(actionBarMenuAction.title)
          .setShowAsActionFlags(SHOW_AS_ACTION_ALWAYS)
          .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
              actionBarMenuAction.action.run();
              return true;
            }
          });
    }
    menu.add("Log Scope Hierarchy")
        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
          @Override
          public boolean onMenuItemClick(MenuItem item) {
            Log.d("DemoActivity", MortarScopeDevHelper.scopeHierarchyToString(activityScope));
            return true;
          }
        });
    return true;
  }

  @Override
  protected void onDestroy() {
    actionBarOwner.dropView(this);
    actionBarOwner.setConfig(null);

    // activityScope may be null in case isWrongInstance() returned true in onCreate()
    if (isFinishing() && activityScope != null) {
      activityScope.destroy();
      activityScope = null;
    }

    super.onDestroy();
  }

  @Override
  public void setShowHomeEnabled(boolean enabled) {
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayShowHomeEnabled(false);
  }

  @Override
  public void setUpButtonEnabled(boolean enabled) {
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(enabled);
    actionBar.setHomeButtonEnabled(enabled);
  }

  @Override
  public void setTitle(CharSequence title) {
    getSupportActionBar().setTitle(title);
  }

  @Override
  public void setMenu(ActionBarOwner.MenuAction action) {
    if (action != actionBarMenuAction) {
      actionBarMenuAction = action;
      invalidateOptionsMenu();
    }
  }

}
