package com.techery.presenta.mortarflow;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;

import com.techery.presenta.mortarscreen.ScreenScoper;

import flow.Path;
import flow.PathContextFactory;
import mortar.MortarScope;

public final class MortarContextFactory implements PathContextFactory {
  private final ScreenScoper screenScoper;

  public MortarContextFactory() {
    screenScoper = new ScreenScoper();
  }

  @Override public Context setUpContext(Path path, Context parentContext) {
    MortarScope screenScope = screenScoper.getScreenScope(parentContext, path.getClass().getName(), path);
    return new TearDownContext(parentContext, screenScope);
  }

  @Override public void tearDownContext(Context context) {
    TearDownContext.destroyScope(context);
  }

  static class TearDownContext extends ContextWrapper {
    private static final String SERVICE = "SNEAKY_MORTAR_PARENT_HOOK";
    private final MortarScope parentScope;
    private LayoutInflater inflater;

    static void destroyScope(Context context) {
      MortarScope.getScope(context).destroy();
    }

    public TearDownContext(Context context, MortarScope scope) {
      super(scope.createContext(context));
      this.parentScope = MortarScope.getScope(context);
    }

    @Override public Object getSystemService(String name) {
      if (LAYOUT_INFLATER_SERVICE.equals(name)) {
        if (inflater == null) {
          inflater = LayoutInflater.from(getBaseContext()).cloneInContext(this);
        }
        return inflater;
      }

      if (SERVICE.equals(name)) {
        return parentScope;
      }

      return super.getSystemService(name);
    }
  }
}
