/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package edu.usc.irds.sparkler.util;

import com.google.common.collect.Iterators;
import edu.usc.irds.sparkler.JobContext;
import edu.usc.irds.sparkler.model.FetchedData;
import edu.usc.irds.sparkler.model.Resource;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static junit.framework.Assert.*;
/**
 * @since 12/28/16
 */
public class FetcherDefaultTest {

    private FetcherDefault fetcher = new FetcherDefault();

    private JobContext job = TestUtils.JOB_CONTEXT;
    private Resource indexPage = new Resource("http://localhost:8080/index.html", "localhost", job);
    private Resource jsPage = new Resource("http://localhost:8080/jspage.html", "localhost", job);
    private Resource notFound = new Resource("http://localhost:8080/vacduyc_NOT_FOUND.html", "localhost", job);
    private List<Resource> resources = Arrays.asList(indexPage, jsPage, notFound);

    @Test
    public void fetch() throws Exception {
        long start = System.currentTimeMillis();
        FetchedData data1 = fetcher.fetch(indexPage);
        assertEquals("text/html", data1.getContentType());
        assertNotNull(data1.getFetchedAt());
        assertTrue(data1.getFetchedAt().getTime() >= start);
        assertTrue(data1.getFetchedAt().getTime() <= System.currentTimeMillis());
        assertEquals(200, data1.getResponseCode());

        try {
            fetcher.fetch(notFound);
            fail("Exception expected");
        } catch (Exception e){
            //expected
        }
    }

    @Test
    public void fetch1() throws Exception {
        Iterator<FetchedData> stream = fetcher.fetch(resources.iterator());
        List<FetchedData> list = new ArrayList<>();
        Iterators.addAll(list, stream);
        assertEquals(list.size(), resources.size());

        for (FetchedData data : list) {
            if (data.getResource().getUrl().contains("NOT_FOUND")) {
                assertEquals(404, data.getResponseCode());
            } else {
                assertEquals(200, data.getResponseCode());
            }
        }
    }

}