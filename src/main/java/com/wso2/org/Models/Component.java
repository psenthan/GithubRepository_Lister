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
package com.wso2.org.Models;

/**
 * TODO:Class level comment
 */
public class Component {

    String componentName;
    String componentURL;
    int productArea;
    int organizationId;

    public int getOrganizationId() {

        return organizationId;
    }

    public void setOrganizationId(int organizationId) {

        this.organizationId = organizationId;
    }

    public Component(String componentName, String componentURL,int organizationId) {

        this.componentName = componentName;
        this.componentURL = componentURL;
        this.organizationId = organizationId;
        productArea =13;


    }

    public String getComponentName() {

        return componentName;
    }

    public String getComponentURL() {

        return componentURL;
    }
    public void setProductArea(int productArea){
        this.productArea = productArea;
    }

    public int getProductArea() {

        return productArea;
    }
}
