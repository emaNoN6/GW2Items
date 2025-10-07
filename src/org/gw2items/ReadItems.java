/*
 * Copyright (C) 2014 Michael Stilson
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gw2items;


import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;
import static java.text.MessageFormat.format;
import static javax.swing.JOptionPane.showMessageDialog;
import static org.gw2items.Start.setStatusLabel;

public class ReadItems {

    private final static String API_URL = "https://api.guildwars2.com/"; //NOI18N
    private final static String API_VERSION = "v1/"; //NOI18N
    private final static String API_ITEMDETAIL = "item_details.json?item_id={0}"; //NOI18N
    private final static String API_RECIPEDETAIL = "recipe_details.json?recipe_id={0}"; //NOI18N

    private static final ArrayList<JSONObject> itemsArray = new ArrayList<>();
    private static final String itemDetailUrl = API_URL.concat(API_VERSION).concat(API_ITEMDETAIL);
    private static final String recipeDetailUrl = API_URL.concat(API_VERSION).concat(API_RECIPEDETAIL);

    @SuppressWarnings("SleepWhileInLoop")
    public static JSONObject loadJSONObjectHTTP(String URL) {
        boolean retry = false;
        int l = 0;
        JSONObject obj = null;
//        do {
            HttpGet getString = new HttpGet(URL);
            CloseableHttpClient httpClient = HttpClients.createDefault();

            CloseableHttpResponse response;
            try {
                response = httpClient.execute(getString);
/*
                System.out.println(response.getProtocolVersion());
                System.out.println(response.getStatusLine().getStatusCode());
                System.out.println(response.getStatusLine().getReasonPhrase());
                System.out.println(response.getStatusLine().toString());
*/
                String respStr = EntityUtils.toString(response.getEntity());
                httpClient.close();
                obj = (JSONObject) new JSONParser().parse(respStr);
                retry = false;
            } catch (org.json.simple.parser.ParseException ex) {
                if (ex.toString().equals("Unexpected character (<) at position 0.")) {
                    setStatusLabel(MessageFormat.format("Error, retrying after waiting {0} second{1}", l, (l == 1) ? "." : "s."));
                    try {
                        // Deal with lag error throwing 503 errors
                        sleep(1000 * ((l < 6) ? l++ : l));
                    } catch (InterruptedException ignore) {
                        currentThread().interrupt();
                    }
                    retry = true;
                } else {
                    showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException | ParseException ex) {
                    showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
//        } while (retry == true);
        return obj;
    }

    public static ArrayList<JSONObject> getItems(ArrayList<Integer> idList, boolean items) {
        idList.stream().map((id) -> MessageFormat.format(items ? itemDetailUrl : recipeDetailUrl, id.toString())).forEach((url) -> {
            itemsArray.add(loadJSONObjectHTTP(url));
            setStatusLabel(format("Receiving item {0}", itemsArray.size()));
        });
        return itemsArray;
    }
}
