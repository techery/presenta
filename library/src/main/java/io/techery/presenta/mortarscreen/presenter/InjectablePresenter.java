package io.techery.presenta.mortarscreen.presenter;

import android.view.View;

import mortar.ViewPresenter;

public class InjectablePresenter<V extends View> extends ViewPresenter<V> {

    public InjectablePresenter(PresenterInjector injector) {
        injector.inject(this);
    }

    @SuppressWarnings("unchecked")
    public interface PresenterInjector {
        void inject(InjectablePresenter presenter);
    }

}
