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
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * TODO: Class level comment.
 */
public class GitHubRepoListor {

    private static Logger logger = LoggerFactory.getLogger(GitHubRepoListor.class);
    //Done with pagination using split
    public static Map<String, String> splitLinkHeader(String header){
        String[] parts = header.split(",");
        Map <String, String> map = new HashMap<String, String>();
        for(int i = 0; i < parts.length; i++){
            String[] sections = parts[i].split(";");
            String PaginationUrl = sections[0].replaceFirst("<(.*)>", "$1");
            String urlPagChange =  PaginationUrl.trim();
            String name = sections[1].substring(6, sections[1].length() - 1);
            map.put(name, urlPagChange);
        }

        return map;
    }

    public void getGitHubRepos(String orgName) throws SQLException, IOException {
        String baseURL = "https://api.github.com";

        String initialUrl = "https://api.github.com/orgs/"+orgName+"/repos";
        ReadConfigureFile credentials= new ReadConfigureFile();

        try {



            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet initialUrlRequest = new HttpGet(initialUrl);

            /**
             * @param InitialUrlRequest
             *            GET /orgs/:org/repos
             *
             *
             */
            initialUrlRequest.addHeader("content-type", "application/json");
            initialUrlRequest.addHeader("Authorization", "Bearer "
                    + credentials.getTokenKey());

            //Return the response of request as a json array
            HttpResponse responseOfReq = httpClient.execute(initialUrlRequest);

            String repoJson = EntityUtils.toString(responseOfReq.getEntity(),
                    "UTF-8");

            JsonElement reposJsonElement = new JsonParser().parse(repoJson);
            JsonArray reposJsonArray = reposJsonElement.getAsJsonArray();

            /**
             * @param RepolJsonArray
             *            Used to get all repositories contents as a Json array
             *
             */

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

                        System.out.println((jarrNext));

                    } catch (Exception e) {
                        containsNext = false;
                    }



                } else {
                    containsNext = false;
                }

            }


            for (int i = 0; i < reposJsonArray.size(); i++) {
                JsonObject repos = (JsonObject) reposJsonArray.get(i);

                String repoName = repos.get("name").toString();
                // repoName returns all repositories names of wso2 in github

                repoName = repoName.substring(1, repoName.length() - 1);
                //logger.info("Name of the repository   :" + repoName);

                String repoURL = repos.get("html_url").toString();
                // repoUrl returns all repositories url of wso2 in GitHub

                repoURL = repoURL.substring(1, repoURL.length() - 1);
                //logger.info("Repository URL   :" + repoURL);

                //ProductComponentMapListUpdator.updateComponent(repoName,repoURL);

                ArrayList<String> githubReposListor=new ArrayList<String>();
                githubReposListor.add(repoName);
                githubReposListor.add(repoURL);

                for (int y = 0; y <githubReposListor.size(); y++) {
                    String githubRepoList = githubReposListor.get(y);
                    System.out.println(githubRepoList);
                }
            }



        }

        catch (IOException ex) {
            logger.info(ex.getStackTrace().toString());
        }

    }


}

