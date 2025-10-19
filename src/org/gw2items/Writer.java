package org.gw2items;

import org.gw2items.models.ItemDetail;
import org.gw2items.models.Recipe;
import org.json.simple.JSONObject;

import java.util.concurrent.BlockingQueue;

import static org.gw2items.Factories.Database.writeItem;
import static org.gw2items.Factories.Database.writeSqlRecipes;
import static org.gw2items.Types.processType;

public class Writer implements Runnable {
    protected BlockingQueue blockingQueue;
    private final boolean getItems;

    public Writer(boolean getItems, BlockingQueue blockingQueue) {
        this.getItems = getItems;
        this.blockingQueue = blockingQueue;
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        JSONObject obj = null;
        do {
            try {
                    obj = (JSONObject) blockingQueue.take();
                if (!obj.containsKey("error") && (obj.containsKey ("name") && !(obj.get("name").equals("ENDENDEND")))) {
                        if (getItems) {
                            ItemDetail item = new ItemDetail(obj);
                            Start.setOutput("Writing Item " + item.getName() + "\n");
                            Start.setBar(blockingQueue.remainingCapacity());
                            writeItem(item);
                            processType(obj);
                        } else {
                            Recipe recipe = new Recipe(obj);
                            writeSqlRecipes(recipe);
                        }
                    }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } while ((obj != null) && (obj.containsKey("name") && !obj.get("name").equals("ENDENDEND")));
    }
}
