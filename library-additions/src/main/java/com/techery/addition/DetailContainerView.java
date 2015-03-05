package com.techery.addition;

import android.content.Context;
import android.util.AttributeSet;

import com.techery.addition.flow.container.SimplePathContainer;
import com.techery.addition.flow.path.MasterDetailPath;

import flow.Path;
import flow.PathContextFactory;


public abstract class DetailContainerView extends FramePathContainerView {

  public DetailContainerView(Context context, AttributeSet attrs) {
    super(context, attrs, null);
    DetailPathContainer container = new DetailPathContainer(R.id.mortar_screen_switcher_tag, Path.contextFactory(), getEmptyPath());
    setContainer(container);
  }

  static class DetailPathContainer extends SimplePathContainer {

    private Path emptyPath;

    DetailPathContainer(int tagKey, PathContextFactory contextFactory, Path emptyPath) {
      super(tagKey, contextFactory);
      this.emptyPath = emptyPath;
    }

    @Override protected int getLayout(Path path) {
      MasterDetailPath mdPath = (MasterDetailPath) path;
      return super.getLayout(mdPath.isMaster() ? emptyPath : mdPath);
    }
  }

  protected abstract Path getEmptyPath();
}
