package cwins.cardgame;

import javax.inject.Named;
import javax.inject.Singleton;

import cwins.cardgame.model.Session;
import dagger.Module;
import dagger.Provides;

@Module
public class FlashPokerModule {
    private Session session;

    public FlashPokerModule() {
        this.session = new Session();
    }

    @Provides
    @Singleton
    Session provideSession() {
        return this.session;
    }

    @Provides
    @Singleton
    CardGameManager provideCardGameManager(Session session, @Named("serverUrl") String serverUrl) {
        CardGameManager cardGameManager = new CardGameManager(session, serverUrl);
        return cardGameManager;
    }

    @Named("serverUrl")
    @Provides
    @Singleton
    String provideServerUrl() {
//        String serverUrl = "http://10.0.1.65:8000";
        String serverUrl = "http://flashpoker.heathskin.com:80";
        return serverUrl;
    }
}
