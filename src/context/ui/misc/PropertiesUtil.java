/*
 
 * Copyright (c) 2015 University of Illinois Board of Trustees, All rights reserved.   
 * Developed at GSLIS/ the iSchool, by Dr. Jana Diesner, Amirhossein Aleyasen,    
 * Chieh-Li Chin, Shubhanshu Mishra, Kiumars Soltani, and Liang Tao.     
 *   
 * This program is free software; you can redistribute it and/or modify it under   
 * the terms of the GNU General Public License as published by the Free Software   
 * Foundation; either version 2 of the License, or any later version.   
 *    
 * This program is distributed in the hope that it will be useful, but WITHOUT   
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or    
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for   
 * more details.   
 *    
 * You should have received a copy of the GNU General Public License along with   
 * this program; if not, see <http://www.gnu.org/licenses>.   
 *
 
 
 */
package context.ui.misc;

import context.ui.control.removestopword.RemoveStopwordsConfigurationController;

/**
 *
 * @author Amirhossein Aleyasen <aleyase2@illinois.edu>
 */
public class PropertiesUtil {

    /**
     *
     * @param aClass
     * @return
     */
    public static String getFXMLPath(Class aClass) {
        String name = "/" + aClass.getName().replaceAll("\\.", "/");
        name = name.substring(0, name.length() - 10) + "View.fxml";
        return name;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        String path = "/context/ui/control/removestopword/RemoveStopwordsConfigurationView.fxml";
        System.out.println(getFXMLPath(RemoveStopwordsConfigurationController.class));
    }
}
