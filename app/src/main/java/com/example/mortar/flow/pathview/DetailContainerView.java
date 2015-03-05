package com.example.mortar.flow.pathview;

import android.content.Context;
import android.util.AttributeSet;

import com.example.mortar.R;
import com.example.mortar.flow.Paths;

import flow.Path;
import flow.PathContextFactory;

import static com.example.mortar.flow.Paths.MasterDetailPath;


public class DetailContainerView extends FramePathContainerView {

  public DetailContainerView(Context context, AttributeSet attrs) {
    super(context, attrs, new DetailPathContainer(R.id.screen_switcher_tag, Path.contextFactory()));
  }

  static class DetailPathContainer extends SimplePathContainer {
    DetailPathContainer(int tagKey, PathContextFactory contextFactory) {
      super(tagKey, contextFactory);
    }

    @Override protected int getLayout(Path path) {
      MasterDetailPath mdPath = (MasterDetailPath) path;
      return super.getLayout(mdPath.isMaster() ? new Paths.NoDetails() : mdPath);
    }
  }
}
