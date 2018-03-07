/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.wso2.org;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.org.Models.Component;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Class level comment.
 */
public class GitHubRepoLister {

    private static Logger logger = LoggerFactory.getLogger(GitHubRepoLister.class);

    //Done with pagination using split
    public static Map<String, String> splitLinkHeader(String header) {

        String[] parts = header.split(",");
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < parts.length; i++) {
            String[] sections = parts[i].split(";");
            String PaginationUrl = sections[0].replaceFirst("<(.*)>", "$1");
            String urlPagChange = PaginationUrl.trim();
            String name = sections[1].substring(6, sections[1].length() - 1);
            map.put(name, urlPagChange);
        }

        return map;
    }

    public static ArrayList<Component> getGitHubRepos(String orgName,int orgId) throws IOException {


        ArrayList<Component> components = new ArrayList<>();
        String initialUrl = Contstants.BASE_URL + "/"+"orgs"+"/" + orgName + "/"+"repos";
        ReadConfigureFile credentials = new ReadConfigureFile();

        try {

            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet initialUrlRequest = new HttpGet(initialUrl);

            initialUrlRequest.addHeader("content-type", "application/json");
            initialUrlRequest.addHeader("Authorization", "Bearer "
                    + credentials.getTokenKey());

            //Return the response of request as a json array
            HttpResponse responseOfReq = httpClient.execute(initialUrlRequest);

            String repoJson = EntityUtils.toString(responseOfReq.getEntity(),
                    "UTF-8");

            JsonElement reposJsonElement = new JsonParser().parse(repoJson);
            JsonArray reposJsonArray = reposJsonElement.getAsJsonArray();

            boolean containsNext = true;
            while (containsNext) {

                if (responseOfReq.containsHeader("Link")) {
                    Header[] linkHeader = responseOfReq.getHeaders("Link");
                    Map<String, String> linkMap = splitLinkHeader(linkHeader[0]
                            .getValue());
                    HttpClientUtils.closeQuietly(responseOfReq);
                    logger.info(linkMap.get("next"));
                    try {
                        HttpGet requestForNext = new HttpGet(
                                linkMap.get("next"));
                        requestForNext.addHeader("content-type",
                                "application/json");
                        requestForNext.addHeader("Authorization", "Bearer "
                                + credentials.getTokenKey());

                        responseOfReq = httpClient.execute(requestForNext);
                        String repoJsonNext = EntityUtils.toString(
                                responseOfReq.getEntity(), "UTF-8");
                        JsonElement jelementNext = new JsonParser()
                                .parse(repoJsonNext);
                        JsonArray jarrNext = jelementNext.getAsJsonArray();
                        reposJsonArray.addAll(jarrNext);
                        HttpClientUtils.closeQuietly(responseOfReq);

                    } catch (Exception e) {
                        containsNext = false;
                    }

                } else {
                    containsNext = false;
                }

            }

            for (int index = 0; index < reposJsonArray.size(); index++) {
                JsonObject repos = (JsonObject) reposJsonArray.get(index);

                String repoName = repos.get("name").toString();
                repoName = repoName.substring(1, repoName.length() - 1);

                String repoURL = repos.get("html_url").toString();
                repoURL = repoURL.substring(1, repoURL.length() - 1);

                Component component = new Component(repoName, repoURL,orgId);
                components.add(component);

            }
        } catch (IOException ex) {
            logger.info(ex.getStackTrace().toString());
        }
        return components;

    }

}

