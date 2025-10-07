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

import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Michael Stilson
 */
public class ReadItemsTest {
    
    public ReadItemsTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of loadJSONObjectHTTP method, of class ReadItems.
     */
    @Test
    public void testLoadJSONObjectHTTP() {
        System.out.println("loadJSONObjectHTTP");
        String URL = "https://api.guildwars2.com/v1/item_detail.json?item_id=99999";
        JSONObject expResult = null;
        JSONObject result = ReadItems.loadJSONObjectHTTP(URL);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getItems method, of class ReadItems.
     */
//    @Test
//    public void testGetItems() {
//        System.out.println("getItems");
//        ArrayList<Integer> idList = null;
//        boolean items = false;
//        ArrayList<JSONObject> expResult = null;
//        ArrayList<JSONObject> result = ReadItems.getItems(idList, items);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    
}
