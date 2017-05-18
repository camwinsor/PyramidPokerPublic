package cwins.cardgame.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import cwins.cardgame.BuildConfig;
import cwins.cardgame.R;
import cwins.cardgame.model.Card;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CardTest {

    @Test
    public void testModelComponents() {
        Card testCard = new Card("Ad");

        assertThat(testCard.getCardName(), is(equalTo("Ad")));
        assertThat(testCard.getResourceId(), is(equalTo(R.drawable.d_a)));
    }
}
