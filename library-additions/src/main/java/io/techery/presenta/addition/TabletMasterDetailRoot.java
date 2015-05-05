package io.techery.presenta.addition;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import io.techery.presenta.addition.flow.path.IsMasterView;
import io.techery.presenta.addition.flow.path.MasterDetailPath;
import io.techery.presenta.addition.flow.util.BackSupport;

import flow.Flow;
import flow.path.Path;
import flow.path.PathContainerView;

import static io.techery.presenta.addition.flow.util.BackSupport.HandlesBack;


/**
 * This view is shown only in landscape orientation on tablets
 */
public class TabletMasterDetailRoot extends LinearLayout
    implements HandlesBack, PathContainerView {
  private FramePathContainerView masterContainer;
  private FramePathContainerView detailContainer;

  private boolean disabled;

  public TabletMasterDetailRoot(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override public boolean dispatchTouchEvent(MotionEvent ev) {
    return !disabled && super.dispatchTouchEvent(ev);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();

    masterContainer = (FramePathContainerView) findViewById(R.id.master_container);
    detailContainer = (FramePathContainerView) findViewById(R.id.detail_container);
  }

  @Override public ViewGroup getCurrentChild() {
    MasterDetailPath showing = Path.get(getContext());
    return showing.isMaster() ? masterContainer.getCurrentChild()
        : detailContainer.getCurrentChild();
  }

  @Override public ViewGroup getContainerView() {
    return this;
  }

  @Override public void dispatch(final Flow.Traversal traversal, Flow.TraversalCallback callback) {

    class CountdownCallback implements Flow.TraversalCallback {
      final Flow.TraversalCallback wrapped;
      int countDown = 2;

      CountdownCallback(Flow.TraversalCallback wrapped) {
        this.wrapped = wrapped;
      }

      @Override public void onTraversalCompleted() {
        countDown--;
        if (countDown == 0) {
          disabled = false;
          wrapped.onTraversalCompleted();
          ((IsMasterView) masterContainer.getCurrentChild()).updateSelection(
              traversal.destination.<MasterDetailPath>top());
        }
      }
    }

    disabled = true;
    callback = new CountdownCallback(callback);
    detailContainer.dispatch(traversal, callback);
    masterContainer.dispatch(traversal, callback);
  }

  @Override public boolean onBackPressed() {
    return BackSupport.onBackPressed(detailContainer);
  }
}
