/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.my51c.see51.app.domian;

/**
 * The data model for every cell in the ListView for this application. This model stores
 * a title, an image resource and a default cell height for every item in the ListView.
 */
public class ListItemObject {

    public String code;  //策略的个数
    public String name;  //策略名字
    public String policysel; //0 默认（允许） 1 目标不可达 2 丢
    public String projectName;//规划的名字
    public String projectSrcip;//源ip地址
    public String projectSrcprt;//
    public String projectDesip;
    public String projectDesport;
    public String projectPro;
    public String projectPolicy;
    private String mTitle;
    private int mImgResource;
    private int mHeight;

    public ListItemObject() {
        super();
    }

    public ListItemObject(String title, int imgResource, int height) {
        super();
        mTitle = title;
        mImgResource = imgResource;
        mHeight = height;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectSrcip() {
        return projectSrcip;
    }

    public void setProjectSrcip(String projectSrcip) {
        this.projectSrcip = projectSrcip;
    }

    public String getProjectSrcprt() {
        return projectSrcprt;
    }

    public void setProjectSrcprt(String projectSrcprt) {
        this.projectSrcprt = projectSrcprt;
    }

    public String getProjectDesip() {
        return projectDesip;
    }

    public void setProjectDesip(String projectDesip) {
        this.projectDesip = projectDesip;
    }

    public String getProjectDesport() {
        return projectDesport;
    }

    public void setProjectDesport(String projectDesport) {
        this.projectDesport = projectDesport;
    }

    public String getProjectPro() {
        return projectPro;
    }

    public void setProjectPro(String projectPro) {
        this.projectPro = projectPro;
    }

    public String getProjectPolicy() {
        return projectPolicy;
    }

    public void setProjectPolicy(String projectPolicy) {
        this.projectPolicy = projectPolicy;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getImgResource() {
        return mImgResource;
    }

    public int getHeight() {
        return mHeight;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPolicysel() {
        return policysel;
    }

    public void setPolicysel(String policysel) {
        this.policysel = policysel;
    }


}
