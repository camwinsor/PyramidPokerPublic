package cwins.cardgame;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class DragDropTest {

    @Test
    public void dragDropTest() {
        ActivityController main = Robolectric.buildActivity(MainActivity.class);


    }


}
