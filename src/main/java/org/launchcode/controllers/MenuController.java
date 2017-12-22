package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.text.MessageFormat;

@Controller
@RequestMapping("menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value = "")
    public String index(Model model) {
        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Menus");
        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddMenuForm(Model model) {
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddMenuForm(@ModelAttribute @Valid Menu newMenu, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }
        menuDao.save(newMenu);
        return "redirect:/menu/view/" + newMenu.getId();
    }

    // TODO note: use this to fix part 2 bonus mission (find in CheeseController)
    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)
    public String viewMenu(@PathVariable("menuId") int menuId, Model model) {
        Menu menu = menuDao.findOne(menuId);
        model.addAttribute("menu", menu);
        return "menu/view";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(@PathVariable("menuId") int menuId, Model model) {
        Menu menu = menuDao.findOne(menuId);
        AddMenuItemForm addMenuItemForm = new AddMenuItemForm(menu, cheeseDao.findAll());
        model.addAttribute("form", addMenuItemForm);
        model.addAttribute("title", "Add item to menu: " + menu.getName());
        return "menu/add-item";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.POST)
    public String addItem(@ModelAttribute @Valid AddMenuItemForm addMenuItemForm,
                          Errors errors,
                          Model model) {
        if (errors.hasErrors()) {
            Menu menu = menuDao.findOne(addMenuItemForm.getMenuId());
            model.addAttribute("title", "Add item to menu: " + menu.getName());
            return "menu/add-item";
        }

        Cheese cheese = cheeseDao.findOne(addMenuItemForm.getCheeseId());
        Menu menu = menuDao.findOne(addMenuItemForm.getMenuId());
        menu.addCheese(cheese);
        menuDao.save(menu);

        return "redirect:/menu/view/" + menu.getId();
    }
}
