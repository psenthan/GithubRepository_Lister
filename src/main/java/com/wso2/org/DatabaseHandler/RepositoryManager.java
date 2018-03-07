/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package com.wso2.org.DatabaseHandler;

import com.wso2.org.Models.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * TODO:Class level comment
 */
public class RepositoryManager {
    public Component getComponentArea(Component component){

        PreparedStatement preparedStatement = null;
        Connection connection = null;
        ResultSet resultSet = null;

        String selectSQL = "SELECT * FROM pqd_component WHERE  pqd_component_name = ? ";
        try {

            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3308/product_quality_dashboard",
                    "root", "");

            preparedStatement = connection.prepareStatement(selectSQL);
            preparedStatement.setString(1,component.getComponentName());
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int areaId =resultSet.getInt("pqd_area_id");
                component.setProductArea(areaId);
            }




        } catch (SQLException e) {

        }catch (NullPointerException e){
            System.out.println(component.getComponentName());
        }finally {
            try {
                if(resultSet!=null){
                    resultSet.close();
                }
                if(connection !=null){
                    connection.close();
                }
                if(preparedStatement !=null){
                    preparedStatement.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return component;
    }
    public void insertComponent(Component component){

        PreparedStatement preparedStatement = null;
        Connection connection = null;
        ResultSet resultSet = null;

        String insertSQL = "INSERT INTO PRODUCT_REPOS(REPO_NAME,REPO_URL,PRODUCT_ID,ORG_ID)VALUES(?,?,?,?) ";
        try {

            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3308/WSO2_PRODUCT_COMPONENTS",
                    "root", "");

            preparedStatement = connection.prepareStatement(insertSQL);
            preparedStatement.setString(1,component.getComponentName());
            preparedStatement.setString(2,component.getComponentURL());
            preparedStatement.setInt(3,component.getProductArea());
            preparedStatement.setInt(4,component.getOrganizationId());
            preparedStatement.execute();




        } catch (SQLException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            System.out.println(component.getComponentName());
        }finally {
            try {
                if(resultSet!=null){
                    resultSet.close();
                }
                if(connection !=null){
                    connection.close();
                }
                if(preparedStatement !=null){
                    preparedStatement.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }



    }
}
