/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hms;

import hms.model.User;

/**
 *
 * @author jgreene
 */
public interface SubMenu {
    
    public void setSubMenuParent(MainMenuController main);
    public void setUser(User e);
  
}
