package org.gw2items;

import org.json.simple.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import static org.gw2items.ReadItems.loadJSONObjectHTTP;

public class Reader implements Runnable {
    protected BlockingQueue<JSONObject> blockingQueue;
    private final ArrayList<Integer> idList;
    private final boolean items;


    private final static String API_URL = "https://api.guildwars2.com/"; //NOI18N
    private final static String API_VERSION = "v1/"; //NOI18N
    private final static String API_ITEMDETAIL = "item_details.json?item_id={0}"; //NOI18N
    private final static String API_RECIPEDETAIL = "recipe_details.json?recipe_id={0}"; //NOI18N

//    private static final ArrayList<JSONObject> itemsArray = new ArrayList<>();
    private static final String itemDetailUrl = API_URL.concat(API_VERSION).concat(API_ITEMDETAIL);
    private static final String recipeDetailUrl = API_URL.concat(API_VERSION).concat(API_RECIPEDETAIL);

    public Reader(ArrayList<Integer> idList, boolean items, BlockingQueue<JSONObject> blockingQueue) {
        this.idList = idList;
        this.items = items;
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
        JSONObject endObj = new JSONObject();
        endObj.put("name", "ENDENDEND");
        idList.stream().map((id) -> MessageFormat.format(items ? itemDetailUrl : recipeDetailUrl, id.toString())).forEach((url) -> {
            try {
                blockingQueue.put(loadJSONObjectHTTP(url));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        try {
            blockingQueue.put(endObj);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

