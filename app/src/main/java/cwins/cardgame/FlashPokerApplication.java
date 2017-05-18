package cwins.cardgame;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;


// create a base application that is the entry point for our entire app
public class FlashPokerApplication extends Application {
    private ApplicationComponent component;

    /**
     * Component defines our injector class. Every time we have a new place that needs stuff
     * to be injected, create a new inject() method inside this interface.
     */
    @Singleton
    @Component(modules = {AndroidModule.class, FlashPokerModule.class})
    public interface ApplicationComponent {
        void inject(FlashPokerApplication application);
        void inject(MenuActivity menuActivity);
        void inject(ScoringActivity scoringActivity);
        void inject(GamesListActivity gamesListActivity);
        void inject(MainActivity mainActivity);
    }

    @Override public void onCreate() {
        super.onCreate();

        // this instantiates our component. DaggerFlashPokerApplication_ApplicationComponent
        // is an auto-generated class created by dagger, based on the requirements we specify
        // in our component definition above
        component = DaggerFlashPokerApplication_ApplicationComponent.builder()
                // every Module we create needs to have its constructor invoked in this list
                .androidModule(new AndroidModule(this))
                .flashPokerModule(new FlashPokerModule())
                .build();

        component().inject(this); // As of now, injection is complete
    }

    public ApplicationComponent component() {
        return component;
    }

    public void setComponent(ApplicationComponent component) {
        this.component = component;
    }
}