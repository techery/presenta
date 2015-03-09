## What's this for?
Mortar and flow together provide a good way to follow MVP pattern and get rid of [lol-cycle with Fragments uglyness](https://corner.squareup.com/2014/10/advocating-against-android-fragments.html).
Nevertheless it's a little boilerplate to create a single Screen:

1. Create `Path` class;
2. Create inner-class `@module` (for Dagger 1) or `@component` + `@module` (for Dagger 2) to provide view with presenter;
3. Inject `Presenter` into the `View`.

`Mortar` already gives easy way to provide view with presenter via scoped context, and step 2 is odd in most cases – that's what `Presenta` for. `Presenta` uses basic `mortar + flow` example with extra extra notation to skip `Dagger` in the middle of `preseter-view` injection.

## Getting started
*Workflow is identical to mortar-sapmle:*

1. Add root scope for mortar, optionally link it with your dagger main component;
2. Add flow support to main activity;
3. Create Path screen with presenter and view refs.

Presenta provides `InjectablePresenter` as a base class for presenters which want to benefit from dagger and mortar, so you have dagger injections available inside presenter with no hassle.

#### 1. Declare your `Path` using `@WithPresenter`
```java
@Layout(R.layout.chat_list_view) @WithPresenter(ChatListScreen.Presenter.class)
public class ChatListScreen extends Path {
  ...
}
```
#### 2. Add Presenter
```java
public static class Presenter extends InjectablePresenter<ChatListView> {

    @Inject Chats chats;
    List<Chat> chatList;

    public Presenter(PresenterInjector injector) {
      super(injector); // Dagger injection will be held there
      this.chatList = chats.getAll();
    }
    
  }
```
#### 3. Use mortar service to get presenter from View
```java
public class ChatListView extends ListView {
  Presenter presenter;

  public ChatListView(Context context, AttributeSet attrs) {
    super(context, attrs);
    presenter = PresenterService.getPresenter(context);
  }
  ...
```
## Arguments for presenter
Most of the time `Path` would have arguments for presenter, which identifies data on screen to be loaded. It's easy with inner-class like presenter and still safe – as presenter is already linked to flow path and will be destroyed even before path is. Note `messageId` and `chatId` in next sample:
```java
@Layout(R.layout.message_view) @WithPresenter(MessageScreen.Presenter.class)
public class MessageScreen extends Path {
  private final int chatId;
  private final int messageId;

  public MessageScreen(int chatId, int messageId) {
    this.chatId = chatId;
    this.messageId = messageId;
  }

  public class Presenter extends InjectablePresenter<MessageView> {
    private final Observable<Message> messageSource;
    private Message message;
    @Inject Chats service;

    public Presenter(PresenterInjector injector) {
      super(injector);
      this.messageSource = service.getChat(chatId).getMessage(messageId);
    }
    ...
  }
}
```
## Dagger's Component support 
Is still here. It's recommended to use @Scoped injection for singletons per path context. Presenta comes with `AppScope` and `ScreenScope` for this purpose. 
```java
@Layout(R.layout.friend_view) @WithComponent(FriendScreen.Component.class)
public class FriendScreen extends Path implements HasParent {
  private final int index;

  public FriendScreen(int index) {
    this.index = index;
  }

  @Override public FriendListScreen getParent() {
    return new FriendListScreen();
  }

  @ScreenScope(FriendScreen.class)
  @dagger.Component(dependencies = MortarDemoActivity.Component.class, modules = Module.class)
  public static interface Component{
    void inject(FriendView view);
  }

  @dagger.Module
  public class Module {
    @Provides User provideFriend(Chats chats) {
      return chats.getFriend(index);
    }
  }

  @ScreenScope(FriendScreen.class)
  public static class Presenter extends ViewPresenter<FriendView> {
    private final User friend;

    @Inject
    public Presenter(User friend) {
      this.friend = friend;
    }

    @Override public void onLoad(Bundle savedInstanceState) {
      super.onLoad(savedInstanceState);
      if (!hasView()) return;
      getView().setFriend(friend.name);
    }
  }
}
```
## Additions
Mortar-flow sample has useful PathContainers to show up working example of it's philisophy. Those containers and view helpers are reused in `library-additions`
## Dev. status
Experimental but already is trying to use in prod. builds.
## Instalation
```groovy
repositories {
    jcenter()
    maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
    maven { url "https://jitpack.io" }
}

dependencies {
    compile 'com.github.techery:presenta.library'
    compile 'com.github.techery:presenta.library-additions'
    ...
}
```
