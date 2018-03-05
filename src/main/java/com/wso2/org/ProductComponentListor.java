package com.wso2.org;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;


public class ProductComponentListor
{
    static Properties prop=new Properties();
    public static void main(String[] args) throws IOException, SQLException {
        prop.load(ProductComponentListor .class.getClassLoader().getResourceAsStream("config.properties"));


        GitHubRepoListor ListRepos = new GitHubRepoListor();

        ListRepos.getGitHubRepos(prop.getProperty("organization1"));
        ListRepos.getGitHubRepos(prop.getProperty("organization2"));
        ListRepos.getGitHubRepos(prop.getProperty("organization3"));


    }
}
